/*
*  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.carbon.mediator.bam.xml;

import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.synapse.config.xml.AbstractMediatorFactory;
import org.apache.synapse.Mediator;
import org.apache.synapse.SynapseConstants;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMAttribute;
import org.wso2.carbon.mediator.bam.BamMediator;
import org.wso2.carbon.mediator.bam.config.BamServerConfig;
import org.wso2.carbon.mediator.bam.config.BamServerConfigBuilder;
import org.wso2.carbon.mediator.bam.config.RegistryManager;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayInputStream;
import java.util.Properties;

public class BamMediatorFactory extends AbstractMediatorFactory {
    public static final QName BAM_Q = new QName(
            SynapseConstants.SYNAPSE_NAMESPACE, "bam");

    public Mediator createSpecificMediator(OMElement omElement, Properties properties) {
        BamMediator bam = new BamMediator();

        BamServerConfigBuilder bamServerConfigBuilder = new BamServerConfigBuilder();
        String resourceString;

        String serverProfilePath = this.getServerProfilePath(omElement);
        String streamName = this.getStreamName(omElement);
        String streamVersion = this.getStreamVersion(omElement);
        if(isNotNullOrEmpty(serverProfilePath) && isNotNullOrEmpty(streamName) && isNotNullOrEmpty(streamVersion)){
            bam.setServerProfile(serverProfilePath);
            bam.setStreamName(streamName);
            bam.setStreamVersion(streamVersion);
        }

        String realServerProfilePath = this.getRealBamServerProfilePath(serverProfilePath);
        RegistryManager registryManager = new RegistryManager();
        if(registryManager.resourceAlreadyExists(realServerProfilePath)){
            resourceString = registryManager.getResourceString(realServerProfilePath);
            try {
                OMElement resourceElement = new StAXOMBuilder(new ByteArrayInputStream(resourceString.getBytes())).getDocumentElement();
                boolean bamServerConfigCreated = bamServerConfigBuilder.createBamServerConfig(resourceElement);
                if(bamServerConfigCreated){
                    this.updateBamMediator(bamServerConfigBuilder, bam, streamName, streamVersion);
                }
            } catch (XMLStreamException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        return bam;
    }

    public QName getTagQName() {
        return BAM_Q;
    }

    private String getServerProfilePath(OMElement omElement){
        OMElement serverProfileElement = omElement.getFirstChildWithName(
                new QName(SynapseConstants.SYNAPSE_NAMESPACE, "serverProfile"));

        if(serverProfileElement != null){
            OMAttribute serverProfileAttr = serverProfileElement.getAttribute(new QName("path"));
            if(serverProfileAttr != null){
                return serverProfileAttr.getAttributeValue();
            }
            else {
                return null;
            }
        }
        return null;
    }
    
    private String getStreamName(OMElement omElement){
        OMElement streamConfigElement = omElement.getFirstChildWithName(
                new QName(SynapseConstants.SYNAPSE_NAMESPACE, "streamConfig"));
        
        if(streamConfigElement != null){
            OMAttribute streamNameAttr = streamConfigElement.getAttribute(new QName("name"));
            if(streamNameAttr != null){
                return streamNameAttr.getAttributeValue();
            }
            else{
                return null;
            }
        }
        return null;
    }

    private String getStreamVersion(OMElement omElement){
        OMElement streamConfigElement = omElement.getFirstChildWithName(
                new QName(SynapseConstants.SYNAPSE_NAMESPACE, "streamConfig"));

        if(streamConfigElement != null){
            OMAttribute streamVersionAttr = streamConfigElement.getAttribute(new QName("version"));
            if(streamVersionAttr != null){
                return streamVersionAttr.getAttributeValue();
            }
            else{
                return null;
            }
        }
        return null;
    }

    private String getRealBamServerProfilePath(String shortServerProfilePath){
        if(shortServerProfilePath != null){
            String registryType = shortServerProfilePath.split(":")[0];
            if (isNotNullOrEmpty(registryType) && registryType.equals("conf")){
                return shortServerProfilePath.split(":")[1];
            }
            return null;
        }
        return null;
    }

    private void updateBamMediator(BamServerConfigBuilder bamServerConfigBuilder, BamMediator bamMediator, String streamName, String streamVersion){
        BamServerConfig bamServerConfig=  bamServerConfigBuilder.getBamServerConfig();
        bamMediator.setStreamNickName(bamServerConfig.getAUniqueStreamConfiguration(streamName, streamVersion).getNickname());
        bamMediator.setStreamDescription(bamServerConfig.getAUniqueStreamConfiguration(streamName, streamVersion).getDescription());
        bamMediator.setUserName(bamServerConfig.getUsername());
        bamMediator.setPassword(bamServerConfig.getPassword());
        bamMediator.setServerIP(bamServerConfig.getIp());
        bamMediator.setServerPort(bamServerConfig.getPort());
        bamMediator.setProperties(bamServerConfig.getAUniqueStreamConfiguration(streamName, streamVersion).getProperties());
    }

    private boolean isNotNullOrEmpty(String string){
        if(string != null && !string.equals("")){
            return true;
        }
        return false;
    }
}
