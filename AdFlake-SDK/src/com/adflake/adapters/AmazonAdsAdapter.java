/**
 * AmazonAdsAdapter.java (AdFlakeSDK-Android)
 *
 * Copyright © 2013 MADE GmbH - All Rights Reserved.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * unless otherwise noted in the License section of this document header.
 *
 * @file AmazonAdsAdapter.java
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

package com.adflake.adapters;

import android.app.Activity;
import android.util.Log;

import com.adflake.AdFlakeLayout;
import com.adflake.AdFlakeTargeting;
import com.adflake.AdFlakeTargeting.Gender;
import com.adflake.obj.Ration;
import com.adflake.util.AdFlakeUtil;
import com.amazon.device.ads.AdError;
import com.amazon.device.ads.AdLayout;
import com.amazon.device.ads.AdListener;
import com.amazon.device.ads.AdProperties;
import com.amazon.device.ads.AdRegistration;
import com.amazon.device.ads.AdSize;
import com.amazon.device.ads.AdTargetingOptions;

/**
 * The Class AmazonAdsAdapter.
 */
public class AmazonAdsAdapter extends AdFlakeAdapter implements AdListener
{

	/**
	 * Instantiates a new amazon ads adapter.
	 * 
	 * @param adFlakeLayout
	 *            the ad flake layout
	 * @param ration
	 *            the ration
	 */
	public AmazonAdsAdapter(AdFlakeLayout adFlakeLayout, Ration ration)
	{
		super(adFlakeLayout, ration);
		_adView = null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.adflake.adapters.AdFlakeAdapter#handle()
	 */
	@Override
	public void handle()
	{
		AdFlakeLayout adFlakeLayout = _adFlakeLayoutReference.get();

		if (adFlakeLayout == null)
			return;

		Activity activity = adFlakeLayout.activityReference.get();
		if (activity == null)
			return;

		if (AdFlakeTargeting.getTestMode() == true)
		{
			// For debugging purposes enable logging, but disable for production
			// builds
			AdRegistration.enableLogging(true);
			// For debugging purposes flag all ad requests as tests, but set to
			// false for production builds
			AdRegistration.enableTesting(true);

			AdRegistration.setAppKey(TEST_API_KEY);
		}
		else
		{
			AdRegistration.setAppKey(_ration.key);
		}

		_adView = new AdLayout(activity, AdSize.SIZE_320x50);
		_adView.setListener(this);
		_adView.setLayoutParams(adFlakeLayout.getOptimalRelativeLayoutParams());

		// Load the ad with the appropriate ad targeting options.
		AdTargetingOptions adOptions = new AdTargetingOptions();
		adOptions.setAge(AdFlakeTargeting.getAge());
		if (AdFlakeTargeting.getGender() == Gender.FEMALE)
			adOptions.setGender(AdTargetingOptions.Gender.FEMALE);
		else if (AdFlakeTargeting.getGender() == Gender.MALE)
			adOptions.setGender(AdTargetingOptions.Gender.MALE);
		_adView.loadAd(adOptions);
	}

	/*
	 * (non-Javadoc)
	 * @see com.adflake.adapters.AdFlakeAdapter#willDestroy()
	 */
	@Override
	public void willDestroy()
	{
		_adView = null;
		super.willDestroy();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.amazon.device.ads.AdListener#onAdCollapsed(com.amazon.device.ads.
	 * AdLayout)
	 */
	@Override
	public void onAdCollapsed(AdLayout arg0)
	{
		Log.d(AdFlakeUtil.ADFLAKE, "Amazon onAdCollapsed");
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.amazon.device.ads.AdListener#onAdExpanded(com.amazon.device.ads.AdLayout
	 * )
	 */
	@Override
	public void onAdExpanded(AdLayout arg0)
	{
		Log.d(AdFlakeUtil.ADFLAKE, "Amazon onAdExpanded");
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.amazon.device.ads.AdListener#onAdFailedToLoad(com.amazon.device.ads
	 * .AdLayout, com.amazon.device.ads.AdError)
	 */
	@Override
	public void onAdFailedToLoad(AdLayout adview, AdError arg1)
	{
		Log.d(AdFlakeUtil.ADFLAKE, "Amazon failture");
		adview.setListener(null);

		AdFlakeLayout adFlakeLayout = _adFlakeLayoutReference.get();
		if (adFlakeLayout == null)
			return;

		adFlakeLayout.adapterDidFailToReceiveAdWithError(this, "ERROR: amazon code" + arg1);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.amazon.device.ads.AdListener#onAdLoaded(com.amazon.device.ads.AdLayout
	 * , com.amazon.device.ads.AdProperties)
	 */
	@Override
	public void onAdLoaded(AdLayout adview, AdProperties arg1)
	{
		adview.setListener(null);

		Log.d(AdFlakeUtil.ADFLAKE, "Amazon success");

		AdFlakeLayout adFlakeLayout = _adFlakeLayoutReference.get();
		if (adFlakeLayout == null)
			return;

		adFlakeLayout.adapterDidReceiveAd(this, adview);
	}

	private static final String	TEST_API_KEY	= "sample-app-v1_pub-2";
	private AdLayout			_adView;
}
