package com.chocoroll.subwayseat.Model;

/**
 * Created by RA on 2015-05-10.
 */
public class Post {
    String num;
    String writer;
    String date;
    String content;
    String answerCount;

    String dealNum;

    public Post(String num, String writer, String date, String content, String answerCount){
        this.num = num;
        this.writer = writer;
        this.date = date;
        this.content = content;
        this.answerCount =answerCount;
    }

    public String getNum(){ return num; }
    public String getWriter(){return writer;}
    public String getDate(){return date;}
    public String getContent(){return content;}
    public String getAnswerCount() {return answerCount; }
}
