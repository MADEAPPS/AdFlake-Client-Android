/**
 * MdotMAdapter.java (AdFlakeSDK-Android)
 *
 * Copyright © 2013 MADE GmbH - All Rights Reserved.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * unless otherwise noted in the License section of this document header.
 *
 * @file MdotMAdapter.java
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

import android.util.Log;
import android.view.View;

import com.adflake.AdFlakeLayout;
import com.adflake.AdFlakeTargeting;
import com.adflake.obj.Ration;
import com.adflake.util.AdFlakeUtil;
import com.mdotm.android.listener.MdotMAdEventListener;
import com.mdotm.android.model.MdotMAdRequest;
import com.mdotm.android.utils.MdotMAdSize;
import com.mdotm.android.view.MdotMAdView;

/**
 * The Class MdotMAdapter.
 */
public class MdotMAdapter extends AdFlakeAdapter implements MdotMAdEventListener
{
	private MdotMAdView	_adView;

	/**
	 * Instantiates a new mdot m adapter.
	 *
	 * @param adFlakeLayout the ad flake layout
	 * @param ration the ration
	 */
	public MdotMAdapter(AdFlakeLayout adFlakeLayout, Ration ration)
	{
		super(adFlakeLayout, ration);
		_adView = null;
	}

	/* (non-Javadoc)
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

		MdotMAdRequest request = new MdotMAdRequest();

		request.setAppKey(this._ration.key);
		request.setAdSize(MdotMAdSize.BANNER_320_50);
		request.setTestMode(AdFlakeTargeting.getTestMode() == true ? "1" : "0");
		request.setAdRefreshInterval(0);
		request.setEnableCaching(false);

		_adView = new MdotMAdView(adFlakeLayout.getContext());
		_adView.loadBannerAd(this, request);
	}

	/* (non-Javadoc)
	 * @see com.adflake.adapters.AdFlakeAdapter#willDestroy()
	 */
	@Override
	public void willDestroy()
	{
		if (_adView != null)
		{
			_adView.endAdSession();
		}

		super.willDestroy();
	}

	/* (non-Javadoc)
	 * @see com.mdotm.android.listener.MdotMAdEventListener#onReceiveBannerAd()
	 */
	@Override
	public void onReceiveBannerAd()
	{
		Log.d(AdFlakeUtil.ADFLAKE, "MdotM success");

		AdFlakeLayout adFlakeLayout = _adFlakeLayoutReference.get();
		if (adFlakeLayout == null)
		{
			return;
		}

		_adView.setVisibility(View.VISIBLE);

		adFlakeLayout.adapterDidReceiveAd(this, _adView);
	}

	/* (non-Javadoc)
	 * @see com.mdotm.android.listener.MdotMAdEventListener#onFailedToReceiveBannerAd()
	 */
	@Override
	public void onFailedToReceiveBannerAd()
	{
		Log.d(AdFlakeUtil.ADFLAKE, "MdotM failure");

		AdFlakeLayout adFlakeLayout = _adFlakeLayoutReference.get();

		if (adFlakeLayout == null)
			return;

		adFlakeLayout.adapterDidFailToReceiveAdWithError(this, "MdotM failture");
	}

	/* (non-Javadoc)
	 * @see com.mdotm.android.listener.MdotMAdEventListener#onFailedToReceiveInterstitialAd()
	 */
	@Override
	public void onFailedToReceiveInterstitialAd()
	{
		Log.d(AdFlakeUtil.ADFLAKE, "MdotM failure");

		AdFlakeLayout adFlakeLayout = _adFlakeLayoutReference.get();

		if (adFlakeLayout == null)
			return;

		adFlakeLayout.adapterDidFailToReceiveAdWithError(this, "MdotM failture");
	}

	/* (non-Javadoc)
	 * @see com.mdotm.android.listener.MdotMAdEventListener#onBannerAdClick()
	 */
	@Override
	public void onBannerAdClick()
	{
		Log.d(AdFlakeUtil.ADFLAKE, "MdotM click");
	}

	/* (non-Javadoc)
	 * @see com.mdotm.android.listener.MdotMAdEventListener#onInterstitialAdClick()
	 */
	@Override
	public void onInterstitialAdClick()
	{
		Log.d(AdFlakeUtil.ADFLAKE, "MdotM click");
	}

	/* (non-Javadoc)
	 * @see com.mdotm.android.listener.MdotMAdEventListener#onDismissScreen()
	 */
	@Override
	public void onDismissScreen()
	{
	}

	/* (non-Javadoc)
	 * @see com.mdotm.android.listener.MdotMAdEventListener#onInterstitialDismiss()
	 */
	@Override
	public void onInterstitialDismiss()
	{
	}

	/* (non-Javadoc)
	 * @see com.mdotm.android.listener.MdotMAdEventListener#onLeaveApplicationFromBanner()
	 */
	@Override
	public void onLeaveApplicationFromBanner()
	{
	}

	/* (non-Javadoc)
	 * @see com.mdotm.android.listener.MdotMAdEventListener#onLeaveApplicationFromInterstitial()
	 */
	@Override
	public void onLeaveApplicationFromInterstitial()
	{
	}

	/* (non-Javadoc)
	 * @see com.mdotm.android.listener.MdotMAdEventListener#onReceiveInterstitialAd()
	 */
	@Override
	public void onReceiveInterstitialAd()
	{
	}

	/* (non-Javadoc)
	 * @see com.mdotm.android.listener.MdotMAdEventListener#willShowInterstitial()
	 */
	@Override
	public void willShowInterstitial()
	{
	}

	/* (non-Javadoc)
	 * @see com.mdotm.android.listener.MdotMAdEventListener#didShowInterstitial()
	 */
	@Override
	public void didShowInterstitial()
	{
	}
}
