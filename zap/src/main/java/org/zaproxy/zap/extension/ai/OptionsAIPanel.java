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

import org.parosproxy.paros.model.OptionsParam;
import org.parosproxy.paros.view.AbstractParamPanel;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class OptionsAIPanel extends AbstractParamPanel {

    private static final long serialVersionUID = 1L;
    private JTextField ollamaUrlField;
    private JTextField modelNameField;
    private JCheckBox darkModeCheckbox;
    private JCheckBox webSearchCheckbox;

    public OptionsAIPanel() {
        super();
        setName("AI Assistant");
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Ollama URL
        add(new JLabel("Ollama URL:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        ollamaUrlField = new JTextField(30);
        add(ollamaUrlField, gbc);

        // Model Name
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        add(new JLabel("Model Name:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        modelNameField = new JTextField(20);
        add(modelNameField, gbc);

        // Options Checkboxes
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;

        webSearchCheckbox = new JCheckBox("Enable Web Search (Context Enrichment)");
        webSearchCheckbox.setToolTipText("If enabled, AI will search the web for recent CVEs and exploits when analyzing alerts.");
        add(webSearchCheckbox, gbc);

        gbc.gridy++;
        darkModeCheckbox = new JCheckBox("Enable Dark Mode (Requires Restart)");
        add(darkModeCheckbox, gbc);

        // Spacer
        gbc.gridy++;
        gbc.weighty = 1.0;
        add(new JLabel(""), gbc);
    }

    @Override
    public void initParam(Object obj) {
        OptionsParam options = (OptionsParam) obj;
        AIOptions aiOptions = options.getParamSet(AIOptions.class);

        if (aiOptions != null) {
            ollamaUrlField.setText(aiOptions.getOllamaUrl());
            modelNameField.setText(aiOptions.getModelName());
            darkModeCheckbox.setSelected(aiOptions.isDarkMode());
            webSearchCheckbox.setSelected(aiOptions.isWebSearchEnabled());
        }
    }

    @Override
    public void validateParam(Object obj) throws Exception {
        // No specific validation for now
    }

    @Override
    public void saveParam(Object obj) throws Exception {
        OptionsParam options = (OptionsParam) obj;
        AIOptions aiOptions = options.getParamSet(AIOptions.class);

        if (aiOptions != null) {
            aiOptions.setOllamaUrl(ollamaUrlField.getText());
            aiOptions.setModelName(modelNameField.getText());
            aiOptions.setDarkMode(darkModeCheckbox.isSelected());
            aiOptions.setWebSearchEnabled(webSearchCheckbox.isSelected());
        }
    }

    @Override
    public String getHelpIndex() {
        return "ui.dialogs.options.ai";
    }
}
