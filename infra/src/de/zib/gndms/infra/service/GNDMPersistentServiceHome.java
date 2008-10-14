package de.zib.gndms.infra.service;

import de.zib.gndms.model.common.GridResource;
import org.jetbrains.annotations.NotNull;
import org.globus.wsrf.ResourceException;
import org.globus.wsrf.ResourceKey;

import javax.persistence.EntityManager;
import javax.persistence.Query;


/**
 * ThingAMagic.
 *
 * @author Stefan Plantikow<plantikow@zib.de>
 * @version $Id$
 *
 *          User: stepn Date: 07.10.2008 Time: 16:20:10
 */
public interface GNDMPersistentServiceHome<M extends GridResource> extends GNDMServiceHome {

    void refresh(final @NotNull M resourceModel) throws ResourceException;

    @NotNull Query getListAllQuery(final @NotNull EntityManager em);

    @NotNull Class<M> getModelClass();

    ResourceKey getKeyForResourceModel( @NotNull M model );
}