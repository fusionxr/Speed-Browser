package com.browser.Speed.database;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.browser.Speed.utils.Utils;

public class HistoryItem implements Comparable<HistoryItem> {

	private int mId = 0;
	private String mUrl = "";
	private String mTitle = "";
	private String mFolder = "";
    private String mFavicon = "";
	private Bitmap mBitmap = null;
	private int mImageId = 0;
	private int mOrder = 0;
    private boolean mIsHeader;

	public HistoryItem() {

	}

	public HistoryItem(int id, String url, String title) {
		this.mId = id;
		this.mUrl = url;
		this.mTitle = title;
		this.mBitmap = null;
	}

	public HistoryItem(String url, String title, int imageId) {
		this.mUrl = url;
		this.mTitle = title;
		this.mBitmap = null;
		this.mImageId = imageId;
	}

    public HistoryItem(String date){
        mTitle = date;
        mIsHeader = true;
    }

	public int getId() { return this.mId; }
	public int getImageId() { return this.mImageId; }
	public void setID(int id) { this.mId = id; }
	public void setImageId(int id) { this.mImageId = id; }
    public void setFavicon(String id) { this.mFavicon = id;}
	public void setBitmap(Bitmap image) { mBitmap = image; }

	public void setFolder(String folder) {
		mFolder = (folder == null) ? "" : folder;
	}

	public void setOrder(int order) {
		mOrder = order;
	}

	public int getOrder() {
		return mOrder;
	}

	public String getFolder() { return mFolder; }

    public String getFavicon() { return mFavicon; }

	public Bitmap getBitmap() {
		return mBitmap;
	}

	public String getUrl() {
		return this.mUrl;
	}

	public void setUrl(String url) {
		this.mUrl = (url == null) ? "" : url;
	}

	public String getTitle() {
		return this.mTitle;
	}

	public void setTitle(String title) { this.mTitle = (title == null) ? "" : title; }

    public void setType(boolean header) { mIsHeader = header; }

    public boolean isHeader() { return mIsHeader; }

	@Override
	public String toString() {
		return mTitle;
	}

	@Override
	public int compareTo(@NonNull HistoryItem another) {
		return mTitle.compareTo(another.mTitle);
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) {
			return true;
		}
		if (o == null || ((Object) this).getClass() != o.getClass()) {
			return false;
		}

		HistoryItem that = (HistoryItem) o;

		if (mId != that.mId) {
			return false;
		}
		if (mImageId != that.mImageId) {
			return false;
		}
		if (mBitmap != null ? !mBitmap.equals(that.mBitmap) : that.mBitmap != null) {
			return false;
		}
		return mTitle.equals(that.mTitle) && mUrl.equals(that.mUrl);
	}

	@Override
	public int hashCode() {

		int result = mId;
		result = 31 * result + mUrl.hashCode();
		result = 31 * result + mTitle.hashCode();
		result = 31 * result + (mBitmap != null ? mBitmap.hashCode() : 0);
		result = 31 * result + mImageId;

		return result;
	}
}
