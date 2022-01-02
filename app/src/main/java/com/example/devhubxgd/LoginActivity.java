package com.example.devhubxgd;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

public class LoginActivity extends BaseActivity{
    private String url,url_me;
    private EditText text_userName,text_pwd;
    private MaterialDialog md_sign_up;
    private TextView admin;
    private ImageView pic;
    //长按的runnable
    private Runnable mLongPressRunnable;
    private TextView name;
    private Timer timer;
    @Override
    protected int getSystemBarColor() {
        return 0xffffff;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        url = getResources().getString(R.string.url);
        url_me = getResources().getString(R.string.url_me);
        super.onCreate(savedInstanceState);
        askForNews();
        setContentView(R.layout.activity_login);
        text_userName = findViewById(R.id.email);
        text_pwd = findViewById(R.id.pwd);
        auto_read();

        name = findViewById(R.id.name);
        name.setText("SY&QX");
        findViewById(R.id.sign_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askForLogin(text_userName.getText().toString().trim(),text_pwd.getText().toString().trim());

            }
        });
    }
    private void auto_read(){
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        if(!TextUtils.isEmpty(sharedPreferences.getString("userName",null))){
            text_userName.setText(sharedPreferences.getString("userName",null).toCharArray(),0,sharedPreferences.getString("userName",null).length());
            text_pwd.setText(sharedPreferences.getString("pwd",null).toCharArray(),0,sharedPreferences.getString("pwd",null).length());
        }

    }
    private void askForNews(){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .proxy(Proxy.NO_PROXY)
                .build();
        Request request = new Request.Builder()
                .url(url_me+"/android/news/is_ok.php" )
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this, "网络请求异常", Toast.LENGTH_SHORT).show();
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
                        if(res.equals("1")){
                            md_sign_up = new MaterialDialog.Builder(LoginActivity.this)
                                    .title("温馨提示")
                                    .content("本系统尚未处于开放期！")
                                    .cancelable(false)
                                    .show();
                        }

                    }
                });
            }
        });
    }
    private void askForLogin(String userName,String pwd){
        md_sign_up = new MaterialDialog.Builder(LoginActivity.this)
                .title("登录中")
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
            jsonObject.put("username",userName);
            jsonObject.put("password",pwd);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MediaType mediaType = MediaType.parse("application/json;charset=utf-8");
        RequestBody requestBody = RequestBody.create(mediaType,""+jsonObject.toString());
        Request request = new Request.Builder()
                .url(url+"/api/v1/user/login/" )
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
                        Toast.makeText(LoginActivity.this, "网络请求异常", Toast.LENGTH_SHORT).show();
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
                            String code = json.getString("code");
                            String msg = json.getString("msg");
                            msg = msg.toString();
                            if (code.equals("200")) {
                                String[] token = new String[5];
                                token = response.header("Set-Cookie").substring(14).split(";");
                                Log.e("====token",token[0]);
                                GlobalData.setUserToken(token[0]);
                                SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("userName",userName);
                                editor.putString("pwd",pwd);
                                editor.commit();
                                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
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
