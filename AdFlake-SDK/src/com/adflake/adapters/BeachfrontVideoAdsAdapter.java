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

public class BeachfrontVideoAdsAdapter extends AdFlakeAdapter
{
	private static BFIOInterstitial _interstitial;
	/**
	 * it seems that previous listeners are not unregistered, so we revert to a
	 * static instance.
	 */
	BFIOInterstitalAd _ad;

	public static BeachfrontVideoAdsAdapter currentAdapter;
	public static Activity currentActivity;

	private static InterstitialListener _listener = new InterstitialListener()
	{
		@Override
		public void onInterstitialClicked()
		{
			if (currentAdapter == null)
				return;

			Log.d(AdFlakeUtil.ADFLAKE, "BeachFront onInterstitialClicked");
			AdFlakeLayout adFlakeLayout = BeachfrontVideoAdsAdapter.currentAdapter._adFlakeLayoutReference.get();

			if (adFlakeLayout == null)
				return;

			adFlakeLayout.adapterDidFinishVideoAd(currentAdapter, true);
		}

		@Override
		public void onInterstitialCompleted()
		{
			if (currentAdapter == null)
				return;

			Log.d(AdFlakeUtil.ADFLAKE, "BeachFront onInterstitialCompleted");
			AdFlakeLayout adFlakeLayout = currentAdapter._adFlakeLayoutReference.get();

			if (adFlakeLayout == null)
				return;

			adFlakeLayout.adapterDidFinishVideoAd(currentAdapter, true);
			currentAdapter._ad = null;
		}

		@Override
		public void onInterstitialDismissed()
		{
			if (currentAdapter == null)
				return;

			Log.d(AdFlakeUtil.ADFLAKE, "BeachFront onInterstitialDismissed");
			AdFlakeLayout adFlakeLayout = currentAdapter._adFlakeLayoutReference.get();

			if (adFlakeLayout == null)
				return;

			adFlakeLayout.adapterDidFinishVideoAd(currentAdapter, false);
			currentAdapter._ad = null;
		}

		@Override
		public void onInterstitialFailed(BFIOErrorCode arg0)
		{
			if (currentAdapter == null)
				return;

			AdFlakeLayout adFlakeLayout = currentAdapter._adFlakeLayoutReference.get();

			if (adFlakeLayout == null)
				return;

			adFlakeLayout.adapterDidFailToReceiveVideoAdWithError(currentAdapter, "ad failed ErrorCode=" + arg0);
			currentAdapter._ad = null;
		}

		@Override
		public void onInterstitialStarted()
		{
			Log.d(AdFlakeUtil.ADFLAKE, "BeachFront onInterstitialStarted");
		}

		@Override
		public void onReceiveInterstitial(BFIOInterstitalAd ad)
		{
			if (currentAdapter == null)
				return;

			AdFlakeLayout adFlakeLayout = currentAdapter._adFlakeLayoutReference.get();

			if (adFlakeLayout == null)
				return;

			// If an Ad is available it will call back to
			currentAdapter._ad = ad;
			adFlakeLayout.adapterDidReceiveVideoAd(currentAdapter);
		}
	};

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

		if (_interstitial == null || BeachfrontVideoAdsAdapter.currentActivity != activity)
		{
			_interstitial = new BFIOInterstitial(activity, _listener);
			BeachfrontVideoAdsAdapter.currentActivity = activity;
		}
		BeachfrontVideoAdsAdapter.currentAdapter = this;
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
}
