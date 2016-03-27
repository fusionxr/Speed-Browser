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
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.StateListDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.*;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebView.HitTestResult;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView.OnEditorActionListener;

import com.browser.Speed.constant.StartPage;
<<<<<<< HEAD
import com.browser.Speed.controller.BackListener;
import com.browser.Speed.controller.IBookmarkManager;
import com.browser.Speed.controller.SpeedHandler;
=======
>>>>>>> origin/master
import com.browser.Speed.database.HistoryManager;
import com.browser.Speed.helper.AnimatorListener;
import com.browser.Speed.object.ITabViewAdapter;
import com.browser.Speed.object.TabViewAdapter;
import com.browser.Speed.controller.ThreadExecutor;
import com.browser.Speed.utils.AdBlock;
import com.browser.Speed.utils.CustomViewPager;
import com.browser.Speed.utils.ExceptionHandler;
import com.browser.Speed.utils.PageSourceTask;
import com.browser.Speed.view.AnimatedProgressBar;
import com.browser.Speed.controller.IBrowserController;
import com.browser.Speed.object.ClickHandler;
import com.browser.Speed.constant.Constants;
import com.browser.Speed.database.DatabaseManager;
import com.browser.Speed.database.HistoryItem;
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
import java.security.cert.TrustAnchor;
import java.util.*;

public class BrowserActivity extends ThemableActivity implements IBrowserController, OnClickListener {

<<<<<<< HEAD
=======
    //public static BrowserActivity mInstance;


>>>>>>> origin/master
    private DrawerLayout mDrawerLayout;
    private DrawerLocker mDrawerListener;
    private FrameLayout mWebViewFrame;
    private FrameLayout mMainLayout;
    private FullscreenHolder mFullscreenContainer;
    private RecyclerView mNormalTabs, mIncognitoTabs, mNormalTabs2, mIncognitoTabs2;
    private LinearLayout mRightDrawer, mLeftDrawer, mSearchBox;
    private LinearLayoutManager mIncognitoManager, mTabManager;
    private RelativeLayout mFindBar, mToolbarLayout, mPageInfoView;
    private EditText mFindEditText;
    private TextView mPageInfoUrl;
    private String mFindText;
    private CustomViewPager mViewPager;
<<<<<<< HEAD
    private PopupWindow mOverflowMenu;
    private FrameLayout mPageInfoFrame;
    private View mPageInfoShadow;

    private int mToolBarSize = Utils.convertDpToPixels(48);
    private int mToolbarHeight = Utils.convertDpToPixels(56);
=======
    private int mToolBarSize = Utils.convertDpToPixels(48);
    private int mToolbarHeight = Utils.convertDpToPixels(56);

    private final List<XWebView> mWebViews = new ArrayList<>();
    private final List<XWebView> mIncognitoWebViews = new ArrayList<>();
    private BookmarkManager mBookmarkManager;
    private FileManager mFileManager;
>>>>>>> origin/master

    private List<XWebView> mWebViews = new ArrayList<>();
    private List<XWebView> mIncognitoWebViews = new ArrayList<>();
    private IBookmarkManager mBookmarkManager;
    private FileManager mFileManager;

    public XWebView mCurrentView;
    private WebView mPreviousView;

    private AnimatedProgressBar mProgressBar;
    private GestureDetector mSwipeListener;
    private AutoCompleteTextView mSearch;
    private ImageView mTabButton, mTopTabButton, mOverflowIcon;
    private VideoView mVideoView;
    private View mCustomView, mVideoProgressView;

    private ITabViewAdapter mTabsAdapter, mIncognitoAdapter;
    private TabViewAdapter mTabsAdapter1, mIncognitoAdapter1;
    private BottomDrawerManager mBottomDrawerManager;
    private SearchAdapter mSearchAdapter;

    private ClickHandler mClickHandler;
    private CustomViewCallback mCustomViewCallback;
    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mFilePathCallback;

    private Activity mActivity;

    private boolean mSystemBrowser = false, mIsNewIntent = false;
    private boolean mBottomLayout, mBottomDrawerOpen, mOpenLinkBackground, mExitOnTabClose, mDarkTheme, mTabButtonSwiped;
    private int mOriginalOrientation, mBackgroundColor, mIdGenerator;
    private int mNumberColor, mHighlight, mLight;
    private Drawable mWhite, mGrey, mDark;
    private String mSearchText, mUntitledTitle, mHomepage, mCameraPhotoPath;

    private DatabaseManager mDatabaseManager;
    private HistoryManager mHistoryManager;
    private RelativeLayout mHistoryPage;
    private AlertDialog mClearHistoryDialog;
    private String mHistoryUrl = "file://history.html";
    private PreferenceManager mPreferences;

    private Bitmap mDefaultVideoPoster, mWebpageBitmap;
    private final ColorDrawable mBackground = new ColorDrawable();
    private Drawable mDeleteIcon, mRefreshIcon, mCopyIcon, mPasteIcon, mIcon;
    //private TintImageView mOverflowIcon;

