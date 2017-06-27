package com.wenjiehe.sendsms.entity;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/6/26.
 */

public class SMSText extends DataSupport {
    private int id;
    private String text;

    public SMSText(String text) {
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
