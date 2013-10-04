/**
 * JumptapAdapter.java (AdFlakeSDK-Android)
 *
 * Copyright © 2013 MADE GmbH - All Rights Reserved.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * unless otherwise noted in the License section of this document header.
 *
 * @file JumptapAdapter.java
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

import android.util.Log;

import com.adflake.AdFlakeLayout;
import com.adflake.obj.Ration;
import com.adflake.util.AdFlakeUtil;
import com.jumptap.adtag.JtAdView;
import com.jumptap.adtag.JtAdViewListener;
import com.jumptap.adtag.JtAdWidgetSettings;
import com.jumptap.adtag.JtAdWidgetSettingsFactory;
import com.jumptap.adtag.utils.JtException;

/**
 * The Class JumptapAdapter.
 */
public class JumptapAdapter extends AdFlakeAdapter implements JtAdViewListener
{

	/**
	 * Instantiates a new jumptap adapter.
	 *
	 * @param adFlakeLayout the ad flake layout
	 * @param ration the ration
	 */
	public JumptapAdapter(AdFlakeLayout adFlakeLayout, Ration ration)
	{
		super(adFlakeLayout, ration);
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
		String publisherID = "";
		String siteID = "";
		String spotID = "";

		try
		{
			JSONObject json = new JSONObject(_ration.key);
			publisherID = json.getString("publisherID");
			siteID = json.getString("siteID");
			spotID = json.getString("spotID");
		}
		catch (JSONException exception)
		{
			exception.printStackTrace();
		}

		JtAdWidgetSettings settings = JtAdWidgetSettingsFactory.createWidgetSettings();
		settings.setPublisherId(publisherID);
		settings.setSpotId(spotID);
		settings.setSiteId(siteID);
		try
		{
			String packageName = adFlakeLayout.getContext().getPackageName();
			settings.setApplicationId(packageName);
			String version = adFlakeLayout.getContext().getPackageManager().getPackageInfo(packageName, 0).versionName;

			settings.setApplicationVersion(version);
		}
		catch (Exception exception)
		{
		}
		try
		{
			JtAdView adView = new JtAdView(adFlakeLayout.getContext(), settings);
			adView.setAdViewListener(this);
			adView.refreshAd();
		}
		catch (JtException exception)
		{
			exception.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see com.jumptap.adtag.JtAdViewListener#onNewAd(com.jumptap.adtag.JtAdView, int, java.lang.String)
	 */
	@Override
	public void onNewAd(JtAdView adView, int arg1, String arg2)
	{
		Log.d(AdFlakeUtil.ADFLAKE, "Jumptap success");

		adView.setAdViewListener(null);

		AdFlakeLayout adFlakeLayout = _adFlakeLayoutReference.get();
		if (adFlakeLayout == null)
			return;

		adFlakeLayout.adapterDidReceiveAd(this, adView);
	}

	/* (non-Javadoc)
	 * @see com.jumptap.adtag.JtAdViewListener#onNoAdFound(com.jumptap.adtag.JtAdView, int)
	 */
	@Override
	public void onNoAdFound(JtAdView arg0, int arg1)
	{
		onAdError(arg0, 0, 0);
	}

	/* (non-Javadoc)
	 * @see com.jumptap.adtag.JtAdViewListener#onAdError(com.jumptap.adtag.JtAdView, int, int)
	 */
	@Override
	public void onAdError(JtAdView adView, int error, int arg2)
	{
		Log.d(AdFlakeUtil.ADFLAKE, "Jumptap failure");
		adView.setAdViewListener(null);

		AdFlakeLayout adFlakeLayout = _adFlakeLayoutReference.get();
		if (adFlakeLayout == null)
			return;

		adFlakeLayout.adapterDidFailToReceiveAdWithError(this, "Jumptap failure=" + error);
	}

	/* (non-Javadoc)
	 * @see com.jumptap.adtag.JtAdViewListener#onBannerClicked(com.jumptap.adtag.JtAdView, int)
	 */
	@Override
	public void onBannerClicked(JtAdView arg0, int arg1)
	{
		Log.d(AdFlakeUtil.ADFLAKE, "Jumptap onBannerClicked");
	}

	/* (non-Javadoc)
	 * @see com.jumptap.adtag.JtAdViewListener#onBeginAdInteraction(com.jumptap.adtag.JtAdView, int)
	 */
	@Override
	public void onBeginAdInteraction(JtAdView arg0, int arg1)
	{
		Log.d(AdFlakeUtil.ADFLAKE, "Jumptap onBeginAdInteraction");
	}

	/* (non-Javadoc)
	 * @see com.jumptap.adtag.JtAdViewListener#onContract(com.jumptap.adtag.JtAdView, int)
	 */
	@Override
	public void onContract(JtAdView arg0, int arg1)
	{
		Log.d(AdFlakeUtil.ADFLAKE, "Jumptap onContract");
	}

	/* (non-Javadoc)
	 * @see com.jumptap.adtag.JtAdViewListener#onEndAdInteraction(com.jumptap.adtag.JtAdView, int)
	 */
	@Override
	public void onEndAdInteraction(JtAdView arg0, int arg1)
	{
		Log.d(AdFlakeUtil.ADFLAKE, "Jumptap onEndAdInteraction");
	}

	/* (non-Javadoc)
	 * @see com.jumptap.adtag.JtAdViewListener#onExpand(com.jumptap.adtag.JtAdView, int)
	 */
	@Override
	public void onExpand(JtAdView arg0, int arg1)
	{
		Log.d(AdFlakeUtil.ADFLAKE, "Jumptap onExpand");
	}

	/* (non-Javadoc)
	 * @see com.jumptap.adtag.JtAdViewListener#onFocusChange(com.jumptap.adtag.JtAdView, int, boolean)
	 */
	@Override
	public void onFocusChange(JtAdView arg0, int arg1, boolean arg2)
	{
		Log.d(AdFlakeUtil.ADFLAKE, "Jumptap onFocusChange");
	}

	/* (non-Javadoc)
	 * @see com.jumptap.adtag.JtAdViewListener#onHide(com.jumptap.adtag.JtAdView, int)
	 */
	@Override
	public void onHide(JtAdView arg0, int arg1)
	{
		Log.d(AdFlakeUtil.ADFLAKE, "Jumptap onHide");
	}

	/* (non-Javadoc)
	 * @see com.jumptap.adtag.JtAdViewListener#onInterstitialDismissed(com.jumptap.adtag.JtAdView, int)
	 */
	@Override
	public void onInterstitialDismissed(JtAdView arg0, int arg1)
	{
		Log.d(AdFlakeUtil.ADFLAKE, "Jumptap onInterstitialDismissed");
	}

	/* (non-Javadoc)
	 * @see com.jumptap.adtag.JtAdViewListener#onLaunchActivity(com.jumptap.adtag.JtAdView, int)
	 */
	@Override
	public void onLaunchActivity(JtAdView arg0, int arg1)
	{
		Log.d(AdFlakeUtil.ADFLAKE, "Jumptap onLaunchActivity");
	}

	/* (non-Javadoc)
	 * @see com.jumptap.adtag.JtAdViewListener#onReturnFromActivity(com.jumptap.adtag.JtAdView, int)
	 */
	@Override
	public void onReturnFromActivity(JtAdView arg0, int arg1)
	{
		Log.d(AdFlakeUtil.ADFLAKE, "Jumptap onReturnFromActivity");
	}

}
