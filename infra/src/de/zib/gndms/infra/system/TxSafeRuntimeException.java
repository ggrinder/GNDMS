package de.zib.gndms.infra.system;

import org.jetbrains.annotations.NotNull;


/**
 * Wrapper for RuntimeExceptions that should not cause a transaction rollback when caught.
 *
 * @see EMTools
 * 
 * @author Stefan Plantikow <plantikow@zib.de>
 * @version $Id$
 *
 *          User: stepn Date: 08.08.2008 Time: 12:08:41
 */
public class TxSafeRuntimeException extends RuntimeException {
	private static final long serialVersionUID = 5446757105425846044L;


	public TxSafeRuntimeException(final @NotNull Throwable cause) {
		super(cause);
	}
}