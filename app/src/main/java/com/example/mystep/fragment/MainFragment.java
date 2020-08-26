package com.example.mystep.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.mystep.R;
import com.example.mystep.bean.Account;
import com.example.mystep.bean.ConfigBean;
import com.example.mystep.bean.VerificationCode;
import com.example.mystep.callback.mSmsCallback;
import com.example.mystep.core.BaseFragment;
import com.example.mystep.model.LoginModel;
import com.example.mystep.util.XToastUtils;
import com.google.gson.Gson;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.text.ParseException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

import static com.example.mystep.model.ViewModel.Dialog_tips;

import static com.example.mystep.util.utils.getMacAddress;
import static com.example.mystep.util.utils.getMyUUID;
import static com.example.mystep.util.utils.getValidateCode;
import static com.example.mystep.util.utils.modifyByCookie;
import static com.example.mystep.util.utils.sendCodeWithOptionalValidate;

@Page(name = "Cz步数助手")
public class MainFragment extends BaseFragment  {
    @BindView(R.id.id_phoneNumber)
    EditText phoneNumber;
    @BindView(R.id.id_verificationCode)
    EditText  verificationCode;
    @BindView(R.id.id_commit)
    Button commit;
    @BindView(R.id.Vclayout)
    ConstraintLayout Vclayout;
    @BindView(R.id.cookie_modify)
    TextView cookie_modify;
    @BindView(R.id.imageview)
    ImageView imageview;
    @BindView(R.id.textView5)
    TextView textView5;
    @BindView(R.id.textView6)
    TextView textView6;
    @BindView(R.id.pwdlogin)
    TextView pwdlogin;
    String userid;
    private LoginModel mLoginModel;
    public Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            Bitmap bitmap = (Bitmap)msg.obj;
            imageview.setImageBitmap(bitmap);//将图片的流转换成图片
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.main_fragment;
    }

    @Override
    protected void initViews() {

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
         ButterKnife.bind(this,view);
       // Log.e("TAG", "onViewCreated: "+getMyUUID(getActivity()));
       // Log.e("TAG", "onViewCreated: "+getMacAddress());
      //  Toast.makeText(getActivity(), ""+getMacAddress(), Toast.LENGTH_SHORT).show();
      //  cookie_modify = findViewById(R.id.cookie_modify);
        cookie_modify.setOnClickListener(new cookie_setOnClickListener() );
     //   imageview = findViewById(R.id.imageview);
        imageview.setImageResource(R.drawable.code);
        imageview.setOnClickListener(new imageView_setOnClickListener());
        imageview.setOnTouchListener(new imageView_setOnTouchListener());
        phoneNumber.addTextChangedListener(new phoneNumber_setTextChangeListener());
       textView6.setOnClickListener(new joinQQGroup_setOnClickListener());
        mLoginModel = new LoginModel();
        commit.setOnClickListener(new commit_setOnListener());

        textView5.setOnClickListener(v -> openPage(CookiesFragment.class));
        pwdlogin.setOnClickListener(v -> {
            openPage(PwdLoginFragment.class);
        });

    }

    private void getAsynVerificationCodeHttp(String url, String phone) {
        Log.e("getAsynVerifica", "getAsynVerificationCodeHttp: "+url+phone+"手机号是"+phone);
        //1.创建OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();
        //2.创建Request对象，设置一个url地址（百度地址）,设置请求方式。
        Request request = new Request.Builder().url(url+phone)
                .addHeader("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 5.1.1; Magic2 Build/LMY48Z)")
                .addHeader("Host", "sports.lifesense.com")
                .addHeader("Connection", "Keep-Alive")
                .method("GET",null)
                .build();
        //3.创建一个call对象,参数就是Request请求对象
        Call call = okHttpClient.newCall(request);
        //4.请求加入调度，重写回调方法
        call.enqueue(new Callback() {
            //请求失败执行的方法
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("response", "onResponsefail: "+e +call);
            }
            //请求成功执行的方法
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                InputStream inputStream = response.body().byteStream();//得到图片的流
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                Log.e("response", "这是获取的图片: "+bitmap);
                Message msg = new Message();
                msg.obj = bitmap;
                handler.sendMessage(msg);

            }
        });
    }

    private void Dialog_Cookies(String note,String cookies){
        SharedPreferences preferences = getActivity().getSharedPreferences("cookie", MODE_PRIVATE);
        userid=preferences.getString("userid","");
        if (userid.length()==0){
            userid="26549632";
        }
        new MaterialDialog.Builder(getActivity())
                .iconRes(R.drawable.icon_tip)
                .title(R.string.tip_infos)
                .content("cookie")
                .inputType(
                        InputType.TYPE_CLASS_TEXT

                                | InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .input(
                        getString(R.string.hint_please_input_step),
                        "",
                        false,
                        ((dialog, input) -> {
                            try {
                                modifyByCookie(input.toString().trim(),userid,cookies,getActivity());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }))
                .inputRange(1, 6)
                .positiveText(R.string.lab_continue)
                .negativeText(R.string.lab_cancel)
                .onPositive(((dialog, which) -> Log.e("TAG", "dialog_ed: " ) ) )
                .cancelable(false)
                .show();


    }


    public boolean joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }


    public VerificationCode getUserInput(){
        VerificationCode vc =new VerificationCode();
        vc.setCode(verificationCode.getText().toString());
        vc.setMobile(phoneNumber.getText().toString());
        return vc;

    }
/*    public Account getUserAccount(){
        Account ac =new Account();
        ac.setAuthCode(smsCode.getText().toString());
        ac.setLoginName(phoneNumber.getText().toString());
        return ac;
    }*/
class joinQQGroup_setOnClickListener implements View.OnClickListener{

    @Override
    public void onClick(View v) {
   if (joinQQGroup("3wwbrM4c9ZyIZ-zjJPHpcK2N4hnd8E5O")){
       XToastUtils.success("成功");
   }else {
       XToastUtils.error("调起失败");
   }
    }
}

    class cookie_setOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
          /*  SharedPreferences preferences = getActivity().getSharedPreferences("cookie", MODE_PRIVATE);
            String cookies = preferences.getString("cookies","");
            Dialog_Cookies(cookies);*/
        }
    }

    class imageView_setOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
        }
    }


    class imageView_setOnTouchListener implements View.OnTouchListener{
         @Override
          public boolean onTouch(View v, MotionEvent event) {
             if (event.getAction()==MotionEvent.ACTION_UP){
                 if (phoneNumber.getText().toString().trim().length()<11){
                         Dialog_tips("手机号不合法,请检查",getActivity());
                         //  XToast.error(getContext(),"内容不得为空");
                     }else {
                         //Log.e("onTouch", "onTouch: "+"点击到了2" );
                         //Toast.makeText(MainActivity.this, "图片校验码发放成功", Toast.LENGTH_SHORT).show();
                         XToastUtils.success("图片校验码发放成功");
                         getAsynVerificationCodeHttp(getValidateCode,phoneNumber.getText().toString().trim());
                     }

                 }

                 return false;
             }
         }

    class phoneNumber_setTextChangeListener implements TextWatcher{

             @Override
             public void beforeTextChanged(CharSequence s, int start, int count, int after) {

             }

             @Override
             public void onTextChanged(CharSequence s, int start, int before, int count) {
                 if (s.length()==11){
                     Vclayout.setVisibility(View.VISIBLE);

                 }
             }

             @Override
             public void afterTextChanged(Editable s) {

             }
         }


    class commit_setOnListener implements View.OnClickListener{

             @Override
             public void onClick(View v) {
                 if (phoneNumber.length()!=11||verificationCode.length()!=4){
                     Dialog_tips("参数不合法，请检查",getContext());

                 }else {
                     mLoginModel.postAsynSmsCodeHttp(sendCodeWithOptionalValidate, getUserInput(), new mSmsCallback() {
                         @Override
                         public void onVerificationCodeSuccess(com.example.mystep.bean.Message message) {
                             Looper.prepare();
                             if (Integer.parseInt(message.getCode())==200){
                                 XToastUtils.success("短信验证码已发送");

                                 //用Bundle携带数据
                                 Bundle bundle=new Bundle();
                                 //传递name参数为tinyphp

                                 Gson gson=new Gson();
                                 String string=gson.toJson(getUserInput());
                                 bundle.putString("bean", string);
                                 openPage(VerifiCationFragment.class, bundle);
                             }else {
                                 Dialog_tips(message.getMsg(),getActivity());
                             }
                             Looper.loop();

                         }

                         @Override
                         public void onFailed() {
                             Dialog_tips("请求参数变化，请联系开发者修改",getActivity());
                         }
                     });


                 }


             }
         }
}
