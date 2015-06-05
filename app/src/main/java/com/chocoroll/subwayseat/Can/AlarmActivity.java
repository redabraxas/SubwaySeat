package com.chocoroll.subwayseat.Can;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chocoroll.subwayseat.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class AlarmActivity extends Activity {
    ImageView imgV;
    TextView alarmStationTv, rest, restTime, arriveTime;
    static int m_alarmType=0;
    int hour, min;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        TextView stationTv = (TextView)findViewById(R.id.arrStation);
        stationTv.setText("GlobalClass.endS.getName()");

        imgV = (ImageView)findViewById(R.id.bell);
        alarmStationTv = (TextView)findViewById(R.id.alarmStation);
        rest = (TextView)findViewById(R.id.rest);
        restTime = (TextView)findViewById(R.id.restTime);

        //nowTime =
        Button btn_play = (Button) findViewById(R.id.btn_play);

        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgV.setImageResource(R.drawable.bell);
                alarmStationTv.setTextColor(Color.RED);
                startService(new Intent("com.chocoroll.subwayseat.Can"));
            }
        });

        Button btn_stop = (Button) findViewById(R.id.btn_stop);
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgV.setImageResource(R.drawable.bell2);
                alarmStationTv.setTextColor(Color.BLACK);
                stopService(new Intent("com.chocoroll.subwayseat.Can"));
            }
        });


        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                String url = "http://bus.go.kr/getXml3.jsp?sstatn_id=1007000728&estatn_id=1007000726";
                new Load_Rest_Time().execute(url);
            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask, 0, 60000); // 0초후 실행, 1분마다 반복실행

    }

    private class Load_Rest_Time extends AsyncTask<String, Void, String>
    {
        protected String doInBackground(String... urls) {
            try {
                Log.d("boha1", urls[0]);
                return (String) downloadUrl((String) urls[0]);
            } catch (IOException e) {
                return "다운로드 실패";
            }
        }
        private String downloadUrl(String myurl) throws IOException {
            HttpURLConnection conn = null;
            try {
                Log.d("boha2", myurl);
                URL url = new URL(myurl);   // 입력된 url
                conn = (HttpURLConnection) url.openConnection(); // 리소스 연결
                BufferedInputStream buf = new BufferedInputStream(conn.getInputStream());   // byte단위로 저장
                BufferedReader bufreader = new BufferedReader(new InputStreamReader(buf, "utf-8")); // 문자 단위로 변환
                String line = null;
                String page = "";
                while ((line = bufreader.readLine()) != null) {   // 문자를 줄단위로
                    page += line;
                }
                Log.d("boha3", page);
                return page;
            } finally {
                conn.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result) {   // doInBackground가 끝나고
            String time = null;
            String tm = null;
            try {
                // 파서 생성
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(new StringReader(result));

                // 이벤트 가져오기
                int eventType = xpp.getEventType();
                boolean bSet = false;
                String tag = null;


                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_DOCUMENT) {
                        ;
                    } else if (eventType == XmlPullParser.START_TAG) {
                        String tag_name = xpp.getName();
                        // 태그가 name 혹은 address인 경우 set을 true로
                        if (tag_name.equals("shtTravelTm") || tag_name.equals("shtStatnCnt"))
                        {
                            bSet = true;
                            tag = tag_name;
                        }
                    } else if (eventType == XmlPullParser.TEXT) {
                        if (bSet) {
                            if (tag.equals("shtTravelTm"))
                                time = xpp.getText();
                            else
                                tm = xpp.getText();
                            bSet = false;
                        }
                    } else if (eventType == XmlPullParser.END_TAG) {
                        ;
                    }
                    eventType = xpp.next();
                }
            }
            catch (Exception e) {
                Log.e("Exception", e.getMessage());
            }
            rest.setText(tm);
            restTime.setText(time+"분");

            hour = Integer.valueOf(new SimpleDateFormat("HH").format(new Date(System.currentTimeMillis())));
            min = Integer.valueOf(new SimpleDateFormat("mm").format(new Date(System.currentTimeMillis())));
            min += Integer.valueOf(time);

            arriveTime = (TextView)findViewById(R.id.arriveTime);
            arriveTime.setText(hour + " : " + min);

        }
    }

    public void AlarmType(View v)
    {
        String[] alarmType = new String[]{"소리", "진동", "소리+진동", "무음", "소리"};
        TextView tv = (TextView)v;

        for(int i=0; i<alarmType.length; i++)
            if(tv.getText().equals(alarmType[i]))
            {
                tv.setText(alarmType[i+1]);
                m_alarmType += 1;
                if(m_alarmType == 4)    m_alarmType = 0;
                break;
            }
    }
}
