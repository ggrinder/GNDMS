package de.zib.gndms.dspace.service.globus.resource;

/*
 * Copyright 2008-2010 Zuse Institute Berlin (ZIB)
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



import de.zib.gndms.dspace.common.DSpaceConstants;
import de.zib.gndms.dspace.common.DSpaceTools;
import de.zib.gndms.dspace.service.DSpaceConfiguration;
import de.zib.gndms.infra.GNDMSTools;
import de.zib.gndms.infra.GridConfig;
import de.zib.gndms.infra.service.GNDMPersistentServiceHome;
import de.zib.gndms.infra.service.GNDMSingletonServiceHome;
import de.zib.gndms.infra.system.GNDMSystem;
import de.zib.gndms.infra.wsrf.ReloadablePersistentResource;
import de.zib.gndms.model.dspace.DSpace;
import de.zib.gndms.neomodel.common.NeoDao;
import org.apache.axis.message.addressing.AttributedURI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.globus.wsrf.*;
import org.globus.wsrf.impl.SimpleResourceKey;
import org.jetbrains.annotations.NotNull;

import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.xml.namespace.QName;


/**
 * This class overrides the ResourceHome that is automatically generated by introduce for Globus
 * Toolkit. In GNDMS this is mainly necessary to provide RDBMS/JPA-based resource persistence.
 * In order to use the extended resource home they have to be configured in jndi-config.xml.
 * If this has been done properly, you should see an info-level log message during the start up
 * of the web service container that notifies successful initialization of the extended resource
 * home.
 *
 * @author  try ste fan pla nti kow zib
 * @version $Id$
 *
 *          User: stepn Date: 16.07.2008 Time: 12:35:27
 */
public final class ExtDSpaceResourceHome  extends DSpaceResourceHome
	  implements GNDMSingletonServiceHome, GNDMPersistentServiceHome<DSpace> {

	// logger can be an instance field since resource home classes are instantiated at most once
	@NotNull @SuppressWarnings({"FieldNameHidesFieldInSuperclass"})
	private final Log logger = LogFactory.getLog(ExtDSpaceResourceHome.class);

	// grid config for gndmsystem initialization
	@SuppressWarnings({"StaticVariableOfConcreteClass"})
	private static final GridConfig SHARED_CONFIG = new GridConfig() {
		@Override @NotNull
		public String getGridJNDIEnvName() throws Exception
			{ return DSpaceConfiguration.getConfiguration().getGridJNDIEnv(); }

		@Override @NotNull
		public String getGridName() throws Exception
			{ return DSpaceConfiguration.getConfiguration().getGridName(); }

		@Override @NotNull
		public String getGridPath() throws Exception
			{ return DSpaceConfiguration.getConfiguration().getGridPath(); }

	};

	public static GridConfig getGridConfig() {
		return SHARED_CONFIG;
	}



	private boolean initialized;

	// System: Set during initialization
	@SuppressWarnings({"FieldAccessedSynchronizedAndUnsynchronized"})
	@NotNull
	private GNDMSystem system;

	// Service Address: set during initialization
	@SuppressWarnings({"FieldAccessedSynchronizedAndUnsynchronized"})
	private AttributedURI serviceAddress;

	@Override
	public synchronized void initialize() throws Exception {
        if (! initialized) {
			logger.info("Extended DSpace home initializing");
			try { try {
                final GridConfig gridConfig = ExtDSpaceResourceHome.getGridConfig();
                logger.debug("Config: " + gridConfig.asString());
                system = gridConfig.retrieveSystemReference();
				serviceAddress = GNDMSTools.getServiceAddressFromContext();

				initialized = true;

				super.initialize();    // Overridden method
			}
			catch ( NamingException e) {
				throw new RuntimeException(e);
			} }
            catch (RuntimeException e) {
                initialized = false;
                logger.error("Initialization failed", e);
                e.printStackTrace(System.err);
                throw e;
            }
		}
    }



	private void ensureInitialized() {
		try
			{ initialize();	}
		catch (Exception e) {
			logger.error("Unexpected initialization error", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public Resource find(ResourceKey key) throws ResourceException {
		PersistentResource resource = (PersistentResource) super.find(null);
		if (key == null)
			return resource;
		else
			if (resource.getID().equals(key.getValue()))
				return resource;
			else
				throw new InvalidResourceKeyException("Invalid singleton key");
	}


    @Override
	public Resource createSingleton() {
		try	{
			final DSpaceResource resource = new DSpaceResource();
			resource.setResourceHome(this);
			resource.load(null);
			return resource;
		}
		catch (ResourceException e) {
			logger.error(e);
			return null;
		}
	}

    @NotNull
    public Class<DSpace> getModelClass() {
        return DSpace.class;
    }


    public ResourceKey getKeyForResourceModel( @NotNull DSpace model ) {
        return getKeyForId( model.getId( ) );
    }


    @Override
    public QName getKeyTypeName() {
        return DSpaceConstants.RESOURCE_KEY;
    }


    @Override
    public Class getKeyTypeClass() {
        return String.class;
    }


    @NotNull
    public Query getListAllQuery(final @NotNull EntityManager em) {
        throw new UnsupportedOperationException();
    }


    public void refresh(final @NotNull DSpace resourceModel) throws ResourceException {
        DSpaceTools.refreshModelResource(resourceModel, this);
    }


    @NotNull
    public GNDMSystem getSystem() throws IllegalStateException {
        ensureInitialized();
        return system;
    }


    public void setSystem(@NotNull GNDMSystem newSystem) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Cant overwrite system");
    }


    @NotNull
    public String getNickName() {
        return "dspace";
    }

    public ResourceKey getKeyForId(@NotNull final String id) {
        return id == null ? null : new SimpleResourceKey( getKeyTypeName(), id );
    }


    @NotNull
    public EntityManagerFactory getEntityManagerFactory() {
        return getSystem().getEntityManagerFactory();
    }


    @NotNull
    public AttributedURI getServiceAddress() {
        ensureInitialized();
        return serviceAddress;
    }


    @SuppressWarnings({ "unchecked" })
    public String getSingletonID() throws ResourceException {
        return ((ReloadablePersistentResource<DSpace, ExtDSpaceResourceHome>)find(null)).getID();
    }

    @NotNull public NeoDao getDao() {
        return system.getDao();
    }
}
