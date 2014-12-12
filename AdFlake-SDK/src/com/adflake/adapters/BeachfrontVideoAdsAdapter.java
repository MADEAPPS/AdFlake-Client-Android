package com.adflake.adapters;

import android.app.Activity;
import android.util.Log;

import com.adflake.AdFlakeLayout;
import com.adflake.obj.Ration;
import com.adflake.util.AdFlakeUtil;
import com.bfio.ad.BFIOErrorCode;
import com.bfio.ad.BFIOInterstitial;
import com.bfio.ad.BFIOInterstitial.InterstitialListener;
import com.bfio.ad.model.BFIOInterstitalAd;

public class BeachfrontVideoAdsAdapter extends AdFlakeAdapter implements InterstitialListener
{
	BFIOInterstitial _interstitial;
	BFIOInterstitalAd _ad;

	/**
	 * Instantiates a new google ad mob ads adapter.
	 * 
	 * @param adFlakeLayout
	 *            the ad flake layout
	 * @param ration
	 *            the ration
	 */
	public BeachfrontVideoAdsAdapter(AdFlakeLayout adFlakeLayout, Ration ration)
	{
		super(adFlakeLayout, ration);
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

		String appID = _ration.key;
		String zoneID = _ration.key2;

		appID = "feb130d1-c7be-4c71-b87c-5b94bc92326d";
		zoneID = "8b56902b-3c98-47ac-9e17-6e08067c74ca";

		_interstitial = new BFIOInterstitial(activity, this);
		_interstitial.requestInterstitial(appID, zoneID);
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
		_ad = null;
		super.willDestroy();
	}

	@Override
	public void playLoadedVideoAd()
	{
		super.playLoadedVideoAd();

		AdFlakeLayout adFlakeLayout = _adFlakeLayoutReference.get();

		if (adFlakeLayout == null)
			return;

		if (_ad == null || _interstitial == null)
		{
			adFlakeLayout.adapterDidFailToReceiveVideoAdWithError(this, "ad not available");
			return;
		}

		_interstitial.showInterstitial(_ad);
	}

	@Override
	public void onInterstitialClicked()
	{
		Log.d(AdFlakeUtil.ADFLAKE, "BeachFront onInterstitialClicked");
		AdFlakeLayout adFlakeLayout = _adFlakeLayoutReference.get();

		if (adFlakeLayout == null)
			return;
		
		adFlakeLayout.adapterDidFinishVideoAd(this, true);
	}

	@Override
	public void onInterstitialCompleted()
	{
		Log.d(AdFlakeUtil.ADFLAKE, "BeachFront onInterstitialCompleted");
		AdFlakeLayout adFlakeLayout = _adFlakeLayoutReference.get();

		if (adFlakeLayout == null)
			return;

		adFlakeLayout.adapterDidFinishVideoAd(this, true);
		_ad = null;
	}

	@Override
	public void onInterstitialDismissed()
	{
		Log.d(AdFlakeUtil.ADFLAKE, "BeachFront onInterstitialDismissed");
		AdFlakeLayout adFlakeLayout = _adFlakeLayoutReference.get();

		if (adFlakeLayout == null)
			return;

		adFlakeLayout.adapterDidFinishVideoAd(this, false);
		_ad = null;

	}

	@Override
	public void onInterstitialFailed(BFIOErrorCode arg0)
	{
		AdFlakeLayout adFlakeLayout = _adFlakeLayoutReference.get();

		if (adFlakeLayout == null)
			return;

		adFlakeLayout.adapterDidFailToReceiveVideoAdWithError(this, "ad failed ErrorCode=" + arg0);
	}

	@Override
	public void onInterstitialStarted()
	{
		Log.d(AdFlakeUtil.ADFLAKE, "BeachFront onInterstitialStarted");
	}

	@Override
	public void onReceiveInterstitial(BFIOInterstitalAd ad)
	{
		AdFlakeLayout adFlakeLayout = _adFlakeLayoutReference.get();

		if (adFlakeLayout == null)
			return;

		// If an Ad is available it will call back to
		_ad = ad;
		adFlakeLayout.adapterDidReceiveVideoAd(this);
	}
}
