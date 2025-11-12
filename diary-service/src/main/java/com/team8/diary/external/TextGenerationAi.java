package com.team8.diary.external;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.HttpOptions;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TextGenerationAi {

    @Value("${gcp.project.id}")
    private String projectId;
    @Value("${gcp.vertexai.location}")
    private String location;

    @Value("${gcp.vertexai.text.model-id}")
    private String textModelId;

    public List<String> summarizeDiaryIntoThreePrompts(String content) {

        String promptTemplate =
                "You are a picture diary assistant. Summarize the following diary entry into 3 short, vivid visual scenes. " +
                        "Return ONLY the 3 phrases, separated by a semicolon (;). Do not add numbers or labels. " +
                        "Diary: " + content;

        try (Client client =
                     Client.builder()
                             .project(projectId)
                             .location(location)
                             .vertexAI(true)
                             .httpOptions(HttpOptions.builder().apiVersion("v1").build())
                             .build()) {



            GenerateContentResponse response =
                    client.models.generateContent(textModelId, promptTemplate, null);

            String fullResponseText = response.text();

            System.out.println(fullResponseText);
            // Example response:
            // Okay, let's break down how AI works. It's a broad field, so I'll focus on the ...
            //
            // Here's a simplified overview:
            // ...
            return Arrays.stream(fullResponseText.split(";"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        }
    }
}