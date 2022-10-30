package com.example.bookworm.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.bookworm.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.net.MalformedURLException;

//싱글톤으로 구성

public class MyFCMService extends FirebaseMessagingService {
    private static MyFCMService myFCMService = new MyFCMService();
    final String CHANNEL_ID = "ChannerID";
    final String CHANNEL_NAME = "ChannerName";
    final String CHANNEL_DESCRIPTION = "ChannerDescription";


    // 클라우드 서버에 등록되면 호출
    // token = applicaion PKkey
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        //token을 서버로 전송
    }

    //인스턴스 생성
    public static MyFCMService getInstance(){
        return myFCMService;
    }

    // 클라우드 서버에서 메시지 전송하면 호출
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // remote message가 메시지 가짐

        //remoteMessage는 getFrom(), getNotification()등의 메서드를 호출해서 메시지에 대한 정보를 얻을 수 있는데,
        //getNotification()는 getTitle(), getBody()로 타이틀과 내용을 얻을 수 있다.

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

        NotificationCompat.Builder builder;
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
                .setSmallIcon(R.drawable.icon_bookworm);

        Notification notification = builder.build();
        notificationManager.notify(1, notification);
    }

    //FCM 메시지
    public void sendPostToFCM(Context context, final String fcmtoken, final String message) throws MalformedURLException {
        SupportFCMServiceImpl service = new SupportFCMServiceImpl(context);
        service.sendMessage(fcmtoken, message);
    }
}

