/*
 *  Copyright (c) 2005-2008, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package org.wso2.carbon.registry.indexing.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.wso2.carbon.CarbonConstants;
import org.wso2.carbon.registry.common.AttributeSearchService;
import org.wso2.carbon.registry.common.ResourceData;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.registry.core.service.RegistryService;
import org.wso2.carbon.registry.core.session.UserRegistry;
import org.wso2.carbon.registry.indexing.IndexingManager;
import org.wso2.carbon.registry.indexing.Utils;
import org.wso2.carbon.registry.indexing.indexer.IndexerException;
import org.wso2.carbon.registry.indexing.service.ContentBasedSearchService;
import org.wso2.carbon.registry.indexing.service.ContentSearchService;
import org.wso2.carbon.registry.indexing.service.SearchResultsBean;
import org.wso2.carbon.utils.WaitBeforeShutdownObserver;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

import java.util.Map;
import java.util.Stack;

/**
 * @scr.component name="org.wso2.carbon.registry.indexing" immediate="true"
 * @scr.reference name="registry.service"
 * interface="org.wso2.carbon.registry.core.service.RegistryService" cardinality="1..1"
 * policy="dynamic" bind="setRegistryService" unbind="unsetRegistryService"
 */
public class IndexingServiceComponent {

    /**
     * This class is the bridge between Carbon and Indexing code
     */

    private static Log log = LogFactory.getLog(IndexingServiceComponent.class);

    private static Stack<ServiceRegistration> registrations = new Stack<ServiceRegistration>();

    protected void activate(ComponentContext context) {
        registrations.push(context.getBundleContext().registerService(
                ContentSearchService.class.getName(), new ContentSearchServiceImpl(), null));
        registrations.push(context.getBundleContext().registerService(
                AttributeSearchService.class.getName(), new AttributeSearchServiceImpl(), null));
        registrations.push(context.getBundleContext().registerService(
                WaitBeforeShutdownObserver.class.getName(), new WaitBeforeShutdownObserver() {
            boolean status = false;
            public void startingShutdown() {
                try {
                    IndexingManager.getInstance().stopIndexing();
                } finally {
                   status = true;
                }
            }

            public boolean isTaskComplete() {
                return status;
            }
        }, null));
        log.debug("Registry Indexing bundle is activated");
    }

    protected void deactivate(ComponentContext context) {
        while (!registrations.empty()) {
            registrations.pop().unregister();
        }
        log.debug("Registry Indexing bundle is deactivated");
    }

    protected void setRegistryService(RegistryService registryService) {
        Utils.setRegistryService(registryService);
        startIndexing();
    }

    protected void unsetRegistryService(RegistryService registryService) {
        stopIndexing();
        Utils.setRegistryService(null);
    }

    private void startIndexing() {
        IndexingManager.getInstance().startIndexing();
    }

    private void stopIndexing() {
        IndexingManager.getInstance().stopIndexing();
    }

    private static class ContentSearchServiceImpl implements ContentSearchService {

        public ResourceData[] search(UserRegistry registry, String query)
                throws RegistryException {
            SearchResultsBean resultsBean;
            try {
                resultsBean = new ContentBasedSearchService().searchContent(query, registry);
            } catch (IndexerException e) {
                throw new RegistryException("Unable to obtain an instance of a Solr client", e);
            }
            String errorMessage = resultsBean.getErrorMessage();
            if (errorMessage != null) {
                throw new RegistryException(errorMessage);
            }
            return resultsBean.getResourceDataList();
        }

        public ResourceData[] search(int tenantId, String query)
                throws RegistryException {
            return search(Utils.getRegistryService().getRegistry(
                    CarbonConstants.REGISTRY_SYSTEM_USERNAME, tenantId), query);
        }

        public ResourceData[] search(String query) throws RegistryException {
            return search(MultitenantConstants.SUPER_TENANT_ID, query);
        }
    }
    private static class AttributeSearchServiceImpl implements AttributeSearchService {

        public ResourceData[] search(UserRegistry registry, Map<String, String> query)
                throws RegistryException {
            SearchResultsBean resultsBean;
            try {
                resultsBean = new ContentBasedSearchService().searchByAttribute(query, registry);
            } catch (IndexerException e) {
                throw new RegistryException("Unable to obtain an instance of a Solr client", e);
            }
            String errorMessage = resultsBean.getErrorMessage();
            if (errorMessage != null) {
                throw new RegistryException(errorMessage);
            }
            return resultsBean.getResourceDataList();
        }

        public ResourceData[] search(int tenantId, Map<String, String> query)
                throws RegistryException {
            return search(Utils.getRegistryService().getRegistry(
                    CarbonConstants.REGISTRY_SYSTEM_USERNAME, tenantId), query);
        }

        public ResourceData[] search(Map<String, String> query) throws RegistryException {
            return search(MultitenantConstants.SUPER_TENANT_ID, query);
        }
    }
}

