package com.headerinteractive.notificationapp;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ibm.mce.sdk.api.fcm.FcmApi;

public class SampleFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if(FcmApi.isFcmMessage(remoteMessage)) {
            FcmApi.handleMceFcmMessage(getApplicationContext(), remoteMessage);
        }
    }
}