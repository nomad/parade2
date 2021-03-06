package org.makumba.aether.percolation;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.logging.Logger;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.makumba.aether.Aether;
import org.makumba.aether.AetherEvent;
import org.makumba.aether.PercolationException;
import org.makumba.aether.UserTypes;
import org.makumba.aether.model.InitialPercolationRule;
import org.makumba.aether.model.MatchedAetherEvent;
import org.makumba.aether.model.PercolationRule;
import org.makumba.aether.model.PercolationStep;
import org.makumba.parade.aether.ActionTypes;
import org.makumba.parade.aether.ObjectTypes;
import org.makumba.parade.init.InitServlet;

/**
 * A rule-based percolator.<br>
 * <br>
 * WARNING: this class is thread-safe, changing the order of methods may result in deadlocks.<br>
 * <br>
 * This percolator uses 5 tables in order to perform percolation and provide ALE:
 * <ul>
 * <li>initial percolation rule table: set of rules to match an event and attribute an initial strength to it</li>
 * <li>percolation rule table: set of rules for that processes relations a {@link MatchedAetherEvent} can propagate
 * through, i.e. calculates consumption</li>
 * <li>percolation step table: stores each step of one event percolation</li>
 * <li>relation table: table holding relations between objects</li>
 * <li>ALE table: table holding focus and nimbus values per object and user.</li>
 * </ul>
 * 
 * The percolator also runs two timers that update focus/nimbus values and perform garbage collection of
 * {@link PercolationStep}-s that are below MIN_ENERGY_LEVEL. The task execution interval is set by
 * GARBAGE_COLLECTION_INTERVAL and CURVE_UPDATE_INTERVAL.
 * 
 * @author Manuel Gay
 * 
 */
public class RuleBasedPercolator implements Percolator {

    static final int MIN_ENERGY_LEVEL = 4;

    static final int GARBAGE_COLLECTION_INTERVAL = 1000 * 60; // 10 mins

    static final int CURVE_UPDATE_INTERVAL = 1000 * 600; // 10 mins

    static final int MAX_PERCOLATION_TIME = 5000000;

    private final Logger logger = Aether.getAetherLogger(RuleBasedPercolator.class.getName());

    private SessionFactory sessionFactory;

    private PercolationStrategy strategy;

    private Timer garbageTimer;

    private Timer curveTimer;

    public static boolean rulesChanged = false;

    public static Object mutex = new Object();
    {
        Thread t = new Thread(new Runnable() {
            public void run() {
                synchronized (mutex) {
                    while (true) {
                        try {
                            mutex.wait();
                        } catch (InterruptedException e) {
                            return;
                        }
                        consumePercolationData();
                        mutex.notifyAll();
                    }
                }
            }

        });
        t.setPriority(Thread.MIN_PRIORITY);
        t.start();
    }

    class PercolationData {

        private AetherEvent aetherEvent = null;

        public PercolationData(AetherEvent aetherEvent, boolean virtualPercolation) {
            this.aetherEvent = aetherEvent;
            this.virtualPercolation = virtualPercolation;
        }

        private boolean virtualPercolation;

        public void setVirtualPercolation(boolean virtualPercolation) {
            this.virtualPercolation = virtualPercolation;
        }

        public boolean isVirtualPercolation() {
            return virtualPercolation;
        }

        public void setAetherEvent(AetherEvent aetherEvent) {
            this.aetherEvent = aetherEvent;
        }

        public AetherEvent getAetherEvent() {
            return aetherEvent;
        }
    }

