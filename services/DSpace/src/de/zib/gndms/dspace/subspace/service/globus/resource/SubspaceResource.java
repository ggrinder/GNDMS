package de.zib.gndms.dspace.subspace.service.globus.resource;

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



import de.zib.gndms.dspace.common.DSpaceTools;
import de.zib.gndms.dspace.stubs.types.DSpaceReference;
import de.zib.gndms.dspace.subspace.common.SubspaceConstants;
import de.zib.gndms.dspace.subspace.stubs.SubspaceResourceProperties;
import de.zib.gndms.infra.model.GridEntityModelHandler;
import de.zib.gndms.infra.model.GridResourceModelHandler;
import de.zib.gndms.infra.system.GNDMSystem;
import de.zib.gndms.infra.wsrf.ReloadablePersistentResource;
import de.zib.gndms.model.dspace.Subspace;
import org.globus.wsrf.InvalidResourceKeyException;
import org.globus.wsrf.ResourceException;
import org.globus.wsrf.ResourceKey;
import org.jetbrains.annotations.NotNull;
import org.apache.axis.types.UnsignedLong;


/**
 * The implementation of this SubspaceResource type.
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
@SuppressWarnings({"FeatureEnvy"})
public class SubspaceResource extends SubspaceResourceBase
	  implements ReloadablePersistentResource<Subspace, ExtSubspaceResourceHome> {

	private GridEntityModelHandler<Subspace, ExtSubspaceResourceHome, SubspaceResource> mH;
	private ExtSubspaceResourceHome resourceHome;


	// Overridden setters modify the model and by virtue of a database trigger set the
	// resource properties

	@Override
	public void setDSpaceReference(final DSpaceReference dSpaceReference) throws ResourceException {
		throw new UnsupportedOperationException("Cannot overwrite this read-only property");
	}


	@Override
	public void setAvailableStorageSize(final UnsignedLong availableStorageSize)
		  throws ResourceException {
		Subspace model = loadModelById(getID());
		model.setAvailableSize( super.getAvailableStorageSize().longValue( ) );
		mH.mergeModel(null, model);
	}


	@Override
	public void setTotalStorageSize(final UnsignedLong totalStorageSize) throws ResourceException {
		Subspace model = loadModelById(getID());
		model.setTotalSize( super.getTotalStorageSize().longValue() );
		mH.mergeModel(null, model);
	}



	@NotNull
	public Subspace loadModelById(final @NotNull String id) throws ResourceException {
		return mH.loadModelById(null, id);
	}


	public void loadViaModelId(final @NotNull String id) throws ResourceException {
		loadFromModel(loadModelById(id));
	}


    public synchronized void loadFromModel(final @NotNull Subspace model) throws ResourceException {
        loadFromModel((SubspaceResourceProperties) getResourceBean(), model);
    }

	public synchronized void loadFromModel(
            final @NotNull SubspaceResourceProperties bean, final @NotNull Subspace model) 
            throws ResourceException {
        // set resource properties from model
        bean.setAvailableStorageSize( DSpaceTools.unsignedLongFromLong( model.getAvailableSize() ) );
        bean.setTotalStorageSize( DSpaceTools.unsignedLongFromLong( model.getTotalSize() ) );

        try {
            final DSpaceReference dSpaceReference =
                  DSpaceTools.dSpaceRefsAsReference(model.getDSpaceRef(), getSystem());
            bean.setDSpaceReference(dSpaceReference);
        }
        catch (Exception e)	{
            logger.error(e);
            e.printStackTrace(System.err);
        }
        // bean.setTerminationTime(model.getTerminationTime());
	}


    @Override
    public void refreshRegistration(final boolean forceRefresh) {
        // super.refreshRegistration(forceRefresh);    // Overridden method
        /* we dont' use this feature */
    }


    public void load(final @NotNull ResourceKey resourceKeyParam)
		  throws ResourceException {
		// only called once, during find!
		final @NotNull String id = (String)resourceKeyParam.getValue();

		Subspace model = loadModelById(id);
		if (getResourceHome().getKeyTypeName().equals(resourceKeyParam.getName())) {
			setResourceKey(resourceKeyParam);
            final SubspaceResourceProperties bean = new SubspaceResourceProperties();
            loadFromModel(bean, model);
            initialize(bean, SubspaceConstants.RESOURCE_PROPERTY_SET, id);
		}
		else
			throw new InvalidResourceKeyException("Invalid resourceKey name");
        
	}


    public void store() throws ResourceException {
		// done elsewhere
	}


	public void remove() throws ResourceException {
		throw new UnsupportedOperationException(
			  "Currently only possible when offline, via the database");
	}


	@NotNull
	public final ExtSubspaceResourceHome getResourceHome() {
		if (resourceHome == null)
			throw new IllegalStateException("No resourceHome set");
		return resourceHome;
	}


	public final void setResourceHome(
		  final @NotNull ExtSubspaceResourceHome resourceHomeParam) {
		if (resourceHome == null) {
			mH = new GridResourceModelHandler<Subspace, ExtSubspaceResourceHome, SubspaceResource>
				  (Subspace.class, resourceHomeParam);
			resourceHome = resourceHomeParam;
		}
		else
			throw new IllegalStateException("resourceHome already set");
	}

	@NotNull
	private GNDMSystem getSystem() throws ResourceException {
		return getResourceHome().getSystem();
	}


	@Override @NotNull
	public String getID() {
		return (String) super.getID();    // Overridden method
	}


}
