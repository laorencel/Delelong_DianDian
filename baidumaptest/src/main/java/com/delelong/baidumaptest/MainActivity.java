package com.delelong.baidumaptest;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.delelong.baidumaptest.bean.Client;
import com.delelong.baidumaptest.listener.MyMapStatusChangeListener;
import com.delelong.baidumaptest.listener.MyOrientationListener;
import com.delelong.baidumaptest.overlayutil.DrivingRouteOverlay;
import com.delelong.baidumaptest.view.CustomNumberPicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener, BDLocationListener, NumberPicker.OnValueChangeListener, OnGetRoutePlanResultListener {

    private static final String TAG = "BAIDUMAPFORTEST";
    private static final int REQUESTPOSITIONCODE = 1;
    private static final int REQUESTDESTINATIONCODE = 2;
    private MapView mMapView;
    private BaiduMap mBaiduMap;

    private Context context;
    Client client;
    private Object cityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initTimePicker();
        initLocation();
        setMapStatusListener();

    }

    private TextView timeOfReach, positon;
    private LinearLayout textInCenter;

    private ImageButton myLocation;

    private ImageButton showTime, hideTime;
    private TextView myPosition, myDestination, timeToGo;
    private LinearLayout lyOfTime, route;

    /**
     * 初始化
     */
    private void initView() {
        getSupportActionBar().hide();
        initMap();
        //创建乘客对象
        initClient();
        initOrder();

        timeOfReach = (TextView) findViewById(R.id.timeOfReach);
        positon = (TextView) findViewById(R.id.positon);
        //时间选择模块
        time_picker = (RelativeLayout) findViewById(R.id.time_picker);

        //地址选择模块
        route = (LinearLayout) findViewById(R.id.route);
        lyOfTime = (LinearLayout) findViewById(R.id.lyOfTime);
        myPosition = (TextView) findViewById(R.id.myPosition);
        myDestination = (TextView) findViewById(R.id.myDestination);
        timeToGo = (TextView) findViewById(R.id.timeToGo);
        timeToGo.setOnClickListener(this);
        myPosition.setOnClickListener(this);
        myDestination.setOnClickListener(this);

        showTime = (ImageButton) findViewById(R.id.showTime);
        hideTime = (ImageButton) findViewById(R.id.hideTime);
        showTime.setOnClickListener(this);//显示打车预约时间
        hideTime.setOnClickListener(this);//隐藏打车预约时间

        myLocation = (ImageButton) findViewById(R.id.myLocation);
        myLocation.setOnClickListener(this);//定位到我的位置

        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        callTime = getTime();

        initConfirmButtonView();
    }

    LinearLayout ly_pinChe;
    RelativeLayout rl_confirm;
    TextView tv_pinChe, tv_buPinChe, tv_coupon, tv_confirm;

    //    Button btn_confirm;
    //拼车及确认按钮
    private void initConfirmButtonView() {
        ly_pinChe = (LinearLayout) findViewById(R.id.ly_pinChe);
        rl_confirm = (RelativeLayout) findViewById(R.id.rl_confirm);
        ly_pinChe.setVisibility(View.GONE);
        rl_confirm.setVisibility(View.GONE);
        tv_pinChe = (TextView) findViewById(R.id.tv_pinChe);
        tv_buPinChe = (TextView) findViewById(R.id.tv_buPinChe);
        tv_coupon = (TextView) findViewById(R.id.tv_coupon);
        tv_confirm = (TextView) findViewById(R.id.tv_confirm);
        //设置初始文本
        tv_pinChe.setText(Html.fromHtml("拼车<br/><big>约30元</big>"));
        tv_pinChe.setTextColor(Color.YELLOW);
        tv_buPinChe.setText(Html.fromHtml("不拼车<br/><big>约35元</big>"));
        tv_coupon.setText(Html.fromHtml("优惠券已抵扣 <font color='#Fe8a03'>5.0</font> 元"));
        tv_confirm.setText(Html.fromHtml("确认拼车<small><small>(您最多可带1人)</small></small>"));

        tv_pinChe.setOnClickListener(this);
        tv_buPinChe.setOnClickListener(this);
        tv_confirm.setOnClickListener(this);
    }

    /**
     * 初始化客户信息
     */
    private void initClient() {
        client = new Client();
        client.setPhone(getIntent().getStringExtra("phone"));
    }

    /**
     * 初始化地图API
     */
    private void initMap() {
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        MapStatus ms = new MapStatus.Builder().zoom(15).build();
        BaiduMapOptions options = new BaiduMapOptions().mapStatus(ms).compassEnabled(true)//去除指南针
                .zoomControlsEnabled(false)//去掉缩放按钮
                .scaleControlEnabled(false)//去掉比例尺
                .rotateGesturesEnabled(false)//不允许旋转手势
                .overlookingGesturesEnabled(false);//不允许俯视手势
        mMapView = new MapView(this, options);
        setContentView(mMapView);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        View view = LayoutInflater.from(this).inflate(R.layout.activity_main, null);
        this.addContentView(view, params);

        this.context = this;
        mBaiduMap = mMapView.getMap();
    }

    //定位相关
    private LocationClient mLocationClient;

    private boolean isFirstIn = true;
    private double myLatitude;
    private double myLongitude;

    private BitmapDescriptor mIconLocation;
    private MyOrientationListener myOrientationListener;
    private float mCurrentX;

    private LocationMode mLocationMode;

    /**
     * 初始化定位
     */
    private void initLocation() {
        mLocationClient = new LocationClient(this);
        mLocationClient.registerLocationListener(this);

        mLocationMode = LocationMode.NORMAL;

        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");//坐标类型
        option.setIsNeedAddress(true);//返回当前位置
        option.setOpenGps(true);
        option.setScanSpan(1000);//隔多少秒进行一次位置请求
        mLocationClient.setLocOption(option);

        //初始化marker图标
        mIconLocation = BitmapDescriptorFactory.fromResource(R.mipmap.navi_map_gps_locked);
        //设置方向监听
        myOrientationListener = new MyOrientationListener(context);
        myOrientationListener.setmOnOritationListener(new MyOrientationListener.OnOritationListener() {
            @Override
            public void onOritationChanged(float x) {
                mCurrentX = x;
            }
        });


        mRoutePlanSearch = RoutePlanSearch.newInstance();
        mRoutePlanSearch.setOnGetRoutePlanResultListener(this);
        positions = new ArrayList<>();
    }

    private MyMapStatusChangeListener myMapStatusChangeListener;
    List<PoiInfo> pois;
    String city;

    /**
     * 设置地图状态改变监听
     */
    private void setMapStatusListener() {
        myMapStatusChangeListener = new MyMapStatusChangeListener(myPosition);
        mBaiduMap.setOnMapStatusChangeListener(myMapStatusChangeListener);
        LatLng centerOfMap = myMapStatusChangeListener.getLatLng();

        myMapStatusChangeListener.getGeoCodeResultListener(new MyMapStatusChangeListener.GeoCodeResulutListener() {

            @Override
            public void getReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

                pois = reverseGeoCodeResult.getPoiList();
                if (pois == null) {
                    myPosition.setText(reverseGeoCodeResult.getAddress());
                } else {
                    myPosition.setText(pois.get(0).name);
                    if (!positions.isEmpty()) {
                        positions.set(0, reverseGeoCodeResult.getLocation());
                    } else {
                        positions.add(0, reverseGeoCodeResult.getLocation());
                    }
                }

                //客户上车地点（经纬度）
                LatLng centerOfMap = reverseGeoCodeResult.getLocation();
                client.setStartLatitude(centerOfMap.latitude);
                client.setStartLongitude(centerOfMap.longitude);

                //当前中心点所在城市
                city = getCityName(reverseGeoCodeResult.getAddress());
//                Log.i(TAG, "getReverseGeoCodeResult: " + pois.get(0).address + pois.get(0).name + "//" + reverseGeoCodeResult.getAddress());
            }
        });
    }


    /**
     * 定位监听
     */
    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        MyLocationData data = new MyLocationData.Builder()
                .accuracy(bdLocation.getRadius())//精度
                .direction(mCurrentX)//当前方向
                .latitude(bdLocation.getLatitude())
                .longitude(bdLocation.getLongitude())
                .build();
        mBaiduMap.setMyLocationData(data);
