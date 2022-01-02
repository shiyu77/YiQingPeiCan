package com.example.devhubxgd;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.devhubxgd.Data.GlobalData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Proxy;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignUpActivity extends BaseActivity{
    private String url;
    private MaterialDialog md_sign_up;
    private EditText text_number,text_userName,text_pwd;
    private String dormitory = "云天苑A座",number,userName,pwd;
    private ArrayAdapter<String> adapter;
    private String [] logmethod;
    private Spinner spinner;

    @Override
    protected int getSystemBarColor() {
        return 0xffffff;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        url = getResources().getString(R.string.url);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        text_number = findViewById(R.id.text_number);
        text_userName = findViewById(R.id.userName);
        text_pwd = findViewById(R.id.pwd);

        findViewById(R.id.sign_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                number = text_number.getText().toString().trim();
                userName = text_userName.getText().toString().trim();
                pwd = text_pwd.getText().toString().trim();
                if(TextUtils.isEmpty(number)||TextUtils.isEmpty(userName)||TextUtils.isEmpty(pwd)){
                    Toast.makeText(SignUpActivity.this,"信息不完全",Toast.LENGTH_SHORT).show();
                }
                else {
                    askForAdd(dormitory,number,userName,pwd);
                }

            }
        });
        spinner = findViewById(R.id.sushe);
        logmethod = getResources().getStringArray(R.array.data);
        adapter = new ArrayAdapter<String>(SignUpActivity.this,android.R.layout.simple_spinner_dropdown_item,logmethod);
        spinner.setAdapter(adapter);
        spinner.setSelection(0,true);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                dormitory = logmethod[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                dormitory = "云天苑A座";
            }
        });
    }
    private void askForAdd(String dormitory,String number,String userName,String pwd){
        md_sign_up = new MaterialDialog.Builder(SignUpActivity.this)
                .title("注册中")
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
            jsonObject.put("dormitory",dormitory);
            jsonObject.put("roomNumber",number);
            jsonObject.put("username",userName);
            jsonObject.put("password",pwd);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MediaType mediaType = MediaType.parse("application/json;charset=utf-8");
        RequestBody requestBody = RequestBody.create(mediaType,""+jsonObject.toString());
        Request request = new Request.Builder()
                .url(url+"/api/v1/user/register/" )
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        md_sign_up.dismiss();
                        Toast.makeText(SignUpActivity.this, "网络请求异常", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String res = response.body().string().trim();
                Log.e("====",res);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        md_sign_up.dismiss();
                        try {
                            JSONObject json = new JSONObject(res);
                            String code = json.getString("code");
                            String msg = json.getString("msg");
                            msg = msg.toString();
                            if (code.equals("201")) {

                                Toast.makeText(SignUpActivity.this, msg, Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(SignUpActivity.this, msg, Toast.LENGTH_SHORT).show();
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
