package com.wenjiehe.sendsms.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.RemoteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.wenjiehe.sendsms.R;
import com.wenjiehe.sendsms.Utils;
import com.wenjiehe.sendsms.entity.PhoneNumber;
import com.wenjiehe.sendsms.view.TelRecyclerViewAdapter;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class TelActivity extends AppCompatActivity  implements TelRecyclerViewAdapter.IonSlidingViewClickListener {

    private int phone_book_num=0;

    private List<PhoneNumber> telList = new ArrayList<>();
    private List<PhoneNumber> newAddTel = new ArrayList<>();//新添加的电话号码
    private int allTelNum = 0;

    @BindView(R.id.activity_tel_recyclerview)
    RecyclerView mRecyclerView;

    private TelRecyclerViewAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tel);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        phone_book_num = intent.getIntExtra("phonebook",0);

        telList = DataSupport.where("owntable = ?",phone_book_num+"").find(PhoneNumber.class);
        //List<PhoneNumber> listAll = DataSupport.findAll(PhoneNumber.class);

        setAdapter();
    }

    private void setAdapter(){
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter = new TelRecyclerViewAdapter(this,telList));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_tel_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.activity_tel_settings_edit:
                Intent intent = new Intent(TelActivity.this,EditTelActivity.class);
                intent.putExtra("phonebook",phone_book_num);
                intent.putExtra("hasTelNumber",telList.size());
                startActivityForResult(intent,1);
                break;
            case R.id.activity_tel_settings_delete:
                new AlertDialog.Builder(TelActivity.this)
                        .setTitle("提示")
                        .setMessage("确认删除该电话本里的所有号码？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //telList.clear();
                                Toast.makeText(TelActivity.this,"一共删除了"+telList.size()+"条电话信息",Toast.LENGTH_SHORT).show();
                                mAdapter.removeAllData(phone_book_num);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();

                break;
            case R.id.activity_tel_settings_export:
                new AlertDialog.Builder(TelActivity.this)
                        .setTitle("提示")
                        .setMessage("将会删除系统中的联系人，确认把电话本中的数据导入到系统联系人中？")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO: 2017/6/26  删除所有联系人
                                //Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
                                //getContentResolver().delete(uri,"_id!=-1", null);
                                try {
                                    Utils.batchAddContact(TelActivity.this,telList);
                                    Utils.showToast(TelActivity.this,"导出数据成功！");
                                } catch (RemoteException e) {

                                } catch (OperationApplicationException e) {
                                    new AlertDialog.Builder(TelActivity.this)
                                            .setTitle("错误")
                                            .setMessage("添加联系人异常，请确认权限和数据库数据是否正确!")
                                            .setPositiveButton("好", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            })
                                            .show();

                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton("下次再说", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
                break;
            case R.id.activity_tel_settings_detail:
                StringBuilder sb = new StringBuilder();
                String messaage = sb.append("当前数据库有 ").append(telList.size()).append(" 条数据,")
                        .append("还可以添加 ").append(300-telList.size()).append(" 条数据.").toString();
                Utils.showAlertDialog(TelActivity.this,"详情",messaage);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(View view, int position) {
        //Log.i(TAG,"点击项："+position);
    }

    @Override
    public void onDeleteBtnCilck(View view, int position) {
        //Log.i(TAG,"删除项："+position);
        mAdapter.removeData(position);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        telList = DataSupport.where("owntable = ?",phone_book_num+"").find(PhoneNumber.class);

        mAdapter.changeAllData(telList);
    }
}