//        Log.i(TAG, "onReceiveLocation: "+bdLocation.getCity());
        //更新经纬度
        myLatitude = bdLocation.getLatitude();
        myLongitude = bdLocation.getLongitude();
        //设置自定义mark图标
        MyLocationConfiguration configuration = new MyLocationConfiguration(mLocationMode, true, mIconLocation);
        mBaiduMap.setMyLocationConfigeration(configuration);
        //首次进入定位到我的位置
        if (isFirstIn) {
            centerToMyLocation(mBaiduMap, myLatitude, myLongitude);
            myPosition.setText(bdLocation.getAddrStr());
            client.setStartLatitude(bdLocation.getLatitude());
            client.setStartLongitude(bdLocation.getLongitude());
            isFirstIn = false;
        }
    }

    private String callTime;
    private String orderedTime;
    private String orderedMode;

    private void initOrder() {
        orderedTime = "现在";
        orderedMode = "拼车";
    }

    private String getTime() {
        return dateFormat.format(new Date());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.myLocation:
                centerToMyLocation(mBaiduMap, myLatitude, myLongitude);
                break;
            case R.id.showTime:
                lyOfTime.setVisibility(View.VISIBLE);
                lyOfTime.setAnimation(AnimationUtils.loadAnimation(this, R.anim.item_time_show));
                break;
            case R.id.hideTime:
                lyOfTime.setVisibility(View.GONE);
                break;
            case R.id.timeToGo:
                //时间选择
                time_picker.setVisibility(View.VISIBLE);
                break;
            case R.id.time_cancel:
                //时间隐藏
                time_picker.setVisibility(View.GONE);
                orderedTime = "现在";
                timeToGo.setText(orderedTime);
                break;
            case R.id.time_confirm:
                //时间隐藏，确定时间
                time_picker.setVisibility(View.GONE);
                timeToGo.setText(orderedTime);
                break;
            case R.id.myDestination:
                intentActivityForResult(this, ChoosePosition.class, "choose", "myDestination", city, REQUESTPOSITIONCODE);
                break;
            case R.id.myPosition:
                intentActivityForResult(this, ChoosePosition.class, "choose", "myPosition", city, REQUESTDESTINATIONCODE);
                break;
            case R.id.tv_pinChe:
                tv_pinChe.setTextColor(Color.YELLOW);
                tv_buPinChe.setTextColor(Color.BLACK);
                orderedMode = "拼车";
                break;
            case R.id.tv_buPinChe:
                tv_pinChe.setTextColor(Color.BLACK);
                tv_buPinChe.setTextColor(Color.YELLOW);
                orderedMode = "不拼车";
                break;
            case R.id.tv_confirm:
                PlanNode startNode;
                if (mPositionInfo == null) {
                    startNode = PlanNode.withLocation(positions.get(0));
                } else {
                    startNode = PlanNode.withLocation(mPositionInfo.location);
                }
//                PlanNode passByNode = PlanNode.withLocation(mPositionInfo.location);
                PlanNode endNode = PlanNode.withLocation(mDestinationInfo.location);
                DrivingRoutePlanOption option = new DrivingRoutePlanOption();
                option.from(startNode).to(endNode);
                mRoutePlanSearch.drivingSearch(option);
                break;
        }
    }

    PoiInfo mPositionInfo;
    PoiInfo mDestinationInfo;

    //获取选取的位置信息
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String value = data.getStringExtra("key");
        if (value.equals("noChoice")) {
            return;
        }
        Bundle bundle = data.getBundleExtra("bundle");
        switch (resultCode) {
            case REQUESTPOSITIONCODE:
                if (value.equals("myPosition")) {
                    mPositionInfo = bundle.getParcelable("PoiInfo");
                    myPosition.setText(mPositionInfo.name);

                    client.setStartLatitude(mPositionInfo.location.latitude);
                    client.setStartLongitude(mPositionInfo.location.longitude);
                    if (!positions.isEmpty()) {
                        positions.set(0, mPositionInfo.location);
                    } else {
                        positions.add(0, mPositionInfo.location);
                    }
                }
                break;
            case REQUESTDESTINATIONCODE:
                if (value.equals("myDestination")) {
                    mDestinationInfo = bundle.getParcelable("PoiInfo");
                    myDestination.setText(mDestinationInfo.name);
                    addDestinationMarker(mDestinationInfo);

                    client.setEndLatitude(mDestinationInfo.location.latitude);
                    client.setEndLongitude(mDestinationInfo.location.latitude);

                    if (positions.size() <= 1) {
                        positions.add(1, mDestinationInfo.location);
                    } else {
                        positions.set(1, mDestinationInfo.location);
                    }
                    //设置拼车按钮可见
                    ly_pinChe.setVisibility(View.VISIBLE);
                    rl_confirm.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    BitmapDescriptor mDestinationMarker;

    /**
     * 添加目的地marker
     *
     * @param info MyCloudPoiInfo
     */
    private void addDestinationMarker(PoiInfo info) {
        mDestinationMarker = BitmapDescriptorFactory.fromResource(R.drawable.icon_destination);
        mBaiduMap.clear();

        LatLng latLng = new LatLng(info.location.latitude, info.location.longitude);

        OverlayOptions options = new MarkerOptions().position(latLng).icon(mDestinationMarker).zIndex(5);
        Marker marker = (Marker) mBaiduMap.addOverlay(options);
    }


    SimpleDateFormat dateFormat;
    String[] date, hour, min, nothing, newHour, newMin;
    String timeNow;
    CustomNumberPicker hourPicker, minutePicker, datePicker;
    TextView time_cancel, time_confirm;
    RelativeLayout time_picker;

    private void initTimePicker() {
        datePicker = (CustomNumberPicker) findViewById(R.id.datepicker);
        hourPicker = (CustomNumberPicker) findViewById(R.id.hourpicker);
        minutePicker = (CustomNumberPicker) findViewById(R.id.minutepicker);

        time_cancel = (TextView) findViewById(R.id.time_cancel);
        time_confirm = (TextView) findViewById(R.id.time_confirm);
        time_cancel.setOnClickListener(this);
        time_confirm.setOnClickListener(this);

        datePicker.setNumberPickerDividerColor(datePicker, Color.GRAY);
        hourPicker.setNumberPickerDividerColor(hourPicker, Color.GRAY);
        minutePicker.setNumberPickerDividerColor(minutePicker, Color.GRAY);

        date = new String[]{"现在", "今天", "明天", "后天"};
        hour = new String[]{"0点", "1点", "2点", "3点", "4点", "5点", "6点", "7点", "8点", "9点", "10点",
                "11点", "12点", "13点", "14点", "15点", "16点", "17点", "18点", "19点", "20点", "21点", "22点", "23点"};
        min = new String[]{"0分", "10分", "20分", "30分", "40分", "50分"};
        nothing = new String[]{"--"};
        //获取当前时间
        timeNow = dateFormat.format(new Date());
        Log.i(TAG, "init: " + timeNow);
        String hourNow = timeNow.substring(11, 13);
        String minNow = timeNow.substring(14, 16);
        //根据时间获取新的数据源
        newHour = getTimeList(Integer.parseInt(hourNow), hour);
        newMin = getTimeList(Integer.parseInt(minNow), min);
        //初始化时间
        mDay = date[1];
        mHour = newHour[0];
        mMin = newMin[0];


        hourPicker.setDisplayedValues(nothing);
        hourPicker.setOnValueChangedListener(this);

        minutePicker.setDisplayedValues(nothing);
        minutePicker.setOnValueChangedListener(this);

        datePicker.setDisplayedValues(date);
        datePicker.setMinValue(0);
        datePicker.setMaxValue(date.length - 1);
        datePicker.setOnValueChangedListener(this);

    }

    String mDay, mHour, mMin;
    int mDateChoice;

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        switch (picker.getId()) {
            case R.id.datepicker:
                switch (newVal) {
                    case 0:
                        mDateChoice = 0;
                        mHour = newHour[0];
                        mMin = newMin[0];
                        changeDataSetWheel(nothing, nothing, hourPicker, minutePicker);
                        break;
                    case 1:
                        mDateChoice = 1;
                        mHour = newHour[0];
                        mMin = newMin[0];
                        if (oldVal - newVal > 0) {
                            changeDataSetWheel(newHour, newMin, hourPicker, minutePicker);
                        } else {
                            changeData(newHour, newMin, hourPicker, minutePicker);
                        }
                        break;
                    case 2:
                        mDateChoice = 2;
                        mHour = hour[0];
                        mMin = hour[0];
                        if (oldVal - newVal > 0) {
                            changeDataSetWheel(hour, min, hourPicker, minutePicker);
                        } else {
                            changeData(hour, min, hourPicker, minutePicker);
                        }
                        break;
                    case 3:
                        mDateChoice = 2;
                        mHour = hour[0];
                        mMin = hour[0];
                        if (oldVal - newVal > 0) {
                            changeDataSetWheel(hour, min, hourPicker, minutePicker);
                        } else {
                            changeData(hour, min, hourPicker, minutePicker);
                        }
                        break;
                }
                mDay = date[newVal];

                break;
            case R.id.hourpicker:
                switch (mDateChoice) {
                    case 0:
                        break;
                    case 1:
                        mHour = newHour[newVal];
                        break;
                    case 2:
                        mHour = hour[newVal];
                        break;
                }
                break;
            case R.id.minutepicker:
                switch (mDateChoice) {
                    case 0:
                        break;
                    case 1:
                        mMin = newMin[newVal];
                        break;
                    case 2:
                        mMin = min[newVal];
                        break;
                }
                break;
        }
        Log.i(TAG, "callTime: //" + "mDay" + mDay + "mHour" + mHour + "mMin" + mMin);
        if (mDateChoice == 0) {
            orderedTime = "现在";
        } else {
            orderedTime = mDay + mHour + mMin;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //开启定位
        if (mBaiduMap != null) {
            mBaiduMap.setMyLocationEnabled(true);
            if (!mLocationClient.isStarted()) {
                mLocationClient.start();
                //开启方向传感器
                myOrientationListener.start();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //停止定位
        mBaiduMap.setMyLocationEnabled(false);
        mLocationClient.stop();
        //关闭方向传感器
        myOrientationListener.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        myMapStatusChangeListener = null;
        myOrientationListener = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();

    }


    boolean isTwice = false;

    /**
     * 2次返回键退出程序
     *
     * @param keyCode keyCode
     * @param event   event
     * @return true or false
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isTwice) {
                isTwice = !isTwice;
                return super.onKeyDown(keyCode, event);
            } else {
                isTwice = !isTwice;
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isTwice = false;
                    }
                }, 4000);
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    RoutePlanSearch mRoutePlanSearch;
    List<LatLng> positions;

    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

    }

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
        if (drivingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
            DrivingRouteLine routeLine = drivingRouteResult.getRouteLines().get(0);
            Log.i(TAG, "onGetDrivingRouteResult: " + routeLine.getDistance());
            mBaiduMap.clear();
            DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaiduMap);
            overlay.setData(routeLine);
            overlay.addToMap();
            overlay.zoomToSpan();
        }
    }

    @Override
    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

    }

    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            return BitmapDescriptorFactory.fromResource(R.drawable.icon_position);
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            return BitmapDescriptorFactory.fromResource(R.drawable.icon_destination);
        }
    }
}

