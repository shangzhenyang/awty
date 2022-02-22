package edu.uw.awty

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {
    var serviceStarted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (checkSelfPermission(android.Manifest.permission.SEND_SMS) !=
            PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.SEND_SMS),
                0
            )
        }

        val inputMsg = findViewById<EditText>(R.id.input_msg)
        val inputPhoneNum = findViewById<EditText>(R.id.input_phone_num)
        val inputInterval = findViewById<EditText>(R.id.input_interval)
        val btnStart = findViewById<Button>(R.id.btn_start)

        btnStart.setOnClickListener {
            when {
                checkSelfPermission(android.Manifest.permission.SEND_SMS) !=
                        PackageManager.PERMISSION_GRANTED -> {
                    AlertDialog.Builder(this)
                        .setTitle(getString(R.string.permission_denied))
                        .setMessage(getString(R.string.cannot_work_without_sms_permission))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.ok, null)
                        .show()
                }
                serviceStarted -> {
                    stopService(Intent(applicationContext, MsgService::class.java))
                    serviceStarted = false
                    btnStart.text = getString(R.string.start)
                }
                else -> {
                    try {
                        when {
                            inputMsg.text.isEmpty() -> {
                                AlertDialog.Builder(this)
                                    .setTitle(getString(R.string.error))
                                    .setMessage(getString(R.string.message_empty))
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setPositiveButton(android.R.string.ok, null)
                                    .show()
                            }
                            inputPhoneNum.text.toString().length != 10 -> {
                                AlertDialog.Builder(this)
                                    .setTitle(getString(R.string.error))
                                    .setMessage(getString(R.string.invalid_phone_number))
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setPositiveButton(android.R.string.ok, null)
                                    .show()
                            }
                            inputInterval.text.isEmpty() ||
                                    inputInterval.text.toString().toInt() <= 0 -> {
                                AlertDialog.Builder(this)
                                    .setTitle(getString(R.string.error))
                                    .setMessage(getString(R.string.interval_must_greater_zero))
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setPositiveButton(android.R.string.ok, null)
                                    .show()
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
                        AlertDialog.Builder(this)
                            .setTitle(getString(R.string.error))
                            .setMessage(err.toString())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.ok, null)
                            .show()
                    }
                }
            }

        }
    }
}