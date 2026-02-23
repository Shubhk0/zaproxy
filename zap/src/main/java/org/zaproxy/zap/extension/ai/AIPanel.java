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

import org.parosproxy.paros.extension.AbstractPanel;
import org.zaproxy.zap.extension.ai.fuzz.PayloadGenerator;
import org.zaproxy.zap.extension.ai.security.LLMAttackLibrary;
import org.zaproxy.zap.utils.DisplayUtils;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.nio.file.Files;
import java.util.function.Consumer;

public class AIPanel extends AbstractPanel {

    private static final long serialVersionUID = 1L;
    private final transient ExtensionAIAssistant extension;
    private JTabbedPane tabbedPane;

    // Chat Components
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private JButton clearButton;
    private JButton resourcesButton;
    private JButton generatePayloadButton; // Now "Quick Inject" or similar

    // Fuzzing Components
    private transient PayloadGenerator payloadGenerator;
    private JTextArea fuzzOutputArea;
    private JComboBox<String> attackTypeCombo;
    private JTextField contextField;
    private JTextField fuzzModelField; // Text field for manual model override for now, or combo if we list them.
    private JButton generateFuzzPayloadsButton;
    private JButton fuzzClearButton;
    private JButton fuzzCopyButton;
    private JButton fuzzSaveButton;

    public AIPanel(ExtensionAIAssistant extension) {
        super();
        this.extension = extension;
        this.setLayout(new BorderLayout());
        this.setName("AI Assistant");

        ImageIcon icon = null;
        try {
             icon = DisplayUtils.getScaledIcon(ExtensionAIAssistant.class.getResource("/resource/icon/16/059.png"));
        } catch (Exception e) {
             // Ignore
        }
        if (icon != null) {
            this.setIcon(icon);
        }

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Chat", createChatPanel());
        tabbedPane.addTab("Smart Fuzzer", createFuzzPanel());

        this.add(tabbedPane, BorderLayout.CENTER);

        appendSystemMessage("Welcome to ZAP AI Assistant! Ask me anything about web security.\n" +
                            "Right-click an Alert to analyze it.\n" +
                            "Check the 'Smart Fuzzer' tab to generate custom payloads.");
    }

    private JPanel createChatPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Chat Area
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Input Area
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        sendButton = new JButton("Send");

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        panel.add(inputPanel, BorderLayout.SOUTH);

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        clearButton = new JButton("Clear");
        resourcesButton = new JButton("Resources");
        generatePayloadButton = new JButton("LLM Injections");

        toolbar.add(clearButton);
        toolbar.add(resourcesButton);
        toolbar.add(generatePayloadButton);

        panel.add(toolbar, BorderLayout.NORTH);