    private static final int API = android.os.Build.VERSION.SDK_INT;
    private static final LayoutParams MATCH_PARENT = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    private static final FrameLayout.LayoutParams COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    private synchronized void initialize() {
<<<<<<< HEAD
        if(!(Thread.getDefaultUncaughtExceptionHandler() instanceof ExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
        }
        mWebpageBitmap = Utils.getWebpageBitmap(getResources(), mDarkTheme);

        mPreferences = PreferenceManager.getInstance();
        if (mPreferences.getBottomDrawerEnabled()) {
            setContentView(R.layout.activity_main1);
            mMainLayout = (FrameLayout) findViewById(R.id.main_layout);
            mBottomDrawerManager = new BottomDrawerManager(this, mMainLayout, mWebViews, mIncognitoWebViews);
            mBookmarkManager = XBookmarkManager.getInstance();
        }
        else {
            setContentView(R.layout.activity_main);
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            mBookmarkManager = new BookmarkManager(this, mDrawerLayout);
            mFileManager = new FileManager(this, mDrawerLayout);
            initDrawerLayout();
        }

        Utils.initToastLayout(getLayoutInflater());
=======
        //mInstance = this;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        mPreferences = PreferenceManager.getInstance();
>>>>>>> origin/master

        mDarkTheme = mPreferences.getUseDarkTheme();
        mActivity = this;
        mWebViews.clear();
        mIncognitoWebViews.clear();

        mClickHandler = new ClickHandler(this);
        mMainLayout = (FrameLayout) findViewById(R.id.main_layout);
        mToolbarLayout = (RelativeLayout) findViewById(R.id.toolbar_layout);
        mWebViewFrame = (FrameLayout) findViewById(R.id.webview_frame);

        mBackground.setColor(((ColorDrawable) mToolbarLayout.getBackground()).getColor());

        mProgressBar = (AnimatedProgressBar) findViewById(R.id.progress_view);

        initFindInPage();

<<<<<<< HEAD
        mHomepage = mPreferences.getHomepage();

        try { mDatabaseManager = DatabaseManager.getInstance(getApplicationContext()); }
=======
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
        mDrawerLayout.setDrawerListener(mDrawerListener = new DrawerLocker());

        mWebpageBitmap = Utils.getWebpageBitmap(getResources(), mDarkTheme);
        mHomepage = mPreferences.getHomepage();

        mBookmarkManager = new BookmarkManager(this, mDrawerLayout);
        mFileManager = new FileManager(this, mDrawerLayout);


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
>>>>>>> origin/master
        catch (Exception e) {}

        mHistoryManager = new HistoryManager(this, getLayoutInflater());
        mHistoryUrl = HistoryManager.initHistoryPage(this);
<<<<<<< HEAD
=======

        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.toolbar_content);
>>>>>>> origin/master

        mSearchBox = (LinearLayout) findViewById(R.id.search_box);

        mTopTabButton = (ImageView) findViewById(R.id.tabs_button);//actionBar.getCustomView().findViewById(R.id.tabs_button);
        mTopTabButton.setLayerType(View.LAYER_TYPE_NONE, null);
        mTabButton = mTopTabButton;

        mSwipeListener = new GestureDetector(new OnSwipeListener());
        if (mPreferences.getSwipeTabsEnabled()) mTabButton.setOnTouchListener(new TabButtonSwipeListener());
        else mTabButton.setOnTouchListener(new TabButtonListener());

<<<<<<< HEAD
        mSearch = (AutoCompleteTextView) findViewById(R.id.search);
=======
        mSearch = (AutoCompleteTextView) actionBar.getCustomView().findViewById(R.id.search);
>>>>>>> origin/master
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

<<<<<<< HEAD
        mOverflowIcon = (ImageView)findViewById(R.id.overflow_icon);//new TintImageView(this);
//        StateListDrawable states = new StateListDrawable();
//        states.addState(new int[]{android.R.attr.state_pressed}, getResources().getDrawable(R.drawable.overflow_highlight));
//        if (mDarkTheme) states.addState(new int[] { }, getResources().getDrawable(R.drawable.overflow_white));
//        else states.addState(new int[]{}, getResources().getDrawable(R.drawable.overflow));
//        mOverflowIcon.setImageDrawable(states);

        initOverflowMenu();
        initPageInfoMenu();
        initLongClickDialogs();

=======
        mOverflowIcon = new TintImageView(this);
>>>>>>> origin/master
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

        if (API >= Build.VERSION_CODES.LOLLIPOP){
            mWhite = new ColorDrawable(Color.WHITE);
            mGrey = new ColorDrawable(Color.LTGRAY);
            mDark = new ColorDrawable(Color.GRAY);
        }
        else {
            mWhite = ContextCompat.getDrawable(this, R.drawable.card_bg);
            mDark =  ContextCompat.getDrawable(this, R.drawable.card_bg_incognito);
            mGrey =  ContextCompat.getDrawable(this, R.drawable.card_bg_dark);
        }

        mSystemBrowser = getSystemBrowser();
        Thread initialize = new Thread(new Runnable() {

            @Override
            public void run() {
                mBookmarkManager.LoadBookmarks(null);
                initializeSearchSuggestions(mSearch);
            }

        });
        initialize.run();

        initializePreferences();
        initializeTabCounter();
        initializeTabs();

        if (API <= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            WebIconDatabase.getInstance().open(getDir("icons", MODE_PRIVATE).getPath());
        }

    }

    private void initDrawerLayout(){

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setScrimColor(Color.TRANSPARENT);
        mRightDrawer = (LinearLayout) findViewById(R.id.right_drawer);
        mRightDrawer.setLayerType(View.LAYER_TYPE_NONE, null);

        mLeftDrawer = (LinearLayout) findViewById(R.id.left_drawer);
        mLeftDrawer.setLayerType(View.LAYER_TYPE_NONE, null);

        setDrawerMargin(mToolbarHeight);

        setNavigationDrawerWidth();
        mDrawerLayout.setDrawerListener(mDrawerListener = new DrawerLocker());

        ImageView back = (ImageView) findViewById(R.id.action_back);
        back.setOnClickListener(this);

        ImageView forward = (ImageView) findViewById(R.id.action_forward);
        forward.setOnClickListener(this);

        ImageView newTab = (ImageView) findViewById(R.id.new_tab_button);
        newTab.setOnClickListener(this);
        newTab.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                String url = mPreferences.getSavedUrl();
                if (url != null) {
                    newTab(url, false, -1, false);
                    Utils.showToast(mActivity, R.string.deleted_tab);
                }
                mPreferences.setSavedUrl(null);
                return true;
            }

        });

        mViewPager = (CustomViewPager) findViewById(R.id.viewpager);
        setupViewPager(mViewPager);
        TabLayout mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);

<<<<<<< HEAD
        mNormalTabs = (RecyclerView) getLayoutInflater().inflate(R.layout.tab_listview, null);
        mIncognitoTabs = (RecyclerView) getLayoutInflater().inflate(R.layout.tab_listview, null);
=======
        initializePreferences();
        initializeTabCounter();
        initializeTabs();
