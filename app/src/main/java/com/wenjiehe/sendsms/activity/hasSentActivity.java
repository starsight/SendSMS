package com.wenjiehe.sendsms.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.wenjiehe.sendsms.R;
import com.wenjiehe.sendsms.Utils;
import com.wenjiehe.sendsms.entity.PhoneNumber;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class hasSentActivity extends AppCompatActivity {


    @BindView(R.id.activity_has_sent_list)
    ListView listView;

    List<PhoneNumber> telList = new ArrayList<>();
    List<String> hasSentList = new ArrayList<>();

    ArrayAdapter<String> adapter;

    //String[] strs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_has_sent);

        ButterKnife.bind(this);

        telList = DataSupport.where("owntable = ?", 12 + "").find(PhoneNumber.class);

        //要显示的数据
        /*if (telList.isEmpty()) {
            strs = new String[]{"当前不存在已发送号码"};
        } else {
            strs = new String[telList.size()];
            int i = 0;
            for (PhoneNumber phoneNumber : telList) {
                strs[i] = phoneNumber.getNumber();
                i++;
            }
        }*/
        if(telList.isEmpty()){
            hasSentList.add("当前不存在已发送号码");
        }else{
            for(PhoneNumber phoneNumber: telList){
                hasSentList.add(phoneNumber.getNumber());
            }
        }

        //String[] strs = (String[])telList.toArray();
        //创建ArrayAdapter
        adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_expandable_list_item_1, hasSentList);
        //获取ListView对象，通过调用setAdapter方法为ListView设置Adapter设置适配器

        listView.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_has_sent_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.activity_has_sent_delete:
                new AlertDialog.Builder(hasSentActivity.this)
                        .setTitle("提示")
                        .setMessage("确认删除所有已发送的号码？")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DataSupport.deleteAll(PhoneNumber.class, "owntable = ?", "12");
                                //adapter.setNotifyOnChange(true);
                                adapter.clear();
                                hasSentList.add("当前不存在已发送号码");
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();

                break;

            case R.id.activity_has_sent_detail:
            {
                StringBuilder sb = new StringBuilder();
                String messaage = sb.append("当前数据库有 ").append(telList.size()).append(" 条数据.").toString();
                Utils.showAlertDialog(hasSentActivity.this,"详情",messaage);
                break;
            }

        }
        return super.onOptionsItemSelected(item);
    }
}
