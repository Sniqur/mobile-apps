package com.example.lab7.data

object QuestionGenerator {
    fun generateQuestions(topic: String): List<Question> {
        return when (topic.lowercase()) {
            "programming" -> programmingQuestions
            "travelling" -> travellingQuestions
            "math" -> mathQuestions
            "security" -> securityQuestions
            else -> emptyList()
        }
    }

    private val programmingQuestions = listOf(
        Question(1, "What is the main purpose of a variable in programming?", "A variable is used to store data that can be referenced and manipulated throughout a program."),
        Question(2, "What does 'API' stand for?", "API stands for Application Programming Interface, which allows different software applications to communicate with each other."),
        Question(3, "What is the difference between '==' and '===' in JavaScript?", "'==' compares values with type coercion, while '===' compares both value and type without coercion."),
        Question(4, "What is object-oriented programming?", "Object-oriented programming (OOP) is a programming paradigm based on the concept of objects, which contain data and code to manipulate that data."),
        Question(5, "What is a function in programming?", "A function is a reusable block of code that performs a specific task and can be called multiple times throughout a program.")
    )

    private val travellingQuestions = listOf(
        Question(1, "What is the capital city of Japan?", "The capital city of Japan is Tokyo."),
        Question(2, "Which country is known as the 'Land of the Rising Sun'?", "Japan is known as the 'Land of the Rising Sun'."),
        Question(3, "What is the longest river in the world?", "The Nile River is the longest river in the world, stretching approximately 6,650 kilometers."),
        Question(4, "Which continent is the largest by land area?", "Asia is the largest continent by land area, covering about 30% of Earth's total land area."),
        Question(5, "What is the smallest country in the world?", "Vatican City is the smallest country in the world, with an area of just 0.44 square kilometers.")
    )

    private val mathQuestions = listOf(
        Question(1, "What is the value of π (pi) to two decimal places?", "The value of π (pi) to two decimal places is 3.14."),
        Question(2, "What is the square root of 144?", "The square root of 144 is 12."),
        Question(3, "What is the formula for the area of a circle?", "The formula for the area of a circle is A = πr², where r is the radius."),
        Question(4, "What is the sum of the angles in a triangle?", "The sum of the angles in a triangle is always 180 degrees."),
        Question(5, "What is 2 to the power of 8?", "2 to the power of 8 equals 256.")
    )

    private val securityQuestions = listOf(
        Question(1, "What is a firewall in computer security?", "A firewall is a network security device that monitors and filters incoming and outgoing network traffic based on predetermined security rules."),
        Question(2, "What is two-factor authentication (2FA)?", "Two-factor authentication is a security process that requires users to provide two different authentication factors to verify their identity."),
        Question(3, "What is phishing?", "Phishing is a cyber attack that uses fraudulent emails, messages, or websites to trick individuals into revealing sensitive information like passwords or credit card numbers."),
        Question(4, "What is encryption?", "Encryption is the process of converting readable data (plaintext) into an encoded form (ciphertext) that can only be decoded with the correct key."),
        Question(5, "What is a VPN?", "A VPN (Virtual Private Network) creates a secure, encrypted connection between your device and the internet, protecting your online privacy and data.")
    )
}

