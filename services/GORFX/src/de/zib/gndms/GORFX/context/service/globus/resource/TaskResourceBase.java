package de.zib.gndms.GORFX.context.service.globus.resource;

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



import de.zib.gndms.GORFX.context.common.TaskConstants;
import de.zib.gndms.GORFX.context.stubs.TaskResourceProperties;
import gov.nih.nci.cagrid.advertisement.AdvertisementClient;
import gov.nih.nci.cagrid.advertisement.exceptions.UnregistrationException;
import gov.nih.nci.cagrid.introduce.servicetools.ReflectionResource;
import org.apache.axis.MessageContext;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.globus.mds.aggregator.types.AggregatorConfig;
import org.globus.mds.aggregator.types.AggregatorContent;
import org.globus.mds.aggregator.types.GetMultipleResourcePropertiesPollType;
import org.globus.mds.servicegroup.client.ServiceGroupRegistrationParameters;
import org.globus.wsrf.*;
import org.globus.wsrf.config.ContainerConfig;
import org.globus.wsrf.container.ServiceHost;
import org.globus.wsrf.encoding.DeserializationException;
import org.globus.wsrf.encoding.ObjectDeserializer;
import org.globus.wsrf.impl.ResourcePropertyTopic;
import org.globus.wsrf.impl.SimpleTopicList;
import org.globus.wsrf.impl.security.descriptor.ResourceSecurityDescriptor;
import org.globus.wsrf.impl.servicegroup.client.ServiceGroupRegistrationClient;
import org.globus.wsrf.security.SecureResource;
import org.globus.wsrf.utils.AddressingUtils;
import org.oasis.wsrf.lifetime.TerminationNotification;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.xml.namespace.QName;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;


