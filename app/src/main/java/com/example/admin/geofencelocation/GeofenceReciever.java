package com.example.admin.geofencelocation;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ADMIN on 4/19/2017.
 */
public class GeofenceReciever extends BroadcastReceiver {


    private static final String TAG = "BootReceiver";
    Context contextBootReceiver;
    Intent intent;
    public static final int GEOFENCE_NOTIFICATION_ID = 0;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    ArrayList<String> names;
    ArrayList<Integer> status;

    ArrayList<String> geofenceswitches = new ArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(GeofenceReciever.class.getSimpleName(), "XXXXXXXXXXXXXXXXXXXXXXXXXXXX!");
        this.contextBootReceiver = context;
        this.intent = intent;
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        editor = pref.edit();




        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        //Handling errors
        if (geofencingEvent.hasError()) {
            String errorMsg = getErrorString(geofencingEvent.getErrorCode());
            Log.e(TAG, errorMsg);
            return;
        }

        int geoFenceTransition = geofencingEvent.getGeofenceTransition();
        // Check if the transition type is of interest
        if (geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            // Get the geofence that were triggered
            Log.d("Trnsition", "Entered");
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            String geofenceTransitionDetails = getGeofenceTrasitionDetails(geoFenceTransition, triggeringGeofences);
            // Send notification details as a String
            //if (feedback.equalsIgnoreCase("F;1;")) {
            //  String temp = feedback;


            sendNotification(geofenceTransitionDetails);


        }



    }


    private String getGeofenceTrasitionDetails(int geoFenceTransition, List<Geofence> triggeringGeofences) {
        // get the ID of each geofence triggered
        ArrayList<String> triggeringGeofencesList = new ArrayList<>();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesList.add(geofence.getRequestId());
        }

        String status = "Entering into Geofence";
        if (geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER)
            System.out.println(status);
        return status + TextUtils.join(", ", triggeringGeofencesList);
    }

    private void sendNotification(String msg) {


        Log.i(TAG, "sendNotification: " + msg);


        // Intent to start the main Activity
        Intent notificationIntent = MainActivity.makeNotificationIntent(
                contextBootReceiver, msg
        );

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(contextBootReceiver);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent notificationPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


        // Creating and sending Notification
        NotificationManager notificatioMng =
                (NotificationManager) contextBootReceiver.getSystemService(Context.NOTIFICATION_SERVICE);
        notificatioMng.notify(
                GEOFENCE_NOTIFICATION_ID,
                createNotification(msg, notificationPendingIntent));

    }

    // Create notification
    private Notification createNotification(String msg, PendingIntent notificationPendingIntent) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(contextBootReceiver);
        notificationBuilder
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(Color.RED)
                .setContentTitle(msg)
                .setContentText("My Notification!")
                .setContentIntent(notificationPendingIntent)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                .setAutoCancel(true);
        return notificationBuilder.build();
    }


    private static String getErrorString(int errorCode) {
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "GeoFence not available";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "Too many GeoFences";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "Too many pending intents";
            default:
                return "Unknown error.";
        }
    }


}
