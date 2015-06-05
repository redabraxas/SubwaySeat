package com.chocoroll.subwayseat.Home;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.chocoroll.subwayseat.Adapter.StationAdatper;
import com.chocoroll.subwayseat.Can.CanActivity;
import com.chocoroll.subwayseat.GlobalClass;
import com.chocoroll.subwayseat.Model.Station;
import com.chocoroll.subwayseat.R;
import com.chocoroll.subwayseat.Retrofit.Retrofit;
import com.chocoroll.subwayseat.TrainInfo.TrainNumDialog;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends Activity {
    ArrayList<Station> stationList = new ArrayList<>();


    ArrayList<Station> stationList1 = new ArrayList<>();
    ArrayList<Station> stationList2 = new ArrayList<>();
    ArrayList<Station> stationList3 = new ArrayList<>();
    ArrayList<Station> stationList4 = new ArrayList<>();
    ArrayList<Station> stationList5 = new ArrayList<>();
    ArrayList<Station> stationList6 = new ArrayList<>();
    ArrayList<Station> stationList7 = new ArrayList<>();

    public static Context mContext;

    EditText editTextSearch;

    ListView listViewSearch;
    StationAdatper mAdatper;
    StationAdatper mSubAdatper;
    LinearLayout searchBox, mainBox;

    boolean flag;
    boolean backFlag = false;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading 중 입니다...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();

        mContext=this;
        getSubwayStation();


        TelephonyManager telephony = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String phoneID = telephony.getDeviceId();    //device id
        GlobalClass.phoneID = phoneID;



        searchBox = (LinearLayout) findViewById(R.id.searchBox);
        mainBox = (LinearLayout) findViewById(R.id.mainBox);


        editTextSearch = (EditText) findViewById(R.id.editTextSearch);
        editTextSearch.addTextChangedListener(filterTextWatcher);

        listViewSearch = (ListView) findViewById(R.id.listViewSearch);

        mAdatper = new StationAdatper(mContext, R.layout.model_station, stationList);
        mSubAdatper = new StationAdatper(mContext, R.layout.model_station, stationList);



        listViewSearch.setOnItemClickListener(onClickListItem);
        searchBox.setVisibility(View.GONE);

        dialog.dismiss();


        (findViewById(R.id.btnLast)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMyLastSeat();
            }
        });


    }



    private TextWatcher filterTextWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {



                if(flag){
                    mAdatper.getFilter().filter(s);
                }else{
                    mSubAdatper.getFilter().filter(s);
                }

        }

    };
    private AdapterView.OnItemClickListener onClickListItem = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {



            Station station = stationList.get(arg2);

            if(flag){
                ((EditText)findViewById(R.id.editStartStation)).setText(station.getName());
                editTextSearch.setText("");
                GlobalClass.startS = station;


            }else{
                ((EditText)findViewById(R.id.editEndStation)).setText(station.getName());
                editTextSearch.setText("");
                GlobalClass.endS = station;

                // 도착역은 저장해놓는다.
                SharedPreferences setting = getSharedPreferences("station", MODE_PRIVATE);
                SharedPreferences.Editor editor = setting.edit();

                editor.putString("name", station.getName());
                editor.putString("code", station.getCode());
                editor.putString("line", station.getLine());
                editor.putFloat("mapx", (float) station.getPosx());
                editor.putFloat("mpay", (float) station.getPosy());
                editor.commit();
            }

            searchBox.setVisibility(View.GONE);
            mainBox.setVisibility(View.VISIBLE);

            InputMethodManager im = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);
        }
    };



    public void btnClick(View view){

        switch (view.getId()){
            case R.id.editStartStation:
                listViewSearch.setAdapter(mAdatper);
                searchBox.setVisibility(View.VISIBLE);
                mainBox.setVisibility(View.GONE);
                flag = true;
                backFlag = true;
                break;
            case R.id.editEndStation:

                if(GlobalClass.startS != null){

                    String line = GlobalClass.startS.getLine();

                    if(line.equals("1")){
                        mSubAdatper = new StationAdatper(mContext, R.layout.model_station, stationList1);
                        GlobalClass.stationList.addAll(stationList1);
                    }else if(line.equals("2")){
                        mSubAdatper = new StationAdatper(mContext, R.layout.model_station, stationList2);
                        GlobalClass.stationList.addAll(stationList2);
                    }else if(line.equals("3")){
                        mSubAdatper = new StationAdatper(mContext, R.layout.model_station, stationList3);
                        GlobalClass.stationList.addAll(stationList3);
                    }else if(line.equals("4")){
                        mSubAdatper = new StationAdatper(mContext, R.layout.model_station, stationList4);
                        GlobalClass.stationList.addAll(stationList4);
                    }else if(line.equals("5")){
                        mSubAdatper = new StationAdatper(mContext, R.layout.model_station, stationList5);
                        GlobalClass.stationList.addAll(stationList5);
                    }else if(line.equals("6")){
                        mSubAdatper = new StationAdatper(mContext, R.layout.model_station, stationList6);
                        GlobalClass.stationList.addAll(stationList6);
                    }else if(line.equals("7")){
                        mSubAdatper = new StationAdatper(mContext, R.layout.model_station, stationList7);
                        GlobalClass.stationList.addAll(stationList7);
                    }

                    listViewSearch.setAdapter(mSubAdatper);
                    searchBox.setVisibility(View.VISIBLE);
                    mainBox.setVisibility(View.GONE);
                    flag = false;
                    backFlag= true;
                    break;

                }else{

                    Toast.makeText(MainActivity.this, "출발역 먼저 선택해주세요!", Toast.LENGTH_SHORT).show();
                }



            case R.id.btnOK:
                if(GlobalClass.startS != null && GlobalClass.endS != null){
                    TrainNumDialog dialog = new TrainNumDialog(MainActivity.this, GlobalClass.startS);
                    dialog.show();
                }else{
                    new AlertDialog.Builder(MainActivity.this).setTitle("실패")        // 제목 설정
                            .setMessage("출발역과 도착역을 정확히 선택해주세요")        // 메세지 설정
                            .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                // 확인 버튼 클릭시 설정
                                public void onClick(DialogInterface dialog, int whichButton) {
                                }
                            }).create().show();
                }
                break;
            case R.id.btnLast:
                break;
        }
    }


    void getSubwayStation(){

        String file = "station.json";
        String result = "";
        try {
            InputStream is = getAssets().open(file);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            result = new String(buffer, "utf-8");
            JSONArray jsonArray = new JSONArray(result);
            for(int i=0; i< jsonArray.length(); i++){
                JSONObject json = jsonArray.getJSONObject(i);
                // 이름
                String name = json.getString("STATION_NM");
                // 전철역 코드
                String code = json.getString("FR_CODE");
                // 라인
                String line = json.getString("LINE_NUM");
                // x좌표
                double posx = json.getDouble("XPOINT");
                // y좌표
                double posy = json.getDouble("YPOINT");

                stationList.add(new Station(name, code, line, posx, posy));
                if(line.equals("1")){
                    stationList1.add(new Station(name, code, line, posx, posy));
                }else if(line.equals("2")){
                    stationList2.add(new Station(name, code, line, posx, posy));
                }else if(line.equals("3")){
                    stationList3.add(new Station(name, code, line, posx, posy));
                }else if(line.equals("4")){
                    stationList4.add(new Station(name, code, line, posx, posy));
                }else if(line.equals("5")){
                    stationList5.add(new Station(name, code, line, posx, posy));
                }else if(line.equals("6")){
                    stationList6.add(new Station(name, code, line, posx, posy));
                }else if(line.equals("7")){
                    stationList7.add(new Station(name, code, line, posx, posy));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }






    void getMyLastSeat(){

        final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        dialog.setMessage("내 자리 정보를 가져오는 중입니다...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();


        final JsonObject info = new JsonObject();
        info.addProperty("phoneID", GlobalClass.phoneID);


        new Thread(new Runnable() {
            public void run() {
                try {

                    RestAdapter restAdapter = new RestAdapter.Builder()
                            .setEndpoint(Retrofit.ROOT)  //call your base url
                            .build();
                    Retrofit retrofit = restAdapter.create(Retrofit.class); //this is how retrofit create your api
                    retrofit.getMyLastSeat(info, new Callback<JsonObject>() {

                        @Override
                        public void success(JsonObject jsonObject, Response response) {

                            dialog.dismiss();

                            String result = (jsonObject.get("result")).getAsString();


                            // 내 정보가 있습니다.
                            if (result.equals("success")) {

                                GlobalClass.trainNum = (jsonObject.get("trainNum")).getAsString();
                                GlobalClass.trainXY = (jsonObject.get("trainCan")).getAsInt();
                                GlobalClass.trainSeat = (jsonObject.get("trainSeat")).getAsInt();

                                SharedPreferences setting = getSharedPreferences("station", MODE_PRIVATE);
                                String name = setting.getString("name", "");
                                String code = setting.getString("code", "");
                                String line = setting.getString("line", "");
                                double mapx = setting.getFloat("mapx", 0);
                                double mapy = setting.getFloat("mpay", 0);
                                GlobalClass.endS = new Station(name, code, line, mapx, mapy);

                                Intent intent = new Intent(MainActivity.this, CanActivity.class);
                                startActivity(intent);


                            } else if (result.equals("not_found")) {

                                new AlertDialog.Builder(MainActivity.this).setTitle("접근 불가")        // 제목 설정
                                        .setMessage("이전 기록이 없습니다.")        // 메세지 설정
                                        .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            // 확인 버튼 클릭시 설정
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                            }
                                        }).create().show();

                            } else {

                                new AlertDialog.Builder(MainActivity.this).setTitle("알 수 없는 오류")        // 제목 설정
                                        .setMessage("다시 시도해주세요.")        // 메세지 설정
                                        .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            // 확인 버튼 클릭시 설정
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                            }
                                        }).create().show();

                            }


                        }

                        @Override
                        public void failure(RetrofitError retrofitError) {
                            dialog.dismiss();
                            new AlertDialog.Builder(MainActivity.this).setTitle("네트워크가 불안정합니다.")        // 제목 설정
                                    .setMessage("네트워크를 확인해주세요")        // 메세지 설정
                                    .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        // 확인 버튼 클릭시 설정
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                        }
                                    }).create().show();

                        }
                    });
                }
                catch (Throwable ex) {

                }
            }
        }).start();


    }



    @Override
    public void onBackPressed(){

        if(backFlag){
            searchBox.setVisibility(View.GONE);
            mainBox.setVisibility(View.VISIBLE);
        }else{
            super.onBackPressed();
        }

    }



}
