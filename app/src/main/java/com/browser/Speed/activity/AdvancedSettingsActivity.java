package com.browser.Speed.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.browser.Speed.controller.SpeedHandler;
import com.browser.Speed.database.DatabaseManager;
import com.browser.Speed.preference.PreferenceManager;
import com.browser.Speed.R;
import com.browser.Speed.utils.AdBlock;

import java.nio.charset.MalformedInputException;
import java.util.List;

public class AdvancedSettingsActivity extends ThemableSettingsActivity {

    private ScrollView mScrollView;
    private RecyclerView mAdblockView;
    private ImageView mHelp;
	private CheckBox cbAllowPopups, cbOpenLinksBackground, cbRestoreTabs, cbHardware, cbExitOnTabClose;
	private Context mContext;
	private TextView mRenderText;
	private TextView mUrlText, mTitle;
	private CharSequence[] mUrlOptions;
	private PreferenceManager mPreferences;
    private AdblockAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.advanced_settings);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		mContext = this;
        mScrollView = (ScrollView) findViewById(R.id.scrollView1);
        mAdblockView = (RecyclerView) findViewById(R.id.adblock_list);
        mTitle = (TextView) findViewById(R.id.settings_title);
        mHelp = (ImageView) findViewById(R.id.help);
        mAdapter = new AdblockAdapter();
		initialize();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        if (mScrollView.getVisibility() == View.GONE) {
            mHelp.setVisibility(View.GONE);
            mTitle.setText("Advanced Settings");
            mAdblockView.setVisibility(View.GONE);
            mScrollView.setVisibility(View.VISIBLE);
            return true;
        }
        finish();
		return true;
	}

    @Override
    public void onBackPressed() {
        if (mScrollView.getVisibility() == View.GONE) {
            mHelp.setVisibility(View.GONE);
            mTitle.setText("Advanced Settings");
            mAdblockView.setVisibility(View.GONE);
            mScrollView.setVisibility(View.VISIBLE);
        }
        else super.onBackPressed();
    }

    private void initialize() {

		mPreferences = PreferenceManager.getInstance();

		RelativeLayout rAllowPopups, rOpenLinksBackground, rRestoreTabs, rExitOnTabClose, rHardware, rAdblock;
		LinearLayout lRenderPicker, lUrlContent;

		rAllowPopups = (RelativeLayout) findViewById(R.id.rAllowPopups);
        rOpenLinksBackground = (RelativeLayout) findViewById(R.id.rOpenLinksBackground);
		rRestoreTabs = (RelativeLayout) findViewById(R.id.rRestoreTabs);
		rExitOnTabClose = (RelativeLayout) findViewById(R.id.rExitOnTabClose);
        rHardware = (RelativeLayout) findViewById(R.id.rHardwareRendering);
        rAdblock = (RelativeLayout) findViewById(R.id.rAdblockList);
		lUrlContent = (LinearLayout) findViewById(R.id.rUrlBarContents);

		cbAllowPopups = (CheckBox) findViewById(R.id.cbAllowPopups);
        cbOpenLinksBackground = (CheckBox) findViewById(R.id.cbOpenLinksBackground);
		cbRestoreTabs = (CheckBox) findViewById(R.id.cbRestoreTabs);
		cbExitOnTabClose = (CheckBox) findViewById(R.id.cbExitOnTabClose);
		cbHardware = (CheckBox) findViewById(R.id.cbHardwareRendering);

        cbAllowPopups.setChecked(mPreferences.getPopupsEnabled());
        cbOpenLinksBackground.setChecked(mPreferences.getOpenLinksBackground());
		cbRestoreTabs.setChecked(mPreferences.getRestoreLostTabsEnabled());
		cbExitOnTabClose.setChecked(mPreferences.getExitOnTabClose());
		cbHardware.setChecked(mPreferences.getHardwareRenderingEnabled());

		mUrlText = (TextView) findViewById(R.id.urlText);

		mUrlOptions = this.getResources().getStringArray(R.array.url_content_array);
		int option = mPreferences.getUrlBoxContentChoice();
		mUrlText.setText(mUrlOptions[option]);

		LayoutClickListener listener = new LayoutClickListener();

		rAllowPopups.setOnClickListener(listener);
        rOpenLinksBackground.setOnClickListener(listener);
		rRestoreTabs.setOnClickListener(listener);
		rExitOnTabClose.setOnClickListener(listener);
        rHardware.setOnClickListener(listener);
        rAdblock.setOnClickListener(listener);
		lUrlContent.setOnClickListener(listener);
        mHelp.setOnClickListener(listener);

		cbAllowPopups.setOnCheckedChangeListener(cbListener);
		cbRestoreTabs.setOnCheckedChangeListener(cbListener);
		cbExitOnTabClose.setOnCheckedChangeListener(cbListener);
        cbOpenLinksBackground.setOnCheckedChangeListener(cbListener);
        cbHardware.setOnCheckedChangeListener(cbListener);
	}

	private class LayoutClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.rAllowPopups:
					cbAllowPopups.setChecked(!cbAllowPopups.isChecked());
					break;
				case R.id.rOpenLinksBackground:
					cbOpenLinksBackground.setChecked(!cbOpenLinksBackground.isChecked());
                    break;
				case R.id.rRestoreTabs:
					cbRestoreTabs.setChecked(!cbRestoreTabs.isChecked());
					break;
				case R.id.rExitOnTabClose:
					cbExitOnTabClose.setChecked(!cbExitOnTabClose.isChecked());
					break;
				case R.id.rHardwareRendering:
					cbHardware.setChecked(!cbHardware.isChecked());
					break;
                case R.id.rAdblockList:
                    mAdapter.show();
                    break;
				case R.id.rUrlBarContents:
					urlBoxPicker();
					break;
                case R.id.help:
                    showHelpDialog();
                    break;
			}
		}

	}

	private OnCheckedChangeListener cbListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.cbAllowPopups:
                    mPreferences.setPopupsEnabled(isChecked);
                    break;
                case R.id.cbOpenLinksBackground:
                    mPreferences.setOpenLinksBackground(isChecked);
                    break;
                case R.id.cbRestoreTabs:
                    mPreferences.setRestoreLostTabsEnabled(isChecked);
                    break;
                case R.id.cbExitOnTabClose:
                    mPreferences.setExitOnTabClose(isChecked);
                    break;
                case R.id.cbHardwareRendering:
                    mPreferences.setHardwareRenderingEnabled(isChecked);
                    break;
            }
        }
    };

	private void urlBoxPicker() {

		AlertDialog.Builder picker = new AlertDialog.Builder(mContext);
		picker.setTitle(getResources().getString(R.string.url_contents));

		int n = mPreferences.getUrlBoxContentChoice();

		picker.setSingleChoiceItems(mUrlOptions, n, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				mPreferences.setUrlBoxContentChoice(which);
				if (which < mUrlOptions.length) {
					mUrlText.setText(mUrlOptions[which]);
				}
			}
		});
		picker.setNeutralButton(getResources().getString(R.string.action_ok), new DialogInterface.OnClickListener() { @Override public void onClick(DialogInterface dialog, int which) { } });
		picker.show();
	}

    private void showHelpDialog(){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        View layout = getLayoutInflater().inflate(R.layout.adblock_help, null);

        dialog.setTitle("Adblock Help");

        ((TextView) layout.findViewById(R.id.help_text)).setText("You can add custom urls to the adblock list by long clicking an image " +
                " or link to show the dialog menu, then long click the url text to show the option. You can also click the page " +
                "info button from the main menu to add the current page url to the list. Blocked urls will show up in this list from which you can " +
                "remove if you wish. The urls will be loaded in addition to the default adblock list. I do not plan to enable addition of bulk hosts" +
                " since the adblocker uses a significant amount of memory by default, however there is no limit on the number of urls you can " +
                "add. You will be able to keep the blocked urls during updates but will be deleted if you uninstall the app.");

        layout.findViewById(R.id.adblock_demo).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout imagelayout = (LinearLayout) getLayoutInflater().inflate(R.layout.adblock_demo_dialog, null);
                AlertDialog.Builder mImageDialog = new AlertDialog.Builder(mContext);
                ((TextView) imagelayout.findViewById(R.id.url)).setText("http://www.LongPressMe.com");
                final TextView blockImage = (TextView) imagelayout.findViewById(R.id.blockUrl);
                (imagelayout.findViewById(R.id.url)).setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        blockImage.setVisibility(View.VISIBLE);
                        return false;
                    }
                });
                mImageDialog.setView(imagelayout);
                mImageDialog.show();
            }
        });

        dialog.setView(layout);
        dialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        dialog.show();
    }

    private class AdblockAdapter extends RecyclerView.Adapter{

        private final LinearLayoutManager mLayoutManager;
        private final Activity mActivity = AdvancedSettingsActivity.this;
        private final int layout = R.layout.url_item;
        private DatabaseManager mDatabaseManager;
        private List<String> data;

        public AdblockAdapter(){
            mAdblockView = (RecyclerView) findViewById(R.id.adblock_list);
            mAdblockView.setHasFixedSize(true);
            mAdblockView.setAdapter(this);
            mLayoutManager = new LinearLayoutManager(mActivity);
            mAdblockView.setLayoutManager(mLayoutManager);
            mDatabaseManager = DatabaseManager.getInstance(mActivity);
        }

        public void show(){
            mTitle.setText("Adblock Settings");
            mScrollView.setVisibility(View.GONE);
            mAdblockView.setVisibility(View.VISIBLE);
            mHelp.setVisibility(View.VISIBLE);
            if (data != null) return;
            data = mDatabaseManager.getAdblockUrls();
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder xholder, int position) {
            UrlHolder holder = (UrlHolder) xholder;
            holder.url.setText(data.get(position));
            holder.delete.setTag(position);
            holder.delete.setOnClickListener(deleteListener);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mActivity.getLayoutInflater().inflate(layout, parent, false);
            return new UrlHolder(view);
        }

        private class UrlHolder extends RecyclerView.ViewHolder {

            private final TextView url;
            private final ImageView delete;

            public UrlHolder(View view) {
                super(view);
                url = (TextView) view.findViewById(R.id.url);
                delete = (ImageView) view.findViewById(R.id.delete);
            }
        }

        private OnClickListener deleteListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int)v.getTag();
                mDatabaseManager.removeAdblockUrl(data.get(position).hashCode());
                AdBlock.remove(data.get(position));
                data.remove(position);
                notifyItemRemoved(position);
            }
        };


        @Override
        public int getItemCount() {
            return (data != null) ? data.size() : 0;
        }
    }

}
