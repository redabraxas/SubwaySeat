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

import com.chocoroll.subwayseat.GlobalClass;
import com.chocoroll.subwayseat.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class AlarmActivity extends Activity {
    ImageView imgV;
    TextView alarmStationTv, rest, restTime, arriveTime, arriveInfo;
    static int m_alarmType=0;
    int hour=0, min=0;
    GetTrainInfoTask getTrainInfoTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        m_alarmType = 0;

        TextView stationTv = (TextView)findViewById(R.id.arrStation);
        stationTv.setText(GlobalClass.endS.getName() + " 역");

        imgV = (ImageView)findViewById(R.id.bell);
        rest = (TextView)findViewById(R.id.rest);
        restTime = (TextView)findViewById(R.id.restTime);
        alarmStationTv = (TextView)findViewById(R.id.alarmStation);
        alarmStationTv.setText(GlobalClass.endS.getName() + " 역");
        arriveInfo = (TextView)findViewById(R.id.arriveInfo);

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

        // 실사간 열차 역에 따라서
        // 도착역까지 남은 시간 및 남은 정거장 수 가져옴
        String url = "http://bus.go.kr/getXml3.jsp?sstatn_id=100"+GlobalClass.startS.getLine()+"000"+GlobalClass.startS.getCode()+"&estatn_id=100"+GlobalClass.endS.getLine()+"000"+GlobalClass.endS.getCode();
        new Load_Rest_Time().execute(url);

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                // 현재 실시간 열차 도착 정보를 알아온다.
                getTrainInfoTask = new GetTrainInfoTask(GlobalClass.endS.getCode(), GlobalClass.endS.getLine());
                getTrainInfoTask.execute((Void) null);
            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask, 0, 10000); // 0초후 실행, 1분마다 반복실행
    }

    private class Load_Rest_Time extends AsyncTask<String, Void, String>
    {
        protected String doInBackground(String... urls) {
            try {
                return (String) downloadUrl((String) urls[0]);
            } catch (IOException e) {
                return "다운로드 실패";
            }
        }
        private String downloadUrl(String myurl) throws IOException {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(myurl);   // 입력된 url
                conn = (HttpURLConnection) url.openConnection(); // 리소스 연결
                BufferedInputStream buf = new BufferedInputStream(conn.getInputStream());   // byte단위로 저장
                BufferedReader bufreader = new BufferedReader(new InputStreamReader(buf, "utf-8")); // 문자 단위로 변환
                String line = null;
                String page = "";
                while ((line = bufreader.readLine()) != null) {   // 문자를 줄단위로
                    page += line;
                }
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
            rest.setText(tm);             // 남은 정거장 수
            restTime.setText(time+"분"); // 예상 소요 시간

            // 현재 시간 가져오기
            hour = Integer.valueOf(new SimpleDateFormat("HH").format(new Date(System.currentTimeMillis())));
            min = Integer.valueOf(new SimpleDateFormat("mm").format(new Date(System.currentTimeMillis())));
            min += Integer.valueOf(time);   // 현재시간 + 예상 소요시간

            if(min > 60)
            {
                hour ++;
                min -= 60;
            }

            // 예상 도착시간
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

    public class GetTrainInfoTask extends AsyncTask<Void, Void, Boolean> {
        JSONArray trainArray;
        private final String urlPath;

        GetTrainInfoTask(String stationCode, String lineCode) {
            // 도착역의 정보 받아오기
            urlPath = "http://m.bus.go.kr/mBus/subway/getArvlByInfo.bms?statnId=100" + lineCode + "000" + stationCode + "&subwayId=100" + lineCode;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            DefaultHttpClient client = new DefaultHttpClient();

            // 객체 연결 설정 부분, 연결 최대시간 등등
            HttpParams param = client.getParams();
            HttpConnectionParams.setConnectionTimeout(param, 5000);
            HttpConnectionParams.setSoTimeout(param, 5000);

            try {
                InputStream contentStream = null;
                try {
                    // HttpClient 를 사용해서 주어진 URL에 대한 입력 스트림을 얻는다.
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpResponse response = httpclient.execute(new HttpGet(urlPath));
                    contentStream = response.getEntity().getContent();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // 입력스트림을 "UTF-8" 를 사용해서 읽은 후, 라인 단위로 데이터를 읽을 수 있는 BufferedReader 를 생성한다.
                BufferedReader br = new BufferedReader(new InputStreamReader(contentStream, "EUC-KR"));

                // 읽은 데이터를 저장한 StringBuffer 를 생성한다.
                StringBuffer sb = new StringBuffer();

                try {
                    // 라인 단위로 읽은 데이터를 임시 저장한 문자열 변수 line
                    String line = null;

                    // 라인 단위로 데이터를 읽어서 StringBuffer 에 저장한다.
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String str = sb.toString();

                // 원격에서 읽어온 데이터로 JSON 객체 생성
                JSONObject object = new JSONObject(str);
                trainArray = new JSONArray(object.getString("resultList"));
                return true;

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success)
                try {
                    temp(trainArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }

        @Override
        protected void onCancelled() {
            getTrainInfoTask = null;
        }
    }

    public void temp(JSONArray jsonArray) throws JSONException {
        getTrainInfoTask = null;

        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject insideObject = jsonArray.getJSONObject(i);

                String time = insideObject.getString("arvlMsg2"); // 도착시간
                String num = insideObject.getString("bTrainNo");  // 열차 번호
                double gpsX = insideObject.getDouble("gpsX");
                double gpsY = insideObject.getDouble("gpsY");

                GlobalClass.endS.setPosx(gpsX);
                GlobalClass.endS.setPosy(gpsY);

//                String[] str = time.split("\\(");
//                time = str[0];
//                String[] str2 = str[1].split("\\)");

                // 비교
                if(num.equals(GlobalClass.trainNum))
                {
                    arriveInfo.setText("도착역 근처 입니다.\n");
                    arriveInfo.append(time);
                    //arriveInfo.append(str[0]);
                }
            }
        }
    }
}