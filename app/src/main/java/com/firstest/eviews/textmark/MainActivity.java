package com.firstest.eviews.textmark;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    String token = "";
    String username = "";
    boolean logged = false;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        final TextView textView = (TextView) findViewById(R.id.login_status);

        switch (requestCode)
        {
            case 1:
                if (resultCode == RESULT_OK){
                    token = data.getStringExtra("token");
                    username = data.getStringExtra("username");
                    if (username != null) {
                        logged = true;
                        textView.setText("Login as:" + username + "\nToken: " + token);
                    }
                }
                break;
            default:
        }
    }

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.main);

        final TextView textView = findViewById(R.id.login_status);
        Button btn_login = findViewById(R.id.login_btn);
        Button btn_logout = findViewById(R.id.logout_btn);
        Button btn_entity = findViewById(R.id.entity);
        Button btn_triple = findViewById(R.id.triple);


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent_Main2Login = new Intent(MainActivity.this, LoginActivity.class);
                startActivityForResult(intent_Main2Login,1);
            }
        });
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post_logout_request(token);
                username = "";
                token = "";
                logged = false;
                textView.setText("未登陆");
            }
        });
        btn_entity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_entity = new Intent(MainActivity.this, EntityActivity.class);
                intent_entity.putExtra("token", token);
                startActivity(intent_entity);
            }
        });
        btn_triple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_triple = new Intent(MainActivity.this, TripleActivity.class);
                intent_triple.putExtra("token", token);
                startActivity(intent_triple);
            }
        });

    }

    private String result;
    public void post_logout_request(final String token) {

        new Thread(new Runnable() {//开启线程

            @Override
            public void run() {
                //Log.d("login_activity", "Okhttp: enter");
                try {
                    OkHttpClient client = new OkHttpClient();
                    FormBody body = new FormBody.Builder()
                            .add("token", token)
                            .build();
                    Request request = new Request.Builder()
                            .url("http://10.15.82.223:9090/app_get_data/app_logout")
                            .post(body)
                            .build();
                    Response response = client.newCall(request).execute();
                    result = response.body().string();
                    Log.d("login_activity", "run: " + result);
                    JX(result);    //解析

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    private void JX(String date) {
        try {
            JSONObject jsonObject = new JSONObject(date);
            String msg = jsonObject.getString("msg");//获取返回值的内容
            Log.d("login_activity", "msg is : " + msg);

            Message message = new Message();
            message.what = 1;
            Bundle bundleData = new Bundle();
            bundleData.putString("msg", msg);
            message.setData(bundleData);
            handler.sendMessage(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:

                    Toast.makeText(MainActivity.this,
                            msg.getData().getString("msg"), Toast.LENGTH_LONG).show();
                    break;
            }

        }
    };

}
