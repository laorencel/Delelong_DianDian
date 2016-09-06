package com.delelong.baidumaptest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.cloud.CloudListener;
import com.baidu.mapapi.cloud.CloudManager;
import com.baidu.mapapi.cloud.CloudPoiInfo;
import com.baidu.mapapi.cloud.CloudSearchResult;
import com.baidu.mapapi.cloud.DetailSearchResult;
import com.baidu.mapapi.cloud.LocalSearchInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionResult.SuggestionInfo;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.delelong.baidumaptest.bean.MyCloudPoiInfo;

import java.util.List;

/**
 * Created by Administrator on 2016/8/23.
 */
public class ChoosePosition extends BaseActivity implements TextWatcher, AdapterView.OnItemClickListener, OnGetPoiSearchResultListener {

    private static final String TAG = "BAIDUMAPFORTESTChoosePosition";
    TextView tv_city;
    EditText edt_choose;
    TextView tv_home, tv_company;
    ListView lv_address;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.ly_choose_location);
        initView();
        searchPosition();
    }


    String city;
    String intentValue;
    private void initView() {
        tv_city = (TextView) findViewById(R.id.tv_city);
        edt_choose = (EditText) findViewById(R.id.edt_choose);

        tv_home = (TextView) findViewById(R.id.tv_home);
        tv_company = (TextView) findViewById(R.id.tv_company);

        lv_address = (ListView) findViewById(R.id.lv_address);

        //设置不同的提示语
        intentValue = getIntent().getStringExtra("choose");
        city = getIntent().getStringExtra("city");
        tv_city.setText(city + ">");
        if (intentValue.equals("myPosition")) {
            edt_choose.setHint("从哪里出发");
        } else {
            edt_choose.setHint("到哪里去");
        }

        edt_choose.addTextChangedListener(this);
    }

    MyAddressAdapter adapter;

    PoiSearch mPoiSearch;
    List<PoiInfo> infoList;

    private void searchPosition() {
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
        mPoiSearch.searchInCity(new PoiCitySearchOption().city(city).keyword("美食"));
        if (infoList != null) {
            adapter = new MyAddressAdapter();
            lv_address.setAdapter(adapter);
        }
    }

    //获取POI检索结果
    @Override
    public void onGetPoiResult(PoiResult poiResult) {
        if (poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            Toast.makeText(ChoosePosition.this, "没有找到检索结果", Toast.LENGTH_LONG).show();
            return;
        }
        infoList = poiResult.getAllPoi();

        //创建、更新适配器
        if (adapter == null) {
            adapter = new MyAddressAdapter();
            lv_address.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
        lv_address.setOnItemClickListener(this);

    }

    //获取Place详情页检索结果
    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
    }

    //监听输入框
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mPoiSearch.searchInCity(new PoiCitySearchOption().city(city).keyword(s.toString()));
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    private static final int RESULTNOCHOICECODE = 0;
    private static final int RESULTPOSITIONCODE = 1;
    private static final int RESULTDESTINATIONCODE = 2;

    /**
     * 点击选择地址，返回地图界面并传值
     *
     * @param parent   parent
     * @param view     view
     * @param position position
     * @param id       id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PoiInfo info = infoList.get(position);
        Bundle bundle = new Bundle();
        Intent intent = new Intent();
        if (intentValue.equals("myPosition")) {
            bundle.putParcelable("PoiInfo", info);
            intent.putExtra("bundle", bundle)
                    .putExtra("key", "myPosition");
            //返回结果
            setResult(RESULTPOSITIONCODE, intent);
            finish();
        } else {
            bundle.putParcelable("PoiInfo", info);
            intent.putExtra("bundle", bundle)
                    .putExtra("key", "myDestination");
            setResult(RESULTDESTINATIONCODE, intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("key", "noChoice");
        setResult(RESULTNOCHOICECODE, intent);
        super.onBackPressed();
    }

    /**
     * 适配器
     */
    class MyAddressAdapter extends BaseAdapter {
        LayoutInflater inflater;

        public MyAddressAdapter() {
            inflater = LayoutInflater.from(ChoosePosition.this);
        }

        @Override
        public int getCount() {
            return infoList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            PoiInfo info = infoList.get(position);

            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_list_addr, null);
                holder = new ViewHolder();

                holder.addressName = (TextView) convertView.findViewById(R.id.addressName);
                holder.addressDetail = (TextView) convertView.findViewById(R.id.addressDetail);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.addressName.setText(info.name);
            holder.addressDetail.setText(info.address);

            return convertView;
        }

        class ViewHolder {
            TextView addressName, addressDetail;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPoiSearch.destroy();
    }
}
