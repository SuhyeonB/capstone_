package com.engdiarytoon.server.chatgpt;

import java.util.List;

public class ChatGPTResponse {
    private List<Choice> choices;

    // Getter/Setter
    public List<Choice> getChoices() {
        return choices;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }

    public static class Choice {
        private Message message;

        // Getter/Setter
        public Message getMessage() {
            return message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }
    }

    public static class Message {
        private String content;

        // Getter/Setter
        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
