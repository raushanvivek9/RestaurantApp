package com.example.restaurant;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class TrainEnq extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        WebView web;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_enq);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Train Enq");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //check Internet Connection
        ConnectivityManager connectivityManager=(ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo infos=connectivityManager.getActiveNetworkInfo();
        if(null!=infos){
            web=findViewById(R.id.web);
            web.setWebViewClient(new myweb());
            web.getSettings().setJavaScriptEnabled(true);
            web.loadUrl("https://indianrailways.info/");

        }else{
            Toast.makeText(getApplicationContext(),"Network Error: Please Check your Connection!!",Toast.LENGTH_SHORT).show();
        }

    }
    public class myweb extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
