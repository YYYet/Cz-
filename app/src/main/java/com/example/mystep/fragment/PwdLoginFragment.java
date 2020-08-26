package com.example.mystep.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mystep.R;
import com.example.mystep.bean.PwdLogin;
import com.example.mystep.bean.UserInfo;
import com.example.mystep.callback.mLoginCallback;
import com.example.mystep.core.BaseFragment;
import com.example.mystep.model.LoginModel;
import com.example.mystep.util.XToastUtils;
import com.google.gson.Gson;
import com.xuexiang.xpage.annotation.Page;

import butterknife.BindView;

import static com.example.mystep.model.ViewModel.Dialog_tips;
import static com.example.mystep.util.utils.loginByPwd;
import static com.example.mystep.util.utils.md5;

@Page(name = "密码登录")
public class PwdLoginFragment extends BaseFragment {
    @BindView(R.id.id_loginPhoneNumber)
    EditText loginphone;
    @BindView(R.id.id_loginPwd)
    EditText loginpwd;
    @BindView(R.id.id_login_commit)
    Button login;
    private Handler handler=new Handler(){
        public void handleMessage(Message msg)
        {
            switch(msg.what) {
                case 1:
                    String jsondata = msg.getData().getString("data");//接受msg传递过来的参数
                    UserInfo account = new Gson().fromJson(jsondata, UserInfo.class);
                    // modifyStepsDialog(account,getActivity(),getContext());
                    Looper.prepare();
                    XToastUtils.toast("返回使用Cookie修改步数");

                    Looper.loop();

                    break;
            }
        }
    };
    @Override
    protected int getLayoutId() {
        return R.layout.pwdlogin_layout;
    }

    @Override
    protected void initViews() {

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        login.setOnClickListener(v -> {
            if (loginphone.getText().toString().length()<11&&loginpwd.getText().toString().length()==0){
                XToastUtils.error("检查账号及密码");
            }else {
                PwdLogin pwdLogin = new PwdLogin();
                pwdLogin.setLoginName(loginphone.getText().toString().trim());
                pwdLogin.setPassword(md5(loginpwd.getText().toString()));
                new LoginModel().LoginByPwd(loginByPwd, pwdLogin, getActivity(), new mLoginCallback() {
                    @Override
                    public void onLoginSuccess(UserInfo userInfo) {
                        // mainFragment = new MainFragment();
                        Gson gson = new Gson();
                        if (Integer.parseInt(userInfo.getCode())==200){
                            String json = gson.toJson(userInfo);
                            Log.e("", "onClick: 监听到登录成功返回的code，这是userinfo"+json );
                            Message message=new Message();
                            Bundle bundle = new Bundle();
                            bundle.putString("data",json);  //往Bundle中存放数据
                            message.setData(bundle);//mes利用Bundle传递数据
                            message.what=1;
                            handler.sendMessage(message);//用handler发送消息

                        }else {
                            Dialog_tips(userInfo.getMsg(),getContext());
                            Log.e("", "onClick: 监听到登录失败的返回，这是userinfo"+userInfo );
                        }
                    }
                    @Override
                    public void onLoginFailed(String msg) {
                        Log.e("", "这是请求登录失败的回调");
                        Dialog_tips(msg,getContext());
                    }
                });
            }
        });

    }
}
