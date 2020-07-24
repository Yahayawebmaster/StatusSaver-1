package com.collabcreation.statussaver.Activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.UrlQuerySanitizer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.collabcreation.statussaver.Modal.Common;
import com.collabcreation.statussaver.Modal.MyBridge;
import com.collabcreation.statussaver.Modal.ZoomstaUtil;
import com.collabcreation.statussaver.R;

import es.dmoral.toasty.Toasty;

public class InstagramOfficalLoginActivity extends AppCompatActivity {
    private WebView mWebView;
    private String mURL = "https://www.instagram.com/accounts/login/";
    private String cookies;
    private Boolean isSessionid = false;
    private String username = "";
    private String redirect_url;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        preferences = getSharedPreferences(Common.LOGIN, MODE_PRIVATE);
        initUI();

    }

    public void initUI() {
        redirect_url = "https://instagram.com/";
        mWebView = findViewById(R.id.webview);
        mWebView.clearHistory();
        mWebView.clearFormData();
        mWebView.clearCache(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new MyBridge(InstagramOfficalLoginActivity.this), "bridge");
        startWebView();

    }


    private void startWebView() {

        mWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e("InstagramLogin", "shouldOverrideUrlLoading: " + url);
                if (url.equalsIgnoreCase(redirect_url)) {
                    try {
                        view.loadUrl(url);
                        UrlQuerySanitizer.ValueSanitizer sanitizer = UrlQuerySanitizer.getAllButNulLegal();
                        sanitizer.sanitize(url);
                        String value = sanitizer.sanitize("username"); // get your value
                        if (MyBridge.getUsername() != null) username = MyBridge.getUsername();
                        Log.d("username", "shouldOverrideUrlLoading: "+ username + " "+value);
                    } catch (Exception e) {
                        Log.e("WebViewClient", "shouldOverrideUrlLoading: " + e);
                    }
                    return true;
                } else {
                    return false;
                }


            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                CookieManager.getInstance().removeAllCookies(null);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url.equalsIgnoreCase(mURL)) {
                    view.addJavascriptInterface(new MyBridge(InstagramOfficalLoginActivity.this), "bridge");
                    String javascript = "javascript: document.getElementsByClassName(\"_0mzm- sqdOP  L3NKy       \")[0].onclick = function() {\n" +
                            "        var username = document.getElementsByName(\"username\").value;\n" +
                            "        var password = document.getElementsByName(\"password\").value;\n" +
                            "        bridge.saveData(username, password);\n" +
                            "    };";
                    view.loadUrl(javascript);
                }


                if (isSessionid) {
                    Intent intent = new Intent(InstagramOfficalLoginActivity.this, MainActivity.class);
                    ZoomstaUtil.setBooleanPreference(getApplicationContext(), "isLogin", true);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    mWebView.destroy();
                    mWebView = null;
                    startActivity(intent);


                }

            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                cookies = CookieManager.getInstance().getCookie(url);

                try {
                    String session_id = getCookie(url, "sessionid");
                    String csrftoken = getCookie(url, "csrftoken");
                    String userid = getCookie(url, "ds_user_id");
                    if (session_id != null && csrftoken != null && userid != null) {
                        isSessionid = true;
                        Common.setCsrf(csrftoken, cookies);
                        ZoomstaUtil.setStringPreference(InstagramOfficalLoginActivity.this, cookies, "cooki");
                        ZoomstaUtil.setStringPreference(InstagramOfficalLoginActivity.this, Common.getCsrf(), "csrf");
                        ZoomstaUtil.setStringPreference(InstagramOfficalLoginActivity.this, session_id, "sessionid");
                        ZoomstaUtil.setStringPreference(InstagramOfficalLoginActivity.this, userid, "userid");
                        ZoomstaUtil.setStringPreference(InstagramOfficalLoginActivity.this, "", "username");


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toasty.error(getApplicationContext(), description).show();
            }

            @TargetApi(android.os.Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                // Redirect to deprecated method, so you can use it in all SDK versions
                onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
            }


        });

        mWebView.loadUrl(mURL);


    }


    public String getCookie(String siteName, String CookieName) {
        String CookieValue = null;

        CookieManager cookieManager = CookieManager.getInstance();
        String cookies = cookieManager.getCookie(siteName);
        if (cookies != null && !cookies.isEmpty()) {
            String[] temp = cookies.split(";");
            for (String ar1 : temp) {
                if (ar1.contains(CookieName)) {
                    String[] temp1 = ar1.split("=");
                    CookieValue = temp1[1];
                    break;
                }
            }
        }
        return CookieValue;
    }

    private void handleReturnCode(String error) {
        System.out.println("======  handleReturnCode error ====" + error);
        Intent intent = new Intent();
        if (error == null) {
            intent.putExtra("username", MyBridge.getUsername());
            intent.putExtra("password", MyBridge.getPassword());
            setResult(RESULT_OK, intent);
        } else {
            setResult(RESULT_CANCELED, intent);
        }
        finish();

    }


}
