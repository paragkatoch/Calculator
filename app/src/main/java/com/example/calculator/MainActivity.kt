package com.example.calculator

import android.content.Context
import android.os.Bundle
import android.os.Vibrator
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.calculator.databinding.ActivityMainBinding


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    // Background control strings
    private var cache: String = ""
    private var backText: String = ""
    private var zero = false

    // OnCreate Method
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        // Setting initial output as zero
        update("0")

        // Call to set Buttons and the actions
        setButtons()
    }

    // Method to create reference to buttons and set their actions
    private fun setButtons() {
        binding.apply {
            val number: List<View> = listOf(zero,one,two,three,four,five,six,seven,eight,nine,dot,clear,back,divide,plus,minus,multiply,percent)
            for (item in number) {
                // call to update the TextView
                item.setOnClickListener {
                    operate(it)
                    vibrate()
                }
            }

            // call to calculate the result
            equals.setOnClickListener {
                calculate()
                vibrate()
            }
        }
    }

    // Method to update backText and screen
    private fun operate(view: View) {

        if (backText[0] == '0' && !zero) backText = ""
        when (view.id) {
            R.id.one -> backText += "1"
            R.id.two -> backText += "2"
            R.id.three -> backText += "3"
            R.id.four -> backText += "4"
            R.id.five -> backText += "5"
            R.id.six -> backText += "6"
            R.id.seven -> backText += "7"
            R.id.eight -> backText += "8"
            R.id.nine -> backText += "9"
            R.id.zero -> { backText += "0"}
            R.id.divide -> operator('÷')
            R.id.plus -> operator('+')
            R.id.minus -> operator('-')
            R.id.multiply -> operator('×')
            R.id.back -> {
                if (backText != "") backText = backText.substring(0, backText.length - 1)
                if (backText.isEmpty()) {backText = "0"; zero = false}
            }
            R.id.dot -> {
                if(backText.isEmpty() || backText[backText.lastIndex] !in '0'..'9') {zero = true;backText +="0"}
                if (backText != "") {
                    dot@for(i in backText.lastIndex downTo 0)
                        if (backText[i] !in '0'..'9') {
                            if (backText[i] != '.')
                                backText += "."
                            break@dot
                        }
                        else if (i == 0)
                            backText += "."
                }
            }
            R.id.clear -> { backText = "0"; zero = false}
            R.id.percent -> {
                if (backText == "")
                    backText = "0"
                calculate()
                backText = (backText.toFloat() / 100).toString()
            }
        }
        // updates the TextView
        update()
    }

    // Method to put button on vibrate when clicked
    private fun vibrate() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(30)
    }

    // Method to handle operator related tasks
    private fun operator(sym: Char) {
        if (backText == "") {
            println("back $backText back");backText += "0"
        } else if (sym != backText.last()) {
            val last = backText.lastIndex
            if (backText[last] in '0'..'9') backText += sym
            else backText = backText.dropLast(1) + sym
        }
    }

    // Method to calculate result and update the screen
    private fun calculate() {
        if (backText.last() !in '0'..'9')
            backText = backText.dropLast(1)
        var curr = 0F
        val i = 0
        backText += " "
        while (backText[i] in '0'..'9' || backText[i] == '.') {
            cache += backText[i]
            backText = backText.substring(i + 1)
        }
        curr += cache.toFloat()
        cache = ""
        while (backText != " ") {
            when (backText[i]) {
                '+' -> {
                    backText = convert(backText)
                    curr += cache.toFloat()
                }
                '-' -> {
                    backText = convert(backText)
                    curr -= cache.toFloat()
                }
                '×' -> {
                    backText = convert(backText)
                    curr *= cache.toFloat()
                }
                else -> {
                    backText = convert(backText)
                    curr /= cache.toFloat()
                }
            }
            cache = ""
        }
        // val text: TextView = findViewById(R.id.calc)
        if (curr - curr.toInt() == 0F)
            update(curr.toInt().toString())
        else
            update(curr.toString())
    }

    private fun convert(txt: String): String {
        var text = txt
        text = text.substring(1)
        while (text[0] in '0'..'9' || text[0] == '.') {
            cache += text[0]
            text = text.substring(1)
        }
        return text
    }

    private fun update(a: String = backText) {
        binding.calc.text = a
        backText = binding.calc.text.toString()
    }
}