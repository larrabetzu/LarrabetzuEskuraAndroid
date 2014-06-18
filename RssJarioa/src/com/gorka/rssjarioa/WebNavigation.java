package com.gorka.rssjarioa;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.google.analytics.tracking.android.EasyTracker;


public class WebNavigation extends Activity {

    private ProgressBar progressBar;
    private WebView browser;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_navigation);

        final Bundle bundle=getIntent().getExtras();
        String link = bundle.getString("weblink");
        Log.e("link",link);
        browser = (WebView) findViewById(R.id.webview);
        browser.loadUrl(link);

        WebSettings settings = browser.getSettings();
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        if(link.equals("https://twitter.com/search?q=larrabetzu")){
            settings.setJavaScriptEnabled(true);
        }

        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        browser.setWebChromeClient(new WebChromeClient()
        {
            @Override
            public void onProgressChanged(WebView view, int progress)
            {
                progressBar.setProgress(0);
                progressBar.setVisibility(View.VISIBLE);
                WebNavigation.this.setProgress(progress * 1000);
                progressBar.incrementProgressBy(progress);
                if(progress == 100)
                {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        browser.setWebViewClient(new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                return false;
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_web, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_elkarbanatu:
                elkarbanatu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void elkarbanatu(){
        String testua = browser.getUrl()+" @larrabetzu #eskura";
        String title = "Aukeratu aplikazioa Ekintza elkarbanatzeko";
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, testua );
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, title));
    }

    @Override
    public void onStart() {
        super.onStart();
        // The rest of your onStart() code.
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        // The rest of your onStop() code.
        EasyTracker.getInstance(this).activityStop(this);
        finish();
    }
}
