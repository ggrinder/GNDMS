package de.zib.gndms.model.gorfx.types;

import java.util.HashMap;

/**
 * @author Maik Jorra <jorra@zib.de>
 * @verson \$id$
 * <p/>
 * User: bzcjorra Date: Sep 15, 2008 4:08:44 PM
 */
public class DataConstraints {

    private SpaceConstraint spaceConstraint; // required
    private TimeConstraint timeConstraint; // not required
    private String[] CFList; // required
    private HashMap<String,String> constraintList; // required 

    
    public SpaceConstraint getSpaceConstraint() {
        return spaceConstraint;
    }


    public void setSpaceConstraint( SpaceConstraint spaceConstraint ) {
        this.spaceConstraint = spaceConstraint;
    }


    public boolean hasTimeConstraint() {
        return timeConstraint != null;
    }


    public TimeConstraint getTimeConstraint() {
        return timeConstraint;
    }


    public void setTimeConstraint( TimeConstraint timeConstraint ) {
        this.timeConstraint = timeConstraint;
    }



    public String[] getCFList() {
        return CFList;
    }


    public void setCFList( String[] CFList ) {
        this.CFList = CFList;
    }


    public HashMap<String, String> getConstraintList() {
        return constraintList;
    }


    public void setConstraintList( HashMap<String, String> constraintList ) {
        this.constraintList = constraintList;
    }
}