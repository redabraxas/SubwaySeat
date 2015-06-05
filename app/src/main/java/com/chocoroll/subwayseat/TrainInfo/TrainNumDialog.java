package com.chocoroll.subwayseat.TrainInfo;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.chocoroll.subwayseat.Adapter.TrainAdapter;
import com.chocoroll.subwayseat.GlobalClass;
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
    ListView listView;
    ArrayList<Train> trainListUp = new ArrayList<>();
    TrainAdapter trainAdapter;

    GetTrainInfoTask getTrainInfoTask;
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

        listView =(ListView) findViewById(R.id.listVIew);
        trainAdapter = new TrainAdapter(mContext, R.layout.model_train, trainListUp);
        listView.setAdapter(trainAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO 아이템 클릭시에 구현할 내용은 여기에.

                Train train = (Train) parent.getAdapter().getItem(position);
                GlobalClass.trainNum = train.getTrainNum();
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

        // 현재 실시간 열차 도착 정보를 알아온다.
        getTrainInfoTask = new GetTrainInfoTask(station.getCode(), station.getLine());
        getTrainInfoTask.execute((Void) null);
    }



    /**
     * 열차번호 AsyncTask
     */
    public class GetTrainInfoTask extends AsyncTask<Void, Void, Boolean> {

        JSONArray trainArray;

        private final String urlPath;

        GetTrainInfoTask(String stationCode, String lineCode) {
            urlPath = "http://m.bus.go.kr/mBus/subway/getArvlByInfo.bms?statnId=100"+lineCode+"00"+stationCode+"&subwayId=100"+lineCode;
            // urlPath="http://openapi.seoul.go.kr:8088/sample/json/SearchArrivalInfoByFRCodeService/1/2/"+stationCode+"/"+direction+"/"+day+"/";

            //urlPath = "http://m.bus.go.kr/mBus/subway/getArvlByInfo.bms?statnId=+"+lineCode+"000212&subwayId=1002";
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
                    setTrainInfo(trainArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

        }

        @Override
        protected void onCancelled() {
            progressDialog.dismiss();
            getTrainInfoTask = null;
        }
    }


    void setTrainInfo(JSONArray trainArray) throws JSONException {

        getTrainInfoTask = null;

        if (trainArray != null) {
            for (int i = 0; i < trainArray.length(); i++) {

                JSONObject insideObject = trainArray.getJSONObject(i);

                String num = insideObject.getString("bTrainNo");  // 열차 번호
                String time = insideObject.getString("arvlMsg2"); // 도착시간
                String dst = insideObject.getString("trainLineNm"); // 행선지


                Train temp = new Train(num, time, dst);
                trainListUp.add(temp);

            }

            listView.setAdapter(trainAdapter);



        }
    }

}
