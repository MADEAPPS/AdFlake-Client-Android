/**
 * SampleActivity.java (AdFlakeSDK-Android)
 *
 * Copyright © 2013 MADE GmbH - All Rights Reserved.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * unless otherwise noted in the License section of this document header.
 *
 * @file SampleActivity.java
 * @copyright 2013 MADE GmbH. All rights reserved.
 * @section License
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.adflake.sampleapp;

import com.adflake.AdFlakeLayout;
import com.adflake.AdFlakeLayout.AdFlakeInterface;
import com.adflake.AdFlakeManager;
import com.adflake.AdFlakeTargeting;
import com.adflake.util.AdFlakeUtil;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.HashSet;

/**
 * The Class SampleActivity demonstrates the use of the AdFlakeLayout when used
 * in an XML environment or created programatically.
 */
public class SampleActivity extends Activity implements AdFlakeInterface
{
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
		LinearLayout layout = (LinearLayout) findViewById(R.id.layout_main);

		if (layout == null)
		{
			Log.e("AdFlake", "Couldn't find main layout!");
			return;
		}

		// These are density-independent pixel units, as defined in
		// http://developer.android.com/guide/practices/screens_support.html
		DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
		final float density = displayMetrics.density;
		final int width = (int) (AdFlakeUtil.BANNER_DEFAULT_WIDTH * density);
		final int height = (int) (AdFlakeUtil.BANNER_DEFAULT_HEIGHT * density);

		AdFlakeTargeting.setAge(23);
		AdFlakeTargeting.setGender(AdFlakeTargeting.Gender.MALE);
		String keywords[] = { "online", "games", "gaming" };
		AdFlakeTargeting.setKeywordSet(new HashSet<String>(Arrays.asList(keywords)));
		AdFlakeTargeting.setPostalCode("76137");
		AdFlakeTargeting.setCompanyName("MADE");

		// NOTE: we enable test mode, to always fetch ad config from the server
		AdFlakeTargeting.setTestMode(true);

		// Optional, will fetch new config if necessary after five minutes.
		AdFlakeManager.setConfigExpireTimeout(1000 * 60 * 5);

		// References AdFlakeLayout defined in the layout XML.
		AdFlakeLayout adFlakeLayout = (AdFlakeLayout) findViewById(R.id.adflake_layout);
		adFlakeLayout.setAdFlakeInterface(this);
		adFlakeLayout.setMaxWidth(width);
		adFlakeLayout.setMaxHeight(height);

		boolean enableMultipleAds = false;

		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

		if (enableMultipleAds)
		{
			// Instantiates AdFlakeLayout from code.
			// NOTE: Showing two ads on the same screen is for illustrative
			// purposes only.
			// You should check with ad networks on their specific policies.
			AdFlakeLayout adFlakeLayout2 = new AdFlakeLayout(this, "52457efc3a945c8936000002");
			adFlakeLayout2.setAdFlakeInterface(this);
			adFlakeLayout2.setMaxWidth(width);
			adFlakeLayout2.setMaxHeight(height);
			layout.addView(adFlakeLayout2, adFlakeLayout2.getOptimalRelativeLayoutParams());

			TextView textView = new TextView(this);
			textView.setText("AdFlakeLayout from code");
			layout.addView(textView, layoutParams);
			layout.invalidate();
		}

		_statusTextView = new TextView(this);
		_statusTextView.setText("...loading");
		layout.addView(_statusTextView, layoutParams);

		Button button = new Button(this);
		button.setText("Rollover Ad");
		button.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				AdFlakeLayout adFlakeLayout = (AdFlakeLayout) findViewById(R.id.adflake_layout);
				if (adFlakeLayout == null)
					return;

				adFlakeLayout.rollover();
			}
		});
		layout.addView(button, layoutParams);

		button = new Button(this);
		button.setText("Rotate Ad");
		button.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				AdFlakeLayout adFlakeLayout = (AdFlakeLayout) findViewById(R.id.adflake_layout);
				if (adFlakeLayout == null)
					return;

				adFlakeLayout.rotateAd();
			}
		});
		layout.addView(button, layoutParams);

		layout.setGravity(Gravity.CENTER_HORIZONTAL);
		layout.invalidate();
	}

	/*
	 * (non-Javadoc)
	 * @see com.adflake.AdFlakeLayout.AdFlakeInterface#adFlakeGeneric()
	 */
	public void adFlakeGeneric()
	{
		Log.e(AdFlakeUtil.ADFLAKE, "In adFlakeGeneric()");
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.adflake.AdFlakeLayout.AdFlakeInterface#adFlakeDidPushAdSubView(com
	 * .adflake.AdFlakeLayout)
	 */
	@Override
	public void adFlakeDidPushAdSubView(AdFlakeLayout layout)
	{
		Log.e(AdFlakeUtil.ADFLAKE, "In adFlakeDidPushAdSubView()");

		_statusTextView.setText("Pushed Ad of network:" + layout.activeRation.name);
	}

	/** The Status text view. */
	TextView	_statusTextView;
}
