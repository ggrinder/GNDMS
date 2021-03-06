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



import de.zib.gndms.logic.model.config.ConfigActionResult;
import de.zib.gndms.logic.model.config.ConfigActionHelp;
import de.zib.gndms.logic.model.config.ConfigOption;
import de.zib.gndms.logic.model.gorfx.permissions.PermissionConfiglet;
import de.zib.gndms.model.common.ConfigletState;
import de.zib.gndms.kit.config.MandatoryOptionMissingException;

import javax.persistence.EntityManager;
import java.io.*;
import java.util.Properties;

/**
 *
 * This class provides a default implementation of {@code SetupConfigletAction}, intended to store
 * permissions in database and manipulate them.
 *
 * <p>A <tt>SetupPermissionConfigletAction</tt> contains a String {@link #permissionProperties} which will either be retrieved from the
 * configuration map during the initialization, or can be set using the corresponding setter methods.
 *
 * 
 * When this action is started with <tt>create</tt> or <tt>update</tt> as SetupMode, the configuration map must
 * have an option 'permissionProperties' set. Otherwise an <tt>IllegalStateException</tt> will be thrown.
 *
 * On
 * <ul>
 *  <li>
 *       <tt>create</tt> mode, <tt>permissionProperties</tt> (converted as <tt>Properties</tt>) will be set as the state
 *      of the entity.
 *  </li>
 *  <li>
 *      <tt>update</tt> mode, permissions declared in <tt>permissionProperties</tt> will be added to an existing entity
 *      and may overwrite allocated values.
    </li>
 *  <li>
 *      <tt>read</tt> mode, the permission properties from an entity will be written to an printwriter
    </li>
 *  </ul>
 *
 * <p>By default the field {@link #name} will be set to "PermissionConfiglet" and
 * the {@link #className} will be set to PermissionConfiglet.class
 * 
 * <p>An instance of this class returns a {@code ConfigActionResult} informing about the success of its execution, when
 * the <tt>execute()</tt> method is called.
 *
 * @see ConfigletState
 * @author  try ma ik jo rr a zib
 * @version $Id$
 *          <p/>
 *          User: mjorra, Date: 09.01.2009, Time: 14:53:12
 */
@ConfigActionHelp(shortHelp = "Setup the permission configlet in the database")
public class SetupPermissionConfigletAction extends SetupConfigletAction {

    private static final String NAME = "PermissionConfiglet";
    private static final Class CLAZZ = PermissionConfiglet.class;

    @ConfigOption( descr = "Properties describing the permissions" )
    private String permissionProperties;


    public SetupPermissionConfigletAction( ) {
        setName( NAME );
        setClassName( CLAZZ.getName() );
    }


    /**
     * Calls <tt>super.initialize()</tt> and checks if the parameter 'permissionProperties' has been set in the configuration map,
     * if SetupMode is <tt>create</tt> or <tt>update</tt>.
     */
    @Override
    public void initialize() {
        super.initialize();    // Overridden method
        if ( SetupMode.CREATE.equals( getMode() )
            || SetupMode.UPDATE.equals( getMode() ) )
        {
            if( !hasOption( "permissionProperties" ) )
                throw new IllegalStateException( "Permission properties are required for creation and update" );
        }
    }


    /**
     * Prints the properties from <tt>state </tt> using the Printwriter {@code writerParam}.
     *
     * @param state the state, the printwriter will read from
     * @param emParam 
     * @param writerParam the printwriter the state will be written to.
     * @return
     */
    @Override
    protected ConfigActionResult read( ConfigletState state, EntityManager emParam, PrintWriter writerParam ) {

        Properties prop = ( Properties ) state.getState( );
        ByteArrayOutputStream os = new ByteArrayOutputStream( );
        try {
            prop.store( os, NAME );
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }

        writerParam.print( os.toString( ) );

        return ok();
    }


    /**
     * Generates a <tt>properties</tt> instance, containg the permission properties and sets it as as the state of <tt>state</tt>
     *
     * @param state the ConfigletState to be created
     * @param emParam
     * @param writerParam
     * @return An {@code OKResult} instance, if no problem occurred. Otherwise a {@code FailedResult} instance.
     */
    @Override
    protected ConfigActionResult create( ConfigletState state, EntityManager emParam, PrintWriter writerParam ) {

        state.setState( asProperties() );
        return ok(  );
    }

    /***
     * Adds the permission properties, belonging to <tt>this</tt>, to <tt>state</tt>.
     * Already existing keys will be overwritten.
     *
     * @param state the ConfigletState to be changed
     * @param emParam 
     * @param writerParam
     * @return
     */
    @Override
    protected ConfigActionResult update( ConfigletState state, EntityManager emParam, PrintWriter writerParam ) {

        Properties op = (Properties) state.getState( );
        Properties np = asProperties();

        for( Object k : np.keySet( ) ) {
            String key = (String) k;
            op.setProperty( key, np.getProperty( key ) );
        }

        state.setState( op );
        
        return ok( );
    }

    /**
     * Returns {@code permissionProperties}. If not set using {@code setPermissionProperties},
     * it will try to retrive it from the value set for the option {@code permissionProperties} in the configuration map.
     *  
     * @return
     */
    public String getPermissionProperties() {

        if( permissionProperties == null ) {
            try{
                permissionProperties = getOption( "permissionProperties" ).replaceAll( "\\s+__EOL__\\s*", "\n");
            } catch ( MandatoryOptionMissingException e ) {
                // can be safely ignored, we wouldn't have come this far w/o permissionProperties
            }
        }

        return permissionProperties;
    }


    public void setPermissionProperties( String permissionProperties ) {
        this.permissionProperties = permissionProperties;
    }


    /**
     * Returns the {@code permissionProperties} as {@code Properties}
     * 
     * @return the {@code permissionProperties} as {@code Properties}
     */
    private Properties asProperties(  ) {
                                          
        Properties prop = new Properties( );
        try{
            ByteArrayInputStream bis = new ByteArrayInputStream( getPermissionProperties().getBytes( "ISO8859_1" ) );
            prop.load( bis );
            bis.close( );
        } catch ( UnsupportedEncodingException e ) {
            throw new RuntimeException( e );
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }

        return prop;
    }
}
