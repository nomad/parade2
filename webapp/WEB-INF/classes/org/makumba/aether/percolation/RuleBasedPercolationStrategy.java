package org.makumba.aether.percolation;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.makumba.aether.AetherEvent;
import org.makumba.aether.PercolationException;
import org.makumba.aether.UserTypes;
import org.makumba.aether.model.Focus;
import org.makumba.aether.model.InitialPercolationRule;
import org.makumba.aether.model.MatchedAetherEvent;
import org.makumba.aether.model.PercolationRule;
import org.makumba.aether.model.PercolationStep;
import org.makumba.parade.aether.ActionTypes;
import org.makumba.parade.aether.ObjectTypes;

/**
 * Percolation strategy for a rule-based percolator
 * @author Manuel Gay
 *
 */
public class RuleBasedPercolationStrategy implements PercolationStrategy {

    public void percolate(AetherEvent e, SessionFactory sessionFactory) throws PercolationException {
        if(RuleBasedPercolator.rulesChanged) {
            configure();
        }
        RuleBasedPercolator.rulesChanged = false;

    }

    /**
     * Method to configure the percolation strategy, to be overwritten by subclasses if needed
     */
    protected void configure() {
        
    }

    /**
     * Builds a {@link MatchedAetherEvent} from a {@link AetherEvent} and a {@link InitialPercolationRule} it was
     * matched against.
     * 
     * @param e
     *            the {@link AetherEvent} to match
     * @param ipr
     *            the {@link InitialPercolationRule} to match it against
     * @param s
     *            a hibernate {@link Session} useful to do queries
     * 
     * @return a {@link MatchedAetherEvent} if anything was matched, null otherwise
     */
    protected MatchedAetherEvent buildMatchedAetherEvent(AetherEvent e, InitialPercolationRule ipr, Session s) {
    
        String userGroup = "";
    
        if (ipr.getUserType().equals(UserTypes.ALL.type())) {
            userGroup = "*";
        }
        if (ipr.getUserType().equals(UserTypes.ALL_BUT_ACTOR.type())) {
            userGroup = "*,-" + e.getActor();
        }
        if (ipr.getUserType().equals(UserTypes.NONE.type())) {
            userGroup = "";
        }
        if (ipr.getUserType().equals(UserTypes.ALL_BUT_OWNER.type())) {
    
            if (ActionTypes.isFileAction(e.getAction())) {
                List<String> own = s
                        .createQuery(
                                "SELECT r.fromURL FROM org.makumba.devel.relations.Relation r WHERE r.toURL = :toURL AND r.type = 'owns'")
                        .setParameter("toURL", ObjectTypes.fileFromRow(e.getObjectURL())).list();
    
                userGroup = "*";
                for (String owner : own) {
                    userGroup += ",-" + owner;
                }
            }
    
        }
        if (ipr.getUserType().equals(UserTypes.OWNER)) {
    
            if (ActionTypes.isFileAction(e.getAction())) {
                List<String> own = s
                        .createQuery(
                                "SELECT r.fromURL FROM org.makumba.devel.relations.Relation r WHERE r.toURL = :toURL AND r.type = 'owns'")
                        .setParameter("toURL", ObjectTypes.fileFromRow(e.getObjectURL())).list();
    
                userGroup = "";
                for (Iterator<String> iterator = own.iterator(); iterator.hasNext();) {
                    userGroup += iterator.next();
                    if (iterator.hasNext()) {
                        userGroup += ",";
                    }
                }
            }
        }
        if (ipr.getUserType().equals(UserTypes.ACTOR)) {
            userGroup = e.getActor();
        }
    
        if (userGroup.length() > 0) {
            return new MatchedAetherEvent(e, userGroup, ipr);
        }
    
        return null;
    }

    /**
     * Builds a percolation step from a {@link MatchedAetherEvent} and the {@link PercolationRule} that matched it
     * 
     * @param mae
     *            the originating {@link MatchedAetherEvent}
     * @param pr
     *            the {@link PercolationRule}
     * @param isFocusPercolation
     *            <code>true</code> if this is focus percolation, <code>false</code> if it's nimbus percolation
     * @param percolationLevel
     *            TODO
     * @param percolationPath
     *            TODO
     * @return a {@link PercolationStep} corresponding to this part of the percolation
     */
    protected PercolationStep buildPercolationStep(MatchedAetherEvent mae, String previousURL, String objectURL, PercolationRule pr, int level,
            boolean isFocusPercolation, PercolationStep previous, int percolationLevel) {
            
                return new PercolationStep(previousURL, objectURL, isFocusPercolation ? mae.getActor() : mae.getUserGroup(), (isFocusPercolation ? level : 0),
                        (!isFocusPercolation ? level : 0), pr, mae, previous, percolationLevel);
            
            }

    /**
     * Adds or updates the total focus of an object for a user
     * 
     * @param objectURL the URL to the object
     * @param user the user 
     * @param energy the energy to be added
     * @param s a Hibernate session
     */
    protected void addOrUpdateFocus(String objectURL, String user, int energy, Session s) {
        int updated = s.createQuery("update Focus set focus = focus + :energy where user = :user and objectURL = :objectURL").setString("user", user).setString("objectURL", objectURL).setParameter("energy", energy).executeUpdate();
        if(updated == 0) {
            Focus f = new Focus(objectURL, user, energy);
            s.save(f);
        }
    }

}