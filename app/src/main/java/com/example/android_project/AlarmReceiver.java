package com.example.android_project;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // 팝업 알림을 생성합니다.
        Notification.Builder builder = new Notification.Builder(context)
                .setContentTitle("Check time")
                .setContentText("챙기지 않은 물건이 있습니다. 꼭 확인하세요!")
                .setSmallIcon(R.drawable.logo);

        // NotificationManager를 사용하여 알림을 표시합니다.
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Android 8.0 (API 레벨 26) 이상에서는 NotificationChannel을 사용하여 알림을 관리합니다.
                NotificationChannel channel = new NotificationChannel("channel_id", "channel_name", NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
                builder.setChannelId(channel.getId());
            }
            notificationManager.notify(0, builder.build());
        }
    }
}