package com.xrtz.xrlive.activity;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.hoo.mina.client.MinaClient;
import com.ksyun.media.player.IMediaPlayer;
import com.ksyun.media.player.KSYMediaMeta;
import com.ksyun.media.player.KSYMediaPlayer;
import com.xrtz.xrlive.R;
import com.xrtz.xrlive.adapter.ChatMsgAdapter;
import com.xrtz.xrlive.application.LiveApplication;
import com.xrtz.xrlive.common.CommonValues;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

/**
 * 看直播
 */
public class SurfacePlayerVideoActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener, View.OnLayoutChangeListener {
    public static final String TAG = "SurfacePlayerVideo";
    private Context mContext;
    private KSYMediaPlayer ksyMediaPlayer;

    private Surface mSurface = null;
    private SurfaceView mVideoSurfaceView = null;
    private SurfaceHolder mSurfaceHolder = null;
    private TextureView mVideoTextureView = null;
    private SurfaceTexture mSurfaceTexture = null;

    //视频播放器的宽高
    private int mVideoWidth = 0;
    private int mVideoHeight = 0;

    //视频地址
    private String mDataSource;

    //聊天信息显示的列表
    private RecyclerView chatRecyclerView;
    List<String> msgList = new ArrayList<>();//消息记录
    ChatMsgAdapter adapter;
    //底部发送消息按钮，送礼物的父容器
    private View bottomTools;
    //发送聊天消息的父容器
    private View sendMsgTools;
    //输入聊天消息的EditText
    private EditText etmsg;
    MinaClient client = new MinaClient();
    private View rootView;
    //屏幕高度
    private int screenHeight = 0;
    //软件盘弹起后所占高度阀值
    private int keyHeight = 0;

