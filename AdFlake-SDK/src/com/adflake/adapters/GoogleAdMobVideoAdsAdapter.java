package com.adflake.adapters;

import android.app.Activity;
import android.util.Log;

import com.adflake.AdFlakeLayout;
import com.adflake.obj.Ration;
import com.adflake.util.AdFlakeUtil;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class GoogleAdMobVideoAdsAdapter extends AdFlakeAdapter
{
	private InterstitialAd _interstitial;

	/**
	 * Instantiates a new google ad mob ads adapter.
	 * 
	 * @param adFlakeLayout
	 *            the ad flake layout
	 * @param ration
	 *            the ration
	 */
	public GoogleAdMobVideoAdsAdapter(AdFlakeLayout adFlakeLayout, Ration ration)
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

		// Create the interstitial.
		_interstitial = new InterstitialAd(activity);
		_interstitial.setAdUnitId(_ration.key);
		_interstitial.setAdListener(new AdListener()
		{
			@Override
			public void onAdClosed()
			{
				Log.d(AdFlakeUtil.ADFLAKE, "AdMob Video onAdClosed");
				super.onAdClosed();

				AdFlakeLayout adFlakeLayout = _adFlakeLayoutReference.get();

				if (adFlakeLayout == null)
					return;

				adFlakeLayout.adapterDidFinishVideoAd(GoogleAdMobVideoAdsAdapter.this, true);
			}

			@Override
			public void onAdFailedToLoad(int errorCode)
			{
				super.onAdFailedToLoad(errorCode);

				AdFlakeLayout adFlakeLayout = _adFlakeLayoutReference.get();

				if (adFlakeLayout == null)
					return;

				adFlakeLayout.adapterDidFailToReceiveVideoAdWithError(GoogleAdMobVideoAdsAdapter.this, "Google Ads Error " + errorCode);
			}

			@Override
			public void onAdLeftApplication()
			{
				Log.d(AdFlakeUtil.ADFLAKE, "AdMob Video onAdLeftApplication");
				super.onAdLeftApplication();
			}

			@Override
			public void onAdLoaded()
			{
				Log.d(AdFlakeUtil.ADFLAKE, "AdMob Video onAdLoaded");
				super.onAdLoaded();

				AdFlakeLayout adFlakeLayout = _adFlakeLayoutReference.get();

				if (adFlakeLayout == null)
					return;

				if (_interstitial.isLoaded())
				{
					adFlakeLayout.adapterDidReceiveVideoAd(GoogleAdMobVideoAdsAdapter.this);
				}
				else
				{
					adFlakeLayout.adapterDidFailToReceiveVideoAdWithError(GoogleAdMobVideoAdsAdapter.this, "Google Ads Error <UNDEFINED>");
				}
			}

			@Override
			public void onAdOpened()
			{
				Log.d(AdFlakeUtil.ADFLAKE, "AdMob Video onAdOpened");
				super.onAdOpened();
			}
		});

		// Create ad request.
		AdRequest adRequest = new AdRequest.Builder().build();

		// Begin loading your interstitial.
		_interstitial.loadAd(adRequest);
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
		super.willDestroy();
	}

	@Override
	public void playLoadedVideoAd()
	{
		super.playLoadedVideoAd();

		AdFlakeLayout adFlakeLayout = _adFlakeLayoutReference.get();

		if (adFlakeLayout == null)
			return;

		if (_interstitial.isLoaded())
		{
			_interstitial.show();
		}
	}

}
