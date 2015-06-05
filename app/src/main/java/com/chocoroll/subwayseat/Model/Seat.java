package com.chocoroll.subwayseat.Model;

/**
 * Created by RA on 2015-06-02.
 */
public class Seat {
    public String phoneID;
    public int seat;
    public String dst;

    // 아이디, 자리번호, 도착역
    public Seat(String phoneID, int _seat, String _dst) {
        this.phoneID = phoneID;
        seat = _seat;
        dst = _dst;
    }
}