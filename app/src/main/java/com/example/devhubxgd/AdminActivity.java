package com.example.devhubxgd;

import static android.view.View.inflate;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.text.TextUtils;
import android.util.JsonReader;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
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
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

public class AdminActivity extends BaseActivity{
    private String value_floor="1";
    private String url;
    private String id_del;
    private TextView text_infor;
    private View lay;
    private Spinner spinner;
    private PopupWindow mPopupWindow;
    private String s,t;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 1:

                    initData();
                    break;
                case 2:
                    setInfo();
            }
        }
    };
    private TextView text_normal,text_special;
    private int area=1,type=1;
    private String location="云天苑A座";
    private int normal=0,special=0;
    private MaterialDialog md_sign_up;
    private OrderAdminAdapter adapter_;
    private PullToRefreshView mPullToRefreshView;
    private ListView listView;
    private List<Order> list;
    private LinearLayout layout;
    private ArrayAdapter<String> adapter,adapter_1,adapter_2;
    private String [] logmethod,logmethod_1,logmethod_2,logmethod_3;
    private Spinner spinner_location,spinner_area,spinner_type,spinner_floor;
    @Override
    protected int getSystemBarColor() {
        return 0xffffff;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        url = getResources().getString(R.string.url);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_admin);
        layout = findViewById(R.id.lay_total);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show();
            }
        });
        text_normal = findViewById(R.id.num_normal);
        text_special = findViewById(R.id.num_special);
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
        listView = findViewById(R.id.list_view);
        findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askForList();

            }
        });
        spinner_location = findViewById(R.id.location);
        spinner_area = findViewById(R.id.area);
        spinner_type = findViewById(R.id.type);
        spinner_floor = findViewById(R.id.floor);
        logmethod = getResources().getStringArray(R.array.data);
        adapter = new ArrayAdapter<String>(AdminActivity.this,android.R.layout.simple_spinner_dropdown_item,logmethod);
        spinner_location.setAdapter(adapter);
        spinner_location.setSelection(0,true);
        spinner_location.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                location = logmethod[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        logmethod_1 = getResources().getStringArray(R.array.area);
        adapter_1 = new ArrayAdapter<String>(AdminActivity.this,android.R.layout.simple_spinner_dropdown_item,logmethod_1);
        spinner_area.setAdapter(adapter_1);
        spinner_area.setSelection(0,true);
        spinner_area.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                area = position+1;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        logmethod_2 = getResources().getStringArray(R.array.meal_type);
        adapter_2 = new ArrayAdapter<String>(AdminActivity.this,android.R.layout.simple_spinner_dropdown_item,logmethod_2);
        spinner_type.setAdapter(adapter_2);
        spinner_type.setSelection(0,true);
        spinner_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                type = position+1;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        logmethod_3 = getResources().getStringArray(R.array.floor);
        adapter = new ArrayAdapter<String>(AdminActivity.this,android.R.layout.simple_spinner_dropdown_item,logmethod_3);
        spinner_floor.setAdapter(adapter);
        spinner_floor.setSelection(0,true);
        spinner_floor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                value_floor = String.valueOf(position+1);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }
    private void setInfo(){
        lay.setVisibility(View.VISIBLE);
        text_infor.setText("已注册学号："+t);
    }
    private void show(){
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.4f;
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        View view = LayoutInflater.from(AdminActivity.this).inflate(R.layout.delet_user, null);
        spinner = view.findViewById(R.id.sushe);
        lay = view.findViewById(R.id.lay_delete);
        EditText number = view.findViewById(R.id.text_number);
        text_infor = view.findViewById(R.id.info);
        view.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askForDel();
            }
        });
        view.findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(number.getText().toString().trim())){
                    normal = 0;
                    special = 0;
                    sendForRes(location,number.getText().toString().trim());
                }
                else {
                    Toast.makeText(AdminActivity.this,"信息不完全",Toast.LENGTH_SHORT).show();
                }
            }
        });
        logmethod = getResources().getStringArray(R.array.data);
        adapter = new ArrayAdapter<String>(AdminActivity.this,android.R.layout.simple_spinner_dropdown_item,logmethod);
        spinner.setAdapter(adapter);
        spinner.setSelection(0,true);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                location = logmethod[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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
    private void askForDel(){
        md_sign_up = new MaterialDialog.Builder(AdminActivity.this)
                .title("删除个人信息中")
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
                .delete()
                .url(url+"/api/v1/user/"+id_del+"/")
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
                        Toast.makeText(AdminActivity.this, "网络请求异常", Toast.LENGTH_SHORT).show();
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
                                String msg = json.getString("msg");
                                Toast.makeText(AdminActivity.this,msg,Toast.LENGTH_SHORT).show();
                                mPopupWindow.dismiss();
                            }
                            else{
                                String msg = json.getString("msg");
                                Toast.makeText(AdminActivity.this,msg,Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });
            }
        });
    }
    private void sendForRes(String location,String number){
        md_sign_up = new MaterialDialog.Builder(AdminActivity.this)
                .title("读取信息中")
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
                .url(url+"/api/v1/user/?dormitory="+location+"&roomNumber="+number)
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
                        Toast.makeText(AdminActivity.this, "网络请求异常", Toast.LENGTH_SHORT).show();
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
                                JSONArray jsonArray = new JSONArray(json.getString("data"));
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                t = jsonObject.getString("username");
                                id_del = jsonObject.getString("id");
                                handler.sendEmptyMessageDelayed(2,10);
                            }
                            else{
                                String msg = json.getString("msg");
                                Toast.makeText(AdminActivity.this,msg,Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });
            }
        });
    }
    private void initData(){
        try {
            list=new ArrayList<Order>();
            JSONArray arr = new JSONArray(s);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject temp = (JSONObject) arr.get(i);
                String roomNumber = temp.getString("roomNumber").toString();
                if(roomNumber.substring(0,1).equals(value_floor)){
                    int time_type = Integer.parseInt(temp.getString("time_type"));
                    int area = Integer.parseInt(temp.getString("area"));
                    int status = Integer.parseInt(temp.getString("time_type"));
                    int normalFoods = Integer.parseInt(temp.getString("normalFoods"));
                    int muslimFoods = Integer.parseInt(temp.getString("muslimFoods"));
                    normal = normal + normalFoods;
                    special = special + muslimFoods;
                    int id = Integer.parseInt(temp.getString("id"));
                    String remark = temp.getString("remark").toString();
                    String c_time = temp.getString("c_time").toString();
                    list.add(new Order(time_type,area,status,normalFoods,muslimFoods,remark,roomNumber,id,c_time));
                }

            }
            OrderAdminAdapter adapter = new OrderAdminAdapter( AdminActivity.this,list);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setView();
    }
    private void setView(){
        text_normal.setText("正常餐"+normal+"份");
        text_special.setText("清真餐"+special+"份");
    }

    private void askForList(){
        md_sign_up = new MaterialDialog.Builder(AdminActivity.this)
                .title("获取订单中")
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
                .url(url+"/api/v1/order/some/?time_type="+type+"&area="+area+"&status="+1+"&dormitory="+location)
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
                        Toast.makeText(AdminActivity.this, "网络请求异常", Toast.LENGTH_SHORT).show();
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
                            if (code.equals("200")) {
                                layout.setVisibility(View.VISIBLE);
                                String number = json.getString("number");
                                JSONObject jsonObject = new JSONObject(number);
//                                normal = jsonObject.get("normal").toString();
//                                special = jsonObject.get("muslim").toString();
                                s = json.getString("data");
                                handler.sendEmptyMessageDelayed(1,10);
                            } else {
                                list = new ArrayList<>();
                                layout.setVisibility(View.GONE);
                                adapter_ = new OrderAdminAdapter(AdminActivity.this,list);
                                listView.setAdapter(adapter_);


                                String msg = json.getString("msg");
                                msg = msg.toString();
                                Toast.makeText(AdminActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });
            }
        });
    }
    private class OrderAdminAdapter extends BaseAdapter {
        private Context context;
        private List<Order> list;
        public OrderAdminAdapter(Context context,List<Order> list){
            this.context = context;
            this.list = list;
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
            View view=inflate(context, R.layout.item_order_admin,null);
            view.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    askForChange(list.get(position).getId());
                }
            });
            TextView number = view.findViewById(R.id.number);
            TextView normal = view.findViewById(R.id.normal);
            TextView special = view.findViewById(R.id.special);
            TextView remark = view.findViewById(R.id.remark);
            number.setText(list.get(position).getRoomNumber());
            normal.setText("正常餐"+String.valueOf(list.get(position).getNormalFoods())+"份");
            special.setText("清真餐"+String.valueOf(list.get(position).getMuslimFoods())+"份");
            remark.setText("(备注信息)"+list.get(position).getRemark());
            return view;
        }
    }
    private void askForChange(int id){
        md_sign_up = new MaterialDialog.Builder(AdminActivity.this)
                .title("确认送达中")
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
            jsonObject.put("id",id);
            jsonObject.put("status",2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MediaType mediaType = MediaType.parse("application/json;charset=utf-8");
        RequestBody requestBody = RequestBody.create(mediaType,""+jsonObject.toString());
        Request request = new Request.Builder()
                .url(url+"/api/v1/order/status/" )
                .addHeader("Authorization", GlobalData.getUserToken())
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
                        Toast.makeText(AdminActivity.this, "网络请求异常", Toast.LENGTH_SHORT).show();
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
                            if (code.equals("200")) {
                                askForList();
                                Toast.makeText(AdminActivity.this, msg, Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(AdminActivity.this, msg, Toast.LENGTH_SHORT).show();
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
