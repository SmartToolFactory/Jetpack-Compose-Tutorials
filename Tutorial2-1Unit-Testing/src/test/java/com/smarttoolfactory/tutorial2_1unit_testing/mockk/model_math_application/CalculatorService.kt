package com.smarttoolfactory.tutorial2_1unit_testing.mockk.model_math_application

interface CalculatorService {
    fun add(input1: Double, input2: Double): Double
    fun subtract(input1: Double, input2: Double): Double
    fun multiply(input1: Double, input2: Double): Double
    fun divide(input1: Double, input2: Double): Double

    fun print(text: String)

    fun log(text: String): String
}