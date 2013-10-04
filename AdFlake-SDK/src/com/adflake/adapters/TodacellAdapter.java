/**
 * TodacellAdapter.java (AdFlakeSDK-Android)
 *
 * Copyright © 2013 MADE GmbH - All Rights Reserved.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * unless otherwise noted in the License section of this document header.
 *
 * @file TodacellAdapter.java
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

import android.annotation.TargetApi;
import android.os.Build;
import android.os.StrictMode;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.adflake.AdFlakeLayout;
import com.adflake.AdFlakeTargeting;
import com.adflake.obj.Ration;
import com.adflake.util.AdFlakeUtil;
import com.todacell.TodacellBannerView;

/**
 * The Class TodacellAdapter.
 */
public class TodacellAdapter extends AdFlakeAdapter
{
	private TodacellBannerView	_todacellBannerView;

	/**
	 * Instantiates a new todacell adapter.
	 * 
	 * @param adFlakeLayout
	 *            the ad flake layout
	 * @param ration
	 *            the ration
	 */
	public TodacellAdapter(AdFlakeLayout adFlakeLayout, Ration ration)
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

		String publisherID = _ration.key;

		if (AdFlakeTargeting.getTestMode() == true)
			publisherID = "0";

		WebView webView = new WebView(adFlakeLayout.getContext());
		webView.setLayoutParams(new RelativeLayout.LayoutParams(0, 0));
		RelativeLayout relativeLayout = new RelativeLayout(adFlakeLayout.getContext());
		relativeLayout.setLayoutParams(adFlakeLayout.getOptimalRelativeLayoutParams());
		relativeLayout.addView(webView);

		final int width = AdFlakeUtil.BANNER_DEFAULT_WIDTH;
		final int height = AdFlakeUtil.BANNER_DEFAULT_HEIGHT;
		final int x = 0, y = 0;

		// NOTE: we're sorry for having to do this, there is a critical bug in
		// the Todacell SDK. Basically they're doing network IO on the main
		// thread which causes an exception to be thrown. So we fix that by
		// allowing network IO on the main thread.
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)
		{
			disableStrictNetworkPolicy();
		}

		_todacellBannerView = new TodacellBannerView(relativeLayout, webView, publisherID, 99999, width, height, x, y);
		_todacellBannerView.start();

		// NOTE: another limitation in the Todacell SDK. There is no listener
		// that we can use to display the ad so we always have to assume that we
		// were able to receive an ad.
		adFlakeLayout.adapterDidReceiveAd(this, relativeLayout);
	}

	/**
	 * Disable strict network policy for devices running gingerbread or later.
	 * 
	 * @remarks We have to do this due to a bug in the Todacell SDK. They're
	 *          actually doing network IO on the mainthread.
	 */
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	private void disableStrictNetworkPolicy()
	{
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
	}

	/*
	 * (non-Javadoc)
	 * @see com.adflake.adapters.AdFlakeAdapter#willDestroy()
	 */
	@Override
	public void willDestroy()
	{
		if (_todacellBannerView != null)
		{
			if (_todacellBannerView.isRunning())
				_todacellBannerView.stop();
			_todacellBannerView = null;
		}
		super.willDestroy();
	}

}
