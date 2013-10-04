/**
 * GoogleAdMobAdsAdapter.java (AdFlakeSDK-Android)
 *
 * Copyright © 2013 MADE GmbH - All Rights Reserved.
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

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.adflake.*;
import com.adflake.AdFlakeLayout.PushAdViewRunnable;
import com.adflake.obj.Ration;
import com.adflake.util.AdFlakeUtil;
import com.google.ads.*;
import com.google.ads.AdRequest.ErrorCode;

/**
 * The Class GoogleAdMobAdsAdapter.
 */
public class GoogleAdMobAdsAdapter extends AdFlakeAdapter implements AdListener
{
	private AdView	adView;

	/**
	 * Instantiates a new google ad mob ads adapter.
	 *
	 * @param adFlakeLayout the ad flake layout
	 * @param ration the ration
	 */
	public GoogleAdMobAdsAdapter(AdFlakeLayout adFlakeLayout, Ration ration)
	{
		super(adFlakeLayout, ration);
	}

	/**
	 * Gender for ad flake targeting.
	 *
	 * @return the ad request. gender
	 */
	protected AdRequest.Gender genderForAdFlakeTargeting()
	{
		switch (AdFlakeTargeting.getGender())
		{
			case MALE:
				return AdRequest.Gender.MALE;
			case FEMALE:
				return AdRequest.Gender.FEMALE;
			default:
				return null;
		}
	}

	/* (non-Javadoc)
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

		adView = new AdView(activity, AdSize.BANNER, _ration.key);

		adView.setAdListener(this);
		adView.loadAd(requestForAdFlakeLayout(adFlakeLayout));
	}

	/* (non-Javadoc)
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
	 * @param message the message
	 */
	protected void log(String message)
	{
		Log.d(AdFlakeUtil.ADFLAKE, "GoogleAdapter " + message);
	}

	/**
	 * Request for ad flake layout.
	 *
	 * @param layout the layout
	 * @return the ad request
	 */
	protected AdRequest requestForAdFlakeLayout(AdFlakeLayout layout)
	{
		AdRequest result = new AdRequest();

		if (AdFlakeTargeting.getTestMode())
		{
			Activity activity = layout.activityReference.get();
			if (activity != null)
			{
				Context context = activity.getApplicationContext();
				String deviceId = AdFlakeUtil.getEncodedDeviceId(context);
				result.addTestDevice(deviceId);
			}
		}
		result.setGender(genderForAdFlakeTargeting());
		result.setBirthday(AdFlakeTargeting.getBirthDate());

		if (layout.extra.locationOn == 1)
		{
			result.setLocation(layout.adFlakeManager.location);
		}

		result.setKeywords(AdFlakeTargeting.getKeywordSet());
		return result;
	}

	/* (non-Javadoc)
	 * @see com.google.ads.AdListener#onDismissScreen(com.google.ads.Ad)
	 */
	@Override
	public void onDismissScreen(Ad arg0)
	{
	}

	/* (non-Javadoc)
	 * @see com.google.ads.AdListener#onFailedToReceiveAd(com.google.ads.Ad, com.google.ads.AdRequest.ErrorCode)
	 */
	@Override
	public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1)
	{
		log("failure (" + arg1 + ")");

		arg0.setAdListener(null);

		AdFlakeLayout adFlakeLayout = _adFlakeLayoutReference.get();

		if (adFlakeLayout == null)
		{
			return;
		}

		adFlakeLayout.rollover();
	}

	/* (non-Javadoc)
	 * @see com.google.ads.AdListener#onLeaveApplication(com.google.ads.Ad)
	 */
	@Override
	public void onLeaveApplication(Ad arg0)
	{
	}

	/* (non-Javadoc)
	 * @see com.google.ads.AdListener#onPresentScreen(com.google.ads.Ad)
	 */
	@Override
	public void onPresentScreen(Ad arg0)
	{
	}

	/* (non-Javadoc)
	 * @see com.google.ads.AdListener#onReceiveAd(com.google.ads.Ad)
	 */
	@Override
	public void onReceiveAd(Ad arg0)
	{
		log("success");

		AdFlakeLayout adFlakeLayout = _adFlakeLayoutReference.get();

		if (adFlakeLayout == null)
		{
			return;
		}
		if (!(arg0 instanceof AdView))
		{
			log("invalid AdView");
			return;
		}

		AdView adView = (AdView) arg0;

		adFlakeLayout.adFlakeManager.resetRollover();
		adFlakeLayout.handler.post(new PushAdViewRunnable(adFlakeLayout, adView));
		adFlakeLayout.rotateThreadedDelayed();
	}
}
