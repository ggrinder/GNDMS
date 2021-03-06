package de.zib.gndmc.GORFX.diag;

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



import de.zib.gndms.GORFX.ORQ.client.ORQClient;
import de.zib.gndms.GORFX.client.GORFXClient;
import de.zib.gndms.GORFX.context.client.TaskClient;
import de.zib.gndms.GORFX.offer.client.OfferClient;
import de.zib.gndms.gritserv.delegation.DelegationAux;
import de.zib.gndms.gritserv.typecon.types.AbstractXSDTypeWriter;
import de.zib.gndms.gritserv.typecon.types.ContextXSDTypeWriter;
import de.zib.gndms.gritserv.typecon.types.ContractXSDTypeWriter;
import de.zib.gndms.model.common.types.TransientContract;
import de.zib.gndms.model.gorfx.types.AbstractORQ;
import de.zib.gndms.model.gorfx.types.io.ORQConverter;
import de.zib.gndms.model.gorfx.types.io.ORQPropertyReader;
import de.zib.gndms.model.gorfx.types.io.ORQWriter;
import de.zib.gndmc.GORFX.GORFXClientUtils;
import org.apache.axis.components.uuid.UUIDGen;
import org.apache.axis.components.uuid.UUIDGenFactory;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.log4j.*;
import types.*;
import types.ContextT;
import types.ContextTEntry;
import de.zib.gndms.gritserv.delegation.DelegationAux;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Properties;

public abstract class RequestRunner implements Runnable{

    private static final UUIDGen uuidGen = UUIDGenFactory.getUUIDGen();
    private static final Logger logger;
    private static final int MILLIS = 2000;
    private ORQConverter converter;
    private String uuid;
    private DynamicOfferDataSeqT orqt;
    private HashMap<String, String> ctx = new HashMap<String,String>();
    private ContextT ctxt;
    private TransientContract con = null;
    private OfferExecutionContractT cont;
    private String gorfxUri;

    private AbstractORQ orq;
    private String proxy;


    static {
        logger = Logger.getLogger( StagingRunner.class );
        ConsoleAppender app = new ConsoleAppender( );
        PatternLayout lay = new PatternLayout( );
        lay.setConversionPattern( "%d{ISO8601} %-5p %c{2} [%t,%M:%L] <%x> %m%n" );
        app.setLayout( lay );
        app.setWriter( new PrintWriter( System.out ) );
        app.setTarget( "System.out");
        RequestRunner.logger.addAppender( app );
        RequestRunner.logger.setLevel( Level.DEBUG );
    }


    public RequestRunner( ) {

        uuid = uuidGen.nextUUID();
        ctx.put( "Workflow.Id", uuid );
    }
    

    public RequestRunner( String uri ) {

        setGorfxUri( uri );
        uuid = uuidGen.nextUUID();
        ctx.put( "Workflow.Id", uuid );
    }


    public abstract void show( );


    protected synchronized void show( ORQWriter w ) {

        System.out.println( "ORQ id: " + uuid );
        converter.setModel( getOrq() );
        converter.setWriter( w );
        converter.convert();
    }


    protected abstract void showResult( TaskClient tcnt ) throws Exception;


    public abstract void prepare( );


    protected <W extends AbstractXSDTypeWriter<? extends DynamicOfferDataSeqT> & ORQWriter>  void prepare( W w ) {

        converter.setModel( orq );
        converter.setWriter( w );
        converter.convert();
        orqt = w.getProduct();
        ctxt = ContextXSDTypeWriter.writeContext( ctx );
        // for debugging
        //showCtx( ctxt );
        if( con == null )
            cont = GORFXClientUtils.newContract();
        else
            cont = ContractXSDTypeWriter.write( con );
    }

