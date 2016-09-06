package com.delelong.diandian.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.delelong.diandian.MainActivity;
import com.delelong.diandian.R;
import com.delelong.diandian.adapter.MyMenuGridAdapter;
import com.delelong.diandian.adapter.MyMenuLvAdapter;
import com.delelong.diandian.bean.MenuListItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/5.
 */
public class MenuFrag extends Fragment implements View.OnClickListener,AdapterView.OnItemClickListener{

    View view;
    MenuFrag menuFrag;
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
        narrow_menu.setOnClickListener(this);
        lv_menu.setOnItemClickListener(this);
        gv_menu.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.lv_menu:
                //ListView
                switch (position){
                    //item position
                    case 0:

                        break;
                    case 1:

                        break;
                    case 2:

                        break;
                    case 3:

                        break;
                }
                break;
            case R.id.gv_menu:
                //GridView
                switch (position){
                    //item position
                    case 0:

                        break;
                    case 1:

                        break;
                    case 2:

                        break;
                    case 3:

                        break;
                    case 4:

                        break;
                    case 5:

                        break;
                }
                break;
        }
    }

    private boolean showGrid;
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ly_back:
                //隐藏本菜单界面
                menuFrag = (MenuFrag) activity.getFragmentManager().findFragmentByTag("menuFrag");
                activity.getFragmentManager().beginTransaction().hide(menuFrag).commit();
                activity.getSupportActionBar().show();
                activity.enableClick();//地图层按钮可用
                break;
            case R.id.narrow_menu:
                //箭头显示隐藏更多功能
                if (showGrid){
                    narrow_menu.setBackgroundResource(R.drawable.upnarrow);
                    ly_lv_menu.setVisibility(View.VISIBLE);
                    lv_menu.setVisibility(View.VISIBLE);
                }
                else {
                    narrow_menu.setBackgroundResource(R.drawable.downarrow);
                    ly_lv_menu.setVisibility(View.GONE);
                    lv_menu.setVisibility(View.GONE);
                }
                showGrid = !showGrid;
                break;
        }
    }
    ImageView img_head;
    TextView nick_name;
    ListView lv_menu;
    GridView gv_menu;
    ImageView narrow_menu;//上下箭头
    LinearLayout ly_lv_menu;
    LinearLayout ly_menu_more;//箭头控制显示、隐藏更多功能
    LinearLayout ly_back;//隐藏菜单
    private void initView() {
        img_head = (ImageView) view.findViewById(R.id.img_head);
        nick_name = (TextView) view.findViewById(R.id.nick_name);
        ly_lv_menu = (LinearLayout) view.findViewById(R.id.ly_lv_menu);
        lv_menu = (ListView) view.findViewById(R.id.lv_menu);

        narrow_menu = (ImageView) view.findViewById(R.id.narrow_menu);
        ly_menu_more = (LinearLayout) view.findViewById(R.id.ly_menu_more);
        gv_menu = (GridView) view.findViewById(R.id.gv_menu);

        ly_back = (LinearLayout) view.findViewById(R.id.ly_back);
        //加载ListView
        initListView();
        initGridView();
    }

    private List<MenuListItem> itemGrid;
    private MyMenuGridAdapter myGridAdapter;
    private void initGridView() {
        MenuListItem item0 = new MenuListItem(R.drawable.img_g_m_tuijian, "推荐有奖");
        MenuListItem item1 = new MenuListItem(R.drawable.img_g_m_zhaomu, "司机招募");
        MenuListItem item2 = new MenuListItem(R.drawable.img_g_m_duihuanma, "兑换码");
        MenuListItem item3 = new MenuListItem(R.drawable.img_g_m_qiye, "企业版");
        MenuListItem item4 = new MenuListItem(R.drawable.img_g_m_jifen, "积分商场");
        MenuListItem item5 = new MenuListItem(R.drawable.img_g_m_discovery, "发现");
        itemGrid = new ArrayList<>();
        itemGrid.add(item0);
        itemGrid.add(item1);
        itemGrid.add(item2);
        itemGrid.add(item3);
        itemGrid.add(item4);
        itemGrid.add(item5);

        myGridAdapter = new MyMenuGridAdapter(activity,itemGrid);
        gv_menu.setAdapter(myGridAdapter);
    }
    private List<MenuListItem> itemList;
    private MyMenuLvAdapter myLvAdapter;
    private void initListView() {
        MenuListItem item0 = new MenuListItem(R.drawable.img_l_m_history, "行程");
        MenuListItem item1 = new MenuListItem(R.drawable.img_l_m_wallet, "钱包");
        MenuListItem item2 = new MenuListItem(R.drawable.img_l_m_service, "客服");
        MenuListItem item3 = new MenuListItem(R.drawable.img_l_m_settings, "设置");
        itemList = new ArrayList<>();
        itemList.add(item0);
        itemList.add(item1);
        itemList.add(item2);
        itemList.add(item3);

        myLvAdapter = new MyMenuLvAdapter(activity,itemList);
        lv_menu.setAdapter(myLvAdapter);
    }

    MainActivity activity;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
    }



}
