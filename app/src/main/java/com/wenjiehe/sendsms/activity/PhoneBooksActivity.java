package com.wenjiehe.sendsms.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.wenjiehe.sendsms.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PhoneBooksActivity extends AppCompatActivity {

    @BindView(R.id.activity_phone_books_list)
    ListView listview;

    List<String> telList =new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_books);

        ButterKnife.bind(this);
        //ListView listview =(ListView) findViewById(R.id.activity_phone_books_list);

        //要显示的数据
        String[] strs = {"电话本一","电话本二","电话本三","电话本四","电话本五","电话本六","电话本七","电话本八","电话本九","电话本十"};
        //创建ArrayAdapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this,android.R.layout.simple_expandable_list_item_1,strs);
        //获取ListView对象，通过调用setAdapter方法为ListView设置Adapter设置适配器

        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent =new Intent(PhoneBooksActivity.this,TelActivity.class);
                intent.putExtra("phonebook",position);
                startActivity(intent);
            }
        });
    }


}
