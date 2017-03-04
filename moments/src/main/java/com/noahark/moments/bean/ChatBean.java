package com.noahark.moments.bean;

import java.io.Serializable;

/**
 * ��ע��
 */
public class ChatBean implements Serializable {

    private String avatar; //ͷ��URI
    private String nickname;//�ǳ�
    private String date; //����
    private String content; //���һ����Ϣ������

    public ChatBean() {
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}