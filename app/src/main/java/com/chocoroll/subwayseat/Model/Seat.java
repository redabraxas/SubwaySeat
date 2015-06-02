package com.chocoroll.subwayseat.Model;

/**
 * Created by RA on 2015-06-02.
 */
public class Seat {
    public String id;
    public int seat;
    public String dst;

    // 아이디, 자리번호, 도착역
    public Seat(String _id, int _seat, String _dst) {
        seat = _seat;
        id = _id;
        dst = _dst;
    }
}