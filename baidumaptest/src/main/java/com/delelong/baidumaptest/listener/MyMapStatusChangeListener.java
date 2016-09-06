package com.delelong.baidumaptest.listener;

import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

/**
 * Created by Administrator on 2016/8/18.
 */
public class MyMapStatusChangeListener implements BaiduMap.OnMapStatusChangeListener {

    private static final String TAG = "BAIDUMAPFORTEST";
    TextView textView;

    public MyMapStatusChangeListener(TextView textView) {
        this.textView = textView;
    }

    @Override
    public void onMapStatusChangeStart(MapStatus mapStatus) {
        textView.setText("正在定位起点...");
    }

    @Override
    public void onMapStatusChange(MapStatus mapStatus) {

    }

    @Override
    public void onMapStatusChangeFinish(MapStatus mapStatus) {
        updateMapState(mapStatus);
    }
    LatLng centerOfMap;
    void updateMapState(MapStatus mapStatus) {
        centerOfMap = mapStatus.target;

        //反地理编码查询，获得地图中心点位置信息
        GeoCoder geoCoder = GeoCoder.newInstance();
        geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(centerOfMap));
        geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {}
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                geoCodeResulutListener.getReverseGeoCodeResult(reverseGeoCodeResult);
            }
        });
    }

    public LatLng getLatLng(){
        return centerOfMap;
    }

    //回调获取POI
    public GeoCodeResulutListener geoCodeResulutListener;
    public void getGeoCodeResultListener(GeoCodeResulutListener geoCodeResulutListener){
        this.geoCodeResulutListener = geoCodeResulutListener;
    }
    public interface GeoCodeResulutListener{
        void getReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult);
    }
}
