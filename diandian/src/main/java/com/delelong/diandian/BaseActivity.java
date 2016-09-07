package com.delelong.diandian;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.LatLng;
import com.delelong.diandian.bean.Client;
import com.delelong.diandian.utils.ToastUtil;

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
    public static final String DEVICE_TYPE = "1";

    /**
     * 注册
     *
     * @param url_upDate url
     * @param name       用户名
     * @param pwd        密码
     * @return 注册结果
     */
    public List<String> registerApp(String url_upDate, String name, String verification, String pwd, String rePwd) {
        String upDateStr = "phone=" + name + "&code=" + verification + "&rePassword=" + rePwd + "&password=" + pwd;//注册

        getHttpMsg(url_upDate, upDateStr);
        return loginResultByJson(httpResult);
    }

    /**
     * 登录
     *
     * @param url_upDate url
     * @param name       用户名
     * @param pwd        密码
     * @return 登录结果
     */
    public List<String> loginApp(String url_upDate, String name, String pwd) {
        String upDateStr = "username=" + name + "&password=" + pwd;//注册

        //利用线程获取数据
        getHttpMsg(url_upDate, upDateStr);
        //json数据解析登录结果
        return loginResultByJson(httpResult);
    }

    private boolean firstLogin = true;
    String token, secret;
    SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);

    /**
     * 使用token与sercet登陆，无需输入账号密码
     *
     * @param url_upDate
     * @param name
     * @param pwd
     * @return
     */
    public List<String> loginAppByPreferences(String url_upDate, String name, String pwd) {
        token = preferences.getString("token", null);
        secret = preferences.getString("sercet", null);

        firstLogin = false;
        String upDateStr = "username=" + name + "&password=" + pwd;//注册
        getHttpMsg(url_upDate, upDateStr);
        return loginResultByJson(httpResult);
    }

    /**
     * 修改密码
     *
     * @param url_upDate url
     * @param password   旧密码
     * @param newPwd     新密码
     * @param rePwd      确认密码
     * @return 修改密码结果
     */
    public List<String> modifyPwd(String url_upDate, String password, String newPwd, String rePwd) {
        String upDateStr = "password=" + password + "&newPassword=" + newPwd + "&rePassword=" + rePwd;//修改密码

        //利用线程获取数据
        getHttpMsg(url_upDate, upDateStr);
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
     * @param url_upDate
     * @param upDateStr
     */
    public void getHttpMsg(String url_upDate, String upDateStr) {
        MyHttpResultThread myHttpResultThread = new MyHttpResultThread(url_upDate, upDateStr);
        myHttpResultThread.start();
        try {
            myHttpResultThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 开启线程获得服务器数据POST
     */
    class MyHttpResultThread extends Thread {
        String url_upDate, detailStr;

        MyHttpResultThread(String url_upDate, String detailStr) {
            this.url_upDate = url_upDate;
            this.detailStr = detailStr;
        }

        @Override
        public void run() {
            super.run();
            httpResult = getHttpResult(url_upDate, detailStr);
        }
    }

    String serialNumber = getSerialNumber();

    /**
     * @param url_upDate url_upDate
     * @param detailStr  detailStr
     * @return 返回服务器数据
     */
    public String getHttpResult(String url_upDate, String detailStr) {
        StringBuilder stringBuilder = null;
        try {
            URL url = new URL(url_upDate);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            //appType：请求的类型，1:表示司机端;2:表示普通会员
            connection.addRequestProperty("appType", APPTYPE_CLIENT);
            //deviceno：设备序列号
            connection.addRequestProperty("deviceno", serialNumber);
            connection.addRequestProperty("devicetype", DEVICE_TYPE);
            //登陆接口
            if (url_upDate.substring(url_upDate.length() - 5, url_upDate.length()).equals("login")) {
                //以后登陆加上token和sercet，免登陆
//                if (!firstLogin) {
////                    Log.i(TAG, "getHttpResult: token"+token+"//secret"+secret);
////                    connection.addRequestProperty("token", token);
////                    connection.addRequestProperty("secret", secret);
//                }
            }
            //注册接口
            else if (url_upDate.substring(url_upDate.length() - 8, url_upDate.length()).equals("register")) {

            }
            //修改密码接口
            else if (url_upDate.substring(url_upDate.length() - 8, url_upDate.length()).equals("password")) {
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
            if (e.toString().contains("Network is unreachable")) {
                ToastUtil.show(this, "请先连接网络");
            }
        }
        Log.i(TAG, "getHttpResult: " + stringBuilder.toString());
        return stringBuilder.toString();
    }


    /**
     * 获取验证码
     *
     * @param url_upDate
     * @param phone
     * @return
     */
    public List<String> getHttpResultForVerification(String url_upDate, String phone, String type) {
        StringBuilder stringBuilder = null;
        String upDateStr = "phone=" + phone + "&type=" + type;
        try {
            URL url = new URL(url_upDate);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            //请求的类型，1:表示司机端;2:表示普通会员
            connection.addRequestProperty("appType", APPTYPE_CLIENT);
            connection.addRequestProperty("deviceno", serialNumber);
            connection.addRequestProperty("devicetype", DEVICE_TYPE);

            connection.setDoOutput(true);
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(upDateStr.getBytes());

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
     * GET方法请求数据
     *
     * @param url_upDate
     * @return result
     */
    public String getHttpResultGET(String url_upDate) {
        token = preferences.getString("token", null);
        secret = preferences.getString("sercet", null);
//        String str = url_upDate + "?name=" + name + "&paw=" + pwd + "&category=user";
        StringBuilder stringBuilder = null;
        try {
            URL url = new URL(url_upDate);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            //appType：请求的类型，1:表示司机端;2:表示普通会员
            connection.addRequestProperty("appType", APPTYPE_CLIENT);
            //deviceno：设备序列号
            connection.addRequestProperty("deviceno", serialNumber);
            connection.addRequestProperty("devicetype", DEVICE_TYPE);
            connection.addRequestProperty("token", token);
            connection.addRequestProperty("secret", secret);

            InputStreamReader inputStream = new InputStreamReader(connection.getInputStream(), "utf-8");
            char[] chars = new char[1024];
            int len = 0;
            stringBuilder = new StringBuilder();
            while ((len = inputStream.read(chars)) != -1) {
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
     * 开启线程获得服务器数据GET
     */
    class MyHttpResultGETThread extends Thread {
        String url_upDate;

        MyHttpResultGETThread(String url_upDate) {
            this.url_upDate = url_upDate;
        }

        @Override
        public void run() {
            super.run();
            httpResult = getHttpResultGET(url_upDate);
        }
    }

    public void getHttpMsgByGET(String url_upDate) {
        MyHttpResultGETThread myHttpResultThread = new MyHttpResultGETThread(url_upDate);
        myHttpResultThread.start();
        try {
            myHttpResultThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Client getClientByGET(String url_upDate) {
        //利用线程获取数据
        getHttpMsgByGET(url_upDate);
        return getClientInfoByJson(httpResult);
    }

    /**
     * @param result httpResult
     * @return json数据解析返回Client
     */
    public Client getClientInfoByJson(String result) {
        Client client = null;
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
                String login_id = (String) data.get("login_id");
                String last_update_time = (String) data.get("last_update_time");

                String phone = (String) data.get("phone");//手机号码
                String post_code = (String) data.get("post_code");//邮编
                String type = (String) data.get("type");//
                String city = (String) data.get("city");//所属城市
                String id = (String) data.get("id");//
                String nick_name = (String) data.get("nick_name");//昵称
                String certificate_type = (String) data.get("certificate_type");//证件类型(Number:1:身份证;)
                String head_portrait = (String) data.get("head_portrait");//头像图片地址
                String urgent_phone = (String) data.get("urgent_phone");//紧急号码
                String urgent_name = (String) data.get("urgent_name");//紧急联系人名称
                String certificate_no = (String) data.get("certificate_no");//证件号
                String county = (String) data.get("county");//所属县
                String email = (String) data.get("email");//邮箱
                String address = (String) data.get("address");//地址
                String real_name = (String) data.get("real_name");//真实姓名
                String province = (String) data.get("province");//所属省
                String gender = (String) data.get("gender");//性别(Number:1:男;2:女;)
                String level = (String) data.get("level");//会员等级

                client = new Client(level, phone, post_code, urgent_name, urgent_phone, nick_name, certificate_type, head_portrait, county,
                        province, city, address, email, gender, certificate_no, real_name);
            }
            client.setStatusList(list);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return client;
    }
    //////////////////////////////////////////////////////////////////////////
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
