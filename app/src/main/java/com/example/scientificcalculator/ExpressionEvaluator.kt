package com.example.scientificcalculator

class ExpressionEvaluator(private val isDegreeMode: Boolean = true) {

    private lateinit var expr: String
    private var pos: Int = -1
    private var ch: Char = ' '

    fun evaluate(expression: String): Double {
        expr = expression.replace(" ", "")
        if (expr.isEmpty()) return 0.0
        pos = -1
        nextChar()
        val result = parseExpression()
        if (pos < expr.length) {
            throw IllegalArgumentException("Unexpected character at position $pos: $ch")
        }
        return result
    }

    private fun nextChar() {
        pos++
        ch = if (pos < expr.length) expr[pos] else Char.MIN_VALUE
    }

    private fun eat(charToEat: Char): Boolean {
        if (ch == charToEat) {
            nextChar()
            return true
        }
        return false
    }

    private fun parseExpression(): Double {
        var x = parseTerm()
        while (true) {
            when {
                eat('+') -> x += parseTerm()
                eat('-') -> x -= parseTerm()
                else -> return x
            }
        }
    }

    private fun parseTerm(): Double {
        var x = parseFactor()
        while (true) {
            when {
                eat('*') -> x *= parseFactor()
                eat('/') -> {
                    val divisor = parseFactor()
                    if (divisor == 0.0) throw ArithmeticException("Division by zero")
                    x /= divisor
                }
                eat('%') -> x %= parseFactor()
                else -> return x
            }
        }
    }

    private fun parseFactor(): Double {
        if (eat('+')) return parseFactor()
        if (eat('-')) return -parseFactor()

        var x: Double
        val startPos = pos

        if (eat('(')) {
            x = parseExpression()
            if (!eat(')')) throw IllegalArgumentException("Missing closing parenthesis")
        } else if (ch.isDigit() || ch == '.') {
            while (ch.isDigit() || ch == '.') nextChar()
            x = expr.substring(startPos, pos).toDouble()
        } else if (ch.isLetter()) {
            while (ch.isLetter()) nextChar()
            val name = expr.substring(startPos, pos)
            x = when (name) {
                "pi" -> Math.PI
                "e" -> Math.E
                else -> {
                    if (!eat('(')) throw IllegalArgumentException("Expected '(' after function $name")
                    val arg = parseExpression()
                    if (!eat(')')) throw IllegalArgumentException("Missing closing parenthesis")
                    applyFunction(name, arg)
                }
            }
        } else {
            throw IllegalArgumentException("Unexpected character: $ch")
        }

        if (eat('^')) {
            x = Math.pow(x, parseFactor())
        }

        while (eat('!')) {
            x = factorial(x)
        }

        return x
    }

    private fun applyFunction(name: String, arg: Double): Double {
        return when (name) {
            "sin" -> Math.sin(toRadiansIfNeeded(arg))
            "cos" -> Math.cos(toRadiansIfNeeded(arg))
            "tan" -> Math.tan(toRadiansIfNeeded(arg))
            "asin" -> fromRadiansIfNeeded(Math.asin(arg))
            "acos" -> fromRadiansIfNeeded(Math.acos(arg))
            "atan" -> fromRadiansIfNeeded(Math.atan(arg))
            "log" -> Math.log10(arg)
            "ln" -> Math.log(arg)
            "sqrt" -> {
                if (arg < 0) throw ArithmeticException("Cannot take sqrt of negative number")
                Math.sqrt(arg)
            }
            "abs" -> Math.abs(arg)
            else -> throw IllegalArgumentException("Unknown function: $name")
        }
    }

    private fun toRadiansIfNeeded(value: Double) =
        if (isDegreeMode) Math.toRadians(value) else value

    private fun fromRadiansIfNeeded(value: Double) =
        if (isDegreeMode) Math.toDegrees(value) else value

    private fun factorial(x: Double): Double {
        if (x < 0 || x != Math.floor(x)) {
            throw IllegalArgumentException("Factorial only defined for non-negative integers")
        }
        var result = 1.0
        var i = 2
        val n = x.toInt()
        while (i <= n) {
            result *= i
            i++
        }
        return result
    }
}