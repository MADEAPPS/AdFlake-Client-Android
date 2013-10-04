/**
 * InMobiAdapter.java (AdFlakeSDK-Android)
 *
 * Copyright © 2013 MADE GmbH - All Rights Reserved.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * unless otherwise noted in the License section of this document header.
 *
 * @file InMobiAdapter.java
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

import com.adflake.AdFlakeLayout;
import com.adflake.AdFlakeTargeting;
import com.adflake.AdFlakeLayout.PushAdViewRunnable;
import com.adflake.AdFlakeTargeting.Gender;
import com.adflake.obj.Extra;
import com.adflake.obj.Ration;
import com.adflake.util.AdFlakeUtil;
import com.inmobi.androidsdk.IMAdListener;
import com.inmobi.androidsdk.IMAdRequest;
import com.inmobi.androidsdk.IMAdRequest.ErrorCode;
import com.inmobi.androidsdk.IMAdRequest.GenderType;
import com.inmobi.androidsdk.IMAdView;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * An adapter for the InMobi Android SDK. Note: The InMobi site Id is looked up
 * using ration.key
 */
public final class InMobiAdapter extends AdFlakeAdapter implements IMAdListener
{
	private Extra	extra	= null;

	/**
	 * Instantiates a new in mobi adapter.
	 *
	 * @param adFlakeLayout the ad flake layout
	 * @param ration the ration
	 */
	public InMobiAdapter(AdFlakeLayout adFlakeLayout, Ration ration)
	{
		super(adFlakeLayout, ration);
		extra = adFlakeLayout.extra;
	}

	/* (non-Javadoc)
	 * @see com.adflake.adapters.AdFlakeAdapter#handle()
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void handle()
	{
		AdFlakeLayout adFlakeLayout = _adFlakeLayoutReference.get();
		if (adFlakeLayout == null)
		{
			return;
		}

		Activity activity = adFlakeLayout.activityReference.get();
		if (activity == null)
		{
			return;
		}

		IMAdView adView = new IMAdView(activity, IMAdView.INMOBI_AD_UNIT_320X50, _ration.key);
		adView.setIMAdListener(this);

		IMAdRequest imAdRequest = new IMAdRequest();
		imAdRequest.setAge(AdFlakeTargeting.getAge());
		imAdRequest.setGender(this.getGender());
		imAdRequest.setLocationInquiryAllowed(this.isLocationInquiryAllowed());
		try
		{
			// NOTE: try/catch here in case they change this in a newer SDK
			// version
			imAdRequest.setTestMode(AdFlakeTargeting.getTestMode());
		}
		catch (Exception exception)
		{
		}

		final String keywords = AdFlakeTargeting.getKeywordSet() != null ? TextUtils.join(",", AdFlakeTargeting.getKeywordSet()) : AdFlakeTargeting.getKeywords();
		if (!TextUtils.isEmpty(keywords))
		{
			imAdRequest.setKeywords(keywords);
		}
		imAdRequest.setPostalCode(AdFlakeTargeting.getPostalCode());

		// Setting tp key based on InMobi's implementation of this adapter.
		Map<String, String> map = new HashMap<String, String>();
		map.put("tp", "c_adflake");
		imAdRequest.setRequestParams(map);

		// Set the auto refresh off.
		adView.setRefreshInterval(IMAdView.REFRESH_INTERVAL_OFF);
		adView.loadNewAd(imAdRequest);
	}

	/* (non-Javadoc)
	 * @see com.inmobi.androidsdk.IMAdListener#onAdRequestCompleted(com.inmobi.androidsdk.IMAdView)
	 */
	@Override
	public void onAdRequestCompleted(IMAdView adView)
	{
		Log.d(AdFlakeUtil.ADFLAKE, "InMobi success");

		AdFlakeLayout adFlakeLayout = _adFlakeLayoutReference.get();
		if (adFlakeLayout == null)
		{
			return;
		}

		adFlakeLayout.adFlakeManager.resetRollover();
		adFlakeLayout.handler.post(new PushAdViewRunnable(adFlakeLayout, adView));
		adFlakeLayout.rotateThreadedDelayed();
	}

	/* (non-Javadoc)
	 * @see com.inmobi.androidsdk.IMAdListener#onAdRequestFailed(com.inmobi.androidsdk.IMAdView, com.inmobi.androidsdk.IMAdRequest.ErrorCode)
	 */
	@Override
	public void onAdRequestFailed(IMAdView adView, ErrorCode errorCode)
	{
		Log.d(AdFlakeUtil.ADFLAKE, "InMobi failure (" + errorCode + ")");
		AdFlakeLayout adFlakeLayout = _adFlakeLayoutReference.get();
		if (adFlakeLayout == null)
		{
			return;
		}
		adFlakeLayout.rollover();
	}

	/* (non-Javadoc)
	 * @see com.inmobi.androidsdk.IMAdListener#onShowAdScreen(com.inmobi.androidsdk.IMAdView)
	 */
	@Override
	public void onShowAdScreen(IMAdView adView)
	{
	}

	/* (non-Javadoc)
	 * @see com.inmobi.androidsdk.IMAdListener#onDismissAdScreen(com.inmobi.androidsdk.IMAdView)
	 */
	@Override
	public void onDismissAdScreen(IMAdView adView)
	{
	}

	/**
	 * Gets the gender.
	 *
	 * @return the gender
	 */
	public GenderType getGender()
	{
		Gender gender = AdFlakeTargeting.getGender();
		if (Gender.MALE == gender)
		{
			return GenderType.MALE;
		}
		if (Gender.FEMALE == gender)
		{
			return GenderType.FEMALE;
		}
		return GenderType.NONE;
	}

	/**
	 * Checks if is location inquiry allowed.
	 *
	 * @return true, if is location inquiry allowed
	 */
	public boolean isLocationInquiryAllowed()
	{
		if (extra.locationOn == 1)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see com.inmobi.androidsdk.IMAdListener#onLeaveApplication(com.inmobi.androidsdk.IMAdView)
	 */
	@Override
	public void onLeaveApplication(IMAdView adView)
	{
	}
}