    protected void showCtx( ContextT con ) {
        ContextTEntry[] entries = con.getEntry();
        ArrayList<ContextTEntry> al = new ArrayList<ContextTEntry>( entries.length );
        EndpointReferenceType epr = null;

        HashMap<String,String> res = new HashMap<String, String>( 1 );
        for( ContextTEntry e : entries )
            if( e.getKey().equals( DelegationAux.DELEGATION_EPR_KEY ) )
                logger.debug( e.getKey().toString() +": " +  e.get_value().toString( ) );
    }


    public abstract void fromProps( Properties props );


    protected void readProps( Properties props, ORQPropertyReader<?> r ) {

        r.setProperties( props );
        r.performReading();
        orq = r.getProduct();
    }
    

    public void run() {

        NDC.push( getUuid() );

        try{

            RequestRunner.logger.debug( "Starting" );

            EndpointReferenceType delegatEPR = null;
            if ( proxy != null && ! proxy.trim().equals( "" ) ) {
                RequestRunner.logger.debug( "Setting up delegation" );
                delegatEPR = GORFXClientUtils.setupDelegation( ctxt, gorfxUri, proxy );
            }

            // create gorfx client and retrieve orq
            final GORFXClient gcnt = new GORFXClient( gorfxUri );
            EndpointReferenceType epr = gcnt.createOfferRequest( orqt, ctxt );

            // create orq client and request offer
            final ORQClient orqcnt = new ORQClient( epr );
            epr = orqcnt.getOfferAndDestroyRequest( cont, ctxt );

            // create offer client and accept it
            final OfferClient ofcnt = new OfferClient( epr );
            epr = ofcnt.accept( );

            // create task resource an wait for completion
            final TaskClient tcnt = new TaskClient( epr );

            TaskStatusT state = TaskStatusT.unknown;
            boolean finished;
            boolean failed;

            do {
                state = tcnt.getTaskState();
                failed = state.equals( TaskStatusT.failed );
                finished = state.equals( TaskStatusT.finished );

                RequestRunner.logger.debug("Waiting for staging to finish...");
                try {
                    Thread.sleep( RequestRunner.MILLIS );
                }
                catch (InterruptedException e) {
                    // intentionally
                }
            }
            while (! (failed || finished ) );

            if( finished ) {
                showResult( tcnt );
            } else {
                final TaskExecutionFailure fail = tcnt.getExecutionFailure();
                TaskExecutionFailureImplementationFault tefif = fail.getImplementationFault();
                RequestRunner.logger.error( "message:       " + tefif.getMessage( ) );
                RequestRunner.logger.error( "faultClass:    " + tefif.getFaultClass( ) );
                RequestRunner.logger.error( "faultTrace:    " + tefif.getFaultTrace( ) );
                RequestRunner.logger.error( "faultLocation: " + tefif.getFaultLocation( ) );
            }

            if( delegatEPR != null)
                DelegationAux.destroyDelegationEPR( delegatEPR );
        } catch (Exception e ) {
            RequestRunner.logger.error( e );
        } finally{
            NDC.pop();
        }
    }


    public String getGorfxUri() {
        return gorfxUri;
    }


    public void setGorfxUri( final String gorfxUri ) {
        this.gorfxUri = gorfxUri;
    }


    public HashMap<String, String> getCtx() {
        return ctx;
    }


    public void setCtx( final HashMap<String, String> ctx ) {
        this.ctx = ctx;
        ctx.put( "Workflow.Id", uuid );
    }


    public TransientContract getCon() {
        return con;
    }


    public void setCon( final TransientContract con ) {
        this.con = con;
    }


    public AbstractORQ getOrq() {
        return orq;
    }


    public void setOrq( final AbstractORQ orq ) {
        this.orq = orq;
    }


    public String getUuid() {
        return uuid;
    }


    public Logger getLogger() {
        return logger;
    }


    public ORQConverter getConverter() {
        return converter;
    }


    public void setConverter( final ORQConverter converter ) {
        this.converter = converter;
    }


    public void setProxy( String proxy ) {
        this.proxy = proxy;
    }
}
