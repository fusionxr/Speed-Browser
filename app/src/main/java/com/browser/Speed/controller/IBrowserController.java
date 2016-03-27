/*
 * Copyright 2014 A.C.R. Development
 */
package com.browser.Speed.controller;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Message;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient.CustomViewCallback;

import com.browser.Speed.view.XWebView;

public interface IBrowserController {

	void updateUrl(String title, boolean shortUrl);
	void updateProgress(int n);
	void updateHistory(String title, String url);
	void openFileChooser(ValueCallback<Uri> uploadMsg);
	void updateTab(int position, boolean incognito, int type);
	void onLongPress();
	void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback);
	void onHideCustomView();
	void onCreateWindow(boolean isUserGesture, Message resultMsg);
	void addBackgroundTab(XWebView webView);
	void longClickPage(String url);
    void clearBrowsingData();
	void newTab(String url);
    void showHistoryPage();
    void removeHistoryPage();
	void showFileChooser(ValueCallback<Uri[]> filePathCallback);
	void moveCursor();
    void setDrawerMargin(int margin);
	int getMenu();
    XWebView getCurrentView();
	Activity getActivity();
	Bitmap getDefaultVideoPoster();
	View getVideoLoadingProgressView();
	String[] getBookmark();
}
