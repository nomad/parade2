package org.makumba.aether;

import java.util.Date;

import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.Index;

/**
 * A aether event that can be processed by the percolation engine
 * 
 * @author Manuel Gay
 * 
 */
@MappedSuperclass
public class AetherEvent implements Cloneable {

    protected String objectURL;

    protected String objectType;

    protected String actor;

    protected String action;

    protected Date eventDate;

    protected Double initialLevelCoefficient;

    public void setObjectURL(String objectURL) {
        this.objectURL = objectURL;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    @Index(name = "IDX_OBJECTURL")
    public String getObjectURL() {
        return objectURL;
    }

    public String getObjectType() {
        return objectType;
    }

    @Index(name = "IDX_ACTOR")
    public String getActor() {
        return actor;
    }

    public String getAction() {
        return action;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public AetherEvent(String objectURL, String objectType, String user, String action, Date eventDate,
            Double initialLevelCoefficient) {
        super();
        this.objectURL = objectURL;
        this.objectType = objectType;
        this.actor = user;
        this.action = action;
        this.eventDate = eventDate;
        this.initialLevelCoefficient = initialLevelCoefficient;
    }

    // for MatchedAetherEvent
    protected AetherEvent(AetherEvent e) {
        this.objectURL = e.getObjectURL();
        this.objectType = e.getObjectType();
        this.actor = e.getActor();
        this.action = e.getAction();
        this.eventDate = e.getEventDate();
        this.initialLevelCoefficient = e.getInitialLevelCoefficient();
    }

    // for MatchedAetherEvent
    protected AetherEvent() {

    }

    @Override
    public String toString() {
        return this.actor + " --(" + this.action + ")--> " + this.objectURL + " (" + this.objectType + ")";
    }

    public Double getInitialLevelCoefficient() {
        return initialLevelCoefficient;
    }

    public void setInitialLevelCoefficient(Double initialLevelCoefficient) {
        this.initialLevelCoefficient = initialLevelCoefficient;
    }

}
