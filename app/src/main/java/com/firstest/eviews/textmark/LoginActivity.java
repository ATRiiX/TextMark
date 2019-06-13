package com.firstest.eviews.textmark;

import android.annotation.SuppressLint;
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
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private Button btn_reg, btn_login;
    private EditText text_username, text_pwd;
    private String str_username, str_pwd;
    private String result;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        text_username = (EditText) findViewById(R.id.login_username);
        text_pwd = (EditText) findViewById(R.id.login_pwd);

        final TextView textView = (TextView) findViewById(R.id.login_status);

        switch (requestCode)
        {
            case 1:
                if (resultCode == RESULT_OK){
                    text_username.setText(data.getStringExtra("username"));
                    text_pwd.setText(data.getStringExtra("password"));
                }
                break;
            default:
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        btn_login = (Button) findViewById(R.id.login_btn);
        btn_reg = (Button) findViewById(R.id.register_btn);
        text_username = (EditText) findViewById(R.id.login_username);
        text_pwd = (EditText) findViewById(R.id.login_pwd);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_username = text_username.getText().toString().trim();
                str_pwd = text_pwd.getText().toString().trim();
                post_login_request(str_username, str_pwd);
            }
        });
        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent_Login2Reg = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivityForResult(intent_Login2Reg,1);
            }
        });

    }



    public void post_login_request(final String userName, final String pwd) {

        new Thread(new Runnable() {//开启线程

            @Override
            public void run() {
                Log.d("login_activity", "Okhttp: enter");
                try {
                    OkHttpClient client=new OkHttpClient();
                    FormBody body =new FormBody.Builder()
                            .add("username",userName)
                            .add("password",pwd)
                            .build();
                    Request request = new Request.Builder()
                            .url("http://10.15.82.223:9090/app_get_data/app_signincheck")
                            .post(body)
                            .build();
                    Response response = client.newCall(request).execute();
                    result = response.body().string();
                    Log.d("login_activity", "run: "+result);
                    JX(result);    //解析

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }
    private void JX(String date){
        try {
            JSONObject jsonObject=new JSONObject(date);
            String msg = jsonObject.getString("msg");//获取返回值的内容
            Log.d("login_activity", "msg is : " + msg);

            Message message = new Message();
            message.what = 1;
            Bundle bundleData = new Bundle();
            bundleData.putString("msg",msg);
            if (jsonObject.length()>1)
                bundleData.putString("token",jsonObject.getString("token"));
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

                    Toast.makeText(LoginActivity.this,
                            msg.getData().getString("msg")+msg.getData().getString("token"),Toast.LENGTH_LONG).show();
                    if (msg.getData().getString("msg").equals("登录成功")) {
                        Intent intent_Login2Main=new Intent();
                        intent_Login2Main.putExtra("token",msg.getData().getString("token"));
                        intent_Login2Main.putExtra("username",str_username);
                        setResult(RESULT_OK,intent_Login2Main);
                        finish();
                    }

                    break;
            }




        }
    };

}
