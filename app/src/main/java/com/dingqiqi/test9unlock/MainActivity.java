package com.dingqiqi.test9unlock;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnEqualPwd:
                Intent intent = new Intent(this, PwdActivity.class);
                intent.putExtra("mode", "equal");
                startActivity(intent);
                break;
            case R.id.btnSetPwd:
                intent = new Intent(this, PwdActivity.class);
                intent.putExtra("mode", "set");
                startActivity(intent);
                break;
        }
    }

}
