package com.wenjiehe.sendsms.entity;

import org.litepal.crud.DataSupport;

import java.util.Date;

/**
 * Created by Administrator on 2017/6/27.
 */

public class VertifyCode extends DataSupport {
    private int id;
    private String code;
    private String password;
    private long time;

    public VertifyCode(String code) {
        this.code = code;
        this.time = new Date().getTime()/1000;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
