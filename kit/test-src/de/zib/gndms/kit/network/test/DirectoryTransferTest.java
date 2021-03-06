package de.zib.gndms.kit.network.test;

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



import java.util.TreeMap;
import java.util.HashMap;

/**
 * @author  try ma ik jo rr a zib
 * @version  $Id$
 * <p/>
 * User: mjorra, Date: 13.10.2008, Time: 10:39:57
 */
public class DirectoryTransferTest extends TransferTestMetaData {

    public DirectoryTransferTest() {
        
    }


    public DirectoryTransferTest( String sourceURI, String destinationURI ) {
        super( sourceURI, destinationURI );
    }


    @Override
    public void setup( ) {

        setFileMap( new TreeMap<String,String>( ) );

        HashMap<String, Long> fileSizes = new HashMap<String, Long>( );
        fileSizes.put( "a_1KB_file", new Long(       1024) );
        fileSizes.put( "b_1MB_file", new Long(    1048576) );
        fileSizes.put( "c_1GB_file", new Long( 1073741824) );
        setFileSizes( fileSizes );
    }
}
