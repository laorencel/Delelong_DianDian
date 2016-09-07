package com.delelong.diandian.menuActivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.delelong.diandian.BaseActivity;
import com.delelong.diandian.R;

/**
 * Created by Administrator on 2016/9/7.
 */
public class MenuInfoActivity extends BaseActivity implements View.OnClickListener{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_menuinfo);
        initActionBar();
        initView();
    }

    private void initView() {

    }

    ImageButton arrow_back;
    TextView tv_modifyInfo;
    private void initActionBar() {
        arrow_back = (ImageButton) findViewById(R.id.arrow_back);
        tv_modifyInfo = (TextView) findViewById(R.id.tv_modifyInfo);
        arrow_back.setOnClickListener(this);
        tv_modifyInfo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.arrow_back:
                finish();
                break;
            case R.id.tv_modifyInfo:
//                startActivityForResult();
                break;
        }
    }
}
