package de.zib.gndms.dspace.slice.service.globus.resource;

import de.zib.gndms.dspace.common.DSpaceTools;
import de.zib.gndms.dspace.service.globus.resource.ExtDSpaceResourceHome;
import de.zib.gndms.dspace.slice.stubs.types.SliceReference;
import de.zib.gndms.infra.GNDMSTools;
import de.zib.gndms.infra.GridConfig;
import de.zib.gndms.infra.service.GNDMPersistentServiceHome;
import de.zib.gndms.infra.system.GNDMSystem;
import de.zib.gndms.infra.wsrf.ReloadablePersistentResource;
import de.zib.gndms.model.dspace.Slice;
import org.apache.axis.message.addressing.AttributedURI;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.globus.wsrf.Resource;
import org.globus.wsrf.ResourceException;
import org.globus.wsrf.ResourceKey;
import org.globus.wsrf.impl.SimpleResourceKey;
import org.globus.wsrf.utils.AddressingUtils;
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
 * of the web service container that notifies succesfull initialization of the extended resource
 * home.
 *
 * @author Stefan Plantikow <plantikow@zib.de>
 * @version $Id$
 *
 *          User: stepn Date: 16.07.2008 Time: 12:35:27
 */
public final class ExtSliceResourceHome extends SliceResourceHome
    implements GNDMPersistentServiceHome<Slice> {

	// logger can be an instance field since resource home classes are instantiated at most once
	@NotNull
	@SuppressWarnings({"FieldNameHidesFieldInSuperclass"})
	private final Log logger = LogFactory.getLog(ExtSliceResourceHome.class);

    // System: Set during initialization
	@SuppressWarnings({"FieldAccessedSynchronizedAndUnsynchronized"})
	@NotNull
	private GNDMSystem system;

	// Serbice Address: set during initialization
	@SuppressWarnings({"FieldAccessedSynchronizedAndUnsynchronized"})
	private AttributedURI serviceAddress;

    private boolean initialized;

    @Override
	public synchronized void initialize() throws Exception {
        if (! initialized) {
			logger.info("Extended Slice home initializing");
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


    @SuppressWarnings({ "unchecked", "RawUseOfParameterizedType" })
    @Override
    protected Resource createNewInstance() throws ResourceException {
        final Resource instance = super.createNewInstance();
        (( ReloadablePersistentResource )instance).setResourceHome(this);
        return instance;
    }

    @NotNull
    public URI getServiceAddress() {
        ensureInitialized();
		return serviceAddress;
    }

    public QName getResourceKeyTypeName() {
        return getKeyTypeName();
    }

    @NotNull
    public EntityManagerFactory getEntityManagerFactory() {
        return getSystem().getEntityManagerFactory();
    }

    @NotNull
    public GNDMSystem getSystem() throws IllegalStateException {
        ensureInitialized();
        return system;
    }

    public void setSystem( @NotNull GNDMSystem systemParam ) throws IllegalStateException {
        throw new UnsupportedOperationException("Cant overwrite system");
    }

    @NotNull
    public Query getListAllQuery(final @NotNull EntityManager em) {
        return em.createNamedQuery("listAllSliceIds");
    }


    public void refresh( @NotNull Slice resource ) throws ResourceException {
        DSpaceTools.refreshModelResource( resource, this );
    }

    @NotNull
    public String getNickName() {
        return "slice";
    }

    @NotNull
    public Class<Slice> getModelClass() {
        return Slice.class;
    }

    public ResourceKey getKeyForResourceModel( @NotNull Slice model ) {
        return getKeyForId(model.getId());
    }


    @NotNull
    public ResourceKey getKeyForId(@NotNull final String id) {
        return new SimpleResourceKey( getKeyTypeName(), id );
    }

    @Override
    public SliceReference getResourceReference(final @NotNull ResourceKey key) throws Exception {
		EndpointReferenceType epr = AddressingUtils.createEndpointReference(serviceAddress.toString(), key);
		SliceReference ref = new SliceReference();
		ref.setEndpointReference(epr);
		return ref;
    }
}
