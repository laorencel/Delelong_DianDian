package com.delelong.baidumaptest.bean;

import java.io.Serializable;

/**
 * Created by Lawrence on 2016/8/23.
 */
public class Client implements Serializable {
    private String name;//用户名
    private String phone;//手机号
    private String payAccount;//支付账号
    private String homeAddress;//家庭住址
    private String companyAddress;//公司地址
    private int userClass;//用户等级
    private int point;//会员积分
    private double startLatitude;//经度
    private double startLongitude;//纬度
    private double endLatitude;//经度
    private double endLongitude;//纬度
    private String historyRoute;//历史行程

    public Client() {
    }

    public Client(String name, String phone, String payAccount, String homeAddress, String companyAddress, int userClass, int point, double startLatitude, double startLongitude, double endLatitude, double endLongitude, String historyRoute) {
        this.name = name;
        this.phone = phone;
        this.payAccount = payAccount;
        this.homeAddress = homeAddress;
        this.companyAddress = companyAddress;
        this.userClass = userClass;
        this.point = point;
        this.startLatitude = startLatitude;
        this.startLongitude = startLongitude;
        this.endLatitude = endLatitude;
        this.endLongitude = endLongitude;
        this.historyRoute = historyRoute;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPayAccount() {
        return payAccount;
    }

    public void setPayAccount(String payAccount) {
        this.payAccount = payAccount;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public int getUserClass() {
        return userClass;
    }

    public void setUserClass(int userClass) {
        this.userClass = userClass;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public double getStartLatitude() {
        return startLatitude;
    }

    public void setStartLatitude(double startLatitude) {
        this.startLatitude = startLatitude;
    }

    public double getStartLongitude() {
        return startLongitude;
    }

    public void setStartLongitude(double startLongitude) {
        this.startLongitude = startLongitude;
    }

    public double getEndLatitude() {
        return endLatitude;
    }

    public void setEndLatitude(double endLatitude) {
        this.endLatitude = endLatitude;
    }

    public double getEndLongitude() {
        return endLongitude;
    }

    public void setEndLongitude(double endLongitude) {
        this.endLongitude = endLongitude;
    }

    public String getHistoryRoute() {
        return historyRoute;
    }

    public void setHistoryRoute(String historyRoute) {
        this.historyRoute = historyRoute;
    }
}
