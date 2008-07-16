package org.makumba.aether;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import org.apache.log4j.Logger;
import org.makumba.aether.percolation.Percolator;
import org.makumba.aether.percolation.RuleBasedPercolator;

/**
 * The access point to the Aether engine
 * 
 * @author Manuel Gay
 * 
 */
public class Aether {

    private Logger logger = Logger.getLogger(Aether.class);

    private static Aether instance;

    public static Aether getAether(AetherContext ctx) {
        if (instance == null) {
            instance = new Aether(ctx);
        }
        return instance;
    }

    private AetherContext ctx;

    private Percolator p;

    private Aether(AetherContext ctx) {
        this.ctx = ctx;
        startup();
    }

    /**
     * Starts the Aether engine up:
     * <ul>
     * <li>computes all relations using the relation computers</li>
     * <li>initialises the percolator</li>
     * </ul>
     */
    private void startup() {
        logger.info("AETHER-INIT: Starting Aether engine at " + new java.util.Date());
        long start = System.currentTimeMillis();

        p = new RuleBasedPercolator();
        p.configure(ctx.getSessionFactory());
        computeAllRelations();

        logger.info("AETHER-INIT: Launching Aether finished at " + new java.util.Date());
        long end = System.currentTimeMillis();
        long refresh = end - start;
        logger.info("AETHER-INIT: Initialisation took " + refresh + " ms");

    }

    /**
     * Computes all the relations using the relation computers
     */
    private void computeAllRelations() {
        List<RelationComputer> computers = ctx.getRelationComputers();
        for (RelationComputer relationComputer : computers) {
            logger.debug("Starting computation of all relations for "+relationComputer.getName());
            try {
                relationComputer.computeRelations();
            } catch (RelationComputationException e) {
                logger.error("Could not compute relations of relation computer " + relationComputer.getName() + ": "
                        + e.getMessage());
            }
            logger.debug("Finished computation of all relations for "+relationComputer.getName());
        }
    }

    public void registerEvent(AetherEvent e, boolean virtualPercolation) {
        
        long start = System.currentTimeMillis();

        try {
            p.percolate(e, virtualPercolation);
        } catch (PercolationException e1) {
            logger.warn("Exception while percolating event \""+e.toString()+"\": "+e1.getMessage());
            StringWriter sw = new StringWriter();
            PrintWriter p = new PrintWriter(sw);
            e1.printStackTrace(p);
            sw.flush();
            logger.debug("\n***********************************************************************\n"
                    + sw.toString()
                    + "***********************************************************************");

        }

        long end = System.currentTimeMillis();
        long refresh = end - start;
        logger.info("AETHER: percolation of event " + e.toString() + " took " + refresh + " ms");
        
    }
    
    public void cleanVirtualPercolations() {
        p.cleanVirtualPercolations();
    }
    
    public int getALE(String objectURL, String user) {
        return p.getALE(objectURL, user);        
    }
}
