package de.zib.gndms.infra.model;

import de.zib.gndms.infra.service.GNDMServiceHome;
import de.zib.gndms.infra.wsrf.ReloadablePersistentResource;
import de.zib.gndms.model.common.GridResource;
import org.jetbrains.annotations.NotNull;

/**
 * Specializing ModelHandler for GridResources
 *
 * @see de.zib.gndms.model.common.GridResource
 * @author Stefan Plantikow <plantikow@zib.de>
 * @version $Id$
 *
 *          User: stepn Date: 09.08.2008 Time: 12:21:32
 */
public class GridResourceModelHandler<M extends GridResource, H extends GNDMServiceHome, R extends ReloadablePersistentResource<M, H>> extends GridEntityModelHandler<M, H, R> {

	public GridResourceModelHandler(final Class<M> theClazz, final H homeParam) {
		super(theClazz, homeParam);    // Overridden method
	}

	@Override
	protected @NotNull M createNewEntity() {
		final @NotNull M model = super.createNewEntity();    // Overridden method
		model.setId(nextUUID());
		return model;
	}

}