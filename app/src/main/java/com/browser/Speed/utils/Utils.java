/*
 * Copyright 2014 A.C.R. Development
 */
package com.browser.Speed.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.webkit.URLUtil;
import android.widget.TextView;
import android.widget.Toast;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.browser.Speed.constant.Constants;
import com.browser.Speed.database.HistoryItem;
import com.browser.Speed.R;
import com.browser.Speed.download.DownloadHandler;

public final class Utils {

	private Utils() {}

	public static void downloadFile(final Activity activity, final String url,
			final String userAgent, final String contentDisposition, final boolean privateBrowsing) {
		String fileName = URLUtil.guessFileName(url, null, null);
		DownloadHandler.onDownloadStart(activity, url, userAgent, contentDisposition, null,
				privateBrowsing);
		Log.i(Constants.TAG, "Downloading" + fileName);
	}

	public static Intent newEmailIntent(Context context, String address, String subject,
			String body, String cc) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.putExtra(Intent.EXTRA_EMAIL, new String[] { address });
		intent.putExtra(Intent.EXTRA_TEXT, body);
		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		intent.putExtra(Intent.EXTRA_CC, cc);
		intent.setType("message/rfc822");
		return intent;
	}

	public static void createInformativeDialog(Context context, String title, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(message)
				.setCancelable(true)
				.setPositiveButton(context.getResources().getString(R.string.action_ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
		AlertDialog alert = builder.create();
		alert.show();
	}

    public static void initToastLayout(LayoutInflater inflater){
        mToast = (TextView) inflater.inflate(R.layout.toast_layout, null);
    }

    private static TextView mToast;

	public static void showToast(Context context, String message) {
        mToast.setText(message);
        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(mToast);
        toast.show();
		//Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

    public static void showToast(Context context, int resource){
        mToast.setText(context.getResources().getText(resource));
        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(mToast);
        toast.show();
    }

	private static DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
	private static float density = metrics.density;

	public static int convertDpToPixels(int dp) {
		return (int) (dp * density + 0.5f);
	}

	public static float convertDpToPixels(float dp){
		return (dp * density + 0.5f);
	}

	public static String getDomainName(String url) {
		boolean ssl = url.startsWith(Constants.HTTPS);
		int index = url.indexOf('/', 8);
		if (index != -1) {
			url = url.substring(0, index);
		}

		URI uri;
		String domain = null;
		try {
			uri = new URI(url);
			domain = uri.getHost();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		if (domain == null || domain.isEmpty()) {
			return url;
		}
		if (ssl) return Constants.HTTPS + domain;
		else return domain.startsWith("www.") ? domain.substring(4) : domain;
	}

	public static String getProtocol(String url) {
		int index = url.indexOf('/');
		return url.substring(0, index + 2);
	}

	public static List<HistoryItem> getOldBookmarks(Context context) {
		List<HistoryItem> bookmarks = new ArrayList<>();
		File bookUrl = new File(context.getFilesDir(), "bookurl");
		File book = new File(context.getFilesDir(), "bookmarks");
		try {
			BufferedReader readUrl = new BufferedReader(new FileReader(bookUrl));
			BufferedReader readBook = new BufferedReader(new FileReader(book));
			String u, t;
			while ((u = readUrl.readLine()) != null && (t = readBook.readLine()) != null) {
				HistoryItem map = new HistoryItem(u, t, R.drawable.ic_bookmark);
				bookmarks.add(map);
			}
			readBook.close();
			readUrl.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bookmarks;
	}

	public static String[] getArray(String input) {
		return input.split("\\|\\$\\|SEPARATOR\\|\\$\\|");
	}

	public static void trimCache(Context context) {
		try {
			File dir = context.getCacheDir();

			if (dir != null && dir.isDirectory()) {
				deleteDir(dir);
			}
		} catch (Exception ignored) {

		}
	}

	public static boolean deleteDir(File dir) {
		if (dir != null && dir.isDirectory()) {
			String[] children = dir.list();
			for (String aChildren : children) {
				boolean success = deleteDir(new File(dir, aChildren));
				if (!success) {
					return false;
				}
			}
		}
		return dir != null && dir.delete();
	}

	/**
	 * Creates and returns a new favicon which is the same as the provided
	 * favicon but with horizontal or vertical padding of 4dp
	 * 
	 * @param bitmap
	 *            is the bitmap to pad.
	 * @return the padded bitmap.
	 */
	public static Bitmap padFavicon(Bitmap bitmap) {
		int padding = Utils.convertDpToPixels(4);

		Bitmap paddedBitmap = Bitmap.createBitmap(bitmap.getWidth() + padding, bitmap.getHeight()
				+ padding, Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(paddedBitmap);
		canvas.drawARGB(0x00, 0x00, 0x00, 0x00); // this represents white color
		canvas.drawBitmap(bitmap, padding / 2, padding / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

		return paddedBitmap;
	}

	@SuppressLint("SimpleDateFormat")
	public static File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		return File.createTempFile(imageFileName, /* prefix */
				".jpg", /* suffix */
				storageDir /* directory */
		);
	}

	public static Bitmap getWebpageBitmap(Resources resources, boolean dark) {
		if (dark) {
			if (mWebIconDark == null) {
				mWebIconDark = BitmapFactory.decodeResource(resources, R.drawable.ic_webpage_dark);
			}
			return mWebIconDark;
		} else {
			if (mWebIconLight == null) {
				mWebIconLight = BitmapFactory.decodeResource(resources, R.drawable.ic_webpage);
			}
			return mWebIconLight;
		}
	}

	private static Bitmap mWebIconLight;
	private static Bitmap mWebIconDark;

}
