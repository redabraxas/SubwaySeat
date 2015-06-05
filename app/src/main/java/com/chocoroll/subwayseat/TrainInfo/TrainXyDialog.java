package com.chocoroll.subwayseat.TrainInfo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chocoroll.subwayseat.Can.CanActivity;
import com.chocoroll.subwayseat.GlobalClass;
import com.chocoroll.subwayseat.R;

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
                GlobalClass.trainXY =trainXY;

                // 정보 확인

                LayoutInflater inflate = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
                final View layout = inflate.inflate(R.layout.dialog_sure, null);

                ((TextView) layout.findViewById(R.id.can)).setText(GlobalClass.startS.getName());
                ((TextView) layout.findViewById(R.id.can)).setText(GlobalClass.endS.getName());
                ((TextView) layout.findViewById(R.id.can)).setText(String.valueOf(GlobalClass.trainXY));


                new AlertDialog.Builder(mContext).setView(layout).setTitle("정보를 확인해주세요!")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(mContext, CanActivity.class);
                                        mContext.startActivity(intent);
                                         }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();



            }
        });

        ((Button) findViewById(R.id.btnNO)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrainNumDialog dialog = new TrainNumDialog(mContext,  GlobalClass.startS);
                dialog.show();
                dismiss();
            }
        });

    }

}
