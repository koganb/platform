<%@ page import="org.wso2.carbon.output.transport.adaptor.manager.stub.OutputTransportAdaptorManagerAdminServiceStub" %>
<%@ page
        import="org.wso2.carbon.output.transport.adaptor.manager.stub.types.OutputTransportAdaptorFileDto" %>
<%@ page
        import="org.wso2.carbon.output.transport.adaptor.manager.ui.OutputTransportAdaptorUIUtils" %>
<%@ taglib uri="http://wso2.org/projects/carbon/taglibs/carbontags.jar" prefix="carbon" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<fmt:bundle basename="org.wso2.carbon.output.transport.adaptor.manager.ui.i18n.Resources">

    <carbon:breadcrumb
            label="transportmanager.details"
            resourceBundle="org.wso2.carbon.output.transport.adaptor.manager.ui.i18n.Resources"
            topPage="false"
            request="<%=request%>"/>

    <script type="text/javascript" src="../admin/js/breadcrumbs.js"></script>
    <script type="text/javascript" src="../admin/js/cookies.js"></script>
    <script type="text/javascript" src="../admin/js/main.js"></script>

    <script type="text/javascript">
        function doDelete(filePath) {
            var theform = document.getElementById('deleteForm');
            theform.filePath.value = filePath;
            theform.submit();
        }
    </script>
    <%
        String filePath = request.getParameter("filePath");
        if (filePath != null) {
            OutputTransportAdaptorManagerAdminServiceStub stub = OutputTransportAdaptorUIUtils.getOutputTransportManagerAdminService(config, session, request);
            stub.removeOutputTransportAdaptorConfigurationFile(filePath);
    %>
    <script type="text/javascript">CARBON.showInfoDialog('Transport File successfully deleted.');</script>
    <%
        }
    %>


    <div id="middle">
        <div id="workArea">
            <h3><fmt:message key="notdeployed.output.transport.adaptors"/></h3>
            <table class="styledLeft">

                <%
                    OutputTransportAdaptorManagerAdminServiceStub stub = OutputTransportAdaptorUIUtils.getOutputTransportManagerAdminService(config, session, request);
                    OutputTransportAdaptorFileDto[] transportDetailsArray = stub.getNotDeployedOutputTransportAdaptorConfigurationFiles();
                    if (transportDetailsArray != null) {
                %>
                <thead>
                <tr>
                    <th><fmt:message key="file.name"/></th>
                    <th><fmt:message key="transport.adaptor.name"/></th>
                    <th><fmt:message key="actions"/></th>
                </tr>
                </thead>
                <%
                    for (OutputTransportAdaptorFileDto outputTransportAdaptorFile : transportDetailsArray) {

                %>

                <tbody>
                <tr>
                    <td>
                        <%=outputTransportAdaptorFile.getFilePath().substring(outputTransportAdaptorFile.getFilePath().lastIndexOf('/') + 1, outputTransportAdaptorFile.getFilePath().length())%>
                    </td>
                    <td><%=outputTransportAdaptorFile.getTransportAdaptorName()%>
                    </td>
                    <td>
                        <a style="background-image: url(../admin/images/delete.gif);"
                           class="icon-link"
                           onclick="doDelete('<%=outputTransportAdaptorFile.getFilePath()%>')"><font
                                color="#4682b4">Delete</font></a>
                        <a style="background-image: url(../admin/images/edit.gif);"
                           class="icon-link"
                           href="edit_transport_details.jsp?transportPath=<%=outputTransportAdaptorFile.getFilePath()%>"><font
                                color="#4682b4">Source View</font></a>
                    </td>

                </tr>
                <%
                        }
                    }
                %>
                </tbody>
            </table>

            <div>
                <form id="deleteForm" name="input" action="" method="get"><input type="HIDDEN"
                                                                                 name="filePath"
                                                                                 value=""/></form>
            </div>
        </div>


        <script type="text/javascript">
            alternateTableRows('expiredsubscriptions', 'tableEvenRow', 'tableOddRow');
            alternateTableRows('validsubscriptions', 'tableEvenRow', 'tableOddRow');
        </script>
</fmt:bundle>