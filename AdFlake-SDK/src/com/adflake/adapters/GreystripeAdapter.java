/**
 * GreystripeAdapter.java (AdFlakeSDK-Android)
 *
 * Copyright © 2013 MADE GmbH - All Rights Reserved.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * unless otherwise noted in the License section of this document header.
 *
 * @file GreystripeAdapter.java
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
import com.greystripe.sdk.GSAd;
import com.greystripe.sdk.GSAdErrorCode;
import com.greystripe.sdk.GSAdListener;
import com.greystripe.sdk.GSMobileBannerAdView;

/**
 * The Class GreystripeAdapter.
 */
public class GreystripeAdapter extends AdFlakeAdapter implements GSAdListener
{

	/**
	 * Instantiates a new greystripe adapter.
	 * 
	 * @param adFlakeLayout
	 *            the ad flake layout
	 * @param ration
	 *            the ration
	 */
	public GreystripeAdapter(AdFlakeLayout adFlakeLayout, Ration ration)
	{
		super(adFlakeLayout, ration);
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

		String applicationID = _ration.key;
		GSMobileBannerAdView adView = new GSMobileBannerAdView(adFlakeLayout.getContext(), applicationID);
		adView.addListener(this);
		adView.refresh();
	}

	/*
	 * (non-Javadoc)
	 * @see com.greystripe.sdk.GSAdListener#onFetchedAd(com.greystripe.sdk.GSAd)
	 */
	@Override
	public void onFetchedAd(GSAd adView)
	{
		Log.d(AdFlakeUtil.ADFLAKE, "Greystripe success");

		adView.removeListener(this);

		AdFlakeLayout adFlakeLayout = _adFlakeLayoutReference.get();
		if (adFlakeLayout == null)
			return;

		adFlakeLayout.adapterDidReceiveAd(this, (GSMobileBannerAdView) adView);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.greystripe.sdk.GSAdListener#onFailedToFetchAd(com.greystripe.sdk.
	 * GSAd, com.greystripe.sdk.GSAdErrorCode)
	 */
	@Override
	public void onFailedToFetchAd(GSAd adView, GSAdErrorCode error)
	{
		Log.d(AdFlakeUtil.ADFLAKE, "Greystripe failure");
		adView.removeListener(null);

		AdFlakeLayout adFlakeLayout = _adFlakeLayoutReference.get();
		if (adFlakeLayout == null)
		{
			return;
		}

		adFlakeLayout.adapterDidFailToReceiveAdWithError(this, "Greystripe failure=" + error);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.greystripe.sdk.GSAdListener#onAdClickthrough(com.greystripe.sdk.GSAd)
	 */
	@Override
	public void onAdClickthrough(GSAd arg0)
	{
		Log.d(AdFlakeUtil.ADFLAKE, "Greystripe onAdClickthrough");
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.greystripe.sdk.GSAdListener#onAdCollapse(com.greystripe.sdk.GSAd)
	 */
	@Override
	public void onAdCollapse(GSAd arg0)
	{
		Log.d(AdFlakeUtil.ADFLAKE, "Greystripe onAdCollapse");
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.greystripe.sdk.GSAdListener#onAdDismissal(com.greystripe.sdk.GSAd)
	 */
	@Override
	public void onAdDismissal(GSAd arg0)
	{
		Log.d(AdFlakeUtil.ADFLAKE, "Greystripe onAdDismissal");
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.greystripe.sdk.GSAdListener#onAdExpansion(com.greystripe.sdk.GSAd)
	 */
	@Override
	public void onAdExpansion(GSAd arg0)
	{
		Log.d(AdFlakeUtil.ADFLAKE, "Greystripe onAdExpansion");
	}
}
