package com.example.pyp19_quiz4

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import kotlin.system.measureNanoTime
import kotlin.system.measureTimeMillis

class MainActivity : AppCompatActivity() {
    // load native c file
    companion object {
        init {
            System.loadLibrary("myHashFile")
        }
    }
    // import native c file function
    external fun hashMe(msg: String, msgLen: Int,
                                salt: String, saltLen: Int,
                                iterations: Int, byteLen: Int) : ByteArray

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // function: 1) on click validate empty field
    //           2) validate if iteration is a number
    //           3) call native c function
    ////////////////////////////////////////////////////////////////////////////////////////////////
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val editText_iteration = findViewById<TextView>(R.id.editText_iteration)
        val editText_message = findViewById<TextView>(R.id.editText_message)
        val editText_salt = findViewById<TextView>(R.id.editText_salt)
        val textView_output = findViewById<TextView>(R.id.textView_output)
        val button_hashme = findViewById<Button>(R.id.button_hashme)

        button_hashme.setOnClickListener{
            button_hashme.hideKeyboard() // hide keyboard on key press
            try{
                val iterationStr = editText_iteration.text.toString()
                val msg = editText_message.text.toString()
                val salt = editText_salt.text.toString()
                var gotError = false

                // validation
                // reset drawable in textview to null
                editText_iteration.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                editText_message.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                editText_salt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                if(iterationStr.isEmpty()){
                    editText_iteration.setHint("huat number here...")
                    editText_iteration.setCompoundDrawablesWithIntrinsicBounds(null, null, // left and top of textview
                        getResources().getDrawable(R.drawable.error_resize, null), null) // right and bottom textview
                    gotError = true
                }
                if(msg.isEmpty()){
                    editText_message.setHint("message here...")
                    editText_message.setCompoundDrawablesWithIntrinsicBounds(null, null, // left and top of textview
                        getResources().getDrawable(R.drawable.error_resize, null), null) // right and bottom textview
                    gotError = true
                }
                if(salt.isEmpty()){
                    editText_salt.setHint("salt here...")
                    editText_salt.setCompoundDrawablesWithIntrinsicBounds(null, null, // left and top of textview
                        getResources().getDrawable(R.drawable.error_resize, null), null) // right and bottom textview
                    gotError = true
                }

                if(!gotError){ // no error
                    val iteration = iterationStr.toInt() // catch NumberFormatException error

                    var time = measureNanoTime {
                        val result = hashMe(msg, msg.length, salt, salt.length, iteration, 128)
                        textView_output.text = result.toHexString()
                    }
                    Toast.makeText(this, "took " +  String.format("%.3f", time.toFloat()/1000000.0f) + "ms", Toast.LENGTH_SHORT).show() // nano to milli is divide by 1000000
                }
            }catch(error:NumberFormatException){ // catch iteration NumberFormatException error
                Toast.makeText(this, "Iteration must be a number", Toast.LENGTH_SHORT).show()
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // function: convert ByteArray to HexString
    ////////////////////////////////////////////////////////////////////////////////////////////////
    fun ByteArray.toHexString() = joinToString("") { "%02X".format(it) }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // function: hide keyboard on key press
    ////////////////////////////////////////////////////////////////////////////////////////////////
    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
}
