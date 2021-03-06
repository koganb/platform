<!--
 ~ Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 ~
 ~ WSO2 Inc. licenses this file to you under the Apache License,
 ~ Version 2.0 (the "License"); you may not use this file except
 ~ in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~    http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing,
 ~ software distributed under the License is distributed on an
 ~ "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 ~ KIND, either express or implied.  See the License for the
 ~ specific language governing permissions and limitations
 ~ under the License.
 -->
<%@ page import="org.apache.axis2.context.ConfigurationContext" %>
<%@ page import="org.wso2.carbon.CarbonConstants" %>
<%@ page import="org.wso2.carbon.module.mgt.ui.client.ModuleManagementClient" %>
<%@ page import="org.wso2.carbon.module.mgt.ui.util.ModuleManagementUtils" %>
<%@ page import="org.wso2.carbon.ui.CarbonUIUtil" %>
<%@ page import="org.wso2.carbon.utils.ServerConstants" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="org.wso2.carbon.module.mgt.stub.ModuleAdminServiceModuleMgtExceptionException" %>

<%
    String moduleName = request.getParameter("moduleName");
    String moduleVersion = request.getParameter("moduleVersion");

    String backendServerURL = CarbonUIUtil.getServerURL(config.getServletContext(), session);
    ConfigurationContext configContext =
            (ConfigurationContext) config.getServletContext().getAttribute(CarbonConstants.CONFIGURATION_CONTEXT);
    String cookie = (String) session.getAttribute(ServerConstants.ADMIN_SERVICE_COOKIE);

    Map<String, String[]> map = request.getParameterMap();
    List<String> params = new ArrayList<String>();
    for (String key : map.keySet()) {
        String paramValue = map.get(key)[0].trim();
        if (paramValue.startsWith("<parameter")) {
            params.add(paramValue);
        }
    }
    try {
        ModuleManagementClient client = new ModuleManagementClient(configContext, backendServerURL, cookie, false);
        client.setModuleParameters(moduleName,moduleVersion,params);
%>

<script type="text/javascript">
    location.href = 'parameters.jsp?moduleName=<%=moduleName%>&moduleVersion=<%=moduleVersion%>'
</script>
<%
    } catch (ModuleAdminServiceModuleMgtExceptionException e) {
        String url = "parameters.jsp?moduleName="+moduleName+"&moduleVersion="+moduleVersion;
        ModuleManagementUtils.handleModuleMgtErrors(e, request, response, url);
        return;
    }
%>