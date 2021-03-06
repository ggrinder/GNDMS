package de.zib.gndms.gritserv.typecon.tests.io;

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



import de.zib.gndms.gritserv.typecon.types.ProviderStageInORQXSDTypeWriter;
import de.zib.gndms.gritserv.typecon.types.ProviderStageInORQXSDReader;
import de.zib.gndms.model.gorfx.types.ProviderStageInORQ;
import de.zib.gndms.model.gorfx.types.io.ProviderStageInORQConverter;
import de.zib.gndms.model.gorfx.types.io.ProviderStageInORQPropertyReader;
import de.zib.gndms.model.gorfx.types.io.ProviderStageInORQPropertyWriter;
import de.zib.gndms.model.gorfx.types.io.tests.ProviderStageInORQIOTest;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import types.ProviderStageInORQT;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


/**
 * @author  try ma ik jo rr a zib
 * @version  $Id$
 * <p/>
 * User: mjorra, Date: 16.10.2008, Time: 11:16:06
 */
public class ProviderStageInORQIO {

    private String propFileName;
    private Properties prop;


    @Parameters( "propFileName" )
    public ProviderStageInORQIO( @Optional( "StageIn_io_test_props.properties" ) String propFileName ) {
        this.propFileName = propFileName;
    }


    @BeforeClass( groups={ "io" } )
    public void beforeClass( ) {

        prop = new Properties( );

        try {
            InputStream is = new FileInputStream( propFileName );
            prop.load( is );
            is.close( );
        } catch ( FileNotFoundException e ) {
            System.err.println( "Please ensure that your specified workingDir contains " +propFileName );
            System.err.println( "If the file isn't present one can use: " );
            System.err.println( ProviderStageInORQIOTest.class.getName() );
            System.err.println( "to obtain it" );
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch ( IOException e ) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        System.out.println( "read following properties from file: " );
        try {
            prop.store( System.out, "some properties");
        } catch ( IOException e ) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }


    @Test( groups={ "io" } )
    public void testIt ( ) throws Exception {

        System.out.println( "Creating ORQ form properties" );
        ProviderStageInORQPropertyReader orqr = new ProviderStageInORQPropertyReader( prop );
        orqr.performReading();

        ProviderStageInORQ orq = orqr.getProduct();

        // create XSDT form orq
        ProviderStageInORQXSDTypeWriter writer = new ProviderStageInORQXSDTypeWriter();
        ProviderStageInORQConverter conv = new ProviderStageInORQConverter( writer, orq );
        conv.convert( );

        ProviderStageInORQT orqt = writer.getProduct( );

        // convert orqt to orq
        ProviderStageInORQ norq = ProviderStageInORQXSDReader.read( orqt, null );
        Properties nprop = new Properties( );

        ProviderStageInORQPropertyWriter pwrit = new ProviderStageInORQPropertyWriter( nprop );
        conv.setModel( norq );
        conv.setWriter( pwrit );
        conv.convert( );

        // show written prop:
        System.out.println( "rewritten prop" );
        nprop.store( System.out, "rewritten prop" );

        System.out.println( "Possible exception may be caused by different order of the hashes" );
        assertEquals( prop, nprop );
    }
}
