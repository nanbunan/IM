package com.example.administrator.im.model.bean;

/**
 * Created by Administrator on 2017/6/4.
 */
//用户账号的bean类
public class UserInfo {
    private String name;//用户名称
    private String hxid;//环信id
    private String nick;//昵称
    private String photo;//头像

    public UserInfo() {
    }

    public UserInfo(String name) {
        this.name = name;
        this.hxid = name;
        this.nick = name;
        this.photo = name;
    }

    public String getName() {
        return name;
    }

    public String getHxid() {
        return hxid;
    }

    public String getNick() {
        return nick;
    }

    public String getPhoto() {
        return photo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHxid(String hxid) {
        this.hxid = hxid;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "name='" + name + '\'' +
                ", hxid='" + hxid + '\'' +
                ", nick='" + nick + '\'' +
                ", photo='" + photo + '\'' +
                '}';
    }
}
