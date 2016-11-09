package com.xrtz.xrlive.activity;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.ksyun.media.streamer.kit.StreamerConstants;
import com.xrtz.xrlive.R;
import com.xrtz.xrlive.application.LiveApplication;
import com.xrtz.xrlive.fragment.HomeFragment;
import com.xrtz.xrlive.fragment.MeFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.image_tab_home)
    ImageView mLiveImage;
    @BindView(R.id.image_tab_me)
    ImageView mMeImage;
    @BindView(R.id.image_tab_show)
    ImageView mShowImage;

    HomeFragment homeFragment;
    MeFragment meFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mLiveImage.setImageResource(R.mipmap.tab_live_p);
        mMeImage.setImageResource(R.mipmap.tab_me);

        if(savedInstanceState==null){
            if(homeFragment==null) {
                homeFragment = HomeFragment.newInstance(null, null);
            }
            if(meFragment==null) {
                meFragment = MeFragment.newInstance(null, null);
            }
        }else{
            homeFragment = (HomeFragment)getSupportFragmentManager().findFragmentByTag("HomeFragment");
            meFragment = (MeFragment)getSupportFragmentManager().findFragmentByTag("MeFragment");
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (!homeFragment.isAdded()) {
            transaction.add(R.id.main_content, homeFragment,"HomeFragment").commit();
        } else{
            transaction.show(homeFragment).commit();
        }
    }
    @OnClick({R.id.image_tab_me,R.id.image_tab_home,R.id.image_tab_show})
    public void onClick(View view){
        switch (view.getId()) {
            case R.id.image_tab_home:
                //有个后缀_p的图片表示选择后的图片
                mLiveImage.setImageResource(R.mipmap.tab_live_p);
                mMeImage.setImageResource(R.mipmap.tab_me);

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                if (!homeFragment.isAdded()) {
                    Log.e("click","image_tab_me------homeFragment.isAdd = false");
                    transaction.add(R.id.main_content, homeFragment,"HomeFragment").commit();
                } else{
                    Log.e("click","image_tab_me------homeFragment.isAdd = true");
                    transaction.hide(meFragment);
                    transaction.show(homeFragment).commit();
                }
                break;
            case R.id.image_tab_me:
                mLiveImage.setImageResource(R.mipmap.tab_live);
                mMeImage.setImageResource(R.mipmap.tab_me_p);
                if(meFragment==null){
                    meFragment = MeFragment.newInstance(null, null);
                }
                FragmentTransaction t = getSupportFragmentManager().beginTransaction();
                if(homeFragment.isAdded()) {
                    t.hide(homeFragment);
                }
                if (!meFragment.isAdded()) {
                    Log.e("click","image_tab_me------meFragment.isAdd = false");
                    t.add(R.id.main_content, meFragment,"MeFragment").commit();
                } else{
                    Log.e("click","image_tab_me------meFragment.isAdd = true");
                    t.show(meFragment).commit();
                }
                break;
            case R.id.image_tab_show:
                //开启直播
                LiveApplication application = (LiveApplication) getApplication();
                String url = "rtmp://211.149.239.170:1935/live/"+application.getUser().getUserId();
                int frameRate= 15;
                int videoBitRate = 800;
                int audioBitRate = 48;
                int videoResolution = StreamerConstants.VIDEO_RESOLUTION_480P;
                boolean landscape = false;
                int encodeMethod =  StreamerConstants.ENCODE_METHOD_SOFTWARE;
                boolean startAuto = false;
                boolean showDebugInfo=false;
                Toast.makeText(this, "点击了开始直播", Toast.LENGTH_SHORT).show();
                CameraActivity.startActivity(getApplicationContext(), 0, url,frameRate, videoBitRate,
                        audioBitRate, videoResolution, landscape, encodeMethod,
                        startAuto, showDebugInfo);
                break;
        }
    }
}
