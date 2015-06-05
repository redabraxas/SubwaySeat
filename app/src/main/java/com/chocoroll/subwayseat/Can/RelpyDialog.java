package com.chocoroll.subwayseat.Can;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.chocoroll.subwayseat.Adapter.ReplyAdapter;
import com.chocoroll.subwayseat.Model.Reply;
import com.chocoroll.subwayseat.R;
import com.chocoroll.subwayseat.Retrofit.Retrofit;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by RA on 2015-05-10.
 */
public class RelpyDialog extends Dialog {

    ProgressDialog dialog;
    ArrayList<Reply> replyList = new ArrayList<Reply>();
    String postNum;

    Context context;

    ListView m_ListView;
    ReplyAdapter m_Adapter;


    public RelpyDialog(Context context, String postNum) {
        super(context);
        this.context = context;
        this.postNum = postNum;
}


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_answer);


        m_ListView=(ListView) findViewById(R.id.listViewReply);
        m_Adapter=new ReplyAdapter(getContext(), R.layout.model_reply, replyList);

        TextView replyAnswer = (TextView) findViewById(R.id.btnReply);
        replyAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = ((EditText) findViewById(R.id.editName)).getText().toString();
                String content = ((EditText) findViewById(R.id.editContent)).getText().toString();
                if (content.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("작성 실패")        // 제목 설정
                            .setMessage("내용을 입력해주세요~")        // 메세지 설정
                            .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                // 확인 버튼 클릭시 설정
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog dialog = builder.create();    // 알림창 객체 생성
                    dialog.show();    // 알림창 띄우기
                } else {
                    ((EditText) findViewById(R.id.editName)).setText("");
                    ((EditText) findViewById(R.id.editContent)).setText("");
                    sendReply(name,content);
                }

            }
        });


        // 답변리스트를 가져온다.
        getReplyList();


    }


    public void getReplyList(){

        replyList.clear();

        dialog = new ProgressDialog(context);
        dialog.setMessage("답변리스트를 받아오는 중입니다...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();

        final JsonObject info = new JsonObject();
        info.addProperty("postNum", postNum);

        new Thread(new Runnable() {
            public void run() {
                try {

                    RestAdapter restAdapter = new RestAdapter.Builder()
                            .setEndpoint(Retrofit.ROOT)  //call your base url
                            .build();
                    Retrofit retrofit = restAdapter.create(Retrofit.class); //this is how retrofit create your api
                    retrofit.getReplyList(info, new Callback<JsonArray>() {

                        @Override
                        public void success(JsonArray jsonElements, Response response) {

                            dialog.dismiss();


                            for (int i = 0; i < jsonElements.size(); i++) {
                                JsonObject jsonObject = (JsonObject) jsonElements.get(i);
                                String num = (jsonObject.get("num")).getAsString();
                                String name = (jsonObject.get("name")).getAsString();
                                String date = (jsonObject.get("date")).getAsString();
                                String content = (jsonObject.get("content")).getAsString();

                                replyList.add(new Reply(num, name, date, content));

                            }
                            Collections.reverse(replyList);
                            m_ListView.setAdapter(m_Adapter);


                        }

                        @Override
                        public void failure(RetrofitError retrofitError) {
                            dialog.dismiss();
                            Log.e("error", retrofitError.getCause().toString());
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
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

    void sendReply(String name, String content){


        dialog = new ProgressDialog(context);
        dialog.setMessage("답변을 작성하는 중입니다...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();

        final JsonObject info = new JsonObject();
        info.addProperty("postNum", postNum);
        info.addProperty("name", name);
        info.addProperty("content", content);

        new Thread(new Runnable() {
            public void run() {
                try {

                    RestAdapter restAdapter = new RestAdapter.Builder()
                            .setEndpoint(Retrofit.ROOT)  //call your base url
                            .build();
                    Retrofit retrofit = restAdapter.create(Retrofit.class); //this is how retrofit create your api
                    retrofit.sendReply(info, new Callback<String>() {

                        @Override
                        public void success(String result, Response response) {

                            dialog.dismiss();

                            if (result.equals("success")) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle("답변 작성 성공")        // 제목 설정
                                        .setMessage("답변을 성공적으로 작성하셨습니다.")        // 메세지 설정
                                        .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            // 확인 버튼 클릭시 설정
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                getReplyList();
                                            }
                                        });

                                AlertDialog dialog = builder.create();    // 알림창 객체 생성
                                dialog.show();    // 알림창 띄우기
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle("실패")        // 제목 설정
                                        .setMessage("답변을 작성하는 데 실패하였습니다. 다시 시도해주세요.")        // 메세지 설정
                                        .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            // 확인 버튼 클릭시 설정
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                            }
                                        });

                                AlertDialog dialog = builder.create();    // 알림창 객체 생성
                                dialog.show();    // 알림창 띄우기
                            }

                        }

                        @Override
                        public void failure(RetrofitError retrofitError) {
                            dialog.dismiss();
                            Log.e("error", retrofitError.getCause().toString());
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
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


}
