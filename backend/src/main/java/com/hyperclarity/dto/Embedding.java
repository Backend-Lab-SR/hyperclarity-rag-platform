package com.hyperclarity.dto;

import java.util.List;

public record Embedding(String text, List<Double> vector, String modelName) {
}
