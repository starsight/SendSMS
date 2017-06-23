package com.wenjiehe.sendsms.entity;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/6/23.
 */

public class PhoneNumber extends DataSupport{

    private int id;
    private int ownTable;//联系人属于第几张表 0-9
    private String name;
    private String number;
    private boolean isSendSMS;

    public PhoneNumber() {
        super();
    }

    public PhoneNumber(int table, String name, String number) {
        super();
        ownTable = table;
        this.name = name;
        this.number = number;
        isSendSMS= false;
    }

    public PhoneNumber(int id, int table, String name, String number) {
        this.id = id;
        ownTable = table;
        this.name = name;
        this.number = number;
        isSendSMS= false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }


    public boolean isSendSMS() {
        return isSendSMS;
    }

    public void setSendSMS(boolean sendSMS) {
        isSendSMS = sendSMS;
    }

    public int getOwnTable() {
        return ownTable;
    }

    public void setOwnTable(int ownTable) {
        this.ownTable = ownTable;
    }

    @Override
    public String toString() {
        return "PhoneNumber{" +
                "id=" + id +
                ", ownTable=" + ownTable +
                ", name='" + name + '\'' +
                ", number='" + number + '\'' +
                ", isSendSMS=" + isSendSMS +
                '}';
    }
}
