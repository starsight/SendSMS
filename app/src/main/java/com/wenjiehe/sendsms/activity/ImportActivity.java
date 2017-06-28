package com.wenjiehe.sendsms.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.os.Environment;
import android.os.RemoteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.wenjiehe.sendsms.R;
import com.wenjiehe.sendsms.Utils;
import com.wenjiehe.sendsms.entity.PhoneNumber;
import com.wenjiehe.sendsms.view.TelRecyclerViewAdapter;

import org.litepal.crud.DataSupport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImportActivity extends AppCompatActivity implements TelRecyclerViewAdapter.IonSlidingViewClickListener {


    List<PhoneNumber> mData = new ArrayList<>();

    @BindView(R.id.activity_import_recyclerview)
    RecyclerView mRecyclerView;

    private TelRecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {//10 第11个电话本
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);

        ButterKnife.bind(this);

        mData = DataSupport.where("owntable = ?", "10").find(PhoneNumber.class);

        setAdapter();
    }

    private void setAdapter() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter = new TelRecyclerViewAdapter(this, mData));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_import_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.activity_import:
                new AlertDialog.Builder(ImportActivity.this)
                        .setTitle("提示")
                        .setMessage("将从外部存储的根目录下读取import.txt文件，请确认权限，文件路径与格式是否正确！")
                        .setPositiveButton("继续", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                importFromTxt();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                break;
            case R.id.activity_import_delete:
                DataSupport.deleteAll(PhoneNumber.class, "owntable =?", "10");
                mData.clear();
                mAdapter.notifyDataSetChanged();
                Utils.showToast(ImportActivity.this,"删除成功！");
                break;

            case R.id.activity_import_detail:
                StringBuilder sb = new StringBuilder();
                String messaage = sb.append("当前数据库有 ").append(mData.size()).append(" 条数据,").toString();
                Utils.showAlertDialog(ImportActivity.this,"详情",messaage);
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

    private void importFromTxt() {
        List<PhoneNumber> temp = new ArrayList<>();
        try {
            File urlFile = new File(Environment.getExternalStorageDirectory(), "import.txt");
            if (!urlFile.exists()) {
                Utils.showAlertDialog(ImportActivity.this, "错误", "文件不存在！");
                return;
            } else {
                InputStreamReader isr = new InputStreamReader(new FileInputStream(urlFile), "UTF-8");
                BufferedReader br = new BufferedReader(isr);
                String str = "";
                String mimeTypeLine = null;
                while ((mimeTypeLine = br.readLine()) != null) {
                    //str = str+mimeTypeLine;
                    if (Utils.checkPhoneNumber(mimeTypeLine))
                        temp.add(new PhoneNumber(10, mimeTypeLine));
                    else {
                        Utils.showToast(ImportActivity.this, mimeTypeLine + " 不是有效的手机格式！跳过！");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        DataSupport.saveAll(temp);
        mData.addAll(temp);
        mAdapter.notifyDataSetChanged();

    }
}
