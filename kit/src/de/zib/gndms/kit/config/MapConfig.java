package de.zib.gndms.kit.config;

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



import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Collections;
import java.util.Iterator;


/**
* This class holds a configuration in a map with methods to read the value of a chosen option.
* Note: A value can also contain system environment variables.                                 
* 
* @see de.zib.gndms.kit.config.AbstractConfig
* @author  try ste fan pla nti kow zib
* @version $Id$
*
*          User: stepn Date: 06.10.2008 Time: 11:21:58
*/
public class MapConfig extends AbstractConfig {
    /**
     * the map containing the keys and their corresponding values
     */
    private final Map<String, String> map;


    /**
     * Loads a new configuration.
     *
     * @param mapParam the new configuration, mapping the optionnames to their set values
     */
    public MapConfig(final Map<String, String> mapParam) {
        super();
        map = Collections.unmodifiableMap(mapParam);
    }


    @Override
    public String getConcreteNonMandatoryOption(final String nameParam) {
        return map.get(nameParam);
    }


    public boolean hasOption(final @NotNull String name) {
        return map.containsKey(name);
    }

    /**
     * Returns an iterator over the keys of the configuration
     * @return an iterator over the keys of the configuration
     */
	public Iterator<String> iterator() {
		return Collections.unmodifiableSet(map.keySet()).iterator();
	}
}
