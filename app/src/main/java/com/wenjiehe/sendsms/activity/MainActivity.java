package com.wenjiehe.sendsms.activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.idescout.sql.SqlScoutServer;
import com.wenjiehe.sendsms.R;
import com.wenjiehe.sendsms.Utils;
import com.wenjiehe.sendsms.entity.PhoneNumber;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.R.attr.phoneNumber;
import static android.R.attr.x;
import static android.R.attr.y;
import static com.wenjiehe.sendsms.R.id.sendtime;
import static com.wenjiehe.sendsms.R.id.spin_one;
import static com.wenjiehe.sendsms.Utils.generateRandomTime;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener {

    @BindView(R.id.spin_one)
    Spinner spinner_tel_book;

    @BindView(sendtime)
    EditText editText_count;

    @BindView(R.id.send_sms)
    Button send_sms;

    private int tel_book = 0;//0-9 电话本一~十
    private int eachHourCount = 0;

    private boolean isSending =false;//正在发送标志位

    List<PhoneNumber> phoneBook = new ArrayList<>();
    List<PhoneNumber> hasSendPhoneNum = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ButterKnife.bind(this);

        SqlScoutServer.create(this, getPackageName());
        Connector.getDatabase();

        spinner_tel_book.setOnItemSelectedListener(this);
        send_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isSending){
                    isSending =true;
                    eachHourCount = Integer.parseInt(editText_count.getText().toString());
                    phoneBook = DataSupport.where("owntable = ?",tel_book+"").find(PhoneNumber.class);
                    sendSMSList(phoneBook,eachHourCount);
                }else{
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("提示")
                            .setMessage("正在发送短信，请稍后……")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .show();
                }

            }
        });
    }



    private void sendSMSList(final List<PhoneNumber> phoneBook,int eachHourCount){
        final Timer timer = new Timer();
        final int phoneNum =phoneBook.size();
        final int x = eachHourCount/10;
        //final int y = eachHourCount%10;
        Utils.showToast(MainActivity.this,tel_book+"-"+eachHourCount+"-每6分钟发送"+x+"条");

        TimerTask mTimerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //处理延时任务
                        Utils.showToast(MainActivity.this,"每六分钟执行一次！");
                        final List<PhoneNumber> temp = new ArrayList<>();
                        int[] index = new int[x];
                        int count =0;
                        for(int i=0;i<phoneBook.size();i++){
                            PhoneNumber phoneNumber = phoneBook.get(i);
                            if(count<x) {
                                if (!phoneNumber.isSendSMS()) {
                                    temp.add(phoneNumber);
                                    index[count] = i;
                                    count++;
                                }
                            }else
                                break;
                        }
                        if(count==0){
                            isSending = false;
                            Utils.showToast(MainActivity.this,"数据库无可用数据！");
                            // 清空数据库
                            DataSupport.deleteAll(PhoneNumber.class,"owntable = ?",tel_book+"");
                            timer.cancel();
                        }

                        int actualX =x;
                        if(temp.size()<x){//数据库里只有不到x条数据能发了
                            actualX =temp.size();
                            Utils.showToast(MainActivity.this,"数据库里只有"+actualX+"条数据能发了");
                        }

                        int[] delayTime = new int[actualX];
                        for(int i=0;i<actualX;i++){
                            delayTime[i] = Utils.generateRandomTime(10,345);
                        }

                        clearHasSendTel();

                        for(int i=0;i<actualX;i++) {
                            Timer timerX = new Timer();
                            final int tem = index[i];
                            final String phone = temp.get(i).getNumber();
                            final String text = "你好啊！！"+i;
                            TimerTask mTimerTaskX = new TimerTask() {
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            sendSMS(MainActivity.this,phone,text,tem);
                                            Utils.showToast(MainActivity.this,"执行！");
                                        }
                                    });
                                }
                            };

                            timerX.schedule(mTimerTaskX, delayTime[i] * 1000);
                        }
                    }
                });
            }
        };
        timer.schedule(mTimerTask, 0, 6*60*1000);

        //sendSMS(MainActivity.this,"","");
    }

    private void clearHasSendTel(){
        Timer timerX = new Timer();
        TimerTask mTimerTaskX = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        phoneBook.removeAll(hasSendPhoneNum);
                        for(int i=0;i<hasSendPhoneNum.size();i++){
                            DataSupport.delete(PhoneNumber.class,hasSendPhoneNum.get(i).getId());
                        }
                        Utils.showToast(MainActivity.this,"清已发送数据"+hasSendPhoneNum.size()+"个");
                        hasSendPhoneNum.clear();
                    }
                });
            }
        };

        timerX.schedule(mTimerTaskX, 355 * 1000);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spin_one:
                tel_book = position;
                //Utils.showToast(MainActivity.this,parent.getItemAtPosition(position).toString());
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_tel_list) {
            Intent intent =new Intent(MainActivity.this,PhoneBooksActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_send_sms) {
           // Intent intent =new Intent(MainActivity.this,SendSMSActivity.class);
           // startActivity(intent);
        } else if (id == R.id.nav_edit_sms) {
            PhoneNumber phoneNumber = new PhoneNumber(0,"hewenjie","18012345678");
            phoneNumber.save();
            PhoneNumber phoneNumber2 = new PhoneNumber(1,"zhangsan","12332112332");
            phoneNumber2.save();
        }/*else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
            sendSMS(this,"10086","1234567890收到请回复！！！！！！");
        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * 调用短信接口发短信，含接收报告和发送报告
     *
     * @param phoneNumber
     * @param message
     */
    public void sendSMS(Context context,String phoneNumber, String message,int index) {
        //处理返回的发送状态
        String SENT_SMS_ACTION = "SENT_SMS_ACTION";
        Intent sentIntent = new Intent(SENT_SMS_ACTION);
        sentIntent.addCategory(index+"");
        PendingIntent sendIntent= PendingIntent.getBroadcast(this, 0, sentIntent,
                0);
        IntentFilter intentFilter1 = new IntentFilter(SENT_SMS_ACTION);
        intentFilter1.addCategory(index+"");
        // register the Broadcast Receivers
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context _context, Intent _intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(MainActivity.this,
                                "短信发送成功"+_intent.getCategories().iterator().next(), Toast.LENGTH_SHORT)
                                .show();
                        int index = Integer.parseInt(_intent.getCategories().iterator().next());
                        phoneBook.get(index).setSendSMS(true);
                        hasSendPhoneNum.add(phoneBook.get(index));
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(MainActivity.this,
                                "短信发送失败1-"+_intent.getCategories().iterator().next(), Toast.LENGTH_SHORT)
                                .show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        // TODO: 2017/6/25 暂时认为发送成功
                        int indexs = Integer.parseInt(_intent.getCategories().iterator().next());
                        phoneBook.get(indexs).setSendSMS(true);
                        hasSendPhoneNum.add(phoneBook.get(indexs));

                        Toast.makeText(MainActivity.this,
                                "短信发送失败2-"+_intent.getCategories().iterator().next(), Toast.LENGTH_SHORT)
                                .show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(MainActivity.this,
                                "短信发送失败3-"+_intent.getCategories().iterator().next(), Toast.LENGTH_SHORT)
                                .show();
                        break;
                    default:
                        Toast.makeText(MainActivity.this,
                                "短信发送失败4-"+_intent.getCategories().iterator().next(), Toast.LENGTH_SHORT)
                                .show();
                        break;
                }
            }
        }, intentFilter1);

        //处理返回的接收状态
        String DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION";
        // create the deilverIntent parameter
        Intent deliverIntent = new Intent(DELIVERED_SMS_ACTION);
        PendingIntent backIntent= PendingIntent.getBroadcast(this, 0,
                deliverIntent, 0);
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context _context, Intent _intent) {
                Toast.makeText(MainActivity.this,
                        "收信人已经成功接收", Toast.LENGTH_SHORT)
                        .show();
            }
        }, new IntentFilter(DELIVERED_SMS_ACTION));

        // 获取短信管理器
        android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
        // 拆分短信内容（手机短信长度限制）
        List<String> divideContents = smsManager.divideMessage(message);
        for (String text : divideContents) {
            smsManager.sendTextMessage(phoneNumber, null, text, sendIntent, backIntent);
        }
    }
}
