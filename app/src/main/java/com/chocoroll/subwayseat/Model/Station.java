package com.chocoroll.subwayseat.Model;

/**
 * Created by RA on 2015-05-27.
 */
public class Station{

    // 지하철 이름, 외부역 코드, 라인
    String name;
    String code;
    String line;
    double posx;
    double posy;


    public Station(String name, String code, String line, double x, double y){
        this.name =name;
        this.code =code;
        this.line =line;
        this.posx =x ;
        this.posy=y;
    }

    public String getCode() {
        return code;
    }

    public String getLine() {
        return line;
    }

    public String getName() {
        return name;
    }

    public double getPosx() {
        return posx;
    }

    public double getPosy() {
        return posy;
    }

    public void setPosx(double gpsX){
        this.posx = gpsX;
    }

    public void setPosy(double gpsY){
        this.posy = gpsY;
    }
}
