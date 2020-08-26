package com.example.mystep.model;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.mystep.bean.Account;
import com.example.mystep.bean.ConfigBean;
import com.example.mystep.bean.Message;

import com.example.mystep.bean.Modifydata;
import com.example.mystep.bean.PwdLogin;
import com.example.mystep.bean.UserInfo;
import com.example.mystep.bean.VerificationCode;
import com.example.mystep.bean.listdata;
import com.example.mystep.callback.mLoginCallback;
import com.example.mystep.callback.mModifyCallback;
import com.example.mystep.callback.mSmsCallback;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.example.mystep.model.DatabaseModel.insertText;
import static com.example.mystep.util.utils.getMacAddress;
import static com.example.mystep.util.utils.getMyUUID;


public class LoginModel {

    String cookie;
    String phone;
    String uuid;
    String userid;
    private void getAccountData(Account account) {

       // mainActivity.loadVerificationCode("https://sports.lifesense.com/sms_service/verify/getValidateCode?requestId=1000&sessionId=nosession&mobile=" + account.getPhoneNumber());
    }

    public void postAsynSmsCodeHttp(String url, VerificationCode v, final mSmsCallback callback) {

   OkHttpClient okHttpClient  = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10,TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();

       // VerificationCode vc= new VerificationCode();
        //vc.setCode(v.getCode());

        //使用Gson 添加 依赖 compile 'com.google.code.gson:gson:2.8.1'
        final Gson gson = new Gson();
        //使用Gson将对象转换为json字符串
        String json = gson.toJson(v);
        Log.e("postAsynSmsCodeHttp", "提交的图片验证码和手机号: "+json );
        //MediaType  设置Content-Type 标头中包含的媒体类型值
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8")
                , json);
        Request request = new Request.Builder()
                .url(url)//请求的url
                .post(requestBody)
                .addHeader("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 7.0; FRD-AL10 Build/HUAWEIFRD-AL10)")
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .addHeader("Host", "sports.lifesense.com")
                .addHeader("Connection", "Keep-Alive")
                .build();

