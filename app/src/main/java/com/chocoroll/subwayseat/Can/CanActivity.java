package com.chocoroll.subwayseat.Can;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chocoroll.subwayseat.Model.Seat;
import com.chocoroll.subwayseat.Retrofit.Retrofit;
import com.chocoroll.subwayseat.GlobalClass;
import com.chocoroll.subwayseat.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CanActivity extends Activity {

    Button btn[] = new Button[42];


    // 한 칸의 자리 리스트
    public static ArrayList<Seat> seatList = new ArrayList<Seat>();
    int seatCount = 7; // 한 줄의 자리 개수  :: 여기서는 7


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_can);

        // 칸 이동 버튼 셋팅
        ((ImageView)findViewById(R.id.btn_allowTOP)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {


                if (GlobalClass.trainXY == 1) {

                    Toast toast = Toast.makeText(CanActivity.this, "첫번째 칸입니다!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    GlobalClass.trainXY--;
                    getSubwaySeat();

                }

            }

        });


        ((ImageView)findViewById(R.id.btn_allowBOTTOM)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (GlobalClass.trainXY == 9) {
                    Toast toast = Toast.makeText(CanActivity.this, "마지막 칸입니다!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    GlobalClass.trainXY++;
                    getSubwaySeat();

                }
            }

        });

        ((Button)findViewById(R.id.btnStand)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // 서있는건 태그값이 1000
                addSeat((Button)view);
            }

        });

        // 알람, 커뮤니티
        ((Button)findViewById(R.id.btnAlarm)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent = new Intent(CanActivity.this, AlarmActivity.class);
                startActivity(intent);
            }

        });

        ((Button)findViewById(R.id.btnChat)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent = new Intent(CanActivity.this, PostActivity.class);
                startActivity(intent);
            }

        });




        getSubwaySeat();
    }


    void setSeatView(){



        final ProgressDialog dialog = new ProgressDialog(CanActivity.this);
        dialog.setMessage("자리정보를 셋팅하는 중입니다...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();



        // 현재 칸 번호 출력
        ((TextView) findViewById(R.id.txt_trainXY)).setText(String.valueOf(GlobalClass.trainXY));

        // 칸의 자리 뷰 셋팅
        LinearLayout linear_left = (LinearLayout) findViewById(R.id.linearLayout_left);
        LinearLayout linear_right = (LinearLayout) findViewById(R.id.linearLayout_right);

        linear_left.removeAllViews();
        linear_right.removeAllViews();


        // 구별 바
        ImageView imageView = new ImageView(CanActivity.this);
        imageView.setImageResource(R.drawable.can_bar);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(500,200));
        linear_left.addView(imageView);

        ImageView imageView2 = new ImageView(CanActivity.this);
        imageView2.setImageResource(R.drawable.can_bar);
        imageView2.setLayoutParams(new LinearLayout.LayoutParams(500,200));
        linear_right.addView(imageView2);


        int temp=0;
        for(int j=0; j<3; j++){


            for (int i =temp; i < temp+seatCount*2; i++) {
                btn[i] = new Button(CanActivity.this);

                btn[i].setLayoutParams(new LinearLayout.LayoutParams(300,100));
                btn[i].setTextSize(11);
                btn[i].setId(i);

                btn[i].setBackground(getResources().getDrawable(R.drawable.can_seat));

                if (i % 2 == 0) {
                    linear_left.addView(btn[i]);
                } else {
                    linear_right.addView(btn[i]);
                }

                btn[i].setOnClickListener(seatListener);

            }


            // 한번에 양 옆 자리까지 .
            temp = temp + seatCount*2;


            // 구별 바
            ImageView imageView3 = new ImageView(CanActivity.this);
            imageView3.setImageResource(R.drawable.can_bar);
            imageView3.setLayoutParams(new LinearLayout.LayoutParams(500,200));
            linear_left.addView(imageView3);

            ImageView imageView4 = new ImageView(CanActivity.this);
            imageView4.setImageResource(R.drawable.can_bar);
            imageView4.setLayoutParams(new LinearLayout.LayoutParams(500,200));
            linear_right.addView(imageView4);


        }





        // 자리 정보가 있는 곳에 목적지와 아이디 셋팅
        for(int i=0; i<seatList.size(); i++){

            // 내가 앉은 자리가 있따면,
            if(GlobalClass.phoneID.equals(seatList.get(i).phoneID)){
                GlobalClass.trainSeat = seatList.get(i).seat;

                if(GlobalClass.trainSeat == 1000){
                    Toast.makeText(CanActivity.this, "현재 서있습니다.", Toast.LENGTH_SHORT).show();
                }else{
                    btn[seatList.get(i).seat].setBackground(getResources().getDrawable(R.drawable.can_seat_me));
                }

            }

            if(GlobalClass.trainSeat == 1000){
            }else{
                btn[seatList.get(i).seat].setText(seatList.get(i).dst);
                btn[seatList.get(i).seat].setTag(seatList.get(i).phoneID);
            }



        }

        dialog.dismiss();

        // 자리입력을 안했으면
        if(!GlobalClass.seatflag){
            AlertDialog.Builder builder = new AlertDialog.Builder(CanActivity.this);
            builder.setTitle("자리를 입력해주세요.")        // 제목 설정
                    .setMessage("자리를 입력해주셔야 알람 기능이 작동합니다. \n 서있다면 서있음을 클릭해주세요.")        // 메세지 설정
                    .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        // 확인 버튼 클릭시 설정
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    }).create().show();
        }

    }



    View.OnClickListener seatListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {

            // 빈자리라면 자리 입력을 할 수 있다.
            if(v.getTag() == null) {


                // 만약 이미 나는 다른 곳에 앉아있다면
                if (GlobalClass.seatflag) {

                    new AlertDialog.Builder(CanActivity.this).setTitle("자리 이동 하시겠습니까?")        // 제목 설정
                            .setMessage("이 자리로 이동하시겠습니까?")        // 메세지 설정
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                // 확인 버튼 클릭시 설정
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    addSeat((Button)v);
                                }
                            }).
                            setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                // 확인 버튼 클릭시 설정
                                public void onClick(DialogInterface dialog, int whichButton) {
                                }
                            }).create().show();

                }else{


                    new AlertDialog.Builder(CanActivity.this).setTitle("자리 선택 하시겠습니까?")        // 제목 설정
                            .setMessage("이 자리로 선택하시겠습니까?")        // 메세지 설정
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                // 확인 버튼 클릭시 설정
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    addSeat((Button) v);
                                }
                            }).
                            setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                // 확인 버튼 클릭시 설정
                                public void onClick(DialogInterface dialog, int whichButton) {
                                }
                            }).create().show();


                }


            }
            // 만약 내가 앉은 자리라면,
            else if(GlobalClass.phoneID.equals(String.valueOf(v.getTag()))){

                new AlertDialog.Builder(CanActivity.this).setTitle(String.valueOf(v.getId())+" 번 자리")
                        .setMessage("자리를 삭제하시겠습니까?").
                        setPositiveButton("자리 삭제", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                delSeat((Button)v);
                            }
                        }).
                        setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            // 확인 버튼 클릭시 설정
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        }).create().show();

            }
            // 전혀 다른 사람이 앉은 자리
            else{

                String str = ((Button) v).getText().toString();
                Toast toast = Toast.makeText(CanActivity.this, str+"역", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }



        }

    };




    void getSubwaySeat(){

        seatList.clear();

        final ProgressDialog dialog = new ProgressDialog(CanActivity.this);
        dialog.setMessage("자리정보를 받아오는 중입니다...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();


        final JsonObject info = new JsonObject();
        info.addProperty("trainNum", GlobalClass.trainNum);
        info.addProperty("trainCan", GlobalClass.trainXY);

        new Thread(new Runnable() {
            public void run() {
                try {

                    RestAdapter restAdapter = new RestAdapter.Builder()
                            .setEndpoint(Retrofit.ROOT)  //call your base url
                            .build();
                    Retrofit retrofit = restAdapter.create(Retrofit.class); //this is how retrofit create your api
                    retrofit.getSubwaySeat(info, new Callback<JsonArray>() {

                        @Override
                        public void success(JsonArray jsonElements, Response response) {

                            dialog.dismiss();

                            for (int i = 0; i < jsonElements.size(); i++) {
                                JsonObject deal = (JsonObject) jsonElements.get(i);
                                String phoneID = (deal.get("phoneID")).getAsString();
                                int subwaySit = (deal.get("seatNum")).getAsInt();
                                String destination = (deal.get("dst")).getAsString();

                                // 시트 추가
                                seatList.add( new Seat(phoneID, subwaySit, destination));
                            }


                            setSeatView();

                        }

                        @Override
                        public void failure(RetrofitError retrofitError) {
                            dialog.dismiss();

                            AlertDialog.Builder builder = new AlertDialog.Builder(CanActivity.this);
                            builder.setTitle("네트워크가 불안정합니다.")        // 제목 설정
                                    .setMessage("네트워크를 확인해주세요")        // 메세지 설정
                                    .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        // 확인 버튼 클릭시 설정
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                        }
                                    });

                            AlertDialog dialog = builder.create();    // 알림창 객체 생성
                            dialog.show();    // 알림창 띄우기

                        }
                    });
                }
                catch (Throwable ex) {

                }
            }
        }).start();


    }



    /**
     * 자리 추가
     */

    void addSeat(final Button tempBtn){

        final ProgressDialog dialog = new ProgressDialog(CanActivity.this);
        dialog.setMessage("내 자리를 등록하는 중입니다...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();


        final JsonObject info = new JsonObject();
        info.addProperty("trainNum", GlobalClass.trainNum);
        info.addProperty("trainCan", GlobalClass.trainXY);
        info.addProperty("trainSeat", String.valueOf(tempBtn.getId()));
        info.addProperty("phoneID", GlobalClass.phoneID);
        info.addProperty("dst", GlobalClass.endS.getName());


        new Thread(new Runnable() {
            public void run() {
                try {

                    RestAdapter restAdapter = new RestAdapter.Builder()
                            .setEndpoint(Retrofit.ROOT)  //call your base url
                            .build();
                    Retrofit retrofit = restAdapter.create(Retrofit.class); //this is how retrofit create your api
                    retrofit.addSeat(info, new Callback<JsonObject>() {

                        @Override
                        public void success(JsonObject jsonObject, Response response) {

                            dialog.dismiss();

                            String result = (jsonObject.get("result")).getAsString();


                            // 일반적인 자리입력 성공
                            if(result.equals("normal")){

                                // 내 자리 업데이트
                                tempBtn.setBackground(getResources().getDrawable(R.drawable.can_seat_me));
                                tempBtn.setText(GlobalClass.endS.getName());
                                tempBtn.setTag(GlobalClass.phoneID);
                                GlobalClass.trainSeat = tempBtn.getId();
                                GlobalClass.seatflag = true;


                                // 이전 기록이 없었다면, 알람 액티비티로 이동.
                                String state = (jsonObject.get("state")).getAsString();
                                if(state.equals("first")){


                                    new AlertDialog.Builder(CanActivity.this).setTitle("자리입력 성공")
                                            .setMessage("자리를 입력하셨습니다.")
                                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                // 확인 버튼 클릭시 설정
                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                    Intent intent = new Intent(CanActivity.this, AlarmActivity.class);
                                                    startActivity(intent);
                                                }
                                            }).create().show();


                                }else{


                                    new AlertDialog.Builder(CanActivity.this).setTitle("자리입력 성공")
                                            .setMessage("자리를 입력하셨습니다.")
                                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                // 확인 버튼 클릭시 설정
                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                }
                                            }).create().show();

                                }




                            }
                            // 자리 이동 중
                            else if(result.equals("move")){

                                // 내 자리 업데이트
                                tempBtn.setBackground(getResources().getDrawable(R.drawable.can_seat_me));
                                tempBtn.setText(GlobalClass.endS.getName());
                                tempBtn.setTag(GlobalClass.phoneID);
                                GlobalClass.trainSeat = tempBtn.getId();
                                GlobalClass.seatflag = true;


                                String state = (jsonObject.get("state")).getAsString();
                                // 칸이 같아 뷰를 갱신해야 할때
                                if(state.equals("view")){
                                    // 이전 자리의 번호
                                    int preSeat = (jsonObject.get("preSeat")).getAsInt();

                                    if(preSeat != 1000){
                                        btn[preSeat].setBackground(getResources().getDrawable(R.drawable.can_seat));
                                        btn[preSeat].setText("");
                                        btn[preSeat].setTag(null);
                                    }




                                }


                                new AlertDialog.Builder(CanActivity.this).setTitle("자리이동 성공")
                                        .setMessage("자리를 이동하셨습니다.")
                                        .setCancelable(false)
                                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            // 확인 버튼 클릭시 설정
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                            }
                                        }).create().show();

                            }
                            // 실패
                            else{
                                    new AlertDialog.Builder(CanActivity.this).setTitle("알 수 없는 오류로 실패")        // 제목 설정
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
                            new AlertDialog.Builder(CanActivity.this).setTitle("네트워크가 불안정합니다.")        // 제목 설정
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


    /**
     *  자리삭제 AsyncTask
     */

    void delSeat(final Button tempBtn){

        final ProgressDialog dialog = new ProgressDialog(CanActivity.this);
        dialog.setMessage("내 자리를 삭제하는 중입니다...");
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
                    retrofit.delSeat(info, new Callback<String>() {

                        @Override
                        public void success(String result, Response response) {

                            dialog.dismiss();

                            if (result.equals("success")) {

                                tempBtn.setText("");
                                tempBtn.setTag(null);
                                tempBtn.setBackground(getResources().getDrawable(R.drawable.can_seat));
                                GlobalClass.trainSeat = -1;
                                GlobalClass.seatflag = false;

                                new AlertDialog.Builder(CanActivity.this).setTitle("자리삭제 성공")        // 제목 설정
                                        .setMessage("자리를 삭제하셨습니다.")        // 메세지 설정
                                        .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            // 확인 버튼 클릭시 설정
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                            }
                                        }).create().show();

                            } else {
                                new AlertDialog.Builder(CanActivity.this).setTitle("알 수 없는 오류로 실패")        // 제목 설정
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

                            new AlertDialog.Builder(CanActivity.this).setTitle("네트워크가 불안정합니다.")        // 제목 설정
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


}

