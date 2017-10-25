package tht.topu.com.tht.ui.activity;

import android.annotation.SuppressLint;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import tht.topu.com.tht.R;
import tht.topu.com.tht.utils.API;
import tht.topu.com.tht.utils.Utilities;

public class OrderListActivity extends AppCompatActivity {

    private WebView orderListWebView;
    private LinearLayout loadingLayout;

    private ImageView back;

    private int Ostatus = 0;
    private static final String MID_KEY = "1x11";

    private String random32;
    private String time10;
    private String key64;

    private Handler uiHandler;
    //json请求
    public static final MediaType JSON = MediaType
            .parse("application/json; charset=utf-8");

    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        uiHandler = new Handler(getMainLooper());
        SharedPreferences sharedPreferences = getSharedPreferences("TokenData", MODE_PRIVATE);
        String mid = sharedPreferences.getString(MID_KEY, "");

        initView();

        Bundle bundle = getIntent().getBundleExtra("bundleOstatus");
        Ostatus = bundle.getInt("ostatus");

        orderListWebView.getSettings().setJavaScriptEnabled(true);
        orderListWebView.addJavascriptInterface(this, "pageApp");
        orderListWebView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                loadingLayout.setVisibility(View.GONE);
                orderListWebView.setVisibility(View.VISIBLE);
            }
        });

        orderListWebView.loadUrl("http://tht.65276588.cn/f/MyOrder.aspx?Ostatus="+Ostatus+"&Mid="+mid+"&Stem_from=2");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                OrderListActivity.this.finish();
            }
        });
    }

    private void initView(){

        loadingLayout = (LinearLayout)findViewById(R.id.loadingLayout);
        orderListWebView = (WebView)findViewById(R.id.orderListWebView);
        back = (ImageView)findViewById(R.id.forumPostBack);
    }

    @JavascriptInterface
    public void goToPay(final String price, final String name, final String mid, final String oSerial, final String oid) {
        orderListWebView.post(new Runnable() {

            @Override
            public void run() {

                if (Float.valueOf(price) <= 0){

                    Toast.makeText(OrderListActivity.this, "价格小于等于0", Toast.LENGTH_SHORT).show();
                }

                Toast.makeText(OrderListActivity.this, "价格："+price+" 名称:"+name+"mid:"+mid+"oid:"+oid, Toast.LENGTH_SHORT).show();

                changeOrderStatus(oid);
            }
        });
    }

    //修改订单状态
    private void changeOrderStatus(final String oid){

        random32 = Utilities.getStringRandom(32);
        time10 = Utilities.get10Time();
        key64 = Utilities.get64Key(random32);

        String json = "{\n" +
                "    \"validate_k\": \"1\",\n" +
                "    \"params\": [\n" +
                "        {\n" +
                "            \"type\": \"Orders\",\n" +
                "            \"act\": \"Ostatus\",\n" +
                "            \"para\": {\n" +
                "                \"params\": {\n" +
                "                    \"d_Oid\": \""+oid+"\",\n" +
                "                    \"Ostatus\": \"4\",\n" +
                "                    \"SF_single\": \"\"\n" +
                "                },\n" +
                "                \"sign_valid\": {\n" +
                "                    \"source\": \"Android\",\n" +
                "                    \"non_str\": \""+random32+"\",\n" +
                "                    \"stamp\": \""+time10+"\",\n" +
                "                    \"signature\": \""+Utilities.encode("d_Oid="+oid+"Ostatus=4"+"SF_single="+"non_str="+random32+"stamp="+time10+"keySecret="+key64)+"\"\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        OkHttpClient okHttpClient = new OkHttpClient();

        RequestBody requestBody = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(API.getAPI()).post(requestBody).build();

        okHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {


            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.body() != null){

                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());

                        Log.d("changeOrderStatus", jsonObject.toString());

                        serialPay(oid);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void serialPay(String oid){

        random32 = Utilities.getStringRandom(32);
        time10 = Utilities.get10Time();
        key64 = Utilities.get64Key(random32);

        String json = "{\n" +
                "    \"validate_k\": \"1\",\n" +
                "    \"params\": [\n" +
                "        {\n" +
                "            \"type\": \"Orders\",\n" +
                "            \"act\": \"Serial_pay\",\n" +
                "            \"para\": {\n" +
                "                \"params\": {\n" +
                "                    \"d_Oid\": \""+oid+"\",\n" +
                "                    \"Serial_pay\": \"瞎编的\"\n" +
                "                },\n" +
                "                \"sign_valid\": {\n" +
                "                    \"source\": \"Android\",\n" +
                "                    \"non_str\": \""+random32+"\",\n" +
                "                    \"stamp\": \""+time10+"\",\n" +
                "                    \"signature\": \""+Utilities.encode("d_Oid="+oid+"Serial_pay=瞎编的"+"non_str="+random32+"stamp="+time10+"keySecret="+key64)+"\"\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        OkHttpClient okHttpClient = new OkHttpClient();

        RequestBody requestBody = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(API.getAPI()).post(requestBody).build();

        okHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.body() != null){

                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());

                        Log.d("changeSerial", jsonObject.toString());

                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {

                                orderListWebView.reload();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }
}