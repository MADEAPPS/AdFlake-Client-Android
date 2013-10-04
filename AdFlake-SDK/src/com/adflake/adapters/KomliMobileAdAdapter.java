/**
 * KomliMobileAdAdapter.java (AdFlakeSDK-Android)
 *
 * Copyright © 2013 MADE GmbH - All Rights Reserved.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * unless otherwise noted in the License section of this document header.
 *
 * @file KomliMobileAdAdapter.java
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

import com.adflake.AdFlakeLayout;
import com.adflake.obj.Ration;
import com.adflake.util.AdFlakeUtil;
import com.komlimobile.sdk.IInterstitialInterface;
import com.komlimobile.sdk.KomliMobileView;

/**
 * The KomliMobileAdAdapter provides Ad network integration for Komli Mobile.
 * 
 * @remarks The KomliMobileAdAdapter is not fully supported in AdFlake SDK 4.0.0
 *          on Android due to major limitations of the KomliMobile SDK on
 *          Android. It is not possible to set the ClientID (aka AppID,
 *          PublisherID) programatically. There is no known workaround
 *          available.
 */
public class KomliMobileAdAdapter extends AdFlakeAdapter implements IInterstitialInterface
{
	private KomliMobileView	_adView;

	/**
	 * Instantiates a new komli mobile ad adapter.
	 *
	 * @param adFlakeLayout the ad flake layout
	 * @param ration the ration
	 * @throws Exception the exception
	 */
	public KomliMobileAdAdapter(AdFlakeLayout adFlakeLayout, Ration ration) throws Exception
	{
		super(adFlakeLayout, ration);

		_adView = null;

		throw new Exception("KomliMobile is not supported on Android. Due to major limitations in their SDK.");
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

		_adView = new KomliMobileView(adFlakeLayout.getContext());
		_adView.setListener(this);
	}

	/* (non-Javadoc)
	 * @see com.komlimobile.sdk.IInterstitialInterface#OnAdClicked()
	 */
	@Override
	public void OnAdClicked()
	{
		Log.d(AdFlakeUtil.ADFLAKE, "KomliMobile ad clicked");
	}

	/* (non-Javadoc)
	 * @see com.komlimobile.sdk.IInterstitialInterface#OnAdClosed()
	 */
	@Override
	public void OnAdClosed()
	{
		Log.d(AdFlakeUtil.ADFLAKE, "KomliMobile ad closed");
	}

	/* (non-Javadoc)
	 * @see com.komlimobile.sdk.IInterstitialInterface#OnAdLoaded()
	 */
	@Override
	public void OnAdLoaded()
	{
		Log.d(AdFlakeUtil.ADFLAKE, "KomliMobile success");

		AdFlakeLayout adFlakeLayout = _adFlakeLayoutReference.get();
		if (adFlakeLayout == null)
		{
			return;
		}

		adFlakeLayout.adapterDidReceiveAd(this, _adView);
	}

	/* (non-Javadoc)
	 * @see com.komlimobile.sdk.IInterstitialInterface#OnAdFaileTODisplay()
	 */
	@Override
	public void OnAdFaileTODisplay()
	{
		this.onAdFaileToLoad();
	}

	/* (non-Javadoc)
	 * @see com.komlimobile.sdk.IInterstitialInterface#onAdFaileToLoad()
	 */
	@Override
	public void onAdFaileToLoad()
	{
		if (_adView == null)
			return;

		Log.d(AdFlakeUtil.ADFLAKE, "KomliMobile failure");

		_adView.setListener(null);

		AdFlakeLayout adFlakeLayout = _adFlakeLayoutReference.get();
		if (adFlakeLayout == null)
		{
			return;
		}
		adFlakeLayout.adapterDidFailToReceiveAdWithError(this, "KomliMobile failture");
	}

	/* (non-Javadoc)
	 * @see com.komlimobile.sdk.IInterstitialInterface#OnInterstitialAdErrorMessage(java.lang.String)
	 */
	@Override
	public void OnInterstitialAdErrorMessage(String arg0)
	{
		Log.d(AdFlakeUtil.ADFLAKE, "KomliMobile error:" + arg0);
	}

	/* (non-Javadoc)
	 * @see com.komlimobile.sdk.IInterstitialInterface#getAdSize()
	 */
	@Override
	public String getAdSize()
	{
		// ALL (default), T Ð text only, S Ð small ( 120 x 20 ),
		// M Ð medium (168 x 28 ), L Ð large ( 216 x 36 ),
		// XL ( 300 x 50 ), XXL (320 x 48 /320 x 52 ).
		return "XXL";
	}

	/* (non-Javadoc)
	 * @see com.komlimobile.sdk.IInterstitialInterface#getAdType()
	 */
	@Override
	public String getAdType()
	{
		return "text+picture";
	}

	/* (non-Javadoc)
	 * @see com.komlimobile.sdk.IInterstitialInterface#getRmsSupport()
	 */
	@Override
	public String getRmsSupport()
	{
		// A: Available
		// NA: Not Available
		return "NA";
	}

}
