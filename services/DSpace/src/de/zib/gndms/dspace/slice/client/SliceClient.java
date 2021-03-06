package de.zib.gndms.dspace.slice.client;

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



import de.zib.gndms.dspace.slice.common.SliceConstants;
import de.zib.gndms.dspace.slice.common.SliceI;
import de.zib.gndms.dspace.slice.stubs.types.SliceReference;
import de.zib.gndms.dspace.stubs.types.InternalFailure;
import de.zib.gndms.dspace.subspace.client.SubspaceClient;
import de.zib.gndms.dspace.subspace.stubs.types.OutOfSpace;
import de.zib.gndms.dspace.subspace.stubs.types.SubspaceReference;
import de.zib.gndms.dspace.subspace.stubs.types.UnknownOrInvalidSliceKind;
import org.apache.axis.client.Stub;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI;
import org.apache.axis.types.URI.MalformedURIException;
import org.apache.axis.types.UnsignedLong;
import org.globus.gsi.GlobusCredential;
import org.globus.wsrf.encoding.DeserializationException;
import org.globus.wsrf.encoding.ObjectDeserializer;
import org.joda.time.DateTime;
import org.oasis.wsrf.properties.GetResourcePropertyResponse;
import types.ContextT;
import types.SliceTransformSpecifierT;
import types.SliceTypeSpecifierT;

import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.Calendar;

/**
 * This class is autogenerated, DO NOT EDIT GENERATED GRID SERVICE ACCESS METHODS.
 *
 * This client is generated automatically by Introduce to provide a clean unwrapped API to the
 * service.
 *
 * On construction the class instance will contact the remote service and retrieve it's security
 * metadata description which it will use to configure the Stub specifically for each method call.
 * 
 * @created by Introduce Toolkit version 1.2
 */
public class SliceClient extends SliceClientBase implements SliceI {	

	public SliceClient(String url) throws MalformedURIException, RemoteException {
		this(url,null);	
	}

	public SliceClient(String url, GlobusCredential proxy) throws MalformedURIException, RemoteException {
	   	super(url,proxy);
	}
	
	public SliceClient(EndpointReferenceType epr) throws MalformedURIException, RemoteException {
	   	this(epr,null);
	}
	
	public SliceClient(EndpointReferenceType epr, GlobusCredential proxy) throws MalformedURIException, RemoteException {
	   	super(epr,proxy);
	}

	public static void usage(){
		System.out.println(SliceClient.class.getName() + " -url <service url>");
	}
	
