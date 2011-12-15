package de.zib.gndms.logic.model.dspace;

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

import de.zib.gndms.model.dspace.Slice;

import java.util.Calendar;
import java.util.List;

/**
 * Provides a mapping of slice ids and slices.
 * 
 * @author Ulrike Golas
 */

public interface SliceProvider {

	/**
	 * Checks whether a given slice id exists for the subspace.
	 * @param subspace The considered subspace.
	 * @param slice The slice id.
	 * @return true, if this slice exists, otherwise false.
	 */
	boolean exists(String subspace, String slice);
	
	/**
	 * Returns a list containing all existing slice ids for the subspace.
	 * @param subspace The considered subspace.
	 * @return The list.
	 * @throws NoSuchElementException 
	 */
    List<String> listSlices(String subspace) throws NoSuchElementException;

    /**
     * Returns the slice for a given slice id for the subspace.
	 * @param subspace The considered subspace.
     * @param slice The requested slice id.
     * @return The corresponding slice.
     * @throws NoSuchElementException 
     */
    Slice getSlice(String subspace, String slice) throws NoSuchElementException;

    String createSlice( String subspace, String sliceKind, String dn, Calendar ttm, long sliceSize ) throws NoSuchElementException;
}
