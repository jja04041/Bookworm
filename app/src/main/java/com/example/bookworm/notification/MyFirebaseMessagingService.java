package com.example.bookworm.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.bookworm.R;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    final String CHANNEL_ID = "ChannerID";
    final String CHANNEL_NAME = "ChannerName";
    final String CHANNEL_DESCRIPTION = "ChannerDescription";

    private static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
    private static final String SERVER_KEY = "AAAAgMY8NzM:APA91bHQsklAUmUfvWkKcu-joUx_HcGTI5g4l_qQDRbfoBuMWi2OpjHh7zQBDU_rJaJpQc_wcD1qw9ADobR5s65lOtzduBPTAwDj4N_aNrF2NT_iOQOIRpUHWpUmvRFULH7wBwmHL6BP";


    // 클라우드 서버에 등록되면 호출
    // token = applicaion PKkey
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        //token을 서버로 전송
    }


    // 클라우드 서버에서 메시지 전송하면 호출
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // remote message가 메시지 가짐

        //remoteMessage는 getFrom(), getNotification()등의 메서드를 호출해서 메시지에 대한 정보를 얻을 수 있는데,
        //getNotification()는 getTitle(), getBody()로 타이틀과 내용을 얻을 수 있다.

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

        NotificationCompat.Builder builder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }
            builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(getApplicationContext());
        }

        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();

        builder.setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_launcher_background);

        Notification notification = builder.build();
        notificationManager.notify(1, notification);
    }

    public void sendPostToFCM(final String fcmtoken, final String message, FirebaseDatabase mFirebaseDatabase) {

        // honeycomb sdk 이상에서는 Main thread 에서 네트워킹을 실행할 수 없다 (Network On Main Thread exception 호출)

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    // json 형식으로  fmc 메시지 만들어준다
                    JSONObject root = new JSONObject();
                    JSONObject notification = new JSONObject();
                    notification.put("body", message);
                    notification.put("title", "BOOKWORM");
                    root.put("notification", notification);
                    root.put("to", fcmtoken);
                    // FMC 메시지 생성 end

                    // url와 firebase serverkey로 네트워크 연결
                    URL Url = new URL(FCM_MESSAGE_URL);
                    HttpURLConnection conn = (HttpURLConnection) Url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.addRequestProperty("Authorization", "key=" + SERVER_KEY);
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setRequestProperty("Content-type", "application/json");
                    OutputStream os = conn.getOutputStream();
                    os.write(root.toString().getBytes("utf-8"));
                    os.flush();
                    conn.getResponseCode();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }
}
