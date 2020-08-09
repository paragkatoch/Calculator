package com.example.calculator

import android.content.Context
import android.os.Bundle
import android.os.Vibrator
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    // Background control strings
    private var cache:String =""
    private var backText: String = ""

    // OnCreate Method
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setting initial output as zero
        val text: TextView = findViewById(R.id.calc)
        text.text = "0"

        // Call to set Buttons and the actions
        setButtons()
    }

    // Method to create reference to buttons and set their actions
    private fun setButtons() {
        val zero: Button = findViewById(R.id.zero)
        val one: Button = findViewById(R.id.one)
        val two: Button = findViewById(R.id.two)
        val three: Button = findViewById(R.id.three)
        val four: Button = findViewById(R.id.four)
        val five: Button = findViewById(R.id.five)
        val six: Button = findViewById(R.id.six)
        val seven: Button = findViewById(R.id.seven)
        val eight: Button = findViewById(R.id.eight)
        val nine: Button = findViewById(R.id.nine)
        val dot:Button = findViewById(R.id.dot)
        val clear:Button = findViewById(R.id.clear)
        val back:Button = findViewById(R.id.back)
        val divide:Button = findViewById(R.id.divide)
        val multiply:Button = findViewById(R.id.multiply)
        val minus:Button = findViewById(R.id.minus)
        val plus:Button = findViewById(R.id.plus)
        val percent:Button = findViewById(R.id.percent)
        val equals:Button = findViewById(R.id.equals)

        val number:List<View> = listOf(zero,one,two,three,four,five,six,seven,eight,nine,dot,clear,back,divide,plus,minus,multiply,percent)

        for (item in number) {
            // call to update the TextView
            item.setOnClickListener {
                operate(it)
                vibrate()
            }
        }

        // call to calculate the result
        equals.setOnClickListener {
            if(backText != "" && backText != "0")
            calculate()
            vibrate()
        }
    }

    // Method to update backText and screen
    private fun operate(view: View) {
        // reference to TextView
        val text: TextView = findViewById(R.id.calc)
        backText = text.text.toString()

        if(backText[0] == '0') backText = ""

        when(view.id) {
            R.id.one      -> backText += "1"
            R.id.two      -> backText += "2"
            R.id.three    -> backText += "3"
            R.id.four     -> backText += "4"
            R.id.five     -> backText += "5"
            R.id.six      -> backText += "6"
            R.id.seven    -> backText += "7"
            R.id.eight    -> backText += "8"
            R.id.nine     -> backText += "9"
            R.id.zero     -> backText += "0"
            R.id.divide   -> operator(text.text.toString(),'÷')
            R.id.plus     -> operator(text.text.toString(),'+')
            R.id.minus    -> operator(text.text.toString(),'-')
            R.id.multiply -> operator(text.text.toString(),'×')
            R.id.back     -> {
                if(backText != "") backText = backText.substring(0,backText.length-1)
                if(backText.isEmpty()) backText = "0"
            }
            R.id.dot      -> {
                if(!backText.contains('.') && backText != "") backText += "."
                else backText = text.text.toString()
            }
            R.id.clear    -> backText = "0"

            R.id.percent  -> {
                if(backText != "" && backText != "0")
                calculate()
                backText = ((text.text.toString()).toFloat()/100).toString()
            }

        }
        // updates the TextView
        text.text = backText
    }

    // Method to put button on vibrate when clicked
    private fun vibrate() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(30)
    }

    // Method to handle operator related tasks
    private  fun operator(text: String, sym: Char) {

        val length = backText.lastIndex
        if(backText == "") backText += "0"
        else if(backText[length] != sym && backText != "") {
            val last = backText.lastIndex
            if (backText[last] in '0'..'9') backText+=sym
            else backText = backText.substring(0,length) + sym
        }
       // else if (backText[length] == sym) backText = text
        else backText = text
    }

    // Method to calculate result and update the screen
    private fun calculate() {
        var curr  = 0F
        val i = 0
        backText += " "
        while (backText[i] in '0'..'9' || backText[i] == '.') {
            cache += backText[i]
            backText = backText.substring(i + 1)
        }
        curr += cache.toFloat()
        cache = ""

        while (backText != " ") {
            when(backText[i]) {
                '+' -> {
                    backText = convert(backText)
                    curr += cache.toFloat()
                }
                '-' -> {
                    backText = convert(backText)
                    curr -=cache.toFloat()
                }
                '×' -> {
                    backText = convert(backText)
                    curr *=cache.toFloat()
                }
                else -> {
                    backText = convert(backText)
                    curr /=cache.toFloat()
                }
            }
            cache = ""
        }
        val text: TextView = findViewById(R.id.calc)
        if(curr - curr.toInt() == 0F)
            text.text = curr.toInt().toString()
        else
            text.text = curr.toString()

    }

    // Method to convert sort string and extract numbers
    private fun convert(txt: String):String {
        var text = txt
        text = text.substring(1)
        while (text[0] in '0'..'9' || text[0] == '.') {
            cache += text[0]
            text = text.substring( 1)
        }
        return text
    }





}