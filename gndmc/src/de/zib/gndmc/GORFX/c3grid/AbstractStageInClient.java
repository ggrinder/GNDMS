package de.zib.gndmc.GORFX.c3grid;

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
import de.zib.gndmc.GORFX.constants.TaskConstants;
import de.zib.gndms.GORFX.offer.client.OfferClient;
import de.zib.gndms.dspace.slice.client.SliceClient;
import de.zib.gndms.gritserv.delegation.DelegationAux;
import de.zib.gndms.gritserv.typecon.ContractStdoutWriter;
import de.zib.gndms.gritserv.typecon.GORFXTools;
import de.zib.gndms.gritserv.typecon.types.ContractXSDReader;
import de.zib.gndms.gritserv.typecon.types.ContractXSDTypeWriter;
import de.zib.gndms.kit.application.AbstractApplication;
import de.zib.gndms.model.common.types.TransientContract;
import de.zib.gndms.model.gorfx.types.io.ContractConverter;
import de.zib.gndms.model.gorfx.types.io.ContractPropertyReader;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI;
import org.globus.gsi.GlobusCredential;
import org.globus.wsrf.NoSuchResourceException;
import org.globus.wsrf.encoding.DeserializationException;
import org.globus.wsrf.encoding.ObjectDeserializer;
import org.joda.time.DateTime;
import org.kohsuke.args4j.Option;
import org.oasis.wsrf.properties.GetResourcePropertyResponse;
import org.oasis.wsrf.properties.ResourceUnknownFaultType;
import types.*;

import javax.xml.namespace.QName;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Properties;


/**
 * ThingAMagic.
 *
 * @author  try ste fan pla nti kow zib
 * @version $Id$
 *
 *          User: stepn Date: 17.12.2008 Time: 14:00:59
 */
@SuppressWarnings({ "UseOfSystemOutOrSystemErr", "MethodMayBeStatic" })
public abstract class AbstractStageInClient extends AbstractApplication {

	protected static final QName fileNameQName = new QName("http://gndms.zib.de/c3grid/types", "DataFile");
	protected static final QName metaFileNameQName = new QName("http://gndms.zib.de/c3grid/types", "MetadataFile");

	protected static final long MILLIS = 2000L;

	protected static final long DAY_LONG = 24L*60L*60L*1000L;

	// commandline args

	@Option( name="-uri", required=true, usage="URL of GORFX-Endpoint", metaVar="URI" )
	protected String gorfxEpUrl;
	@Option( name="-props", required=true, usage="staging.properties" )
	protected String sfrPropFile;
	@Option( name="-con-props", usage="contract.properties" )
	protected String conPropFile;
	@Option( name="-dn", required=true, usage="DN" )
	protected String dn;
    @Option( name="-proxyfile", usage="grid-proxy-file to lead", metaVar="proxy-file" )
    protected String proxyFile = null;
    @Option( name="-cancel", required = false, usage = "ms to wait before destroying taskClient.")
    protected Long cancel = null;


	protected TransientContract contract;
	protected String dataFile;
	protected String metaDataFile;
    protected GlobusCredential credential;

	protected EndpointReferenceType createOffer(
	        final ContextT xsdContextParam, final EndpointReferenceType orqEPRParam)
	        throws URI.MalformedURIException, RemoteException {
	    final ORQClient orqPort = new ORQClient(orqEPRParam);

	    OfferExecutionContractT xsdOfferContract;

	    if( contract == null )
	        xsdOfferContract = createContract();
	    else
	        xsdOfferContract = ContractXSDTypeWriter.write( contract );

        orqPort.setProxy( credential );
	    return orqPort.getOfferAndDestroyRequest(xsdOfferContract, xsdContextParam);
	}

	protected EndpointReferenceType acceptOfferAndCreateTask(final EndpointReferenceType offerEprParam)
		  throws URI.MalformedURIException, RemoteException, DeserializationException {
        final OfferClient offerClient = new OfferClient(offerEprParam);

		final GetResourcePropertyResponse rpResponse= offerClient.getResourceProperty(
				de.zib.gndmc.GORFX.constants.OfferConstants.OFFEREXECUTIONCONTRACT);
		final OfferExecutionContractT contractT = (OfferExecutionContractT) ObjectDeserializer.toObject( rpResponse.get_any()[0], OfferExecutionContractT.class );
		final TransientContract readContract = ContractXSDReader.readContract(contractT);

		final EndpointReferenceType taskEpr = offerClient.accept();

		System.out.println("# Accepted contract");
		ContractConverter contractConv = new ContractConverter(new ContractStdoutWriter(), readContract);
		contractConv.convert();

        return taskEpr;
    }


