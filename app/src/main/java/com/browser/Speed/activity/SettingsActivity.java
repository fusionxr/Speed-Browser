/*
 * Copyright 2014 A.C.R. Development
 */
package com.browser.Speed.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.browser.Speed.constant.Constants;
//import com.browser.Speed.database.BookmarkActivity;
import com.browser.Speed.database.DatabaseManager;
import com.browser.Speed.preference.PreferenceManager;
import com.browser.Speed.R;
import com.browser.Speed.utils.Utils;


public class SettingsActivity extends ThemableSettingsActivity {

	private static final int API = android.os.Build.VERSION.SDK_INT;
	private PreferenceManager mPreferences;
	private Context mContext;
	private Activity mActivity;
	private RelativeLayout layoutFlash, layoutBlockAds, layoutImages, layoutEnableJS,
                           layoutOrbot, layoutSwipeTabs, layoutDrawer, layoutToolbar, layoutBookmarks;
	private CheckBox cbFlash, cbAdblock, cbImages, cbEnablejs, cbOrbot, cbSwipeTabs, cbDrawer, cbToolbar;
    private boolean mBottomDrawerEnabled;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		mContext = this;
		mActivity = this;
		init();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		finish();
		return true;
	}

	@SuppressLint("NewApi")
	public void init() {
		// set up ActionBar
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// mPreferences storage
		mPreferences = PreferenceManager.getInstance();
        mBottomDrawerEnabled = mPreferences.getBottomDrawerEnabled();

		// initialize UI
		layoutFlash = (RelativeLayout) findViewById(R.id.layoutFlash);
		layoutBlockAds = (RelativeLayout) findViewById(R.id.layoutAdBlock);
		layoutBlockAds.setEnabled(Constants.FULL_VERSION);
		layoutImages = (RelativeLayout) findViewById(R.id.layoutImages);
		layoutEnableJS = (RelativeLayout) findViewById(R.id.layoutEnableJS);
		layoutOrbot = (RelativeLayout) findViewById(R.id.layoutUseOrbot);
        layoutSwipeTabs = (RelativeLayout) findViewById(R.id.layoutSwipeTabs);
		layoutDrawer = (RelativeLayout) findViewById(R.id.layoutBottomDrawer);
        layoutToolbar = (RelativeLayout) findViewById(R.id.layoutBottomToolbar);
		layoutBookmarks = (RelativeLayout) findViewById(R.id.layoutBookmarks);

		layoutBookmarks.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(mContext, BookmarkActivity.class));
			}

		});

		if (API >= 19) {
			mPreferences.setFlashSupport(0);
		}
		int flashNum = mPreferences.getFlashSupport();
		boolean imagesBool = mPreferences.getBlockImagesEnabled();
		boolean enableJSBool = mPreferences.getJavaScriptEnabled();

		cbFlash = (CheckBox) findViewById(R.id.cbFlash);
		cbAdblock = (CheckBox) findViewById(R.id.cbAdblock);
		cbAdblock.setEnabled(Constants.FULL_VERSION);
		cbImages = (CheckBox) findViewById(R.id.cbImageBlock);
		cbEnablejs = (CheckBox) findViewById(R.id.cbJavascript);
		cbOrbot = (CheckBox) findViewById(R.id.cbOrbot);
        cbSwipeTabs = (CheckBox) findViewById(R.id.cbSwipeTabs);
		cbDrawer = (CheckBox) findViewById(R.id.cbBottomDrawer);
        cbToolbar = (CheckBox) findViewById(R.id.cbBottomToolbar);

		cbImages.setChecked(imagesBool);
		cbEnablejs.setChecked(enableJSBool);
		if (flashNum > 0) {
			cbFlash.setChecked(true);
		} else {
			cbFlash.setChecked(false);
		}
		cbAdblock.setChecked(mPreferences.getAdBlockEnabled());
		cbOrbot.setChecked(mPreferences.getUseProxy());
        cbSwipeTabs.setChecked(mPreferences.getSwipeTabsEnabled());
		cbDrawer.setChecked(mPreferences.getBottomDrawerEnabled());
        cbToolbar.setChecked(mPreferences.getBottomToolbarEnabled());
        if (mPreferences.getBottomDrawerEnabled()) layoutToolbar.setVisibility(View.VISIBLE);

        initCheckBox();
		clickListenerForCheckBoxes();

		RelativeLayout general = (RelativeLayout) findViewById(R.id.layoutGeneral);
		RelativeLayout display = (RelativeLayout) findViewById(R.id.layoutDisplay);
		RelativeLayout privacy = (RelativeLayout) findViewById(R.id.layoutPrivacy);
		RelativeLayout advanced = (RelativeLayout) findViewById(R.id.layoutAdvanced);
		RelativeLayout about = (RelativeLayout) findViewById(R.id.layoutAbout);

		general(general);
		display(display);
		privacy(privacy);
		advanced(advanced);
		about(about);
	}

	public void clickListenerForCheckBoxes() {
		layoutFlash.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (API < 19) {
                    cbFlash.setChecked(!cbFlash.isChecked());
                } else {
                    Utils.createInformativeDialog(mContext,
                            getResources().getString(R.string.title_warning), getResources()
                                    .getString(R.string.dialog_adobe_dead));
                }
            }
        });
		layoutBlockAds.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                cbAdblock.setChecked(!cbAdblock.isChecked());
            }

        });
		layoutImages.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                cbImages.setChecked(!cbImages.isChecked());
            }

        });
		layoutEnableJS.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                cbEnablejs.setChecked(!cbEnablejs.isChecked());
            }

        });
		layoutOrbot.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (cbOrbot.isEnabled()) {
                    cbOrbot.setChecked(!cbOrbot.isChecked());
                } else Utils.showToast(mContext, getResources().getString(R.string.install_orbot));
            }
        });
        layoutSwipeTabs.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cbSwipeTabs.setChecked(!cbSwipeTabs.isChecked());
            }
        });
		layoutDrawer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cbDrawer.setChecked(!cbDrawer.isChecked());
            }
        });
        layoutToolbar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cbToolbar.setChecked(!cbToolbar.isChecked());
            }
        });
	}

	public void initCheckBox() {
		cbFlash.setEnabled(API < 19);
		cbFlash.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    getFlashChoice();
                } else mPreferences.setFlashSupport(0);

                boolean flashInstalled = false;
                try {
                    PackageManager pm = getPackageManager();
                    ApplicationInfo ai = pm.getApplicationInfo("com.adobe.flashplayer", 0);
                    if (ai != null) {
                        flashInstalled = true;
                    }
                } catch (NameNotFoundException e) {
                    flashInstalled = false;
                }
                if (!flashInstalled && isChecked) {
                    Utils.createInformativeDialog(SettingsActivity.this,
                            getResources().getString(R.string.title_warning), getResources()
                                    .getString(R.string.dialog_adobe_not_installed));
                    buttonView.setChecked(false);
                    mPreferences.setFlashSupport(0);

                } else if ((API >= 17) && isChecked) {
                    Utils.createInformativeDialog(SettingsActivity.this,
                            getResources().getString(R.string.title_warning), getResources()
                                    .getString(R.string.dialog_adobe_unsupported));
                }
            }
        });
		cbAdblock.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mPreferences.setAdBlockEnabled(isChecked);
			}

		});
		cbImages.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mPreferences.setBlockImagesEnabled(isChecked);
			}

		});
		cbEnablejs.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mPreferences.setJavaScriptEnabled(isChecked);
			}

		});
        cbSwipeTabs.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPreferences.setSwipeTabsEnabled(isChecked);
            }
        });
		cbDrawer.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mPreferences.setBottomDrawerEnabled(isChecked);
                if (isChecked) layoutToolbar.setVisibility(View.VISIBLE);
                else {
                    layoutToolbar.setVisibility(View.GONE);
                    cbToolbar.setChecked(false);
                }
                mPreferences.setRestartActivity(mBottomDrawerEnabled != isChecked);
			}
		});
        cbToolbar.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPreferences.setBottomToolbarEnabled(isChecked);
            }
        });
		//OrbotHelper oh = new OrbotHelper(this);
		//if (!oh.isOrbotInstalled()) {
			//orbot.setEnabled(false);
		//}

		//orbot.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			//@Override
			//public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				//mPreferences.setUseProxy(isChecked);

			//}

		//});

	}

	private void getFlashChoice() {
		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
		builder.setTitle(mContext.getResources().getString(R.string.title_flash));
		builder.setMessage(getResources().getString(R.string.flash))
				.setCancelable(true)
				.setPositiveButton(getResources().getString(R.string.action_manual),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								mPreferences.setFlashSupport(1);
							}
						})
				.setNegativeButton(getResources().getString(R.string.action_auto),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								mPreferences.setFlashSupport(2);
							}
						}).setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                mPreferences.setFlashSupport(0);
            }

				});
		AlertDialog alert = builder.create();
        alert.show();
	}

	public void agentPicker() {
		final AlertDialog.Builder agentStringPicker = new AlertDialog.Builder(mActivity);

		agentStringPicker.setTitle(getResources().getString(R.string.title_user_agent));
		final EditText getAgent = new EditText(this);
		getAgent.append(mPreferences.getUserAgentString(""));
		agentStringPicker.setView(getAgent);
		agentStringPicker.setPositiveButton(getResources().getString(R.string.action_ok),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String text = getAgent.getText().toString();
						mPreferences.setUserAgentString(text);
						getAgent.setText(getResources().getString(R.string.agent_custom));
					}
				});
		agentStringPicker.show();
	}

	public void general(RelativeLayout view) {
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(mContext, GeneralSettingsActivity.class));
			}
		});
	}

	public void display(RelativeLayout view) {
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(mContext, DisplaySettingsActivity.class));
			}
		});
	}

	public void privacy(RelativeLayout view) {
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(mContext, PrivacySettingsActivity.class));
			}

		});
	}

	public void advanced(RelativeLayout view) {
		view.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, AdvancedSettingsActivity.class));}

		});
	}

	public void about(RelativeLayout view) {
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(mContext, AboutSettingsActivity.class));
			}
		});
	}

}
