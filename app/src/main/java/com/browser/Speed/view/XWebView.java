package com.browser.Speed.view;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.MailTo;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.DecelerateInterpolator;
import android.webkit.CookieManager;
import android.webkit.GeolocationPermissions;
import android.webkit.HttpAuthHandler;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import com.browser.Speed.R;
import com.browser.Speed.activity.BrowserActivity;
import com.browser.Speed.constant.Constants;
import com.browser.Speed.constant.StartPage;
import com.browser.Speed.controller.BrowserController;
import com.browser.Speed.download.LightningDownloadListener;
import com.browser.Speed.preference.PreferenceManager;
import com.browser.Speed.utils.AdBlock;
import com.browser.Speed.utils.IntentUtils;
import com.browser.Speed.utils.Utils;

public class XWebView {

    private final Title mTitle;
    private CustomWebView mWebView;
    private BrowserController mBrowserController;
    private GestureDetector mGestureDetector;
    private OnTouchListener mOnTouchListener;
    private OnTouchListener mFullscreenListener;
    private LinearLayout mToolbarLayout;
    private final Activity mActivity;
    private WebSettings mSettings;
    private static String mHomepage;
    private static String mDefaultUserAgent;
    private static Bitmap mWebpageBitmap;
    private static PreferenceManager mPreferences;
    private final AdBlock mAdBlock;
    private boolean isPageAd;
    private IntentUtils mIntentUtils;
    private final Paint mPaint = new Paint();
    private int mPosition;
    private boolean mFlistenerSet;
    private boolean mFullscreenEnabled;
    private boolean isForegroundTab;
    private boolean mIncognitoTab = false;
    private boolean mTextReflow = false;
    private boolean mInvertPage = false;
    private static final int API = android.os.Build.VERSION.SDK_INT;
    private static final int SCROLL_UP_THRESHOLD = Utils.convertDpToPixels(10);
    private static final int SCROLL_DOWN_THRESHOLD = Utils.convertDpToPixels(100);
    private static final float[] mNegativeColorArray = { -1.0f, 0, 0, 0, 255, // red
            0, -1.0f, 0, 0, 255, // green
            0, 0, -1.0f, 0, 255, // blue
            0, 0, 0, 1.0f, 0 // alpha
    };

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public XWebView(Activity activity, String url, boolean darkTheme, boolean incognitoTab) {

        mActivity = activity;
        mWebView = new CustomWebView(activity);
        mTitle = new Title(activity, darkTheme);
        mAdBlock = AdBlock.getInstance(activity.getApplicationContext());

        mWebpageBitmap = Utils.getWebpageBitmap(activity.getResources(), darkTheme);

        try {
            mBrowserController = (BrowserController) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity + " must implement BrowserController");
        }
        mIntentUtils = new IntentUtils(mBrowserController);
        mWebView.setDrawingCacheBackgroundColor(0x00000000);
        mWebView.setFocusableInTouchMode(true);
        mWebView.setFocusable(true);
        mWebView.setAnimationCacheEnabled(false);
        mWebView.setDrawingCacheEnabled(false);
        mWebView.setWillNotCacheDrawing(true);
        mWebView.setAlwaysDrawnWithCacheEnabled(false);
        mWebView.setBackgroundColor(activity.getResources().getColor(android.R.color.white));

        mIncognitoTab = incognitoTab;

        if (API > 15) {
            mWebView.setBackground(null);
            mWebView.getRootView().setBackground(null);
        } else if (mWebView.getRootView() != null) {
            mWebView.getRootView().setBackgroundDrawable(null);
        }
        mWebView.setScrollbarFadingEnabled(true);
        mWebView.setSaveEnabled(true);
        mWebView.setWebChromeClient(new LightningChromeClient(activity));
        mWebView.setWebViewClient(new LightningWebClient(activity));
        mWebView.setDownloadListener(new LightningDownloadListener(activity));
        mGestureDetector = new GestureDetector(activity, new CustomGestureListener());
        mWebView.setOnTouchListener(mOnTouchListener = new TouchListener());
        mDefaultUserAgent = mWebView.getSettings().getUserAgentString();
        mSettings = mWebView.getSettings();
        initializeSettings(mWebView.getSettings(), activity);
        initializePreferences(activity);

        if (url != null) {
            if (!url.trim().isEmpty()) {
                mWebView.loadUrl(url);
            }
            //else {} // don't load anything, the user is looking for a blank tab
        }
        else {
            if (mHomepage.startsWith("about:home")) {
                mWebView.loadUrl(getHomepage());
            }
            else mWebView.loadUrl(mHomepage);
        }

