package com.noahark.moments.bean;

import java.io.Serializable;
import java.util.List;
import java.sql.Date;

/**
 * Created by chicken on 2016/11/21.
 */
public class UserZoneBean implements Serializable {

    private Date time; //ʱ��

    private String picture; //ͼƬ�б�

    private String content; //����

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
