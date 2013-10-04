/**
 * MillennialAdapter.java (AdFlakeSDK-Android)
 *
 * Copyright © 2013 MADE GmbH - All Rights Reserved.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * unless otherwise noted in the License section of this document header.
 *
 * @file MillennialAdapter.java
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

import android.text.TextUtils;
import android.util.Log;

import com.adflake.AdFlakeLayout;
import com.adflake.AdFlakeTargeting;
import com.adflake.AdFlakeTargeting.Gender;
import com.adflake.obj.Extra;
import com.adflake.obj.Ration;
import com.adflake.util.AdFlakeUtil;
import com.millennialmedia.android.MMAd;
import com.millennialmedia.android.MMAdView;
import com.millennialmedia.android.MMException;
import com.millennialmedia.android.MMRequest;
import com.millennialmedia.android.MMSDK;
import com.millennialmedia.android.RequestListener;

/**
 * The Class MillennialAdapter.
 */
public class MillennialAdapter extends AdFlakeAdapter implements RequestListener
{

	/**
	 * Instantiates a new millennial adapter.
	 * 
	 * @param adFlakeLayout
	 *            the ad flake layout
	 * @param ration
	 *            the ration
	 */
	public MillennialAdapter(AdFlakeLayout adFlakeLayout, Ration ration)
	{
		super(adFlakeLayout, ration);
	}

	private static boolean	isInitialized	= false;

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

		if (isInitialized == false)
		{
			MMSDK.initialize(adFlakeLayout.getContext());
			isInitialized = true;
		}

		MMAdView adView = new MMAdView(adFlakeLayout.getContext());
		adView.setApid(_ration.key);

		MMRequest request = new MMRequest();
		adView.setMMRequest(request);

		// Sets the id to preserve your ad on configuration changes.
		adView.setId(MMSDK.getDefaultAdId());
		adView.setHorizontalScrollBarEnabled(false);
		adView.setVerticalScrollBarEnabled(false);
		adView.setListener(this);

		Extra extra = adFlakeLayout.extra;
		if (extra.locationOn == 1 && adFlakeLayout.adFlakeManager.location != null)
		{
			MMRequest.setUserLocation(adFlakeLayout.adFlakeManager.location);
		}

		final AdFlakeTargeting.Gender gender = AdFlakeTargeting.getGender();
		if (gender == Gender.MALE)
		{
			request.setGender(MMRequest.GENDER_MALE);
		}
		else if (gender == Gender.FEMALE)
		{
			request.setGender(MMRequest.GENDER_FEMALE);
		}

		final int age = AdFlakeTargeting.getAge();
		if (age != -1)
		{
			request.setAge(String.valueOf(age));
		}

		final String postalCode = AdFlakeTargeting.getPostalCode();
		if (!TextUtils.isEmpty(postalCode))
		{
			request.setZip(postalCode);
		}
		final String keywords = AdFlakeTargeting.getKeywordSet() != null ? TextUtils.join(",", AdFlakeTargeting.getKeywordSet()) : AdFlakeTargeting.getKeywords();
		if (!TextUtils.isEmpty(keywords))
		{
			request.setKeywords(keywords);
		}

		request.setVendor("adflake");

		/*
		 * int layoutWidth =
		 * (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
		 * BANNER_AD_WIDTH, adFlakeLayout.getResources().getDisplayMetrics());
		 * int layoutHeight =
		 * (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
		 * BANNER_AD_HEIGHT, adFlakeLayout.getResources().getDisplayMetrics());
		 * RelativeLayout.LayoutParams layoutParams = new
		 * RelativeLayout.LayoutParams(layoutWidth, layoutHeight); //This
		 * positions the banner.
		 * layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		 * layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		 * adView.setLayoutParams(layoutParams);
		 */

		adView.getAd();
	}

	/*
	 * (non-Javadoc)
	 * @see com.millennialmedia.android.RequestListener#MMAdOverlayClosed(com.
	 * millennialmedia.android.MMAd)
	 */
	@Override
	public void MMAdOverlayClosed(MMAd arg0)
	{
		Log.d(AdFlakeUtil.ADFLAKE, "MMAdOverlayClosed");
	}

	/*
	 * (non-Javadoc)
	 * @see com.millennialmedia.android.RequestListener#MMAdOverlayLaunched(com.
	 * millennialmedia.android.MMAd)
	 */
	@Override
	public void MMAdOverlayLaunched(MMAd arg0)
	{
		Log.d(AdFlakeUtil.ADFLAKE, "Millennial Ad Overlay Launched");
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.millennialmedia.android.RequestListener#MMAdRequestIsCaching(com.
	 * millennialmedia.android.MMAd)
	 */
	@Override
	public void MMAdRequestIsCaching(MMAd arg0)
	{
		Log.d(AdFlakeUtil.ADFLAKE, "MMAdRequestIsCaching");
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.millennialmedia.android.RequestListener#onSingleTap(com.millennialmedia
	 * .android.MMAd)
	 */
	@Override
	public void onSingleTap(MMAd arg0)
	{
		Log.d(AdFlakeUtil.ADFLAKE, "Millennial Ad Clicked to overlay");
	}

	/*
	 * (non-Javadoc)
	 * @see com.millennialmedia.android.RequestListener#requestCompleted(com.
	 * millennialmedia.android.MMAd)
	 */
	@Override
	public void requestCompleted(MMAd ad)
	{
		Log.d(AdFlakeUtil.ADFLAKE, "Millennial success");

		MMAdView adView = (MMAdView) ad;
		adView.setListener(null);

		AdFlakeLayout adFlakeLayout = _adFlakeLayoutReference.get();
		if (adFlakeLayout == null)
		{
			return;
		}

		adFlakeLayout.adapterDidReceiveAd(this, adView);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.millennialmedia.android.RequestListener#requestFailed(com.millennialmedia
	 * .android.MMAd, com.millennialmedia.android.MMException)
	 */
	@Override
	public void requestFailed(MMAd adView, MMException error)
	{
		Log.d(AdFlakeUtil.ADFLAKE, "Millennial failure");
		adView.setListener(null);

		AdFlakeLayout adFlakeLayout = _adFlakeLayoutReference.get();
		if (adFlakeLayout == null)
		{
			return;
		}

		adFlakeLayout.adapterDidFailToReceiveAdWithError(this, "Millenial failure=" + error);
	}
}
