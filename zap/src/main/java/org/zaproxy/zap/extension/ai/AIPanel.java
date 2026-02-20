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
import org.zaproxy.zap.extension.ai.security.LLMAttackLibrary;
import org.zaproxy.zap.utils.DisplayUtils;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

public class AIPanel extends AbstractPanel {

    private static final long serialVersionUID = 1L;
    private final transient ExtensionAIAssistant extension;
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private JButton clearButton;
    private JButton resourcesButton;
    private JButton generatePayloadButton;

    public AIPanel(ExtensionAIAssistant extension) {
        super();
        this.extension = extension;
        this.setLayout(new BorderLayout());
        this.setName("AI Assistant");

        // Simplified icon loading
        ImageIcon icon = null;
        try {
             icon = DisplayUtils.getScaledIcon(ExtensionAIAssistant.class.getResource("/resource/icon/16/059.png"));
        } catch (Exception e) {
             // Ignore
        }
        if (icon != null) {
            this.setIcon(icon);
        }

        // Chat Area
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        this.add(scrollPane, BorderLayout.CENTER);

        // Input Area
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        sendButton = new JButton("Send");

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        this.add(inputPanel, BorderLayout.SOUTH);

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        clearButton = new JButton("Clear");
        resourcesButton = new JButton("Resources");
        generatePayloadButton = new JButton("Gen Payload");

        toolbar.add(clearButton);
        toolbar.add(resourcesButton);
        toolbar.add(generatePayloadButton);

        this.add(toolbar, BorderLayout.NORTH);

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

        appendSystemMessage("Welcome to ZAP AI Assistant! Ask me anything about web security.\n" +
                            "Right-click an Alert to analyze it.\n" +
                            "Use 'Resources' to see useful links like Awesome AI Cybersecurity.");
    }

    private void sendMessage() {
        String text = inputField.getText().trim();
        if (text.isEmpty()) return;

        appendUserMessage(text);
        inputField.setText("");
        inputField.setEnabled(false);
        sendButton.setEnabled(false);

        // Ask AI
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
