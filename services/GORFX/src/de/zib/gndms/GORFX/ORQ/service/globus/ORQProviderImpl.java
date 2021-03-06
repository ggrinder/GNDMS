package de.zib.gndms.GORFX.ORQ.service.globus;

/*
 * Copyright 2008-2011 Zuse Institute Berlin (ZIB)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



import de.zib.gndms.GORFX.ORQ.service.ORQImpl;

import java.rmi.RemoteException;

/** 
 * DO NOT EDIT:  This class is autogenerated!
 *
 * This class implements each method in the portType of the service.  Each method call represented
 * in the port type will be then mapped into the unwrapped implementation which the user provides
 * in the GORFXImpl class.  This class handles the boxing and unboxing of each method call
 * so that it can be correclty mapped in the unboxed interface that the developer has designed and 
 * has implemented.  Authorization callbacks are automatically made for each method based
 * on each methods authorization requirements.
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
public class ORQProviderImpl{
	
	ORQImpl impl;
	
	public ORQProviderImpl() throws RemoteException {
		impl = new ORQImpl();
	}
	

    public de.zib.gndms.GORFX.ORQ.stubs.GetOfferAndDestroyRequestResponse getOfferAndDestroyRequest(de.zib.gndms.GORFX.ORQ.stubs.GetOfferAndDestroyRequestRequest params) throws RemoteException, de.zib.gndms.GORFX.ORQ.stubs.types.UnfullfillableRequest, de.zib.gndms.GORFX.ORQ.stubs.types.PermissionDenied {
    de.zib.gndms.GORFX.ORQ.stubs.GetOfferAndDestroyRequestResponse boxedResult = new de.zib.gndms.GORFX.ORQ.stubs.GetOfferAndDestroyRequestResponse();
    boxedResult.setEndpointReference(impl.getOfferAndDestroyRequest(params.getOfferExecutionContract().getOfferExecutionContract(),params.getContext().getContext()));
    return boxedResult;
  }

    public de.zib.gndms.GORFX.ORQ.stubs.PermitEstimateAndDestroyRequestResponse permitEstimateAndDestroyRequest(de.zib.gndms.GORFX.ORQ.stubs.PermitEstimateAndDestroyRequestRequest params) throws RemoteException, de.zib.gndms.GORFX.ORQ.stubs.types.UnfullfillableRequest, de.zib.gndms.GORFX.ORQ.stubs.types.PermissionDenied {
    de.zib.gndms.GORFX.ORQ.stubs.PermitEstimateAndDestroyRequestResponse boxedResult = new de.zib.gndms.GORFX.ORQ.stubs.PermitEstimateAndDestroyRequestResponse();
    boxedResult.setOfferExecutionContract(impl.permitEstimateAndDestroyRequest(params.getOfferExecutionContract().getOfferExecutionContract(),params.getContext().getContext()));
    return boxedResult;
  }

}
