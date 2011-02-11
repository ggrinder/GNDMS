package de.zib.gndms.neomodel.common;

import de.zib.gndms.model.common.GridResourceItf;
import org.jetbrains.annotations.NotNull;
import org.neo4j.graphdb.Node;

/**
 * Created by IntelliJ IDEA.
 * User: stepn
 * Date: 03.02.11
 * Time: 16:33
 * To change this template use File | Settings | File Templates.
 */
public class NodeGridResource<I> extends ModelNode implements GridResourceItf, ROType<I> {
    private static final String GRID_RESOURCE_ID_P = "ID_P";

    protected NodeGridResource(@NotNull NeoReprSession session, @NotNull String typeNick, @NotNull Node underlying) {
        super(session, typeNick, underlying);
    }

    public String getId() {
        return (String) repr().getProperty(GRID_RESOURCE_ID_P, null);
    }

    public void setId(String id) {
        repr().setProperty(GRID_RESOURCE_ID_P, id);
    }

    public I getReadOnly() {
        return (I) this;
    }
}