>>>>>>> origin/master

        mTabsAdapter1 = new TabViewAdapter(this,  mViewPager, mNormalTabs, R.layout.tab_list_item, mWebViews, false);

        mTabManager = new LinearLayoutManager(mActivity);
        mTabManager.setStackFromEnd(true);
        mNormalTabs.setLayoutManager(mTabManager);

        mIncognitoAdapter1 = new TabViewAdapter(this,  mViewPager, mIncognitoTabs, R.layout.tab_list_item, mIncognitoWebViews, true);
        mIncognitoAdapter = mIncognitoAdapter1;

<<<<<<< HEAD
        mIncognitoManager = new LinearLayoutManager(mActivity);
        mIncognitoManager.setStackFromEnd(true);
        mIncognitoTabs.setLayoutManager(mIncognitoManager);
=======
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

>>>>>>> origin/master
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
                if (hasFocus) {
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
                    setBackListener(listener);
                }
                else {
                    mSearch.removeTextChangedListener(textWatcher);
                    mIcon = mRefreshIcon;
                    mSearch.setCompoundDrawables(null, null, mRefreshIcon, null);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mSearch.getWindowToken(), 0);
                    if (mCurrentView != null) {
                        if (mCurrentView.getProgress() < 100) {
                            setIsLoading();
                        }
                        else setIsFinishedLoading();
                        updateUrl(mCurrentView.getUrl(), false);
                    }
                    setBackListener(mDefaultBackListener);
                }
            }

            BackListener listener = new BackListener() {
                @Override public void onBackPressed() {
                    mCurrentView.requestFocus();
                    mBackListener = mDefaultBackListener;
                }
            };
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

    public DrawerLocker getDrawerListener(){ return mDrawerListener; }

    private class DrawerLocker implements DrawerListener {

        @Override
        public void onDrawerClosed(View v) {
            if (v == mLeftDrawer) {
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, mRightDrawer);
            }
            else mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, mLeftDrawer);
            setBackListener(mDefaultBackListener);
        }

        @Override
        public void onDrawerOpened(View v) {
            if (v == mLeftDrawer) {
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, mRightDrawer);
                setBackListener(leftDrawer);
            }
            else {
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, mLeftDrawer);
                setBackListener(rightDrawer);
            }
        }

        BackListener leftDrawer = new BackListener() {
            @Override public void onBackPressed() {
                mDrawerLayout.closeDrawer(mLeftDrawer);
                mBackListener = mDefaultBackListener;
            }
        };

        BackListener rightDrawer = new BackListener() {
            @Override
            public void onBackPressed() {
                mDrawerLayout.closeDrawer(mRightDrawer);
                mBackListener = mDefaultBackListener;
            }
        };

        @Override public void onDrawerSlide(View v, float arg) {}
        @Override public void onDrawerStateChanged(int arg) {}

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
    private float radius;
    private RectF rect;

    private void initializeTabCounter(){
        Bitmap bm = Bitmap.createBitmap(mToolBarSize, mToolBarSize, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bm);
        strokePaint = new Paint();

        strokePaint.setTextAlign(Paint.Align.CENTER);
        strokePaint.setStyle(Paint.Style.STROKE);
        //float x = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
        float width = Utils.convertDpToPixels(1.6f);
        strokePaint.setStrokeWidth(width);
        radius = Utils.convertDpToPixels(0.6f);

        textPaint = new Paint();

        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setFakeBoldText(true);

        textPaint.setTextSize(Utils.convertDpToPixels(11));

        int paddingX = Utils.convertDpToPixels(16);
        int paddingY = Utils.convertDpToPixels(15);

        xPos = (int)Utils.convertDpToPixels(23.5f);//- (int) ((2*x)+(x/2)));
        yPos = canvas.getHeight() - (int)Utils.convertDpToPixels(20.5f);//+(int)(x+(x/3)));

        rect = new RectF(paddingX, paddingY, canvas.getWidth()-paddingX, canvas.getHeight()-paddingX);
    }

    public void setTabButton(ImageView view){
        RelativeLayout.LayoutParams sParams = (RelativeLayout.LayoutParams) mSearchBox.getLayoutParams();
        if (view == null || view == mTopTabButton) {
            mTabButton = mTopTabButton;
            mTabButton.setVisibility(View.VISIBLE);
            sParams.rightMargin = Utils.convertDpToPixels(96);
            mSearchBox.setLayoutParams(sParams);
        }
        else {
            mTopTabButton.setVisibility(View.GONE);
            mTabButton = view;
            sParams.rightMargin = Utils.convertDpToPixels(40);
            mSearchBox.setLayoutParams(sParams);
        }
        if (mCurrentView != null) writeTabCount(0);
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
        canvas.drawRoundRect(rect, radius, radius, strokePaint);
        canvas.drawText(text, xPos, yPos, textPaint);
        mTabButton.setImageDrawable(new BitmapDrawable(getResources(), bm));
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
                    newTab(urlString, false, -1, false);
                    count++;
                }
            }
            if (url != null) newTab(url, false, -1, false);
            else if (count == 0) newTab(null, false, -1, false);
        }
        else newTab(url, false, -1, false);
        mTabsAdapter.dataSetChanged();
    }

    private boolean mSettingsChanged;

    public void initializePreferences() {
        mSettingsChanged = false;
        if (mPreferences == null) {
            mPreferences = PreferenceManager.getInstance();
        }
        if (mPreferences.getRestartActivity()) {
            mPreferences.setRestartActivity(false);
            DatabaseManager.setRestarting(true);
            restart();
            return;
        }
        if (mPreferences.getSwipeTabsEnabled()) {
            mTabButton.setOnTouchListener(new TabButtonSwipeListener());
        }
        else mTabButton.setOnTouchListener(new TabButtonListener());
        boolean mFullScreen = mPreferences.getFullScreenEnabled();
        mOpenLinkBackground = mPreferences.getOpenLinksBackground();
        if (mPreferences.getBottomDrawerEnabled()){
            mBottomDrawerManager.initialize(!mSettingsChanged); //
            mBottomLayout = true;
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mWebViewFrame.getLayoutParams();
            if (mFullScreen) {
//                params.topMargin = 0;
//                mMainLayout.removeView(mWebViewFrame);
//                mWebViewFrame = (FrameLayout) getLayoutInflater().inflate(R.layout.webview_frame, null);
//                mMainLayout.addView(mWebViewFrame, 0, MATCH_PARENT);
            }
            else params.topMargin = mToolbarHeight;
            mWebViewFrame.setLayoutParams(params);
        }
        else{
            mBottomLayout = false;
            setTabButton(mTopTabButton);
            mTabsAdapter = mTabsAdapter1;
            mIncognitoAdapter = mIncognitoAdapter1;
            mTabsAdapter.notifyDataSetChanged();
            mIncognitoAdapter.notifyDataSetChanged();
            DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) mWebViewFrame.getLayoutParams();
            if (mFullScreen) params.topMargin = 0;
            else params.topMargin = mToolbarHeight;
            mWebViewFrame.setLayoutParams(params);

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
        }
        mExitOnTabClose = mPreferences.getExitOnTabClose();
