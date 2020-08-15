package com.example.mystep.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mystep.R;
import com.example.mystep.adapter.mAdapter;
import com.example.mystep.bean.CookieBean;
import com.example.mystep.core.BaseFragment;
import com.example.mystep.util.XToastUtils;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;
import static com.example.mystep.model.DatabaseModel.deleteAll;
import static com.example.mystep.model.DatabaseModel.findAll;
import static com.example.mystep.util.utils.modifyByCookie;

@Page(name = "Cookies")
public class CookiesFragment extends BaseFragment {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    String  userid;
    private mAdapter adapter;
    private List<CookieBean> list = new ArrayList<>();
    @Override
    protected int getLayoutId() {
        return R.layout.cookies_fragment;
    }

    @Override
    protected void initViews() {

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        list = findAll();
        adapter = new mAdapter(getContext(),list);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.setOnMyItemClickListener(new mAdapter.OnMyItemClickListener() {
            @Override
            public void myClick(View v, int pos) {

                Dialog_Cookies(list.get(pos).getUserid(),list.get(pos).getCookie());
            }

            @Override
            public void mLongClick(View v, int pos) {
                deleteAll(list.get(pos).getUserid());
                adapter.removeData(pos);
                XToastUtils.success("Cookie移除成功");
            }
        });
    }

    private void Dialog_Cookies(String userid,String cookies){


        new MaterialDialog.Builder(getActivity())
                .iconRes(R.drawable.icon_tip)
                .title(R.string.tip_infos)
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
                                modifyByCookie(input.toString().trim(),userid,cookies,getActivity());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }))
                .inputRange(1, 6)
                .positiveText(R.string.lab_continue)
                .negativeText(R.string.lab_cancel)
                .onPositive(((dialog, which) -> Log.e("TAG", "关闭" ) ) )
                .cancelable(false)
                .show();


    }

}
