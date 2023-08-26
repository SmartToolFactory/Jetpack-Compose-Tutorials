package com.smarttoolfactory.tutorial2_1unit_testing.model_math_application

class MathApplication {

    var calcService: CalculatorService? = null


    fun add(input1: Double, input2: Double): Double {
        return calcService!!.add(input1, input2)
    }


    fun subtract(input1: Double, input2: Double): Double {
        return calcService!!.subtract(input1, input2)
    }

    fun multiply(input1: Double, input2: Double): Double {
        return calcService!!.multiply(input1, input2)
    }

    fun divide(input1: Double, input2: Double): Double {
        return calcService!!.divide(input1, input2)
    }

    fun print(text: String) {
        calcService!!.print(text)
    }

    fun log(text: String) = calcService!!.log(text)
}