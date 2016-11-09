package com.xrtz.xrlive.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.xrtz.xrlive.R;
import com.xrtz.xrlive.application.LiveApplication;
import com.xrtz.xrlive.bean.User;
import com.xrtz.xrlive.common.CommonValues;
import com.xrtz.xrlive.iservice.UserService;
import com.xrtz.xrlive.response.StrResponse;
import com.xrtz.xrlive.response.UserResponse;
import com.xrtz.xrlive.util.RegularUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class RegActivity extends AppCompatActivity {

    @BindView(R.id.userName)
    EditText userNameEt;

    @BindView(R.id.userPassword)
    EditText userPasswordEt;

    @BindView(R.id.userRePassword)
    EditText userRePasswordEt;

    @BindView(R.id.userEmail)
    EditText userEmailEt;

    @BindView(R.id.userNickName)
    EditText userNickNameEt;

    String userName;
    String userPassword;
    String userRePassword;
    String userEmail;
    String userNickName;

    UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        ButterKnife.bind(this);
        
    }
    @OnClick({R.id.goLogin,R.id.userReg})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.goLogin:
                startActivity(new Intent(this,LoginActivity.class));
                finish();
                break;
            case R.id.userReg:
                userName = userNameEt.getText().toString().trim();
                userPassword = userPasswordEt.getText().toString().trim();
                userRePassword = userRePasswordEt.getText().toString().trim();
                userEmail = userEmailEt.getText().toString().trim();
                userNickName = userNickNameEt.getText().toString().trim();
                if(TextUtils.isEmpty(userName) || !RegularUtil.isUserName(userName)){
                    Toast.makeText(this, "用户名必须为4—16位的英文字母或数字", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(userPassword) ||  !RegularUtil.isPassword(userPassword)){
                    Toast.makeText(this, "密码必须为6—16位", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!userRePassword.equals(userPassword) ){
                    Toast.makeText(this, "确认密码需要和设置密码相同", Toast.LENGTH_SHORT).show();
                }
                if(TextUtils.isEmpty(userEmail) || !RegularUtil.isEmail(userEmail) ){
                    Toast.makeText(this, "邮箱格式不正确！", Toast.LENGTH_SHORT).show();
                }
                if(TextUtils.isEmpty(userNickName) || !RegularUtil.isNickName(userNickName) ){
                    Toast.makeText(this, "昵称必须为2—16位！", Toast.LENGTH_SHORT).show();
                }
                //开始提交数据
                Retrofit retrofit = new Retrofit.Builder().baseUrl(CommonValues.BaseUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create()).build();
                //{"status":1,"data":{"userNicheng":"abcabc","userId":13,"userPassword":"123456","userName":"abcabc","userEmail":"abcabc@126.com"},"msg":"注册成功！"}
                userService = retrofit.create(UserService.class);

                userService.reg(userName,userPassword,userEmail,userNickName)
                .map(new Func1<UserResponse, Observable<UserResponse>>() {
                    @Override
                    public Observable<UserResponse> call(UserResponse userResponse) {
                        Log.e("reg","reg:"+userResponse);
                        if(userResponse!=null &&userResponse.getStatus()==1){
                            User  user = userResponse.getData();
                            if(user!=null) {
                                return userService.login(user.getUserName(), user.getUserPassword());
                            }
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.newThread())//请求在新的线程中执行请求
                .observeOn(Schedulers.io())         //请求完成后在io线程中执行
                .doOnNext(new Action1<Observable<UserResponse>>() {
                    @Override
                    public void call(Observable<UserResponse> userResponseObservable) {

                    }
                })
                .observeOn(Schedulers.io())
                .subscribe(new Action1<Observable<UserResponse>>() {
                    @Override
                    public void call(Observable<UserResponse> userResponseObservable) {
                        if(userResponseObservable!=null){
                            userResponseObservable.subscribe(new Action1<UserResponse>() {
                                @Override
                                public void call(UserResponse userResponse) {
                                    Log.e("call", "login_after_reg-userResponse:" + userResponse);
                                    if (userResponse!=null) {
                                        if(userResponse.getStatus()==1){//成功
                                            LiveApplication.newInstance().setUser(userResponse.getData());
                                            startActivity(new Intent(RegActivity.this,MainActivity.class));
                                            RegActivity.this.finish();
                                        }else{
                                            Toast.makeText(RegActivity.this,userResponse.getMsg(),Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(RegActivity.this," 返回数据失败！ ",Toast.LENGTH_SHORT).show();
                        }

                    }
                }) ;
                break;
        }


    }
}
