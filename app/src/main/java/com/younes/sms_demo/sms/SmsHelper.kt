package com.younes.sms_demo.sms

import android.content.Context
import kotlin.random.Random
import com.younes.sms_demo.MainActivity

object SmsHelper {

    /**
     * generate the content of OTP sms, includes the OTP and app Hash value
     *
     * changing value or format of returned string requires updating the parsing logic in [parseSmsContent]
     * called in [MainActivity.sendOtpSms]
     * @param context application context
     * @return sms message content to be sent to user phone number
     */
    fun generateSmsContent(context: Context): String {
        //generate Hash
        val hashValue = SmsHashManager().getHashValue(context)

        //generate Otp
        val otp = Random.nextInt(
            100000,
            999999
        ).toString()

        //generate sms content, OTP is the sixth word
        return "[#] Your demo verification code is $otp $hashValue"
    }

    /**
     * get OTP from SMS message
     *
     * @param smsContent received sms message
     * @return otp as string
     */
    fun parseSmsContent(smsContent:String ):String {
        /**
         * OTP is the sixth word in our sms message, see variable smsContent in [generateSmsContent]
         */
        return  smsContent.split(" ")[6]

    }
}