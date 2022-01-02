package com.example.devhubxgd;

import static android.view.View.generateViewId;
import static android.view.View.inflate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.devhubxgd.Data.GlobalData;
import com.example.devhubxgd.Data.Order;
import com.yalantis.phoenix.PullToRefreshView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Proxy;
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

public class OrderFormActivity extends BaseActivity{
    private String url;
    private String s;
    private List<Order> list;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 0:
                    initData(s);
                    break;
            }
        }
    };
    private MaterialDialog md_sign_up;
    private PullToRefreshView mPullToRefreshView;
    private ListView listView;
    @Override
    protected int getSystemBarColor() {
        return 0xffffff;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        url = getResources().getString(R.string.url);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_form);
        list = new ArrayList<Order>();
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        listView = findViewById(R.id.list_view);
        mPullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPullToRefreshView.setRefreshing(false);
                        askForList();
                    }
                }, 1500);
            }
        });


        askForList();

    }
    private void initData(String data){
        try {
            list=new ArrayList<Order>();

            JSONArray arr = new JSONArray(data);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject temp = (JSONObject) arr.get(i);
                int time_type = Integer.parseInt(temp.getString("time_type"));
                int area = Integer.parseInt(temp.getString("area"));
                int status = Integer.parseInt(temp.getString("status"));
                int normalFoods = Integer.parseInt(temp.getString("normalFoods"));
                int muslimFoods = Integer.parseInt(temp.getString("muslimFoods"));
                int id = Integer.parseInt(temp.getString("id"));
                String roomNumber = temp.getString("roomNumber").toString();
                String remark = temp.getString("remark").toString();
                String c_time = temp.getString("c_time").toString();
                list.add(new Order(time_type,area,status,normalFoods,muslimFoods,remark,roomNumber,id,c_time));
            }
            OrderListAdapter adapter = new OrderListAdapter(list, OrderFormActivity.this);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void askForList(){
        md_sign_up = new MaterialDialog.Builder(OrderFormActivity.this)
                .title("获取历史订单中")
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
                .url(url+"/api/v1/order/all/" )
                .addHeader("Authorization", GlobalData.getUserToken())
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        md_sign_up.dismiss();
                        Toast.makeText(OrderFormActivity.this, "网络请求异常", Toast.LENGTH_SHORT).show();
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
                            if(code.equals("200")){
                                s = json.getString("data");
                                handler.sendEmptyMessageDelayed(0,10);
                            }
                            else {
                                String msg = json.getString("msg");
                                Toast.makeText(OrderFormActivity.this,msg,Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }



                    }
                });
            }
        });
    }
    private static class OrderListAdapter extends BaseAdapter{
        private Context context;
        private List<Order> list;
        public OrderListAdapter(List<Order> list, Context context){
            this.list = list;
            this.context = context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view=inflate(context, R.layout.item_order,null);

            TextView type = view.findViewById(R.id.text_type);
            TextView status = view.findViewById(R.id.text_state);
            TextView normal = view.findViewById(R.id.normal);
            TextView special = view.findViewById(R.id.special);
            TextView remark = view.findViewById(R.id.remark);
            TextView date = view.findViewById(R.id.date);
            type.setText(getType(list.get(position).getTime_type()));
            status.setText(getStatus(list.get(position).getStatus()));
            normal.setText("正常餐"+String.valueOf(list.get(position).getNormalFoods())+"份");
            special.setText("清真餐"+String.valueOf(list.get(position).getMuslimFoods())+"份");
            remark.setText("(备注信息)"+list.get(position).getRemark());
            date.setText("下单时间:"+list.get(position).getC_time().substring(0,10));
            return view;
        }

        private String getStatus(int a){
            String s = null;
            if(a==1){
                s = "已下单";
            }
            if(a==2){
                s = "已配送";
            }
            return s;
        }
        private String getType(int a){
            String s = null;
            if(a==1){
                s = "早餐";
            }
            if(a==2){
                s = "午餐";
            }
            if(a==3){
                s = "晚餐";
            }
            return s;
        }
    }


}
