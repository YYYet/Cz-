package com.example.mystep.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mystep.R;
import com.example.mystep.bean.Account;
import com.example.mystep.bean.UserInfo;
import com.example.mystep.bean.VerificationCode;
import com.example.mystep.callback.mLoginCallback;
import com.example.mystep.core.BaseFragment;
import com.example.mystep.model.LoginModel;
import com.example.mystep.util.XToastUtils;
import com.google.gson.Gson;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.edittext.verify.VerifyCodeEditText;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.mystep.model.InternetModel.postModifyData;
import static com.example.mystep.model.ViewModel.Dialog_tips;
import static com.example.mystep.model.ViewModel.modifyStepsDialog;
import static com.example.mystep.util.utils.loginByAuth;

@Page(name = "短信验证码")
public class VerifiCationFragment extends BaseFragment {

    private MainFragment mainFragment;
    @BindView(R.id.vcet_1)
    VerifyCodeEditText vcode;
    @BindView(R.id.loginbtn)
    Button loginbtn;
    private Handler handler=new Handler(){
        public void handleMessage(Message msg)
        {
            switch(msg.what) {
                case 1:
                    String jsondata = msg.getData().getString("data");//接受msg传递过来的参数
                    UserInfo account = new Gson().fromJson(jsondata, UserInfo.class);
                   // modifyStepsDialog(account,getActivity(),getContext());
                    Looper.prepare();
                    XToastUtils.toast("请返回使用Cookie修改步数");
                    Looper.loop();
                    break;
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.verification_fragment;
    }

    @Override
    protected void initViews() {

    }
    public Account DealWithLoginInfo(String code){
        Account ac = new Account();
        Bundle bundle = getArguments();
        if(bundle != null){
            String loginInfo =  bundle.getString("bean");
            VerificationCode account = new Gson().fromJson(loginInfo,VerificationCode.class);
            ac.setAuthCode(vcode.getInputValue());
            ac.setLoginName(account.getMobile());
            return ac;
        }else {
            ac.setAuthCode("null");
            ac.setLoginName("null");
            return ac;
        }

    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);

        vcode.setOnInputListener(new VerifyCodeEditText.OnInputListener() {
            @Override
            public void onComplete(String input) {

            }

            @Override
            public void onChange(String input) {
                if (input.length()==6){

                }
            }

            @Override
            public void onClear() {

            }
        });

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                LoginModel loginModel = new LoginModel();
                Log.e("", "onClick: 监听到短信验证码,开始执行登录操作"+vcode.getInputValue() );
                loginModel.login(loginByAuth, DealWithLoginInfo(vcode.getInputValue()),getActivity(), new mLoginCallback() {
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

                            //  modifyStepsDialog(userInfo,v);

                 /*           new MaterialDialog.Builder(getActivity())
                                    .iconRes(R.drawable.icon_warning)
                                    .title(R.string.tip_warning)
                                    .content(R.string.content_warning)
                                    .inputType(
                                            InputType.TYPE_CLASS_TEXT

                                                    | InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                                    .input(
                                            getString(R.string.hint_please_input_step),
                                            "",
                                            false,
                                            ((dialog, input) -> {
                                                try {
                                                    postModifyData(input.toString().trim(),userInfo,getActivity());
                                                    // XToastUtils.success("获取并执行"+input);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    Log.e("Exception", "modifyStepsDialogException: "+e );
                                                }
                                            }))
                                    .inputRange(1, 6)
                                    .positiveText(R.string.lab_continue)
                                    .negativeText(R.string.lab_change)
                                    .onPositive(((dialog, which) -> {
                                        XToastUtils.success("点击"+dialog.getInputEditText().getText().toString());
                                    }))
                                    .cancelable(true)
                                    .show();*/
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

