/**
 * Copyright Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */package com.divetime.firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.divetime.BuildConfig;
import com.divetime.LandingActivity;
import com.divetime.R;
import com.divetime.login.LoginActivity;
import com.divetime.utils.Constants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFMService";

    String from_user_id;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String msg="";
        String push_name="";


        /*Bundle[{google.sent_time=1499248983801, notification_time=2017-07-05 10:03:03, from_user_id=,
        from=524844180533, type=Event, badge=1, google.message_id=0:1499248983806998%45f2d88ef9fd7ecd,
        message=new test event 2, senderid=833710522226, form_type=custom}]*/


        if (remoteMessage == null)
            return;
        if(remoteMessage.getData()!=null) {
            Log.d(TAG, "Notification Message Body: " + remoteMessage.getData().toString());

            if (remoteMessage.getData().size() > 0) {
                Log.e(TAG, "FooterCountData Payload: " + remoteMessage.getData().toString());
                Log.e(TAG, "remoteMessage: " + remoteMessage.toString());

                try {
                    from_user_id=remoteMessage.getData().get("from_user_id");
                    push_name=remoteMessage.getData().get("type");
                    String message = remoteMessage.getData().get("message");
//                    String badge = remoteMessage.getData().get("badge");
//                    String senderid = remoteMessage.getData().get("senderid");

                    int push_id=0;
//                    push_id=Integer.parseInt(remoteMessage.getData().get("id"));

                    sendNotification(message,push_id,push_name);

                } catch (Exception e) {
                    Log.e(TAG, "Exception: " + e.getMessage());
                    sendNotification("You have new notification",1,push_name);//just to ensure push is received
                }
            }
            else if(remoteMessage.getNotification().getBody()!=null){
                sendNotification(remoteMessage.getNotification().getBody(),0,push_name);
            }

            //Calling method to generate notification
        }
    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(String messageBody,int id,String type) {

        Intent intent;

        if(Constants.getLoggedinStatus(this))
        {
            intent = new Intent(this, LandingActivity.class);
            intent.putExtra("type",type);
        }
        else{
            intent = new Intent(this, LoginActivity.class);
            intent.putExtra("type",type);
        }


        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, id, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder;
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
             notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.icon_transparent)
                    .setContentTitle("DiveTime")
                    .setContentText(messageBody)
                    .setColor(getResources().getColor(R.color.dive_app_blue))
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);
        }
        else{
            notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.icon_transparent)
                    .setContentTitle("DiveTime")
                    .setContentText(messageBody)
//                    .setColor(getResources().getColor(R.color.dive_app_blue))
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(id, notificationBuilder.build());

        Intent intent2 = new Intent("fcm_push");

        //put whatever data you want to send, if any
        intent2.putExtra("type", type);
        intent2.putExtra("from_user_id", from_user_id);

        //send broadcast
        sendBroadcast(intent2);
    }


    private void storeBadgesCount(int count,String type)
    {
        SharedPreferences.Editor editor = getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE).edit();

        if(type.equalsIgnoreCase("connection_request") || type.equalsIgnoreCase("connection_request_accept")){

        editor.putInt(Constants.strShPrefConnectionBadges, count);
        editor.commit();
        }
        else if(type.equalsIgnoreCase("meetup_request") || type.equalsIgnoreCase("meetup_request_accept") || type.equalsIgnoreCase("meetup_comment")){

            editor.putInt(Constants.strShPrefMeetupBadges, count);
            editor.commit();
        }
        else if(type.equalsIgnoreCase("bulletin_comment")){

            editor.putInt(Constants.strShPrefBulletinBadges, count);
            editor.commit();
        }
        else if(type.equalsIgnoreCase("messaging")){

            editor.putInt(Constants.strShPrefChatupBadges, count);
            editor.commit();
        }
    }
}
