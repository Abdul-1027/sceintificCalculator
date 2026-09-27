package com.example.scientificcalculator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.scientificcalculator.databinding.ActivityMainBinding
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isDegreeMode = true
    private var expression = StringBuilder()
    private val resultFormatter = DecimalFormat("#,##0.##########")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupButtons()
        refreshDisplay()
    }

    private fun setupButtons() {
        binding.btn0.setOnClickListener { append("0") }
        binding.btn1.setOnClickListener { append("1") }
        binding.btn2.setOnClickListener { append("2") }
        binding.btn3.setOnClickListener { append("3") }
        binding.btn4.setOnClickListener { append("4") }
        binding.btn5.setOnClickListener { append("5") }
        binding.btn6.setOnClickListener { append("6") }
        binding.btn7.setOnClickListener { append("7") }
        binding.btn8.setOnClickListener { append("8") }
        binding.btn9.setOnClickListener { append("9") }
        binding.btnDot.setOnClickListener { append(".") }

        binding.btnAdd.setOnClickListener { append("+") }
        binding.btnSubtract.setOnClickListener { append("-") }
        binding.btnMultiply.setOnClickListener { append("*") }
        binding.btnDivide.setOnClickListener { append("/") }
        binding.btnPercent.setOnClickListener { append("%") }
        binding.btnParenOpen.setOnClickListener { append("(") }
        binding.btnParenClose.setOnClickListener { append(")") }

        binding.btnSin.setOnClickListener { append("sin(") }
        binding.btnCos.setOnClickListener { append("cos(") }
        binding.btnTan.setOnClickListener { append("tan(") }
        binding.btnLog.setOnClickListener { append("log(") }
        binding.btnLn.setOnClickListener { append("ln(") }
        binding.btnSqrt.setOnClickListener { append("sqrt(") }

        binding.btnPow.setOnClickListener { append("^") }
        binding.btnFactorial.setOnClickListener { append("!") }

        binding.btnPi.setOnClickListener { append("pi") }
        binding.btnE.setOnClickListener { append("e") }

        binding.btnDegRad.setOnClickListener {
            isDegreeMode = !isDegreeMode
            val label = if (isDegreeMode) "DEG" else "RAD"
            binding.btnDegRad.text = label
            binding.tvMode.text = label
            refreshDisplay()
        }

        binding.btnClear.setOnClickListener {
            expression.clear()
            refreshDisplay()
        }

        binding.btnDel.setOnClickListener {
            if (expression.isNotEmpty()) {
                expression.deleteCharAt(expression.length - 1)
            }
            refreshDisplay()
        }

        binding.btnEquals.setOnClickListener {
            evaluateExpression(commit = true)
        }
    }

    private fun append(value: String) {
        expression.append(value)
        refreshDisplay()
    }

    private fun refreshDisplay() {
        binding.tvExpression.text = if (expression.isEmpty()) "" else expression.toString()
        evaluateExpression(commit = false)
    }

    private fun evaluateExpression(commit: Boolean) {
        if (expression.isEmpty()) {
            binding.tvResult.text = "0"
            return
        }
        try {
            val evaluator = ExpressionEvaluator(isDegreeMode)
            val result = evaluator.evaluate(expression.toString())
            val formatted = formatResult(result)
            binding.tvResult.text = formatted
            if (commit) {
                expression.clear()
                expression.append(formatted)
                binding.tvExpression.text = ""
            }
        } catch (ex: Exception) {
            if (commit) {
                binding.tvResult.text = "Error"
            }
        }
    }

    private fun formatResult(value: Double): String {
        if (value.isNaN()) return "Error"
        if (value.isInfinite()) return if (value > 0) "Infinity" else "-Infinity"
        return if (value == value.toLong().toDouble() && Math.abs(value) < 1e15) {
            value.toLong().toString()
        } else {
            resultFormatter.format(value)
        }
    }
}