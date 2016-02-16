package com.browser.Speed.activity;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.content.res.Configuration;
import android.content.res.Resources.Theme;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.support.v7.internal.widget.TintImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.*;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebView.HitTestResult;
import android.widget.*;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView.OnEditorActionListener;

import com.browser.Speed.database.BookmarkFolder;
import com.browser.Speed.database.BookmarkItem;
import com.browser.Speed.controller.BookmarkController;
import com.browser.Speed.database.FileBookmarkManager;
import com.browser.Speed.database.HistoryManager;
import com.browser.Speed.helper.ItemTouchHelperAdapter;
import com.browser.Speed.helper.ItemTouchHelperViewHolder;
import com.browser.Speed.helper.SimpleItemTouchHelperCallback;
import com.browser.Speed.utils.CustomViewPager;
import com.browser.Speed.view.AnimatedProgressBar;
import com.browser.Speed.controller.BrowserController;
import com.browser.Speed.object.ClickHandler;
import com.browser.Speed.constant.Constants;
import com.browser.Speed.database.HistoryDatabase;
import com.browser.Speed.database.HistoryItem;
import com.browser.Speed.constant.HistoryPage;
import com.browser.Speed.view.XWebView;
import com.browser.Speed.preference.PreferenceManager;
import com.browser.Speed.R;
import com.browser.Speed.object.SearchAdapter;
import com.browser.Speed.utils.Utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

public class BrowserActivity extends ThemableActivity implements BrowserController, OnClickListener {

    // Layout
    private DrawerLayout mDrawerLayout;
    private FrameLayout mWebViewFrame;
    private FrameLayout mMainLayout;
    private FullscreenHolder mFullscreenContainer;
    private RecyclerView mNormalTabs, mIncognitoTabs;
    private LinearLayout mRightDrawer, mLeftDrawer, mToolbarLayout, mSearchBox;
    private LinearLayoutManager mIncognitoManager, mTabManager;
    private RelativeLayout mSearchBar;
    private EditText mFindEditText;
    private String mFindText;
    private CustomViewPager mViewPager;
    private int mToolbarHeight;

    private final List<XWebView> mWebViews = new ArrayList<>();
    private final List<XWebView> mIncognitoWebViews = new ArrayList<>();
    private FileBookmarkManager mBookmarkManager;

    private XWebView mCurrentView;
    private WebView mPreviousView;

    private AnimatedProgressBar mProgressBar;
    private Animator.AnimatorListener mAnimEndListener;
    private AutoCompleteTextView mSearch;
    private ImageView mTabCounter, mOverlay;
    private VideoView mVideoView;
    private View mCustomView, mVideoProgressView;

    private LightningViewAdapter mTabsAdapter, mIncognitoAdapter;
    private SearchAdapter mSearchAdapter;

    private ClickHandler mClickHandler;
    private CustomViewCallback mCustomViewCallback;
    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mFilePathCallback;

    private Activity mActivity;

    private boolean mSystemBrowser = false, mIsNewIntent = false, mFullScreen, mDarkTheme, mExitOnTabClose, mTabButtonSwiped;
    private int mOriginalOrientation, mBackgroundColor, mIdGenerator;
    private int mNumberColor, mHighlight;
    private String mSearchText, mUntitledTitle, mHomepage, mCameraPhotoPath;

    private HistoryDatabase mHistoryDatabase;
    private HistoryManager mHistoryManager;
    private RelativeLayout mHistoryPage;
    private String mHistoryUrl = "file://history.html";
    private PreferenceManager mPreferences;

    private Bitmap mDefaultVideoPoster, mWebpageBitmap;
    private final ColorDrawable mBackground = new ColorDrawable();
    private Drawable mDeleteIcon, mRefreshIcon, mCopyIcon, mPasteIcon, mIcon;
    private TintImageView mOverflowIcon;

