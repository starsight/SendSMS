package com.wenjiehe.sendsms.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.wenjiehe.sendsms.R;
import com.wenjiehe.sendsms.adapter.SMSTextAdapter;
import com.wenjiehe.sendsms.entity.SMSText;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.R.id.message;
import static org.litepal.crud.DataSupport.findAll;

public class EditSMSTextActivity extends AppCompatActivity {

    @BindView(R.id.activity_edit_sms_text_listview)
    ListView listView;

    SMSTextAdapter mAdapter;

    private Context mContext;

    private List<SMSText> mData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_smstext);

        ButterKnife.bind(this);


        mContext = EditSMSTextActivity.this;
        getSMSTextForDatabase();

        mAdapter = new SMSTextAdapter(mData, mContext);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //mData.get(position)
                editSMSText(position,"短信"+position+"内容编辑");
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void getSMSTextForDatabase(){
        mData = DataSupport.findAll(SMSText.class);
        mData.add(new SMSText("电话本一二电话本一二电话本一二电话本一二电话本一二电话本一二电话本一二电话本一二电话本一二电话本一二电话本一二电话本一二电话本一二电话本一二电话本一二电话本一二电话本一二电话本一二电话本一二电话本一二"));
        for(int i=mData.size();i<50;i++){
            mData.add(new SMSText("点击编辑短信:"+i));
        }
    }

    private void editSMSText(final int position,String message){
        final EditText et = new EditText(this);
        //et.setSingleLine();
        //et.requestFocus(); //请求获取焦点
        //InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(message)
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String input = et.getText().toString();
                        if (!input.equals("")) {
                            //Toast.makeText(getApplicationContext(), "内容不能为空" + input, Toast.LENGTH_LONG).show();
                            mData.get(position).setText(input);
                            dialog.dismiss();
                        }
                    }
                })
                .setNegativeButton("取消", null);

        AlertDialog ad = builder.create();
        ad.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        ad.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        ad.show();
    }
}
