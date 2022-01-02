package com.example.devhubxgd;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.devhubxgd.Data.GlobalData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Proxy;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends BaseActivity{
    private String url,url_me;
    private Boolean isConfirm;
    private LinearLayout lay;
    private TextView admin,news;
    private ImageView pic;
    private EditText number_1,number_2,info;
    private TextView location;
    private MaterialDialog md_sign_up;
    private int type =1;
    private ArrayAdapter<String> adapter;
    private String [] logmethod;
    private Spinner spinner;
    private PopupWindow mPopupWindow;
    private Timer timer;
    @Override
    protected int getSystemBarColor() {
        return 0xffffff;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        url = getResources().getString(R.string.url);
        url_me = getResources().getString(R.string.url_me);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        location = findViewById(R.id.text_location);
        news = findViewById(R.id.news);
        askForNews();
        askForMsg();
        pic = findViewById(R.id.pic);
        pic.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        timer = new Timer();
                        TimerTask timerTask = new TimerTask() {
                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        admin.setVisibility(View.VISIBLE);

                                    }
                                });
                            }
                        };
                        timer.schedule(timerTask,3000);
                        break;
                    case MotionEvent.ACTION_UP:
                        //释放了
                        if (timer != null) {
                            timer.cancel();
                        }
                        break;
                }
                return true;
            }
        });
        admin = findViewById(R.id.admin);
        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,AdminActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initPopupWindows(MainActivity.this);
            }
        });
        findViewById(R.id.older).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,OrderFormActivity.class);
                startActivity(intent);
            }
        });
    }
    private void askForNews(){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .proxy(Proxy.NO_PROXY)
                .build();
        Request request = new Request.Builder()
                .url(url_me+"/android/news/msg.php" )
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "网络请求异常", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String res = Objects.requireNonNull(response.body()).string().trim();
                Log.e("====body: ",res);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       news.setText(res);

                    }
                });
            }
        });
    }
    private void askForMsg(){
        md_sign_up = new MaterialDialog.Builder(MainActivity.this)
                .title("获取宿舍信息中")
                .content("Please Wait...")
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .show();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .proxy(Proxy.NO_PROXY)
                .build();


        Request request = new Request.Builder()
                .url(url+"/api/v1/user/info/" )
                .addHeader("Authorization",GlobalData.getUserToken())
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        md_sign_up.dismiss();
                        Toast.makeText(MainActivity.this, "网络请求异常", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String res = Objects.requireNonNull(response.body()).string().trim();
                Log.e("====body: ",res);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        md_sign_up.dismiss();
                        try {
                            JSONObject json = new JSONObject(res);
                            String dormitory = json.getString("dormitory").toString();
                            String number = json.getString("roomNumber").toString();
                            GlobalData.setLocation(dormitory+number);
                            location.setText(GlobalData.getLocation());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
    }
        private void initPopupWindows(Context context){
            isConfirm = false;
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.alpha = 0.4f;
            getWindow().setAttributes(lp);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            View view = LayoutInflater.from(context).inflate(R.layout.select, null);
            lay = view.findViewById(R.id.lay_tip);
            number_1 = view.findViewById(R.id.number_1);
            number_2 = view.findViewById(R.id.number_2);
            info = view.findViewById(R.id.content);
            view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPopupWindow.dismiss();
                }
            });
            view.findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(TextUtils.isEmpty(number_1.getText().toString().trim())||TextUtils.isEmpty(number_2.getText().toString().trim())){
                        Toast.makeText(MainActivity.this,"信息不完全",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        if(isConfirm){
                            setOrder(type,number_1.getText().toString().trim(),number_2.getText().toString().trim(),info.getText().toString());

                        }
                        else {
                            lay.setVisibility(View.VISIBLE);
                            isConfirm = true;
                        }
                    }
                }
            });
            TextView textView = view.findViewById(R.id.location);
            textView.setText(GlobalData.getLocation());
            spinner = view.findViewById(R.id.type);
            logmethod = getResources().getStringArray(R.array.meal_type);
            adapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_spinner_dropdown_item,logmethod);
            spinner.setAdapter(adapter);
            spinner.setSelection(0,true);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                    type = position+1;
                }
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    type = 1;
                }
            });
            mPopupWindow = new PopupWindow(view, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT, true);
            mPopupWindow.setContentView(view);
            mPopupWindow.setOutsideTouchable(false);
            mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    WindowManager.LayoutParams lp = getWindow().getAttributes();
                    lp.alpha = 1.0f;
                    getWindow().setAttributes(lp);
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

                }
            });
            mPopupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        }
        private void setOrder(int type,String number_normal,String number_special,String info){
            md_sign_up = new MaterialDialog.Builder(MainActivity.this)
                    .title("创建订单中")
                    .content("Please Wait...")
                    .progress(true, 0)
                    .progressIndeterminateStyle(true)
                    .show();
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .proxy(Proxy.NO_PROXY)
                    .build();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("time_type",type);
                jsonObject.put("normalFoods",Integer.parseInt(number_normal));
                jsonObject.put("muslimFoods",Integer.parseInt(number_special));
                jsonObject.put("remark",info);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            MediaType mediaType = MediaType.parse("application/json;charset=utf-8");
            RequestBody requestBody = RequestBody.create(mediaType,""+jsonObject.toString());


            Request request = new Request.Builder()
                    .url(url+"/api/v1/order/" )
                    .addHeader("Authorization",GlobalData.getUserToken())
                    .post(requestBody)
                    .build();
            Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            md_sign_up.dismiss();
                            Toast.makeText(MainActivity.this, "网络请求异常", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    final String res = Objects.requireNonNull(response.body()).string().trim();
                    Log.e("====body: ",res);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            md_sign_up.dismiss();
                            try {
                                JSONObject json = new JSONObject(res);
                                String code = json.getString("code").toString();
                                String msg = json.getString("msg").toString();
                                if(code.equals("200")){
                                    Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
                                   mPopupWindow.dismiss();
                                }
                                else {
                                    Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                }
            });

        }
    }
