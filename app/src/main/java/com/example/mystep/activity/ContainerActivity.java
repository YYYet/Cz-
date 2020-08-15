package com.example.mystep.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.mystep.R;
import com.example.mystep.fragment.MainFragment;
import com.xuexiang.xpage.base.XPageActivity;
import com.xuexiang.xui.utils.StatusBarUtils;

import butterknife.ButterKnife;

import static com.example.mystep.util.utils.verifyStoragePermissions;

public class ContainerActivity extends XPageActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        ButterKnife.bind(this);
        StatusBarUtils.translucent(this);
        StatusBarUtils.setStatusBarLightMode(this);
        verifyStoragePermissions(this);

        openPage(MainFragment.class);
    }
}