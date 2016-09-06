package com.delelong.diandian.bean;

import java.io.Serializable;

/**
 * Created by Lawrence on 2016/8/23.
 */
public class Client implements Serializable {

    private String real_name;//真实姓名
    private String phone;//手机号码
    private String post_code;//邮编
    private String urgent_name;//紧急联系人名称
    private String urgent_phone;//紧急号码
    private String nick_name;//昵称
    private String certificate_type;//
    private String head_portrait;//
    private String county;//
    private String province;//
    private String city;//
    private String address;//
    private String email;//
    private String gender;//
    private String certificate_no;//

    public Client() {

    }

    /**
     *
     * @param real_name 真实姓名
     * @param phone//手机号码
     * @param post_code//邮编
     * @param urgent_name//紧急联系人名称
     * @param urgent_phone//紧急号码
     * @param nick_name//昵称
     * @param certificate_type//证件类型(Number:1:身份证;)
     * @param head_portrait//头像图片地址
     * @param county//所属县
     * @param province//所属省
     * @param city//所属城市
     * @param address//地址
     * @param email//邮箱
     * @param gender//性别(Number:1:男;2:女;)
     * @param certificate_no//证件号
     */
    public Client(String real_name, String phone, String post_code, String urgent_name, String urgent_phone,
                  String nick_name, String certificate_type, String head_portrait, String county,
                  String province, String city, String address, String email, String gender, String certificate_no) {
        this.real_name = real_name;
        this.phone = phone;
        this.post_code = post_code;
        this.urgent_name = urgent_name;
        this.urgent_phone = urgent_phone;
        this.nick_name = nick_name;
        this.certificate_type = certificate_type;
        this.head_portrait = head_portrait;
        this.county = county;
        this.province = province;
        this.city = city;
        this.address = address;
        this.email = email;
        this.gender = gender;
        this.certificate_no = certificate_no;
    }
}
