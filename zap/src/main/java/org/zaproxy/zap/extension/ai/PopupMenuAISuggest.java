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

import org.parosproxy.paros.model.SiteNode;
import org.zaproxy.zap.view.popup.PopupMenuItemSiteNodeContainer;

public class PopupMenuAISuggest extends PopupMenuItemSiteNodeContainer {

    private static final long serialVersionUID = 1L;
    private final transient ExtensionAIAssistant extension;

    public PopupMenuAISuggest(ExtensionAIAssistant extension) {
        super("Get Hunting Suggestions", true);
        this.extension = extension;
    }

    @Override
    public void performAction(SiteNode node) {
        extension.getAIPanel().setTabFocus();
        String context = "Node: " + node.getNodeName();
        // In a real scenario, we would extract more details like parameters, children, etc.

        extension.getAIPanel().appendSystemMessage("Generating suggestions for: " + node.getNodeName() + "...");

        extension.getAIClient().generateBugHuntingSuggestions(context,
            response -> {
                extension.getAIPanel().appendAIMessage(response);
            },
            completion -> {
                // Done
            }
        );
    }

    @Override
    public boolean isSafe() {
        return true;
    }
}
