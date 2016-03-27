
package com.browser.Speed.constant;

import android.app.Activity;
import android.os.AsyncTask;

import com.browser.Speed.activity.BrowserApp;
import com.browser.Speed.R;
import com.browser.Speed.preference.PreferenceManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class StartPage {

    private static PreferenceManager mPreferences = PreferenceManager.getInstance();
    public static String mHomepage;
    public static StartPage mInstance;
    private static Activity mActivity;
    private GetLogoTask mGetLogoTask = new GetLogoTask();
    private String mLogoUrl;

    private class GetLogoTask extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected String doInBackground(Void... params) {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL("http://www.google.com").openConnection();
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.connect();
                // Read and store the result line by line then return the entire string.
                InputStream stream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder builder = new StringBuilder();
                for (String line; (line = reader.readLine()) != null; ) {
                    builder.append(line);
                }
                stream.close();

                String html = builder.toString();
                String source = "";
                String logo = "";
                int index;
                if ((index = html.indexOf("id=\"hplogo")) != -1) {
                    source = html.substring(index - 200, index);
                    logo = source.substring(source.indexOf("url(") + 4, source.indexOf(") "));
                    mLogoUrl = "http://www.google.co.uk/"+ logo;
                    initHomepage(mActivity);
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return "";
        }

    }

    public static void initHomepage(Activity activity) {
        mInstance = new StartPage();
        mActivity = activity;
        StringBuilder homepageBuilder = new StringBuilder();
        homepageBuilder.append(StartPage.HEAD);
        String icon;
        String searchUrl;
        switch (mPreferences.getSearchChoice()) {
            case 0: // CUSTOM SEARCH
                icon = "file:///android_asset/lightning.png";
                searchUrl = mPreferences.getSearchUrl();
                break;
            case 1: // GOOGLE_SEARCH;
                icon = "file:///android_asset/google.png"; // "https://www.google.com/images/srpr/logo11w.png";
                searchUrl = Constants.GOOGLE_SEARCH;
                break;
            case 2: // ANDROID SEARCH;
                icon = "file:///android_asset/ask.png";
                searchUrl = Constants.ASK_SEARCH;
                break;
            case 3: // BING_SEARCH;
                icon = "file:///android_asset/bing.png"; // "http://upload.wikimedia.org/wikipedia/commons/thumb/b/b1/Bing_logo_%282013%29.svg/500px-Bing_logo_%282013%29.svg.png";
                searchUrl = Constants.BING_SEARCH;
                break;
            case 4: // YAHOO_SEARCH;
                icon = "file:///android_asset/yahoo.png"; // "http://upload.wikimedia.org/wikipedia/commons/thumb/2/24/Yahoo%21_logo.svg/799px-Yahoo%21_logo.svg.png";
                searchUrl = Constants.YAHOO_SEARCH;
                break;
            case 5: // STARTPAGE_SEARCH;
                icon = "file:///android_asset/startpage.png"; // "https://startpage.com/graphics/startp_logo.gif";
                searchUrl = Constants.STARTPAGE_SEARCH;
                break;
            case 6: // STARTPAGE_MOBILE
                icon = "file:///android_asset/startpage.png"; // "https://startpage.com/graphics/startp_logo.gif";
                searchUrl = Constants.STARTPAGE_MOBILE_SEARCH;
                break;
            case 7: // DUCK_SEARCH;
                icon = "file:///android_asset/duckduckgo.png"; // "https://duckduckgo.com/assets/logo_homepage.normal.v101.png";
                searchUrl = Constants.DUCK_SEARCH;
                break;
            case 8: // DUCK_LITE_SEARCH;
                icon = "file:///android_asset/duckduckgo.png"; // "https://duckduckgo.com/assets/logo_homepage.normal.v101.png";
                searchUrl = Constants.DUCK_LITE_SEARCH;
                break;
            case 9: // BAIDU_SEARCH;
                icon = "file:///android_asset/baidu.png"; // "http://www.baidu.com/img/bdlogo.gif";
                searchUrl = Constants.BAIDU_SEARCH;
                break;
            case 10: // YANDEX_SEARCH;
                icon = "file:///android_asset/yandex.png"; // "http://upload.wikimedia.org/wikipedia/commons/thumb/9/91/Yandex.svg/600px-Yandex.svg.png";
                searchUrl = Constants.YANDEX_SEARCH;
                break;
            default: // DEFAULT GOOGLE_SEARCH;
                icon = "file:///android_asset/google.png";
                searchUrl = Constants.GOOGLE_SEARCH;
                break;

        }

        homepageBuilder.append(icon);
        homepageBuilder.append(StartPage.MIDDLE);
        homepageBuilder.append(searchUrl);
        homepageBuilder.append(StartPage.END);

        File homepage = new File(activity.getFilesDir(), "homepage.html");
        try {
            FileWriter hWriter = new FileWriter(homepage, false);
            hWriter.write(homepageBuilder.toString());
            hWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mHomepage = Constants.FILE + homepage;
    }

	public static final String HEAD = "<!DOCTYPE html><html xmlns=\"http://www.w3.org/1999/xhtml\">"
			+ "<head>"
			+ "<meta content=\"en-us\" http-equiv=\"Content-Language\" />"
			+ "<meta content=\"text/html; charset=utf-8\" http-equiv=\"Content-Type\" />"
			+ "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no\">"
			+ "<title>"
			+ BrowserApp.getAppContext().getString(R.string.home)
			+ "</title>"
			+ "</head>"
			+ "<style>body{background:#FFFFFF;text-align:center;margin:0px;}"
            + "#search_input{height:40px; width:100%;outline:none;border:none;font-size: 16px;background-color:transparent;}"
			+ "span { display: block; overflow: hidden; padding-left:5px;vertical-align:middle;}"
			+ ".search_bar{display:table;vertical-align:middle; width:90%; height:40px; max-width:500px; margin:0 auto; background-color:#fff;font-family: Arial; color: #444; "
            + "-moz-border-radius: 1px;-webkit-border-radius: 2px; border-radius: 2px; border-width:1px; border-style: solid; border-color: #b3b3b3;  }"
			+ "#search_submit{outline:none;height:37px;float:right;color:#404040;font-size:16px;font-weight:bold;border:none; background-color:transparent;}"
            + ".outer { display: table; position: absolute; height: 100%; width: 100%;}"
			+ ".middle { display: table-cell; vertical-align: middle;}"
            + ".inner { margin-left: auto; margin-right: auto; margin-bottom:10%; <!-->maybe bad for small screens</!--> width: 100%;} "
            + "img.smaller{width:60%;max-width:400px;}"
			+ ".box { vertical-align:middle;position:relative; display: block; margin: 10px;padding-left:10px;padding-right:10px;padding-top:5px;padding-bottom:5px;"
			+ " background-color:#fff;font-family: Arial;color: #444;"
			+ "font-size: 12px;-moz-border-radius: 2px;-webkit-border-radius: 2px;"
			+ "border-radius: 2px;}</style><body> <div class=\"outer\"><div class=\"middle\"><div class=\"inner\"><img id=\"logo\" class=\"smaller\" src=\"";

	public static final String MIDDLE = "\" ></br></br><form onsubmit=\"return search()\" class=\"search_bar\">"
			+ "<input type=\"submit\" id=\"search_submit\" value=\"\" ><span><input class=\"search\" type=\"text\" value=\"\" placeholder=\"Search\" id=\"search_input\" onclick=\"movecursor()\">"
			+ "</span></form></br></br></div></div></div><script type=\"text/javascript\">"
            + "function movecursor(){ Android.moveCursor(); } "
            + "function loadLogo(url) { document.getElementById(\"logo\").src = url; }"
            + "function search(){ if(document.getElementById(\"search_input\").value != \"\"){ window.location.href = \"";

	public static final String END = "\" + document.getElementById(\"search_input\").value;document.getElementById(\"search_input\").value = \"\";}return false;} </script></body></html>";

}
