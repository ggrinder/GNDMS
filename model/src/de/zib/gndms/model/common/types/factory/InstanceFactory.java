package de.zib.gndms.model.common.types.factory;

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


/**
 * An InstanceFactory creates instances for a chosen class. being a subclass of KeyFactoryInstance
 *
 * @author  try ste fan pla nti kow zib
 * @version $Id$
 *
 *          User: stepn Date: 05.09.2008 Time: 18:00:40
 */
public class InstanceFactory<T extends KeyFactoryInstance<Class<T>, T>> implements KeyFactory<Class<T>, T> {

    @NotNull
    public T getInstance(@NotNull final Class<T> clazz)
            throws IllegalAccessException, InstantiationException {
        final T instance = clazz.newInstance();
        instance.setFactory(this);
        instance.setKey(clazz);
        return instance;
    }


    public void setup() {
    }
}
