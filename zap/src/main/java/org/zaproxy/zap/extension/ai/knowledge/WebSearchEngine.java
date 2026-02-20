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
package org.zaproxy.zap.extension.ai.knowledge;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple web search engine that scrapes results from DuckDuckGo (HTML version).
 * This allows the AI Assistant to fetch recent information without API keys.
 */
public class WebSearchEngine {

    private static final Logger LOGGER = LogManager.getLogger(WebSearchEngine.class);
    private static final String SEARCH_URL = "https://html.duckduckgo.com/html/?q=";

    public List<String> search(String query) {
        List<String> results = new ArrayList<>();
        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
            URL url = new java.net.URI(SEARCH_URL + encodedQuery).toURL();

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            conn.setInstanceFollowRedirects(true);

            if (conn.getResponseCode() != 200) {
                LOGGER.warn("Web search failed with code: " + conn.getResponseCode());
                return results;
            }

            StringBuilder html = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    html.append(line);
                }
            }

            Source source = new Source(html.toString());
            // DuckDuckGo HTML result links logic
            // Try to find elements with class "result__a" which are the main links
            List<Element> linkElements = source.getAllElements("class", "result__a", false);

            if (linkElements != null && !linkElements.isEmpty()) {
                for (Element link : linkElements) {
                    if (results.size() >= 5) break;

                    String title = link.getTextExtractor().toString();
                    String href = link.getAttributeValue("href");

                    if (href != null && !href.isEmpty()) {
                        results.add(title + " - " + href);
                    }
                }
            } else {
                 // Fallback: search for any anchor tag that looks like a result
                 List<Element> allLinks = source.getAllElements("a");
                 for (Element link : allLinks) {
                     if (results.size() >= 5) break;
                     String href = link.getAttributeValue("href");
                     // Filter out internal DDG links
                     if (href != null && href.startsWith("http") && !href.contains("duckduckgo.com") && !href.contains("spreadprivacy.com")) {
                         String title = link.getTextExtractor().toString();
                         if (!title.isEmpty()) {
                             results.add(title + " - " + href);
                         }
                     }
                 }
            }

        } catch (Exception e) {
            LOGGER.error("Error performing web search: " + e.getMessage(), e);
        }
        return results;
    }
}
