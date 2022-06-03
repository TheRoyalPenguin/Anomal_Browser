package com.example.anomalbrowser.webAppLogic;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.PermissionRequest;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.anomalbrowser.R;

public class webApp extends AppCompatActivity {
    private WebView webView;
    private WebSettings webSettings;
    Bundle arguments;
    private void init()
    {
        webView = findViewById(R.id.appWebView);
        webSettings = webView.getSettings();
        arguments = getIntent().getExtras();
    }
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_app);
        init();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);


        webView.setWebViewClient(new ExtendWebViewClient());
        String name = arguments.get("url").toString();
        webView.loadUrl(name);

        // Go back on WebView
        webView.canGoBack();
        webView.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK
                        && event.getAction() == MotionEvent.ACTION_UP
                        && webView.canGoBack()) {
                    webView.goBack();
                    return true;
                }
                return false;
            }
        });
        //...
    }

}

class ExtendWebViewClient extends WebViewClient {

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        //Ignore SSL certificate errors
        handler.proceed();
    }
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }
}