	public static void main(String [] args){
	    System.out.println("Running the Grid Service Client");
		try{
		if(!(args.length < 2)){
			if(args[0].equals("-url")){
			  SliceClient client = new SliceClient(args[1]);
			  // place client calls here if you want to use this main as a
			  // test....
                // no thank you
            } else {
				usage();
				System.exit(1);
			}
		} else {
			usage();
			System.exit(1);
		}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

  public org.oasis.wsrf.lifetime.DestroyResponse destroy(org.oasis.wsrf.lifetime.Destroy params) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"destroy");
    return portType.destroy(params);
    }
  }

  public org.oasis.wsrf.lifetime.SetTerminationTimeResponse setTerminationTime(org.oasis.wsrf.lifetime.SetTerminationTime params) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"setTerminationTime");
    return portType.setTerminationTime(params);
    }
  }

  public org.oasis.wsn.SubscribeResponse subscribe(org.oasis.wsn.Subscribe params) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"subscribe");
    return portType.subscribe(params);
    }
  }

  public org.oasis.wsrf.properties.GetMultipleResourcePropertiesResponse getMultipleResourceProperties(org.oasis.wsrf.properties.GetMultipleResourceProperties_Element params) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"getMultipleResourceProperties");
    return portType.getMultipleResourceProperties(params);
    }
  }

  public org.oasis.wsrf.properties.GetResourcePropertyResponse getResourceProperty(javax.xml.namespace.QName params) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"getResourceProperty");
    return portType.getResourceProperty(params);
    }
  }

  public org.oasis.wsrf.properties.QueryResourcePropertiesResponse queryResourceProperties(org.oasis.wsrf.properties.QueryResourceProperties_Element params) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"queryResourceProperties");
    return portType.queryResourceProperties(params);
    }
  }

  public de.zib.gndms.dspace.slice.stubs.types.SliceReference transformSliceTo(types.SliceTransformSpecifierT sliceTransformSpecifier,types.ContextT context) throws RemoteException, de.zib.gndms.dspace.stubs.types.UnknownSubspace, de.zib.gndms.dspace.subspace.stubs.types.OutOfSpace, de.zib.gndms.dspace.subspace.stubs.types.UnknownOrInvalidSliceKind, de.zib.gndms.dspace.stubs.types.InternalFailure {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"transformSliceTo");
    de.zib.gndms.dspace.slice.stubs.TransformSliceToRequest params = new de.zib.gndms.dspace.slice.stubs.TransformSliceToRequest();
    de.zib.gndms.dspace.slice.stubs.TransformSliceToRequestSliceTransformSpecifier sliceTransformSpecifierContainer = new de.zib.gndms.dspace.slice.stubs.TransformSliceToRequestSliceTransformSpecifier();
    sliceTransformSpecifierContainer.setSliceTransformSpecifier(sliceTransformSpecifier);
    params.setSliceTransformSpecifier(sliceTransformSpecifierContainer);
    de.zib.gndms.dspace.slice.stubs.TransformSliceToRequestContext contextContainer = new de.zib.gndms.dspace.slice.stubs.TransformSliceToRequestContext();
    contextContainer.setContext(context);
    params.setContext(contextContainer);
    de.zib.gndms.dspace.slice.stubs.TransformSliceToResponse boxedResult = portType.transformSliceTo(params);
    return boxedResult.getSliceReference();
    }
  }

    public String getSliceKind( ) throws RemoteException {
        GetResourcePropertyResponse resp = getResourceProperty( SliceConstants.SLICEKIND );
        return resp.get_any()[0].getValue( );
    }

    public String getSliceLocation( ) throws RemoteException {
        GetResourcePropertyResponse resp = getResourceProperty( SliceConstants.SLICELOCATION );
        return resp.get_any()[0].getValue( );
    }

    public Calendar getTerminationTime( ) throws Exception, ParseException, DeserializationException {
        GetResourcePropertyResponse resp = getResourceProperty( SliceConstants.TERMINATIONTIME );
        //Calendar tt = (Calendar) ObjectDeserializer.toObject( resp.get_any()[0], GregorianCalendar.class );
        String tm = (String) resp.get_any()[0].getObjectValue( String.class );
        DateTime dt = new DateTime( tm );
        return dt.toGregorianCalendar();
    }

    public long getTotalStorageSize( ) throws RemoteException, DeserializationException {
        GetResourcePropertyResponse resp = getResourceProperty( SliceConstants.TOTALSTORAGESIZE );
        return ( ( UnsignedLong ) ObjectDeserializer.toObject( resp.get_any()[0], UnsignedLong.class) ).longValue( );
    }

    public SubspaceClient getSubspace( ) throws RemoteException, DeserializationException, MalformedURIException {
        GetResourcePropertyResponse resp = getResourceProperty( SliceConstants.SUBSPACEREFERENCE );
        SubspaceReference srf = (SubspaceReference) ObjectDeserializer.toObject( resp.get_any()[0], SubspaceReference.class );
        return new SubspaceClient( srf.getEndpointReference() );
    }

    // some convenience methods for slice transformation

    // transforms the slice kind
    public SliceReference transformSliceTo( String sk, ContextT ctx ) throws MalformedURIException, RemoteException, InternalFailure, UnknownOrInvalidSliceKind, OutOfSpace {

        SliceTransformSpecifierT spec = new SliceTransformSpecifierT( );
        spec.setSliceKind( new URI( sk ) );
        return transformSliceTo( spec, ctx );
    }


    // puts the slice into another subspace
    public SliceReference transformSliceTo( javax.xml.namespace.QName subs, ContextT ctx  ) throws RemoteException, InternalFailure, UnknownOrInvalidSliceKind, OutOfSpace {

        SliceTransformSpecifierT spec = new SliceTransformSpecifierT( );
        spec.setSubspaceSpecifier( subs );
        return transformSliceTo( spec, ctx );
    }


    public SliceReference transformSliceTo( String sk, javax.xml.namespace.QName subs, ContextT ctx ) throws MalformedURIException, RemoteException, InternalFailure, UnknownOrInvalidSliceKind, OutOfSpace {

        SliceTransformSpecifierT spec = new SliceTransformSpecifierT( );
        SliceTypeSpecifierT tspec = new SliceTypeSpecifierT(  );
        tspec.setSliceKind( new URI( sk ) );
        tspec.setSubspaceSpecifier( subs );
        spec.setSliceTypeSpecifier( tspec );
        return transformSliceTo( spec, ctx );

    }
}
