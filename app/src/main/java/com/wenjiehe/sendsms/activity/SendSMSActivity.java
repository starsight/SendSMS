package com.wenjiehe.sendsms.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wenjiehe.sendsms.R;
import com.wenjiehe.sendsms.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.wenjiehe.sendsms.R.id.spin_one;

public class SendSMSActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    @BindView(spin_one)
    Spinner spinner_tel_book;

    @BindView(R.id.spin_two)
    Spinner spinner_send_time;

    //@BindView(R.id.send)
    //Button send;

    private boolean tel_book_selected = false;
    private boolean send_time_selected = false;

    private int tel_book = 0;//0-9 电话本一~十
    private int sendtime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sms);

        ButterKnife.bind(this);

        spinner_tel_book.setOnItemSelectedListener(this);
        spinner_send_time.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spin_one:
                tel_book = position;
                Utils.showToast(SendSMSActivity.this,parent.getItemAtPosition(position).toString());
                break;
            case R.id.spin_two:
                sendtime = position;
                Utils.showToast(SendSMSActivity.this,parent.getItemAtPosition(position).toString());
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
