# Huawei SMS retriever demo

Simple demo that automatically fills in an OTP code from SMS message, based on Huawei SMS Retriever API.

***The SMS is sent directly from the device using your sim card for demonstration purposes, in production environment the SMS should be sent from server side, therefore you do not need to include or request the permission `Manifest.permission.SEND_SMS`.***

## demo
 
https://user-images.githubusercontent.com/36759976/233957239-6eb918ac-d60b-4fd3-8aec-3736549bacc3.mp4
 
if the above video link is broken, then download it from here: [https://github.com/megaacheyounes/hms-sms-demo/blob/master/screenshot/demo.mp4](https://github.com/megaacheyounes/hms-sms-demo/blob/master/screenshot/demo.mp4)

## run

1. clone the project `git clone https://github.com/megaacheyounes/hms-sms-demo.git`
2. open project in Android studio (a version that support Gradle 7)
3. run the app on an HMS device with a working sim card

## Notes

- You have to use a device with HMS core and a SIM card for testing
- Charges may apply when sending an SMS
- You can send an SMS to the same number/same device 
- Configure ProGuard if necessary: <https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides-V5/config-obfuscation-script-0000001056835760-V5>

## References

- Android `SmsManager`: <https://developer.android.com/reference/android/telephony/SmsManager#getDefault()>
- Huawei `ReadSmsManager`: <https://developer.huawei.com/consumer/en/doc/development/HMSCore-References/account-support-sms-readsmsmanager-0000001050050553#EN-US_TOPIC_0000001050050553__section2074916403014>
- Huawei read SMS documentation: <https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides-V5/readsmsmanager-0000001050050861-V5>

## disclaimer

This repo includes the file `agconnect-services.json` for educational purposes only, you are not allowed to use it in other projects or distribute it in anyway.
