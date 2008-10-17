package de.zib.gndms.GORFX.common.type.io;

import de.zib.gndms.model.gorfx.types.AbstractORQ;
import types.ContextT;

/**
 * @author: Maik Jorra <jorra@zib.de>
 * @version: $Id$
 * <p/>
 * User: mjorra, Date: 14.10.2008, Time: 13:16:31
 */
public class AbstractORQXSDReader {

    public static <M extends AbstractORQ> M read( Class<M> clazz, ContextT ctx ) throws IllegalAccessException, InstantiationException {

        M orq = clazz.newInstance();
        orq.setContext( ContextXSDReader.readContext( ctx ) );

        return orq;
    }


}