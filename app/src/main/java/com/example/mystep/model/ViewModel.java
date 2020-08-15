package com.example.mystep.model;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ParseException;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;

import com.example.mystep.R;
import com.example.mystep.bean.UserInfo;
import com.example.mystep.callback.mModifyCallback;
import com.example.mystep.fragment.VerifiCationFragment;
import com.example.mystep.util.XToastUtils;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;

import static android.content.Context.MODE_PRIVATE;
import static com.example.mystep.model.InternetModel.postModifyData;
import static com.example.mystep.util.utils.uploadMobileStep;
import static com.xuexiang.xui.utils.ResUtils.getString;

public class ViewModel {

    public static  void Dialog_tips(String note, Context context){

        new MaterialDialog.Builder(context)
                .iconRes(R.drawable.icon_tip)
                .title(R.string.tip_infos)
                .content(note)
                .positiveText(R.string.lab_submit)
                .show();


    }

    public static void modifyStepsDialog(UserInfo userInfo,Activity activity,Context context){
        Log.e("", "这是请求修改步数");
        String steps;
        new MaterialDialog.Builder(context)
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
                                Log.e("Exception", "postModifyData: "+input.toString().trim() );
                                postModifyData(input.toString().trim(),userInfo,activity);
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
                .show();


    }

}
