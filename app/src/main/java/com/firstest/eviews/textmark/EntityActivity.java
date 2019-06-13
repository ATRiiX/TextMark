package com.firstest.eviews.textmark;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class EntityActivity extends AppCompatActivity {


    String token;
    Button btn_get, btn_max, btn_min, btn_save, btn_load, btn_upload;
    TextView textViewToken, textViewTitle, textViewContent, textViewId;

    static EntitiesClass t = new EntitiesClass();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entity);
        Intent intent = getIntent();
        token = intent.getStringExtra("token");
        textViewToken = findViewById(R.id.textView_token);
        textViewTitle = findViewById(R.id.textView_title);
        textViewContent = findViewById(R.id.textView_content);
        textViewId = findViewById(R.id.textView_id);
        textViewContent.setCustomSelectionActionModeCallback(callback2);
        //textViewContent.setMovementMethod(LinkMovementMethod.getInstance());
        btn_get = findViewById(R.id.get_entity);
        btn_max = findViewById(R.id.button_maxsize);
        btn_min = findViewById(R.id.button_minsize);
        btn_save = findViewById(R.id.button_save);
        btn_load = findViewById(R.id.button_load);
        btn_upload = findViewById(R.id.button_upload);
        textViewToken.setText("token:"+token);


        btn_get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                okHttpEntity(token,1);
            }
        });


        btn_max.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textViewContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, textViewContent.getTextSize() + 1f);
            }
        });
        btn_min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textViewContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, textViewContent.getTextSize() - 1f);
            }
        });


        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("entitiesTOJSON", t.toJSONString());
                FileOutputStream out = null;
                BufferedWriter writer = null;
                try {
                    out = openFileOutput("data", Context.MODE_PRIVATE);
                    writer = new BufferedWriter(new OutputStreamWriter(out));
                    writer.write(t.toJSONString());
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (writer != null) {
                            writer.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        btn_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view  ) {
                FileInputStream in = null;
                BufferedReader reader = null;
                StringBuilder content = new StringBuilder();
                try {
                    in = openFileInput("data");

                    reader = new BufferedReader(new InputStreamReader(in));
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        content.append(line);
                    }

                    //Log.d("entities", content.toString());

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                t.JSONLoader(content.toString());
                Log.d("entities", content.toString());
                textViewTitle.setText("article title: "+t.getTitle());
                textViewId.setText("doc_id: "+t.getDocid()+"\nsent_id: "+t.getSentid());
                refresh();

            }

        });

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                okHttpEntity(token,2);
            }
        });
    }

    private void refresh() {
        SpannableStringBuilder spannable2 = new SpannableStringBuilder(t.getContent());
        for (final Entity i : t.data) {
            //final Entity entityi = i;
            if (i.nerTag.equals("PERSON")) {

                spannable2.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(EntityActivity.this);
                        dialog.setTitle("该实体标注为PERSON");
                        dialog.setMessage("是否删除该标注");
                        dialog.setCancelable(false);
                        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //写这里？
                                t.deleteEntity(i);
                                Log.d("11111删除PERSON",i.toString());
                                refresh();
                                dialog.dismiss();
                            }
                        });
                        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();

                    }
                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(Color.RED);//设置颜色
                    }
                },i.start,i.end,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                //spannable2.setSpan(new ForegroundColorSpan(Color.BLUE), i.start, i.end
                // , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannable2.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(EntityActivity.this,"this is title",Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder dialog = new AlertDialog.Builder(EntityActivity.this);
                        dialog.setTitle("该实体标注为TITLE");
                        dialog.setMessage("是否删除该标注");
                        dialog.setCancelable(false);
                        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                t.deleteEntity(i);
                                Log.d("11111删除TITLE",i.toString());
                                refresh();
                                dialog.dismiss();
                            }
                        });
                        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();

                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(Color.BLUE);//设置颜色
                    }
                },i.start,i.end,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        textViewContent = findViewById(R.id.textView_content);
        textViewId = findViewById(R.id.textView_id);
        textViewContent.setCustomSelectionActionModeCallback(callback2);
        textViewContent.setText(spannable2, TextView.BufferType.SPANNABLE);
        textViewContent.setMovementMethod(LinkMovementMethod.getInstance());

    }


    private String result;
    public void okHttpEntity(final String token, final int flag) {
        new Thread(new Runnable() {//开启线程
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    if(flag==1)
                    {
                        FormBody body = new FormBody.Builder()
                                .add("token", token)
                                .build();
                        Request request = new Request.Builder()
                                .url("http://10.15.82.223:9090/app_get_data/app_get_entity")
                                .post(body)
                                .build();
                        Response response = client.newCall(request).execute();
                        result = response.body().string();
                    }
                    else
                    {
                        FormBody body = new FormBody.Builder()
                                .add("entities",t.toJSONStringtoServer())
                                .add("token", token)
                                .build();
                        Log.d("logloglog", t.toJSONStringtoServer());
                        Request request = new Request.Builder()
                                .url("http://10.15.82.223:9090/app_get_data/app_upload_entity")
                                .post(body)
                                .build();
                        Response response = client.newCall(request).execute();
                        result = response.body().string();
                    }

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

            if (jsonObject.length() == 1) {
                String msg = jsonObject.getString("msg");//获取返回值的内容
                //Log.d("login_activity", "msg is : " + msg);
                Message message = new Message();
                message.what = 1;
                Bundle bundleData = new Bundle();
                bundleData.putString("msg", msg);
                message.setData(bundleData);
                handler.sendMessage(message);
            } else {
                t.init(jsonObject.getString("title"), jsonObject.getString("content"), jsonObject.getInt("sent_id"), jsonObject.getString("doc_id"));
                Message message = new Message();
                message.what = 2;
                handler.sendMessage(message);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Toast.makeText(EntityActivity.this,
                            msg.getData().getString("msg"), Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    textViewTitle.setText("article title: "+t.getTitle());
                    textViewContent.setText(t.getContent());
                    textViewId.setText("doc_id: "+t.getDocid() + "\nsent_id: " + Integer.toString(t.getSentid()));
                    break;
            }

        }
    };


    private ActionMode.Callback2 callback2 = new ActionMode.Callback2() {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            MenuInflater menuInflater = actionMode.getMenuInflater();
            menuInflater.inflate(R.menu.entity_selection_menu, menu);
            return true;//返回false则不会显示弹窗
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {

            MenuInflater menuInflater = actionMode.getMenuInflater();
            menu.clear();
            menuInflater.inflate(R.menu.entity_selection_menu, menu);
            return true;

        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            //根据item的ID处理点击事件
            int min = 0;
            int max = textViewContent.length();
            if (textViewContent.isFocused()) {
                final int selStart = textViewContent.getSelectionStart();
                final int selEnd = textViewContent.getSelectionEnd();
                min = Math.max(0, Math.min(selStart, selEnd));
                max = Math.max(0, Math.max(selStart, selEnd));
            }

            String content_divide = String.valueOf(textViewContent.getText().subSequence(min, max));


            switch (menuItem.getItemId()) {
                case R.id.menu_person:
                    //Toast.makeText(EntityActivity.this, content_divide, Toast.LENGTH_SHORT).show();
                    t.appendEntity(new Entity(content_divide, min, max, "PERSON"));
                    actionMode.finish();//收起操作菜单
                    break;
                case R.id.menu_title:
                    //Toast.makeText(EntityActivity.this, content_divide, Toast.LENGTH_SHORT).show();
                    t.appendEntity(new Entity(content_divide, min, max, "TITLE"));
                    actionMode.finish();
                    break;
            }
            refresh();
            return false;//返回true则系统的"复制"、"搜索"之类的item将无效，只有自定义item有响应
        }


        @Override
        public void onDestroyActionMode(ActionMode actionMode) {

        }

        @Override
        public void onGetContentRect(ActionMode mode, View view, Rect outRect) {
            //可选  用于改变弹出菜单的位置
            super.onGetContentRect(mode, view, outRect);
        }



    };
}

