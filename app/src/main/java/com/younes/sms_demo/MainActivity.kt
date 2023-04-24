package com.younes.sms_demo

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.huawei.hms.support.sms.ReadSmsManager
import com.huawei.hms.support.sms.common.ReadSmsConstant

import com.younes.sms_demo.databinding.ActivityMainBinding
import com.younes.sms_demo.sms.HuaweiOtpBroadcastReceiver
import com.younes.sms_demo.sms.SmsHelper


/**
 * Huawei SMS retriever demo
 * the SMS is sent directly from device for demo purposes
 *
 * clone project and run directly, no configuration required
 *
 * the included 'agconnect-services.json' file is for educational purposes only, you are not allowed to use it in other projects
 * or re-distribute it in anyway
 *
 * keep in mind:
 * - device with HMS core and SIM card is required for testing
 * - Charges might apply when send an SMS
 * - You can send an SMS to the same number/same device
 */

@SuppressLint("SetTextI18n")

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    /*** receivers : start **/
    /**
     * receives the SMS containing the OTP from HMS broadcast receiver to display it
     * the broadcast received here is sent from [HuaweiOtpBroadcastReceiver.onReceive]
     */
    private val updateUIReceiver = object : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            intent: Intent
        ) { //UI update here
            setStatus("updateUIReceiver:onReceive")
            intent.getStringExtra(HuaweiOtpBroadcastReceiver.DEMO_EXTRA_SMS)?.let {
                setStatus("OtpReceiver::onReceive:: => $it")

                val otp = SmsHelper.parseSmsContent(it)
                setStatus("parsed OTP ==> $otp")
                binding.otpField.setText(otp)
            }
        }
    }

    /**
     * Huawei otp broadcast receiver
     * receives the whole SMS message contains our OTP sent by HMS Core
     *
     * registered when use click the "send OTP" button, in [initSmsReaderThenSendOtp]
     */
    private val  huaweiOtpBroadcastReceiver = HuaweiOtpBroadcastReceiver()
    /*** receivers : end **/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
    }

    private fun initUI(){
        setStatus("initializing...")

        binding.sendOtpBtn.setOnClickListener {
            setStatus("checking send_sms permission, required for this demo")
            checkPermissionThenSendOtp()
        }

        setStatus("click send OTP to start")
    }

    /*** receivers logic : START **/

    override fun onStart() {
        super.onStart()
        registerLocalOtpBroadcastReceiver()
    }

    /**
     * this broadcast receiver can 'timeout'
     * should not be registered in 'onStart()' method
     *
     * this method is called when user click sendOtp button [initSmsReaderThenSendOtp]
     */
    private fun registerSmsBroadcastReceiver() {
        setStatus("registerSmsBroadcastReceiver:invoke")

        val intentFilter = IntentFilter( ReadSmsConstant.READ_SMS_BROADCAST_ACTION)
        registerReceiver(huaweiOtpBroadcastReceiver, intentFilter)
    }

    /**
     * local receiver that will receive the OTP from [HuaweiOtpBroadcastReceiver] to update the UI
     *
     */
    private fun registerLocalOtpBroadcastReceiver() {
        setStatus("registerOtpBroadcastReceiver:invoke")
        val filter = IntentFilter()
        filter.addAction( HuaweiOtpBroadcastReceiver.DEMO_ACTION_UPDATE_UI )

        registerReceiver(updateUIReceiver, filter)
    }
    override fun onStop() {
        super.onStop()
        unregisterReceiver(huaweiOtpBroadcastReceiver)
        unregisterReceiver(updateUIReceiver)
    }
    /*** receivers logic : END **/


    /*** send_sms permission handling : START **/

    private val SMS_REQ_CODE = 1

    /**
     * check if app has send_sms permission
     * this is required for this demo only, in production this permission should not be used for verifying user's number
     *
     *  called when user click 'sendOtp' button, in [initUI]
     */
    private fun checkPermissionThenSendOtp() {
        val permissionCheck =
            ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)

        if (permissionCheck == PackageManager.PERMISSION_GRANTED)
            initSmsReaderThenSendOtp()
        else
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.SEND_SMS), SMS_REQ_CODE
            )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == SMS_REQ_CODE) {

            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                initSmsReaderThenSendOtp()
            else
                Toast.makeText(
                    this,
                    "Permissions has been refused",
                    Toast.LENGTH_LONG
                ).show()
        }
    }

    /*** send_sms permission handling : END **/


    /**
     * initialize huawei sms reader, register Otp receiver, then send otp using user's sim card for demo purposes
     * called after getting send_sms permission in [onRequestPermissionsResult]
     */
    private fun initSmsReaderThenSendOtp() {
        setStatus("generateSmsCode:invoke")
        registerSmsBroadcastReceiver()
        initHuaweiSmsReader()
        sendOtpSms()
    }

    /**
     * initialize Huawei sms reader, required for [HuaweiOtpBroadcastReceiver] to receive OTP SMS
     * called in [initHuaweiSmsReader]
     */
    private fun initHuaweiSmsReader() {
        setStatus("initSmsManager:invoke")

        val task = ReadSmsManager.start(this@MainActivity)
        task.addOnCompleteListener {
            setStatus("initSmsManager:task:success=> ${it.isSuccessful}")

            if (task.isSuccessful) {
                // The service is enabled successfully. Continue with the process.
                setStatus("Huawei ReadSmsManager service has been enabled.")
            } else {
                setStatus("Huawei ReadSmsManager service failed to initialized")
                setStatus("error: ${task.exception}")
            }

        }
    }

    /**
     * send SMS to phone number using user's sim card, you can send an sms to yourself (same number)
     * this is used for demo only, in production the SMS is usually sent from server side
     *
     *
     * called in [initSmsReaderThenSendOtp]
     */
    private fun sendOtpSms() {
        val phoneNumber = binding.phoneNumberField.text.toString()

        if (phoneNumber.isEmpty()) {
            setStatus("No phone number, Aborting... ")
            Toast.makeText(this, "Please enter the phone number..", Toast.LENGTH_LONG).show()
            return
        }

        //generate sms content
        val smsContent = SmsHelper.generateSmsContent(applicationContext)
        setStatus("sending sms => $smsContent")

        val smsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
              getSystemService(SmsManager::class.java)
        } else {
            @Suppress("DEPRECATION")
            SmsManager.getDefault()
        }

        //send SMS using device itself _ for production scenario this will be done from server side
        smsManager.sendTextMessage(
            phoneNumber,
            null,
            smsContent,
            null,
            null
        )

    }


    /**
     * append new line to status TextView to keep track of whats going on
     * for debugging and demo purposes
     *
     * @param status
     */
    @SuppressLint("SetTextI18n")
    fun setStatus(status: String) {
        runOnUiThread {

            val existing = binding.status.text
            binding.status.text = """${existing}
    $status""".trimMargin()
        }
    }

}
