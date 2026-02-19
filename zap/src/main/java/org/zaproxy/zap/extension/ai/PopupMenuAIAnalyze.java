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

import org.parosproxy.paros.core.scanner.Alert;
import org.zaproxy.zap.view.popup.PopupMenuItemSiteNodeContainer;

/**
 * Using PopupMenuItemSiteNodeContainer as a base since alerts are attached to nodes.
 * We will verify the alert presence in isEnableForComponent.
 */
public class PopupMenuAIAnalyze extends PopupMenuItemSiteNodeContainer {

    private static final long serialVersionUID = 1L;
    private final transient ExtensionAIAssistant extension;

    public PopupMenuAIAnalyze(ExtensionAIAssistant extension) {
        super("Analyze with AI", true);
        this.extension = extension;
    }

    @Override
    public boolean isSafe() {
        return true;
    }

    @Override
    public void performAction(org.parosproxy.paros.model.SiteNode node) {
        if (node != null && !node.getAlerts().isEmpty()) {
             Alert alert = node.getAlerts().get(0); // Just take the first one for this prototype
             extension.getAIPanel().setTabFocus();
             extension.getAIPanel().appendSystemMessage("Analyzing alert: " + alert.getName() + "...");

             extension.getAIClient().analyzeAlert(alert,
                response -> {
                    extension.getAIPanel().appendAIMessage(response);
                },
                completion -> {
                    // Done
                }
             );
        }
    }
}