<<<<<<< HEAD
=======
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
>>>>>>> origin/master

        if (mPreferences.getHideStatusBarEnabled()) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        else getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        switch (mPreferences.getSearchChoice()) {
            case 0:
                mSearchText = mPreferences.getSearchUrl();
                if (!mSearchText.startsWith(Constants.HTTP) && !mSearchText.startsWith(Constants.HTTPS)) {
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
        StartPage.initHomepage(this);
<<<<<<< HEAD
        mSettingsChanged = false;
=======
>>>>>>> origin/master
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

//    @Override
//    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        if ((keyCode == KeyEvent.KEYCODE_MENU) && (Build.VERSION.SDK_INT <= 16)
//                && (Build.MANUFACTURER.compareTo("LGE") == 0)) {
//            // Workaround for stupid LG devices that crash
//            //openOptionsMenu();
//            return true;
//        }
//        return super.onKeyUp(keyCode, event);
//    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        return false;
    }

<<<<<<< HEAD
    private void showKeyBoardDelayed(){
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(mFindEditText, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 150);
=======
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
                mSettingsChanged = true;
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
                showRenderingMenu();
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
>>>>>>> origin/master
    }

    private void initFindInPage() {
        mFindBar = (RelativeLayout) getLayoutInflater().inflate(R.layout.find_in_page, null);
        mMainLayout.addView(mFindBar);

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mFindBar.getLayoutParams();
        params.height = mToolbarHeight;
        mFindBar.setLayoutParams(params);

        mFindEditText = (EditText) findViewById(R.id.search_query);
        mFindEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_ACTION_NEXT
                        || actionId == EditorInfo.IME_ACTION_SEND
                        || actionId == EditorInfo.IME_ACTION_SEARCH) {
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

        mMainLayout.removeView(mFindBar);
    }

    private void showCloseDialog(final int position, final boolean incognito) {
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
                        deleteTab(position, incognito);
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

    Animator.AnimatorListener mAnimEndListener = new Animator.AnimatorListener() {
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

    private synchronized void showTabAnimated(List<XWebView> list, int index, Direction direction) {

        if (list.size() < 2) return;
        XWebView view;
        int current = mCurrentView.getPosition();
        if (index < 0){
            if (current == 0) view = list.get(list.size() - 1);
            else view = list.get(current - 1);
        }
        else {
            if (current == list.size() - 1) view = list.get(0);
            else view = list.get(current + 1);
        }
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
        if (direction == Direction.up) {
            webView.setTranslationY(mWebViewFrame.getHeight());
            webView.setAlpha(1.0f);
            webView.animate().translationY(0).setDuration(200).setListener(mAnimEndListener).start();
        }
        else if (direction == Direction.down){
            webView.setTranslationY(-mWebViewFrame.getHeight());
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
        updateUrl(mCurrentView.getUrl(), false);
        updateProgress(mCurrentView.getProgress());
//        else {
//            updateUrl("", false);
//            updateProgress(0);
//        }
        if (mCurrentView.isIncognitoTab()) {
            incognitoToolbar();
        }
        else normalToolbar();

        mCurrentView.requestFocus();
        mCurrentView.onResume();
    }

    public synchronized void showTab(XWebView view, boolean closeDrawer) {

        if (view == null) return;
        if (mCurrentView != null) {
            mCurrentView.setForegroundTab(false);
            mCurrentView.onPause();
        }
        mCurrentView = view;
        mCurrentView.setForegroundTab(true);

        mWebViewFrame.removeAllViews();
        mWebViewFrame.addView(mCurrentView.getWebView(), MATCH_PARENT);

        if (mCurrentView.getWebView() != null) {
            updateUrl(mCurrentView.getUrl(), false);
            updateProgress(mCurrentView.getProgress());
            //mMainLayout.setWebView(mCurrentView);
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
        writeTabCount(0);
        if (closeDrawer && !mBottomLayout){
            mHandler.postDelayed(new Runnable() {
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
        mIncognitoAdapter.itemChanged(position, 1);
        mIncognitoAdapter.remove(oldpos);

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
        mTabsAdapter.itemChanged(position, 1);
        mTabsAdapter.remove(oldpos);

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
            newTab(url, false, -1, false);
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
        newTab(url, false, mCurrentView.getPosition() + 1, mCurrentView.isIncognitoTab());
    }

    protected synchronized boolean newTab(String url, boolean background, int index, boolean incognito) {

        mIsNewIntent = false;
        XWebView startingTab = new XWebView(mActivity, url, incognito, background);

        if (mIdGenerator == 0) {
            startingTab.resumeTimers();
        }
        mIdGenerator++;
        if (incognito){
            if (mCurrentView.isIncognitoTab() && index > -1) {
                mIncognitoWebViews.add(index, startingTab);
                updateTabIndexes(mIncognitoWebViews, index);
            }
            else {
                mIncognitoWebViews.add(startingTab);
                index = mIncognitoWebViews.size() - 1;
                updateTabIndexes(mIncognitoWebViews, index);
            }
            mIncognitoAdapter.itemInserted(index);
        }
        else {
            if (index > -1) {
                mWebViews.add(index, startingTab);
                updateTabIndexes(mWebViews, index);
            }
            else {
                mWebViews.add(startingTab);
                index = mWebViews.size() - 1;
                updateTabIndexes(mWebViews, index);
            }
            mTabsAdapter.itemInserted(index);
        }
        if (!background) showTab(startingTab, true);
        else writeTabCount(mHighlight);

        return true;
    }

    public synchronized void addBackgroundTab(XWebView webView) {

        mIsNewIntent = false;
        if (mIdGenerator == 0) {
            webView.resumeTimers();
        }
        mIdGenerator++;
        int index = mCurrentView.getPosition() + 1;
        if (webView.isIncognitoTab()){

            mIncognitoWebViews.add(index, webView);
            updateTabIndexes(mIncognitoWebViews, index);
            mIncognitoAdapter.itemInserted(index);
        }
        else {
            mWebViews.add(index, webView);
            updateTabIndexes(mWebViews, index);
            mTabsAdapter.itemInserted(index);
        }
        writeTabCount(mHighlight);
    }

    public synchronized void deleteTab(int position, boolean incognito) {

        if (incognito && position >= mIncognitoWebViews.size()) return;
        if (!incognito && position >= mWebViews.size()) return;

        if (mCurrentView.isIncognitoTab()) {
            if (incognito) deleteIncognitoTab(position);
            else deleteFragmentTab(position, false);
        }
        else if (incognito){
            deleteFragmentTab(position, true);
        }
        else deleteNormalTab(position);
    }

    private synchronized void deleteNormalTab(int position){
        int current = mWebViews.indexOf(mCurrentView);

        XWebView reference = mWebViews.get(position);
        if (reference == null) return;

        if (reference.getUrl() != null && !reference.getUrl().startsWith(Constants.FILE)) {
            mPreferences.setSavedUrl(reference.getUrl());
        }
        if (position < current) { //if before current tab
            mTabsAdapter.remove(position);
        }
        else if (position + 1 < mWebViews.size()) { //if after or current
            if (position == 0){
                displayRemoveTab(mWebViews, 1, position);
            }
            else if (current == position) {
                displayRemoveTab(mWebViews, position - 1, position);
            }
            else mTabsAdapter.remove(position);
        }
        else if (mWebViews.size() > 1) {
            if (current == position) {
                displayRemoveTab(mWebViews, position - 1, position);
            }
            else mTabsAdapter.remove(position);
        }
        else {
            if (mExitOnTabClose && (mCurrentView.getUrl() == null
                    || mCurrentView.getUrl().startsWith(Constants.FILE)
                    || mCurrentView.getUrl().equals(mHomepage))) {
                mIncognitoWebViews.clear();
                finish();
                return;
            }
            else {
                mWebViews.remove(position);
                mTabsAdapter.itemRemoved(position);
                if (mPreferences.getClearCacheExit() && mCurrentView != null && !isIncognitoTab()) {
                    mCurrentView.clearCache(true);
                }
                if (mPreferences.getClearHistoryExitEnabled() && !isIncognitoTab()) {
                    clearHistory();
                }
                if (mPreferences.getClearCookiesExitEnabled() && !isIncognitoTab()) {
                    clearCookies();
                }
                if (!mExitOnTabClose) {
                    newTab(null, false, -1, false);
                    reference.onDestroy();
                    reference = null;
                    return;
                }
                reference.pauseTimers();
                reference.onDestroy();
                reference = null;
                mCurrentView = null;
                finish();
                return;
            }
        }

        writeTabCount(0);

        if (mIsNewIntent && reference.isShown()) {
            mIsNewIntent = false;
            closeActivity();
        }
        reference.onDestroy();
        reference = null;
    }

    private synchronized void deleteIncognitoTab(int position){

        int current = mIncognitoWebViews.indexOf(mCurrentView);
        XWebView reference = mIncognitoWebViews.get(position);

        if (current > position) {
            mIncognitoAdapter.remove(position);
        }
        else if (position + 1 < mIncognitoWebViews.size()) { //if after current tab
            if (position == 0){
                displayRemoveIncognitoTab(mIncognitoWebViews, 1, position);
            }
            else if (current == position) {
                displayRemoveIncognitoTab(mIncognitoWebViews, position - 1, position);
            }
            else mIncognitoAdapter.remove(position);
        }
        else if (mIncognitoWebViews.size() > 1) {
            if (current == position) {
                displayRemoveIncognitoTab(mIncognitoWebViews, position - 1, position);//showTab(mIncognitoWebViews.get(position - 1), false);
            }
            else mIncognitoAdapter.remove(position);
        }
        else { //size = 1 last tab
            mIncognitoWebViews.remove(position);
            mIncognitoAdapter.notifyDataSetChanged();
            if (mWebViews.size() > 0) showTab(mWebViews.get(mWebViews.size() - 1), true);
            else newTab(null, false, -1, false);
            if (mBottomLayout) mBottomDrawerManager.showTabs();
            else mViewPager.setCurrentItem(0, true);
        }
        writeTabCount(0);
        reference.onDestroy();
    }

    private synchronized void deleteFragmentTab(int position, boolean deleteIncognito){
        if (deleteIncognito){ //delete tabs in incognito fragment from normal tabs fragment
            mIncognitoAdapter.remove(position);
        }
        else mTabsAdapter.remove(position);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showCloseDialog(mCurrentView.getPosition(), mCurrentView.isIncognitoTab());
        }
        return true;
    }

    private void closeBrowser() {
        if (mPreferences.getClearCacheExit() && mCurrentView != null && !isIncognitoTab()) {
            for (XWebView webView : mWebViews) {
                webView.clearCache(true);
            }
<<<<<<< HEAD
=======
            Log.d(Constants.TAG, "Cache Cleared");
>>>>>>> origin/master
        }
        if (mPreferences.getClearHistoryExitEnabled() && !isIncognitoTab()) {
            clearHistory();
        }
        if (mPreferences.getClearCookiesExitEnabled() && !isIncognitoTab()) {
            clearCookies();
        }
        mCurrentView = null;
        for (int n = 0; n < mWebViews.size(); n++) {
            if (mWebViews.get(n) != null) mWebViews.get(n).onDestroy();
        }
        mWebViews = new ArrayList<>();
        finish();
    }

    public void clearHistory() {
        mHistoryManager.deleteFaviconDir();
        mDatabaseManager.dropTables();
        WebViewDatabase m = WebViewDatabase.getInstance(this);
        m.clearFormData();
        m.clearHttpAuthUsernamePassword();
//        if (mSystemBrowser) {
//            try {
//                //Browser.clearHistory(getContentResolver());
//            }
//            catch (NullPointerException ignored) { }
//        }
        Utils.trimCache(this);
        openHistory();
    }

    private void clearCache(){
        for (XWebView view : mWebViews){
            view.getWebView().clearCache(true);
        }
        for (XWebView view : mIncognitoWebViews){
            view.getWebView().clearCache(true);
        }
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public void clearCookies() {
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

    BackListener mDefaultBackListener = new BackListener() {
        @Override public void onBackPressed() {
            if (mCurrentView != null) {
                if (mCurrentView.canGoBack()) {
                    if (!mCurrentView.isShown()) {
                        onHideCustomView();
                    }
                    else mCurrentView.goBack();
                }
                else {
                    if (mCurrentView.isIncognitoTab()) deleteIncognitoTab(mCurrentView.getPosition());
                    else deleteNormalTab(mCurrentView.getPosition());
                }
            }
            else {
                Log.e(Constants.TAG, "This shouldn't happen ever");
                //super.onBackPressed();
            }
        }
    };

    BackListener mBackListener = mDefaultBackListener;

    public void setBackListener(BackListener listener){
        mBackListener = listener;
    }

    public void clearBacklistener(){
        mBackListener = mDefaultBackListener;
    }

    @Override public void onBackPressed() {
        mBackListener.onBackPressed();
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
        if (mBookmarkManager.getBookmarksChanged()) {
            ThreadExecutor.execute(new Runnable() {
                public void run() {
                    mBookmarkManager.saveBookmarks(null);
                }
            });
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
        if (mDatabaseManager != null) {
            mDatabaseManager.close();
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSearchAdapter != null) {
            mSearchAdapter.refreshPreferences();
            //mSearchAdapter.refreshBookmarks();
        }
        if (mCurrentView != null) {
            mCurrentView.resumeTimers();
            mCurrentView.onResume();

            mDatabaseManager = DatabaseManager.getInstance(getApplicationContext());
            if (mHistoryPage == null) mHistoryPage = mHistoryManager.getHistoryPage();
            mDatabaseManager.updateDate();
        }
        if (!mSettingsChanged) return;
        initializePreferences();
        if (mWebViews != null) {
            for (int n = 0; n < mWebViews.size(); n++) {
                if (mWebViews.get(n) != null) {
                    mWebViews.get(n).initializePreferences();
                }
                else mWebViews.remove(n);
            }
        }
        if (mIncognitoWebViews != null) {
            for (int n = 0; n < mIncognitoWebViews.size(); n++) {
                if (mIncognitoWebViews.get(n) != null) {
                    mIncognitoWebViews.get(n).initializePreferences();
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

<<<<<<< HEAD
=======
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
            holder.favicon.setImageBitmap(web.getFavicon());

            if (web.isForegroundTab()) {
                holder.txtTitle.setTextAppearance(context, R.style.boldText);
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

>>>>>>> origin/master
    private void incognitoToolbar(){
        if (incognitotoolbar) return;
        mToolbarLayout.setBackgroundColor(Color.DKGRAY);
        mSearchBox.setBackground(mDark);
        mSearch.setTextColor(Color.WHITE);
        mOverflowIcon.setColorFilter(Color.WHITE);
        incognitotoolbar = true;
        normaltoolbar = false;
    }

    private boolean normaltoolbar = false;
    private boolean incognitotoolbar = false;

    private void normalToolbar(){
        if (normaltoolbar) return;
        if (mDarkTheme) {
            mSearchBox.setBackground(mGrey);
        }
        else {
            mToolbarLayout.setBackgroundColor(mBackgroundColor);
            mSearchBox.setBackground(mWhite);
        }
        mSearch.setTextColor(Color.BLACK);
        mOverflowIcon.clearColorFilter();
        normaltoolbar = true;
        incognitotoolbar = false;
    }

    public Bitmap getDefaultFavicon(){
        return mWebpageBitmap;
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
                case 0: // Default, openDrawer only the domain
                    //url = url.replaceFirst(Constants.HTTP, "");
                    //url = Utils.getDomainName(url);
                    mSearch.setText(url);
                    break;
                case 1: // URL, openDrawer the entire URL
                    mSearch.setText(url);
                    break;
                case 2: // Title, openDrawer the page's title
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
        if (url == null || url.startsWith(Constants.FILE)) {
            return;
        }
        if (mDatabaseManager == null) {
            mDatabaseManager = DatabaseManager.getInstance(mActivity);
        }
        ThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mDatabaseManager.visitHistoryItem(title, url);
            }
        });
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
        mHistoryPage = mHistoryManager.getHistoryPage();
        mCurrentView.loadUrl(mHistoryUrl);
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
    public void updateTab(int position, boolean incognito, int type) {
        if (incognito) mIncognitoAdapter.itemChanged(position, type);
        else mTabsAdapter.itemChanged(position, type);
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
        WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;

        int position = 1;
        if (mOpenLinkBackground) {
            XWebView webView = new XWebView(this, "", mCurrentView.isIncognitoTab(), true).asBackground();
            transport.setWebView(webView.getWebView());
            resultMsg.sendToTarget();
            return;
        }
        newTab("", false, position = mCurrentView.getPosition() + 1, mCurrentView.isIncognitoTab());
        if (mCurrentView.isIncognitoTab()){
            transport.setWebView(mIncognitoWebViews.get(position).getWebView());
        }
        else transport.setWebView(mWebViews.get(position).getWebView());
        resultMsg.sendToTarget();
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
    public String[] getBookmark(){
        return new String[]{mCurrentView.getTitle(), mCurrentView.getUrl(), mCurrentView.getFaviconName()};
    }

    public void setTabAdapter(ITabViewAdapter adapter){
        mTabsAdapter = adapter;
    }

    public void setIncognitoAdapter(ITabViewAdapter adapter){
        mIncognitoAdapter = adapter;
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
        if (mCurrentView.getWebView() != null) {
            result = mCurrentView.getWebView().getHitTestResult();
        }
        if (url != null) {
            if (result != null && result.getExtra() != null && (result.getType() == HitTestResult.SRC_IMAGE_ANCHOR_TYPE || result.getType() == HitTestResult.IMAGE_TYPE)) {
                showLongClickImageDialog(url, result.getExtra());
            }
            else showLongClickLinkDialog(url);
        }
        else if (result != null) {
            if (result.getExtra() != null) {
                final String newUrl = result.getExtra();
                if (result.getType() == HitTestResult.SRC_IMAGE_ANCHOR_TYPE || result.getType() == HitTestResult.IMAGE_TYPE) {
                    showLongClickImageDialog(newUrl, newUrl);
                }
                else showLongClickLinkDialog(newUrl);
            }
        }
    }

<<<<<<< HEAD
    private void showLongClickLinkDialog(final String url){

        final boolean isIncognito = mCurrentView.isIncognitoTab();

        blockLink.setVisibility(View.GONE);
        linkUrl.setText(url);

        if (isIncognito){
            newTab1.setText("Open link in new tab");
            newTab2.setText("Open link in new incognito tab");
        }

        openLink.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentView.loadUrl(url);
                mLinkDialog.dismiss();
            }
        });

        copyLink.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", url);
                clipboard.setPrimaryClip(clip);
                mLinkDialog.dismiss();
            }
        });

        newTab1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                newTab(url, mOpenLinkBackground, -1, isIncognito);
                mLinkDialog.dismiss();
            }
        });

        newTab2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                newTab(url, mOpenLinkBackground, mCurrentView.getPosition() + 1, isIncognito);
                mLinkDialog.dismiss();
            }
        });

        newTab1.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                newTab(url, !mOpenLinkBackground, -1, isIncognito);
                mLinkDialog.dismiss();
                return true;
            }
        });

        newTab2.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                newTab(url, !mOpenLinkBackground, mCurrentView.getPosition() + 1, isIncognito);
                mLinkDialog.dismiss();
                return true;
            }
        });

        mLinkDialog.show();
    }

    private void showLongClickImageDialog(final String url, final String image){
        final boolean isIncognito = mCurrentView.isIncognitoTab();

        imageUrl.setText(url);
        blockImage.setVisibility(View.GONE);

        blockImage.setOnClickListener(new OnClickListener() {
            @Override public void onClick(View v) {
                mDatabaseManager.addAdblockUrl(url);
                Utils.showToast(mActivity, "Url added to adblock list");
                mImageDialog.dismiss();
            }
        });

        openImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentView.loadUrl(image);
                mImageDialog.dismiss();
            }
        });

        downloadImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.downloadFile(mActivity, image, mCurrentView.getUserAgent(), "attachment", false);
                mImageDialog.dismiss();
            }
        });

        copyImageLink.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                String copy = (url == null) ? image : url;
                ClipData clip = ClipData.newPlainText("label", copy);
                clipboard.setPrimaryClip(clip);
                mImageDialog.dismiss();
            }
        });

        newTabImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                newTab(image, mOpenLinkBackground, mCurrentView.getPosition() + 1, isIncognito);
                mImageDialog.dismiss();
            }
        });

        newTabImage.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                newTab(image, !mOpenLinkBackground, mCurrentView.getPosition() + 1, isIncognito);
                mImageDialog.dismiss();
                return true;
            }
        });

        newTabImage2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                newTab(url, mOpenLinkBackground, mCurrentView.getPosition() + 1, isIncognito);
                mImageDialog.dismiss();
            }
        });
        newTabImage2.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                newTab(url, !mOpenLinkBackground, mCurrentView.getPosition() + 1, isIncognito);
                mImageDialog.dismiss();
                return true;
            }
        });

        if (url == null) newTabImage2.setVisibility(View.GONE);

        mImageDialog.show();
    }

    private AlertDialog mImageDialog;
    private AlertDialog mLinkDialog;
    private LinearLayout mImageDialogLayout;
    private LinearLayout mLinkDialogLayout;

    private TextView imageUrl;
    private TextView blockImage;
    private TextView openImage;
    private TextView downloadImage;
    private TextView copyImageLink;
    private TextView newTabImage;
    private TextView newTabImage2;

    private TextView linkUrl;
    private TextView blockLink;
    private TextView openLink;
    private TextView copyLink;
    private TextView newTab1;
    private TextView newTab2;

    private void initLongClickDialogs(){
        mImageDialogLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.image_dialog, null);
        imageUrl = (TextView) mImageDialogLayout.findViewById(R.id.url);
        blockImage = (TextView) mImageDialogLayout.findViewById(R.id.blockUrl);
        openImage = (TextView) mImageDialogLayout.findViewById(R.id.openLink);
        downloadImage = (TextView) mImageDialogLayout.findViewById(R.id.download);
        copyImageLink = (TextView) mImageDialogLayout.findViewById(R.id.copyLink);
        newTabImage = (TextView) mImageDialogLayout.findViewById(R.id.newTab1);
        newTabImage2 = (TextView) mImageDialogLayout.findViewById(R.id.newTab2);

        mLinkDialogLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.link_dialog, null);
        linkUrl = (TextView) mLinkDialogLayout.findViewById(R.id.url);
        blockLink = (TextView) mLinkDialogLayout.findViewById(R.id.blockUrl);
        openLink = (TextView) mLinkDialogLayout.findViewById(R.id.openLink);
        copyLink = (TextView) mLinkDialogLayout.findViewById(R.id.copyLink);
        newTab1 = (TextView) mLinkDialogLayout.findViewById(R.id.newTab1);
        newTab2 = (TextView) mLinkDialogLayout.findViewById(R.id.newTab2);

        imageUrl.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                blockImage.setVisibility(View.VISIBLE);
                return false;
            }
        });

        linkUrl.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                blockLink.setVisibility(View.VISIBLE);
                return false;
            }
        });

        mImageDialog = new AlertDialog.Builder(mActivity).create();
        mImageDialog.setView(mImageDialogLayout);

        mLinkDialog = new AlertDialog.Builder(mActivity).create();
        mLinkDialog.setView(mLinkDialogLayout);
    }

    public void clearBrowsingData(){
        if (mClearHistoryDialog != null){
            mClearHistoryDialog.show();
            return;
        }
        final int[] array = new int[] {1, 1, 1};
        LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.clearhistory_dialog, null);
        layout.findViewById(R.id.cbHistory).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckedTextView cb = (CheckedTextView) view;
                cb.setChecked(!cb.isChecked());
                if (cb.isChecked()) {
                    array[0] = 1;
                }
                else array[0] = 0;
            }
        });
        layout.findViewById(R.id.cbCache).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckedTextView cb = (CheckedTextView) view;
                cb.setChecked(!cb.isChecked());
                if (cb.isChecked()) {
                    array[1] = 1;
                }
                array[1] = 0;
            }
        });
        layout.findViewById(R.id.cbCookies).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckedTextView cb = (CheckedTextView) view;
                cb.setChecked(!cb.isChecked());
                if (cb.isChecked()) {
                    array[2] = 1;
                }
                array[2] = 0;
            }
        });
        mClearHistoryDialog = new AlertDialog.Builder(this)
                .setTitle("Clear browsing data")
                .setView(layout)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (array[0] == 1) clearHistory();
                        if (array[1] == 1) clearCache();
                        if (array[2] == 1) clearCookies();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .create();
        mClearHistoryDialog.setCanceledOnTouchOutside(true);
        mClearHistoryDialog.show();
    }
