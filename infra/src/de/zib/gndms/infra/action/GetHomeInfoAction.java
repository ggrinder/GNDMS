package de.zib.gndms.infra.action;

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



import de.zib.gndms.infra.service.GNDMServiceHome;
import de.zib.gndms.infra.service.GNDMSingletonServiceHome;
import de.zib.gndms.infra.service.GNDMPersistentServiceHome;
import de.zib.gndms.kit.config.MandatoryOptionMissingException;
import de.zib.gndms.logic.model.config.ConfigActionHelp;
import de.zib.gndms.logic.model.config.ConfigOption;
import de.zib.gndms.model.common.GridResource;
import org.apache.axis.types.URI;
import org.jetbrains.annotations.NotNull;
import org.globus.wsrf.ResourceException;

import javax.persistence.EntityManager;
import javax.xml.namespace.QName;
import java.io.PrintWriter;


/**
 * Figures out key type names and other information about a resource home at runtime
 *
 * <p>When {@code initialize()} is invoked, it tries to retrieve the lookup service home of the GNDMSystem from the
 * configuration map. Therefore the option {@link #home 'home'} must be set.
 *
 * @see de.zib.gndms.infra.service.GNDMServiceHome
 * @author  try ste fan pla nti kow zib
 * @version $Id$
 *
 *          User: stepn Date: 22.08.2008 Time: 16:47:23
 */
@ConfigActionHelp(shortHelp = "Retrieve information about a GNDMS resource home")
public class GetHomeInfoAction extends SystemAction<String> {
    @ConfigOption(descr = "Short name (without trailing 'Home') of the resource home whose info is to be determined")
    private String home;
    private static final int CAPACITY = 160;


    public GetHomeInfoAction() {
        super();
    }


    @Override
    public void initialize() {
        super.initialize();    // Overridden method
        try {
            home = getOption("home");
            if (home.endsWith("Home"))
                home = home.substring(0, home.length()-4);
        }
        catch (MandatoryOptionMissingException e) {
            throw new IllegalArgumentException(e);
        }
    }


    @SuppressWarnings({ "FeatureEnvy" })
    @Override
    public @NotNull String execute(final @NotNull EntityManager em, 
                                   final @NotNull PrintWriter writer) {
        final @NotNull GNDMServiceHome instance =
                getSystem().getInstanceDir().lookupServiceHome(home);

        StringBuilder buffer = new StringBuilder(CAPACITY);
        buffer.append("home: '");
        final String nick = instance.getNickName();
        buffer.append(nick);
        buffer.append("'; ");

        if (instance instanceof GNDMSingletonServiceHome){
            try {
                String singleId = ((GNDMSingletonServiceHome)instance).getSingletonID();
                buffer.append("singletonId: '");
                buffer.append(singleId == null ? "(null)" : singleId);
                buffer.append("'; ");
            }
            catch (ResourceException e) {
                throw new RuntimeException(e);
            }
        }
        buffer.append("serviceAddress: '");
        final URI addr = instance.getServiceAddress();
        buffer.append(addr.toString());
        buffer.append("'; ");

        buffer.append("homeClass: '");
        buffer.append(instance.getClass().getName());
        buffer.append("'; ");

        if (instance instanceof GNDMPersistentServiceHome) {
            buffer.append("modelClass: '");
            final Class<? extends GridResource> modelClass =
                    ((GNDMPersistentServiceHome<?>) instance).getModelClass();
            buffer.append(modelClass.getName());
            buffer.append("'; ");
        }

        buffer.append("keyTypeClass: '");
        final Class<?> keyClass = instance.getKeyTypeClass();
        buffer.append(keyClass == null ? "(null)" : keyClass.getName());
        buffer.append("'; ");

        buffer.append("keyTypeName: '");
        final QName qName = instance.getKeyTypeName();
        buffer.append(qName == null ? "(null)" : qName.toString());
        buffer.append("'; ");

        return buffer.toString();
    }
}
