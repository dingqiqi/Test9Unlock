package com.dingqiqi.test9unlock;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/8/29.
 */
public class PwdActivity extends AppCompatActivity {

    private SharedPreferences mPreferences;

    private LockView mLockView;

    private boolean isSetPwd;

    private String mPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwd);

        mLockView = (LockView) findViewById(R.id.lockView);
        mPreferences = getSharedPreferences("pwd", Context.MODE_PRIVATE);

        String mode = getIntent().getStringExtra("mode");

        mLockView.setGravity(LockView.Gravity.CENTER);

        if ("equal".equals(mode)) {
            isSetPwd = false;
            mLockView.setMode(LockView.Style.EQUALPWD);
        } else {
            isSetPwd = true;
            mLockView.setMode(LockView.Style.SETPWD);
        }

        mLockView.setListener(new LockView.PswListener() {
            @Override
            public void returnPwd(String pwd) {
                if (isSetPwd) {
                    if (pwd.length() < 4) {
                        mLockView.initPwdView();
                        Toast.makeText(PwdActivity.this, "密码最小长度四位，请重新设置密码", Toast.LENGTH_SHORT).show();
                    } else {
                        if (mPwd == null || "".equals(mPwd)) {
                            mPwd = pwd;
                            mLockView.initPwdView();
                            Toast.makeText(PwdActivity.this, "请再次设置密码", Toast.LENGTH_SHORT).show();
                        } else {
                            if (pwd.equals(mPwd)) {
                                mPreferences.edit().putString("pwd", mPwd).commit();
                                Toast.makeText(PwdActivity.this, "密码设置成功", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                mPwd = "";
                                mLockView.initPwdView();
                                Toast.makeText(PwdActivity.this, "两次密码不一致，请重新设置密码", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } else {
                    if (pwd.equals(mPreferences.getString("pwd", ""))) {
                        Toast.makeText(PwdActivity.this, "密码一致", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(PwdActivity.this, "密码不一致，请重新输入", Toast.LENGTH_SHORT).show();
                        mLockView.initPwdView();
                    }
                }

            }
        });

    }


}
