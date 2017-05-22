package com.phoenix.social.model.bean;

/**
 * 用户账号信息的Bean
 */

public class UserInfo {
    private String name;//用户名称
    private String hxid;//环信ID
    private String nick;//用户的昵称
    private String photo;//头像

    public UserInfo() {
    }

    public UserInfo(String name) {
        //为了简化操作这样设置，实际项目中应各自设置
        this.name = name;
        this.hxid = name;
        this.nick = name;
    }

    public String getHxid() {
        return hxid;
    }

    public void setHxid(String hxid) {
        this.hxid = hxid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getPhoto() {
        return photo;
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
