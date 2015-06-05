package com.chocoroll.subwayseat.Model;

/**
 * Created by RA on 2015-05-10.
 */
public class Reply {
    String num;
    String content;
    String date;
    String writer;

    public Reply(String num, String writer, String date, String content){
        this.num = num;
        this.date = date;
        this.content = content;
        this.writer =writer;
    }

    public String getContent() {
        return content;
    }

    public String getDate() {
        return date;
    }

    public String getNum() {
        return num;
    }

    public String getWriter() {
        return writer;
    }
}
