package com.example.ballhit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    //声明需要的组件
    private Button login,exit,reg;
    private  EditText   username,password;
    private SharedPreferences share;//声明SharedPreferences

    private static final String FILENAME = "login_info";
    private static final String USER_PREFS = "user_data";

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
                } else {
                    //检查用户名和密码是否匹配
                    try {
                        FileInputStream fis = openFileInput(FILENAME);
                        BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
                        String line;
                        boolean found = false;
                        while ((line = reader.readLine()) != null) {
                            String[] data = line.split(",");
                            if (data.length == 2 && data[0].equals(name) && data[1].equals(pass)) {
                                SharedPreferences preferences = getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("username", name);
                                editor.apply();
                                Toast.makeText(MainActivity.this, "WELCOME TO BALL HIT！", Toast.LENGTH_LONG).show();
                                //成功登陆，进入LoginokActivity界面
                                Intent intent=new Intent(MainActivity.this,LoginokActivity.class);
                                intent.putExtra("username", name); // Pass the user name to the next activity
                                startActivity(intent);
                                finish();
                                found = true;
                                break;
                            }
                        }
                        fis.close();
                        if (!found) {
                            //错误的话
                            Toast.makeText(MainActivity.this, "User name or password is wrong, please confirm the information or register", Toast.LENGTH_LONG).show();
                            return;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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

    private class RegListenerimp implements View.OnClickListener {

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
            //检查是否已经注册过
            try {
                FileInputStream fis = openFileInput(FILENAME);
                BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(",");
                    if (data.length == 2 && data[0].equals(nam) && data[1].equals(pas)) {
                        Toast.makeText(MainActivity.this, "This username has already been registered!", Toast.LENGTH_LONG).show();
                        fis.close();
                        return;
                    }
                }
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //进入注册的Dialog
            Dialog dialog=new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Register")
                    .setMessage("Do you confirm the registration information?")
                    .setPositiveButton("Confirm", new  DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 保存输入的信息到文件
                            saveLoginInfoToFile(nam, pas);
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

        private void saveLoginInfoToFile(String username, String password) {
            try {
                FileOutputStream fos = openFileOutput(FILENAME, MODE_APPEND);
                String data = username + "," + password + "\n";
                fos.write(data.getBytes());
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
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