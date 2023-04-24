package com.younes.sms_demo.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.huawei.hms.common.api.CommonStatusCodes
import com.huawei.hms.support.api.client.Status
import com.huawei.hms.support.sms.common.ReadSmsConstant
import com.younes.sms_demo.MainActivity

class HuaweiOtpBroadcastReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "HuaweiOtpReceiver"
        const val DEMO_ACTION_UPDATE_UI = "com.younes.demo.sendOtpToActivity"
        const val DEMO_EXTRA_SMS = "sms"
    }

    /**
     * receive the SMS contains OTP from HMS core
     * forward the SMS to [MainActivity] to update the UI using local broadcast
     *
     * see [MainActivity.updateUIReceiver]
     *
     * @param context
     * @param intent
     */
    override fun onReceive(context: Context?, intent: Intent?) {

        val bundle = intent!!.extras
        if (bundle != null) {
            val status  = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle.getParcelable(ReadSmsConstant.EXTRA_STATUS,Status::class.java)
            } else {
                @Suppress("DEPRECATION")
                bundle.getParcelable(ReadSmsConstant.EXTRA_STATUS )
            }
            when (status?.statusCode) {
                CommonStatusCodes.TIMEOUT -> {
                    // Service has timed out and no SMS message that meets the requirement is read. Service ended.
                    // doSomethingWhenTimeOut()
                    Log.w(TAG, "HuaweiOtpBroadcastReceiver timeout error, Service ended")
                }
                CommonStatusCodes.SUCCESS -> {

                    if (bundle.containsKey(ReadSmsConstant.EXTRA_SMS_MESSAGE)) {

                        // An SMS message that meets the requirement is read. Service ended.

                        bundle.getString(ReadSmsConstant.EXTRA_SMS_MESSAGE)?.let {

                            Log.d(TAG, it)
                            //send local broadcast to update UI
                            val local = Intent()
                            local.action = DEMO_ACTION_UPDATE_UI
                            local.putExtra(DEMO_EXTRA_SMS, it)
                            context!!.sendBroadcast(local)
                        }
                    }
                }
            }
        }
    }
}