package com.chocoroll.subwayseat.TrainInfo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chocoroll.subwayseat.Adapter.TrainAdapter;
import com.chocoroll.subwayseat.Can.CanActivity;
import com.chocoroll.subwayseat.MainActivity;
import com.chocoroll.subwayseat.Model.Station;
import com.chocoroll.subwayseat.Model.Train;
import com.chocoroll.subwayseat.R;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by RA on 2015-05-29.
 */
public class TrainXyDialog  extends Dialog {

    Context mContext;

    int trainXY = 1;       // 승차위치

    public TrainXyDialog(Context context) {
        super(context);
        mContext = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_train_xy);
        this.setTitle("칸 선택");


        ImageView btnUP = (ImageView) findViewById(R.id.image_up);
        ImageView btnDown = (ImageView) findViewById(R.id.image_down);
        final TextView txtXY = (TextView) findViewById(R.id.txt_trainxy);

        btnUP.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (trainXY < 9)
                    trainXY++;
                else
                    trainXY = 1;

                txtXY.setText(String.valueOf(trainXY));

            }
        });

        btnDown.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (trainXY > 1)
                    trainXY--;
                else
                    trainXY = 9;

                txtXY.setText(String.valueOf(trainXY));

            }
        });


        ((Button) findViewById(R.id.btnOK)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)MainActivity.mContext).userInfo.setTrainXY(trainXY);

                // 정보 확인
                MainActivity.UserInfo userInfo =  ((MainActivity)MainActivity.mContext).getUserInfo();

                new AlertDialog.Builder(mContext).setTitle("정보를 확인해주세요.")        // 제목 설정
                        .setMessage("출발역: "+userInfo.getStartS().getName()+"\n도착역 :"+userInfo.getEndS().getName()+"\n칸: "+userInfo.getTrainXY())        // 메세지 설정
                        .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            // 확인 버튼 클릭시 설정
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dismiss();


                                Intent intent = new Intent(mContext, CanActivity.class);
                                mContext.startActivity(intent);
                            }
                        }).create().show();

            }
        });

        ((Button) findViewById(R.id.btnNO)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrainNumDialog dialog = new TrainNumDialog(mContext,   ((MainActivity)MainActivity.mContext).userInfo.getStartS());
                dialog.show();
                dismiss();
            }
        });

    }

}
