/**
 * MobFoxAdapter.java (AdFlakeSDK-Android)
 *
 * Copyright © 2013 MADE GmbH - All Rights Reserved.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * unless otherwise noted in the License section of this document header.
 *
 * @file MobFoxAdapter.java
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
import com.adsdk.sdk.Ad;
import com.adsdk.sdk.AdListener;
import com.adsdk.sdk.banner.AdView;

/**
 * The Class MobFoxAdapter.
 */
public class MobFoxAdapter extends AdFlakeAdapter implements AdListener
{
	private AdView	_adView;

	/**
	 * Instantiates a new mob fox adapter.
	 *
	 * @param adFlakeLayout the ad flake layout
	 * @param ration the ration
	 */
	public MobFoxAdapter(AdFlakeLayout adFlakeLayout, Ration ration)
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
			return;

		String appKey = _ration.key;

		_adView = new AdView(adFlakeLayout.getContext(), "http://my.mobfox.com/request.php", appKey, adFlakeLayout.extra.locationOn == 1, false);
		_adView.setAdListener(this);

		adFlakeLayout.adapterDidReceiveAd(this, _adView);
	}

	/* (non-Javadoc)
	 * @see com.adflake.adapters.AdFlakeAdapter#willDestroy()
	 */
	@Override
	public void willDestroy()
	{
		_adView = null;

		super.willDestroy();
	}

	/* (non-Javadoc)
	 * @see com.adsdk.sdk.AdListener#adLoadSucceeded(com.adsdk.sdk.Ad)
	 */
	@Override
	public void adLoadSucceeded(Ad ad)
	{
		Log.d(AdFlakeUtil.ADFLAKE, "MobFox sucess");

		AdFlakeLayout adFlakeLayout = _adFlakeLayoutReference.get();
		if (adFlakeLayout == null)
			return;

		if (_adView == null)
			return;

		_adView.setAdListener(null);

		// NOTE: we have already pushed the subview, so don't do it again
	}

	/* (non-Javadoc)
	 * @see com.adsdk.sdk.AdListener#noAdFound()
	 */
	@Override
	public void noAdFound()
	{
		Log.d(AdFlakeUtil.ADFLAKE, "MobFox failure");

		AdFlakeLayout adFlakeLayout = _adFlakeLayoutReference.get();

		if (adFlakeLayout == null)
			return;

		if (_adView != null)
			_adView.setAdListener(null);

		adFlakeLayout.adapterDidFailToReceiveAdWithError(this, "MobFox failture");
	}

	/* (non-Javadoc)
	 * @see com.adsdk.sdk.AdListener#adClicked()
	 */
	@Override
	public void adClicked()
	{
		Log.d(AdFlakeUtil.ADFLAKE, "MobFox adClicked");
	}

	/* (non-Javadoc)
	 * @see com.adsdk.sdk.AdListener#adClosed(com.adsdk.sdk.Ad, boolean)
	 */
	@Override
	public void adClosed(Ad arg0, boolean arg1)
	{
		Log.d(AdFlakeUtil.ADFLAKE, "MobFox adClosed");

	}

	/* (non-Javadoc)
	 * @see com.adsdk.sdk.AdListener#adShown(com.adsdk.sdk.Ad, boolean)
	 */
	@Override
	public void adShown(Ad arg0, boolean arg1)
	{
		Log.d(AdFlakeUtil.ADFLAKE, "MobFox adShown");

	}

}