        // Events
        sendButton.addActionListener(e -> sendMessage());
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
        });

        clearButton.addActionListener(e -> chatArea.setText(""));

        resourcesButton.addActionListener(e -> {
            appendSystemMessage(LLMAttackLibrary.getKnowledgeBaseInfo());
        });

        generatePayloadButton.addActionListener(e -> {
            String prompt = "Generate a list of 5 advanced prompt injection payloads to test an LLM chatbot for security vulnerabilities. Explain how each works.";
            inputField.setText(prompt);
            sendMessage();
        });

        return panel;
    }

    private JPanel createFuzzPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        payloadGenerator = new PayloadGenerator(extension.getAIClient());

        JPanel configPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        configPanel.add(new JLabel("Attack Type:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        String[] attacks = {"SQL Injection", "XSS (Cross Site Scripting)", "Command Injection", "Path Traversal", "Server Side Template Injection (SSTI)", "XML External Entity (XXE)", "NoSQL Injection", "Custom"};
        attackTypeCombo = new JComboBox<>(attacks);
        configPanel.add(attackTypeCombo, gbc);

        gbc.gridx = 0; gbc.gridy++;
        gbc.weightx = 0.0;
        configPanel.add(new JLabel("Context (Optional):"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        contextField = new JTextField(20);
        contextField.setToolTipText("E.g., Login form, Search bar, Specific parameter name");
        configPanel.add(contextField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        gbc.weightx = 0.0;
        configPanel.add(new JLabel("Model Override:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        fuzzModelField = new JTextField(20);
        fuzzModelField.setToolTipText("Leave empty to use default model");
        configPanel.add(fuzzModelField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        gbc.gridwidth = 2;
        generateFuzzPayloadsButton = new JButton("Generate Payloads");
        configPanel.add(generateFuzzPayloadsButton, gbc);

        panel.add(configPanel, BorderLayout.NORTH);

        // Output Area
        fuzzOutputArea = new JTextArea();
        fuzzOutputArea.setEditable(true);
        fuzzOutputArea.setLineWrap(false);
        JScrollPane scrollPane = new JScrollPane(fuzzOutputArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Bottom Toolbar
        JPanel bottomToolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        fuzzClearButton = new JButton("Clear");
        fuzzCopyButton = new JButton("Copy to Clipboard");
        fuzzSaveButton = new JButton("Save to File");

        bottomToolbar.add(fuzzClearButton);
        bottomToolbar.add(fuzzCopyButton);
        bottomToolbar.add(fuzzSaveButton);

        panel.add(bottomToolbar, BorderLayout.SOUTH);

        // Logic
        generateFuzzPayloadsButton.addActionListener(e -> {
            String attackType = (String) attackTypeCombo.getSelectedItem();
            String context = contextField.getText();
            String model = fuzzModelField.getText().trim();
            if (model.isEmpty()) model = null;

            fuzzOutputArea.setText("Generating payloads for " + attackType + "...");
            generateFuzzPayloadsButton.setEnabled(false);

            payloadGenerator.generatePayloads(attackType, context, 10, model,
                response -> SwingUtilities.invokeLater(() -> fuzzOutputArea.setText(response)),
                completion -> SwingUtilities.invokeLater(() -> generateFuzzPayloadsButton.setEnabled(true))
            );
        });

        fuzzClearButton.addActionListener(e -> fuzzOutputArea.setText(""));

        fuzzCopyButton.addActionListener(e -> {
            String text = fuzzOutputArea.getText();
            if (text != null && !text.isEmpty()) {
                StringSelection selection = new StringSelection(text);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, selection);
            }
        });

        fuzzSaveButton.addActionListener(e -> {
            String text = fuzzOutputArea.getText();
            if (text == null || text.isEmpty()) return;

            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    Files.write(file.toPath(), text.getBytes());
                    JOptionPane.showMessageDialog(this, "Saved to " + file.getAbsolutePath());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return panel;
    }

    private void sendMessage() {
        String text = inputField.getText().trim();
        if (text.isEmpty()) return;

        appendUserMessage(text);
        inputField.setText("");
        inputField.setEnabled(false);
        sendButton.setEnabled(false);

        extension.getAIClient().askOllama(text,
            response -> {
                SwingUtilities.invokeLater(() -> {
                     chatArea.append(response);
                     chatArea.setCaretPosition(chatArea.getDocument().getLength());
                });
            },
            completion -> {
                SwingUtilities.invokeLater(() -> {
                    chatArea.append("\n\n");
                    inputField.setEnabled(true);
                    sendButton.setEnabled(true);
                    inputField.requestFocusInWindow();
                });
            }
        );
    }

    public void appendUserMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append("\n--------------------------------------------------\n");
            chatArea.append("You: " + message + "\n");
            chatArea.append("AI: ");
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }

    public void appendAIMessage(String message) {
        SwingUtilities.invokeLater(() -> {
             chatArea.append(message);
             chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }

    public void appendSystemMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append("\n[System]: " + message + "\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }

    @Override
    public void setTabFocus() {
        this.requestFocusInWindow();
    }
}
