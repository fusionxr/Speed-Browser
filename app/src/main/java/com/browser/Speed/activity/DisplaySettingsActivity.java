
package com.browser.Speed.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.browser.Speed.preference.PreferenceManager;
import com.browser.Speed.R;

public class DisplaySettingsActivity extends ThemableSettingsActivity {

	// mPreferences variables
    private Context mContext;
	private PreferenceManager mPreferences;
    private TextView mRenderText;
	private CheckBox cbHideStatusBar, cbStackTabsTop, cbFullScreen, cbWideViewPort, cbOverView, cbTextReflow, cbDarkTheme;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.display_settings);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		mPreferences = PreferenceManager.getInstance();
        mContext = this;
		initialize();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		finish();
		return true;
	}

	private void initialize() {

		RelativeLayout rHideStatusBar, rStackTabsTop, rFullScreen, rDarkTheme, rWideViewPort, rOverView, rTextReflow, rTextSize;
        LinearLayout rRenderMode ;
        LayoutClickListener clickListener = new LayoutClickListener();
		CheckBoxToggleListener toggleListener = new CheckBoxToggleListener();

		rHideStatusBar = (RelativeLayout) findViewById(R.id.rHideStatusBar);
		rStackTabsTop = (RelativeLayout) findViewById(R.id.rStackTabsTop);
		rFullScreen = (RelativeLayout) findViewById(R.id.rFullScreen);
		rWideViewPort = (RelativeLayout) findViewById(R.id.rWideViewPort);
		rOverView = (RelativeLayout) findViewById(R.id.rOverView);
		rTextReflow = (RelativeLayout) findViewById(R.id.rTextReflow);
		rTextSize = (RelativeLayout) findViewById(R.id.rTextSize);
		rRenderMode = (LinearLayout) findViewById(R.id.layoutRendering);
		rDarkTheme = (RelativeLayout) findViewById(R.id.rDarkTheme);
		
		rHideStatusBar.setOnClickListener(clickListener);
        rStackTabsTop.setOnClickListener(clickListener);
		rFullScreen.setOnClickListener(clickListener);
		rWideViewPort.setOnClickListener(clickListener);
		rOverView.setOnClickListener(clickListener);
		rTextReflow.setOnClickListener(clickListener);
		rTextSize.setOnClickListener(clickListener);
		rRenderMode.setOnClickListener(clickListener);
		rDarkTheme.setOnClickListener(clickListener);

		cbHideStatusBar = (CheckBox) findViewById(R.id.cbHideStatusBar);
        cbStackTabsTop = (CheckBox) findViewById(R.id.cbStackTabsTop);
		cbFullScreen = (CheckBox) findViewById(R.id.cbFullScreen);
		cbWideViewPort = (CheckBox) findViewById(R.id.cbWideViewPort);
		cbOverView = (CheckBox) findViewById(R.id.cbOverView);
		cbTextReflow = (CheckBox) findViewById(R.id.cbTextReflow);
		cbDarkTheme = (CheckBox) findViewById(R.id.cbDarkTheme);

		cbHideStatusBar.setChecked(mPreferences.getHideStatusBarEnabled());
        cbStackTabsTop.setChecked(mPreferences.getStackTabsTop());
		cbFullScreen.setChecked(mPreferences.getFullScreenEnabled());
		cbWideViewPort.setChecked(mPreferences.getUseWideViewportEnabled());
		cbOverView.setChecked(mPreferences.getOverviewModeEnabled());
		cbTextReflow.setChecked(mPreferences.getTextReflowEnabled());
		cbDarkTheme.setChecked(mPreferences.getUseDarkTheme());

		cbHideStatusBar.setOnCheckedChangeListener(toggleListener);
        cbStackTabsTop.setOnCheckedChangeListener(toggleListener);
		cbFullScreen.setOnCheckedChangeListener(toggleListener);
		cbWideViewPort.setOnCheckedChangeListener(toggleListener);
		cbOverView.setOnCheckedChangeListener(toggleListener);
		cbTextReflow.setOnCheckedChangeListener(toggleListener);
		cbDarkTheme.setOnCheckedChangeListener(toggleListener);

        mRenderText = (TextView) findViewById(R.id.renderText);
        switch (mPreferences.getRenderingMode()) {
            case 0:
                mRenderText.setText(mContext.getString(R.string.name_normal));
                break;
            case 1:
                mRenderText.setText(mContext.getString(R.string.name_inverted));
                break;
            case 2:
                mRenderText.setText(mContext.getString(R.string.name_grayscale));
                break;
            case 3:
                mRenderText.setText(mContext.getString(R.string.name_inverted_grayscale));
                break;
        }
	}

	private class LayoutClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.rHideStatusBar:
					cbHideStatusBar.setChecked(!cbHideStatusBar.isChecked());
					break;
                case R.id.rStackTabsTop:
                    cbStackTabsTop.setChecked(!cbStackTabsTop.isChecked());
                    break;
				case R.id.rFullScreen:
					cbFullScreen.setChecked(!cbFullScreen.isChecked());
					break;
				case R.id.rWideViewPort:
					cbWideViewPort.setChecked(!cbWideViewPort.isChecked());
					break;
				case R.id.rOverView:
					cbOverView.setChecked(!cbOverView.isChecked());
					break;
				case R.id.rTextReflow:
					cbTextReflow.setChecked(!cbTextReflow.isChecked());
					break;
				case R.id.rTextSize:
					textSizePicker();
					break;
				case R.id.layoutRendering:
					renderPicker();
					break;
				case R.id.rDarkTheme:
					cbDarkTheme.setChecked(!cbDarkTheme.isChecked());
					break;
			}
		}

	}

	private class CheckBoxToggleListener implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			switch (buttonView.getId()) {
				case R.id.cbHideStatusBar:
					mPreferences.setHideStatusBarEnabled(isChecked);
					break;
                case R.id.cbStackTabsTop:
                    mPreferences.setStackTabsTop(isChecked);
                    break;
				case R.id.cbFullScreen:
					mPreferences.setFullScreenEnabled(isChecked);
					break;
				case R.id.cbWideViewPort:
					mPreferences.setUseWideViewportEnabled(isChecked);
					break;
				case R.id.cbOverView:
					mPreferences.setOverviewModeEnabled(isChecked);
					break;
				case R.id.cbTextReflow:
					mPreferences.setTextReflowEnabled(isChecked);
					break;
				case R.id.cbDarkTheme:
					mPreferences.setUseDarkTheme(isChecked);
					mPreferences.setRestartActivity(true);
					restart();
					break;
			}
		}

	}

	private void textSizePicker() {
		AlertDialog.Builder picker = new AlertDialog.Builder(DisplaySettingsActivity.this);
		picker.setTitle(getResources().getString(R.string.title_text_size));

		int n = mPreferences.getTextSize();

		picker.setSingleChoiceItems(R.array.text_size, n - 1,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which + 1) {
							case 1:
                                mPreferences.setTextSize(200);
								break;
							case 2:
                                mPreferences.setTextSize(150);
								break;
							case 3:
                                mPreferences.setTextSize(100);
								break;
							case 4:
                                mPreferences.setTextSize(75);
								break;
							case 5:
                                mPreferences.setTextSize(50);
								break;
						}
					}
				});
		picker.setNeutralButton(getResources().getString(R.string.action_ok),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});
		picker.show();
	}

	private void renderPicker() {

		AlertDialog.Builder picker = new AlertDialog.Builder(mContext);
		picker.setTitle(getResources().getString(R.string.rendering_mode));
		CharSequence[] chars = { mContext.getString(R.string.name_normal),
				mContext.getString(R.string.name_inverted),
				mContext.getString(R.string.name_grayscale),
				mContext.getString(R.string.name_inverted_grayscale) };

		int n = mPreferences.getRenderingMode();

		picker.setSingleChoiceItems(chars, n, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				mPreferences.setRenderingMode(which);
				switch (which) {
					case 0:
						mRenderText.setText(mContext.getString(R.string.name_normal));
						break;
					case 1:
						mRenderText.setText(mContext.getString(R.string.name_inverted));
						break;
					case 2:
						mRenderText.setText(mContext.getString(R.string.name_grayscale));
						break;
					case 3:
						mRenderText.setText(mContext.getString(R.string.name_inverted_grayscale));
						break;
				}
			}
		});
		picker.setNeutralButton(getResources().getString(R.string.action_ok),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});
		picker.show();
	}
}
