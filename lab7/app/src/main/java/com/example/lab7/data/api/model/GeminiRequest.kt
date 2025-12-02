package com.example.lab7.data.api.model

data class GeminiRequest(
    val contents: List<RequestContent>
)

data class RequestContent(
    val parts: List<RequestPart>
)

data class RequestPart(
    val text: String
)