	protected static OfferExecutionContractT createContract ( ) {

        final OfferExecutionContractT xsdOfferContract = new OfferExecutionContractT();

        xsdOfferContract.setIfDecisionBefore( new DateTime().plusHours(1).toGregorianCalendar() );

        final FutureTimeT execLikelyUntil = new FutureTimeT();

        //long day = 5*1000;
        execLikelyUntil.setOffset( DAY_LONG );
        xsdOfferContract.setExecutionLikelyUntil( execLikelyUntil );

        final FutureTimeT resultValidity = new FutureTimeT();
        resultValidity.setOffset( 2L*DAY_LONG );
        xsdOfferContract.setResultValidUntil( resultValidity );

        return xsdOfferContract;
    }


	@SuppressWarnings({ "FeatureEnvy", "OverlyLongMethod" })
    protected void waitForTaskToFinishOrFail(final EndpointReferenceType taskClientEpr)
	      throws RemoteException, DeserializationException, URI.MalformedURIException {
        TaskExecutionState state = null;
        boolean finished = false;
        boolean failed = false ;
		TaskClient taskClient = null;

        int cnt=0;

        do {
	        try {
		        if (taskClient == null)
		            taskClient = new TaskClient(taskClientEpr);
				final GetResourcePropertyResponse rpResponse= taskClient.getResourceProperty(
						TaskConstants.TASKEXECUTIONSTATE);
				state = (TaskExecutionState) ObjectDeserializer.toObject( rpResponse.get_any()[0], TaskExecutionState.class );
				failed = TaskStatusT.failed.equals(state.getStatus());
				finished = TaskStatusT.finished.equals(state.getStatus());
                if( cancel != null &&  cancel < cnt * MILLIS ) {
                    taskClient.destroy( );
                    taskClient = null;
                    break;
                }
                ++cnt;
	        }
	        catch (NoSuchResourceException nre) {
		        failed = true;
	        }
            catch ( ResourceUnknownFaultType rfe ) {
                failed = true;
            }
	        catch (Exception re) {
		        re.printStackTrace(System.err);
		        taskClient = null;
	        }

	        final String outputStr = makeOutputStr(state);
	        System.err.println(outputStr);
            try {
	            if (! (failed || finished))
                    Thread.sleep(MILLIS);
            }
            catch (InterruptedException e) {
                // intentionally
            }
        }  while (! (failed || finished));

        if( taskClient == null ) {
            System.out.println( "client destroyed" );
            System.exit( 1 );
        }

        // Write results to console
        if (finished)
	        handleFinish(taskClient);
        else
	        handleFailed(taskClient);
    }


	protected static void handleFailed(final TaskClient taskClientParam)
		  throws RemoteException, DeserializationException {
		final GetResourcePropertyResponse rpResponse= taskClientParam.getResourceProperty(
			  TaskConstants.TASKEXECUTIONFAILURE);
		final TaskExecutionFailure fail = (TaskExecutionFailure) ObjectDeserializer.toObject(rpResponse.get_any()[0], TaskExecutionFailure.class);
		TaskExecutionFailureImplementationFault tefif = fail.getImplementationFault();
		System.out.println( "message:       " + tefif.getMessage( ) );
		System.out.println( "faultClass:    " + tefif.getFaultClass( ) );
		System.out.println( "faultTrace:    " + tefif.getFaultTrace( ) );
		System.out.println( "faultLocation: " + tefif.getFaultLocation( ) );
		throw new RuntimeException(fail.toString());
	}


	@SuppressWarnings({ "HardcodedFileSeparator" })
	protected void handleFinish(final TaskClient taskClientParam)
		  throws RemoteException, DeserializationException, URI.MalformedURIException {
		final GetResourcePropertyResponse rpResponse= taskClientParam.getResourceProperty(
			  TaskConstants.TASKEXECUTIONRESULTS);
		final ProviderStageInResultT result = (ProviderStageInResultT) ObjectDeserializer.toObject( rpResponse.get_any()[0], ProviderStageInResultT.class);
		System.out.println(result);
		SliceReference sr =  ( SliceReference ) ObjectDeserializer.toObject( result.get_any()[0], SliceReference.class ) ;
		if( sr != null ) {
		    EndpointReferenceType epr = sr.getEndpointReference();
		    System.out.println( epr );
		    SliceClient sliceClient = new SliceClient(epr);
			System.out.println("Collect your results at:");
			final String sliceLoc = sliceClient.getSliceLocation();
			System.out.println(sliceLoc);
			System.out.println("");
			System.out.println("Collect your results by executing:");
			String home = System.getProperty("user.home", "/tmp");
			System.out.println("globus-url-copy '" + sliceLoc + '/' + dataFile + "' 'file://" + home + '/' + dataFile + '\'');
			System.out.println("globus-url-copy '" + sliceLoc + '/' + metaDataFile + "' 'file://" + home + '/' +  metaDataFile + '\'');
			System.out.println("");
		}
	}


