package com.murat.ozlusozler.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.Collections;
import java.util.List;

public class GeminiRequest {

    @SerializedName("contents")
    private final List<Content> contents;

    @SerializedName("generationConfig")
    private final GenerationConfig generationConfig;

    public GeminiRequest(List<Content> contents, GenerationConfig generationConfig) {
        this.contents = contents;
        this.generationConfig = generationConfig;
    }

    public List<Content> getContents() {
        return contents;
    }

    public GenerationConfig getGenerationConfig() {
        return generationConfig;
    }

    public static GeminiRequest fromPrompt(String prompt) {
        Part part = new Part(prompt);
        Content content = new Content(Collections.singletonList(part));
        GenerationConfig config = new GenerationConfig(0.2f, 1024);
        return new GeminiRequest(Collections.singletonList(content), config);
    }

    public static class Content {
        @SerializedName("parts")
        private final List<Part> parts;

        public Content(List<Part> parts) {
            this.parts = parts;
        }

        public List<Part> getParts() {
            return parts;
        }
    }

    public static class Part {
        @SerializedName("text")
        private final String text;

        public Part(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }

    public static class GenerationConfig {
        @SerializedName("temperature")
        private final float temperature;

        @SerializedName("maxOutputTokens")
        private final int maxOutputTokens;

        public GenerationConfig(float temperature, int maxOutputTokens) {
            this.temperature = temperature;
            this.maxOutputTokens = maxOutputTokens;
        }

        public float getTemperature() {
            return temperature;
        }

        public int getMaxOutputTokens() {
            return maxOutputTokens;
        }
    }
}