    //各种对视频的处理监听
    private IMediaPlayer.OnPreparedListener mOnPreparedListener = new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer mp) {

            mVideoWidth = ksyMediaPlayer.getVideoWidth();
            mVideoHeight = ksyMediaPlayer.getVideoHeight();

            // Set Video Scaling Mode
            ksyMediaPlayer.setVideoScalingMode(KSYMediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);

            //start player
            ksyMediaPlayer.start();

            //  get meta data
            Bundle bundle = ksyMediaPlayer.getMediaMeta();
            KSYMediaMeta meta = KSYMediaMeta.parse(bundle);
            if (meta != null)
            {
                if (meta.mHttpConnectTime > 0) {
                    double http_connection_time = Double.valueOf(meta.mHttpConnectTime);
                    //mHttpConnectionTime.setText("HTTP Connection Time: " + (int)http_connection_time);
                }

                int dns_time = meta.mAnalyzeDnsTime;
                if (dns_time > 0) {
                    //mDNSTime.setText("DNS time: " + dns_time);
                }
            }

        }
    };
    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onEvent(String msg){
        msgList.add(msg);
        adapter.notifyDataSetChanged();
        chatRecyclerView.smoothScrollToPosition(msgList.size()-1);
    }


    private IMediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener = new IMediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(IMediaPlayer mp, int percent) {
            /*long duration = ksyMediaPlayer.getDuration();
            long progress = duration * percent/100;
            mPlayerSeekbar.setSecondaryProgress((int)progress);*/
        }
    };

    private IMediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangeListener = new IMediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sarNum, int sarDen) {
            if(mVideoWidth > 0 && mVideoHeight > 0) {
                if(width != mVideoWidth || height != mVideoHeight) {
                    mVideoWidth = mp.getVideoWidth();
                    mVideoHeight = mp.getVideoHeight();

                    if(ksyMediaPlayer != null)
                        ksyMediaPlayer.setVideoScalingMode(KSYMediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                }
            }
        }
    };

    private IMediaPlayer.OnSeekCompleteListener mOnSeekCompletedListener = new IMediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(IMediaPlayer mp) {
        }
    };
    private IMediaPlayer.OnCompletionListener mOnCompletionListener = new IMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(IMediaPlayer mp) {
            Toast.makeText(mContext, "OnCompletionListener, play complete.", Toast.LENGTH_LONG).show();
            videoPlayEnd();
        }
    };
    private void videoPlayEnd() {
        if(ksyMediaPlayer != null)
        {
            ksyMediaPlayer.release();
            ksyMediaPlayer = null;
        }

        /*if(mQosThread != null) {
            mQosThread.stopThread();
            mQosThread = null;
        }*/

        //mHandler = null;

        finish();
    }
    private IMediaPlayer.OnErrorListener mOnErrorListener = new IMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(IMediaPlayer mp, int what, int extra) {
            switch (what)
            {
                case KSYMediaPlayer.MEDIA_ERROR_UNKNOWN:
                    Log.e(TAG, "OnErrorListener, Error Unknown:" + what + ",extra:" + extra);
                    break;
                default:
                    Log.e(TAG, "OnErrorListener, Error:" + what + ",extra:" + extra);
            }

            videoPlayEnd();

            return false;
        }
    };

    public IMediaPlayer.OnInfoListener mOnInfoListener = new IMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
            switch (i) {
                case KSYMediaPlayer.MEDIA_INFO_BUFFERING_START:
                    Log.d(TAG, "Buffering Start.");
                    break;
                case KSYMediaPlayer.MEDIA_INFO_BUFFERING_END:
                    Log.d(TAG, "Buffering End.");
                    break;
                case KSYMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:
                    //Toast.makeText(mContext, "Audio Rendering Start", Toast.LENGTH_SHORT).show();
                    break;
                case KSYMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                    //Toast.makeText(mContext, "Video Rendering Start", Toast.LENGTH_SHORT).show();
                    break;
                case KSYMediaPlayer.MEDIA_INFO_SUGGEST_RELOAD:
                    // Player find a new stream(video or audio), and we could reload the video.
                    if(ksyMediaPlayer != null)
                        ksyMediaPlayer.reload(mDataSource, false);
                    break;
                case KSYMediaPlayer.MEDIA_INFO_RELOADED:
                    //Toast.makeText(mContext, "Succeed to reload video.", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Succeed to reload video.");
                    return false;
            }
            return false;
        }
    };
    private final SurfaceHolder.Callback mSurfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if(ksyMediaPlayer != null && ksyMediaPlayer.isPlaying())
                ksyMediaPlayer.setVideoScalingMode(KSYMediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if(ksyMediaPlayer != null)
                ksyMediaPlayer.setDisplay(holder);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.d(TAG, "surfaceDestroyed");
            if(ksyMediaPlayer != null) {
                ksyMediaPlayer.setDisplay(null);
            }
        }
    };
    //视频缩放用的
    /*private View.OnClickListener mVideoScaleButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int mode = mVideoScaleIndex % 2;
            mVideoScaleIndex++;
            mHandler.removeMessages(HIDDEN_SEEKBAR);
            Message msg = new Message();
            msg.what = HIDDEN_SEEKBAR;
            mHandler.sendMessageDelayed(msg,3000);
            if(ksyMediaPlayer != null) {
                if(mode == 1)
                    ksyMediaPlayer.setVideoScalingMode(KSYMediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                else
                    ksyMediaPlayer.setVideoScalingMode(KSYMediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
            }
        }
    };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_surface_player_video);
        rootView = findViewById(R.id.activity_video_player);
        rootView.addOnLayoutChangeListener(this);
        new Thread(){
            @Override
            public void run() {
                if (client.connect(CommonValues.CHATIP,CommonValues.CHATPORT)) {
                    //连接服务器这个操作是耗时的，所以在子线程
                    //connect 连接好服务器后 ,会启动一个子线程接受消息的,接受消息的处理类叫 ClientMessageHandlerAdapter ，子线程通过EventBus将接受到的消息发送到主线程来更新列表
                }
            }
        }.start();

        //获取屏幕高度
        screenHeight = this.getWindowManager().getDefaultDisplay().getHeight();
        //阀值设置为屏幕高度的1/3
        keyHeight = screenHeight/3;
        mDataSource = getIntent().getStringExtra("path");

        mVideoSurfaceView = (SurfaceView) findViewById(R.id.player_surface);
        mSurfaceHolder = mVideoSurfaceView.getHolder();
        mSurfaceHolder.addCallback(mSurfaceCallback);
        mVideoSurfaceView.setKeepScreenOn(true);

        ksyMediaPlayer = new KSYMediaPlayer.Builder(mContext).build();
        ksyMediaPlayer.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
        ksyMediaPlayer.setOnCompletionListener(mOnCompletionListener);
        ksyMediaPlayer.setOnPreparedListener(mOnPreparedListener);
        ksyMediaPlayer.setOnInfoListener(mOnInfoListener);
        ksyMediaPlayer.setOnVideoSizeChangedListener(mOnVideoSizeChangeListener);
        ksyMediaPlayer.setOnErrorListener(mOnErrorListener);
        ksyMediaPlayer.setOnSeekCompleteListener(mOnSeekCompletedListener);
        ksyMediaPlayer.setScreenOnWhilePlaying(true);
        ksyMediaPlayer.setBufferTimeMax(3);
        ksyMediaPlayer.setTimeout(5, 30);

        chatRecyclerView = (RecyclerView) findViewById(R.id.msglist);
        /*for(int i=0;i<15;i++){
            StringBuffer sb= new StringBuffer();
            for(int j=0;j<6;j++){
                sb.append(i);
            }
            msgList.add(sb.toString());
        }*/
        //
        adapter = new ChatMsgAdapter(msgList);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        chatRecyclerView.setAdapter(adapter);

        sendMsgTools = findViewById(R.id.send_msg);
        bottomTools = findViewById(R.id.room_bottom_tools);
        etmsg = (EditText) findViewById(R.id.etmsg);
        etmsg.setOnFocusChangeListener(this);
        findViewById(R.id.playvideo_sendmsg).setOnClickListener(this);
        findViewById(R.id.send).setOnClickListener(this);
        try {
            ksyMediaPlayer.setDataSource(mDataSource);
            ksyMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //退出播放
        findViewById(R.id.playvideo_stop).setOnClickListener(this);

        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.playvideo_stop:
                videoPlayEnd();
                break;

            case R.id.playvideo_sendmsg://发送消息按钮点击，显示消息的输入框
                sendMsgTools.setVisibility(View.VISIBLE);
                bottomTools.setVisibility(View.GONE);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                etmsg.setFocusable(true);
                etmsg.setFocusableInTouchMode(true);
                etmsg.requestFocus();
                etmsg.findFocus();
                break;
            case R.id.send://正式发消息
                //发送消息后，输入框还在那里
                String msg = etmsg.getText().toString().trim();
                if(!TextUtils.isEmpty(msg)){
                    msgList.add(LiveApplication.newInstance().getUser().getUserName()+":"+msg);
                    adapter.notifyDataSetChanged();
                    etmsg.setText("");
                    client.send(msg);
                }
                break;
        }
    }
    //消息输入框是否还有获取到焦点
    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if (hasFocus) {
            // 获得焦点

        } else {
            // 失去焦点
            sendMsgTools.setVisibility(View.GONE);
            bottomTools.setVisibility(View.VISIBLE);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoTextureView = null;
        mSurfaceTexture = null;
        EventBus.getDefault().unregister(this);
    }
    @Override
    protected void onPause() {
        super.onPause();

        if(ksyMediaPlayer != null)
        {
            ksyMediaPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(ksyMediaPlayer != null)
        {
            ksyMediaPlayer.start();
        }
    }
    @Override
    public int getChangingConfigurations() {
        return super.getChangingConfigurations();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
    /*public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK  && event.getRepeatCount() == 0) {

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }*/

    @Override
    public void onLayoutChange(View v, int left, int top, int right,
                               int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        //old是改变前的左上右下坐标点值，没有old的是改变后的左上右下坐标点值
        //现在认为只要控件将Activity向上推的高度超过了1/3屏幕高，就认为软键盘弹起
        if(oldBottom != 0 && bottom != 0 &&(oldBottom - bottom > keyHeight)){
            //监听到软件盘打开...

        }else if(oldBottom != 0 && bottom != 0 &&(bottom - oldBottom > keyHeight)){
            //监听到软件盘关闭...
            sendMsgTools.setVisibility(View.GONE);
            bottomTools.setVisibility(View.VISIBLE);
        }
    }
}