=======
>>>>>>> origin/master

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
                if (!mCurrentView.goBack()) {
                    deleteTab(mCurrentView.getPosition(), mCurrentView.isIncognitoTab());
                }
                break;
            case R.id.action_forward:
                mCurrentView.goForward();
                break;
            case R.id.new_tab_button:
                newTab(null, false, -1, IsIncognitoMode());
                break;
            case R.id.button_next:
                String text;
                if (!(text = mFindEditText.getText().toString()).equals(mFindText)){
                    mCurrentView.find(mFindText = text);
                }
                else mCurrentView.findNext();
                break;
            case R.id.button_back:
                if (!(text = mFindEditText.getText().toString()).equals(mFindText)){
                    mCurrentView.find(mFindText = text);
                }
                else mCurrentView.findPrevious();
                break;
            case R.id.button_quit:
                mCurrentView.getWebView().clearMatches();
                mMainLayout.removeView(mFindBar);

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

<<<<<<< HEAD
    public class OnSwipeListener extends GestureDetector.SimpleOnGestureListener{

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            mTabButtonSwiped = true;
            float x1 = e1.getX();
            float y1 = e1.getY();

            float x2 = e2.getX();
            float y2 = e2.getY();

            double rad = Math.atan2(y1-y2,x2-x1) + Math.PI;
            double angle =  (rad*180/Math.PI + 180)%360;
            get(angle);

            return false;
        }

        private void get(double angle){
            if(inRange(angle, 45, 135)) {// Direction.up;
                if (!isIncognitoTab()) showTabAnimated(mWebViews, -1, Direction.up);
                else showTabAnimated(mIncognitoWebViews, -1, Direction.up);
            }
            else if(inRange(angle, 0,45) || inRange(angle, 315, 360)) {//Direction.right;
                if (!isIncognitoTab()) showTabAnimated(mWebViews, 1, Direction.right);
                showTabAnimated(mIncognitoWebViews, 1, Direction.right);
            }
            else if(inRange(angle, 225, 315)) {//Direction.down;
                if (!isIncognitoTab()) showTabAnimated(mWebViews, 1, Direction.down);
                showTabAnimated(mIncognitoWebViews, 1, Direction.down);
            }
            else {//Direction.left;
                if (!isIncognitoTab()) showTabAnimated(mWebViews, -1, Direction.left);
                else showTabAnimated(mIncognitoWebViews, -1, Direction.left);
            }
        }
        private boolean inRange(double angle, float init, float end){
            return (angle >= init) && (angle < end);
        }
    }

    private class TabButtonListener implements OnTouchListener{
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                view.setPressed(true);
            }
            else if (event.getAction() == MotionEvent.ACTION_UP) {
                view.setPressed(false);
                if (mBottomLayout) {
                    mBottomDrawerManager.openDrawer();
                }
                else openTabDrawer();
            }
            return true;
        }
    }

    private class TabButtonSwipeListener implements OnTouchListener{
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            mSwipeListener.onTouchEvent(event);
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                view.setPressed(true);
                mTabButtonSwiped = false;
            }
            else if (event.getAction() == MotionEvent.ACTION_UP) {
                view.setPressed(false);
                if (!mTabButtonSwiped) {
                    if (mBottomLayout) {
                        mBottomDrawerManager.openDrawer();
                    }
                    else openTabDrawer();
                }
            }
            return true;
        }
    }

   

    private Handler mHandler = SpeedHandler.getHandler();
}
=======
    private enum Direction { up, down, left, right }

    private int mYOffset = -Utils.convertDpToPixels(56);

    private void showRenderingMenu(){
        View layout = getLayoutInflater().inflate(R.layout.rendering_menu, null);
        final PopupWindow popupWindow = new PopupWindow(layout, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layout.findViewById(R.id.normal).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPreferences.setRenderingMode(0);
                mCurrentView.initializePreferences(mActivity);
                mCurrentView.reload();
            }
        });
        layout.findViewById(R.id.inverted).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPreferences.setRenderingMode(1);
                mCurrentView.initializePreferences(mActivity);
            }
        });
        layout.findViewById(R.id.grayscale).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPreferences.setRenderingMode(2);
                mCurrentView.initializePreferences(mActivity);
            }
        });
        layout.findViewById(R.id.invertedGrayscale).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPreferences.setRenderingMode(3);
                mCurrentView.initializePreferences(mActivity);
            }
        });
        layout.findViewById(R.id.nightMode).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentView.loadUrl("javascript:(function(){N=document.createElement('link');S='*{background:#151515 !important;color:grey !important}:link,:link *{color:#ddddff !important}:visited,:visited *{color:#ddffdd !important}';N.rel='stylesheet';N.href='data:text/css,'+escape(S);document.getElementsByTagName('head')[0].appendChild(N);})()");
            }
        });
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.showAsDropDown(mOverflowIcon, 0, mYOffset);

    }
}
>>>>>>> origin/master
