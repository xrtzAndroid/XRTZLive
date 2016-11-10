package com.xrtz.xrlive.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.xrtz.xrlive.R;
import com.xrtz.xrlive.application.LiveApplication;
import com.xrtz.xrlive.common.CommonValues;
import com.xrtz.xrlive.iservice.UserService;
import com.xrtz.xrlive.response.UserResponse;
import com.xrtz.xrlive.util.NetStatueUtil;
import com.xrtz.xrlive.util.SharePreference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 登陆Activity
 */
public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.login_user)
    EditText mLoginUserEt;
    @BindView(R.id.login_passwsd)
    EditText mLoginPwdEt;
    @BindView(R.id.remember_checkbox)
    CheckBox remember_checkbox;
    String userName;
    String password;
    /*是否记住密码,ssss*/
    private boolean rememberuser;
    private final String REMMEMBERUSER="rememberuser";
    private final String USERNAME="userName";
    private final String PASSWORD="password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initdata();
        remember_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rememberuser=isChecked;
            }
        });
    }
    /*获取缓存的账号密码数据，尝试直接登录*/
    private void initdata() {
        rememberuser=SharePreference.getBoolean(this,REMMEMBERUSER);
        remember_checkbox.setChecked(rememberuser);
        if(rememberuser){
            mLoginUserEt.setText(SharePreference.getString(this,USERNAME));
            mLoginPwdEt.setText(SharePreference.getString(this,PASSWORD));
            login();
        }
    }

    @OnClick({R.id.login_button, R.id.login_reg})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.login_button:
                //startActivity(new Intent(this,MainActivity.class));
                login();
                break;
            case R.id.login_reg:
                LiveApplication.rememberuser=rememberuser;
                startActivity(new Intent(this,RegActivity.class));
                this.finish();
                break;
        }
    }

    private void login() {
        userName = mLoginUserEt.getText().toString().trim();
        password = mLoginPwdEt.getText().toString().trim();
        if(checkLoginInfo(userName,password)){
            loginSubmit(userName,password);
        }
    }

    /**
     * 检查用户名密码的合法性
     * @param userName
     * @param password
     * @return
     */
    public boolean checkLoginInfo(String userName,String password){
        if(TextUtils.isEmpty(userName)){
            Toast.makeText(this,"请输入登陆账号",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"请输入登陆密码",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * 登陆提交
     * @param userName
     * @param password
     */
    public void loginSubmit(final String userName,final String password){
        if(NetStatueUtil.isConnected(getApplicationContext()) && !TextUtils.isEmpty(userName) && !TextUtils.isEmpty(password)){//网络连接ok

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(CommonValues.BaseUrl) //这个就是常量类里的基础路径 ;
                    .addConverterFactory(GsonConverterFactory.create())  //有这个Convert会自动将json转换成实体bean
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create()) //有这个配置，会自动将结果转换成一个支持Rxjava的Observable对象
                    .build();

            UserService apiService = retrofit.create(UserService.class);
            apiService.login(userName,password)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<UserResponse>() {
                        @Override
                        public void onCompleted() {
                            Log.e("onCompleted","onCompleted----");
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("onError",e.getMessage());
                        }

                        @Override
                        public void onNext(UserResponse userResponse) {
                            if (userResponse!=null) {
                                Log.e("onNext", "userResponse:" + userResponse);
                                if(userResponse.getStatus()==1){//成功
                                    LiveApplication.newInstance().setUser(userResponse.getData());
                                    /*如果选择记住密码，缓存账号密码*/
                                    SharePreference.putBoolean(LoginActivity.this,REMMEMBERUSER,rememberuser);
                                    if(rememberuser){
                                        SharePreference.putString(LoginActivity.this,USERNAME,userName);
                                        SharePreference.putString(LoginActivity.this,PASSWORD,password);
                                    }
                                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                    LoginActivity.this.finish();
                                }else{
                                    Toast.makeText(LoginActivity.this,userResponse.getMsg(),Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });

        }else{
            Toast.makeText(this,"",Toast.LENGTH_SHORT).show();
        }
    }
}

