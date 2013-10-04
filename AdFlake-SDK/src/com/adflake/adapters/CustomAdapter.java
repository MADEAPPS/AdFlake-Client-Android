/**
 * CustomAdapter.java (AdFlakeSDK-Android)
 *
 * Copyright © 2013 MADE GmbH - All Rights Reserved.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * unless otherwise noted in the License section of this document header.
 *
 * @file CustomAdapter.java
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

import com.adflake.AdFlakeLayout;
import com.adflake.obj.Ration;
import com.adflake.util.AdFlakeUtil;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView.ScaleType;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * The CustomAdapter class displays house ads as configured on the AdFlake
 * website.
 */
public class CustomAdapter extends AdFlakeAdapter
{
	/**
	 * Instantiates a new custom adapter.
	 * 
	 * @param adFlakeLayout
	 *            the ad flake layout
	 * @param ration
	 *            the ration
	 */
	public CustomAdapter(AdFlakeLayout adFlakeLayout, Ration ration)
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
		{
			return;
		}

		adFlakeLayout.scheduler.schedule(new FetchCustomRunnable(this), 0, TimeUnit.SECONDS);
	}

	/**
	 * Display the custom ad.
	 */
	public void displayCustom()
	{
		AdFlakeLayout adFlakeLayout = _adFlakeLayoutReference.get();
		if (adFlakeLayout == null)
		{
			return;
		}

		Activity activity = adFlakeLayout.activityReference.get();
		if (activity == null)
		{
			return;
		}

		// This may be incorrect and need to be adjusted for density.
		double density = AdFlakeUtil.getDensity(activity);
		double px320 = AdFlakeUtil.convertToScreenPixels(320, density);
		double px50 = AdFlakeUtil.convertToScreenPixels(50, density);

		// NOTE: FILL_PARENT (renamed MATCH_PARENT in API Level 8 and
		// higher),
		// which means that the view wants to be as big as its parent
		// (minus padding)
		@SuppressWarnings("deprecation")
		final int layoutTypeFillParent = android.view.ViewGroup.LayoutParams.FILL_PARENT;

		switch (adFlakeLayout.currentCustom.type)
		{
			case AdFlakeUtil.CUSTOM_TYPE_BANNER:
				Log.d(AdFlakeUtil.ADFLAKE, "Serving custom type: banner");

				if (adFlakeLayout.currentCustom.image == null)
				{
					adFlakeLayout.rotateThreadedNow();
					return;
				}

				RelativeLayout bannerView = new RelativeLayout(activity);
				bannerView.setLayoutParams(new LayoutParams((int) px320, (int) px50));

				ImageView bannerImageView = new ImageView(activity);
				bannerImageView.setImageDrawable(adFlakeLayout.currentCustom.image);

				RelativeLayout.LayoutParams viewParams = new RelativeLayout.LayoutParams(layoutTypeFillParent, layoutTypeFillParent);
				bannerView.addView(bannerImageView, viewParams);

				adFlakeLayout.pushSubView(bannerView);
				break;

			case AdFlakeUtil.CUSTOM_TYPE_ICON:
				Log.d(AdFlakeUtil.ADFLAKE, "Serving custom type: icon");
				RelativeLayout iconView = new RelativeLayout(activity);
				if (adFlakeLayout.currentCustom.image == null)
				{
					adFlakeLayout.rotateThreadedNow();
					return;
				}

				double px4 = AdFlakeUtil.convertToScreenPixels(4, density);
				double px6 = AdFlakeUtil.convertToScreenPixels(6, density);

				// This may be incorrect and need to be adjusted for density.
				iconView.setLayoutParams(new LayoutParams((int) px320, (int) px50));

				int gradientBottomColor = Color.rgb(adFlakeLayout.extra.bgRed, adFlakeLayout.extra.bgGreen, adFlakeLayout.extra.bgBlue);
				int gradientTopColor = Color.WHITE;

				ImageView blendView = new ImageView(activity);
				GradientDrawable blend = new GradientDrawable(Orientation.TOP_BOTTOM, new int[] { gradientTopColor, gradientBottomColor, gradientBottomColor, gradientBottomColor });

				// NOTE: This method was deprecated in API level 16. Use
				// setBackground(Drawable) instead
				try
				{
					// blendView.setBackgroundDrawable(blend);
					blendView.getClass().getMethod(android.os.Build.VERSION.SDK_INT >= 16 ? "setBackground" : "setBackgroundDrawable", Drawable.class).invoke(blendView, blend);
				}
				catch (Exception ex)
				{
					// do nothing
				}
				RelativeLayout.LayoutParams blendViewParams = new RelativeLayout.LayoutParams(layoutTypeFillParent, layoutTypeFillParent);
				iconView.addView(blendView, blendViewParams);

				ImageView iconImageView = new ImageView(activity);
				iconImageView.setImageDrawable(adFlakeLayout.currentCustom.image);
				iconImageView.setId(10);
				iconImageView.setPadding((int) px4, 0, (int) px6, 0);
				iconImageView.setScaleType(ScaleType.CENTER);

				RelativeLayout.LayoutParams iconViewParams = new RelativeLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, layoutTypeFillParent);
				iconView.addView(iconImageView, iconViewParams);

				ImageView frameImageView = new ImageView(activity);
				InputStream drawableStream = getClass().getResourceAsStream("/com/adflake/assets/ad_frame.gif");

				Drawable adFrameDrawable = new BitmapDrawable(activity.getResources(), drawableStream);
				frameImageView.setImageDrawable(adFrameDrawable);
				frameImageView.setPadding((int) px4, 0, (int) px6, 0);
				frameImageView.setScaleType(ScaleType.CENTER);

				RelativeLayout.LayoutParams frameViewParams = new RelativeLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, layoutTypeFillParent);
				iconView.addView(frameImageView, frameViewParams);
				TextView iconTextView = new TextView(activity);
				iconTextView.setText(adFlakeLayout.currentCustom.description);
				iconTextView.setTypeface(Typeface.DEFAULT_BOLD, 1);
				iconTextView.setTextColor(Color.rgb(adFlakeLayout.extra.fgRed, adFlakeLayout.extra.fgGreen, adFlakeLayout.extra.fgBlue));

				RelativeLayout.LayoutParams textViewParams = new RelativeLayout.LayoutParams(layoutTypeFillParent, layoutTypeFillParent);
				textViewParams.addRule(RelativeLayout.RIGHT_OF, iconImageView.getId());
				textViewParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				textViewParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				textViewParams.addRule(RelativeLayout.CENTER_VERTICAL);
				textViewParams.addRule(RelativeLayout.CENTER_IN_PARENT);
				iconTextView.setGravity(Gravity.CENTER_VERTICAL);
				iconView.addView(iconTextView, textViewParams);
				adFlakeLayout.pushSubView(iconView);
				break;

			default:
				Log.w(AdFlakeUtil.ADFLAKE, "Unknown custom type!");
				adFlakeLayout.rotateThreadedNow();
				return;
		}

		adFlakeLayout.adFlakeManager.resetRollover();
		adFlakeLayout.rotateThreadedDelayed();
	}

	/**
	 * The FetchCustomRunnable class fetches the house AD custom configuration
	 * from the server.
	 */
	private static class FetchCustomRunnable implements Runnable
	{
		private CustomAdapter	customAdapter;

		/**
		 * Instantiates a new fetch custom runnable.
		 * 
		 * @param customAdapter
		 *            the custom adapter
		 */
		public FetchCustomRunnable(CustomAdapter customAdapter)
		{
			this.customAdapter = customAdapter;
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run()
		{
			AdFlakeLayout adFlakeLayout = customAdapter._adFlakeLayoutReference.get();
			if (adFlakeLayout == null)
			{
				return;
			}

			adFlakeLayout.currentCustom = adFlakeLayout.adFlakeManager.fetchCustomBannerFromServerWithNetworkID(customAdapter._ration.nid);
			if (adFlakeLayout.currentCustom == null)
			{
				adFlakeLayout.rotateThreadedNow();
				return;
			}

			adFlakeLayout.handler.post(new DisplayCustomRunnable(customAdapter));
		}
	}

	/**
	 * The DisplayCustomRunnable class prepares the display of a previously
	 * fetched custom ad.
	 */
	private static class DisplayCustomRunnable implements Runnable
	{
		private CustomAdapter	customAdapter;

		/**
		 * Instantiates a new display custom runnable.
		 * 
		 * @param customAdapter
		 *            the custom adapter
		 */
		public DisplayCustomRunnable(CustomAdapter customAdapter)
		{
			this.customAdapter = customAdapter;
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run()
		{
			customAdapter.displayCustom();
		}
	}
}
