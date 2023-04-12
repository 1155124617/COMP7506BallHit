package com.example.ballhit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    //声明需要的组件
    private Button login,exit,reg;
    private  EditText   username,password;
    private SharedPreferences share;//声明SharedPreferences

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        intiview();//初始化视图，寻找id
        saveuser();//先保存一个数据admin 123456
        exit.setOnClickListener(new Listenerimp());//退出的监听事件
        reg.setOnClickListener(new RegListenerimp());//注册的监听事件
        //登陆的事件监听处理内部类
        login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //获取输入的信息
                String name=username.getText().toString();
                String pass=password.getText().toString();
                //判断输入信息是否为空
                if(name.trim().equals("") || pass.trim().equals("")) {
                    Toast.makeText(MainActivity.this, "Username and password cannot be empty", Toast.LENGTH_LONG).show();
                }
                //获取保存文件中的用户名和密码
                String savedUsername = share.getString("username","");
                String savedPassword = share.getString("password","");
                //查看输入的密码和名字是否一致
                if(name.trim().equals(savedUsername) && pass.trim().equals(savedPassword)) {
                    Toast.makeText(MainActivity.this, "WELCOME TO BALL HIT！", Toast.LENGTH_LONG).show();

                    //成功登陆，进入LoginokActivity界面
                    Intent intent=new Intent(MainActivity.this,LoginokActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    //错误的话
                    Toast.makeText(MainActivity.this, "User name or password is wrong, please confirm the information or register", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });

    }


    private class Listenerimp implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            finish();//结束一个Activity
        }
    }
    private class  RegListenerimp implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            //定义两个字符串常量，并获取信息
            final String nam=username.getText().toString();
            final String pas=password.getText().toString();
            //判读信息是否空
            if(nam.trim().equals("") || pas.trim().equals("")) {
                Toast.makeText(MainActivity.this, "WARNING：When registering, neither the username nor the password can be empty！", Toast.LENGTH_LONG).show();
                return;//为空就会返回
            }
            //进入注册的Dialog
            Dialog dialog=new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Register")
                    .setMessage("Do you confirm the registration information?")
                    .setPositiveButton("Confirm", new  DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            //保存输入的信息 	 Editor 别忘了		edit.commit();提交
                            share=getSharedPreferences("info",Activity.MODE_PRIVATE);
                            SharedPreferences.Editor edit=share.edit();
                            edit.putString("username", nam);
                            edit.putString("password", pas);
                            edit.commit();
                            //提示成功注册
                            Toast.makeText(MainActivity.this, "Congratulations, registration is successful!", Toast.LENGTH_LONG).show();

                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                        }
                    }).create();//创建一个dialog
            dialog.show();//显示对话框，否者不成功
        }

    }


    //实现写一个admin 123456的用户
    private void saveuser() {
        // TODO Auto-generated method stub
        share=getSharedPreferences("info", Activity.MODE_PRIVATE);
        SharedPreferences.Editor edit=share.edit();
        edit.putString("username", "admin");
        edit.putString("password", "123456");
        edit.commit();
    }


    private void intiview() {
        // TODO Auto-generated method stub
        login=(Button)findViewById(R.id.login);
        exit=(Button)findViewById(R.id.exit);
        reg=(Button)findViewById(R.id.reg);
        username=(EditText)findViewById(R.id.username);
        password=(EditText)findViewById(R.id.password);
    }

}