        //创建/Call
        final Call call = okHttpClient.newCall(request);
        //加入队列 异步操作
        call.enqueue(new Callback() {
            //请求错误回调方法
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("连接失败");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {



                String json=response.body().string();  //假设从服务拿出来的json字符串，就是上面的内容

                try {
                    Message ms = new Message();
                    // Log.e("123123", "onResponse: "+response.body().string().indexOf("msg")+"---"+response.body().string().indexOf("msg")+"");

                    JSONObject personObj = new JSONObject(json);
                   String code=personObj.getString("code");
                    String msg=personObj.getString("msg");
                    ms.setCode( code);
                    ms.setMsg(msg);
                    Log.e("onResponse", "onResponse: "+json );
                    callback.onVerificationCodeSuccess(ms);
                } catch (Exception e) {
                    e.printStackTrace();

                }






            }
        });

    }

   /* private void sendSmsSuccess(String sign, String message, mSmsCallback callback){
        Message ms = new Message();
        ms.setCode(sign);
        ms.setCode(message);
        callback.onVerificationCodeSuccess(ms);

    }*/

    public void login(String url, Account account, Activity activity, final mLoginCallback callback) {

        OkHttpClient okHttpClient  = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10,TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();

        phone = account.getLoginName();
        uuid = getMacAddress();
        final Gson gson = new Gson();

        String json = gson.toJson(account);
        Log.e("TAG", "登录的请求体: "+json );

        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8")
                , json);
        Request request = new Request.Builder()
                .url(url)//请求的url
                .post(requestBody)
                .addHeader("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 5.1.1; Magic2 Build/LMY48Z)")
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .addHeader("Host", "sports.lifesense.com")
                .addHeader("Connection", "Keep-Alive")
                .build();

        final Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("连接失败");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (null != response.header("Set-Cookie")) {
                  cookie = response.header("Set-Cookie");
                    Log.e("TAG", "登录响应的cookie: "+cookie );
           /*         SharedPreferences preferences = activity.getSharedPreferences("cookie", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("cookies",cookie.toString());
                    editor.commit();*/

                }

                String json=response.body().string();  //假设从服务拿出来的json字符串，就是上面的内容
                Log.e("", "登录结果的响应: "+json );
                UserInfo account = new Gson().fromJson(json,UserInfo.class);
                if (account.getCode().equals("412")){
                    callback.onLoginFailed(account.getMsg());
                }else {
           /*         SharedPreferences preferences = activity.getSharedPreferences("cookie", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("userid",account.getData().getUserId().toString());
                    editor.commit();*/
                    userid = account.getData().getUserId().toString();

                    insertText(phone,userid,cookie,uuid);



                    callback.onLoginSuccess(account);
                }



            }
        });

    }






















    public void LoginByPwd(String url, PwdLogin account, Activity activity, final mLoginCallback callback) {

        OkHttpClient okHttpClient  = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10,TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();

        phone = account.getLoginName();
        uuid = getMacAddress();
        final Gson gson = new Gson();

        String json = gson.toJson(account);
        Log.e("TAG", "密码登录的请求体: "+json );

        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8")
                , json);
        Request request = new Request.Builder()
                .url(url)//请求的url
                .post(requestBody)
                .addHeader("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 5.1.1; Magic2 Build/LMY48Z)")
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .addHeader("Host", "sports.lifesense.com")
                .addHeader("Connection", "Keep-Alive")
                .build();

        final Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("连接失败");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (null != response.header("Set-Cookie")) {
                    cookie = response.header("Set-Cookie");
                    Log.e("TAG", "登录响应的cookie: "+cookie );
           /*         SharedPreferences preferences = activity.getSharedPreferences("cookie", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("cookies",cookie.toString());
                    editor.commit();*/

                }

                String json=response.body().string();  //假设从服务拿出来的json字符串，就是上面的内容
                Log.e("", "登录结果的响应: "+json );
                UserInfo account = new Gson().fromJson(json,UserInfo.class);
                if (account.getCode().equals("412")){
                    callback.onLoginFailed(account.getMsg());
                }else {
           /*         SharedPreferences preferences = activity.getSharedPreferences("cookie", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("userid",account.getData().getUserId().toString());
                    editor.commit();*/
                    userid = account.getData().getUserId().toString();

                    insertText(phone,userid,cookie,uuid);



                    callback.onLoginSuccess(account);
                }



            }
        });

    }






    /*
     * 将时间转换为时间戳
     */
    public static String dateToStamp(String s) throws ParseException {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(s);
        long ts = date.getTime();
        res = String.valueOf(ts);
        return res;
    }

    /*
     * 将时间戳转换为时间
     */
    public static String stampToDate(String s){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }




    public void postAsynModifyStepsHttp(Activity activity,String url, String steps, UserInfo userInfo, mModifyCallback callback) throws ParseException {


        OkHttpClient okHttpClient  = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10,TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();

        Log.e("TAG", "下面开始修改" );

        int distance = Integer.parseInt(steps)/3;
        int calories = Integer.parseInt(steps) / 4;
        Log.e("", "这是修改的卡路里: "+calories );
        Log.e("", "这是修改的步数: "+steps);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
        int t= (int)System.currentTimeMillis();


        Timestamp times = new Timestamp(new Date().getTime());
        Date date = new Date(t);



         Gson gson = new Gson();
        //使用Gson将对象转换为json字符串
        listdata listdata = new listdata();
        Modifydata modifydata = new Modifydata();
        listdata.setCalories(calories);
        listdata.setDistance(distance);
        listdata.setMeasurementTime(  df.format(new Date())+"");
        listdata.setUpdated(System.currentTimeMillis());
        listdata.setStep(Integer.parseInt(steps));
        listdata.setUserId(Integer.parseInt(userInfo.getData().getUserId()));
        List data = new ArrayList();
        data.add(listdata);
        modifydata.setList(data);


        Log.e("", "这是修改的测试时间: "+ df.format(new Date()) );
        Log.e("", "这是修改的测试时间戳: "+System.currentTimeMillis() );
        Log.e("", "这是修改的距离: "+distance );
        SharedPreferences preferences = activity.getSharedPreferences("cookie", MODE_PRIVATE);
        String cookies=preferences.getString("cookies","");

        Log.e("TAG", "从sp中取出的cookie: "+ cookies);
        String json = gson.toJson(modifydata);

        Log.e("TAG", "这是修改的json请求体: "+json );
        //MediaType  设置Content-Type 标头中包含的媒体类型值
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8")
                , json);
        Request request = new Request.Builder()
                .url(url)//请求的url
                .post(requestBody)
                .addHeader("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 5.1.1; Magic2 Build/LMY48Z)")
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .addHeader("Host", "sports.lifesense.com")
                .addHeader("Connection", "Keep-Alive")
                .header("Cookie", cookies)
                .build();

        //创建/Call
        final Call call = okHttpClient.newCall(request);
        //加入队列 异步操作
        call.enqueue(new Callback() {
            //请求错误回调方法
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("连接失败");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {



                String json=response.body().string();  //假设从服务拿出来的json字符串，就是上面的内容

                Log.e("提交更改步数后的响应", "onResponse: "+json);
                JSONObject personObj = null;
                try {
                    personObj = new JSONObject(json);
                    String code=personObj.getString("code");
                    String msg=personObj.getString("msg");
                    if ("200"==code){
                        callback.onModifySucess(msg);
                    }else {
                        callback.onModifyFailed();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }






            }
        });

    }



    public void postAsynModifyStepsHttpByCookie(Activity activity,String url, String steps, String userid,String cookies, mModifyCallback callback) throws ParseException {


        OkHttpClient okHttpClient  = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10,TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();

        Log.e("TAG", "下面开始修改" );

        int distance = Integer.parseInt(steps)/3;
        int calories = Integer.parseInt(steps) / 4;
        Log.e("", "这是修改的卡路里: "+calories );
        Log.e("", "这是修改的步数: "+steps);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
        int t= (int)System.currentTimeMillis();


        Timestamp times = new Timestamp(new Date().getTime());
        Date date = new Date(t);



        Gson gson = new Gson();
        //使用Gson将对象转换为json字符串
        listdata listdata = new listdata();
        Modifydata modifydata = new Modifydata();
        listdata.setCalories(calories);
        listdata.setDistance(distance);
        listdata.setMeasurementTime(  df.format(new Date())+"");
        listdata.setUpdated(System.currentTimeMillis());
        listdata.setStep(Integer.parseInt(steps));
        listdata.setUserId(Integer.parseInt(userid));
        List data = new ArrayList();
        data.add(listdata);
        modifydata.setList(data);


        Log.e("", "这是修改的测试时间: "+ df.format(new Date()) );
        Log.e("", "这是修改的测试时间戳: "+System.currentTimeMillis() );
        Log.e("", "这是修改的距离: "+distance );
/*        SharedPreferences preferences = activity.getSharedPreferences("cookie", MODE_PRIVATE);
        String cookies=preferences.getString("cookies","");*/
        if (null==cookies){
            callback.onModifyFailed();
        }
        Log.e("TAG", "接收到的cookie: "+ cookies);
        String json = gson.toJson(modifydata);

        Log.e("TAG", "这是修改的json请求体: "+json );
        //MediaType  设置Content-Type 标头中包含的媒体类型值
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8")
                , json);
        Request request = new Request.Builder()
                .url(url)//请求的url
                .post(requestBody)
                .addHeader("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 5.1.1; Magic2 Build/LMY48Z)")
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .addHeader("Host", "sports.lifesense.com")
                .addHeader("Connection", "Keep-Alive")
                .header("Cookie", cookies)
                .build();

        //创建/Call
        final Call call = okHttpClient.newCall(request);
        //加入队列 异步操作
        call.enqueue(new Callback() {
            //请求错误回调方法
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("连接失败");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {



                String json=response.body().string();  //假设从服务拿出来的json字符串，就是上面的内容

                Log.e("提交更改步数后的响应", "onResponse: "+json);
                JSONObject personObj = null;
                try {
                    personObj = new JSONObject(json);
                    String code=personObj.getString("code");
                    String msg=personObj.getString("msg");
                    Log.e("TAG", "code: "+code );
                    if ("200".equals(code)){
                        callback.onModifySucess(msg);
                    }else {
                        callback.onModifyFailed();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }






            }
        });

    }
}