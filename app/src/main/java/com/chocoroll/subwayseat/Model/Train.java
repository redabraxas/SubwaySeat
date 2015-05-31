package com.chocoroll.subwayseat.Model;

/**
 * Created by RA on 2015-05-29.
 */
public class Train {

    String trainNum;
    String arriveTime;
    String dst;

    // 열차번호, 역 도착 시간, 행
    public Train(String trainNum, String arriveTime, String dst) {
        this.trainNum = trainNum;
        this.arriveTime = arriveTime;
        this.dst = dst;
    }

    public String getArriveTime() {
        return arriveTime;
    }

    public String getDst() {
        return dst;
    }

    public String getTrainNum() {
        return trainNum;
    }
}
