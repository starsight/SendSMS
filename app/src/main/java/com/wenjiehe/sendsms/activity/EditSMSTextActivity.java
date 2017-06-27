package com.wenjiehe.sendsms.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.wenjiehe.sendsms.R;
import com.wenjiehe.sendsms.adapter.SMSTextAdapter;
import com.wenjiehe.sendsms.entity.PhoneNumber;
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
                editSMSText(position,"短信"+(position+1)+"内容编辑");
            }
        });
    }

    private void getSMSTextForDatabase(){
        mData = DataSupport.findAll(SMSText.class);
        for(int i=mData.size();i<50;i++){
            mData.add(new SMSText("点击编辑短信:"+(i+1)));
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
                        //if(input.length()<=140)
                        {
                            if (!input.equals("")) {
                                //Toast.makeText(getApplicationContext(), "内容不能为空" + input, Toast.LENGTH_LONG).show();
                                mData.get(position).setText(input);

                                mAdapter.notifyDataSetChanged();
                                mData.get(position).save();

                                dialog.dismiss();
                            } else {
                                mData.get(position).setText("点击编辑短信:" + (position + 1));
                                mAdapter.notifyDataSetChanged();
                                mData.get(position).delete();
                                dialog.dismiss();
                            }
                        }
                    }
                })
                .setNegativeButton("取消", null);

        AlertDialog ad = builder.create();
        ad.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        ad.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        ad.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_edit_sms_text,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.activity_edit_sms_text_delete:
                new AlertDialog.Builder(EditSMSTextActivity.this)
                        .setTitle("提示")
                        .setMessage("确认删除所有内容？")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DataSupport.deleteAll(SMSText.class);

                                mData.clear();
                                for(int i=mData.size();i<50;i++){
                                    mData.add(new SMSText("点击编辑短信:"+(i+1)));
                                }

                                mAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();

                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
