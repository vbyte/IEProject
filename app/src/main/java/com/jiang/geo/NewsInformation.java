package com.jiang.geo;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewsInformation extends AppCompatActivity {
    private String newsid;
    DatabaseReference mnews;
    FirebaseDatabase mdatabase;
    WebView webView;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_information);
        webView = (WebView) findViewById(R.id.webView);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        mdatabase = FirebaseDatabase.getInstance();
//        progressBar = (ProgressBar) findViewById(R.id.pro);
        mnews = mdatabase.getReference("News");
        if (getIntent() != null) {
            newsid = getIntent().getStringExtra("NewsID");

        }
        if (!newsid.isEmpty()) {
            webView.loadUrl(newsid);
            webView.setWebViewClient(new WebViewClient());
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
//                    progressBar.setVisibility(View.VISIBLE);
                    setTitle("Loading...");
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
//                    progressBar.setVisibility(View.GONE);
                    setTitle(view.getTitle());
                }
            });
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
