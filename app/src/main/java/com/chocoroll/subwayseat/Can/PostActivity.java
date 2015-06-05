package com.chocoroll.subwayseat.Can;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.chocoroll.subwayseat.Adapter.PostAdapter;
import com.chocoroll.subwayseat.GlobalClass;
import com.chocoroll.subwayseat.Model.Post;
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

public class PostActivity extends Activity {

    ArrayList<Post> postList =  new ArrayList<Post>();
    PostAdapter mAdapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        // 큐엔에이 작성구문
        TextView btnQna = (TextView)findViewById(R.id.btnReview);
        btnQna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = ((EditText) findViewById(R.id.editName)).getText().toString();
                String content = ((EditText) findViewById(R.id.editContent)).getText().toString();

                if(content.equals("") || name.equals("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(PostActivity.this);
                    builder.setTitle("작성 실패")        // 제목 설정
                            .setMessage("모든 칸을 입력해주세요~")        // 메세지 설정
                            .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                // 확인 버튼 클릭시 설정
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog dialog = builder.create();    // 알림창 객체 생성
                    dialog.show();    // 알림창 띄우기

                }else{
                    ((EditText) findViewById(R.id.editName)).setText("");
                    ((EditText) findViewById(R.id.editContent)).setText("");
                    sendPost(name, content);

                }
            }
        });

        // 리스트뷰 셋팅
        listView = (ListView) findViewById(R.id.listViewReview);
        mAdapter= new PostAdapter(PostActivity.this, R.layout.model_post, postList);

        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setAdapter(mAdapter);

        getPostList();



    }



    void sendPost(String name, String content){


        final ProgressDialog dialog = new ProgressDialog(PostActivity.this);
        dialog.setMessage("질문을 작성하는 중입니다...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();


        final JsonObject info = new JsonObject();
        info.addProperty("trainNum", GlobalClass.trainNum);
        info.addProperty("trainCan", GlobalClass.trainXY);
        info.addProperty("name", name);
        info.addProperty("content", content);

        new Thread(new Runnable() {
            public void run() {
                try {

                    RestAdapter restAdapter = new RestAdapter.Builder()
                            .setEndpoint(Retrofit.ROOT)  //call your base url
                            .build();
                    Retrofit retrofit = restAdapter.create(Retrofit.class); //this is how retrofit create your api
                    retrofit.sendPost(info, new Callback<String>() {

                        @Override
                        public void success(String result, Response response) {

                            dialog.dismiss();

                            if (result.equals("success")) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(PostActivity.this);
                                builder.setTitle("질문 작성 성공")        // 제목 설정
                                        .setMessage("질문을 성공적으로 작성하셨습니다.")        // 메세지 설정
                                        .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            // 확인 버튼 클릭시 설정
                                            public void onClick(DialogInterface dialog, int whichButton) {

                                                getPostList();
                                            }
                                        });

                                AlertDialog dialog = builder.create();    // 알림창 객체 생성
                                dialog.show();    // 알림창 띄우기
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(PostActivity.this);
                                builder.setTitle("실패")        // 제목 설정
                                        .setMessage("질문을 작성하는 데 실패하였습니다. 다시 시도해주세요.")        // 메세지 설정
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(PostActivity.this);
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


    void getPostList(){

        postList.clear();

        final ProgressDialog dialog = new ProgressDialog(PostActivity.this);
        dialog.setMessage("리뷰를 받아오는 중입니다...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();

        final JsonObject info = new JsonObject();
        info.addProperty("trainNum", GlobalClass.trainNum);
        info.addProperty("trainCan", GlobalClass.trainXY);

        new Thread(new Runnable() {
            public void run() {
                try {

                    RestAdapter restAdapter = new RestAdapter.Builder()
                            .setEndpoint(Retrofit.ROOT)  //call your base url
                            .build();
                    Retrofit retrofit = restAdapter.create(Retrofit.class); //this is how retrofit create your api
                    retrofit.getPostList(info, new Callback<JsonArray>() {

                        @Override
                        public void success(JsonArray jsonElements, Response response) {

                            dialog.dismiss();


                            for (int i = 0; i < jsonElements.size(); i++) {
                                JsonObject deal = (JsonObject) jsonElements.get(i);
                                String num = (deal.get("num")).getAsString();
                                String name = (deal.get("name")).getAsString();
                                String date = (deal.get("date")).getAsString();
                                String content = (deal.get("content")).getAsString();
                                String replyCount = (deal.get("replyCount")).getAsString();

                                postList.add(new Post(num, name, date, content, replyCount));

                            }


                            Collections.reverse(postList);
                            listView.setAdapter(mAdapter);


                        }

                        @Override
                        public void failure(RetrofitError retrofitError) {
                            dialog.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(PostActivity.this);
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
