package com.example.lab7.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab7.BuildConfig
import com.example.lab7.data.Question
import com.example.lab7.data.api.GeminiApiClient
import com.example.lab7.data.api.model.RequestContent
import com.example.lab7.data.api.model.GeminiRequest
import com.example.lab7.data.api.model.RequestPart
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TriviaUiState(
    val isLoading: Boolean = false,
    val questions: List<Question> = emptyList(),
    val error: String? = null
)

class TriviaViewModel : ViewModel() {
    private val apiService = GeminiApiClient.create()
    private val apiKey = BuildConfig.GEMINI_API_KEY

    private val _uiState = MutableStateFlow(TriviaUiState())
    val uiState: StateFlow<TriviaUiState> = _uiState.asStateFlow()

    fun generateQuestions(topic: String) {
        if (topic.isBlank()) {
            _uiState.value = TriviaUiState(error = "Topic cannot be empty")
            return
        }

        viewModelScope.launch {
            _uiState.value = TriviaUiState(isLoading = true, error = null)

            try {
                val prompt = """
                    Generate exactly 5 trivia questions with answers about "$topic".
                    
                    Format each question exactly like this:
                    Q: [question text here]
                    A: [answer text here]
                    
                    Separate each question-answer pair with a blank line.
                    Make the questions interesting, educational, and relevant to the topic.
                    Keep answers concise but informative.
                """.trimIndent()

                val request = GeminiRequest(
                    contents = listOf(
                        RequestContent(
                            parts = listOf(RequestPart(text = prompt))
                        )
                    )
                )

                val response = apiService.generateContent(apiKey, request)
                
                val questions = parseQuestionsFromResponse(response, topic)
                
                _uiState.value = TriviaUiState(
                    isLoading = false,
                    questions = questions,
                    error = null
                )
            } catch (e: Exception) {
                val errorMessage = when {
                    e.message?.contains("404") == true -> "API endpoint not found. Please check if the API key is valid and the model name is correct."
                    e.message?.contains("401") == true -> "Unauthorized. Please check your API key."
                    e.message?.contains("403") == true -> "Access forbidden. Please check your API key permissions."
                    e.message?.contains("400") == true -> "Bad request. Please check the request format."
                    else -> "Error: ${e.message ?: "Failed to generate questions"}"
                }
                _uiState.value = TriviaUiState(
                    isLoading = false,
                    error = errorMessage
                )
            }
        }
    }

    private fun parseQuestionsFromResponse(
        response: com.example.lab7.data.api.model.GeminiResponse,
        topic: String
    ): List<Question> {
        val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            ?: return emptyList()

        val questions = mutableListOf<Question>()
        
        // Split by double newlines first (question-answer pairs)
        val sections = text.split(Regex("\n\n+")).filter { it.trim().isNotBlank() }
        
        var questionId = 1
        
        for (section in sections) {
            val lines = section.split("\n").map { it.trim() }.filter { it.isNotBlank() }
            var currentQuestion = ""
            var currentAnswer = ""
            
            for (line in lines) {
                when {
                    line.matches(Regex("^[Qq]:\\s*.+", RegexOption.IGNORE_CASE)) -> {
                        currentQuestion = line.replace(Regex("^[Qq]:\\s*", RegexOption.IGNORE_CASE), "").trim()
                    }
                    line.matches(Regex("^[Aa]:\\s*.+", RegexOption.IGNORE_CASE)) -> {
                        currentAnswer = line.replace(Regex("^[Aa]:\\s*", RegexOption.IGNORE_CASE), "").trim()
                    }
                    currentQuestion.isEmpty() && line.isNotBlank() -> {
                        // Might be a question without Q: prefix
                        if (!line.matches(Regex("^[Aa]:\\s*", RegexOption.IGNORE_CASE))) {
                            currentQuestion = line
                        }
                    }
                    currentQuestion.isNotEmpty() && currentAnswer.isEmpty() -> {
                        // Continuation of question
                        currentQuestion += " $line"
                    }
                    currentAnswer.isNotEmpty() || line.matches(Regex("^[Aa]:\\s*", RegexOption.IGNORE_CASE)) -> {
                        // Answer or continuation
                        if (line.matches(Regex("^[Aa]:\\s*", RegexOption.IGNORE_CASE))) {
                            currentAnswer = line.replace(Regex("^[Aa]:\\s*", RegexOption.IGNORE_CASE), "").trim()
                        } else {
                            currentAnswer += if (currentAnswer.isEmpty()) line else " $line"
                        }
                    }
                }
            }
            
            if (currentQuestion.isNotEmpty() && currentAnswer.isNotEmpty()) {
                questions.add(Question(questionId++, currentQuestion.trim(), currentAnswer.trim()))
            } else if (currentQuestion.isNotEmpty()) {
                // Question without explicit answer - use the question as both
                questions.add(Question(questionId++, currentQuestion.trim(), "See question above."))
            }
        }

        // Fallback: try to parse numbered questions
        if (questions.isEmpty()) {
            val numberedPattern = Regex("(\\d+)[\\.\\)]\\s*(.+?)(?=\\d+[\\.\\)]|$)", RegexOption.DOT_MATCHES_ALL)
            val matches = numberedPattern.findAll(text)
            matches.forEachIndexed { index, match ->
                val content = match.groupValues[2].trim()
                val parts = content.split(Regex("(?i)(answer|ans|a:)\\s*"), 2)
                if (parts.size >= 2) {
                    questions.add(Question(index + 1, parts[0].trim(), parts[1].trim()))
                } else if (content.isNotBlank()) {
                    questions.add(Question(index + 1, content, "Answer not provided."))
                }
            }
        }

        // Last resort: split by sentences and create questions
        if (questions.isEmpty() && text.isNotBlank()) {
            val sentences = text.split(Regex("[.!?]+")).filter { it.trim().length > 10 }
            sentences.take(5).forEachIndexed { index, sentence ->
                questions.add(Question(index + 1, sentence.trim(), "Answer related to: $sentence"))
            }
        }

        return questions.take(5) // Limit to 5 questions
    }
}

