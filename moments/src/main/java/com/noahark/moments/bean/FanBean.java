package com.noahark.moments.bean;

import java.io.Serializable;

/**
 * ��ע��
 */
public class FanBean implements Serializable {

    private String avatar; //ͷ��URI
    private String nickname;//�ǳ�

    public FanBean(){
        super();
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}