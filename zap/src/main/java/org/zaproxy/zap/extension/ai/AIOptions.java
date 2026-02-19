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

import org.parosproxy.paros.common.AbstractParam;

public class AIOptions extends AbstractParam {

    private static final String AI_BASE_KEY = "ai";

    private static final String OLLAMA_URL_KEY = AI_BASE_KEY + ".ollama.url";
    private static final String MODEL_NAME_KEY = AI_BASE_KEY + ".model.name";
    private static final String DARK_MODE_KEY = AI_BASE_KEY + ".ui.darkmode";
    private static final String OPENAI_KEY_KEY = AI_BASE_KEY + ".openai.key";

    private String ollamaUrl = "http://localhost:11434";
    // Defaulting to deepseek-r1 as requested
    private String modelName = "deepseek-r1";
    private boolean darkMode = true;
    private String openAiKey = "";

    @Override
    protected void parse() {
        ollamaUrl = getString(OLLAMA_URL_KEY, "http://localhost:11434");
        modelName = getString(MODEL_NAME_KEY, "deepseek-r1");
        darkMode = getBoolean(DARK_MODE_KEY, true);
        openAiKey = getString(OPENAI_KEY_KEY, "");
    }

    public String getOllamaUrl() {
        return ollamaUrl;
    }

    public void setOllamaUrl(String ollamaUrl) {
        this.ollamaUrl = ollamaUrl;
        getConfig().setProperty(OLLAMA_URL_KEY, ollamaUrl);
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
        getConfig().setProperty(MODEL_NAME_KEY, modelName);
    }

    public boolean isDarkMode() {
        return darkMode;
    }

    public void setDarkMode(boolean darkMode) {
        this.darkMode = darkMode;
        getConfig().setProperty(DARK_MODE_KEY, darkMode);
    }

    public String getOpenAiKey() {
        return openAiKey;
    }

    public void setOpenAiKey(String openAiKey) {
        this.openAiKey = openAiKey;
        getConfig().setProperty(OPENAI_KEY_KEY, openAiKey);
    }
}
