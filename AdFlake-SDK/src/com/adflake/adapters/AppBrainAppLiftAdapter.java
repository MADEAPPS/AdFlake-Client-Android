package com.adflake.adapters;

import android.app.Activity;
import android.util.Log;

import com.adflake.AdFlakeLayout;
import com.adflake.AdFlakeTargeting;
import com.adflake.obj.Ration;
import com.adflake.util.AdFlakeUtil;
import com.appbrain.AppBrain;
import com.appbrain.AppBrainBanner;
import com.appbrain.BannerListener;

public class AppBrainAppLiftAdapter extends AdFlakeAdapter implements BannerListener
{
	private AppBrainBanner	_adView;

	public AppBrainAppLiftAdapter(AdFlakeLayout adFlakeLayout, Ration ration)
	{
		super(adFlakeLayout, ration);

		_adView = null;
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

		AppBrain.init(activity);

		if (AdFlakeTargeting.getTestMode() == true)
		{
		}

		_adView = new AppBrainBanner(adFlakeLayout.getContext());

		_adView.setBannerListener(this);
		_adView.requestAd();
	}

	@Override
	public void willDestroy()
	{
		_adView = null;
		super.willDestroy();
	}

	@Override
	public void onAdRequestDone(boolean adAvailable)
	{
		Log.d(AdFlakeUtil.ADFLAKE, "AppBrainAppLift onAdRequestDone=" + adAvailable);

		if (_adView == null)
			return;

		_adView.setBannerListener(null);

		AdFlakeLayout adFlakeLayout = _adFlakeLayoutReference.get();
		if (adFlakeLayout == null)
			return;
		
		if (adAvailable == false)
		{			
			adFlakeLayout.adapterDidFailToReceiveAdWithError(this, "ERROR: AppBrain reports ad not available");
			return;
		}
		else
		{
			adFlakeLayout.adapterDidReceiveAd(this, _adView);
		}
	}

	@Override
	public void onClick()
	{
		Log.d(AdFlakeUtil.ADFLAKE, "AppBrainAppLift onClick");

	}
}
