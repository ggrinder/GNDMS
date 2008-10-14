package de.zib.gndms.model.gorfx.types;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.HashMap;


/**
 * An base class for model classes representing ORQ's.
 *
 * @author Maik Jorra <jorra@zib.de>
 * @verson \$id$
 * <p/>
 * User: bzcjorra Date: Sep 5, 2008 3:38:17 PM
 */
public abstract class AbstractORQ implements Serializable {

    private String offerType;
    private HashMap<String,String> context;
    private transient boolean justEstimate = false;

    private static final long serialVersionUID = 5782532835559987893L;

    protected AbstractORQ() {
    }


    protected AbstractORQ( String offerType ) {
        this.offerType = offerType;
    }


    public String getOfferType() {
        return offerType;
    }


    protected void setOfferType( String URI ) {
        this.offerType = URI;
    }


    public void setJustEstimate( boolean b ) {
        justEstimate = b;
    }


    public boolean isJustEstimate() {
        return justEstimate;
    }


    public abstract @NotNull String getDescription();


    public HashMap<String, String> getContext() {
        return context;
    }


    public void setContext( HashMap<String, String> context ) {
        this.context = context;
    }


    public boolean hasContext() {
        return context != null;
    }
}