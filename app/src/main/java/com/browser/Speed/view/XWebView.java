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
import android.graphics.Picture;
import android.net.MailTo;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
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
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.browser.Speed.R;
import com.browser.Speed.activity.BottomDrawerManager;
import com.browser.Speed.activity.BrowserActivity;
import com.browser.Speed.constant.Constants;
import com.browser.Speed.constant.StartPage;
import com.browser.Speed.controller.IBrowserController;
import com.browser.Speed.controller.SpeedHandler;
import com.browser.Speed.controller.ThreadExecutor;
import com.browser.Speed.database.HistoryManager;
import com.browser.Speed.download.LightningDownloadListener;
import com.browser.Speed.helper.FaviconListener;
import com.browser.Speed.preference.PreferenceManager;
import com.browser.Speed.utils.AdBlock;
import com.browser.Speed.download.Downloader;
import com.browser.Speed.utils.IntentUtils;
import com.browser.Speed.utils.Utils;

public class XWebView {

    private String mTitle;
    private Bitmap mFavicon, mViewBitmap;
    private final Bitmap mDefaultIcon;
    private WebView mWebView;
    private final SpeedWebClient mWebClient = getWebClient();
    private final IBrowserController mBrowserController;
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
    private boolean mNightMode;
    private boolean mFlistenerSet;
    private boolean mFullscreenEnabled;
    private boolean isForegroundTab;
    private boolean IsIncognito = false;
    private boolean IsBackgroundTab = false;
    private boolean IsInterceptEnabled = false;
    private boolean mTextReflow = false;
    private static final int API = android.os.Build.VERSION.SDK_INT;
    private static final float[] mNegativeColorArray = { -1.0f, 0, 0, 0, 255, // red
            0, -1.0f, 0, 0, 255, // green
            0, 0, -1.0f, 0, 255, // blue
            0, 0, 0, 1.0f, 0 // alpha
    };

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public XWebView(Activity activity, String url, boolean incognitoTab, boolean background) {

        mActivity = activity;
        mBrowserController = (IBrowserController) activity;
        mPreferences = PreferenceManager.getInstance();
        boolean darkTheme = mPreferences.getUseDarkTheme();
        mDefaultIcon = Utils.getWebpageBitmap(activity.getResources(), darkTheme);
        mFavicon = mDefaultIcon;
        mTitle = mActivity.getString(R.string.action_new_tab);
        mAdBlock = AdBlock.getInstance(activity.getApplicationContext());
        IsIncognito = incognitoTab;
        IsBackgroundTab = background;

        mWebpageBitmap = Utils.getWebpageBitmap(activity.getResources(), darkTheme);

        mFullscreenEnabled = mPreferences.getFullScreenEnabled();
        mFullscreenListener = new FullscreenTouchListener();

        if (mFullscreenEnabled) {
            mWebView = new CustomWebView(activity);
        }
        else mWebView = new WebView(activity);

        mIntentUtils = new IntentUtils(mBrowserController);
        mWebView.setDrawingCacheBackgroundColor(0x00000000);
        mWebView.setFocusableInTouchMode(true);
        mWebView.setFocusable(true);
        mWebView.setAnimationCacheEnabled(false);
        mWebView.setDrawingCacheEnabled(false);
        mWebView.setWillNotCacheDrawing(true);
        mWebView.setAlwaysDrawnWithCacheEnabled(false);
        mWebView.setBackgroundColor(activity.getResources().getColor(android.R.color.white));
        mWebView.setScrollbarFadingEnabled(true);
        mWebView.setSaveEnabled(true);
        mWebView.setDownloadListener(new LightningDownloadListener(activity));
        mGestureDetector = new GestureDetector(activity, new CustomGestureListener());
        mWebView.setOnTouchListener(mOnTouchListener = new TouchListener());
        mDefaultUserAgent = mWebView.getSettings().getUserAgentString();
        mSettings = mWebView.getSettings();
        initializeSettings(mWebView.getSettings(), activity);
        initializePreferences();

        if (BottomDrawerManager.CardViewEnabled()) {
            mWebView.setWebViewClient(new OverrideWebClient());
            mWebView.setWebChromeClient(new OverrideChromeClient(url));
        }
        else {
            if (!mNightMode) mWebView.setWebViewClient(mWebClient);
            mWebView.setWebChromeClient(new SpeedChromeClient());
        }

        if (url != null) {
            if (!url.trim().isEmpty()) {
                mWebView.loadUrl(url);
            }
        }
        else {
            if (mHomepage.startsWith("about:home")) {
                mWebView.loadUrl(StartPage.mHomepage);
            }
            else mWebView.loadUrl(mHomepage);
        }

        mWebView.addJavascriptInterface(new JavaScriptInterface(), "Android");
    }

