/*
 * Copyright 2014 A.C.R. Development
 */
package com.browser.Speed.object;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.browser.Speed.controller.IBrowserController;

public class ClickHandler extends Handler {

	private IBrowserController mBrowserController;

	public ClickHandler(Context context) {
		try {
			mBrowserController = (IBrowserController) context;
		} catch (ClassCastException e) {
			throw new ClassCastException(context + " must implement IBrowserController");
		}
	}

	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		String url = msg.getData().getString("url");
		mBrowserController.longClickPage(url);
	}
}
