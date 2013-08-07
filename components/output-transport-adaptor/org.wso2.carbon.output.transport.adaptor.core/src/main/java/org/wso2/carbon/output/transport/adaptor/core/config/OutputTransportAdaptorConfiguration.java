/*
 * Copyright 2004,2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.output.transport.adaptor.core.config;


import java.util.Map;

/**
 * This class contain the configuration details of the transport
 */

public class OutputTransportAdaptorConfiguration {

    private String name;

    private String type;

    private boolean enableTracing;

    private boolean enableStatistics;

    private InternalOutputTransportAdaptorConfiguration internalOutputTransportAdaptorConfiguration = null;

    public InternalOutputTransportAdaptorConfiguration getOutputConfiguration() {
        return internalOutputTransportAdaptorConfiguration;
    }

    public void setOutputConfiguration(
            InternalOutputTransportAdaptorConfiguration internalOutputTransportAdaptorConfiguration) {
        this.internalOutputTransportAdaptorConfiguration = internalOutputTransportAdaptorConfiguration;
    }


    public Map<String, String> getOutputProperties() {
        if (internalOutputTransportAdaptorConfiguration != null) {
            return internalOutputTransportAdaptorConfiguration.getProperties();
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isEnableTracing() {
        return enableTracing;
    }

    public void setEnableTracing(boolean enableTracing) {
        this.enableTracing = enableTracing;
    }

    public boolean isEnableStatistics() {
        return enableStatistics;
    }

    public void setEnableStatistics(boolean enableStatistics) {
        this.enableStatistics = enableStatistics;
    }
}