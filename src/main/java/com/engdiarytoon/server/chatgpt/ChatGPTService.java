package com.engdiarytoon.server.chatgpt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class ChatGPTService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    public ChatGPTService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // 번역 요청
    public String translateContent(String content) {
        ChatGPTRequest request = new ChatGPTRequest("Translate the following text to Korean in a natural diary tone: " + content);
        var response = sendRequestToOpenAI(request);
        return extractContentFromResponse(response);
    }

    // 문법 및 문맥 피드백 요청
    public String analyzeContent(String content) {
        ChatGPTRequest request = new ChatGPTRequest("Provide polite feedback in Korean. Focus on correcting grammar mistakes and unnatural expressions in the context of an English diary. Include numbered examples of the corrections. For example: 1. 'It's sunny day today.' 에서 sunny 앞에는 'a'가 붙어야 합니다.\\n2. ..." + content);
        var response = sendRequestToOpenAI(request);
        return extractContentFromResponse(response);
    }

    // OpenAI API 요청 메서드
    private ChatGPTResponse sendRequestToOpenAI(ChatGPTRequest request) {
        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 본문과 헤더 결합
        HttpEntity<ChatGPTRequest> httpEntity = new HttpEntity<>(request, headers);

        // API 호출
        ResponseEntity<ChatGPTResponse> response = restTemplate.postForEntity(
                apiUrl,
                httpEntity,
                ChatGPTResponse.class
        );

        // 응답 반환
        return response.getBody();
    }

    // 응답에서 content 추출
    private String extractContentFromResponse(ChatGPTResponse response) {
        if (response != null && !response.getChoices().isEmpty()) {
            return response.getChoices().get(0).getMessage().getContent();
        }
        return "No response from ChatGPT.";
    }
}
