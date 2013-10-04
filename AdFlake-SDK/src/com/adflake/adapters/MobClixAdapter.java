/**
 * MobClixAdapter.java (AdFlakeSDK-Android)
 *
 * Copyright © 2013 MADE GmbH - All Rights Reserved.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * unless otherwise noted in the License section of this document header.
 *
 * @file MobClixAdapter.java
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

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Log;

import com.adflake.AdFlakeLayout;
import com.adflake.AdFlakeTargeting;
import com.adflake.obj.Ration;
import com.adflake.util.AdFlakeUtil;
import com.mobclix.android.sdk.MobclixAdView;
import com.mobclix.android.sdk.MobclixAdViewListener;
import com.mobclix.android.sdk.MobclixMMABannerXLAdView;

/**
 * The Class MobClixAdapter.
 */
public class MobClixAdapter extends AdFlakeAdapter implements MobclixAdViewListener
{
	MobclixAdView	_adView;

	/**
	 * Instantiates a new mob clix adapter.
	 * 
	 * @param adFlakeLayout
	 *            the ad flake layout
	 * @param ration
	 *            the ration
	 */
	public MobClixAdapter(AdFlakeLayout adFlakeLayout, Ration ration)
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
		{
			return;
		}

		JSONObject json;
		String appID = "";
		try
		{
			json = new JSONObject(_ration.key);
			appID = json.getString("appID");
		}
		catch (JSONException exception)
		{
			exception.printStackTrace();
		}

		if (appID == null || appID.length() == 0)
		{
			Log.e(AdFlakeUtil.ADFLAKE, "MobClix: couldn't get APPID from AdFlake ration");
			return;
		}


		// Note: For banner ads, Mobclix provides two classes:
		// MobclixMMABannerXLAdView, which is a 320 x 50 banner ad, and
		// MobclixIABRectangleMAdView, which is a 300 x 250 banner ad.
		_adView = new MobclixMMABannerXLAdView(adFlakeLayout.getContext());

		_adView.addMobclixAdViewListener(this);
	}

	/*
	 * (non-Javadoc)
	 * @see com.adflake.adapters.AdFlakeAdapter#willDestroy()
	 */
	@Override
	public void willDestroy()
	{
		_adView.removeMobclixAdViewListener(this);
		_adView = null;
		super.willDestroy();
	}

	/*
	 * (non-Javadoc)
	 * @see com.mobclix.android.sdk.MobclixAdViewListener#keywords()
	 */
	@Override
	public String keywords()
	{
		String keywords = AdFlakeTargeting.getKeywordSet() != null ? TextUtils.join(",", AdFlakeTargeting.getKeywordSet()) : AdFlakeTargeting.getKeywords();

		return keywords;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.mobclix.android.sdk.MobclixAdViewListener#onAdClick(com.mobclix.android
	 * .sdk.MobclixAdView)
	 */
	@Override
	public void onAdClick(MobclixAdView arg0)
	{
		Log.d(AdFlakeUtil.ADFLAKE, "MobClix onAdClick");
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.mobclix.android.sdk.MobclixAdViewListener#onCustomAdTouchThrough(
	 * com.mobclix.android.sdk.MobclixAdView, java.lang.String)
	 */
	@Override
	public void onCustomAdTouchThrough(MobclixAdView arg0, String arg1)
	{
		Log.d(AdFlakeUtil.ADFLAKE, "MobClix onCustomAdTouchThrough");
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.mobclix.android.sdk.MobclixAdViewListener#onFailedLoad(com.mobclix
	 * .android.sdk.MobclixAdView, int)
	 */
	@Override
	public void onFailedLoad(MobclixAdView arg0, int error)
	{
		Log.d(AdFlakeUtil.ADFLAKE, "MobClix failure");

		_adView.removeMobclixAdViewListener(this);

		AdFlakeLayout adFlakeLayout = _adFlakeLayoutReference.get();
		if (adFlakeLayout == null)
			return;

		adFlakeLayout.adapterDidFailToReceiveAdWithError(this, "MobClix failure=" + error);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.mobclix.android.sdk.MobclixAdViewListener#onOpenAllocationLoad(com
	 * .mobclix.android.sdk.MobclixAdView, int)
	 */
	@Override
	public boolean onOpenAllocationLoad(MobclixAdView arg0, int arg1)
	{
		Log.d(AdFlakeUtil.ADFLAKE, "MobClix onOpenAllocationLoad");
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.mobclix.android.sdk.MobclixAdViewListener#onSuccessfulLoad(com.mobclix
	 * .android.sdk.MobclixAdView)
	 */
	@Override
	public void onSuccessfulLoad(MobclixAdView arg0)
	{
		Log.d(AdFlakeUtil.ADFLAKE, "MobClix success");

		_adView.removeMobclixAdViewListener(this);

		AdFlakeLayout adFlakeLayout = _adFlakeLayoutReference.get();
		if (adFlakeLayout == null)
			return;

		adFlakeLayout.adapterDidReceiveAd(this, _adView);
	}

	/*
	 * (non-Javadoc)
	 * @see com.mobclix.android.sdk.MobclixAdViewListener#query()
	 */
	@Override
	public String query()
	{
		return null;
	}

}
