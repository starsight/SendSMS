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

public class SendSMSActivity extends AppCompatActivity  {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sms);


    }


}
