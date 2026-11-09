package com.alphanicholson.qrmenu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Fullscreen WebView wrapper for the Il Conformista in-room dining menu,
 * tuned for Android TV (D-pad navigation, no touch, screen stays on).
 */
public class MainActivity extends Activity {

    /** The web app this APK wraps. Change here if the URL ever moves. */
    private static final String START_URL = "https://alphanicholson.com/qrmenu/";

    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hotel TV: never let the screen dim while the menu is open
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        webView = new WebView(this);
        webView.setBackgroundColor(Color.BLACK);
        webView.setFocusable(true);
        webView.setFocusableInTouchMode(true);
        setContentView(webView);

        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);          // localStorage (language choice etc.)
        s.setDatabaseEnabled(true);
        s.setCacheMode(WebSettings.LOAD_DEFAULT);
        s.setLoadWithOverviewMode(true);
        s.setUseWideViewPort(true);
        s.setSupportZoom(false);
        s.setBuiltInZoomControls(false);
        s.setDisplayZoomControls(false);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        // Identify as a TV so the site can adapt if it ever wants to
        s.setUserAgentString(s.getUserAgentString() + " AlphaNicholsonTV/1.0");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return handleUrl(request.getUrl());
            }

            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return handleUrl(Uri.parse(url));
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                if (request.isForMainFrame()) {
                    showOfflinePage();
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                view.requestFocus();
            }
        });

        hideSystemUi();

        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState);
        } else {
            webView.loadUrl(START_URL);
        }
    }

    /** Keep the menu inside the app; hand tel:/mailto: links to the system (no-op on most TVs). */
    private boolean handleUrl(Uri uri) {
        String scheme = uri.getScheme() == null ? "" : uri.getScheme();
        if (scheme.equals("http") || scheme.equals("https")) {
            return false; // let the WebView load it
        }
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        } catch (Exception ignored) {
            // TV has no dialer / mail client — just stay on the page
        }
        return true;
    }

    private void showOfflinePage() {
        String html = "<html><body style='margin:0;background:#1a1512;color:#f3e9df;"
                + "font-family:sans-serif;display:flex;align-items:center;justify-content:center;"
                + "height:100vh;text-align:center'><div>"
                + "<h1 style='font-weight:300;letter-spacing:.08em'>Il Conformista</h1>"
                + "<p style='opacity:.8'>The menu is temporarily unavailable.<br>"
                + "Το μενού δεν είναι προσωρινά διαθέσιμο.</p>"
                + "<p style='opacity:.6;font-size:.9em'>Reconnecting&hellip;</p>"
                + "</div></body></html>";
        webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
        // Retry automatically every 15 seconds
        webView.postDelayed(() -> webView.loadUrl(START_URL), 15000);
    }

    private void hideSystemUi() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) hideSystemUi();
    }

    /**
     * Remote-control keys:
     *  BACK  -> go back inside the menu; on the start page, exit the app.
     *  MENU / long-press handled by the TV launcher as usual.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    protected void onPause() {
        webView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        webView.destroy();
        super.onDestroy();
    }
}
