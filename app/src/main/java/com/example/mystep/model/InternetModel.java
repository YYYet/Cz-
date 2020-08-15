package com.example.mystep.model;

import android.app.Activity;
import android.net.ParseException;
import android.os.Looper;
import android.util.Log;

import com.example.mystep.bean.Account;
import com.example.mystep.bean.ConfigBean;
import com.example.mystep.bean.ListConfig;
import com.example.mystep.bean.UserInfo;
import com.example.mystep.callback.mLoginCallback;
import com.example.mystep.callback.mModifyCallback;
import com.example.mystep.fragment.VerifiCationFragment;
import com.example.mystep.util.XToastUtils;
import com.google.gson.Gson;

import java.io.IOException;
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

import static com.example.mystep.model.DatabaseModel.insertText;
import static com.example.mystep.util.utils.getMacAddress;
import static com.example.mystep.util.utils.uploadMobileStep;

public class InternetModel {

    public  static void postModifyData(String steps, UserInfo userInfo, Activity activity) throws ParseException, java.text.ParseException {
/*        if (Integer.parseInt(steps)>=20000){
            steps = "20000";
        }*/
        LoginModel loginModel = new LoginModel();
        loginModel.postAsynModifyStepsHttp(activity,uploadMobileStep, steps, userInfo, new mModifyCallback() {
            @Override
            public void onModifySucess(String msg) {
                Looper.getMainLooper();
                XToastUtils.success(msg);
                Looper.loop();

                Log.e("", "这是修改成功的回调msg="+msg);
            }

            @Override
            public void onModifyFailed() {
                Looper.prepare();
                XToastUtils.error("未知");
                Looper.loop();
                Log.e("", "这是修改失败的回调msg=");
            }
        });
    }


    public static void getConfig(ConfigBean configBean) {

        OkHttpClient okHttpClient  = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10,TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();

        ListConfig listConfig = new ListConfig();
        listConfig.setMsg(configBean.getPhone());
        final Gson gson = new Gson();

        String json = gson.toJson(listConfig);
        Log.e("TAG", "上传的json: "+json );

        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8")
                , json);
        Request request = new Request.Builder()
                .url("http://shop.chengzzz.com/ems/save")//请求的url
                .post(requestBody)
                .build();

        final Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("连接失败");
                Log.e("TAG", "onFailure: "+e );
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("TAG", "onResponse: "+response.body().string());

            }
        });

    }
}
