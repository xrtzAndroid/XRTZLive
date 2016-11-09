package com.xrtz.xrlive.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.os.Build;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.xrtz.xrlive.R;

/**
 * 加载网页的Activity，需要传网页地址过来
 * 原本打算用WebView的形式来加载登陆和注册页面的，现在可能用不上了
 */
public class BaseWebViewActivity extends AppCompatActivity {

    public static final String URL="url";
    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_web_view);

        Bundle bundle = getIntent().getExtras();
        String url = bundle.getString(URL);
        initialize(url);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initialize(String url){
        webView=(WebView)findViewById(R.id.webView);
        // 设置webview的各种属性
        WebSettings settings = webView.getSettings();
        if (Build.VERSION.SDK_INT >= 19) {// 设置一开始不加载图片
            settings.setLoadsImagesAutomatically(true);
        } else {
            settings.setLoadsImagesAutomatically(false);
        }

        settings.setAllowFileAccess(true);
        settings.setJavaScriptEnabled(true);
        settings.setAppCacheEnabled(true);
        //下面两句话解决努比亚手机加载不了webView网页（没下面两句，不然所有的网页都显示不了）
        //设置缓存模式
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                webView.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (!webView.getSettings().getLoadsImagesAutomatically()) {
                    webView.getSettings().setLoadsImagesAutomatically(true);// 加载图片
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                webView.loadDataWithBaseURL(null, "", "text/html", "utf-8",null);// 加载失败时候清除界面内容
            }
        });
    }

}

