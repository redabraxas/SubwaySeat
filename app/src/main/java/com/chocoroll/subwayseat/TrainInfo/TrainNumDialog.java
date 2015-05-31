package com.chocoroll.subwayseat.TrainInfo;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.chocoroll.subwayseat.Adapter.TrainAdapter;
import com.chocoroll.subwayseat.MainActivity;
import com.chocoroll.subwayseat.Model.Station;
import com.chocoroll.subwayseat.Model.Train;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by RA on 2015-05-29.
 */
public class TrainNumDialog extends Dialog{

    ProgressDialog progressDialog;
    ListView listViewUp, listViewDown;
    ArrayList<Train> trainListUp = new ArrayList<>();
    ArrayList<Train>  trainListDown = new ArrayList<>();
    TrainAdapter upAdapter, downAdatper;

    GetTrainInfoTask upTask, downTask;
    Station station;

    Context mContext;

    public TrainNumDialog(Context context, Station station) {
        super(context);
        mContext = context;
        this.station = station;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_train_num);
        this.setTitle("열차 선택");

        listViewUp=(ListView) findViewById(R.id.listViewUp);
        listViewDown=(ListView) findViewById(R.id.listViewDown);
        upAdapter = new TrainAdapter(mContext, R.layout.model_train, trainListUp);
        downAdatper = new TrainAdapter(mContext, R.layout.model_train, trainListDown);
        listViewUp.setAdapter(upAdapter);
        listViewDown.setAdapter(downAdatper);

        listViewUp.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO 아이템 클릭시에 구현할 내용은 여기에.

                Train train = (Train) parent.getAdapter().getItem(position);
                ((MainActivity)MainActivity.mContext).userInfo.setTrainNum(train.getTrainNum());
                dismiss();
                TrainXyDialog dialog = new TrainXyDialog(mContext);
                dialog.show();
            }

        });


        listViewDown.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO 아이템 클릭시에 구현할 내용은 여기에.

                Train train = (Train) parent.getAdapter().getItem(position);
                ((MainActivity)MainActivity.mContext).userInfo.setTrainNum(train.getTrainNum());
                dismiss();
                TrainXyDialog dialog = new TrainXyDialog(mContext);
                dialog.show();
            }

        });




        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("열차 리스트를 받아오는 중입니다...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();


        // 오늘 날짜

        int day;
        Calendar cal= Calendar.getInstance ( );
        int day_of_week = cal.get ( Calendar.DAY_OF_WEEK );
        switch(day_of_week) {
            case 1:
                day = 3;   // 일요일 및 공휴일
                break;
            case 7:
                day = 2;   // 토요일
                break;
            default:
                day = 1;   // 평일
        }

        // 상행/외선
        upTask = new GetTrainInfoTask(station.getCode(), String.valueOf(1), String.valueOf(day));
        upTask.execute((Void) null);

        // 하행/내선
        downTask = new GetTrainInfoTask(station.getCode(), String.valueOf(2), String.valueOf(day));
        downTask.execute((Void) null);
    }



    /**
     * 열차번호 AsyncTask
     */
    public class GetTrainInfoTask extends AsyncTask<Void, Void, Boolean> {

        String direction;
        JSONArray trainArray;

        private final String urlPath;
        GetTrainInfoTask(String stationCode, String direction, String day) {
            this.direction =direction;
            urlPath = "http://m.bus.go.kr/mBus/subway/getArvlByInfo.bms?statnId=1002000212&subwayId=1002";
            // urlPath="http://openapi.seoul.go.kr:8088/sample/json/SearchArrivalInfoByFRCodeService/1/2/"+stationCode+"/"+direction+"/"+day+"/";
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            DefaultHttpClient client = new DefaultHttpClient();

            // 객체 연결 설정 부분, 연결 최대시간 등등
            HttpParams param = client.getParams();
            HttpConnectionParams.setConnectionTimeout(param, 5000);
            HttpConnectionParams.setSoTimeout(param, 5000);

            try{

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
                BufferedReader br = new BufferedReader(new InputStreamReader(contentStream, "UTF-8"));

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
                Log.e("result",str);

                String line = null;

                // 원격에서 읽어온 데이터로 JSON 객체 생성
                JSONObject object = new JSONObject(str);
                //JSONObject object2 = new JSONObject(object.getString("SearchArrivalInfoByFRCodeService"));


                trainArray = new JSONArray(object.getString("resultList"));

                // "row" 배열로 구성 되어있으므로 JSON 배열생성
                //trainArray = new JSONArray(object2.getString("row"));




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
            progressDialog.dismiss();

            if(success)
                try {
                    setTrainInfo(direction, trainArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

        }

        @Override
        protected void onCancelled() {
            progressDialog.dismiss();
            if (direction.equals(1)) {
                upTask = null;
            } else {
                //하행일 대
                downTask = null;
            }
        }
    }


    void setTrainInfo(String direction, JSONArray trainArray) throws JSONException {
        // 상행일 때
        if (direction.equals("1")) {
            upTask = null;

            if (trainArray != null) {
                for (int i = 0; i < trainArray.length(); i++) {

                    JSONObject insideObject = trainArray.getJSONObject(i);

//                    String num = insideObject.getString("TRAINCODE");  // 열차 번호
//                    String time = insideObject.getString("ARRIVETIME"); // 도착시간
//                    String dst = insideObject.getString("SUBWAYNAME"); // 종착역

                    String num = insideObject.getString("bTrainNo");  // 열차 번호
                    String time = insideObject.getString("arvlMsg2"); // 도착시간
                    String dst = insideObject.getString("statnNm"); // 종착역


                    Train temp = new Train(num, time, dst);
                    trainListUp.add(temp);

                }

                listViewUp.setAdapter(upAdapter);
            }


        } else {
            //하행일 때
            downTask = null;


            if (trainArray != null) {
                for (int i = 0; i < trainArray.length(); i++) {

                    JSONObject insideObject = trainArray.getJSONObject(i);

                    String num = insideObject.getString("TRAINCODE");  // 열차 번호
                    String time = insideObject.getString("ARRIVETIME"); // 도착시간
                    String dst = insideObject.getString("SUBWAYNAME"); // 종착역

                    Train temp = new Train(num, time, dst);
                    trainListDown.add(temp);

                }

                listViewDown.setAdapter(downAdatper);
            }
        }

    }

}
