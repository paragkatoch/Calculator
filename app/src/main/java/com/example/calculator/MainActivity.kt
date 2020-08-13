package com.example.calculator

import android.content.Context
import android.os.Bundle
import android.os.Vibrator
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.calculator.databinding.ActivityMainBinding


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var cache: String = ""            //equation control string
    private var backText: String = ""         //background string
    private var zero = false
    private var equal = false
    private var stop = false

    // OnCreate Method
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        // Setting initial output as zero
        update("0")

        // Call to set Buttons and the actions
        setButtons()
    }

    // Method to create reference to buttons and set their actions
    private fun setButtons() {
        binding.apply {
            val number: List<View> = listOf(zero, one, two, three, four, five, six, seven, eight,
                nine, dot, clear, back, divide, plus, minus, multiply, percent)

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
                equal = true
            }
        }
    }

    // Method to update backText and screen
    private fun operate(view: View) {

        if ((backText[0] == '0' && !zero) || stop) {backText = ""; stop=false}
        when (view.id) {
            R.id.one -> digits("1")
            R.id.two -> digits("2")
            R.id.three -> digits("3")
            R.id.four -> digits("4")
            R.id.five -> digits("5")
            R.id.six -> digits("6")
            R.id.seven -> digits("7")
            R.id.eight -> digits("8")
            R.id.nine -> digits("9")
            R.id.zero -> digits("0")
            R.id.divide -> operator('÷')
            R.id.plus -> operator('+')
            R.id.minus -> operator('-')
            R.id.multiply -> operator('×')
            R.id.back -> {
                if (backText != "") backText = backText.substring(0, backText.length - 1)
                if (backText.isEmpty()) {
                    backText = "0"; zero = false
                }
            }
            R.id.clear -> {
                backText = "0"; zero = false
            }
            R.id.dot -> {
                if (backText.isEmpty() || backText[backText.lastIndex] !in '0'..'9') {
                    zero = true
                    backText += "0"
                }
                if (backText != "") {
                    dot@ for (i in backText.lastIndex downTo 0)
                        if (backText[i] !in '0'..'9') {
                            if (backText[i] != '.')
                                backText += "."
                            break@dot
                        } else if (i == 0)
                            backText += "."
                }
            }
            R.id.percent -> {
                if (backText == "")
                    backText = "0"
                calculate()
                backText = (backText.toFloat() / 100).toString()
            }
        }
        equal = false
        if(backText.length >=18) {
            val toast = Toast.makeText(this,"Sorry limit exceeded",Toast.LENGTH_SHORT)
            toast.show()
            backText = backText.dropLast(1)
        }
        // updates the screen
        update()
    }
    //Method to handle digit related tasks
    private fun digits(c: String) {
        backText += c
        if (equal)
            backText = c
    }

    // Method to handle operator related tasks
    private fun operator(sym: Char) {
        if (backText == "")
            backText += "0"
        else if (sym != backText.last()) {
            val last = backText.lastIndex
            if (backText[last] in '0'..'9') backText += sym
            else backText = backText.dropLast(1) + sym
        }
    }

    // Method to calculate result and update the screen
    private fun calculate(dec: String = "%.4f") {
        if (backText.last() !in '0'..'9')
            backText = backText.dropLast(1)
        var n = 0
        var firstIndex: Int
        var lastIndex: Int
        try {
            while (true) {
                for (i in 0 until backText.lastIndex) {
                    //println("$i,${backText.lastIndex},$backText")
                    if (backText[i] == '×' || backText[i] == '/') {
                        n = 1
                        firstIndex = 0
                        lastIndex = backText.length
                        back@ for (j in i + 1..backText.lastIndex)
                            if (backText[j] !in '0'..'9' && backText[j] != '.') {
                                lastIndex = j
                                break@back
                            }
                        front@ for (j in i - 1 downTo 0)
                            if (backText[j] !in '0'..'9' && backText[j] != '.') {
                                firstIndex = j + 1
                                break@front
                            }
                        cal(firstIndex, lastIndex)
                    }
                }
                if (n == 0)
                    break
                n = 0
            }
        }
        catch (e: Exception){println(e)}
        cal(0, backText.length)

        if (backText.contains('E') || backText.contentEquals("Infinity")) {
            update(backText)
            stop = true
        }
        else if (backText.toFloat() - backText.toFloat().toInt() == 0F)
           // update()
            update(backText.toFloat().toInt().toString())
        else
            update(String.format(dec, backText.toFloat()))

    }

    private fun cal(first: Int, last: Int) {
        var curr = 0F
        val i = 0
        var str = backText.substring(first, last) + " "
        println("str$str")
        while (str[i] in '0'..'9' || str[i] == '.' || str[i] == 'E') {
            cache += str[i]
            str = str.substring(i + 1)
        }
        println(cache)
        curr += cache.toFloat()
        cache = ""

        while (str != " ") {
            when (str[i]) {
                '+' -> {
                    str = convert(str)
                    curr += cache.toFloat()
                }
                '-' -> {
                    str = convert(str)
                    curr -= cache.toFloat()
                }
                '×' -> {

                    str = convert(str)
                    curr *= cache.toFloat()

                }
                else -> {
                    str = convert(str)
                    curr /= cache.toFloat()
                }
            }
            cache = ""
        }
        backText = backText.substring(0, first) + curr.toString() + backText.substring(last)
    }

    // Method to convert sort string and extract numbers
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
    // Method to put button on vibrate when clicked
    private fun vibrate() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(30)
    }

}