        mWebView.addJavascriptInterface(new WebAppInterface(), "Android");
        mFullscreenEnabled = mPreferences.getFullScreenEnabled();
        mFullscreenListener = new FullscreenTouchListener();
    }

    public String getHomepage() {
        StringBuilder homepageBuilder = new StringBuilder();
        homepageBuilder.append(StartPage.HEAD);
        String icon;
        String searchUrl;
        switch (mPreferences.getSearchChoice()) {
            case 0: // CUSTOM SEARCH
                icon = "file:///android_asset/lightning.png";
                searchUrl = mPreferences.getSearchUrl();
                break;
            case 1: // GOOGLE_SEARCH;
                icon = "file:///android_asset/google.png"; // "https://www.google.com/images/srpr/logo11w.png";
                searchUrl = Constants.GOOGLE_SEARCH;
                break;
            case 2: // ANDROID SEARCH;
                icon = "file:///android_asset/ask.png";
                searchUrl = Constants.ASK_SEARCH;
                break;
            case 3: // BING_SEARCH;
                icon = "file:///android_asset/bing.png"; // "http://upload.wikimedia.org/wikipedia/commons/thumb/b/b1/Bing_logo_%282013%29.svg/500px-Bing_logo_%282013%29.svg.png";
                searchUrl = Constants.BING_SEARCH;
                break;
            case 4: // YAHOO_SEARCH;
                icon = "file:///android_asset/yahoo.png"; // "http://upload.wikimedia.org/wikipedia/commons/thumb/2/24/Yahoo%21_logo.svg/799px-Yahoo%21_logo.svg.png";
                searchUrl = Constants.YAHOO_SEARCH;
                break;
            case 5: // STARTPAGE_SEARCH;
                icon = "file:///android_asset/startpage.png"; // "https://startpage.com/graphics/startp_logo.gif";
                searchUrl = Constants.STARTPAGE_SEARCH;
                break;
            case 6: // STARTPAGE_MOBILE
                icon = "file:///android_asset/startpage.png"; // "https://startpage.com/graphics/startp_logo.gif";
                searchUrl = Constants.STARTPAGE_MOBILE_SEARCH;
                break;
            case 7: // DUCK_SEARCH;
                icon = "file:///android_asset/duckduckgo.png"; // "https://duckduckgo.com/assets/logo_homepage.normal.v101.png";
                searchUrl = Constants.DUCK_SEARCH;
                break;
            case 8: // DUCK_LITE_SEARCH;
                icon = "file:///android_asset/duckduckgo.png"; // "https://duckduckgo.com/assets/logo_homepage.normal.v101.png";
                searchUrl = Constants.DUCK_LITE_SEARCH;
                break;
            case 9: // BAIDU_SEARCH;
                icon = "file:///android_asset/baidu.png"; // "http://www.baidu.com/img/bdlogo.gif";
                searchUrl = Constants.BAIDU_SEARCH;
                break;
            case 10: // YANDEX_SEARCH;
                icon = "file:///android_asset/yandex.png"; // "http://upload.wikimedia.org/wikipedia/commons/thumb/9/91/Yandex.svg/600px-Yandex.svg.png";
                searchUrl = Constants.YANDEX_SEARCH;
                break;
            default: // DEFAULT GOOGLE_SEARCH;
                icon = "file:///android_asset/google.png";
                searchUrl = Constants.GOOGLE_SEARCH;
                break;

        }

        homepageBuilder.append(icon);
        homepageBuilder.append(StartPage.MIDDLE);
        homepageBuilder.append(searchUrl);
        homepageBuilder.append(StartPage.END);

        File homepage = new File(mActivity.getFilesDir(), "homepage.html");
        try {
            FileWriter hWriter = new FileWriter(homepage, false);
            hWriter.write(homepageBuilder.toString());
            hWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Constants.FILE + homepage;
    }

    @SuppressWarnings("deprecation")
    @SuppressLint({ "NewApi", "SetJavaScriptEnabled" })
    public synchronized void initializePreferences(Context context) {
        mPreferences = PreferenceManager.getInstance();
        mHomepage = mPreferences.getHomepage();
        mAdBlock.updatePreference();
        if (mSettings == null && mWebView != null) {
            mSettings = mWebView.getSettings();
        }
        else if (mSettings == null) return;

        setColorMode(mPreferences.getRenderingMode());

        if (!mIncognitoTab) {
            mSettings.setGeolocationEnabled(mPreferences.getLocationEnabled());
        }
        else mSettings.setGeolocationEnabled(false);
        if (API < 19) {
            switch (mPreferences.getFlashSupport()) {
                case 0:
                    mSettings.setPluginState(PluginState.OFF);
                    break;
                case 1:
                    mSettings.setPluginState(PluginState.ON_DEMAND);
                    break;
                case 2:
                    mSettings.setPluginState(PluginState.ON);
                    break;
                default:
                    break;
            }
        }

        switch (mPreferences.getUserAgentChoice()) {
            case 1:
                if (API > 16) {
                    mSettings.setUserAgentString(WebSettings.getDefaultUserAgent(context));
                }
                else mSettings.setUserAgentString(mDefaultUserAgent);
                break;
            case 2:
                mSettings.setUserAgentString(Constants.DESKTOP_USER_AGENT);
                break;
            case 3:
                mSettings.setUserAgentString(Constants.MOBILE_USER_AGENT);
                break;
            case 4:
                mSettings.setUserAgentString(mPreferences.getUserAgentString(mDefaultUserAgent));
                break;
        }

        if (mPreferences.getSavePasswordsEnabled() && !mIncognitoTab) {
            if (API < 18) {
                mSettings.setSavePassword(true);
            }
            mSettings.setSaveFormData(true);
        }
        else {
            if (API < 18) {
                mSettings.setSavePassword(false);
            }
            mSettings.setSaveFormData(false);
        }

        if (mPreferences.getJavaScriptEnabled()) {
            mSettings.setJavaScriptEnabled(true);
            mSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        }

        if (mPreferences.getTextReflowEnabled()) {
            mTextReflow = true;
            mSettings.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
            if (API >= android.os.Build.VERSION_CODES.KITKAT) {
                try {
                    mSettings.setLayoutAlgorithm(LayoutAlgorithm.TEXT_AUTOSIZING);
                } catch (Exception e) {
                    // This shouldn't be necessary, but there are a number
                    // of KitKat devices that crash trying to set this
                    Log.e(Constants.TAG, "Problem setting LayoutAlgorithm to TEXT_AUTOSIZING");
                }
            }
        } else {
            mTextReflow = false;
            mSettings.setLayoutAlgorithm(LayoutAlgorithm.NORMAL);
        }

        mSettings.setBlockNetworkImage(mPreferences.getBlockImagesEnabled());
        mSettings.setSupportMultipleWindows(mPreferences.getPopupsEnabled());
        mSettings.setUseWideViewPort(mPreferences.getUseWideViewportEnabled());
        mSettings.setLoadWithOverviewMode(mPreferences.getOverviewModeEnabled());
        switch (mPreferences.getTextSize()) {
            case 1:
                mSettings.setTextZoom(200);
                break;
            case 2:
                mSettings.setTextZoom(150);
                break;
            case 3:
                mSettings.setTextZoom(100);
                break;
            case 4:
                mSettings.setTextZoom(75);
                break;
            case 5:
                mSettings.setTextZoom(50);
                break;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(mWebView,
                    !mPreferences.getBlockThirdPartyCookiesEnabled());
        }
        mFullscreenEnabled = mPreferences.getFullScreenEnabled();
        if (mFullscreenEnabled && !mTitle.getTitle().equals("New Tab")) {
            mWebView.setOnTouchListener(mFullscreenListener);
            mFlistenerSet = true;
        }
        else mWebView.setOnTouchListener(mOnTouchListener);
    }

    @SuppressWarnings("deprecation")
    @SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
    public void initializeSettings(WebSettings settings, Context context) {
        if (API < 18) {
            settings.setAppCacheMaxSize(Long.MAX_VALUE);
        }
        if (API < 17) {
            settings.setEnableSmoothTransition(true);
        }
        if (API > 16) {
            settings.setMediaPlaybackRequiresUserGesture(true);
        }
        if (API >= Build.VERSION_CODES.LOLLIPOP && !mIncognitoTab) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        } else if (API >= Build.VERSION_CODES.LOLLIPOP) {
            // We're in Incognito mode, reject
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        }
        settings.setDomStorageEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setDatabaseEnabled(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setAllowContentAccess(true);
        settings.setAllowFileAccess(true);
        settings.setDefaultTextEncodingName("utf-8");
        if (API > 16) {
            settings.setAllowFileAccessFromFileURLs(false);
            settings.setAllowUniversalAccessFromFileURLs(false);
        }

        settings.setAppCachePath(context.getDir("appcache", 0).getPath());
        settings.setGeolocationDatabasePath(context.getDir("geolocation", 0).getPath());
        if (API < Build.VERSION_CODES.KITKAT) {
            settings.setDatabasePath(context.getDir("databases", 0).getPath());
        }
    }

    public boolean isShown() {
        return mWebView != null && mWebView.isShown();
    }

    public synchronized void onPause() {
        if (mWebView != null) {
            mWebView.onPause();
        }
    }

    public synchronized void onResume() {
        if (mWebView != null) {
            mWebView.onResume();
        }
    }

    public void setPositon(int index){
        mPosition = index;
    }

    public int getPosition() {
        return mPosition;
    }

    public void setForegroundTab(boolean isForeground) {
        isForegroundTab = isForeground;
        if (mIncognitoTab) mBrowserController.updateIncognitoTab(mPosition);
        else mBrowserController.updateTab(mPosition);
    }

    public void setAsForegroundTab(boolean isForeground, int position){
        isForegroundTab = isForeground;
        mPosition = position;
    }

    private void UpdateTab() {
        if (mIncognitoTab) mBrowserController.updateIncognitoTab(mPosition);
        else mBrowserController.updateTab(mPosition);
    }

    public boolean isForegroundTab() {
        return isForegroundTab;
    }

    public int getProgress() {
        if (mWebView != null) return mWebView.getProgress();
        else return 100;
    }

    public synchronized void stopLoading() {
        if (mWebView != null) {
            mWebView.stopLoading();
        }
    }

    public void setHardwareRendering() {
        mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, mPaint);
    }

    public void setNormalRendering() {
        mWebView.setLayerType(View.LAYER_TYPE_NONE, null);
    }

    public void setSoftwareRendering() {
        mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    public void setColorMode(int mode) {
        mInvertPage = false;
        switch (mode) {
            case 0:
                mPaint.setColorFilter(null);
                // setSoftwareRendering();
                // Some devices get segfaults in the WebView with Hardware Acceleration enabled,
                // the only fix is to disable hardware rendering
                setNormalRendering();
                mInvertPage = false;
                break;
            case 1:
                ColorMatrixColorFilter filterInvert = new ColorMatrixColorFilter(mNegativeColorArray);
                mPaint.setColorFilter(filterInvert);
                setHardwareRendering();
                mInvertPage = true;
                break;
            case 2:
                ColorMatrix cm = new ColorMatrix();
                cm.setSaturation(0);
                ColorMatrixColorFilter filterGray = new ColorMatrixColorFilter(cm);
                mPaint.setColorFilter(filterGray);
                setHardwareRendering();
                break;
            case 3:
                ColorMatrix matrix = new ColorMatrix();
                matrix.set(mNegativeColorArray);
                ColorMatrix matrixGray = new ColorMatrix();
                matrixGray.setSaturation(0);
                ColorMatrix concat = new ColorMatrix();
                concat.setConcat(matrix, matrixGray);
                ColorMatrixColorFilter filterInvertGray = new ColorMatrixColorFilter(concat);
                mPaint.setColorFilter(filterInvertGray);
                setHardwareRendering();

                mInvertPage = true;
                break;
        }
    }

    public synchronized void pauseTimers() {
        if (mWebView != null) {
            mWebView.pauseTimers();
        }
    }

    public synchronized void resumeTimers() {
        if (mWebView != null) {
            mWebView.resumeTimers();
        }
    }

    public void requestFocus() {
        if (mWebView != null && !mWebView.hasFocus()) {
            mWebView.requestFocus();
        }
    }

    public void setVisibility(int visible) {
        if (mWebView != null) {
            mWebView.setVisibility(visible);
        }
    }

    public void clearCache(boolean disk) {
        if (mWebView != null) {
            mWebView.clearCache(disk);
        }
    }

    public synchronized void reload() {
        if (mWebView != null) {
            mWebView.reload();
        }
    }

    private void cacheFavicon(Bitmap icon) {
        String hash = String.valueOf(Utils.getDomainName(getUrl()).hashCode());
        Log.d(Constants.TAG, "Caching icon for " + Utils.getDomainName(getUrl()));
        File image = new File(mActivity.getCacheDir(), hash + ".png");
        try {
            FileOutputStream fos = new FileOutputStream(image);
            icon.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        }
        catch (IOException e) { e.printStackTrace(); }
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public synchronized void find(String text) {
        if (mWebView != null) {
            if (API > 16) mWebView.findAllAsync(text);
            else mWebView.findAll(text);
        }
    }

    public synchronized void findNext(){
        mWebView.findNext(false);
    }

    public synchronized void findPrevious(){
        mWebView.findNext(true);
    }

    public Activity getActivity() {
        return mActivity;
    }

    public synchronized void onDestroy() {
        if (mWebView != null) {
            mWebView.stopLoading();
            mWebView.onPause();
            mWebView.clearHistory();
            mWebView.setVisibility(View.GONE);
            mWebView.removeAllViews();
            mWebView.destroyDrawingCache();
            // mWebView.destroy(); //this is causing the segfault
            mWebView = null;
        }
    }

    public synchronized void goBack() {
        if (mWebView != null) {
            mWebView.goBack();
        }
    }

    public String getUserAgent() {
        if (mWebView != null) {
            return mWebView.getSettings().getUserAgentString();
        }
        else return "";
    }

    public void setUserAgent(int i){
        if (mWebView != null) {
            if (i == 1) {
                if (API > 16) {
                    mSettings.setUserAgentString(WebSettings.getDefaultUserAgent(mActivity));
                }
                else mSettings.setUserAgentString(mDefaultUserAgent);
            }
            else mSettings.setUserAgentString(Constants.DESKTOP_USER_AGENT);
        }
    }

    public void setPadding(){
        mWebView.loadUrl("javascript:document.body.style.paddingTop = \"56px\"; void 0");
    }

    public void setToolbar(LinearLayout toolbar){
        mToolbarLayout = toolbar;
    }

    public boolean isDesktopMode(){
        return mSettings.getUserAgentString().equals(Constants.DESKTOP_USER_AGENT);
    }

    public synchronized void goForward() {
        if (mWebView != null) {
            mWebView.goForward();
        }
    }

    public boolean isPageAd(){ return isPageAd; }

    public boolean isIncognitoTab() { return mIncognitoTab; }

    public boolean canGoBack() {
        return mWebView != null && mWebView.canGoBack();
    }

    public boolean canGoForward() {
        return mWebView != null && mWebView.canGoForward();
    }

    public WebView getWebView() {
        return mWebView;
    }

    public Bitmap getFavicon() {
        return mTitle.getFavicon();
    }

    public synchronized void loadUrl(String url) {
        if (mWebView != null) {
            mWebView.loadUrl(url);
        }
    }

    public boolean isHistoryPage() {
        String url =  getUrl();
        return  url.contains("history.html");
    }

    public synchronized void invalidate() {
        if (mWebView != null) {
            mWebView.invalidate();
        }
    }

    public String getTitle() {
        return mTitle.getTitle();
    }

    public String getUrl() {
        if (mWebView != null) {
            return mWebView.getUrl();
        }
        else return "";
    }

    public class LightningWebClient extends WebViewClient {

        final Context mActivity;

        LightningWebClient(Context context) {
            mActivity = context;
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            if (mAdBlock.isAd(request.getUrl().getHost())) {
                ByteArrayInputStream EMPTY = new ByteArrayInputStream("".getBytes());
                return new WebResourceResponse("text/plain", "utf-8", EMPTY);
            }

            return super.shouldInterceptRequest(view, request);
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            if (mAdBlock.isAd(url)) {
                ByteArrayInputStream EMPTY = new ByteArrayInputStream("".getBytes());
                return new WebResourceResponse("text/plain", "utf-8", EMPTY);
            }
            return null;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (view.isShown()) {
                mBrowserController.updateUrl(url, true);
                view.postInvalidate();
                if (mFullscreenEnabled) {
                    if (mTitle.getTitle().equals("Homepage")) {
                        mWebView.setOnTouchListener(mOnTouchListener);
                        mFlistenerSet = false;
                        mReleased = true;
                    }
                    else if (!mFlistenerSet) {
                        mWebView.setOnTouchListener(mFullscreenListener);
                        mFlistenerSet = true;
                    }
                }
            }
            if (view.getTitle() == null || view.getTitle().isEmpty()) {
                mTitle.setTitle(mActivity.getString(R.string.untitled));
                if (mFullscreenEnabled) {
                    mWebView.setOnTouchListener(mOnTouchListener);
                    mFlistenerSet = false;
                }
            }
            else mTitle.setTitle(view.getTitle());
            if (API >= android.os.Build.VERSION_CODES.KITKAT && mInvertPage) {
                view.evaluateJavascript(Constants.JAVASCRIPT_INVERT_PAGE, null);
            }
            //UpdateTab();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (isShown()) {
                mBrowserController.updateUrl(url, false);
                mToolbarOffset = -1;
                onScrolled();
                setVisible();
            }
            mTitle.setFavicon(mWebpageBitmap);
            UpdateTab();
        }

        @Override
        public void onReceivedHttpAuthRequest(final WebView view, @NonNull final HttpAuthHandler handler, final String host, final String realm) {

            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            final EditText name = new EditText(mActivity);
            final EditText password = new EditText(mActivity);
            LinearLayout passLayout = new LinearLayout(mActivity);
            passLayout.setOrientation(LinearLayout.VERTICAL);

            passLayout.addView(name);
            passLayout.addView(password);

            name.setHint(mActivity.getString(R.string.hint_username));
            name.setSingleLine();
            password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            password.setSingleLine();
            password.setTransformationMethod(new PasswordTransformationMethod());
            password.setHint(mActivity.getString(R.string.hint_password));
            builder.setTitle(mActivity.getString(R.string.title_sign_in));
            builder.setView(passLayout);
            builder.setCancelable(true)
                    .setPositiveButton(mActivity.getString(R.string.title_sign_in),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    String user = name.getText().toString();
                                    String pass = password.getText().toString();
                                    handler.proceed(user.trim(), pass.trim());
                                    Log.d(Constants.TAG, "Request Login");
                                }
                            })
                    .setNegativeButton(mActivity.getString(R.string.action_cancel),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    handler.cancel();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }

        private boolean mIsRunning = false;
        private float mZoomScale = 0.0f;

        @Override
        public void onScaleChanged(final WebView view, final float oldScale, final float newScale) {
            if (view.isShown() && mTextReflow && API >= android.os.Build.VERSION_CODES.KITKAT) {
                if (mIsRunning) return;
                if (Math.abs(mZoomScale - newScale) > 0.01f) {
                    mIsRunning = view.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            mZoomScale = newScale;
                            view.evaluateJavascript(Constants.JAVASCRIPT_TEXT_REFLOW, null);
                            mIsRunning = false;
                        }

                    }, 100);
                }

            }
        }

        @Override
        public void onReceivedSslError(WebView view, @NonNull final SslErrorHandler handler, SslError error) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setTitle(mActivity.getString(R.string.title_warning));
            builder.setMessage(mActivity.getString(R.string.message_untrusted_certificate))
                    .setCancelable(true)
                    .setPositiveButton(mActivity.getString(R.string.action_yes),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    handler.proceed();
                                }
                            })
                    .setNegativeButton(mActivity.getString(R.string.action_no),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    handler.cancel();
                                }
                            });
            AlertDialog alert = builder.create();
            if (error.getPrimaryError() == SslError.SSL_UNTRUSTED) {
                alert.show();
            }
            else handler.proceed();
        }

        @Override
        public void onFormResubmission(WebView view, @NonNull final Message dontResend, final Message resend) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setTitle(mActivity.getString(R.string.title_form_resubmission));
            builder.setMessage(mActivity.getString(R.string.message_form_resubmission))
                    .setCancelable(true)
                    .setPositiveButton(mActivity.getString(R.string.action_yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) { resend.sendToTarget();
                                }
                            })
                    .setNegativeButton(mActivity.getString(R.string.action_no), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) { dontResend.sendToTarget(); }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (mAdBlock.isAd(url)) {
                isPageAd = true;
                return true;
            }
            if (url.startsWith("about:")) {
                return super.shouldOverrideUrlLoading(view, url);
            }
            if (url.contains("mailto:")) {
                MailTo mailTo = MailTo.parse(url);
                Intent i = Utils.newEmailIntent(mActivity, mailTo.getTo(), mailTo.getSubject(), mailTo.getBody(), mailTo.getCc());
                mActivity.startActivity(i);
                view.reload();
                return true;
            }
            else if (url.startsWith("intent://")) {
                Intent intent;
                try { intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME); }
                catch (URISyntaxException ex) { return false;}
                if (intent != null) {
                    try { mActivity.startActivity(intent); }
                    catch (ActivityNotFoundException e) {
                        Log.e(Constants.TAG, "ActivityNotFoundException");
                    }
                    return true;
                }
            }
            return mIntentUtils.startActivityForUrl(mWebView, url);
        }
    }

    public class LightningChromeClient extends WebChromeClient {

        final Context mActivity;

        LightningChromeClient(Context context) {
            mActivity = context;
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (isShown()) {
                mBrowserController.updateProgress(newProgress);
                if (mFullscreenEnabled) {
                    mWebView.loadUrl("javascript:document.body.style.paddingTop = \"56px\"; void 0");
                }
            }
        }

        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            mTitle.setFavicon(icon);
            UpdateTab();
            cacheFavicon(icon);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            if (!title.isEmpty()) {
                mTitle.setTitle(title);
            }
            else mTitle.setTitle(mActivity.getString(R.string.untitled));
            UpdateTab();
            if (!mIncognitoTab){
                mBrowserController.updateHistory(title, view.getUrl());
            }
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(final String origin, final GeolocationPermissions.Callback callback) {
            final boolean remember = true;
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setTitle(mActivity.getString(R.string.location));
            String org;
            if (origin.length() > 50) org = origin.subSequence(0, 50) + "...";
            else org = origin;

            builder.setMessage(org + mActivity.getString(R.string.message_location))
                    .setCancelable(true)
                    .setPositiveButton(mActivity.getString(R.string.action_allow),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    callback.invoke(origin, true, remember);
                                }
                            })
                    .setNegativeButton(mActivity.getString(R.string.action_dont_allow),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    callback.invoke(origin, false, remember);
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();

        }

        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            mBrowserController.onCreateWindow(isUserGesture, resultMsg);
            return true;
        }

        @Override
        public void onCloseWindow(WebView window) {
            // TODO Auto-generated method stub
            super.onCloseWindow(window);
        }

        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            mBrowserController.openFileChooser(uploadMsg);
        }

        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            mBrowserController.openFileChooser(uploadMsg);
        }

        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            mBrowserController.openFileChooser(uploadMsg);
        }

        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
            mBrowserController.showFileChooser(filePathCallback);
            return true;
        }

        @Override
        public Bitmap getDefaultVideoPoster() {
            return mBrowserController.getDefaultVideoPoster();
        }

        @Override
        public View getVideoLoadingProgressView() {
            return mBrowserController.getVideoLoadingProgressView();
        }

        @Override
        public void onHideCustomView() {
            mBrowserController.onHideCustomView();
            super.onHideCustomView();
        }

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            Activity activity = mBrowserController.getActivity();
            mBrowserController.onShowCustomView(view, activity.getRequestedOrientation(), callback);
            super.onShowCustomView(view, callback);
        }

        @Override
        @Deprecated
        public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
            mBrowserController.onShowCustomView(view, requestedOrientation, callback);
            super.onShowCustomView(view, requestedOrientation, callback);
        }
    }

    public class Title {

        private Bitmap mFavicon;
        public String mTitle;
        private final Bitmap mDefaultIcon;

        public Title(Context context, boolean darkTheme) {
            mDefaultIcon = Utils.getWebpageBitmap(context.getResources(), darkTheme);
            mFavicon = mDefaultIcon;
            mTitle = mActivity.getString(R.string.action_new_tab);
        }

        public void setFavicon(Bitmap favicon) {
            if (favicon == null) mFavicon = mDefaultIcon;
            else mFavicon = Utils.padFavicon(favicon);
        }

        public void setTitle(String title) {
            if (title == null) mTitle = "";
            else mTitle = title;
        }

        public void setTitleAndFavicon(String title, Bitmap favicon) {
            mTitle = title;

            if (favicon == null) mFavicon = mDefaultIcon;
            else mFavicon = Utils.padFavicon(favicon);
        }

        public String getTitle() {
            return mTitle;
        }

        public Bitmap getFavicon() {
            return mFavicon;
        }

    }

    private class TouchListener implements OnTouchListener {

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent e) {
            if (view != null && !view.hasFocus()) {
                view.requestFocus();
            }
            mGestureDetector.onTouchEvent(e);
            return false;
        }
    }

    private class CustomGestureListener extends SimpleOnGestureListener {

        private boolean mCanTriggerLongPress = true;

        @Override
        public void onLongPress(MotionEvent e) {
            if (mCanTriggerLongPress)
                mBrowserController.onLongPress();
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            mCanTriggerLongPress = false;
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            mCanTriggerLongPress = true;
        }
    }

    private class FullscreenTouchListener implements OnTouchListener {

        int mAction;

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent e) {
            if (view != null && !view.hasFocus()) {
                view.requestFocus();
            }
            mAction = e.getAction();
            if (mAction == MotionEvent.ACTION_DOWN) {
                mReleased = false;
                mScrolling = true;
                mScrollY = mWebView.getScrollY();
            }
            else if (mAction == MotionEvent.ACTION_UP) {
                mReleased = true;
                if (!mScrolling) onScrollStateChanged();
            }
            mGestureDetector.onTouchEvent(e);
            return false;
        }
    }

    private static final float HIDE_THRESHOLD = Utils.convertDpToPixels(24);
    private static final float SHOW_THRESHOLD = Utils.convertDpToPixels(24);

    private float mToolbarOffset = 0;
    private boolean mControlsVisible = true;
    private float mToolbarHeight = Utils.convertDpToPixels(56) ;
    private int mPadding = (int)mToolbarHeight + 10;
    private int mScrollPos = (int)mToolbarHeight;
    private boolean mFullscreen;
    private boolean mScrolling;
    private boolean mReleased;
    private float mScrollY;
    private float mdy;
    private int mY0;
    private int mY1;

    private void onScrollStateChanged() {
        if (mControlsVisible) {
            if (mToolbarOffset > HIDE_THRESHOLD) {
                setInvisible();
            }
            else setVisible();
        }
        else {
            if ((mToolbarHeight - mToolbarOffset) > SHOW_THRESHOLD) {
                setVisible();
            }
            else setInvisible();
        }
    }

    private void onScrolled() {
        if (mToolbarOffset > mToolbarHeight) {
            mToolbarOffset = mToolbarHeight;
            if (!mFullscreen) {
                mFullscreen = true;
                mBrowserController.setDrawerMargin(0);
            }
        }
        else if(mToolbarOffset < 0) {
            mToolbarOffset = 0;
            if (mFullscreen) {
                mFullscreen = false;
                mBrowserController.setDrawerMargin((int)mToolbarHeight);
            }
        }
        //mBrowserController.onScrolled(mToolbarOffset);
        mToolbarLayout.setTranslationY(-mToolbarOffset);

        if((mToolbarOffset < mToolbarHeight && mdy > 0) || (mToolbarOffset > 0 && mdy < 0)) {
            mToolbarOffset += mdy;
        }
    }

    private void setVisible() {
        if(mToolbarOffset > 0) {
            //mBrowserController.showToolbar();
            mToolbarLayout.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
            if (mScrollY < mPadding) {
                ObjectAnimator anim = ObjectAnimator.ofInt(mWebView, "scrollY", mWebView.getScrollY(), 0);
                anim.setInterpolator(new DecelerateInterpolator(2));
            }
            mToolbarOffset = 0;
            mFullscreen = false;
            mBrowserController.setDrawerMargin((int) mToolbarHeight);
        }
        else if (mY1 == 0) mToolbarLayout.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
        mControlsVisible = true;
    }

    private void setInvisible() {
        if(mToolbarOffset < mToolbarHeight) {
            //mBrowserController.hideToolbar();
            mToolbarLayout.animate().translationY(-mToolbarHeight).setInterpolator(new DecelerateInterpolator(2)).start();
            if (mScrollY < mPadding) {
                ObjectAnimator anim = ObjectAnimator.ofInt(mWebView, "scrollY", mWebView.getScrollY(), mScrollPos);
                anim.setInterpolator(new DecelerateInterpolator(2));
                anim.start();
            }
            mToolbarOffset = mToolbarHeight;
            mFullscreen = true;
            mBrowserController.setDrawerMargin(0);
        }
        mControlsVisible = false;
    }

    private final Handler mHandler = new Handler();
    private final Runnable mRunnable = new Runnable() {
        public void run() {
            if (mWebView.getScrollY() == mY1) {
                mScrolling = false;
                if (mReleased) onScrollStateChanged();
            }
        }
    };

    public class CustomWebView extends WebView
    {
        @Override
        protected void onScrollChanged(final int oldx, final int oldy, final int x, final int y) {
            super.onScrollChanged(oldx, oldy, x, y);
            if (!mFullscreenEnabled) return;
            mHandler.removeCallbacks(mRunnable);
            if (mScrolling || !mReleased) {
                mdy = oldy - y;
                onScrolled();
                mY1 = mWebView.getScrollY();
                mHandler.postDelayed(mRunnable, 20);
            }
        }

        public CustomWebView(final Context context)
        {
            super(context);
        }
        public CustomWebView(final Context context, final AttributeSet attrs) { super(context, attrs); }
        public CustomWebView(final Context context, final AttributeSet attrs, final int defStyle) { super(context, attrs, defStyle); }

    }
}