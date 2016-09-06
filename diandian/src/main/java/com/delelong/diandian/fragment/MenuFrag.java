package com.delelong.diandian.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.delelong.diandian.MainActivity;
import com.delelong.diandian.R;
import com.delelong.diandian.adapter.MyMenuLvAdapter;
import com.delelong.diandian.bean.MenuListItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/5.
 */
public class MenuFrag extends Fragment implements View.OnClickListener{

    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_menu, container, false);
        initView();
        setListener();
        return view;
    }

    private void setListener() {
        ly_back.setOnClickListener(this);
    }
    MenuFrag menuFrag;
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ly_back:
                menuFrag = (MenuFrag) activity.getFragmentManager().findFragmentByTag("menuFrag");
                activity.getFragmentManager().beginTransaction().hide(menuFrag).commit();
                activity.getActionBar().show();
                break;
        }
    }
    ImageView img_head;
    TextView nick_name;
    ListView lv_main;
    LinearLayout ly_back;
    private void initView() {
        img_head = (ImageView) view.findViewById(R.id.img_head);
        nick_name = (TextView) view.findViewById(R.id.nick_name);
        lv_main = (ListView) view.findViewById(R.id.lv_main);
        ly_back = (LinearLayout) view.findViewById(R.id.ly_back);
        //加载ListView
        initListView();
    }

    private List<MenuListItem> itemList;
    private MyMenuLvAdapter myLvAdapter;
    private void initListView() {
        MenuListItem item0 = new MenuListItem(R.drawable.img_route_history, "行程");
        MenuListItem item1 = new MenuListItem(R.drawable.img_wallet, "钱包");
        MenuListItem item2 = new MenuListItem(R.drawable.img_customer_service, "客服");
        MenuListItem item3 = new MenuListItem(R.drawable.img_settings, "设置");
        itemList = new ArrayList<>();
        itemList.add(item0);
        itemList.add(item1);
        itemList.add(item2);
        itemList.add(item3);

        myLvAdapter = new MyMenuLvAdapter(activity,itemList);
        lv_main.setAdapter(myLvAdapter);
    }

    MainActivity activity;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
    }


}
