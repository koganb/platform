/*
*  Copyright (c) 2012, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.status.monitor.ui.clients;

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.CarbonConstants;
import org.wso2.carbon.registry.core.exceptions.RegistryException;

import org.wso2.carbon.status.monitor.stub.HealthMonitorServiceStub;
import org.wso2.carbon.status.monitor.stub.beans.xsd.ServiceStateDetailInfoBean;
import org.wso2.carbon.status.monitor.stub.beans.xsd.ServiceStateInfoBean;

import org.wso2.carbon.ui.CarbonUIUtil;
import org.wso2.carbon.utils.ServerConstants;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpSession;

/**
 * HealthMonitorService Client of status.monitor.ui
 */
public class HealthMonitorServiceClient {
    private static final Log log = LogFactory.getLog(
            org.wso2.carbon.status.monitor.ui.clients.HealthMonitorServiceClient.class);

    private HealthMonitorServiceStub stub;

    public HealthMonitorServiceClient(String cookie, String backendServerURL,
                                      ConfigurationContext configContext) throws RegistryException {

        String epr = backendServerURL + "HealthMonitorService";

        try {
            stub = new HealthMonitorServiceStub(configContext, epr);

            ServiceClient client = stub._getServiceClient();
            Options option = client.getOptions();
            option.setManageSession(true);
            option.setProperty(org.apache.axis2.transport.http.HTTPConstants.COOKIE_STRING, cookie);

        } catch (AxisFault axisFault) {
            String msg = "Failed to initiate HealthMonitor service client. " + axisFault.getMessage();
            log.error(msg, axisFault);
            throw new RegistryException(msg, axisFault);
        }
    }

    public HealthMonitorServiceClient(ServletConfig config, HttpSession session)
            throws RegistryException {

        String cookie = (String)session.getAttribute(ServerConstants.ADMIN_SERVICE_COOKIE);
        String backendServerURL = CarbonUIUtil.getServerURL(config.getServletContext(), session);
        ConfigurationContext configContext = (ConfigurationContext) config.
                getServletContext().getAttribute(CarbonConstants.CONFIGURATION_CONTEXT);
        String epr = backendServerURL + "HealthMonitorService";

        try {
            stub = new HealthMonitorServiceStub(configContext, epr);

            ServiceClient client = stub._getServiceClient();
            Options option = client.getOptions();
            option.setManageSession(true);
            option.setProperty(org.apache.axis2.transport.http.HTTPConstants.COOKIE_STRING, cookie);

        } catch (AxisFault axisFault) {
            String msg = "Failed to initiate HealthMonitor Services service client. " +
                    axisFault.getMessage();
            log.error(msg, axisFault);
            throw new RegistryException(msg, axisFault);
        }
    }

    public ServiceStateInfoBean[] retrieveStatuses() throws Exception {
        return stub.getAllServiceStatus();
    }

    public ServiceStateInfoBean getServiceStatus(String serviceName) throws Exception {
        return stub.getServiceStatus(serviceName);
    }

    public ServiceStateDetailInfoBean[] retrieveStateDetails() throws Exception {
        return stub.getAllServiceStateDetail();
    }
}