	@SuppressWarnings({ "MethodWithMoreThanThreeNegations", "HardcodedFileSeparator" })
	protected static String makeOutputStr(final TaskExecutionState stateParam) {
		String stateStr = "(null)";
		int maxProgress = 100;
		int progress = 0;
		if (stateParam != null) {
			final TaskStatusT statusT = stateParam.getStatus();
			if (stateParam.getMaxProgress() != null)
			    maxProgress = Integer.parseInt(stateParam.getMaxProgress().toString());
			if (stateParam.getProgress() != null)
			    progress = Integer.parseInt(stateParam.getProgress().toString());
			if (statusT != null)
				stateStr = statusT.toString();
		}
		return "Waiting for staging to finish or fail... (state=" + stateStr +
			  ", progress=[" + Integer.toString(progress) + '/' + Integer.toString(maxProgress) + "])";
	}


	protected void extractFileNames(final MessageElement[] elems) throws Exception {
		for (MessageElement elem : elems) {
			if (elem != null) {
				final QName qName = elem.getQName();
					if (fileNameQName.equals(qName))
						dataFile = elem.getObjectValue().toString();
					else if (metaFileNameQName.equals(qName))
						metaDataFile = elem.getObjectValue().toString();
			}
		}
	}


	protected void loadAndPrintContract() throws IOException {
		if( conPropFile !=  null ) {
	        contract = ContractPropertyReader.readFromFile( conPropFile );

				// Print initial contract
				System.out.println("# Requested contract");
				ContractConverter contractConv = new ContractConverter(new ContractStdoutWriter(), contract);
				contractConv.convert();
		}
	}


	protected Properties loadSFRProps(final String sfrPropFileParam) throws IOException {// Load SFR property file
		File propFile = new File(sfrPropFileParam);
		Properties sfrProps = new Properties();
		final FileInputStream stream = new FileInputStream(propFile);
		try {
			sfrProps.load(stream);
		}
		finally {
			stream.close();
		}
		return sfrProps;
	}


	@Override
    public void run() throws Exception {

	    // Create reusable context with pseudo DN
	    final ContextT xsdContext = new ContextT();
	    final ContextTEntry entry =
	        GORFXTools.createContextEntry("Auth.DN", dn);
	    xsdContext.setEntry(new ContextTEntry[] { entry });

		final DynamicOfferDataSeqT xsdArgs = loadSFR();

		// Extract file names for later output generation
	    extractFileNames(xsdArgs.get_any());

		// Print SFR
		printSFR();

		loadAndPrintContract();

		// Open GORFX and create ORQ
	    final EndpointReferenceType orqEPR = createORQ(gorfxEpUrl, xsdContext, xsdArgs);

	    // Negotiate with ORQ for Offer
	    final EndpointReferenceType offerEpr = createOffer(xsdContext, orqEPR);

	    // Accept offer and thus create Task
	    final EndpointReferenceType taskClientEpr = acceptOfferAndCreateTask(offerEpr);

	    waitForTaskToFinishOrFail(taskClientEpr);
	}


	protected abstract DynamicOfferDataSeqT loadSFR() throws IOException;

	protected abstract void printSFR() throws IOException;


	protected EndpointReferenceType createORQ(
            final String gorfxEpUrlParam, final ContextT xsdContextParam,
            final DynamicOfferDataSeqT xsdArgsParam)
		  throws Exception, RemoteException {

        GORFXClient gorfx = new GORFXClient(gorfxEpUrlParam);

        String delfac = DelegationAux.createDelegationAddress( gorfxEpUrlParam );

        credential = DelegationAux.findCredential( proxyFile );
        EndpointReferenceType epr = DelegationAux.createProxy( delfac, credential );

        DelegationAux.addDelegationEPR( xsdContextParam, epr );
        gorfx.setProxy( credential );

        // Create ORQ via GORFX
        return gorfx.createOfferRequest(xsdArgsParam, xsdContextParam);
    }
}
