package de.zib.gndms.model.gorfx.types;

/**
 * An base class for model classes representing ORQ's.
 *
 * @author Maik Jorra <jorra@zib.de>
 * @verson \$id$
 * <p/>
 * User: bzcjorra Date: Sep 5, 2008 3:38:17 PM
 */
public class AbstractORQ {

    private String offerType;


    public String getOfferType() {
        return offerType;
    }


    protected void setOfferType( String URI ) {
        this.offerType = URI;
    }
}
