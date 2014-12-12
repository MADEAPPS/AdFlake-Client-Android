package com.adflake.adapters;

import android.app.Activity;
import android.util.Log;

import com.adflake.AdFlakeLayout;
import com.adflake.obj.Ration;
import com.adflake.util.AdFlakeUtil;
import com.jirbo.adcolony.AdColony;
import com.jirbo.adcolony.AdColonyAd;
import com.jirbo.adcolony.AdColonyAdAvailabilityListener;
import com.jirbo.adcolony.AdColonyAdListener;
import com.jirbo.adcolony.AdColonyVideoAd;

public class AdColonyVideoAdsAdapter extends AdFlakeAdapter implements AdColonyAdAvailabilityListener, AdColonyAdListener
{
	AdColonyVideoAd _interstitial;

	/**
	 * Instantiates a new google ad mob ads adapter.
	 * 
	 * @param adFlakeLayout
	 *            the ad flake layout
	 * @param ration
	 *            the ration
	 */
	public AdColonyVideoAdsAdapter(AdFlakeLayout adFlakeLayout, Ration ration)
	{
		super(adFlakeLayout, ration);
	}

	static String _currentAppID = "";
	static String _currentZoneID = "";

	public static void prepareForRation(Ration ration, AdFlakeLayout adFlakeLayout)
	{
		Activity activity = adFlakeLayout.activityReference.get();

		if (activity == null)
			return;

		String appID = ration.key;
		String zoneID = ration.key2;

		if (_currentAppID == appID && _currentZoneID == zoneID)
			return;

		AdColony.configure(activity, "version:1.0,store:google", appID, zoneID);
		_currentAppID = appID;
		_currentZoneID = zoneID;
	}

	@Override
	public void handle()
	{
		AdFlakeLayout adFlakeLayout = _adFlakeLayoutReference.get();

		if (adFlakeLayout == null)
			return;

		Activity activity = adFlakeLayout.activityReference.get();
		if (activity == null)
			return;

		@SuppressWarnings("unused")
		String appID = _ration.key;
		String zoneID = _ration.key2;

		prepareForRation(_ration, adFlakeLayout);

		AdColony.addAdAvailabilityListener(this);

		_interstitial = new AdColonyVideoAd(zoneID);
		_interstitial.withListener(this);

		if (!_interstitial.isReady())
		{
			adFlakeLayout.adapterDidFailToReceiveVideoAdWithError(this, "ad not ready");
		}
		else
		{
			adFlakeLayout.adapterDidReceiveVideoAd(this);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.adflake.adapters.AdFlakeAdapter#willDestroy()
	 */
	@Override
	public void willDestroy()
	{
		_interstitial = null;
		AdColony.removeAdAvailabilityListener(this);
		super.willDestroy();
	}

	@Override
	public void playLoadedVideoAd()
	{
		super.playLoadedVideoAd();

		AdFlakeLayout adFlakeLayout = _adFlakeLayoutReference.get();

		if (adFlakeLayout == null)
			return;
		
		if (!_interstitial.isReady())
		{
			adFlakeLayout.adapterDidFailToReceiveVideoAdWithError(this, "ad not ready");
			return;			
		}

		_interstitial.show();
	}

	@Override
	public void onAdColonyAdAvailabilityChange(boolean arg0, String arg1)
	{
		Log.d(AdFlakeUtil.ADFLAKE, "AdColony onAdColonyAdAvailabilityChange=" + arg0 + " (" + arg1 + ")");
	}

	@Override
	public void onAdColonyAdAttemptFinished(AdColonyAd ad)
	{
		AdFlakeLayout adFlakeLayout = _adFlakeLayoutReference.get();

		if (adFlakeLayout == null)
			return;

		adFlakeLayout.adapterDidFinishVideoAd(this, !ad.skipped() && ad.shown() && !ad.noFill());

	}

	@Override
	public void onAdColonyAdStarted(AdColonyAd arg0)
	{
		// TODO Auto-generated method stub

	}

}
