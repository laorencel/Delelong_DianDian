package com.delelong.diandian;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BAIDUMAPFORTEST";
    public static final String URL_LOGIN = "http://192.168.4.103:8080/Jfinal/api/login";
    public static final String APPTYPE_CLIENT = "2";

    /**
     * 注册
     *
     * @param url_upData url
     * @param name       用户名
     * @param pwd        密码
     * @return 注册结果
     */
    public List<String> registerApp(String url_upData, String name, String verification, String pwd, String rePwd) {
        String upDataStr = "phone=" + name + "&code=" + verification + "&rePassword=" + rePwd + "&password=" + pwd;//注册

        getHttpMsg(url_upData, upDataStr);
        return loginResultByJson(httpResult);
    }

    /**
     * 登录
     *
     * @param url_upData url
     * @param name       用户名
     * @param pwd        密码
     * @return 登录结果
     */
    public List<String> loginApp(String url_upData, String name, String pwd) {
        String upDataStr = "username=" + name + "&password=" + pwd;//注册

        //利用线程获取数据
        getHttpMsg(url_upData, upDataStr);
        //json数据解析登录结果
        return loginResultByJson(httpResult);
    }

    private boolean firstLogin = true;
    String token, secret;

    /**
     * 使用token与sercet登陆，无需输入账号密码
     *
     * @param url_upData
     * @param name
     * @param pwd
     * @return
     */
    public List<String> loginAppByPreferences(String url_upData, String name, String pwd) {
        SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        token = preferences.getString("token", null);
        secret = preferences.getString("sercet", null);

        firstLogin = false;
        String upDataStr = "username=" + name + "&password=" + pwd;//注册
        getHttpMsg(url_upData, upDataStr);
        return loginResultByJson(httpResult);
    }

    /**
     * 修改密码
     *
     * @param url_upData url
     * @param password      旧密码
     * @param newPwd        新密码
     * @param rePwd         确认密码
     * @return 修改密码结果
     */
    public List<String> modifyPwd(String url_upData, String password, String newPwd, String rePwd) {
        String upDataStr = "password=" + password + "&newPassword=" + newPwd + "&rePassword=" + rePwd;//修改密码

        //利用线程获取数据
        getHttpMsg(url_upData, upDataStr);
        return loginResultByJson(httpResult);
    }


    /**
     * @param result 从服务器获取的数据
     * @return 登录结果，是否成功登录
     */
    public List<String> loginResultByJson(String result) {
        return analysisJsonObject(result);
    }

    /**
     * @param result result
     * @return json数据解析返回String集合
     */
    public List<String> analysisJsonObject(String result) {
        JSONObject object;
        List<String> list = new ArrayList<>();
        try {
            object = new JSONObject(result);
            String status = object.getString("status");
            String msg = object.getString("msg");
            list.add(status);
            list.add(msg);
            JSONObject data = object.getJSONObject("data");
            //注册、修改密码和获取验证码时 data为空
            if (data != null) {
                String token = (String) data.get("token");
                String secret = (String) data.get("secret");
                list.add(token);
                list.add(secret);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    String httpResult = null;

    /**
     * 封装线程，简化代码
     *
     * @param url_upData
     * @param upDataStr
     */
    public void getHttpMsg(String url_upData, String upDataStr) {
        MyHttpResultThread myHttpResultThread = new MyHttpResultThread(url_upData, upDataStr);
        myHttpResultThread.start();
        try {
            myHttpResultThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 开启线程获得服务器数据
     */
    class MyHttpResultThread extends Thread {
        String url_upData, detailStr;

        MyHttpResultThread(String url_upData, String detailStr) {
            this.url_upData = url_upData;
            this.detailStr = detailStr;
        }

        @Override
        public void run() {
            super.run();
            httpResult = getHttpResult(url_upData, detailStr);
        }
    }

    String serialNumber = getSerialNumber();

    /**
     * @param url_upData url_upData
     * @param detailStr  detailStr
     * @return 返回服务器数据
     */
    public String getHttpResult(String url_upData, String detailStr) {
        StringBuilder stringBuilder = null;
        try {
            URL url = new URL(url_upData);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            //appType：请求的类型，1:表示司机端;2:表示普通会员
            connection.addRequestProperty("appType", APPTYPE_CLIENT);
            //deviceno：设备序列号
            connection.addRequestProperty("deviceno", serialNumber);

            //登陆接口
            if (url_upData.substring(url_upData.length() - 5, url_upData.length()).equals("login")) {
                //以后登陆加上token和sercet，免登陆
//                if (!firstLogin) {
////                    Log.i(TAG, "getHttpResult: token"+token+"//secret"+secret);
////                    connection.addRequestProperty("token", token);
////                    connection.addRequestProperty("secret", secret);
//                }
            }
            //注册接口
            else if (url_upData.substring(url_upData.length() - 8, url_upData.length()).equals("register")) {

            }
            //修改密码接口
            else if (url_upData.substring(url_upData.length() - 8, url_upData.length()).equals("password")){
                connection.addRequestProperty("token", token);
                connection.addRequestProperty("secret", secret);
            }

            connection.setDoOutput(true);
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(detailStr.getBytes());

            InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream(), "utf-8");
            char[] chars = new char[1024];
            int len = 0;
            stringBuilder = new StringBuilder();
            while ((len = inputStreamReader.read(chars)) != -1) {
                stringBuilder.append(chars, 0, len);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }


    /**
     * 获取验证码
     *
     * @param url_upData
     * @param phone
     * @return
     */
    public List<String> getHttpResultForVerification(String url_upData, String phone, String type) {
        StringBuilder stringBuilder = null;
        String upDataStr = "phone=" + phone + "&type=" + type;
        try {
            URL url = new URL(url_upData);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            //请求的类型，1:表示司机端;2:表示普通会员
            connection.addRequestProperty("appType", APPTYPE_CLIENT);
            connection.addRequestProperty("deviceno", serialNumber);

            connection.setDoOutput(true);
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(upDataStr.getBytes());

            InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream(), "utf-8");
            char[] chars = new char[1024];
            int len = 0;
            stringBuilder = new StringBuilder();
            while ((len = inputStreamReader.read(chars)) != -1) {
                stringBuilder.append(chars, 0, len);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return loginResultByJson(stringBuilder.toString());
    }

    /**
     * 定位到我的位置
     */
    /**
     * 定位到我的位置
     */
    public void centerToMyLocation(AMap aMap, double myLatitude, double myLongitude) {
        aMap.animateCamera(CameraUpdateFactory.changeLatLng(new LatLng(
                myLatitude, myLongitude)));
    }

    public <T> void intentActivityForResult(Context context, Class<T> tClass, String key, String value, String city, int requestCode) {
        Intent intent = new Intent(context, tClass);
        intent.putExtra(key, value);
        intent.putExtra("city", city);
        startActivityForResult(intent, requestCode);
    }

    /**
     * @return 获取手机序列号
     */
    public String getSerialNumber() {
        String serial = null;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialno");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serial;
    }

}
