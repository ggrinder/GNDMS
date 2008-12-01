package de.zib.gndms.model.common.types;

import de.zib.gndms.model.common.PersistentContract;
import org.joda.time.DateTime;

import java.util.Collections;
import java.util.Map;


/**
 * ThingAMagic.
 *
 * @author Stefan Plantikow<plantikow@zib.de>
 * @version $Id$
 *
 *          User: stepn Date: 24.11.2008 Time: 15:29:51
 */
public final class TransientContract implements Cloneable {
	private DateTime accepted;
	private FutureTime deadline;
	private FutureTime resultValidity;

    // expected size of task in case of a transfer or staging
    private Long expectedSize;

    // map for additional notes
    private Map<String,String> additionalNotes;


	public PersistentContract acceptAt(DateTime dt) {
		setAccepted(dt);
		return acceptAsIs();
	}

	public PersistentContract acceptNow() {
		return acceptAt(new DateTime());
	}


    /**
     * Creates a persistent-contract form this contract by fixing future time using the accepted time stamp.
     *
     * @note The created contract may be invalid wrt its jpa constraints.
     *
     * @return A persistent-contract representing this contract.
     *
     * todo maybe set fixed values here
     */
    @SuppressWarnings({ "FeatureEnvy" })
    public PersistentContract acceptAsIs() {

        PersistentContract pc = new PersistentContract();
		pc.setAccepted(accepted.toGregorianCalendar());

        DateTime fixedDeadline = null;
        if( hasDeadline() ) {
            fixedDeadline = getDeadline().fixedWith(accepted).getFixedTime();
            pc.setDeadline(fixedDeadline.toGregorianCalendar());
        }

        if( hasResultValidity() ) {
            DateTime fixedResultValidity;
            if( fixedDeadline != null )
                fixedResultValidity =  getResultValidity().fixedWith(fixedDeadline).getFixedTime();
            else  {
                fixedResultValidity =  getResultValidity().fixedWith(accepted).getFixedTime();
            }
            pc.setResultValidity(fixedResultValidity.toGregorianCalendar());
        }

        if (hasExpectedSize())
			pc.setExpectedSize(getExpectedSize());
		return pc;
	}


    public DateTime getAccepted() {
		return accepted;
	}


	public void setAccepted(final DateTime acceptedParam) {
		accepted = acceptedParam;
	}


    private boolean hasDeadline() {
        return deadline != null;
    }


	public FutureTime getDeadline() {
		return deadline;
	}


	public void setDeadline(final FutureTime deadlineParam) {
		deadline = deadlineParam;
	}


    private boolean hasResultValidity() {
        return resultValidity != null;
    }


	public FutureTime getResultValidity() {
		return resultValidity;
	}


	public void setResultValidity(final FutureTime resultValidityParam) {
		resultValidity = resultValidityParam;
	}


	public boolean hasExpectedSize( ) {
	    return getExpectedSize() != null;
	}


	public Long getExpectedSize() {
		return expectedSize;
	}


	public void setExpectedSize(final Long expectedSizeParam) {
		expectedSize = expectedSizeParam;
	}


    public boolean hasAdditionalNotes( ) {
        return additionalNotes != null && additionalNotes.size() > 0;
    }


    @SuppressWarnings({ "ReturnOfCollectionOrArrayField" })
	public Map<String, String> getAdditionalNotes() {
		return additionalNotes;
	}


	public void setAdditionalNotes(final Map<String, String> additionalNotesParam) {
		additionalNotes = Collections.unmodifiableMap(additionalNotesParam);
	}


	@Override
	public TransientContract clone() throws CloneNotSupportedException {
		final TransientContract clone = (TransientContract) super.clone();
		clone.setAccepted(getAccepted());
		clone.setResultValidity(getResultValidity().clone());
		clone.setDeadline(getDeadline().clone());
		clone.setAdditionalNotes(getAdditionalNotes());
		if (hasExpectedSize())
			clone.setExpectedSize(getExpectedSize());
		return clone;    // Overridden method
	}
}
