package com.chocoroll.subwayseat;

import android.app.Application;
import android.content.Context;
import android.telephony.TelephonyManager;

import com.chocoroll.subwayseat.Model.Seat;
import com.chocoroll.subwayseat.Model.Station;

import java.util.ArrayList;

/**
 * Created by RA on 2015-06-02.
 */
public class GlobalClass extends Application {

    // 사용자 아이디
    public static String phoneID;


    public static Station startS, endS;   // 출발역, 도착역
    public static String trainNum;    // 열차 번호
    public static int trainXY =1; // 열차 칸
    public static int trainSeat = -1; // 자리 번호

    public static boolean seatflag = false;     // 자리 입력 했는지 안했는지

    // 호선 리스트
    public static ArrayList<Station> stationList;


    @Override
    public void onCreate() {
        super.onCreate();


    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }


}
