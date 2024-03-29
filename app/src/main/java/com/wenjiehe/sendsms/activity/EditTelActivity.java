package com.wenjiehe.sendsms.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.wenjiehe.sendsms.R;
import com.wenjiehe.sendsms.Utils;
import com.wenjiehe.sendsms.entity.PhoneNumber;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.R.id.list;

public class EditTelActivity extends AppCompatActivity {

    private int phone_book_num = 0;

    private int editNum = 10;
    //private List<PhoneNumber> newAddTel = new ArrayList<>();//新添加的电话号码
    //private PhoneNumber[] newTel = new PhoneNumber[editNum];
    private List<PhoneNumber> newTelList = new ArrayList<>();

    private boolean hasSave = true;

    private  int hasTelNumber=0;
    //private List<Integer> notTel = new ArrayList<>();

    @BindView(R.id.activity_edit_tel_et0)
    EditText editText0;
    @BindView(R.id.activity_edit_tel_et1)
    EditText editText1;
    @BindView(R.id.activity_edit_tel_et2)
    EditText editText2;
    @BindView(R.id.activity_edit_tel_et3)
    EditText editText3;
    @BindView(R.id.activity_edit_tel_et4)
    EditText editText4;
    @BindView(R.id.activity_edit_tel_et5)
    EditText editText5;
    @BindView(R.id.activity_edit_tel_et6)
    EditText editText6;
    @BindView(R.id.activity_edit_tel_et7)
    EditText editText7;
    @BindView(R.id.activity_edit_tel_et8)
    EditText editText8;
    @BindView(R.id.activity_edit_tel_et9)
    EditText editText9;

    private List<EditText> editTextList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tel);

        ButterKnife.bind(this);
        addTextListener();
        editTextList.add(editText0);
        editTextList.add(editText1);
        editTextList.add(editText2);
        editTextList.add(editText3);
        editTextList.add(editText4);
        editTextList.add(editText5);
        editTextList.add(editText6);
        editTextList.add(editText7);
        editTextList.add(editText8);
        editTextList.add(editText9);


        Intent intent = getIntent();
        phone_book_num = intent.getIntExtra("phonebook", 0);
        hasTelNumber = intent.getIntExtra("hasTelNumber", 0);
        if(hasTelNumber>=290){
            Utils.showToast(EditTelActivity.this,"数据库还能存"+(300-hasTelNumber)+"条数据！");
        }
    }

    private void addTextListener() {
        editText0.addTextChangedListener(new MyTextWatcher(editText0, 0));
        editText1.addTextChangedListener(new MyTextWatcher(editText1, 1));
        editText2.addTextChangedListener(new MyTextWatcher(editText2, 2));
        editText3.addTextChangedListener(new MyTextWatcher(editText3, 3));
        editText4.addTextChangedListener(new MyTextWatcher(editText4, 4));
        editText5.addTextChangedListener(new MyTextWatcher(editText5, 5));
        editText6.addTextChangedListener(new MyTextWatcher(editText6, 6));
        editText7.addTextChangedListener(new MyTextWatcher(editText7, 7));
        editText8.addTextChangedListener(new MyTextWatcher(editText8, 8));
        editText9.addTextChangedListener(new MyTextWatcher(editText9, 9));
    }

    private String removeSpace(String str) {
        if (str.length() == 13) {
            return str.replace(" ", "");
        } else
            return "";
    }

    private void changeFocus(int position) {
        hasSave= false;
        //Utils.showToast(EditTelActivity.this,position+"");
        if (position != (editNum - 1))//最后一项
            editTextList.get(position + 1).requestFocus();
        else
            editText9.clearFocus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_edit_tel_save, menu);
        return true;
    }

    public void getPhoneNumberList() {
        newTelList.clear();
        for (int position = 0; position < editNum && position < editTextList.size(); position++) {
            EditText editText = editTextList.get(position);
            String str = removeSpace(editText.getText().toString());
            if("".equals(str)){
                continue;
            }else if(str.length()!=11){
                Utils.showToast(EditTelActivity.this, "新增" +(position + 1) + ":不是一个正确的电话号码，不保存！");
                continue;
            }

            if (Utils.checkPhoneNumber(str)) {
                newTelList.add(new PhoneNumber(phone_book_num, str));
            } else {
                Utils.showToast(EditTelActivity.this, "新增" + (position + 1) + ":不是一个正确的电话号码，不保存！");
                continue;
            }
            editText.setText("");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.activity_eit_tel_save:
                if(hasTelNumber>=300){
                    Utils.showToast(EditTelActivity.this,"数据库存不下数据了！");
                    break;
                }else if(hasTelNumber>=290){
                    if(newTelList.size()+hasTelNumber>300){
                        Utils.showToast(EditTelActivity.this,"数据库只能存"+(300-hasTelNumber)+"条数据，请删除列表中多余的数据");
                        break;
                    }
                }

                if (!hasSave) {
                    getPhoneNumberList();
                    if (!newTelList.isEmpty()) {
                        hasTelNumber +=newTelList.size();//更新现有的数据记录

                        DataSupport.saveAll(newTelList);
                        newTelList.clear();

                        editText0.requestFocus();
                        editText0.clearFocus();

                        hasSave = true;

                        Utils.showToast(EditTelActivity.this, "保存成功！");

                        if(hasTelNumber>=290){
                            Utils.showToast(EditTelActivity.this,"数据库还能存"+(300-hasTelNumber)+"条数据！");
                        }
                    }else{
                        Utils.showToast(EditTelActivity.this, "请输入号码！");
                    }
                } else {
                    Utils.showToast(EditTelActivity.this, "已全部保存！");
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class MyTextWatcher implements TextWatcher {

        private EditText editText;
        private int position;

        MyTextWatcher(EditText editText, int position) {
            this.editText = editText;
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
            String str = text.toString().replace(" ", "");
            if (str.length() > 0) {
                String endStr = "";
                int len = str.length();
                for (int i = 0; i < len; i++) {
                    endStr += str.charAt(i);
                    if ((i + 2) % 4 == 0 && (i + 1) != len) {
                        endStr += " ";
                    }
                }
                if (lengthAfter != 0) {
                    editText.removeTextChangedListener(this);
                    editText.setText(endStr);
                    editText.addTextChangedListener(this);
                }
            }

            if (lengthAfter == 0) {
                editText.setSelection(start + lengthAfter);
            } else if (lengthAfter == 1) {
                if ((start - 3) % 5 == 0) {
                    start++;
                }
                editText.setSelection(start + lengthAfter);
            } else if (lengthAfter > 1) {
                editText.setSelection(editText.getText().toString().length());
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() == 13) {
                changeFocus(position);
            } else if (s.length() > 13 && (s.length() % 4 == 0)) {
                Utils.showToast(EditTelActivity.this, "不是一个正确的电话号码!");
                //if(!notTel.contains(position))
                //    notTel.add(position);
            }

        }
    }
}