    private static final int API = android.os.Build.VERSION.SDK_INT;
    private static final LayoutParams MATCH_PARENT = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    private static final FrameLayout.LayoutParams COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    private int mToolBarSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    private synchronized void initialize() {
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        mPreferences = PreferenceManager.getInstance();
        mDarkTheme = mPreferences.getUseDarkTheme();
        mActivity = this;
        mWebViews.clear();
        mIncognitoWebViews.clear();

        mClickHandler = new ClickHandler(this);
        mMainLayout = (FrameLayout) findViewById(R.id.main_layout);
        mWebViewFrame = (FrameLayout) findViewById(R.id.content_frame);
        mToolbarLayout = (LinearLayout) findViewById(R.id.toolbar_layout);
        mBackground.setColor(((ColorDrawable) mToolbarLayout.getBackground()).getColor());

        mProgressBar = (AnimatedProgressBar) findViewById(R.id.progress_view);

        mToolBarSize = Utils.convertDpToPixels(48);
        mToolbarHeight = Utils.convertDpToPixels(56);
        initFindInPage();

        RelativeLayout newTab = (RelativeLayout) findViewById(R.id.new_tab_button);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);mRightDrawer = (LinearLayout) findViewById(R.id.right_drawer);
        mRightDrawer.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        mLeftDrawer = (LinearLayout) findViewById(R.id.left_drawer);
        mLeftDrawer.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        setDrawerMargin(mToolbarHeight);
        if (mPreferences.getFullScreenEnabled()){
            DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) mWebViewFrame.getLayoutParams();
            params.topMargin = 0;
            mWebViewFrame.setLayoutParams(params);
        }
        else {
            DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) mWebViewFrame.getLayoutParams();
            params.topMargin = mToolbarHeight;
            mWebViewFrame.setLayoutParams(params);
        }

        setNavigationDrawerWidth();
        mDrawerLayout.setDrawerListener(new DrawerLocker());

        mWebpageBitmap = Utils.getWebpageBitmap(getResources(), mDarkTheme);
        mHomepage = mPreferences.getHomepage();



        mBookmarkManager = new FileBookmarkManager(this, mDrawerLayout);

        mNormalTabs = (RecyclerView) getLayoutInflater().inflate(R.layout.tab_listview, null);
        mIncognitoTabs = (RecyclerView) getLayoutInflater().inflate(R.layout.tab_listview, null);

        mTabsAdapter = new LightningViewAdapter(this, R.layout.tab_list_item, mWebViews);
        mNormalTabs.setAdapter(mTabsAdapter);
        mNormalTabs.setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);
        mNormalTabs.setHasFixedSize(true);
        mTabManager = new LinearLayoutManager(mActivity);
        mTabManager.setStackFromEnd(true);
        mNormalTabs.setLayoutManager(mTabManager);
        mNormalTabs.getItemAnimator().setRemoveDuration(100);
        mNormalTabs.getItemAnimator().setMoveDuration(140);
        ((SimpleItemAnimator) mNormalTabs.getItemAnimator()).setSupportsChangeAnimations(false);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mTabsAdapter);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mNormalTabs);

        mIncognitoAdapter = new LightningViewAdapter(this, R.layout.tab_list_item, mIncognitoWebViews);
        mIncognitoTabs.setAdapter(mIncognitoAdapter);

        mIncognitoTabs.setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);
        mIncognitoTabs.setHasFixedSize(true);

        mIncognitoManager = new LinearLayoutManager(mActivity);
        mIncognitoManager.setStackFromEnd(true);
        mIncognitoTabs.setLayoutManager(mIncognitoManager);
        mIncognitoTabs.getItemAnimator().setRemoveDuration(100);
        mIncognitoTabs.getItemAnimator().setMoveDuration(140);
        ((SimpleItemAnimator) mIncognitoTabs.getItemAnimator()).setSupportsChangeAnimations(false);

        callback = new SimpleItemTouchHelperCallback(mIncognitoAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mIncognitoTabs);

        try { mHistoryDatabase = HistoryDatabase.getInstance(getApplicationContext()); }
        catch (Exception e) {}

        mHistoryManager = new HistoryManager(this, getLayoutInflater());
        mHistoryUrl = HistoryPage.initHistoryPage(this);

        // set display options of the ActionBar
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.toolbar_content);

        mSearchBox = (LinearLayout) findViewById(R.id.search_box);

        final View v = actionBar.getCustomView();
        LayoutParams lp = v.getLayoutParams();
        lp.width = LayoutParams.MATCH_PARENT;
        v.setLayoutParams(lp);

        mTabCounter = (ImageView) actionBar.getCustomView().findViewById(R.id.tabCount);
        mTabCounter.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        final LinearLayout tabButton = (LinearLayout) actionBar.getCustomView().findViewById(R.id.tabs_button);
        //tabButton.setOnClickListener(this);
        final GestureDetector gestureDetector = new GestureDetector(new OnSwipeListener());
        tabButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    view.setPressed(true);
                    mTabButtonSwiped = false;
                }
                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    view.setPressed(false);
                    if (!mTabButtonSwiped) {
                        openTabDrawer();
                    }
                }
                return true;
            }
        });

        RelativeLayout back = (RelativeLayout) findViewById(R.id.action_back);
        back.setOnClickListener(this);

        RelativeLayout forward = (RelativeLayout) findViewById(R.id.action_forward);
        forward.setOnClickListener(this);

        // create the search EditText in the ToolBar
        mSearch = (AutoCompleteTextView) actionBar.getCustomView().findViewById(R.id.search);
        mUntitledTitle = getString(R.string.untitled);
        mBackgroundColor = getResources().getColor(R.color.primary_color);
        mNumberColor = getResources().getColor(R.color.tabs);
        mHighlight = getResources().getColor(R.color.accent_color);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mDeleteIcon = getResources().getDrawable(R.drawable.ic_action_delete);
            mRefreshIcon = getResources().getDrawable(R.drawable.ic_action_refresh);
            mCopyIcon = getResources().getDrawable(R.drawable.ic_action_copy);
            mPasteIcon = getResources().getDrawable(R.drawable.ic_action_paste);
        }
        else {
            Theme theme = getTheme();
            mDeleteIcon = getResources().getDrawable(R.drawable.ic_action_delete, theme);
            mRefreshIcon = getResources().getDrawable(R.drawable.ic_action_refresh, theme);
            mCopyIcon = getResources().getDrawable(R.drawable.ic_action_copy, theme);
            mPasteIcon = getResources().getDrawable(R.drawable.ic_action_paste, theme);
        }

        int iconBounds = Utils.convertDpToPixels(24);
        mDeleteIcon.setBounds(0, 0, iconBounds, iconBounds);
        mRefreshIcon.setBounds(0, 0, iconBounds, iconBounds);
        mCopyIcon.setBounds(0, 0, iconBounds, iconBounds);
        mPasteIcon.setBounds(0, 0, iconBounds, iconBounds);
        mIcon = mRefreshIcon;
        SearchClass search = new SearchClass();
        mSearch.setCompoundDrawables(null, null, mRefreshIcon, null);
        mSearch.setOnKeyListener(search.new KeyListener());
        mSearch.setOnFocusChangeListener(search.new FocusChangeListener());
        mSearch.setOnEditorActionListener(search.new EditorActionListener());
        mSearch.setOnTouchListener(search.new TouchListener());

        mSystemBrowser = getSystemBrowser();
        Thread initialize = new Thread(new Runnable() {

            @Override
            public void run() {
                mBookmarkManager.LoadBookmarks(null);
                initializeSearchSuggestions(mSearch);
            }

        });
        initialize.run();

        newTab.setOnClickListener(this);
        newTab.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                String url = mPreferences.getSavedUrl();
                if (url != null) {
                    newTab(url, true, false, true, false);
                    Toast.makeText(mActivity, R.string.deleted_tab, Toast.LENGTH_SHORT).show();
                }
                mPreferences.setSavedUrl(null);
                return true;
            }

        });

        mViewPager = (CustomViewPager) findViewById(R.id.viewpager);
        setupViewPager(mViewPager);
        TabLayout mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        mOverflowIcon = new TintImageView(this);
        initializeTabCounter();
        initializeTabs();

        if (API <= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            WebIconDatabase.getInstance().open(getDir("icons", MODE_PRIVATE).getPath());
        }

        final String overflowDesc = getString(R.string.overflow_id);
        final ViewGroup decor = (ViewGroup) getWindow().getDecorView();
        decor.postDelayed(new Runnable() {
            @Override
            public void run() {
                final ArrayList<View> outViews = new ArrayList<>();
                decor.findViewsWithText(outViews, overflowDesc, View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
                if (outViews.isEmpty()) return;
                mOverflowIcon = (TintImageView) outViews.get(0);
            }

        }, 1000);

        initializePreferences();
        mAnimEndListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mWebViewFrame.removeView(mPreviousView);
            }
            @Override
            public void onAnimationStart(Animator animation) {}
            @Override
            public void onAnimationCancel(Animator animation) {}
            @Override
            public void onAnimationRepeat(Animator animation) {}
        };
    }

    private void setupViewPager(CustomViewPager viewPager) {
        ViewPagerAdapter mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        FragmentTabView tabView = new FragmentTabView();
        tabView.setListView(mNormalTabs);
        FragmentIncognito incognito = new FragmentIncognito();
        incognito.setListView(mIncognitoTabs);
        mViewPagerAdapter.addFragment(tabView, "Tabs");
        mViewPagerAdapter.addFragment(incognito, "Incognito");
        viewPager.setAdapter(mViewPagerAdapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) { super(manager); }

        @Override
        public Fragment getItem(int position) { return mFragmentList.get(position); }

        @Override
        public int getCount() { return mFragmentList.size(); }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public RecyclerView getTabList(){
        return mNormalTabs;
    }

    public RecyclerView getIncognitoList(){
        return mIncognitoTabs;
    }

    private class SearchClass {

        public class KeyListener implements OnKeyListener {

            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2) {

                switch (arg1) {
                    case KeyEvent.KEYCODE_ENTER:
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(mSearch.getWindowToken(), 0);
                        searchTheWeb(mSearch.getText().toString());
                        mSearchBox.requestFocus();
                        return true;
                    default: break;
                }
                return false;
            }
        }

        public class EditorActionListener implements OnEditorActionListener {
            @Override
            public boolean onEditorAction(TextView arg0, int actionId, KeyEvent arg2) {
                // hide the keyboard and search the web when the enter key button is pressed
                if (actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_ACTION_NEXT
                        || actionId == EditorInfo.IME_ACTION_SEND
                        || actionId == EditorInfo.IME_ACTION_SEARCH
                        || (arg2.getAction() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mSearch.getWindowToken(), 0);
                    searchTheWeb(mSearch.getText().toString());
                    mSearchBox.requestFocus();
                    return true;
                }
                return false;
            }
        }

        TextWatcher textWatcher = new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().length()==0) {
                    mIcon = mPasteIcon;
                    mSearch.setCompoundDrawables(null, null, mPasteIcon, null);
                }
                else {
                    mIcon = mCopyIcon;
                    mSearch.setCompoundDrawables(null, null, mCopyIcon, null);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void afterTextChanged(Editable s) { }
        };

        public class FocusChangeListener implements OnFocusChangeListener {
            @Override
            public void onFocusChange(View v, final boolean hasFocus) {
                if (!hasFocus && mCurrentView != null) {
                    if (mCurrentView.getProgress() < 100) {
                        setIsLoading();
                    }
                    else setIsFinishedLoading();
                    updateUrl(mCurrentView.getUrl(), false);
                }
                else if (hasFocus) {
                    mSearch.addTextChangedListener(textWatcher);
                    String url = mCurrentView.getUrl();
                    if (url == null || url.startsWith(Constants.FILE)) {
                        mSearch.setText("");
                        mIcon = mPasteIcon;
                        mSearch.setCompoundDrawables(null, null, mPasteIcon, null);
                    }
                    else {
                        mSearch.setText(url);
                        mIcon = mCopyIcon;
                        mSearch.setCompoundDrawables(null, null, mCopyIcon, null);
                    }
                    ((AutoCompleteTextView) v).selectAll(); // Hack to make sure the text gets selected
                }
                if (!hasFocus) {
                    mSearch.removeTextChangedListener(textWatcher);
                    mIcon = mRefreshIcon;
                    mSearch.setCompoundDrawables(null, null, mRefreshIcon, null);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mSearch.getWindowToken(), 0);
                }
            }
        }

        public class TouchListener implements OnTouchListener {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mSearch.getCompoundDrawables()[2] != null) {
                    boolean tappedX = event.getX() > (mSearch.getWidth() - mSearch.getPaddingRight() - mIcon.getIntrinsicWidth());
                    if (tappedX) {
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            if (mSearch.hasFocus()) {
                                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                if (mIcon == mPasteIcon) {
                                    if (clipboard.hasPrimaryClip()) {
                                        if (clipboard.getPrimaryClipDescription().hasMimeType("text/plain")) {
                                            ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
                                            mSearch.append(item.getText().toString());
                                        }
                                    }
                                }
                                else if (mIcon == mCopyIcon) {
                                    ClipData clip = ClipData.newPlainText("label", mSearch.getText().toString());
                                    clipboard.setPrimaryClip(clip);
                                    Utils.showToast(mActivity, mActivity.getResources().getString(R.string.message_text_copied));
                                }
                            }
                            else refreshOrStop();
                        }
                        return true;
                    }
                }
                return false;
            }

        }
    }

    private class DrawerLocker implements DrawerListener {

        @Override
        public void onDrawerClosed(View v) {
            if (v == mLeftDrawer) {
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, mRightDrawer);
            }
            else mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, mLeftDrawer);
        }

        @Override
        public void onDrawerOpened(View v) {
            if (v == mLeftDrawer) {
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, mRightDrawer);
            }
            else mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, mLeftDrawer);
        }

        @Override
        public void onDrawerSlide(View v, float arg) {}

        @Override
        public void onDrawerStateChanged(int arg) {}

    }

    private boolean isTablet() {
        return (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    private void setNavigationDrawerWidth() {
        int width = getResources().getDisplayMetrics().widthPixels - mToolbarHeight;
        int maxWidth;
        if (isTablet()) maxWidth = Utils.convertDpToPixels(320);
        else maxWidth = Utils.convertDpToPixels(300);
        if (width > maxWidth) {
            DrawerLayout.LayoutParams params = (android.support.v4.widget.DrawerLayout.LayoutParams) mRightDrawer
                    .getLayoutParams();
            params.width = maxWidth;
            mRightDrawer.setLayoutParams(params);
            mRightDrawer.requestLayout();
            DrawerLayout.LayoutParams paramsRight = (android.support.v4.widget.DrawerLayout.LayoutParams) mLeftDrawer.getLayoutParams();
            paramsRight.width = maxWidth;
            mLeftDrawer.setLayoutParams(paramsRight);
            mLeftDrawer.requestLayout();
        }
        else {
            DrawerLayout.LayoutParams params = (android.support.v4.widget.DrawerLayout.LayoutParams) mRightDrawer.getLayoutParams();
            params.width = width;
            mRightDrawer.setLayoutParams(params);
            mRightDrawer.requestLayout();
            DrawerLayout.LayoutParams paramsRight = (android.support.v4.widget.DrawerLayout.LayoutParams) mLeftDrawer.getLayoutParams();
            paramsRight.width = width;
            mLeftDrawer.setLayoutParams(paramsRight);
            mLeftDrawer.requestLayout();
        }
    }

    private Paint strokePaint;
    private Paint textPaint;
    private int xPos;
    private int yPos;
    private RectF rect;

    private void initializeTabCounter(){
        Bitmap bm = Bitmap.createBitmap(mToolBarSize, mToolBarSize, Bitmap.Config.ARGB_8888);
        strokePaint = new Paint();

        strokePaint.setTextAlign(Paint.Align.CENTER);
        strokePaint.setStyle(Paint.Style.STROKE);
        float x = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
        strokePaint.setStrokeWidth(x);

        textPaint = new Paint();

        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setFakeBoldText(true);

        textPaint.setTextSize(mToolBarSize /4);

        Canvas canvas = new Canvas(bm);

        xPos = ((canvas.getWidth() / 2)- (int) ((2*x)+(x/2)));
        yPos = (int) ((canvas.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2)+(int)(x+(x/3)));

        rect = new RectF( (canvas.getHeight()/5),(canvas.getHeight()/3),(canvas.getHeight()*3/5),(canvas.getHeight()*3/4));
    }

    private void writeTabCount(int backgroundTab) {

        Bitmap bm = Bitmap.createBitmap(mToolBarSize, mToolBarSize, Bitmap.Config.ARGB_8888);
        String text;
        if (isIncognitoTab()) {
            text = mIncognitoWebViews.size() + "";
            strokePaint.setColor(0xffffffff);
            textPaint.setColor(0xffffffff);
        }
        else {
            text = mWebViews.size() + "";
            strokePaint.setColor(mNumberColor);
            textPaint.setColor(mNumberColor);
        }
        if (mDarkTheme) {
            strokePaint.setColor(0xffffffff);
            textPaint.setColor(0xffffffff);
        }
        if (backgroundTab != 0) {
            strokePaint.setColor(backgroundTab);
            textPaint.setColor(backgroundTab);
        }
        Canvas canvas = new Canvas(bm);
        canvas.drawRoundRect(rect, 3, 3, strokePaint);
        canvas.drawText(text, xPos, yPos, textPaint);
        mTabCounter.setImageDrawable(new BitmapDrawable(getResources(), bm));
    }

    public boolean IsIncognitoMode(){
        int index = mViewPager.getCurrentItem();
        return index == 1;
    }

    public synchronized void initializeTabs() {

    }

    public void restoreOrNewTab() {
        mIdGenerator = 0;
        String url = null;
        if (getIntent() != null) {
            url = getIntent().getDataString();
            if (url != null) {
                if (url.startsWith(Constants.FILE)) {
                    Utils.showToast(this, getResources().getString(R.string.message_blocked_local));
                    url = null;
                }
            }
        }
        if (mPreferences.getRestoreLostTabsEnabled()) {
            String mem = mPreferences.getMemoryUrl();
            mPreferences.setMemoryUrl("");
            String[] array = Utils.getArray(mem);
            int count = 0;
            for (String urlString : array) {
                if (urlString.length() > 0) {
                    if (url != null && url.compareTo(urlString) == 0) {
                        url = null;
                    }
                    newTab(urlString, true, false, true, false);
                    count++;
                }
            }
            if (url != null) newTab(url, true, false, true, false);
            else if (count == 0) newTab(null, true, false, true, false);
        }
        else newTab(url, true, false, true, false);
    }

    public void initializePreferences() {
        if (mPreferences == null) {
            mPreferences = PreferenceManager.getInstance();
        }
        mFullScreen = mPreferences.getFullScreenEnabled();
        showToolbar();
        if (mFullScreen){
            DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) mWebViewFrame.getLayoutParams();
            params.topMargin = 0;
            mWebViewFrame.setLayoutParams(params);
        }
        else {
            setDrawerMargin(mToolbarHeight);
            DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) mWebViewFrame.getLayoutParams();
            params.topMargin = mToolbarHeight;
            mWebViewFrame.setLayoutParams(params);
        }
        mExitOnTabClose = mPreferences.getExitOnTabClose();
        if (mPreferences.getStackTabsTop()){
            mIncognitoManager.setStackFromEnd(false);
            mTabManager.setStackFromEnd(false);
            mIncognitoTabs.setLayoutManager(mIncognitoManager);
            mNormalTabs.setLayoutManager(mTabManager);
        }
        else {
            mIncognitoManager.setStackFromEnd(true);
            mTabManager.setStackFromEnd(true);
            mIncognitoTabs.setLayoutManager(mIncognitoManager);
            mNormalTabs.setLayoutManager(mTabManager);
        }


        if (mPreferences.getHideStatusBarEnabled()) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        switch (mPreferences.getSearchChoice()) {
            case 0:
                mSearchText = mPreferences.getSearchUrl();
                if (!mSearchText.startsWith(Constants.HTTP)
                        && !mSearchText.startsWith(Constants.HTTPS)) {
                    mSearchText = Constants.GOOGLE_SEARCH;
                }
                break;
            case 1: { mSearchText = Constants.GOOGLE_SEARCH; break; }
            case 2: { mSearchText = Constants.ASK_SEARCH; break; }
            case 3: { mSearchText = Constants.BING_SEARCH; break; }
            case 4: { mSearchText = Constants.YAHOO_SEARCH; break; }
            case 5: { mSearchText = Constants.STARTPAGE_SEARCH; break; }
            case 6: { mSearchText = Constants.STARTPAGE_MOBILE_SEARCH; break; }
            case 7: { mSearchText = Constants.DUCK_SEARCH; break; }
            case 8: { mSearchText = Constants.DUCK_LITE_SEARCH; break; }
            case 9: { mSearchText = Constants.BAIDU_SEARCH; break; }
            case 10: { mSearchText = Constants.YANDEX_SEARCH; break; }
        }
        updateCookiePreference();
    }

    /*
     * Override this if class overrides BrowserActivity
     */
    public void updateCookiePreference() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            if (mSearch.hasFocus()) {
                searchTheWeb(mSearch.getText().toString());
            }
        } else if ((keyCode == KeyEvent.KEYCODE_MENU) && (Build.VERSION.SDK_INT <= 16)
                && (Build.MANUFACTURER.compareTo("LGE") == 0)) {
            // Workaround for stupid LG devices that crash
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_MENU) && (Build.VERSION.SDK_INT <= 16)
                && (Build.MANUFACTURER.compareTo("LGE") == 0)) {
            // Workaround for stupid LG devices that crash
            openOptionsMenu();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        MenuItem item = menu.findItem(R.id.action_desktop_mode);
        item.setChecked(mCurrentView.isDesktopMode());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action buttons
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mDrawerLayout.isDrawerOpen(mLeftDrawer)) {
                    mDrawerLayout.closeDrawer(mLeftDrawer);
                }
                return true;
            case R.id.action_back:
                if (mCurrentView != null) {
                    if (mCurrentView.canGoBack()) {
                        mCurrentView.goBack();
                    }
                }
                return true;
            case R.id.action_forward:
                if (mCurrentView != null) {
                    if (mCurrentView.canGoForward()) {
                        mCurrentView.goForward();
                    }
                }
                return true;
            case R.id.action_new_tab:
                newTab(null, true, false, true, false);
                return true;
            case R.id.incognito_tab:
                newTab(null, true, false, false, true);
                mViewPager.setCurrentItem(1);
                return true;
            case R.id.action_share:
                if (!mCurrentView.getUrl().startsWith(Constants.FILE)) {
                    Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                            mCurrentView.getTitle());
                    String shareMessage = mCurrentView.getUrl();
                    shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent,
                            getResources().getString(R.string.dialog_title_share)));
                }
                return true;
            case R.id.action_bookmarks:
                openBookmarks();
                return true;

            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.action_history:
                openHistory();
                return true;
            case R.id.action_add_bookmark:
                if (!mCurrentView.getUrl().startsWith(Constants.FILE)) {
                    mBookmarkManager.BookmarkPage(mCurrentView.getTitle(), mCurrentView.getUrl());
                }
                return true;
            case R.id.action_desktop_mode:
                if (item.isChecked()) {
                    item.setChecked(false);
                    mCurrentView.setUserAgent(1);
                }
                else {
                    item.setChecked(true);
                    mCurrentView.setUserAgent(2);
                }
                mCurrentView.reload();
                return true;
            case R.id.action_rendering_mode:
                PopupMenu popupMenu = new PopupMenu(this, mOverflowIcon);
                popupMenu.inflate(R.menu.rendering);
                popupMenu.show();
                return true;
            case R.id.action_find:
                mMainLayout.removeView(mSearchBar);
                mMainLayout.addView(mSearchBar);
                return true;
            case R.id.action_reading_mode:
                Intent read = new Intent(this, ReadingActivity.class);
                read.putExtra(Constants.LOAD_READING_URL, mCurrentView.getUrl());
                startActivity(read);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initFindInPage() {
        mSearchBar = (RelativeLayout) getLayoutInflater().inflate(R.layout.find_in_page, null);
        mMainLayout.addView(mSearchBar);

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mSearchBar.getLayoutParams();
        params.height = mToolbarHeight;
        mSearchBar.setLayoutParams(params);

        mFindEditText = (EditText) findViewById(R.id.search_query);
        mFindEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    mCurrentView.find(mFindText = mFindEditText.getText().toString());
                    return true;
                }
                return false;
            }
        });

        ImageButton up = (ImageButton) findViewById(R.id.button_next);
        up.setOnClickListener(this);

        ImageButton down = (ImageButton) findViewById(R.id.button_back);
        down.setOnClickListener(this);

        ImageButton quit = (ImageButton) findViewById(R.id.button_quit);
        quit.setOnClickListener(this);

        mMainLayout.removeView(mSearchBar);
    }

    private void showCloseDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mActivity,
                android.R.layout.simple_dropdown_item_1line);
        adapter.add(mActivity.getString(R.string.close_tab));
        adapter.add(mActivity.getString(R.string.close_all_tabs));
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        deleteTab(position);
                        break;
                    case 1:
                        closeBrowser();
                        break;
                    default:
                        break;
                }
            }
        });
        builder.show();
    }

    /**
     * The click listener for ListView in the navigation drawer
     */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mIsNewIntent = false;
            showTab(mWebViews.get(position), true);
        }
    }

    private class TabDrawerItemClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            if (IsIncognitoMode()){
                int position = mIncognitoTabs.getChildAdapterPosition(v);
                if (mCurrentView != mIncognitoWebViews.get(position)) {
                    mIsNewIntent = false;
                    showTab(mIncognitoWebViews.get(position), true);
                }
            }
            else {
                int position = mNormalTabs.getChildAdapterPosition(v);
                if (mCurrentView != mWebViews.get(position)) {
                    mIsNewIntent = false;
                    showTab(mWebViews.get(position), true);
                }
            }
        }
    }

    private class TabDrawerItemLongClickListener implements OnLongClickListener {

        @Override
        public boolean onLongClick(View v) {
            int position = mNormalTabs.getChildAdapterPosition(v);
            showCloseDialog(position);
            return true;
        }
    }

    private synchronized void showTabAnimated(XWebView view, Direction direction) {

        if (view == null) return;
        if (mCurrentView != null) {
            mCurrentView.setForegroundTab(false);
            mCurrentView.onPause();
            mPreviousView = mCurrentView.getWebView();
        }
        mCurrentView = view;
        mCurrentView.setForegroundTab(true);
        writeTabCount(0);

        WebView webView = mCurrentView.getWebView();
        webView.setAlpha(0.0f);
        mWebViewFrame.addView(webView, MATCH_PARENT); // Remove browser frame background to reduce overdraw
        if (direction == Direction.up){
            webView.setTranslationY(-mWebViewFrame.getHeight());
            webView.setAlpha(1.0f);
            webView.animate().translationY(0).setDuration(200).setListener(mAnimEndListener).start();
        }
        else if (direction == Direction.down) {
            webView.setTranslationY(mWebViewFrame.getHeight());
            webView.setAlpha(1.0f);
            webView.animate().translationY(0).setDuration(200).setListener(mAnimEndListener).start();
        }
        else if (direction == Direction.left){
            webView.setTranslationX(mWebViewFrame.getWidth());
            webView.setAlpha(1.0f);
            webView.animate().translationX(0).setDuration(200).setListener(mAnimEndListener).start();
        }
        else if (direction == Direction.right){
            webView.setTranslationX(-mWebViewFrame.getWidth());
            webView.setAlpha(1.0f);
            webView.animate().translationX(0).setDuration(200).setListener(mAnimEndListener).start();
        }

        if (mCurrentView.getWebView() != null) {
            updateUrl(mCurrentView.getUrl(), false);
            updateProgress(mCurrentView.getProgress());
        }
        else {
            updateUrl("", false);
            updateProgress(0);
        }
        if (mCurrentView.isIncognitoTab()) {
            incognitoToolbar();
        }
        else normalToolbar();

        mCurrentView.requestFocus();
        mCurrentView.onResume();

    }

    private synchronized void showTab(XWebView view, boolean closeDrawer) {

        if (view == null) return;
        if (mCurrentView != null) {
            mCurrentView.setForegroundTab(false);
            mCurrentView.onPause();
        }
        mCurrentView = view;
        mCurrentView.setForegroundTab(true);
        writeTabCount(0);

        mWebViewFrame.removeAllViews();
        mWebViewFrame.addView(mCurrentView.getWebView(), MATCH_PARENT);

        if (mCurrentView.getWebView() != null) {
            updateUrl(mCurrentView.getUrl(), false);
            updateProgress(mCurrentView.getProgress());
        }
        else {
            updateUrl("", false);
            updateProgress(0);
        }
        if (mCurrentView.isIncognitoTab()) {
            incognitoToolbar();
        }
        else normalToolbar();

        mCurrentView.requestFocus();
        mCurrentView.onResume();

        if (closeDrawer){
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mDrawerLayout.closeDrawers();
                }
            }, 150);
        }
    }

    private synchronized void displayRemoveIncognitoTab(List<XWebView> list, int position, int oldpos) {
        if (list == null) return;

        mCurrentView = list.get(position);
        mCurrentView.setAsForegroundTab(true, position);
        mIncognitoAdapter.notifyItemChanged(position);
        mIncognitoWebViews.remove(oldpos);
        mIncognitoAdapter.notifyItemRemoved(oldpos);
        updateTabIndexes(mIncognitoWebViews);

        mWebViewFrame.removeAllViews();
        mWebViewFrame.addView(mCurrentView.getWebView(), MATCH_PARENT);

        writeTabCount(0);

        if (mCurrentView.getWebView() != null) {
            updateUrl(mCurrentView.getUrl(), false);
            updateProgress(mCurrentView.getProgress());
        }
        else {
            updateUrl("", false);
            updateProgress(0);
        }
        incognitoToolbar();

        mCurrentView.requestFocus();
        mCurrentView.onResume();
    }

    private synchronized void displayRemoveTab(List<XWebView> list, int position, int oldpos) {
        if (list == null) return;

        mCurrentView = list.get(position);
        mCurrentView.setAsForegroundTab(true, position);
        mTabsAdapter.notifyItemChanged(position);
        mWebViews.remove(oldpos);
        mTabsAdapter.notifyItemRemoved(oldpos);
        updateTabIndexes(mWebViews);

        mWebViewFrame.removeAllViews();
        mWebViewFrame.addView(mCurrentView.getWebView(), MATCH_PARENT);

        writeTabCount(0);

        if (mCurrentView.getWebView() != null) {
            updateUrl(mCurrentView.getUrl(), false);
            updateProgress(mCurrentView.getProgress());
        }
        else {
            updateUrl("", false);
            updateProgress(0);
        }
        normalToolbar();

        mCurrentView.requestFocus();
        mCurrentView.onResume();
    }

    /**
     * creates a new tab with the passed in URL if it isn't null
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    public void handleNewIntent(Intent intent) {
        String url = null;
        if (intent != null) {
            url = intent.getDataString();
        }
        int num = 0;
        if (intent != null && intent.getExtras() != null) {
            num = intent.getExtras().getInt(getPackageName() + ".Origin");
        }
        if (num == 1) mCurrentView.loadUrl(url);
        else if (url != null) {
            if (url.startsWith(Constants.FILE)) {
                Utils.showToast(this, getResources().getString(R.string.message_blocked_local));
                url = null;
            }
            newTab(url, true, false, true, false);
            mIsNewIntent = true;
        }
    }

    private void closeCurrentTab() {
        // don't delete the tab because the browser will close and mess stuff up
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onTrimMemory(int level) {
        if (level > TRIM_MEMORY_MODERATE && Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            Log.d(Constants.TAG, "Low Memory, Free Memory");
            for (XWebView view : mWebViews) {
                view.getWebView().freeMemory();
            }
        }
    }

    public synchronized void newTab(String url) {
        if (mCurrentView.isIncognitoTab()) newTab(url, true, true, false, true);
        else newTab(url, true, true, true, false);
    }

    protected synchronized boolean newTab(String url, boolean show, boolean parent, boolean normal, boolean incognito) {
        if (normal) incognito = false;

        mIsNewIntent = false;
        XWebView startingTab = new XWebView(mActivity, url, mDarkTheme, incognito);
        startingTab.setToolbar(mToolbarLayout);
        if (mIdGenerator == 0) {
            startingTab.resumeTimers();
        }
        mIdGenerator++;
        int index = 0;
        if (incognito){
            if (mCurrentView.isIncognitoTab() && parent) {
                mIncognitoWebViews.add(index = mIncognitoWebViews.indexOf(mCurrentView) + 1, startingTab);
                updateTabIndexes(mIncognitoWebViews, index - 1);
            }
            else {
                mIncognitoWebViews.add(startingTab);
                index = mIncognitoWebViews.size() - 1;
                updateTabIndexes(mIncognitoWebViews, index);
                if (mIncognitoWebViews.size() > 7) mIncognitoManager.scrollToPosition(mIncognitoWebViews.size() - 1);
            }
            mIncognitoAdapter.notifyItemInserted(index);
        }
        else {
            if (parent) {
                mWebViews.add(index = mWebViews.indexOf(mCurrentView) + 1, startingTab);
                updateTabIndexes(mWebViews, index - 1);
            }
            else {
                mWebViews.add(startingTab);
                index = mWebViews.size() - 1;
                updateTabIndexes(mWebViews, index);
                if (mWebViews.size() > 7) mTabManager.scrollToPosition(mWebViews.size() - 1);
            }
            mTabsAdapter.notifyItemInserted(index);
        }
        if (show) showTab(startingTab, true);
        else writeTabCount(mHighlight);
        return true;
    }

    private synchronized void deleteTab(int position) {

        boolean isIncognito = IsIncognitoMode();
        if (isIncognito && position >= mIncognitoWebViews.size()) return;
        if (!isIncognito && position >= mWebViews.size()) return;

        if (mCurrentView.isIncognitoTab()) {
            if (isIncognito) deleteIncognitoTab(position);
            else deleteFragmentTab(position, false);
            return;
        }
        else if (isIncognito){
            deleteFragmentTab(position, true);
            return;
        }

        int current = mWebViews.indexOf(mCurrentView);

        XWebView reference = mWebViews.get(position);
        if (reference == null) return;

        if (reference.getUrl() != null && !reference.getUrl().startsWith(Constants.FILE) && !isIncognitoTab()) {
            mPreferences.setSavedUrl(reference.getUrl());
        }
        boolean isShown = reference.isShown();
        if (isShown) {
            mWebViewFrame.setBackgroundColor(mBackgroundColor);
        }
        if (position < current) { //if before current tab
            mWebViews.remove(position);
            mTabsAdapter.notifyItemRemoved(position);
            updateTabIndexes(mWebViews, position);
            reference.onDestroy();
        }
        else if (position + 1 < mWebViews.size()) { //if after or current
            if (position == 0){
                displayRemoveTab(mWebViews, 1, position);
            }
            else if (current == position) {
                displayRemoveTab(mWebViews, position - 1, position);
            }
            else {
                mWebViews.remove(position);
                mTabsAdapter.notifyItemRemoved(position);
                updateTabIndexes(mWebViews, position);
            }
            reference.onDestroy();
        }
        else if (mWebViews.size() > 1) {
            if (current == position) {
                displayRemoveTab(mWebViews, position - 1, position);
            }
            else {
                mWebViews.remove(position);
                mTabsAdapter.notifyItemRemoved(position);
                updateTabIndexes(mWebViews);
            }
            reference.onDestroy();
        }
        else {
            if (mExitOnTabClose && (mCurrentView.getUrl() == null
                    || mCurrentView.getUrl().startsWith(Constants.FILE)
                    || mCurrentView.getUrl().equals(mHomepage))) {
                mIncognitoWebViews.clear();
                finish();
            }
            else {
                mWebViews.remove(position);
                if (mPreferences.getClearCacheExit() && mCurrentView != null && !isIncognitoTab()) {
                    mCurrentView.clearCache(true);
                    Log.d(Constants.TAG, "Cache Cleared");
                }
                if (mPreferences.getClearHistoryExitEnabled() && !isIncognitoTab()) {
                    clearHistory();
                    Log.d(Constants.TAG, "History Cleared");

                }
                if (mPreferences.getClearCookiesExitEnabled() && !isIncognitoTab()) {
                    clearCookies();
                    Log.d(Constants.TAG, "Cookies Cleared");
                }
                reference.pauseTimers();
                reference.onDestroy();
                if (!mExitOnTabClose) {
                    newTab(null, true, false, true, false);
                    return;
                }
                mCurrentView = null;
                finish();
                return;
            }
        }
        writeTabCount(0);

        if (mIsNewIntent && isShown) {
            mIsNewIntent = false;
            closeActivity();
        }
        Log.d(Constants.TAG, "deleted tab");
    }

    private synchronized void deleteIncognitoTab(int position){

        int current = mIncognitoWebViews.indexOf(mCurrentView);
        XWebView reference = mIncognitoWebViews.get(position);
        if (reference == null) return;
        boolean isShown = reference.isShown();
        if (isShown) mWebViewFrame.setBackgroundColor(mBackgroundColor);
        if (current > position) {
            mIncognitoWebViews.remove(position);
            mIncognitoAdapter.notifyItemRemoved(position);
            updateTabIndexes(mIncognitoWebViews, position);
            reference.onDestroy();
        }
        else if (position + 1 < mIncognitoWebViews.size()) { //if after current tab
            if (position == 0){
                displayRemoveIncognitoTab(mIncognitoWebViews, 1, position);//showTab(mIncognitoWebViews.get(1), false);
            }
            else if (current == position) {
                displayRemoveIncognitoTab(mIncognitoWebViews, position - 1, position);//showTab(mIncognitoWebViews.get(position - 1), false);
            }
            else {
                mIncognitoWebViews.remove(position);
                mIncognitoAdapter.notifyItemRemoved(position);
            }
            reference.onDestroy();
        }
        else if (mIncognitoWebViews.size() > 1) {
            if (current == position) {
                displayRemoveIncognitoTab(mIncognitoWebViews, position - 1, position);//showTab(mIncognitoWebViews.get(position - 1), false);
            }
            else {
                mIncognitoWebViews.remove(position);
                mIncognitoAdapter.notifyItemRemoved(position);
            }
            reference.onDestroy();
        }
        else { //size = 1 last tab
            mIncognitoWebViews.remove(position);
            mIncognitoAdapter.notifyItemRemoved(position);
            if (mWebViews.size() > 0) showTab(mWebViews.get(mWebViews.size() - 1), true);
            else newTab(null, true, false, true, false);
            mViewPager.setCurrentItem(0);
            reference.onDestroy();
        }
        writeTabCount(0);
        if (mIsNewIntent && isShown) {
            mIsNewIntent = false;
            closeActivity();
        }
        Log.d(Constants.TAG, "deleted tab");
    }

    private synchronized void deleteFragmentTab(int position, boolean deleteIncognito){
        if (deleteIncognito){ //delete tabs in incognito fragment from normal tabs fragment
            mIncognitoWebViews.remove(position);
            mIncognitoAdapter.notifyItemRemoved(position);
            updateTabIndexes(mIncognitoWebViews);
        }
        else {
            mWebViews.remove(position);
            mTabsAdapter.notifyItemRemoved(position);
            updateTabIndexes(mWebViews);
        }
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showCloseDialog(CurrentIndex());
        }
        return true;
    }

    private void closeBrowser() {
        mWebViewFrame.setBackgroundColor(mBackgroundColor);
        if (mPreferences.getClearCacheExit() && mCurrentView != null && !isIncognitoTab()) {
            mCurrentView.clearCache(true);
            Log.d(Constants.TAG, "Cache Cleared");
        }
        if (mPreferences.getClearHistoryExitEnabled() && !isIncognitoTab()) {
            clearHistory();
            Log.d(Constants.TAG, "History Cleared");
        }
        if (mPreferences.getClearCookiesExitEnabled() && !isIncognitoTab()) {
            clearCookies();
            Log.d(Constants.TAG, "Cookies Cleared");
        }
        mCurrentView = null;
        for (int n = 0; n < mWebViews.size(); n++) {
            if (mWebViews.get(n) != null) mWebViews.get(n).onDestroy();
        }
        mWebViews.clear();
        finish();
    }

    @SuppressWarnings("deprecation")
    public void clearHistory() {
        //this.deleteDatabase(HistoryDatabase.DatabaseName);
        mHistoryDatabase.dropTables();
        WebViewDatabase m = WebViewDatabase.getInstance(this);
        m.clearFormData();
        m.clearHttpAuthUsernamePassword();
        if (API < 18) {
            m.clearUsernamePassword();
            WebIconDatabase.getInstance().removeAllIcons();
        }
        if (mSystemBrowser) {
            try {
                //Browser.clearHistory(getContentResolver());
            }
            catch (NullPointerException ignored) { }
        }
        Utils.trimCache(this);
    }

    private void clearCache(){
        for (XWebView view : mWebViews){
            view.getWebView().clearCache(true);
        }
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public void clearCookies() {
        //TODO Break out web storage deletion into its own option/action
        //TODO clear web storage for all sites that are visited in Incognito mode
        WebStorage storage = WebStorage.getInstance();
        storage.deleteAllData();
        CookieManager c = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            c.removeAllCookies(null);
        }
        else {
            CookieSyncManager.createInstance(this);
            c.removeAllCookie();
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(mRightDrawer)) {
            mDrawerLayout.closeDrawer(mRightDrawer);
        }
        else if (mDrawerLayout.isDrawerOpen(mLeftDrawer)) {
            mDrawerLayout.closeDrawer(mLeftDrawer);
        }
        else {
            if (mCurrentView != null) {
                Log.d(Constants.TAG, "onBackPressed");
                if (mSearch.hasFocus()) {
                    mCurrentView.requestFocus();
                }
                else if (mCurrentView.canGoBack()) {
                    if (!mCurrentView.isShown()) {
                        onHideCustomView();
                    }
                    else mCurrentView.goBack();
                }
                else deleteTab(CurrentIndex());
            }
            else {
                Log.e(Constants.TAG, "This shouldn't happen ever");
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(Constants.TAG, "onPause");
        if (mCurrentView != null) {
            mCurrentView.pauseTimers();
            mCurrentView.onPause();
        }
    }

    @Override
    protected void onStop(){
        if (mBookmarkManager.mBookmarksChanged) {
            new Thread(new Runnable() {
                public void run() {
                    mBookmarkManager.saveBookmarks(null);
                }
            }).start();
        }
        super.onStop();
    }

    public void saveOpenTabs() {
        if (mPreferences.getRestoreLostTabsEnabled()) {
            String s = "";
            for (int n = 0; n < mWebViews.size(); n++) {
                if (mWebViews.get(n).getUrl() != null) {
                    s = s + mWebViews.get(n).getUrl() + "|$|SEPARATOR|$|";
                }
            }
            mPreferences.setMemoryUrl(s);
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(Constants.TAG, "onDestroy");
        if (mHistoryDatabase != null) {
            mHistoryDatabase.close();
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(Constants.TAG, "onResume");
        if (mSearchAdapter != null) {
            mSearchAdapter.refreshPreferences();
            //mSearchAdapter.refreshBookmarks();
        }
        if (mCurrentView != null) {
            mCurrentView.resumeTimers();
            mCurrentView.onResume();

            mHistoryDatabase = HistoryDatabase.getInstance(getApplicationContext());
            if (mHistoryPage == null) mHistoryPage = mHistoryManager.getHistoryPage();
            mHistoryDatabase.updateDate();
        }
        initializePreferences();
        if (mWebViews != null) {
            for (int n = 0; n < mWebViews.size(); n++) {
                if (mWebViews.get(n) != null) {
                    mWebViews.get(n).initializePreferences(this);
                }
                else mWebViews.remove(n);
            }
        }
        if (mIncognitoWebViews != null) {
            for (int n = 0; n < mIncognitoWebViews.size(); n++) {
                if (mIncognitoWebViews.get(n) != null) {
                    mIncognitoWebViews.get(n).initializePreferences(this);
                }
                else mIncognitoWebViews.remove(n);
            }
        }
        supportInvalidateOptionsMenu();
    }

    void searchTheWeb(String query) {
        if (query.equals("")) return;
        String SEARCH = mSearchText;
        query = query.trim();
        mCurrentView.stopLoading();

        if (query.startsWith("www.")) query = Constants.HTTP + query;
        else if (query.startsWith("ftp.")) query = "ftp://" + query;

        boolean containsPeriod = query.contains(".");
        boolean isIPAddress = (TextUtils.isDigitsOnly(query.replace(".", ""))
                && (query.replace(".", "").length() >= 4) && query.contains("."));
        boolean aboutScheme = query.contains("about:");
        boolean validURL = (query.startsWith("ftp://") || query.startsWith(Constants.HTTP)
                || query.startsWith(Constants.FILE) || query.startsWith(Constants.HTTPS))
                || isIPAddress;
        boolean isSearch = ((query.contains(" ") || !containsPeriod) && !aboutScheme);

        if (isIPAddress && (!query.startsWith(Constants.HTTP) || !query.startsWith(Constants.HTTPS))) {
            query = Constants.HTTP + query;
        }

        if (isSearch) {
            try { query = URLEncoder.encode(query, "UTF-8"); }
            catch (UnsupportedEncodingException e) { e.printStackTrace(); }
            mCurrentView.loadUrl(SEARCH + query);
        }
        else if (!validURL) mCurrentView.loadUrl(Constants.HTTP + query);
        else mCurrentView.loadUrl(query);
    }

    public class LightningViewAdapter
            extends RecyclerView.Adapter<LightningViewAdapter.LightningViewHolder>
            implements ItemTouchHelperAdapter {

        private final Context context;
        private final int layoutResourceId;
        private List<XWebView> data = null;
        private final TabDrawerItemClickListener mClickListener;

        public LightningViewAdapter(Context context, int layoutResourceId, List<XWebView> data) {
            this.layoutResourceId = layoutResourceId;
            this.context = context;
            this.data = data;
            this.mClickListener = new TabDrawerItemClickListener();
        }

        @Override
        public LightningViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            View view = inflater.inflate(layoutResourceId, viewGroup, false);
            return new LightningViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final LightningViewHolder holder, int position) {
            holder.layout.setOnClickListener(mClickListener);

            final XWebView web = data.get(position);
            holder.txtTitle.setText(web.getTitle());

            final Bitmap favicon = web.getFavicon();
            if (web.isForegroundTab()) {
                holder.txtTitle.setTextAppearance(context, R.style.boldText);
                holder.favicon.setImageBitmap(favicon);
                holder.layout.setBackgroundColor(Color.LTGRAY);
            }
            else {
                holder.layout.setBackgroundColor(0);
                holder.txtTitle.setTextAppearance(context, R.style.normalText);
            }

            holder.txtTitle.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return false;
                }
            });

            holder.exit.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemDismiss(web.getPosition());
                }
            });
        }

        public class LightningViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

            public LightningViewHolder(View view) {
                super(view);
                txtTitle = (TextView) view.findViewById(R.id.textTab);
                favicon = (ImageView) view.findViewById(R.id.faviconTab);
                exit = (ImageView) view.findViewById(R.id.deleteButton);
                layout = (RelativeLayout) view.findViewById(R.id.tabItem);
                exit.setColorFilter(R.color.gray_extra_dark, PorterDuff.Mode.SRC_IN);
            }

            final TextView txtTitle;
            final ImageView favicon;
            final ImageView exit;
            final RelativeLayout layout;

            @Override
            public void onItemSelected() {
                itemView.setBackgroundColor(Color.LTGRAY);
            }

            @Override
            public void onItemClear() {
                itemView.setBackgroundColor(0);
            }
        }

        @Override
        public int getItemCount() {
            return (data != null) ? data.size() : 0;
        }

        @Override
        public void onItemDismiss(int position) {
            deleteTab(position);
        }

        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
            Collections.swap(data, fromPosition, toPosition);
            data.get(toPosition).setPositon(toPosition);
            data.get(fromPosition).setPositon(fromPosition);
            notifyItemMoved(fromPosition, toPosition);
            return true;
        }
    }

    private void incognitoToolbar(){
        if (mDarkTheme) return;
        int dark = getResources().getColor(R.color.primary_color_dark);
        mBackground.setColor(dark);
        getWindow().setBackgroundDrawable(mBackground);
        mToolbarLayout.setBackgroundColor(Color.DKGRAY);
        mSearchBox.setBackgroundColor(Color.GRAY);
        mSearch.setTextColor(Color.WHITE);
        mOverflowIcon.setColorFilter(Color.WHITE);
    }

    private void normalToolbar(){
        if (mDarkTheme) return;
        int light = getResources().getColor(R.color.primary_color);
        mBackground.setColor(light);
        getWindow().setBackgroundDrawable(mBackground);
        mToolbarLayout.setBackgroundColor(light);
        mSearchBox.setBackgroundColor(Color.WHITE);
        mSearch.setTextColor(Color.BLACK);
        mOverflowIcon.clearColorFilter();
    }

    private void getImage(ImageView image, HistoryItem web) {
        new DownloadImageTask(image, web).execute(web.getUrl());
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        final ImageView bmImage;
        final HistoryItem mWeb;

        public DownloadImageTask(ImageView bmImage, HistoryItem web) {
            this.bmImage = bmImage;
            this.mWeb = web;
        }

        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap mIcon = null;
            // unique path for each url that is bookmarked.
            String hash = String.valueOf(Utils.getDomainName(url).hashCode());
            File image = new File(mActivity.getCacheDir(), hash + ".png");
            String urldisplay;
            try { urldisplay = Utils.getProtocol(url) + getDomainName(url) + "/favicon.ico"; }
            catch (URISyntaxException e) {
                e.printStackTrace();
                urldisplay = "https://www.google.com/s2/favicons?domain_url=" + url;
            }
            // checks to see if the image exists
            if (!image.exists()) {
                try {
                    // if not, download it...
                    URL urlDownload = new URL(urldisplay);
                    HttpURLConnection connection = (HttpURLConnection) urlDownload.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream in = connection.getInputStream();

                    if (in != null) mIcon = BitmapFactory.decodeStream(in);
                    // ...and cache it
                    if (mIcon != null) {
                        FileOutputStream fos = new FileOutputStream(image);
                        mIcon.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        fos.flush();
                        fos.close();
                        Log.d(Constants.TAG, "Downloaded: " + urldisplay);
                    }
                }
                catch (Exception e) { e.printStackTrace(); }
            }
            else mIcon = BitmapFactory.decodeFile(image.getPath());
            if (mIcon == null) {
                try { // if not, download it...
                    URL urlDownload = new URL("https://www.google.com/s2/favicons?domain_url=" + url);
                    HttpURLConnection connection = (HttpURLConnection) urlDownload.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream in = connection.getInputStream();

                    if (in != null) mIcon = BitmapFactory.decodeStream(in); // ...and cache it
                    if (mIcon != null) {
                        FileOutputStream fos = new FileOutputStream(image);
                        mIcon.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        fos.flush();
                        fos.close();
                    }
                }
                catch (Exception e) { e.printStackTrace(); }
            }
            if (mIcon == null) return mWebpageBitmap;
            else return mIcon;
        }

        protected void onPostExecute(Bitmap result) {
            Bitmap fav = Utils.padFavicon(result);
            bmImage.setImageBitmap(fav);
            mWeb.setBitmap(fav);
        }
    }

    static String getDomainName(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String domain = uri.getHost();
        if (domain == null) return url;
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }

    static String removeHttp(String url){
        return url.replace("https://", "").replace("http://", "");
    }

    @Override
    public void updateUrl(String url, boolean shortUrl) {
        if (url == null || mSearch == null || mSearch.hasFocus()) {
            return;
        }
        if (url.startsWith(Constants.FILE)) {
            if (!url.contains("history.html")) {
                url = "";
                mSearch.setText(url);
            }
        }
        else if (shortUrl) {
            switch (mPreferences.getUrlBoxContentChoice()) {
                case 0: // Default, show only the domain
                    //url = url.replaceFirst(Constants.HTTP, "");
                    //url = Utils.getDomainName(url);
                    mSearch.setText(url);
                    break;
                case 1: // URL, show the entire URL
                    mSearch.setText(url);
                    break;
                case 2: // Title, show the page's title
                    if (mCurrentView != null && !mCurrentView.getTitle().isEmpty()) {
                        mSearch.setText(mCurrentView.getTitle());
                    }
                    else mSearch.setText(mUntitledTitle);
                    break;
            }
        }
        else mSearch.setText(url);
    }

    @Override
    public void updateProgress(int n) {
        if (n >= 100) { setIsFinishedLoading(); }
        else setIsLoading();
        mProgressBar.setProgress(n);
    }

    @Override
    public void updateHistory(final String title, final String url) {

    }

    public void addItemToHistory(final String title, final String url) {
        Runnable update = new Runnable() {
            @Override
            public void run() {
                if (isSystemBrowserAvailable() && mPreferences.getSyncHistoryEnabled()) {
                    try {
                        //Browser.updateVisitedHistory(getContentResolver(), url, true);
                    }
                    catch (NullPointerException ignored) { }
                }
                try {
                    if (mHistoryDatabase == null) {
                        mHistoryDatabase = HistoryDatabase.getInstance(mActivity);
                    }
                    mHistoryDatabase.visitHistoryItem(url.replace("https://", "").replace("http://", ""), title);
                } catch (IllegalStateException e) {
                    Log.e(Constants.TAG, "IllegalStateException in updateHistory");
                } catch (NullPointerException e) {
                    Log.e(Constants.TAG, "NullPointerException in updateHistory");
                } catch (SQLiteException e) {
                    Log.e(Constants.TAG, "SQLiteException in updateHistory");
                }
            }
        };
        if (url != null && !url.startsWith(Constants.FILE)) {
            new Thread(update).start();
        }
    }

    public boolean isSystemBrowserAvailable() {
        return mSystemBrowser;
    }

    public boolean getSystemBrowser() {
        Cursor c = null;
        String[] columns = new String[] { "url", "title" };
        boolean browserFlag;
        try {
            Uri bookmarks = null;// Browser.BOOKMARKS_URI;
            c = getContentResolver().query(bookmarks, columns, null, null, null);
        }
        catch (SQLiteException | IllegalStateException | NullPointerException e) {
            e.printStackTrace();
        }
        if (c != null) {
            Log.d("Browser", "System Browser Available");
            browserFlag = true;
        }
        else {
            Log.e("Browser", "System Browser Unavailable");
            browserFlag = false;
        }
        if (c != null) c.close();
        mPreferences.setSystemBrowserPresent(browserFlag);
        return browserFlag;
    }

    private void initializeSearchSuggestions(final AutoCompleteTextView getUrl) {

        getUrl.setThreshold(1);
        getUrl.setDropDownWidth(-1);
        getUrl.setDropDownAnchor(R.id.toolbar_layout);
        getUrl.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                try {
                    String url;
                    url = ((TextView) arg1.findViewById(R.id.url)).getText().toString();
                    if (url.startsWith(mActivity.getString(R.string.suggestion))) {
                        url = ((TextView) arg1.findViewById(R.id.title)).getText().toString();
                    }
                    else getUrl.setText(url);
                    searchTheWeb(url);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getUrl.getWindowToken(), 0);
                    if (mCurrentView != null) mCurrentView.requestFocus();
                }
                catch (NullPointerException e) {
                    Log.e("Browser Error: ", "NullPointerException on item click");
                }
            }

        });

        getUrl.setSelectAllOnFocus(true);
        mSearchAdapter = new SearchAdapter(mActivity, mDarkTheme);
        getUrl.setAdapter(mSearchAdapter);
    }

    private boolean isIncognitoTab() {
        return mCurrentView.isIncognitoTab();
    }

    private void openHistory() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //mCurrentView.loadUrl(HistoryPage.getHistoryPage(mActivity));
                mHistoryPage = mHistoryManager.getHistoryPage();
                mCurrentView.loadUrl(mHistoryUrl);
            }
        }).run();
    }

    public void showHistoryPage(){
        if (!mCurrentView.isHistoryPage()) return;
        mWebViewFrame.removeView(mHistoryPage);
        mWebViewFrame.addView(mHistoryPage);
        updateProgress(100);
        mSearch.setText("Speed://History");
    }

    public void removeHistoryPage() {
        mSearch.setText("");
        mWebViewFrame.removeView(mHistoryPage);
    }

    private void openBookmarks() {
        if (mDrawerLayout.isDrawerOpen(mRightDrawer)) {
            mDrawerLayout.closeDrawers();
        }
        mDrawerLayout.openDrawer(mLeftDrawer);
    }

    private void openTabDrawer(){
        if (mDrawerLayout.isDrawerOpen(mLeftDrawer)) {
            mDrawerLayout.closeDrawers();
        }
         if (mDrawerLayout.isDrawerOpen(mRightDrawer)){
            mDrawerLayout.closeDrawers();
        }
        else mDrawerLayout.openDrawer(mRightDrawer);
    }

    public void closeDrawers() {
        mDrawerLayout.closeDrawers();
    }

    private void updateTabIndexes(List<XWebView> list, int from){
        int size = list.size();
        for (int i = from; i < size; i++){
            list.get(i).setPositon(i);
        }
    }

    private void updateTabIndexes(List<XWebView> list){
        int size = list.size();
        for (int i = 0; i < size; i++){
            list.get(i).setPositon(i);
        }
    }

    @Override
    public void updateTab(int position) {
        mTabsAdapter.notifyItemChanged(position);
    }

    @Override
    public void updateIncognitoTab(int position){
        mIncognitoAdapter.notifyItemChanged(position);
    }

    private int CurrentIndex(){
        if (mCurrentView.isIncognitoTab()) {
            return mIncognitoWebViews.indexOf(mCurrentView);
        }
        else return mWebViews.indexOf(mCurrentView);
    }

    @Override
    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
        mUploadMessage = uploadMsg;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("*/*");
        startActivityForResult(Intent.createChooser(i, getString(R.string.title_file_chooser)), 1);
    }


    /**
     * used to allow uploading into the browser
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (API < Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == 1) {
                if (null == mUploadMessage) return;
                Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;

            }
        }

        if (requestCode != 1 || mFilePathCallback == null) {
            super.onActivityResult(requestCode, resultCode, intent);
            return;
        }

        Uri[] results = null;

        // Check that the response is a good one
        if (resultCode == Activity.RESULT_OK) {
            if (intent == null) {
                // If there is not data, then we may have taken a photo
                if (mCameraPhotoPath != null) {
                    results = new Uri[] { Uri.parse(mCameraPhotoPath) };
                }
            }
            else {
                String dataString = intent.getDataString();
                if (dataString != null) {
                    results = new Uri[] { Uri.parse(dataString) };
                }
            }
        }

        mFilePathCallback.onReceiveValue(results);
        mFilePathCallback = null;
    }

    @Override
    public void showFileChooser(ValueCallback<Uri[]> filePathCallback) {
        if (mFilePathCallback != null) {
            mFilePathCallback.onReceiveValue(null);
        }
        mFilePathCallback = filePathCallback;

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = Utils.createImageFile();
                takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e(Constants.TAG, "Unable to create Image File", ex);
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            }
            else takePictureIntent = null;
        }

        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("image/*");

        Intent[] intentArray;
        if (takePictureIntent != null) {
            intentArray = new Intent[] { takePictureIntent };
        }
        else intentArray = new Intent[0];

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

        mActivity.startActivityForResult(chooserIntent, 1);
    }

    @Override
    public void onLongPress() {
        if (mClickHandler == null) {
            mClickHandler = new ClickHandler(mActivity);
        }
        Message click = mClickHandler.obtainMessage();
        if (click != null) {
            click.setTarget(mClickHandler);
            mCurrentView.getWebView().requestFocusNodeHref(click);
        }
    }

    @Override
    public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
        if (view == null) return;
        if (mCustomView != null && callback != null) {
            callback.onCustomViewHidden();
            return;
        }
        try { view.setKeepScreenOn(true); }
        catch (SecurityException e) {
            Log.e(Constants.TAG, "WebView is not allowed to keep the screen on");
        }
        mOriginalOrientation = getRequestedOrientation();
        FrameLayout decor = (FrameLayout) getWindow().getDecorView();
        mFullscreenContainer = new FullscreenHolder(this);
        mCustomView = view;
        mFullscreenContainer.addView(mCustomView, COVER_SCREEN_PARAMS);
        decor.addView(mFullscreenContainer, COVER_SCREEN_PARAMS);
        setFullscreen(true);
        mCurrentView.setVisibility(View.GONE);
        if (view instanceof FrameLayout) {
            if (((FrameLayout) view).getFocusedChild() instanceof VideoView) {
                mVideoView = (VideoView) ((FrameLayout) view).getFocusedChild();
                mVideoView.setOnErrorListener(new VideoCompletionListener());
                mVideoView.setOnCompletionListener(new VideoCompletionListener());
            }
        }
        mCustomViewCallback = callback;
    }

    @Override
    public void onHideCustomView() {
        if (mCustomView == null || mCustomViewCallback == null || mCurrentView == null) {
            return;
        }
        Log.d(Constants.TAG, "onHideCustomView");
        mCurrentView.setVisibility(View.VISIBLE);
        try { mCustomView.setKeepScreenOn(false); }
        catch (SecurityException e) {
            Log.e(Constants.TAG, "WebView is not allowed to keep the screen on");
        }
        setFullscreen(mPreferences.getHideStatusBarEnabled());
        FrameLayout decor = (FrameLayout) getWindow().getDecorView();
        if (decor != null) decor.removeView(mFullscreenContainer);
        if (API < 19) {
            try { mCustomViewCallback.onCustomViewHidden(); }
            catch (Throwable ignored) { }
        }
        mFullscreenContainer = null;
        mCustomView = null;
        if (mVideoView != null) {
            mVideoView.setOnErrorListener(null);
            mVideoView.setOnCompletionListener(null);
            mVideoView = null;
        }
        setRequestedOrientation(mOriginalOrientation);
    }

    private class VideoCompletionListener implements MediaPlayer.OnCompletionListener,
            MediaPlayer.OnErrorListener {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            return false;
        }

        @Override
        public void onCompletion(MediaPlayer mp) {
            onHideCustomView();
        }

    }

    public void setFullscreen(boolean enabled) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        if (enabled) winParams.flags |= bits;
        else {
            winParams.flags &= ~bits;
            if (mCustomView != null) {
                mCustomView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
            else mWebViewFrame.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
        win.setAttributes(winParams);
    }

    static class FullscreenHolder extends FrameLayout {

        public FullscreenHolder(Context ctx) {
            super(ctx);
            setBackgroundColor(ctx.getResources().getColor(android.R.color.black));
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouchEvent(MotionEvent evt) {
            return true;
        }

    }

    @Override
    public Bitmap getDefaultVideoPoster() {
        if (mDefaultVideoPoster == null) {
            mDefaultVideoPoster = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_media_play);
        }
        return mDefaultVideoPoster;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getVideoLoadingProgressView() {
        if (mVideoProgressView == null) {
            LayoutInflater inflater = LayoutInflater.from(this);
            mVideoProgressView = inflater.inflate(R.layout.video_loading_progress, null);
        }
        return mVideoProgressView;
    }

    @Override
    public void onCreateWindow(boolean isUserGesture, Message resultMsg) {
        if (resultMsg == null) return;

        if (mCurrentView.isIncognitoTab()) newTab("", false, true, false, true);
        else newTab("", false, true, true, false);
        WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
        int position;
        XWebView target;
        if (mCurrentView.isIncognitoTab()){
            position = mIncognitoWebViews.indexOf(mCurrentView) + 1;
            target = mIncognitoWebViews.get(position);
        }
        else {
            position = mWebViews.indexOf(mCurrentView) + 1;
            target = mWebViews.get(position);
        }
        transport.setWebView(target.getWebView());
        resultMsg.sendToTarget();
        if (target.isPageAd()) deleteTab(position);
    }

    @Override
    public Activity getActivity() {
        return mActivity;
    }

    @Override
    public XWebView getCurrentView(){
        return mCurrentView;
    }

    @Override
    public void setDrawerMargin(int margin){
        DrawerLayout.LayoutParams params = (android.support.v4.widget.DrawerLayout.LayoutParams) mRightDrawer.getLayoutParams();
        params.topMargin = margin;
        mRightDrawer.setLayoutParams(params);
        params = (android.support.v4.widget.DrawerLayout.LayoutParams) mLeftDrawer.getLayoutParams();
        params.topMargin = margin;
        mLeftDrawer.setLayoutParams(params);
    }

    public void showToolbar() {
        mToolbarLayout.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
    }

    public void hideToolbar() {
        mToolbarLayout.animate().translationY(-mToolbarHeight).setInterpolator(new DecelerateInterpolator(2)).start();
    }

    @Override
    public void longClickPage(final String url) {
        HitTestResult result = null;
        CharSequence[] options;
        final CharSequence[] link = { "Open link", "Copy link address", "Open in incognito tab", "Open in new tab" };
        final CharSequence[] incognito = { "Open link", "Copy link address", "Open in new tab", "Open in incognito tab" };
        final CharSequence[] image = { "Open image", "Download image", "Open in new tab" };
        final boolean isIncognito = mCurrentView.isIncognitoTab();
        options = isIncognito ? incognito : link;
        if (mCurrentView.getWebView() != null) {
            result = mCurrentView.getWebView().getHitTestResult();
        }
        if (url != null) {
            if (result != null) {
                if (result.getType() == HitTestResult.SRC_IMAGE_ANCHOR_TYPE || result.getType() == HitTestResult.IMAGE_TYPE) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int image) {
                            switch (image) {
                                case 0: { mCurrentView.loadUrl(url); break; }
                                case 1:
                                    if (API > 8) {
                                        Utils.downloadFile(mActivity, url, mCurrentView.getUserAgent(), "attachment", false);
                                    }
                                    break;
                                case 2: {
                                    if (isIncognito) newTab(url, false, true, false, true);
                                    else newTab(url, false, true, true, false);
                                    break;
                                }
                            }
                        }
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity); // dialog
                    builder.setTitle(url.replace(Constants.HTTP, "")).setItems(image, dialogClickListener).show();
                }
                else {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int incognito) {
                            switch (incognito) {
                                case 0: { mCurrentView.loadUrl(url); break;}
                                case 1:
                                    ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                    ClipData clip = ClipData.newPlainText("label", url);
                                    clipboard.setPrimaryClip(clip);
                                    break;
                                case 2: {
                                    if (isIncognito) newTab(url, false, true, true, false);
                                    else newTab(url, false, true, false, true);
                                    break;
                                }
                                case 3: {
                                    if (isIncognito) newTab(url, false, true, false, true);
                                    else newTab(url, false, true, true, false);
                                    break;
                                }
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity); // dialog
                    builder.setTitle(url).setItems(options, dialogClickListener).show();
                }
            }
            else {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int incognito) {
                        switch (incognito) {
                            case 0: { mCurrentView.loadUrl(url); break;}
                            case 1:
                                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("label", url);
                                clipboard.setPrimaryClip(clip);
                                break;
                            case 2: {
                                if (isIncognito) newTab(url, false, true, true, false);
                                else newTab(url, false, true, false, true);
                                break;
                            }
                            case 3: {
                                if (isIncognito) newTab(url, false, true, false, true);
                                else newTab(url, false, true, true, false);
                                break;
                            }
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity); // dialog
                builder.setTitle(url).setItems(options, dialogClickListener).show();
            }
        }
        else if (result != null) {
            if (result.getExtra() != null) {
                final String newUrl = result.getExtra();
                if (result.getType() == HitTestResult.SRC_IMAGE_ANCHOR_TYPE || result.getType() == HitTestResult.IMAGE_TYPE) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int image) {
                            switch (image) {
                                case 0: { mCurrentView.loadUrl(newUrl); break; }
                                case 1:
                                    if (API > 8) {
                                        Utils.downloadFile(mActivity, newUrl, mCurrentView.getUserAgent(), "attachment", false);
                                    }
                                    break;
                                case 2: {
                                    if (isIncognito) newTab(newUrl, false, true, false, true);
                                    else newTab(newUrl, false, true, true, false);
                                    break;
                                }
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity); // dialog
                    builder.setTitle(newUrl.replace(Constants.HTTP, "")).setItems(image, dialogClickListener).show();
                }
                else {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int incognito) {
                            switch (incognito) {
                                case 0: { mCurrentView.loadUrl(url); break;}
                                case 1:
                                    ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                    ClipData clip = ClipData.newPlainText("label", url);
                                    clipboard.setPrimaryClip(clip);
                                    break;
                                case 2: {
                                    if (isIncognito) newTab(url, false, true, true, false);
                                    else newTab(url, false, true, false, true);
                                    break;
                                }
                                case 3: {
                                    if (isIncognito) newTab(url, false, true, false, true);
                                    else newTab(url, false, true, true, false);
                                    break;
                                }
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity); // dialog
                    builder.setTitle(url).setItems(options, dialogClickListener).show();
                }
            }
        }
    }

    public void clearBrowsingData(){
        final CharSequence[] items = {" History "," Cache "," Cookies "};
        final ArrayList<Integer> seletedItems = new ArrayList<>();

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Clear browsing data")
                .setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                        if (isChecked) {
                            seletedItems.add(indexSelected);// If the user checked the item, add it to the selected items
                        } else if (seletedItems.contains(indexSelected)) {
                            seletedItems.remove(Integer.valueOf(indexSelected));// Else, if the item is already in the array, remove it
                        }
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (seletedItems.contains(0)) clearHistory();
                        if (seletedItems.contains(1)) clearCache();
                        if (seletedItems.contains(2)) clearCookies();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //  Your code when user clicked on Cancel
                    }
                }).create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public void setIsLoading() {
        if (!mSearch.hasFocus()) {
            mIcon = mDeleteIcon;
            mSearch.setCompoundDrawables(null, null, mDeleteIcon, null);
        }
    }

    public void setIsFinishedLoading() {
        if (!mSearch.hasFocus()) {
            mIcon = mRefreshIcon;
            mSearch.setCompoundDrawables(null, null, mRefreshIcon, null);
        }
    }

    public void refreshOrStop() {
        if (mCurrentView != null) {
            if (mCurrentView.getProgress() < 100) {
                mCurrentView.stopLoading();
            }
            else mCurrentView.reload();
        }
    }

    // Override this, use finish() for Incognito, moveTaskToBack for Main
    public void closeActivity() {
        finish();
    }

    public class SortIgnoreCase implements Comparator<HistoryItem> {

        public int compare(HistoryItem o1, HistoryItem o2) {
            return o1.getTitle().toLowerCase(Locale.getDefault())
                    .compareTo(o2.getTitle().toLowerCase(Locale.getDefault()));
        }

    }

    @Override
    public void moveCursor(){
        mSearch.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mSearch, InputMethodManager.SHOW_IMPLICIT);
    }

    @Override
    public int getMenu() { return R.menu.main; }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_back:
                if (mCurrentView != null) {
                    if (mCurrentView.canGoBack()) {
                        mCurrentView.goBack();
                    }
                    else deleteTab(CurrentIndex());
                }
                break;
            case R.id.action_forward:
                if (mCurrentView != null) {
                    if (mCurrentView.canGoForward()) {
                        mCurrentView.goForward();
                    }
                }
                break;
            case R.id.tabs_button:
                if (mDrawerLayout.isDrawerOpen(mLeftDrawer)) {
                    mDrawerLayout.closeDrawer(mLeftDrawer);
                    mDrawerLayout.openDrawer(mRightDrawer);
                }
                else {
                    if (mDrawerLayout.isDrawerOpen(mRightDrawer)) {
                        mDrawerLayout.closeDrawer(mRightDrawer);
                    }
                    else mDrawerLayout.openDrawer(mRightDrawer);
                }
                break;
            case R.id.new_tab_button:
                if (IsIncognitoMode()) newTab(null, true, false, false, true);
                else newTab(null, true, false, true, false);
                break;
            case R.id.button_next:
                String text;
                if (!(text = mFindEditText.getText().toString()).equals(mFindText)){
                    mCurrentView.find(mFindText = text);
                }
                else mCurrentView.findNext();
                //mCurrentView.getWebView().findNext(false);
                break;
            case R.id.button_back:
                if (!(text = mFindEditText.getText().toString()).equals(mFindText)){
                    mCurrentView.find(mFindText = text);
                }
                else mCurrentView.findPrevious();
                //mCurrentView.getWebView().findNext(true);
                break;
            case R.id.button_quit:
                mCurrentView.getWebView().clearMatches();
                mMainLayout.removeView(mSearchBar);
                break;
        }
    }

    private boolean mSearchHadFocus;

    @Override
    public void onActionModeStarted(ActionMode mode) {
        if (mSearch.hasFocus()) {
            mSearchHadFocus = true;
            mToolbarLayout.setTranslationY(mToolbarHeight);
        }
        super.onActionModeStarted(mode);
    }

    @Override
    public void onActionModeFinished(ActionMode mode) {
        if (mSearchHadFocus) {
            mToolbarLayout.setTranslationY(0);
            mSearchHadFocus = false;
        }
        super.onActionModeFinished(mode);
    }

    private enum Direction { up, down, left, right }
}