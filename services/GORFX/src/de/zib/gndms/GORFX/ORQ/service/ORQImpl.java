package de.zib.gndms.GORFX.ORQ.service;

import java.rmi.RemoteException;

/** 
 * TODO:I am the service side implementation class.  IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
public class ORQImpl extends ORQImplBase {

	
	public ORQImpl() throws RemoteException {
		super();
	}
	
  public org.apache.axis.message.addressing.EndpointReferenceType getOfferAndDestroyRequest(types.OfferExecutionContractT offerExecutionContract,types.ContextT context) throws RemoteException, de.zib.gndms.GORFX.ORQ.stubs.types.UnfullfillableRequest, de.zib.gndms.GORFX.ORQ.stubs.types.PermissionDenied {
    //TODO: Implement this autogenerated method
    throw new RemoteException("Not yet implemented");
  }

  public types.OfferExecutionContractT permitEstimateAndDestroyRequest(types.OfferExecutionContractT offerExecutionContract,types.ContextT context) throws RemoteException, de.zib.gndms.GORFX.ORQ.stubs.types.UnfullfillableRequest, de.zib.gndms.GORFX.ORQ.stubs.types.PermissionDenied {
    //TODO: Implement this autogenerated method
    throw new RemoteException("Not yet implemented");
  }

}

