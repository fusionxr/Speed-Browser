package com.browser.Speed.controller;

import android.graphics.Bitmap;

import com.browser.Speed.database.BookmarkFolder;
import com.browser.Speed.database.BookmarkItem;
import com.browser.Speed.view.XWebView;

import java.io.File;

public interface IBookmarkManager {

    void BookmarkPage(XWebView webView, Bitmap icon);
    void editBookmark(BookmarkItem bookmark);
    void softDelete(BookmarkItem bookmark);
    void newFolder();
    boolean getBookmarksChanged();
    boolean saveBookmarks(File folder);
    boolean LoadBookmarks(File file);
    BookmarkFolder getRootFolder();
}
