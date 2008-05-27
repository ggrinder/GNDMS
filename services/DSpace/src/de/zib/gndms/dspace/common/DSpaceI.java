package de.zib.gndms.dspace.common;

import java.rmi.RemoteException;

/** 
 * This class is autogenerated, DO NOT EDIT.
 * 
 * This interface represents the API which is accessable on the grid service from the client. 
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
public interface DSpaceI {

  public org.oasis.wsrf.properties.GetMultipleResourcePropertiesResponse getMultipleResourceProperties(org.oasis.wsrf.properties.GetMultipleResourceProperties_Element params) throws RemoteException ;

  public org.oasis.wsrf.properties.GetResourcePropertyResponse getResourceProperty(javax.xml.namespace.QName params) throws RemoteException ;

  public org.oasis.wsrf.properties.QueryResourcePropertiesResponse queryResourceProperties(org.oasis.wsrf.properties.QueryResourceProperties_Element params) throws RemoteException ;

  /**
   * Returns the subspace with the given id
   *
   * @param subspaceId
   * @throws UnknownSubspace
   *	
   */
  public de.zib.gndms.dspace.subpace.stubs.types.SubspaceReference getSubspaceById(javax.xml.namespace.QName subspaceId) throws RemoteException, de.zib.gndms.dspace.stubs.types.UnknownSubspace ;

  /**
   * Returns list of all publically anounced subspaces for the given schema
   *
   * @param schemaURI
   */
  public de.zib.gndms.dspace.subpace.stubs.types.SubspaceReference[] listPublicSubspaces(org.apache.axis.types.URI schemaURI) throws RemoteException ;

  /**
   * Returns list of supported schemas
   *
   */
  public org.apache.axis.types.URI[] listSupportedSchemas() throws RemoteException ;

}

