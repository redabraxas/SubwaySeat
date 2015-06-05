package com.chocoroll.subwayseat.Can;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.IBinder;

import com.chocoroll.subwayseat.R;

public class ServiceClass extends Service {
    private MediaPlayer mPlayer = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        //Log.d("slog", "onStart()");
        super.onStart(intent, startId);
        // raw 폴더에 재생할 mp3
        mPlayer = MediaPlayer.create(this, R.raw.facebook);

        CountDownTimer ctimer;
        ctimer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }
            @Override
            public void onFinish() {
                if((AlarmActivity.m_alarmType != 1) && (AlarmActivity.m_alarmType != 3))
                    mPlayer.start();

                // 생성
                Notification.Builder builder = new Notification.Builder(getApplicationContext());

                // builder setting
                builder.setSmallIcon(R.mipmap.ic_launcher);
                builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
                builder.setTicker("알람");
                builder.setContentTitle("알람");
                builder.setContentText("일어나세요!!!");
                if((AlarmActivity.m_alarmType == 1) || (AlarmActivity.m_alarmType == 2))
                    builder.setVibrate(new long[]{0,1000});
                builder.setAutoCancel(true);

                //PandingIntent 생성 및 빌더에 setting
                Intent i = new Intent(getApplicationContext(), AlarmActivity.class);
                PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), 1, i, PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(pIntent);

                // 알람 빌드(생성) 후, 매니저 연결
                Notification notification = builder.build();
                notification.flags = notification.flags|Notification.FLAG_ONGOING_EVENT;
                startForeground(1000, notification);
            }
        };
        ctimer.start();
    }


    @Override
    public void onDestroy() {
        //Log.d("slog", "onDestroy()");
        mPlayer.stop();
        super.onDestroy();
    }
}