package com.chocoroll.subwayseat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.chocoroll.subwayseat.Adapter.StationAdatper;
import com.chocoroll.subwayseat.Model.Station;
import com.chocoroll.subwayseat.TrainInfo.TrainNumDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;


public class MainActivity extends Activity {

    ArrayList<Station> stationList = new ArrayList<>();
    public static Context mContext;

    EditText editTextSearch;

    ListView listViewSearch;
    StationAdatper mAdatper;
    LinearLayout searchBox, mainBox;

    boolean flag;


    public UserInfo userInfo= new UserInfo();
    public UserInfo getUserInfo() { return userInfo; }

    public class UserInfo{
        Station startS, endS;
        String trainNum;
        int trainXY, seat;

        UserInfo(){

        }

        public void setTrainNum(String trainNum){ this.trainNum = trainNum; }
        public void setTrainXY(int trainXY){ this.trainXY = trainXY; }

        public void setEndStation(Station endS) {
            this.endS = endS;
        }

        public void setStartStation(Station startS) {
            this.startS = startS;
        }

        public void setSeat(int seat) {
            this.seat = seat;
        }

        public Station getStartS() {
            return startS;
        }

        public String getTrainNum() {
            return trainNum;
        }

        public int getSeat() {
            return seat;
        }

        public int getTrainXY() {
            return trainXY;
        }

        public Station getEndS() {
            return endS;
        }


    }




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


        searchBox = (LinearLayout) findViewById(R.id.searchBox);
        mainBox = (LinearLayout) findViewById(R.id.mainBox);


        editTextSearch = (EditText) findViewById(R.id.editTextSearch);
        editTextSearch.addTextChangedListener(filterTextWatcher);


        listViewSearch = (ListView) findViewById(R.id.listViewSearch);

        mAdatper = new StationAdatper(mContext, R.layout.model_station, stationList);


        listViewSearch.setAdapter(mAdatper);

        listViewSearch.setOnItemClickListener(onClickListItem);
        searchBox.setVisibility(View.GONE);

        dialog.dismiss();

    }



    private TextWatcher filterTextWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {

                mAdatper.getFilter().filter(s);

        }

    };
    private AdapterView.OnItemClickListener onClickListItem = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {



            Station station = stationList.get(arg2);

            if(flag){
                ((EditText)findViewById(R.id.editStartStation)).setText(station.getName());
                editTextSearch.setText("");
                userInfo.setStartStation(station);
            }else{
                ((EditText)findViewById(R.id.editEndStation)).setText(station.getName());
                editTextSearch.setText("");
                userInfo.setEndStation(station);
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
                searchBox.setVisibility(View.VISIBLE);
                mainBox.setVisibility(View.GONE);
                flag = true;
                break;
            case R.id.editEndStation:
                searchBox.setVisibility(View.VISIBLE);
                mainBox.setVisibility(View.GONE);
                flag = false;
                break;
            case R.id.btnOK:
                if(userInfo.getStartS() != null && userInfo.getEndS() != null){
                    TrainNumDialog dialog = new TrainNumDialog(MainActivity.this, userInfo.getStartS());
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
            }
        } catch (Exception e) {
            Log.e("result",e.getCause().toString());
        }


    }










}
