/**
 * GoogleAdMobAdsAdapter.java (AdFlakeSDK-Android)
 *
 * Copyright � 2013 MADE GmbH - All Rights Reserved.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * unless otherwise noted in the License section of this document header.
 *
 * @file GoogleAdMobAdsAdapter.java
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

import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.adflake.*;
import com.adflake.AdFlakeLayout.PushAdViewRunnable;
import com.adflake.obj.Ration;
import com.adflake.util.AdFlakeUtil;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

/**
 * The Class GoogleAdMobAdsAdapter.
 */
public class GoogleAdMobAdsAdapter extends AdFlakeAdapter
{
	private AdView adView;

	/**
	 * Instantiates a new google ad mob ads adapter.
	 * 
	 * @param adFlakeLayout
	 *            the ad flake layout
	 * @param ration
	 *            the ration
	 */
	public GoogleAdMobAdsAdapter(AdFlakeLayout adFlakeLayout, Ration ration)
	{
		super(adFlakeLayout, ration);
	}

	/*
	 * (non-Javadoc)
	 * 
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

		adView = new AdView(activity);
		adView.setAdSize(AdSize.BANNER);
		adView.setAdUnitId(_ration.key);	
		adView.setAdListener(new AdListener()
		{
			@Override
			public void onAdClosed()
			{
				super.onAdClosed();
			}
			 
			@Override
			public void onAdFailedToLoad(int errorCode)
			{
				super.onAdFailedToLoad(errorCode);
				
				log("Google AdMob failure (" + errorCode + ")");

				adView.setAdListener(null);

				final AdFlakeLayout adFlakeLayout = _adFlakeLayoutReference.get();

				if (adFlakeLayout == null)
					return;

				adFlakeLayout.rollover();
			}
			
			@Override
			public void onAdLeftApplication()
			{
				super.onAdLeftApplication();
			}
			
			@Override
			public void onAdLoaded()
			{
				super.onAdLoaded();

				log("Google AdMob success");

				AdFlakeLayout adFlakeLayout = _adFlakeLayoutReference.get();

				if (adFlakeLayout == null)
					return;

				adFlakeLayout.adFlakeManager.resetRollover();
				adFlakeLayout.handler.post(new PushAdViewRunnable(adFlakeLayout, adView));
				adFlakeLayout.rotateThreadedDelayed();
			}
			
			@Override
			public void onAdOpened()
			{
				super.onAdOpened();
			}
		});
		adView.loadAd(requestForAdFlakeLayout(adFlakeLayout));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.adflake.adapters.AdFlakeAdapter#willDestroy()
	 */
	@Override
	public void willDestroy()
	{
		log("AdView will get destroyed");
		if (adView != null)
		{
			adView.destroy();
		}
	}

	/**
	 * Log.
	 * 
	 * @param message
	 *            the message
	 */
	protected void log(String message)
	{
		Log.d(AdFlakeUtil.ADFLAKE, "GoogleAdapter " + message);
	}

	/**
	 * Request for ad flake layout.
	 * 
	 * @param layout
	 *            the layout
	 * @return the ad request
	 */
	protected AdRequest requestForAdFlakeLayout(AdFlakeLayout layout)
	{
		final AdRequest.Builder builder = new AdRequest.Builder();

		if (AdFlakeTargeting.getTestMode())
		{
			Activity activity = layout.activityReference.get();
			if (activity != null)
			{
				Context context = activity.getApplicationContext();
				String deviceId = AdFlakeUtil.getEncodedDeviceId(context);
				builder.addTestDevice(deviceId);

				builder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
			}
		}

		// AdRequest result = builder.build();

		builder.setGender(genderForAdFlakeTargeting());
		builder.setBirthday(dateForAdFlakeTargeting());

		if (layout.extra.locationOn == 1)
		{
			builder.setLocation(layout.adFlakeManager.location);
		}


		// result.setKeywords(AdFlakeTargeting.getKeywordSet());
		
		final AdRequest request = builder.build();
		
		
		return request;
	}

	private Date dateForAdFlakeTargeting()
	{
		if (AdFlakeTargeting.getBirthDate() == null)
			return null;
		
		return AdFlakeTargeting.getBirthDate().getTime();
	}

	private int genderForAdFlakeTargeting()
	{
		switch (AdFlakeTargeting.getGender())
		{
			case MALE:
				return AdRequest.GENDER_MALE;
			case FEMALE:
				return AdRequest.GENDER_FEMALE;
			default:
				return AdRequest.GENDER_UNKNOWN;
		}
	}

}