    protected void consumePercolationData() {
        for (PercolationData d : new java.util.ArrayList<PercolationData>(percolationQueue)) {
            try {
                strategy.percolate(d.getAetherEvent(), d.isVirtualPercolation(), sessionFactory);
            } catch (PercolationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            percolationQueue.remove(d);
        }
    }

    private final Vector<PercolationData> percolationQueue = new Vector<PercolationData>();

    private void addToPercolationQueue(AetherEvent e, boolean virtualPercolation) throws PercolationException {

        synchronized (mutex) {

            percolationQueue.add(new PercolationData(e, virtualPercolation));
            consumePercolationData();
            mutex.notifyAll();

        }
    }

    public void percolate(AetherEvent e, boolean virtualPercolation) throws PercolationException {
        addToPercolationQueue(e, virtualPercolation);
    }

    private void checkInitialPercolationRules() {
        logger.info("Checking initial percolation rules...");

        Session s = null;
        Transaction tx = null;
        try {
            s = sessionFactory.openSession();
            tx = s.beginTransaction();

            @SuppressWarnings("unchecked")
            List<InitialPercolationRule> rules = s.createQuery("from InitialPercolationRule r").list();
            logger.fine("Found " + rules.size() + " initial percolation rules");
            for (InitialPercolationRule r : rules) {
                logger.fine(r.toString());

                if (!ActionTypes.getActions().contains(r.getAction())) {
                    logger.warning("Initial percolation rule \"" + r.toString() + "\" has invalid action "
                            + r.getAction() + ". It will be ignored.");
                    r.setActive(false);
                }

                if (!UserTypes.getUserTypes().contains(r.getUserType())) {
                    logger.warning("Initial percolation rule \"" + r.toString() + "\" has invalid user type "
                            + r.getUserType() + ". It will be ignored.");
                    r.setActive(false);
                }

                if (!ObjectTypes.getObjectTypes().contains(r.getObjectType())) {
                    logger.warning("Initial percolation rule \"" + r.toString() + "\" has invalid object type "
                            + r.getObjectType() + ". It will be ignored.");
                    r.setActive(false);
                }

            }

            tx.commit();

        } finally {
            if (s != null) {
                s.close();
            }
        }

    }

    private void checkPercolationRules() {
        logger.info("Checking percolation rules...");

        Session s = null;
        Transaction tx = null;
        try {
            s = sessionFactory.openSession();
            tx = s.beginTransaction();

            @SuppressWarnings("unchecked")
            List<PercolationRule> rules = s.createQuery("from PercolationRule r").list();
            logger.fine("Found " + rules.size() + " percolation rules");
            for (PercolationRule r : rules) {
                logger.fine(r.toString());

                if (!ObjectTypes.getObjectTypes().contains(r.getObject())) {
                    logger.warning("Initial percolation rule \"" + r.toString() + "\" has invalid object type "
                            + r.getObject() + ". It will be ignored.");
                    r.setActive(false);
                }

                if (!ObjectTypes.getObjectTypes().contains(r.getSubject())) {
                    logger.warning("Initial percolation rule \"" + r.toString() + "\" has invalid subject type "
                            + r.getObject() + ". It will be ignored.");
                    r.setActive(false);
                }

            }

            tx.commit();

        } finally {
            if (s != null) {
                s.close();
            }
        }

    }

    public void configure(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.strategy = new GroupedPercolationStrategy(sessionFactory); // new SimplePercolationStrategy();

        logger.info("Starting initialisation of Rule-based percolator");

        checkInitialPercolationRules();
        checkPercolationRules();

        configureGarbageCollectionTimer();
        configureProgressionCurveTimer();

        logger.info("Finished initialisation of Rule-based percolator");
    }

    /**
     * Configures the timer that runs garbage collection
     */
    private void configureGarbageCollectionTimer() {
        garbageTimer = new Timer(true);
        Date runAt = new Date();

        garbageTimer.scheduleAtFixedRate(new GarbageTask(), runAt, GARBAGE_COLLECTION_INTERVAL);
    }

    /**
     * Configures the timer that runs the F/N update according to the progression curves
     */
    private void configureProgressionCurveTimer() {
        curveTimer = new Timer(true);
        Date runAt = new Date();

        curveTimer.scheduleAtFixedRate(new CurveTask(), runAt, CURVE_UPDATE_INTERVAL);
    }

    /**
     * Computes the ALE (focus + nimbus) of one object for one event
     * 
     * @param objectURL
     *            the object for which the ALE should be computed
     * @param user
     *            the user for which the ALE should be computed
     * @return the sum of focus and nimbus, if there is focus/nimbus match
     */
    public int getALE(String objectURL, String user) {
        return -1;
    }

    /**
     * Deletes all the {@link PercolationStep}-s of which the energy is too low.
     * 
     * @param s
     *            a Hibernate {@link Session}
     */
    private void collectGarbage(Session s) {

        Query q0 = s.createQuery("delete from PercolationStep ps where ps.virtualPercolation = true");

        Query q1 = s
                .createQuery("delete from PercolationStep ps where (ps.nimbus < 20 and ps.focus = 0) and ps.matchedAetherEvent.id in (select mae.id from MatchedAetherEvent mae join mae.initialPercolationRule ipr where (ipr.percolationMode = 20 or ipr.percolationMode = 30))");

        Query q2 = s
                .createQuery("delete from PercolationStep ps where (ps.focus < 20 and ps.nimbus = 0) and ps.matchedAetherEvent.id in (select mae.id from MatchedAetherEvent mae join mae.initialPercolationRule ipr where (ipr.percolationMode = 10 or ipr.percolationMode = 30))");

        Query q3 = s
                .createQuery("delete from MatchedAetherEvent mae where not exists (from PercolationStep ps where mae.id = ps.matchedAetherEvent.id)");

        Query q4 = s.createQuery("delete from ALE ale where focus < 20 and nimbus < 20");

        Transaction transaction = s.beginTransaction();
        int d0 = q0.executeUpdate();
        int d1 = q1.executeUpdate();
        int d2 = q2.executeUpdate();
        int d3 = q3.executeUpdate();
        int d4 = q4.executeUpdate();
        transaction.commit();

        logger.info("Garbage-collected " + (d0 + d1 + d2) + " percolation steps");
        logger.info("Garbage-collected " + d3 + " MatchedAetherEvents");
        logger.info("Garbage-collected " + d4 + " ALE values");

    }

    /**
     * Recomputes the focus and nimbus values
     * 
     * @param s
     *            a Hibernate session
     */
    private void updateALEValues(Session s) {

        String q = "update ALE a set a.focus = (select sum(ps.focus) from PercolationStep ps where ps.objectURL = a.objectURL and ps.matchedAetherEvent.actor = a.user), "
                + "a.nimbus = (select sum(ps.nimbus) from PercolationStep ps where ps.objectURL = a.objectURL and ps.userGroup like '%*%' and ps.userGroup not like concat(concat(concat('%','-'), a.user), '%'))";
        Transaction tx1 = s.beginTransaction();
        int updated = s.createQuery(q).executeUpdate();

        // if no steps are found in the previous inner select, the sum is null so we have to fix this here
        s.createQuery("update ALE set focus = 0 where focus is null").executeUpdate();
        s.createQuery("update ALE set nimbus = 0 where nimbus is null").executeUpdate();

        tx1.commit();

        logger.info("Updated " + updated + " ALE values");

    }

    /**
     * Updates all the focus and nimbus values of {@link PercolationStep}-s according the the progression curves
     * 
     * @param s
     *            a Hibernate {@link Session}
     */
    public void executeEnergyProgressionUpdate(Session s) {
        @SuppressWarnings("unchecked")
        List<InitialPercolationRule> iprs = s.createQuery("from InitialPercolationRule ipr").list();
        for (InitialPercolationRule initialPercolationRule : iprs) {
            executeEnergyProgressionForProgressionCurve(initialPercolationRule, s);
        }

    }

    /**
     * Updates all the focus and nimbus values of {@link PercolationStep}-s for one {@link InitialPercolationRule}
     * 
     * @param ipr
     *            the {@link InitialPercolationRule} containing the progression curves
     * @param s
     *            a Hibernate {@link Session}
     */
    private void executeEnergyProgressionForProgressionCurve(InitialPercolationRule ipr, Session s) {

        int updatedFocusPercolationSteps = 0;
        int updatedNimbusPercolationSteps = 0;

        boolean focusCurve = ipr.getFocusProgressionCurve() != null
                && ipr.getFocusProgressionCurve().trim().length() != 0;
        boolean nimbusCurve = ipr.getNimbusProgressionCurve() != null
                && ipr.getNimbusProgressionCurve().trim().length() != 0;

        Query focusUpdate = null;
        Query nimbusUpdate = null;

        if (focusCurve) {
            String focusQuery = buildEnergyUpdateStatement(ipr.getFocusProgressionCurve(), true);
            focusUpdate = s.createQuery(focusQuery).setParameter(0, ipr.getId());
        }

        if (nimbusCurve) {
            String nimbusQuery = buildEnergyUpdateStatement(ipr.getNimbusProgressionCurve(), false);
            nimbusUpdate = s.createQuery(nimbusQuery).setParameter(0, ipr.getId());
        }

        Transaction tx = s.beginTransaction();
        if (focusUpdate != null) {
            logger.fine("Now running " + focusUpdate.getQueryString());
            updatedFocusPercolationSteps = focusUpdate.executeUpdate();
        }

        if (nimbusUpdate != null) {
            logger.fine("Now running " + nimbusUpdate.getQueryString());
            updatedNimbusPercolationSteps = nimbusUpdate.executeUpdate();
        }

        tx.commit();

        logger.info("Updated " + updatedFocusPercolationSteps + " percolation steps for for focus and "
                + updatedNimbusPercolationSteps + " for nimbus");

    }

    /**
     * Builds a UPDATE statement that updates all the nimbus or focus values of all PercolationSteps matched by a
     * {@link InitialPercolationRule}.
     * 
     * @param progressionCurve
     *            the string representing the progression curve. Supports only basic SQL operations
     * @param isFocusCurve
     *            whether this is a focus or a nimbus progression curve
     * 
     * @return a query string of a UPDATE query that expects as argument the reference to a InitialPercolationRule id
     *         (not named parameter)
     */
    private String buildEnergyUpdateStatement(String progressionCurve, boolean isFocusCurve) {
        String query = "UPDATE PercolationStep SET " + (isFocusCurve ? "focus" : "nimbus") + " = ";

        // progression curve is something like "1 - t" or "1 - t*t + t - 1/t"
        query += (isFocusCurve ? "initialFocus" : "initialNimbus") + " * ("
                + progressionCurve.replaceAll("t", "TIMESTAMPDIFF(HOUR, created, now())") + ") ";
        query += "WHERE matchedAetherEvent.id IN (SELECT mae.id FROM MatchedAetherEvent mae JOIN mae.initialPercolationRule ipr WHERE ipr.id = ?)";

        return query;
    }

    private class GarbageTask extends TimerTask {

        @Override
        public void run() {

            synchronized (mutex) {

                runGC();
                mutex.notifyAll();
            }
        }
    }

    private void runGC() {
        Session s = null;
        try {
            s = InitServlet.getSessionFactory().openSession();
            collectGarbage(s);
            updateALEValues(s);
        } finally {
            if (s != null) {
                s.close();
            }
        }

    }

    private class CurveTask extends TimerTask {

        @Override
        public void run() {

            synchronized (mutex) {

                Session s = null;
                try {
                    s = InitServlet.getSessionFactory().openSession();
                    executeEnergyProgressionUpdate(s);
                    collectGarbage(s);
                    updateALEValues(s);
                } finally {
                    if (s != null) {
                        s.close();
                    }
                }
                mutex.notifyAll();

            }
        }
    }

    public void cleanVirtualPercolations() {
        Session s = null;
        try {
            s = sessionFactory.openSession();
            Transaction tx = s.beginTransaction();
            s
                    .createQuery(
                            "delete from PercolationStep ps where ps.matchedAetherEvent.id in (select mae.id from MatchedAetherEvent mae where ps.matchedAetherEvent.id = mae.id and mae.virtualPercolation = true)")
                    .executeUpdate();
            s.createQuery("delete from MatchedAetherEvent mae where mae.virtualPercolation = true").executeUpdate();
            s.createQuery("update ALE set virtualFocus = 0, virtualNimbus = 0").executeUpdate();
            tx.commit();

        } finally {
            if (s != null)
                s.close();
        }

    }

}
