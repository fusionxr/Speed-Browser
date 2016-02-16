package com.browser.Speed.constant;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Context;

import com.browser.Speed.activity.BrowserApp;
import com.browser.Speed.database.HistoryItem;
import com.browser.Speed.R;
import com.browser.Speed.database.HistoryDatabase;

public class HistoryPage {

	private static final String FILENAME = "history.html";

    private static final String BODY = "<!DOCTYPE html><html xmlns=\"http://www.w3.org/1999/xhtml\"><head><meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\"><title>History</title></head><body onload=Android.showHistoryPage(); onfocus=Android.showHistoryPage(); ><div id=\"content\">";
	private static final String SCRIPT = "<script type=\"text/javascript\">function showHistory(){Android.showHistoryPage();  return false;} var myEvent = window.attachEvent || window.addEventListener; var chkevent = window.attachEvent ? 'onbeforeunload' : 'beforeunload'; myEvent(chkevent, function(e) { Android.removeHistoryPage(); });  </script>";
	private static final String END = "</div></body></html>";


    public static String initHistoryPage(Context context){
        StringBuilder historyBuilder = new StringBuilder();
        historyBuilder.append(HistoryPage.BODY);
        historyBuilder.append(HistoryPage.SCRIPT);
        historyBuilder.append(HistoryPage.END);
        File historyWebPage = new File(context.getFilesDir(), FILENAME);
        try {
            FileWriter historyWriter = new FileWriter(historyWebPage, false);
            historyWriter.write(historyBuilder.toString());
            historyWriter.close();
        }
        catch (IOException e) { e.printStackTrace(); }
        return Constants.FILE + historyWebPage;
    }

}
