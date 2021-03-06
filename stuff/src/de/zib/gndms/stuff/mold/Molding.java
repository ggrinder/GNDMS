package de.zib.gndms.stuff.mold;

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
 * This Interface is needed to create a {@code molder} for a specific class.
 *
 *
 * @see Molder
 * @author  try ste fan pla nti kow zib
 * @version $Id$
 *
 *          User: stepn Date: 01.12.2008 Time: 12:08:19
 */
public interface Molding {
    /**
     * Returns a molder for a specific class. 
     * @param moldedClazz the class, the {@code molder} should be applied to
     * @return a molder for instances of {@code moldedClazz}'s class
     */
    <D> Molder<D> molder(@NotNull final Class<D> moldedClazz);
}