/** 
 * DO NOT EDIT:  This class is autogenerated!
 *
 * This class is the base class of the resource type created for this service.
 * It contains accessor and utility methods for managing any resource properties
 * of these resource as well as code for registering any properties selected
 * to the index service.
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
public abstract class TaskResourceBase extends ReflectionResource implements Resource
                                                  ,TopicListAccessor
                                                  ,SecureResource
                                                  ,RemoveCallback
                                                  {

	static final Log logger = LogFactory.getLog(TaskResourceBase.class);

	private TaskResourceConfiguration configuration;
	private ResourceSecurityDescriptor desc;
	private ResourceKey key;

	// this can be used to cancel the registration renewal
    private AdvertisementClient registrationClient;
    
    private URL baseURL;
    private TopicList topicList;
    private boolean beingLoaded = false;
    
    public TaskResourceBase() {
    }


	/**
	 * @see org.globus.wsrf.jndi.Initializable#initialize()
	 */
	public void initialize(Object resourceBean,
                           QName resourceElementQName,
                           Object id) throws ResourceException {
                           
        // Call the super initialize on the ReflectionResource                  
	    super.initialize(resourceBean,resourceElementQName,id);
		this.desc = null;
		this.topicList = new SimpleTopicList(this);

        // create the topics for each resource property
        Iterator it = getResourcePropertySet().iterator();
        List newTopicProps = new ArrayList();
        while(it.hasNext()){
            ResourceProperty prop = (ResourceProperty)it.next();
            prop.getMetaData().getName();
            prop = new ResourcePropertyTopic(prop);
            this.topicList.addTopic((Topic)prop);
            newTopicProps.add(prop);
        }
        // replace the non topic properties with the topic properties
        Iterator newTopicIt = newTopicProps.iterator();
        while(newTopicIt.hasNext()){
            ResourceProperty prop = (ResourceProperty)newTopicIt.next();
            getResourcePropertySet().remove(prop.getMetaData().getName());
            getResourcePropertySet().add(prop);
        }
        


		// register the service to the index service
		refreshRegistration(true);
		
	}
	
	
	
	/**
	 * 
	 * @see org.globus.wsrf.ResourceLifetime#setTerminationTime(java.util.Calendar)
	 */
	public void setTerminationTime(Calendar time) {	
		Topic terminationTopic = ((Topic)getResourcePropertySet().get(TaskConstants.TERMINATIONTIME));
        if (terminationTopic != null) {
            TerminationNotification terminationNotification =
                new TerminationNotification();
            terminationNotification.setTerminationTime(time);
            try {
                terminationTopic.notify(terminationNotification);
            } catch(Exception e) {
                logger.error("Unable to send terminationTime notification", e);
            }
        }	
        
		super.setTerminationTime(time);
	}



	    //Getters/Setters for ResourceProperties
	
	
	public types.TaskExecutionState getTaskExecutionState(){
		return ((TaskResourceProperties) getResourceBean()).getTaskExecutionState();
	}
	
	public void setTaskExecutionState(types.TaskExecutionState taskExecutionState ) throws ResourceException {
        ResourceProperty prop = getResourcePropertySet().get(TaskConstants.TASKEXECUTIONSTATE);
		prop.set(0, taskExecutionState);
	}
	
	
	
	public types.TaskExecutionFailure getTaskExecutionFailure(){
		return ((TaskResourceProperties) getResourceBean()).getTaskExecutionFailure();
	}
	
	public void setTaskExecutionFailure(types.TaskExecutionFailure taskExecutionFailure ) throws ResourceException {
        ResourceProperty prop = getResourcePropertySet().get(TaskConstants.TASKEXECUTIONFAILURE);
		prop.set(0, taskExecutionFailure);
	}
	
	
	
	public types.DynamicOfferDataSeqT getTaskExecutionResults(){
		return ((TaskResourceProperties) getResourceBean()).getTaskExecutionResults();
	}
	
	public void setTaskExecutionResults(types.DynamicOfferDataSeqT taskExecutionResults ) throws ResourceException {
        ResourceProperty prop = getResourcePropertySet().get(TaskConstants.TASKEXECUTIONRESULTS);
		prop.set(0, taskExecutionResults);
	}
	


	
    /**
     * Sets the security descriptor for this resource.  The default resource
     * security will be null so it will fall back to method level then service
     * level security.  If you want to protect this particular instance of this
     * resource then provide a resource security descriptor to this resource
     * through this method.
     */
	public void setSecurityDescriptor(ResourceSecurityDescriptor desc) {
		this.desc = desc;
	}
	
	
	public ResourceSecurityDescriptor getSecurityDescriptor() {
		return this.desc;
	}  

	
	public TaskResourceConfiguration getConfiguration() {
		if (this.configuration != null) {
			return this.configuration;
		}
		MessageContext ctx = MessageContext.getCurrentContext();

		String servicePath = ctx.getTargetService();
		servicePath = servicePath.substring(0,servicePath.lastIndexOf("/"));
		servicePath+="/Task";

		String jndiName = Constants.JNDI_SERVICES_BASE_NAME + servicePath + "/configuration";
		logger.debug("Will read configuration from jndi name: " + jndiName);
		try {
			Context initialContext = new InitialContext();
			this.configuration = (TaskResourceConfiguration) initialContext.lookup(jndiName);
		} catch (Exception e) {
			logger.error("when performing JNDI lookup for " + jndiName + ": " + e, e);
		}

		return this.configuration;
	}


    /**
     * This checks the configuration file, and attempts to register to the
     * IndexService if shouldPerformRegistration==true. It will first read the
     * current container URL, and compare it against the saved value. If the
     * value exists, it will only try to reregister if the values are different.
     * This exists to handle fixing the registration URL which may be incorrect
     * during initialization, then later corrected during invocation. The
     * existence of baseURL does not imply successful registration (a non-null
     * registrationClient does). We will only attempt to reregister when the URL
     * changes (to prevent attempting registration with each invocation if there
     * is a configuration problem).
     */
    public void refreshRegistration(boolean forceRefresh) {
        if (getConfiguration().shouldPerformRegistration()) {

            // first check to see if there are any resource properties that
            // require registration
            ResourceContext ctx;
            try {
                MessageContext msgContext = MessageContext.getCurrentContext();
                if (msgContext == null) {
                    logger.error("Unable to determine message context!");
                    return;
                }

                ctx = ResourceContext.getResourceContext(msgContext);
            } catch (ResourceContextException e) {
                logger.error("Could not get ResourceContext: " + e, e);
                return;
            }
            EndpointReferenceType epr;
            try {
               String transportURL = (String) ctx.getProperty(org.apache.axis.MessageContext.TRANS_URL);
	           transportURL = transportURL.substring(0,transportURL.lastIndexOf('/') +1 );
	           transportURL += "Task";
			   epr = AddressingUtils.createEndpointReference(transportURL, getResourceKey());
            } catch (Exception e) {
                logger.error("Could not form EPR: " + e, e);
                return;
            }

            ServiceGroupRegistrationParameters params = null;

            File registrationFile = new File(ContainerConfig.getBaseDirectory() + File.separator
                + getConfiguration().getRegistrationTemplateFile());

            if (registrationFile.exists() && registrationFile.canRead()) {
                logger.debug("Loading registration argumentsrmation from:" + registrationFile);

                try {
                    params = ServiceGroupRegistrationClient.readParams(registrationFile.getAbsolutePath());
                } catch (Exception e) {
                    logger.error("Unable to read registration file:" + registrationFile, e);
                }

                // set our service's EPR as the registrant, or use the specified
                // value
                EndpointReferenceType registrantEpr = params.getRegistrantEPR();
                if (registrantEpr == null) {
                    params.setRegistrantEPR(epr);
                }

            } else {
                logger.error("Unable to read registration file:" + registrationFile);
            }

            if (params != null) {

                AggregatorContent content = (AggregatorContent) params.getContent();
                AggregatorConfig config = content.getAggregatorConfig();
                MessageElement[] elements = config.get_any();
                GetMultipleResourcePropertiesPollType pollType = null;
                try {
                    pollType = (GetMultipleResourcePropertiesPollType) ObjectDeserializer.toObject(elements[0],
                        GetMultipleResourcePropertiesPollType.class);
                } catch (DeserializationException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

 
                if (pollType != null) {
                    
                    // if there are properties names that need to be registered then
                    // register them to the index service
                    if (pollType.getResourcePropertyNames()!=null && pollType.getResourcePropertyNames().length != 0) {

                        URL currentContainerURL = null;
                        try {
                            currentContainerURL = ServiceHost.getBaseURL();
                        } catch (IOException e) {
                            logger.error("Unable to determine container's URL!  Skipping registration.", e);
                            return;
                        }

                        if (this.baseURL != null) {
                            // we've tried to register before (or we are being
                            // forced to
                            // retry)
                            // do a string comparison as we don't want to do DNS
                            // lookups
                            // for comparison
                            if (forceRefresh || !this.baseURL.equals(currentContainerURL)) {
                                // we've tried to register before, and we have a
                                // different
                                // URL now.. so cancel the old registration (if
                                // it
                                // exists),
                                // and try to redo it.
                                if (registrationClient != null) {
                                    try {
                                        this.registrationClient.unregister();
                                    } catch (UnregistrationException e) {
                                        logger
                                            .error("Problem unregistering existing registration:" + e.getMessage(), e);
                                    }
                                }

                                // save the new value
                                this.baseURL = currentContainerURL;
                                logger.info("Refreshing existing registration [container URL=" + this.baseURL + "].");
                            } else {
                                // URLs are the same (and we weren't forced), so
                                // don't
                                // try
                                // to reregister
                                return;
                            }

                        } else {
                            // we've never saved the baseURL (and therefore
                            // haven't
                            // tried to
                            // register)
                            this.baseURL = currentContainerURL;
                            logger.info("Attempting registration for the first time[container URL=" + this.baseURL
                                + "].");
                        }

                        try {
                            // perform the registration for this service
                            this.registrationClient = new AdvertisementClient(params);
                            this.registrationClient.register();

                        } catch (Exception e) {
                            logger.error("Exception when trying to register service (" + epr + "): " + e, e);
                        }
                    } else {
                        logger.info("No resource properties to register for service (" + epr + ")");
                    }
                } else {
                    logger.warn("Registration file deserialized with no poll type (" + epr + ")");
                }
            } else {
                logger.warn("Registration file deserialized with returned null SeviceGroupParams");
            }
        } else {
            logger.info("Skipping registration.");
        }
    }
    
    

	public ResourceKey getResourceKey(){
	    return this.key;
	}
	
	protected void setResourceKey(ResourceKey key){
	    this.key = key;
	}
	

    public TopicList getTopicList() {
        return this.topicList;
    }

    public void remove() throws ResourceException {
    }

	
	
}
