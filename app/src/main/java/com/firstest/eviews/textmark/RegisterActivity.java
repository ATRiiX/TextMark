package com.firstest.eviews.textmark;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {


    private Button btn;
    private EditText text_username, text_email, text_pwd;
    private String str_username, str_email, str_pwd;
    private String result, is;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        btn = (Button) findViewById(R.id.register_request);
        text_username = (EditText) findViewById(R.id.register_user_name);
        text_email = (EditText) findViewById(R.id.register_email);
        text_pwd = (EditText) findViewById(R.id.register_pwd);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_username = text_username.getText().toString().trim();
                str_email = text_email.getText().toString().trim();
                str_pwd = text_pwd.getText().toString().trim();
                post_register_request(str_username, str_email, str_pwd);

            }
        });
    }

    public void post_register_request(final String userName, final String email, final String pwd) {

        new Thread(new Runnable() {//开启线程

            @Override
            public void run() {
                Log.d("login_activity", "Okhttp: enter");
                try {
                    OkHttpClient client = new OkHttpClient();
                    FormBody body = new FormBody.Builder()
                            .add("username", userName)
                            .add("email", email)
                            .add("password", pwd)
                            .build();
                    Request request = new Request.Builder()
                            .url("http://10.15.82.223:9090/app_get_data/app_register")
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
            bundleData.putString("msg",msg);
            message.setData(bundleData);
            handler.sendMessage(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:

                    Toast.makeText(RegisterActivity.this,
                            msg.getData().getString("msg"),Toast.LENGTH_LONG).show();
                    if (msg.getData().getString("msg").equals("注册成功")) {
                        Intent intent_Reg2Login=new Intent();
                        intent_Reg2Login.putExtra("username",str_username);
                        intent_Reg2Login.putExtra("password",str_pwd);
                        setResult(RESULT_OK,intent_Reg2Login);
                        finish();
                    }

                    break;
            }




        }
    };

}
