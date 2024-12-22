package com.engdiarytoon.server.chatgpt;

import java.util.List;
import java.util.Map;

public class ChatGPTRequest {
    private String model;
    private List<Map<String, String>> messages;

    public ChatGPTRequest(String content) {
        this.model = "gpt-3.5-turbo"; // OpenAI 모델
        this.messages = List.of(Map.of("role", "user", "content", content));
    }

    // Getter/Setter
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<Map<String, String>> getMessages() {
        return messages;
    }

    public void setMessages(List<Map<String, String>> messages) {
        this.messages = messages;
    }
}
