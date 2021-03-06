package de.zib.gndms.GORFX.service.globus.resource;

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



import de.zib.gndms.GORFX.service.GORFXConfiguration;
import de.zib.gndms.infra.GNDMSTools;
import de.zib.gndms.infra.GridConfig;
import de.zib.gndms.infra.service.GNDMSingletonServiceHome;
import de.zib.gndms.infra.system.GNDMSystem;
import de.zib.gndms.model.gorfx.types.io.xml.ProviderStageInXML;
import de.zib.gndms.gritserv.typecon.util.ProviderStageInXMLImpl;
import de.zib.gndms.logic.model.gorfx.c3grid.ParmFormatAux;
import org.apache.axis.message.addressing.AttributedURI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.globus.wsrf.Resource;
import org.globus.wsrf.ResourceException;
import org.globus.wsrf.ResourceKey;
import org.globus.wsrf.impl.SimpleResourceKey;
import org.jetbrains.annotations.NotNull;

import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;


/**
 * This class overrides the ResourceHome that is automatically generated by introduce for Globus
 * Toolkit. In GNDMS this is mainly necessary to provide RDBMS/JPA-based resource persistence.
 * In order to use the extended resource home they have to be configured in jndi-config.xml.
 * If this has been done properly, you should see an info-level log message during the start up
 * of the web service container that notifies succesfull initialization of the extended resource
 * home.
 *
 * @author  try ste fan pla nti kow zib
 * @version $Id$
 *
 *          User: stepn Date: 16.07.2008 Time: 12:35:27
 */
public final class ExtGORFXResourceHome extends GORFXResourceHome
    implements GNDMSingletonServiceHome
{

    // logger can be an instance field since resource home classes are instantiated at most once
	@NotNull
	@SuppressWarnings({"FieldNameHidesFieldInSuperclass"})
	private final Log logger = LogFactory.getLog(ExtGORFXResourceHome.class);

    // System: Set during initialization
    @SuppressWarnings({"FieldAccessedSynchronizedAndUnsynchronized"})
    @NotNull
    private GNDMSystem system;

    // Serbice Address: set during initialization
    @SuppressWarnings({"FieldAccessedSynchronizedAndUnsynchronized"})
    private AttributedURI serviceAddress;

    private boolean initialized;


    // grid config for gndmsystem initialization
    @SuppressWarnings({"StaticVariableOfConcreteClass"})
    private static final GridConfig SHARED_CONFIG = new GridConfig() {
        @Override @NotNull
        public String getGridJNDIEnvName() throws Exception
        { return GORFXConfiguration.getConfiguration().getGridJNDIEnv(); }

        @Override @NotNull
        public String getGridName() throws Exception
        { return GORFXConfiguration.getConfiguration().getGridName(); }

        @Override @NotNull
        public String getGridPath() throws Exception
        { return GORFXConfiguration.getConfiguration().getGridPath(); }

    };


    public static GridConfig getGridConfig() {
        return SHARED_CONFIG;
    }

    @SuppressWarnings({ "UseOfSystemOutOrSystemErr" })
    @Override
	public synchronized void initialize() throws Exception {
        if (! initialized) {
			logger.info("Extended GORFX home initializing");
			try { try {
                final GridConfig gridConfig = getGridConfig();
                logger.debug("Config: " + gridConfig.asString());
                system = gridConfig.retrieveSystemReference();
				serviceAddress = GNDMSTools.getServiceAddressFromContext();

                // todo add ProviderStageInXSD to system
                // no nice place here but i currently don't know another
                system.getInstanceDir().addInstance( ProviderStageInXML.class.getName(), new ProviderStageInXMLImpl( ) );
                ParmFormatAux.setXmlWriter( new ProviderStageInXMLImpl( ) );

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


    @NotNull
    public synchronized GNDMSystem getSystem() {
        ensureInitialized(); 
        return system;
    }


    public void setSystem( @NotNull GNDMSystem systemParam ) {
        throw new UnsupportedOperationException( "Attribute \"system\" is read-only" );
    }


    @NotNull
	public synchronized AttributedURI getServiceAddress() {
		ensureInitialized();
		return serviceAddress;
	}


    public ResourceKey getKeyForId(@NotNull final String id) {
        return new SimpleResourceKey( getKeyTypeName(), id );
    }


    @NotNull
    public String getNickName() {
        return "gorfx";
    }


    @Override
	public Resource createSingleton() {
          final GORFXResource resource = new GORFXResource();
          resource.setHome(this);
          return resource;
	}

    @SuppressWarnings({ "unchecked" })
    public String getSingletonID() throws ResourceException {
        return (String) ((GORFXResource)find(null)).getID();
    }


    @NotNull
    public EntityManagerFactory getEntityManagerFactory() {
       return getSystem().getEntityManagerFactory();
    }
}
