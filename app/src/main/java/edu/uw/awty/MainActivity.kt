package edu.uw.awty

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    var serviceStarted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val inputMsg = findViewById<EditText>(R.id.input_msg)
        val inputPhoneNum = findViewById<EditText>(R.id.input_phone_num)
        val inputInterval = findViewById<EditText>(R.id.input_interval)
        val btnStart = findViewById<Button>(R.id.btn_start)

        btnStart.setOnClickListener {
            if (serviceStarted) {
                stopService(Intent(applicationContext, MsgService::class.java))
                serviceStarted = false
                btnStart.text = getString(R.string.start)
            } else {
                try {
                    when {
                        inputMsg.text.isEmpty() -> {
                            Toast.makeText(
                                this,
                                getString(R.string.message_empty),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        inputPhoneNum.text.toString().length != 10 -> {
                            Toast.makeText(
                                this,
                                getString(R.string.invalid_phone_number),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        inputInterval.text.isEmpty() ||
                                inputInterval.text.toString().toInt() <= 0 -> {
                            Toast.makeText(
                                this,
                                getString(R.string.interval_must_greater_zero),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        else -> {
                            val intent = Intent(applicationContext, MsgService::class.java).apply {
                                putExtra("msg", inputMsg.text.toString())
                                putExtra("phone_num", inputPhoneNum.text.toString())
                                putExtra("interval", inputInterval.text.toString().toInt())
                            }
                            startForegroundService(intent)
                            serviceStarted = true
                            btnStart.text = getString(R.string.stop)
                        }
                    }
                } catch (err: Exception) {
                    Toast.makeText(this, err.toString(), Toast.LENGTH_SHORT).show()
                }
            }

        }
    }
}