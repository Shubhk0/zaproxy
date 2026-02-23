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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.parosproxy.paros.extension.ExtensionAdaptor;
import org.parosproxy.paros.extension.ExtensionHook;
import org.zaproxy.zap.view.ZapMenuItem;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ExtensionAIAssistant extends ExtensionAdaptor {

    public static final String NAME = "ExtensionAIAssistant";
    private static final Logger LOGGER = LogManager.getLogger(ExtensionAIAssistant.class);

    private AIPanel aiPanel = null;
    private AIOptions aiOptions = null;
    private OptionsAIPanel optionsAIPanel = null;
    private ZapMenuItem menuAI = null;
    private AIClient aiClient = null;

    private PopupMenuAIAnalyze popupAIAnalyze = null;
    private PopupMenuAISuggest popupAISuggest = null;

    public ExtensionAIAssistant() {
        super(NAME);
        this.setOrder(69); // Place it somewhere appropriate
    }

    @Override
    public String getUIName() {
        return "AI Assistant";
    }

    @Override
    public String getAuthor() {
        return "ZAP AI Team";
    }

    @Override
    public void hook(ExtensionHook extensionHook) {
        super.hook(extensionHook);

        if (getView() != null) {
            extensionHook.getHookView().addStatusPanel(getAIPanel());
            extensionHook.getHookMenu().addToolsMenuItem(getMenuAI());
            extensionHook.getHookView().addOptionPanel(getOptionsAIPanel());

            extensionHook.getHookMenu().addPopupMenuItem(getPopupAIAnalyze());
            extensionHook.getHookMenu().addPopupMenuItem(getPopupAISuggest());
        }
        extensionHook.addOptionsParamSet(getAIOptions());

        // Initialize Client
        getAIClient();
    }

    public AIPanel getAIPanel() {
        if (aiPanel == null) {
            aiPanel = new AIPanel(this);
        }
        return aiPanel;
    }

    public AIOptions getAIOptions() {
        if (aiOptions == null) {
            aiOptions = new AIOptions();
        }
        return aiOptions;
    }

    private OptionsAIPanel getOptionsAIPanel() {
        if (optionsAIPanel == null) {
            optionsAIPanel = new OptionsAIPanel();
        }
        return optionsAIPanel;
    }

    private ZapMenuItem getMenuAI() {
        if (menuAI == null) {
            menuAI = new ZapMenuItem("ai.menu.tools.ai");
            menuAI.setText("AI Assistant");
            menuAI.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (aiPanel != null) {
                        aiPanel.setTabFocus();
                    }
                }
            });
        }
        return menuAI;
    }

    private PopupMenuAIAnalyze getPopupAIAnalyze() {
        if (popupAIAnalyze == null) {
            popupAIAnalyze = new PopupMenuAIAnalyze(this);
        }
        return popupAIAnalyze;
    }

    private PopupMenuAISuggest getPopupAISuggest() {
        if (popupAISuggest == null) {
            popupAISuggest = new PopupMenuAISuggest(this);
        }
        return popupAISuggest;
    }

    public AIClient getAIClient() {
        if (aiClient == null) {
            aiClient = new AIClient(this);
        }
        return aiClient;
    }

    @Override
    public boolean canUnload() {
        return true;
    }

    @Override
    public void unload() {
        if (getView() != null) {
            if (aiPanel != null) {
                // Remove from view if necessary
            }
        }
    }
}
