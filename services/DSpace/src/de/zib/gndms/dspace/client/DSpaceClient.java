package de.zib.gndms.dspace.client;

import java.io.InputStream;
import java.rmi.RemoteException;
import java.net.URI;

import javax.xml.namespace.QName;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.client.AxisClient;
import org.apache.axis.client.Stub;
import org.apache.axis.configuration.FileProvider;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;

import org.oasis.wsrf.properties.GetResourcePropertyResponse;

import org.globus.gsi.GlobusCredential;

import de.zib.gndms.dspace.stubs.DSpacePortType;
import de.zib.gndms.dspace.stubs.service.DSpaceServiceAddressingLocator;
import de.zib.gndms.dspace.common.DSpaceI;
import de.zib.gndms.dspace.subspace.client.SubspaceClient;
import de.zib.gndms.dspace.subspace.stubs.types.SubspaceReference;
import de.zib.gndms.model.common.ImmutableScopedName;
import gov.nih.nci.cagrid.introduce.security.client.ServiceSecurityClient;

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
public class DSpaceClient extends DSpaceClientBase implements DSpaceI {	

	public DSpaceClient(String url) throws MalformedURIException, RemoteException {
		this(url,null);	
	}

	public DSpaceClient(String url, GlobusCredential proxy) throws MalformedURIException, RemoteException {
	   	super(url,proxy);
	}
	
	public DSpaceClient(EndpointReferenceType epr) throws MalformedURIException, RemoteException {
	   	this(epr,null);
	}
	
	public DSpaceClient(EndpointReferenceType epr, GlobusCredential proxy) throws MalformedURIException, RemoteException {
	   	super(epr,proxy);
	}

    public SubspaceClient findSubspace( String scopeName, String localName ) throws RemoteException, MalformedURIException {

        return findSubspace( new ImmutableScopedName( scopeName, localName ) );
    }


    public SubspaceClient findSubspace( ImmutableScopedName name ) throws RemoteException, MalformedURIException {

        return findSubspace( name.toQName() );
    }


    public SubspaceClient findSubspace( QName name ) throws RemoteException, MalformedURIException {

        SubspaceReference sr = getSubspace( name );
        return new SubspaceClient( sr.getEndpointReference() );
    }

    
    public static void usage(){
		System.out.println(DSpaceClient.class.getName() + " -url <service url>");
	}
	
	public static void main(String [] args){
	    System.out.println("Running the Grid Service Client");
		try{
		if(!(args.length < 2)){
			if(args[0].equals("-url")){
                DSpaceClient client = new DSpaceClient(args[1]);
                

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

  public de.zib.gndms.dspace.subspace.stubs.types.SubspaceReference getSubspace(javax.xml.namespace.QName subspaceSpecifier) throws RemoteException, de.zib.gndms.dspace.stubs.types.UnknownSubspace {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"getSubspace");
    de.zib.gndms.dspace.stubs.GetSubspaceRequest params = new de.zib.gndms.dspace.stubs.GetSubspaceRequest();
    de.zib.gndms.dspace.stubs.GetSubspaceRequestSubspaceSpecifier subspaceSpecifierContainer = new de.zib.gndms.dspace.stubs.GetSubspaceRequestSubspaceSpecifier();
    subspaceSpecifierContainer.setSubspaceSpecifier(subspaceSpecifier);
    params.setSubspaceSpecifier(subspaceSpecifierContainer);
    de.zib.gndms.dspace.stubs.GetSubspaceResponse boxedResult = portType.getSubspace(params);
    return boxedResult.getSubspaceReference();
    }
  }

  public de.zib.gndms.dspace.subspace.stubs.types.SubspaceReference[] listPublicSubspaces(org.apache.axis.types.URI schemaURI) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"listPublicSubspaces");
    de.zib.gndms.dspace.stubs.ListPublicSubspacesRequest params = new de.zib.gndms.dspace.stubs.ListPublicSubspacesRequest();
    params.setSchemaURI(schemaURI);
    de.zib.gndms.dspace.stubs.ListPublicSubspacesResponse boxedResult = portType.listPublicSubspaces(params);
    return boxedResult.getSubspaceReference();
    }
  }

  public org.apache.axis.types.URI[] listSupportedSchemas() throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"listSupportedSchemas");
    de.zib.gndms.dspace.stubs.ListSupportedSchemasRequest params = new de.zib.gndms.dspace.stubs.ListSupportedSchemasRequest();
    de.zib.gndms.dspace.stubs.ListSupportedSchemasResponse boxedResult = portType.listSupportedSchemas(params);
    return boxedResult.getResponse();
    }
  }

  public de.zib.gndms.dspace.slice.client.SliceClient createSliceInSubspace(javax.xml.namespace.QName subspaceSpecifier,types.SliceCreationSpecifier sliceCreationSpecifier,types.ContextT context) throws RemoteException, org.apache.axis.types.URI.MalformedURIException, de.zib.gndms.dspace.subspace.stubs.types.OutOfSpace, de.zib.gndms.dspace.subspace.stubs.types.UnknownOrInvalidSliceKind, de.zib.gndms.dspace.stubs.types.UnknownSubspace, de.zib.gndms.dspace.stubs.types.InternalFailure {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"createSliceInSubspace");
    de.zib.gndms.dspace.stubs.CreateSliceInSubspaceRequest params = new de.zib.gndms.dspace.stubs.CreateSliceInSubspaceRequest();
    de.zib.gndms.dspace.stubs.CreateSliceInSubspaceRequestSubspaceSpecifier subspaceSpecifierContainer = new de.zib.gndms.dspace.stubs.CreateSliceInSubspaceRequestSubspaceSpecifier();
    subspaceSpecifierContainer.setSubspaceSpecifier(subspaceSpecifier);
    params.setSubspaceSpecifier(subspaceSpecifierContainer);
    de.zib.gndms.dspace.stubs.CreateSliceInSubspaceRequestSliceCreationSpecifier sliceCreationSpecifierContainer = new de.zib.gndms.dspace.stubs.CreateSliceInSubspaceRequestSliceCreationSpecifier();
    sliceCreationSpecifierContainer.setSliceCreationSpecifier(sliceCreationSpecifier);
    params.setSliceCreationSpecifier(sliceCreationSpecifierContainer);
    de.zib.gndms.dspace.stubs.CreateSliceInSubspaceRequestContext contextContainer = new de.zib.gndms.dspace.stubs.CreateSliceInSubspaceRequestContext();
    contextContainer.setContext(context);
    params.setContext(contextContainer);
    de.zib.gndms.dspace.stubs.CreateSliceInSubspaceResponse boxedResult = portType.createSliceInSubspace(params);
    EndpointReferenceType ref = boxedResult.getSliceReference().getEndpointReference();
    return new de.zib.gndms.dspace.slice.client.SliceClient(ref);
    }
  }

}
