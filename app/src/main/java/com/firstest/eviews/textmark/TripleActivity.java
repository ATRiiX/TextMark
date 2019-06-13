package com.firstest.eviews.textmark;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.text.PrecomputedTextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.PrecomputedText;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
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
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Random;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TripleActivity extends AppCompatActivity {


    String token;
    Button btn_get, btn_max, btn_min, btn_save, btn_load, btn_upload, btn_add;
    TextView textViewToken, textViewTitle, textViewContent, textViewId;
    LinearLayout linearLayout;
    TableLayout tableLayout;

    static TriplesClass t = new TriplesClass();
    Triple newTriple = new Triple(RandomString(20), -1, -1,
            -1, -1, -1, -1, null, null, 1, 1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.triple);
        Intent intent = getIntent();
        token = intent.getStringExtra("token");
        textViewToken = findViewById(R.id.textView_token_triple);
        textViewTitle = findViewById(R.id.textView_title_triple);
        textViewContent = findViewById(R.id.textView_content_triple);
        textViewId = findViewById(R.id.textView_id_triple);
        linearLayout = findViewById(R.id.linearLayout_triple);
        tableLayout = findViewById(R.id.tableLayout_triple);
        textViewContent.setCustomSelectionActionModeCallback(callback2);
        btn_get = findViewById(R.id.button_get_triple);
        btn_max = findViewById(R.id.button_maxsize_triple);
        btn_min = findViewById(R.id.button_minsize_triple);
        btn_save = findViewById(R.id.button_save_triple);
        btn_load = findViewById(R.id.button_load_triple);
        btn_upload = findViewById(R.id.button_upload_triple);
        btn_add = findViewById(R.id.button_add_triple);
        textViewToken.setText("token:" + token);


        btn_get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tableLayout.removeAllViews();
                okHttpEntity(token, 1);
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
            public void onClick(View view) {
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
                textViewTitle.setText("article title: " + t.getTitle());
                textViewId.setText("doc_id: " + t.getDocid() + "\nsent_id: " + t.getSentid());
                //refresh();

            }

        });

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                okHttpEntity(token, 2);
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((newTriple.left_entity != null) && (newTriple.right_entity != null)) {
                    AlertDialog.Builder dialog2 = new AlertDialog.Builder(TripleActivity.this);
                    //dialog2.setTitle("请选择三元组关系类型");
                    dialog2.setMessage("请选择三元组关系类型");
                    dialog2.setCancelable(false);
                    dialog2.setPositiveButton("任职关系", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            newTriple.relation_id = 0;
                            t.appendTriple(newTriple);
                            newTriple = new Triple(RandomString(20), -1, -1,
                                    -1, -1, -1, -1, null, null, 1, 1);
                            buttonRefresh();
                            dialog.dismiss();
                        }
                    });
                    dialog2.setNegativeButton("亲属关系", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            newTriple.relation_id = 1;
                            t.appendTriple(newTriple);
                            newTriple = new Triple(RandomString(20), -1, -1,
                                    -1, -1, -1, -1, null, null, 1, 1);
                            buttonRefresh();
                            dialog.dismiss();
                        }
                    });
                    dialog2.show();


                } else {
                    Toast.makeText(TripleActivity.this, "三元组缺少实体标注", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private String result;

    public void okHttpEntity(final String token, final int flag) {
        new Thread(new Runnable() {//开启线程
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    if (flag == 1) {
                        FormBody body = new FormBody.Builder()
                                .add("token", token)
                                .build();
                        Request request = new Request.Builder()
                                .url("http://10.15.82.223:9090/app_get_data/app_get_triple")
                                .post(body)
                                .build();
                        Response response = client.newCall(request).execute();
                        result = response.body().string();
                    } else {
                        FormBody body = new FormBody.Builder()
                                .add("triples", t.toJSONString())
                                .add("token", token)
                                .build();
                        Log.d("triples", t.toJSONString());
                        Request request = new Request.Builder()
                                .url("http://10.15.82.223:9090/app_get_data/app_upload_triple")
                                .post(body)
                                .build();
                        Response response = client.newCall(request).execute();
                        result = response.body().string();
                    }
                    JX(result);

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
                t.JSONLoader(date);
                Message message = new Message();
                message.what = 2;
                handler.sendMessage(message);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //@SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Toast.makeText(TripleActivity.this,
                            msg.getData().getString("msg"), Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    textViewTitle.setText("article title: " + t.getTitle());
                    textViewContent.setText(t.getContent());
                    textViewId.setText("doc_id: " + t.getDocid() + "\nsent_id: " + Integer.toString(t.getSentid()));
                    buttonRefresh();
                    break;
            }

        }
    };


    private void buttonRefresh() {
        tableLayout.removeAllViews();
        if (t.data != null) {
            for (final Triple i : t.data) {
                Button button_show = new Button(TripleActivity.this);
                button_show.setText("显示");
                button_show.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SpannableStringBuilder spannable2 = new SpannableStringBuilder(t.getContent());
                        spannable2.setSpan(new ClickableSpan() {
                            @Override
                            public void onClick(View view) {
                            }

                            @Override
                            public void updateDrawState(TextPaint ds) {
                                super.updateDrawState(ds);
                                ds.setColor(Color.RED);//设置颜色
                            }
                        }, i.left_e_start, i.left_e_end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


                        spannable2.setSpan(new ClickableSpan() {
                            @Override
                            public void onClick(View view) {
                            }

                            @Override
                            public void updateDrawState(TextPaint ds) {
                                super.updateDrawState(ds);
                                ds.setColor(Color.GREEN);//设置颜色
                            }
                        }, i.right_e_start, i.right_e_end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        if (i.relation_start != -1) {
                            spannable2.setSpan(new ClickableSpan() {
                                @Override
                                public void onClick(View view) {
                                }

                                @Override
                                public void updateDrawState(TextPaint ds) {
                                    super.updateDrawState(ds);
                                    ds.setColor(Color.BLUE);//设置颜色
                                }
                            }, i.relation_start, i.relation_end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }

                        textViewContent = findViewById(R.id.textView_content_triple);
                        textViewContent.setText(spannable2, TextView.BufferType.SPANNABLE);
                        textViewContent.setMovementMethod(LinkMovementMethod.getInstance());
                    }
                });

                TextView textView_left = new TextView(TripleActivity.this);
                textView_left.setText(i.left_entity);
                textView_left.setGravity(Gravity.CENTER);

                TextView textView_right = new TextView(TripleActivity.this);
                textView_right.setText(i.right_entity);
                textView_right.setGravity(Gravity.CENTER);

                TextView textView_relation = new TextView(TripleActivity.this);
                if (i.relation_id == 1) {
                    textView_relation.setText("亲属");
                } else {
                    textView_relation.setText("任职");
                }
                textView_relation.setGravity(Gravity.CENTER);

                boolean ischecked = true;
                final CheckBox checkBox = new CheckBox(TripleActivity.this);
                if (i.status == 1) {
                    checkBox.setChecked(ischecked);
                }
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        Log.d("check" + i.id, Integer.toString(i.status));
                        if (checkBox.isChecked()) {
                            i.status = 1;
                            Log.d("check" + i.id, Integer.toString(i.status) + "is set true");
                        } else {
                            i.status = -1;
                            Log.d("check" + i.id, Integer.toString(i.status) + "is set false");
                        }
                    }
                });

                TableRow tableRow = new TableRow(TripleActivity.this);
                tableLayout.addView(tableRow);
                tableRow.addView(button_show);
                tableRow.addView(textView_left);
                tableRow.addView(textView_right);
                tableRow.addView(textView_relation);
                tableRow.addView(checkBox);

            }
        }
    }

    private void refresh() {
        SpannableStringBuilder spannable3 = new SpannableStringBuilder(t.getContent());

        if (newTriple.left_entity != null) {
            spannable3.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(TripleActivity.this);
                    dialog.setTitle("该标注为三元组左实体");
                    dialog.setMessage("是否删除该实体标注");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            newTriple.left_entity = null;
                            newTriple.left_e_start = -1;
                            newTriple.left_e_end = -1;
                            //Log.d("11111删除PERSON", i.toString());
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
            }, newTriple.left_e_start, newTriple.left_e_end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if (newTriple.right_entity != null) {
            spannable3.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(EntityActivity.this,"this is title",Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder dialog = new AlertDialog.Builder(TripleActivity.this);
                    dialog.setTitle("该标注为三元组右实体");
                    dialog.setMessage("是否删除该实体标注");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            newTriple.right_entity = null;
                            newTriple.right_e_start = -1;
                            newTriple.right_e_end = -1;
                            //Log.d("11111删除TITLE", i.toString());
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
                    ds.setColor(Color.GREEN);//设置颜色
                }
            }, newTriple.right_e_start, newTriple.right_e_end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }


        if (newTriple.relation_start != -1 && newTriple.relation_end != -1) {
            spannable3.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(EntityActivity.this,"this is title",Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder dialog = new AlertDialog.Builder(TripleActivity.this);
                    dialog.setTitle("该标注为三元组关系词");
                    dialog.setMessage("是否删除该关系词标注");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            newTriple.relation_start = -1;
                            newTriple.relation_end = -1;
                            //Log.d("11111删除TITLE", i.toString());
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
            }, newTriple.relation_start, newTriple.relation_end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        textViewContent = findViewById(R.id.textView_content_triple);
        textViewContent.setCustomSelectionActionModeCallback(callback2);
        textViewContent.setText(spannable3, TextView.BufferType.SPANNABLE);
        textViewContent.setMovementMethod(LinkMovementMethod.getInstance());
    }


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
            menuInflater.inflate(R.menu.triple_selection_menu, menu);
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
                case R.id.menu_entity1:
                    //Toast.makeText(EntityActivity.this, content_divide, Toast.LENGTH_SHORT).show();
                    /*t.appendTriple(new Triple());*/
                    newTriple.left_entity = content_divide;
                    newTriple.left_e_start = min;
                    newTriple.left_e_end = max;
                    actionMode.finish();//收起操作菜单
                    break;
                case R.id.menu_entity2:
                    //Toast.makeText(EntityActivity.this, content_divide, Toast.LENGTH_SHORT).show();
                    /*t.appendEntity(new Entity(content_divide, min, max, "TITLE"));*/
                    newTriple.right_entity = content_divide;
                    newTriple.right_e_start = min;
                    newTriple.right_e_end = max;
                    actionMode.finish();
                    break;
                case R.id.menu_relation:
                    newTriple.relation_start = min;
                    newTriple.relation_end = max;
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


    public String RandomString(int length) {
        Random random = new Random();
        ArrayList<Character> charTable = new ArrayList<>();
        StringBuffer ranString = new StringBuffer();

        //initialize the char table
        for(int i = 0; i < 26; i++){
            charTable.add((char) ('a' + i));
            charTable.add((char) ('A' + i));
        }
        for(int i = 0; i < 10; i++){
            charTable.add((char) ('0' + i));
        }

        //random fetch a char in the table to form the String
        for(int i = 0; i < length; i++){
            ranString.append((char)charTable.get(random.nextInt(62)));
        }

        return ranString.toString();
    }


}
