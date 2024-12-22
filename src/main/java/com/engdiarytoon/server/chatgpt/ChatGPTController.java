package com.engdiarytoon.server.chatgpt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/assistant")
public class ChatGPTController {

    private final ChatGPTService chatGPTService;

    @Autowired
    public ChatGPTController(ChatGPTService chatGPTService) {
        this.chatGPTService = chatGPTService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<Map<String, String>> analyzeContent(@RequestBody Map<String, String> request) {
        String content = request.get("content");

        // 1. 번역 요청
        String translation = chatGPTService.translateContent(content);

        // 2. 문법 분석 요청
        String feedback = chatGPTService.analyzeContent(content);

        // 3. 응답 구성
        return ResponseEntity.ok(Map.of(
                "translate", translation,
                "assistant", feedback
        ));
    }
}
