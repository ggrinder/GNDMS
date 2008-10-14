package de.zib.gndms.logic.model;

import de.zib.gndms.logic.action.Action;
import de.zib.gndms.model.common.GridResource;
import de.zib.gndms.model.common.ModelUUIDGen;
import org.jetbrains.annotations.NotNull;

import javax.persistence.EntityManager;


/**
 * ThingAMagic.
 *
 * @author Stefan Plantikow <plantikow@zib.de>
 * @version $Id$
 *
 *          User: stepn Date: 14.08.2008 Time: 14:44:57
 */
public interface EntityAction<R> extends Action<R>, ModelUUIDGen {
    EntityManager getEntityManager();

    EntityManager getOwnEntityManager();

    void setOwnEntityManager(final @NotNull EntityManager entityManagerParam);


    BatchUpdateAction<GridResource, ?> getPostponedActions();

    void setOwnPostponedActions(final @NotNull BatchUpdateAction<GridResource, ?> postponedActionsParam);

}
