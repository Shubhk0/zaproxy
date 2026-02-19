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
import org.zaproxy.zap.utils.DisplayUtils;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class AIPanel extends AbstractPanel {

    private static final long serialVersionUID = 1L;
    private transient ExtensionAIAssistant extension;

    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private JButton clearButton;
    private JButton quickScanButton;
    private JButton analyzeButton;

    public AIPanel(ExtensionAIAssistant extension) {
        super();
        this.extension = extension;
        setName("AI Assistant");
        setIcon(DisplayUtils.getScaledIcon(new ImageIcon(ExtensionAIAssistant.class.getResource("/resource/icon/16/059.png"))));
        setLayout(new BorderLayout());

        // Quick Actions Toolbar
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setRollover(true);

        quickScanButton = new JButton("Quick Scan");
        quickScanButton.setToolTipText("Run a quick active scan on the selected node");
        quickScanButton.addActionListener(e -> appendSystemMessage("Quick Scan feature coming soon..."));

        analyzeButton = new JButton("Analyze Context");
        analyzeButton.setToolTipText("Analyze the current context for vulnerabilities");
        analyzeButton.addActionListener(e -> appendSystemMessage("Analysis feature coming soon..."));

        clearButton = new JButton("Clear Chat");
        clearButton.addActionListener(e -> chatArea.setText(""));

        toolbar.add(quickScanButton);
        toolbar.add(analyzeButton);
        toolbar.addSeparator();
        toolbar.add(clearButton);

        add(toolbar, BorderLayout.NORTH);

        // Chat Area
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);

        // Input Area
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
        });

        sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        add(inputPanel, BorderLayout.SOUTH);
    }

    private void sendMessage() {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
            appendUserMessage(message);
            inputField.setText("");

            processAIQuery(message);
        }
    }

    public void appendUserMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append("\nYou: " + message + "\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }

    public void appendAIMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append("\nAI: " + message + "\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }

    public void appendSystemMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append("\n[System]: " + message + "\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }

    private void processAIQuery(String query) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append("\nAI: Thinking...\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });

        extension.getAIClient().askOllama(
            query,
            response -> {
                SwingUtilities.invokeLater(() -> {
                     String currentText = chatArea.getText();
                     String marker = "AI: Thinking...\n";
                     int index = currentText.lastIndexOf(marker);
                     if (index != -1) {
                         chatArea.replaceRange("AI: " + response + "\n", index, index + marker.length());
                     } else {
                         chatArea.append("AI: " + response + "\n");
                     }
                     chatArea.setCaretPosition(chatArea.getDocument().getLength());
                });
            },
            complete -> {
                // Completion logic if needed
            }
        );
    }
}
