/*
 * Zed Attack Proxy (ZAP) and its related class files.
 *
 * ZAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2024 The ZAP Development Team
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
package org.zaproxy.zap.extension.ai;

import org.junit.jupiter.api.Test;
import org.parosproxy.paros.core.scanner.Alert;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

/**
 * Unit tests for AIClient logic.
 */
public class AIClientTest {

    @Test
    public void testBuildAnalyzeAlertPrompt() {
        // Given
        AIClient client = new AIClient(null);
        Alert alert = new Alert(1, Alert.RISK_HIGH, Alert.CONFIDENCE_MEDIUM, "SQL Injection");
        alert.setDescription("A SQL injection vulnerability was found.");
        alert.setUri("http://example.com/vuln");
        alert.setParam("id");
        alert.setEvidence("SELECT * FROM users");

        // When
        String prompt = client.buildAnalyzeAlertPrompt(alert);

        // Then
        assertThat(prompt, containsString("Name: SQL Injection"));
        assertThat(prompt, containsString("Risk: High"));
        assertThat(prompt, containsString("Description: A SQL injection vulnerability was found."));
        assertThat(prompt, containsString("URL: http://example.com/vuln"));
        assertThat(prompt, containsString("Parameter: id"));
        assertThat(prompt, containsString("Evidence: SELECT * FROM users"));
    }
}
