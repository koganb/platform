/*
 * Copyright (c) 2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package jaggery.integration.tests.emptyjaggeryobject;

import jaggery.integration.tests.util.Utility;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.test.ASIntegrationTest;

import java.io.BufferedReader;
import java.net.URL;
import java.net.URLConnection;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

/**
 * This class sends requests to test123.jag which does not exist in the jaggery web application
 */
public class EmptyJaggeryObjectTestCase extends ASIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
    }

    @Test(groups = "wso2.as", description = "invoke test123.jag which does not exist")
    public void testApplication() throws Exception {

        URL jaggeryURL = new URL(asServer.getWebAppURL() + "/testapp/test123.jag");
        URLConnection jaggeryServerConnection = Utility.openConnection(jaggeryURL);
        assertNotNull(jaggeryServerConnection, "Connection establishment failure");

        BufferedReader in = Utility.inputReader(jaggeryServerConnection);
        assertNull(in, "Input stream failure");
    }
}
