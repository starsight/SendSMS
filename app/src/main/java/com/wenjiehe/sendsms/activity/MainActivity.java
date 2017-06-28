package com.wenjiehe.sendsms.activity;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.idescout.sql.SqlScoutServer;
import com.wenjiehe.sendsms.R;
import com.wenjiehe.sendsms.Utils;
import com.wenjiehe.sendsms.entity.PhoneNumber;
import com.wenjiehe.sendsms.entity.SMSText;
import com.wenjiehe.sendsms.entity.VertifyCode;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;


import static android.media.CamcorderProfile.get;
import static com.wenjiehe.sendsms.R.id.sendtime;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener {

    @BindView(R.id.spin_one)
    Spinner spinner_tel_book;

    @BindView(sendtime)
    EditText editText_count;

    @BindView(R.id.send_sms)
    Button send_sms;

    @BindView(R.id.activity_main_message)
    TextView message;

    @BindView(R.id.spinner_one_tip)
    TextView spinner_tel_book_tip;

    ImageView header;
    TextView hasVertified;

    private int tel_book = 0;//0-9 电话本一~十
    private int eachHourCount = 0;

    private boolean isSending = false;//正在发送标志位

    List<PhoneNumber> phoneBook = new ArrayList<>();
    List<PhoneNumber> hasSendPhoneNum = new ArrayList<>();
    List<SMSText> smsText = new ArrayList<>();
    List<Integer> sendingList= new ArrayList<>();

    private BroadcastReceiver broadcastReceiver1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context _context, Intent _intent) {
            int index =0;
            if(!sendingList.isEmpty()){
                index = sendingList.get(0);
                sendingList.remove(0);
            }else
                return;

            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    sendSMSMessage("短信发送成功-" + index);

                    phoneBook.get(index).setSendSMS(true);

                    phoneBook.get(index).setOwnTable(12);//13号表为已发送的短信列表
                    phoneBook.get(index).save();

                    hasSendPhoneNum.add(phoneBook.get(index));
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    Utils.showToast(MainActivity.this, "短信发送失败1-" + index);
                    sendSMSMessage("短信发送失败1-" + index);
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    // TODO: 2017/6/25 暂时认为发送成功

                    /*phoneBook.get(index).setSendSMS(true);
                    phoneBook.get(index).setOwnTable(12);//13号表为已发送的短信列表
                    phoneBook.get(index).save();
                    hasSendPhoneNum.add(phoneBook.get(index));*/
                    Utils.showToast(MainActivity.this, "短信发送失败2-" + index);
                    //int indexs = Integer.parseInt(index);
                    //phoneBook.get(index).setSendSMS(true);
                    //hasSendPhoneNum.add(phoneBook.get(index));
                    sendSMSMessage("短信发送失败2-" + index);
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    Utils.showToast(MainActivity.this, "短信发送失败3-" + index);
                    sendSMSMessage("短信发送失败3-" + index);
                    break;
                default:
                    sendSMSMessage("短信发送失败4-" + index);
                    Utils.showToast(MainActivity.this, "短信发送失败4-" + index);
                    break;
            }
        }
    };
    private BroadcastReceiver  broadcastReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context _context, Intent _intent) {
            sendSMSMessage("收信人已经成功接收");
        }
    };

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

        View headerView = navigationView.getHeaderView(0);
        header = (ImageView) headerView.findViewById(R.id.header);
        hasVertified = (TextView) headerView.findViewById(R.id.hasVertified);

        SqlScoutServer.create(this, getPackageName());
        Connector.getDatabase();

        spinner_tel_book.setOnItemSelectedListener(this);
        send_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSending) {
                    isSending = true;
                    if ("".equals(editText_count.getText().toString()))
                        eachHourCount = 150;
                    else
                        eachHourCount = Integer.parseInt(editText_count.getText().toString());
                    eachHourCount = eachHourCount < 10 ? 10 : eachHourCount;
                    eachHourCount = eachHourCount > 300 ? 300 : eachHourCount;

                    phoneBook = DataSupport.where("owntable = ?", tel_book + "").find(PhoneNumber.class);
                    smsText = DataSupport.findAll(SMSText.class);
                    if(smsText.isEmpty()){
                        isSending =  false;
                        Utils.showAlertDialog(MainActivity.this,"警告","请先编辑要发送的短信内容！");
                        return ;
                    }

                    sendSMSList(phoneBook, eachHourCount);
                } else {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("提示")
                            .setMessage("正在发送短信，请稍后……")
                            .setPositiveButton("好",null)
                            .show();
                }

            }
        });

        final List<VertifyCode> list = DataSupport.findAll(VertifyCode.class);
        if(list.isEmpty()){
            String str = Utils.generatePass();
            VertifyCode vertifyCode = new VertifyCode(str);
            vertifyCode.save();
            list.add(vertifyCode);
        }else{
            //int date = (int)(1498972300-list.get(0).getTime())/86400;
            int date = (int)(new Date().getTime()/1000-list.get(0).getTime())/86400;
            if(!Utils.correctPassward.equals(list.get(0).getPassword())){
                if(date>1){
                    Utils.showToast(MainActivity.this,"已过期！");
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            finish();
                        }
                    }, 2000);
                }else
                    Utils.showToast(MainActivity.this,"未注册，请尽快注册！");
            }else{
                hasVertified.setText("短信宝(已注册)");
            }
        }

        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);

                if(Utils.correctPassward.equals(list.get(0).getPassword())){
                    Utils.showToast(MainActivity.this,"已注册！");
                }else{
                    LayoutInflater mLayoutInflater = LayoutInflater.from(MainActivity.this);
                    View view = mLayoutInflater.inflate(R.layout.vertify_password, null);

                    final TextView tv_dialog = (TextView) view.findViewById(R.id.tv_vertify_password);
                    final EditText et_dialog = (EditText) view.findViewById(R.id.et_vertify_password);

                    tv_dialog.setText(list.get(0).getCode());

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                            .setTitle("请输入序列号密码")
                            .setView(view)
                            .setCancelable(false)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    String s = et_dialog.getText().toString();
                                    if(Utils.correctPassward.equals(s)){
                                        hasVertified.setText("短信宝(已注册)");
                                        list.get(0).setPassword(Utils.correctPassward);
                                        list.get(0).save();
                                        Utils.showToast(MainActivity.this,"恭喜！注册成功！");
                                    }else{
                                        Utils.showToast(MainActivity.this,"序列号密码不正确！");
                                    }
                                }
                            })
                            .setNegativeButton("取消", null);
                    builder.show();
                }
            }
        });

        /*boolean canSMS = PermissionUtils.hasSelfPermissions(MainActivity.this, permissonSMS1, permissonContacts1, permissonStorage, permissonSMS2, permissonContacts2);
        if (!canSMS) {
            Utils.authorityManagement(MainActivity.this, "应用需要相关权限，点击确定跳转至应用详情授予权限。");
        }*/

        registerReceiver();
    }

    private void sendSMSMessage(String msg) {

        if (message != null) {
            if (message.getLineCount() >= 20) {
                message.setText(msg + "\n");
            } else
                message.append(msg + "\n");
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        //InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //imm.hideSoftInputFromWindow(editText_count.getWindowToken(), 0);

        /*boolean canSMS = PermissionUtils.hasSelfPermissions(MainActivity.this, permissonSMS1, permissonContacts1, permissonStorage, permissonSMS2, permissonContacts2);
        if (!canSMS) {
            Utils.authorityManagement(MainActivity.this, "应用需要相关权限，点击确定跳转至应用详情授予权限。");
        }*/
    }

    private void sendSMSList(final List<PhoneNumber> phoneBook, int eachHourCount) {

        final Timer timer = new Timer();
        final int phoneNum = phoneBook.size();
        final int x = eachHourCount / 10;
        //final int y = eachHourCount%10;
        sendSMSMessage(tel_book + "-" + eachHourCount + "-每6分钟发送" + x + "条");
        //Utils.showToast(MainActivity.this,tel_book+"-"+eachHourCount+"-每6分钟发送"+x+"条");

        TimerTask mTimerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //处理延时任务
                        //Utils.showToast(MainActivity.this,"每六分钟执行一次！");
                        sendSMSMessage("每六分钟执行一次！");
                        if (!hasSendPhoneNum.isEmpty()) {
                            phoneBook.removeAll(hasSendPhoneNum);
                            /*for (int i = 0; i < hasSendPhoneNum.size(); i++) {
                                hasSendPhoneNum.get(i).setOwnTable(12);//13号表为已发送的短信列表
                                hasSendPhoneNum.get(i).save();
                            }*/
                            sendSMSMessage("清除已发送数据" + hasSendPhoneNum.size() + "个");
                            Utils.showToast(MainActivity.this, "清除已发送数据" + hasSendPhoneNum.size() + "个");
                            hasSendPhoneNum.clear();
                        }

                        final List<PhoneNumber> temp = new ArrayList<>();
                        int[] index = new int[x];
                        int count = 0;
                        for (int i = 0; i < phoneBook.size(); i++) {
                            PhoneNumber phoneNumber = phoneBook.get(i);
                            if (count < x) {
                                if (!phoneNumber.isSendSMS()) {
                                    temp.add(phoneNumber);
                                    index[count] = i;
                                    count++;
                                }
                            } else
                                break;
                        }
                        if (count == 0) {
                            isSending = false;
                            sendSMSMessage("数据库无可用数据！");
                            Utils.showToast(MainActivity.this, "数据库无可用数据！");
                            // 清空数据库
                            DataSupport.deleteAll(PhoneNumber.class, "owntable = ?", tel_book + "");
                            timer.cancel();
                            return;
                        }

                        int actualX = x;
                        if (temp.size() < x) {//数据库里只有不到x条数据能发了
                            actualX = temp.size();
                            sendSMSMessage("数据库里只有" + actualX + "条数据能发了");
                            //Utils.showToast(MainActivity.this,"数据库里只有"+actualX+"条数据能发了");
                        }

                        int[] delayTime = new int[actualX];
                        for (int i = 0; i < actualX; i++) {
                            delayTime[i] = Utils.generateRandomTime(2, 348);
                        }

                        //clearHasSendTel();

                        for (int i = 0; i < actualX; i++) {
                            Timer timerX = new Timer();
                            final int tem = index[i];
                            final String phone = temp.get(i).getNumber();
                            final String text = smsText.get(Utils.generateRandomTime(0,smsText.size())).getText();
                            TimerTask mTimerTaskX = new TimerTask() {
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            sendSMS(MainActivity.this, phone, text, tem);
                                            sendSMSMessage("发送短信！");
                                            sendSMSMessage(text);
                                            //Utils.showToast(MainActivity.this,"发送短信！");
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
        timer.schedule(mTimerTask, 0, 6 * 60 * 1000);

        //sendSMS(MainActivity.this,"","");
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spin_one:
                tel_book = position;
                int count = DataSupport.where("owntable=?",position+"").count(PhoneNumber.class);
                spinner_tel_book_tip.setText("共"+count+"条电话");
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


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_tel_list) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    Intent intent = new Intent(MainActivity.this, PhoneBooksActivity.class);
                    startActivity(intent);
                }
            }, 400);

        } else if (id == R.id.nav_send_sms) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    spinner_tel_book_tip.setText("选择电话本");
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    spinner_tel_book.setFocusable(true);
                    spinner_tel_book.setFocusableInTouchMode(true);
                    spinner_tel_book.requestFocus();
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }, 400);
        } else if (id == R.id.nav_edit_sms) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    Intent intent = new Intent(MainActivity.this, EditSMSTextActivity.class);
                    startActivity(intent);
                }
            }, 400);
        } else if (id == R.id.nav_import) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    Intent intent = new Intent(MainActivity.this, ImportActivity.class);
                    startActivity(intent);
                }
            }, 400);
        } else if (id == R.id.nav_has_send) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    Intent intent = new Intent(MainActivity.this, hasSentActivity.class);
                    startActivity(intent);
                }
            }, 400);
        } else if (id == R.id.nav_exit) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    System.exit(0);
                }
            }, 400);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /***
     * 权限处理
     */
