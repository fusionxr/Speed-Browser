package com.browser.Speed.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.browser.Speed.preference.PreferenceManager;
import com.browser.Speed.R;

@SuppressWarnings("deprecation")
public class MainActivity extends BrowserActivity {

	CookieManager mCookieManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void updateCookiePreference() {
		mCookieManager = CookieManager.getInstance();
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			CookieSyncManager.createInstance(this);
		}
		mCookieManager.setAcceptCookie(PreferenceManager.getInstance().getCookiesEnabled());
		super.updateCookiePreference();
	}

	@Override
	public synchronized void initializeTabs() {
		restoreOrNewTab();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		handleNewIntent(intent);
		super.onNewIntent(intent);
	}

	@Override
	protected void onPause() {
		super.onPause();
        saveOpenTabs();
	}

	@Override
	public int getMenu() {
		return R.menu.main;
	}

	@Override
	public void closeActivity() {
		closeDrawers();
		moveTaskToBack(true);
	}
}
