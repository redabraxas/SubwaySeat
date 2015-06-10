package com.chocoroll.subwayseat.Can;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import com.chocoroll.subwayseat.GlobalClass;
import com.chocoroll.subwayseat.R;

import java.util.Timer;
import java.util.TimerTask;

public class ServiceClass extends Service {
    private MediaPlayer mPlayer = null;
    Notification notification;
    private GpsInfo gps;
    private Handler mHandler;
    Timer timer;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        mHandler = new Handler();
        super.onStart(intent, startId);
        // raw 폴더에 재생할 mp3
        mPlayer = MediaPlayer.create(this, R.raw.facebook);

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                // 현재 열차 위치를 알아온다.
                gps = new GpsInfo(ServiceClass.this);

                // GPS 사용유무 가져오기
                double now_y = 0, now_x = 0;
                if (gps.isGetLocation()) {
                    now_y = gps.getLatitude();
                    now_x = gps.getLongitude();
                    //mHandler.post(new ToastRunnable("당신의 위치 - \nx: " + now_x + "\ny: " + now_y));
                } else {
                    // GPS 를 사용할수 없으므로
                    gps.showSettingsAlert();
                }

                // 비교
                double endS_x = GlobalClass.endS.getPosx();     // 도착역
                double endS_y = GlobalClass.endS.getPosy();
                double dist = calDistance(now_x, now_y, endS_x, endS_y);
                mHandler.post(new ToastRunnable("거리 : " + dist));
                //if(dist < 800)     // 거리는 미터 단위로 나옴
                    alarmStart();
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, 0, 10000); // 0초후 실행, 10초마다 반복실행
    }


    public void alarmStart()
    {
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
        PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), 0, i, 0);
        builder.setContentIntent(pIntent);

        // 알람 빌드(생성) 후, 매니저 연결
        notification = builder.build();
        notification.flags = notification.flags|Notification.FLAG_ONGOING_EVENT;
        startForeground(100, notification);
    }

    public double calDistance(double lat1, double lon1, double lat2, double lon2){

        double theta, dist;
        theta = lon1 - lon2;
        dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);

        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;    // 단위 mile 에서 km 변환.
        dist = dist * 1000.0;      // 단위  km 에서 m 로 변환

        return dist;
    }

    // 주어진 도(degree) 값을 라디언으로 변환
    private double deg2rad(double deg){
        return (double)(deg * Math.PI / (double)180d);
    }

    // 주어진 라디언(radian) 값을 도(degree) 값으로 변환
    private double rad2deg(double rad){
        return (double)(rad * (double)180d / Math.PI);
    }

    private class ToastRunnable implements Runnable {
        String mText;

        public ToastRunnable(String text) {
            mText = text;
        }

        @Override
        public void run(){
            Toast.makeText(getApplicationContext(), mText, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        //Log.d("slog", "onDestroy()");
        mPlayer.stop();
        timer.cancel();
        super.onDestroy();
    }
}