//    private final int requestPermission = 0;
//    private final int requestPermissionSMS = 1;
//    private final int requestPermissionContacts = 2;
//    private final int requestPermissionStorage =3;
    String permissonSMS1 = Manifest.permission.SEND_SMS;
    String permissonSMS2 = Manifest.permission.READ_SMS;
    String permissonContacts1 = Manifest.permission.READ_CONTACTS;
    String permissonContacts2 = Manifest.permission.WRITE_CONTACTS;
    String permissonStorage = Manifest.permission.READ_EXTERNAL_STORAGE;
    //String permissonStorage2 = Manifest.permission.WRITE_EXTERNAL_STORAGE;


    String SENT_SMS_ACTION = "SENT_SMS_ACTION";
    //处理返回的接收状态
    String DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION";
    /**
     * 调用短信接口发短信，含接收报告和发送报告
     *
     * @param phoneNumber
     * @param message
     */
    public void sendSMS(Context context, String phoneNumber, String message, int index) {
        //处理返回的发送状态

        Intent sentIntent = new Intent(SENT_SMS_ACTION);

        sendingList.add(index);//加入正在发送的队列

        final PendingIntent sendIntent = PendingIntent.getBroadcast(this, 0, sentIntent,
                0);
        // create the deilverIntent parameter
        Intent deliverIntent = new Intent(DELIVERED_SMS_ACTION);
        PendingIntent backIntent = PendingIntent.getBroadcast(this, 0,
                deliverIntent, 0);

        // 获取短信管理器
        android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
        // 拆分短信内容（手机短信长度限制）
        List<String> divideContents = smsManager.divideMessage(message);
        for (String text : divideContents) {
            smsManager.sendTextMessage(phoneNumber, null, text, sendIntent, backIntent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver1!=null)
            unregisterReceiver(broadcastReceiver1);
        if(broadcastReceiver2!=null)
            unregisterReceiver(broadcastReceiver2);
    }
    private long exitTime = 0;

    /**
     * 捕捉返回事件按钮
     * <p>
     * 因为此 Activity 继承 TabActivity 用 onKeyDown 无响应，所以改用 dispatchKeyEvent
     * 一般的 Activity 用 onKeyDown 就可以了
     */

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                this.exitApp();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * 退出程序
     */
    private void exitApp() {
        // 判断2次点击事件时间
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            System.exit(0);
        }
    }

    void registerReceiver(){
        registerReceiver(broadcastReceiver1, new IntentFilter(SENT_SMS_ACTION));
        registerReceiver(broadcastReceiver2, new IntentFilter(DELIVERED_SMS_ACTION));
    }
}