    public XWebView asBackground(){
        mWebView.setWebViewClient(new BackgroundWebClient());
        return this;
    }

    public synchronized void initializePreferences() {
        mHomepage = mPreferences.getHomepage();
        mAdBlock.updatePreference();
        if (mSettings == null && mWebView != null) {
            mSettings = mWebView.getSettings();
        }
        else if (mSettings == null) return;

        setColorMode(mPreferences.getRenderingMode());

        if (!IsIncognito) {
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
        mSettings.setUserAgentString(mPreferences.getUserAgentString(mDefaultUserAgent));
        if (mPreferences.getSavePasswordsEnabled() && !IsIncognito) {
            if (API < 18) mSettings.setSavePassword(true);
            mSettings.setSaveFormData(true);
        }
        else {
            if (API < 18) mSettings.setSavePassword(false);
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
                }
                catch (Exception e) { // This shouldn't be necessary, but there are a number of KitKat devices that crash trying to set this
                    Log.e(Constants.TAG, "Problem setting LayoutAlgorithm to TEXT_AUTOSIZING");
                    Utils.showToast(mActivity, "failed to enable text reflow");
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
        mSettings.setTextZoom(mPreferences.getTextSize());
        if (API >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(mWebView, !mPreferences.getBlockThirdPartyCookiesEnabled());
        }
//        mFullscreenEnabled = mPreferences.getFullScreenEnabled();
//        if (mFullscreenEnabled && !mTitle.equals("New Tab")) {
//            mWebView.setOnTouchListener(mFullscreenListener);
//            mFlistenerSet = true;
//        }
//        else mWebView.setOnTouchListener(mOnTouchListener);
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
        if (API >= Build.VERSION_CODES.LOLLIPOP && !IsIncognito) {
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

    public boolean isShown() { return mWebView != null && mWebView.isShown(); }
    public synchronized void onPause() { mWebView.onPause(); }
    public synchronized void onResume() { mWebView.onResume(); }

    public void setPositon(int index){ mPosition = index; }
    public int getPosition() { return mPosition; }

    public void setForegroundTab(boolean isForeground) {
        isForegroundTab = isForeground;
        mBrowserController.updateTab(mPosition, IsIncognito, 1);
    }

    public void setAsForegroundTab(boolean isForeground, int position){
        isForegroundTab = isForeground;
        mPosition = position;
    }

    private void UpdateTab() { mBrowserController.updateTab(mPosition, IsIncognito, 0); }
    public boolean isForegroundTab() { return isForegroundTab; }
    public int getProgress() { return mWebView.getProgress(); }
    public void requestFocus() { mWebView.requestFocus(); }
    public void setVisibility(int visible) { mWebView.setVisibility(visible); }
    public void clearCache(boolean disk) { mWebView.clearCache(disk); }
    public synchronized void reload() { mWebView.reload(); }
    public synchronized void stopLoading() { mWebView.stopLoading(); }

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

    private static final String mNightModeUrl = "javascript:(function(){N=document.createElement('link');S='*{background:#151515 !important;color:grey !important}:link,:link *{color:#ddddff !important}:visited,:visited *{color:#ddffdd !important}';N.rel='stylesheet';N.href='data:text/css,'+escape(S);document.getElementsByTagName('head')[0].appendChild(N);})()";

    public void setHardwareRendering() {
        mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, mPaint);
    }

    public void setNormalRendering() {
        if (mPreferences.getHardwareRenderingEnabled()) mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        else mWebView.setLayerType(View.LAYER_TYPE_NONE, null);
    }

    public void setSoftwareRendering() {
        mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    public void setColorMode(int mode) {
        switch (mode) {
            case 0:
                mPaint.setColorFilter(null);
                setNormalRendering();
                break;
            case 1:
                ColorMatrixColorFilter filterInvert = new ColorMatrixColorFilter(mNegativeColorArray);
                mPaint.setColorFilter(filterInvert);
                setHardwareRendering();
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
                break;
            case 4:
                mNightMode = true;
                mWebView.loadUrl(mNightModeUrl);
                mWebView.setWebViewClient(new NightMode());
                break;
        }
        if (mode != 4 && mNightMode) {
            mNightMode = false;
            mWebView.setWebViewClient(mWebClient);
        }
    }

    public void startDownloadManager(){
        mWebView.evaluateJavascript("javascript:window.Android.getPageSource" +
                "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');", null);
    }

    public synchronized void find(String text) {
        if (API > 16) mWebView.findAllAsync(text);
        else mWebView.findAll(text);
    }
    public synchronized void findNext(){ mWebView.findNext(false); }
    public synchronized void findPrevious(){ mWebView.findNext(true); }

    public synchronized void onDestroy() {
        mWebView.stopLoading();
        mWebView.onPause();
        mWebView.clearHistory();
        mWebView.setVisibility(View.GONE);
        mWebView.removeAllViews();
        mWebView.destroyDrawingCache();
        mWebView.destroy(); //this is causing the segfault
        mWebView = null;
    }

    public synchronized boolean goBack() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return false;
    }

    public synchronized void goForward() {
        if (mWebView.canGoForward()) mWebView.goForward();
    }

    public String getUserAgent() {
        return mWebView.getSettings().getUserAgentString();
    }

    public void setUserAgent(int i){
        if (i == 1) {
            if (API > 16) {
                mSettings.setUserAgentString(WebSettings.getDefaultUserAgent(mActivity));
            }
            else mSettings.setUserAgentString(mDefaultUserAgent);
        }
        else mSettings.setUserAgentString(Constants.DESKTOP_USER_AGENT);
    }

    public void setPadding(){
        mWebView.loadUrl("javascript:document.body.style.paddingTop = \"56px\"; void 0");
    }

    public WebView getWebView() {
        return mWebView;
    }

    public String getTitle() { return mTitle; }
    public String getUrl() { return mWebView.getUrl(); }

    public Bitmap getBitmap(){ return mViewBitmap; }
    public void setBitmap(Bitmap view){ mViewBitmap = view; }
    public Bitmap getFavicon() { return mFavicon; }

    public synchronized void loadUrl(String url) { mWebView.loadUrl(url); }

    public boolean isPageAd(){ return isPageAd; }
    public boolean isIncognitoTab() { return IsIncognito; }
    public boolean isBackgroundTab() { return IsBackgroundTab; }

    public boolean isHistoryPage() {
        String url =  getUrl();
        return  url.contains("history.html");
    }

    public boolean isDesktopMode(){
        return mSettings.getUserAgentString().equals(Constants.DESKTOP_USER_AGENT);
    }

    public boolean canGoBack() {
        return mWebView.canGoBack();
    }

    public synchronized void invalidate() {
        mWebView.invalidate();
    }

    private SpeedWebClient getWebClient(){
        if (API < 21) return new API20WebClient();
        else return new API21WebClient();
    }

    public boolean getInterceptEnabled() { return IsInterceptEnabled; }
    
    public boolean receivedFavicon(){
        return getProgress() != 100 && mFavicon != mWebpageBitmap;
    }

    public void setFaviconListener(FaviconListener listener){
        mFaviconListener = listener;
    }

    public String getFaviconName(){
        return String.valueOf(currentDomain) + ".png";
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

        private boolean mCanTriggerLongPress = false;

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
        float y1;
        float y2;

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent e) {
            if (view != null && !view.hasFocus()) {
                view.requestFocus();
            }
            mAction = e.getAction();
            if (mAction == MotionEvent.ACTION_DOWN) {
                y1 = e.getY();
                mReleased = false;
                mScrolling = true;
                mScrollY = mWebView.getScrollY();
                //mFrameLayout.setCanIntercept(mScrollY == 0);
                //if (mScrollY == 0) mFrameLayout.setCanIntercept(true);
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

        if((mToolbarOffset < mToolbarHeight && mdy > 0) || (mToolbarOffset > 0 && mdy < 0)) {
            mToolbarOffset += mdy;
        }
        mToolbarLayout.setTranslationY(-mToolbarOffset);
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

        public CustomWebView(final Context context)
        {
            super(context);
        }
        public CustomWebView(final Context context, final AttributeSet attrs) { super(context, attrs); }
        public CustomWebView(final Context context, final AttributeSet attrs, final int defStyle) { super(context, attrs, defStyle); }

    }
}