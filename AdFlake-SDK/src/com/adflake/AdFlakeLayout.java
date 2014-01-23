/**
 * AdFlakeLayout.java (AdFlakeSDK-Android)
 *
 * Copyright © 2013 MADE GmbH - All Rights Reserved.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * unless otherwise noted in the License section of this document header.
 *
 * @file AdFlakeLayout.java
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

package com.adflake;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.adflake.adapters.AdFlakeAdapter;
import com.adflake.obj.Custom;
import com.adflake.obj.Extra;
import com.adflake.obj.Ration;
import com.adflake.util.AdFlakeUtil;

/**
 * The AdFlakeLayout class manages the display of ADs as received from the
 * AdFlake server.
 */
public class AdFlakeLayout extends RelativeLayout
{
	public static final String				ADFLAKE_KEY	= "ADFLAKE_KEY";
	public WeakReference<Activity>			activityReference;

	/**
	 * The UI handler.
	 * 
	 * @note Only the UI thread can update the UI, so we need a Handler for UI
	 *       callbacks
	 */
	public final Handler					handler		= new Handler();

	/**
	 * The background scheduler manages background threads.
	 */
	public final ScheduledExecutorService	scheduler	= Executors.newScheduledThreadPool(1);
	public Extra							extra;

	public Custom							currentCustom;

	/** @note This is just so our threads can reference us explicitly. */
	public WeakReference<RelativeLayout>	superViewReference;

	public Ration							activeRation;
	public Ration							nextRation;

	public AdFlakeInterface					adFlakeInterface;

	public AdFlakeManager					adFlakeManager;

	/**
	 * Instantiates a new ad flake layout.
	 * 
	 * @param context
	 *            the context
	 * @param adFlakeKey
	 *            the ad flake key
	 */
	public AdFlakeLayout(final Activity context, final String adFlakeKey)
	{
		super(context);
		init(context, adFlakeKey);
	}

	/**
	 * Instantiates a new ad flake layout.
	 * 
	 * @param context
	 *            the context
	 * @param attrs
	 *            the attrs
	 */
	public AdFlakeLayout(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		
		if (this.isInEditMode())
			return;
		
		// Retrieves AdFlake key.
		String key = getAdFlakeKey(context);
		init((Activity) context, key);
	}

	/**
	 * Gets the optimal layout params.
	 * 
	 * @return the layout params
	 */
	public LayoutParams getOptimalRelativeLayoutParams()
	{
		DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
		
		final float density = displayMetrics.density;
		final int width = (int) (AdFlakeUtil.BANNER_DEFAULT_WIDTH * density);
		final int height = (int) (AdFlakeUtil.BANNER_DEFAULT_HEIGHT * density); 

		return new RelativeLayout.LayoutParams(width, height);
	}

	/**
	 * Sets the max height.
	 * 
	 * @param height
	 *            the new max height
	 */
	public void setMaxHeight(int height)
	{
		_maximumHeight = height;
	}

	/**
	 * Sets the max width.
	 * 
	 * @param width
	 *            the new max width
	 */
	public void setMaxWidth(int width)
	{
		_maximumWidth = width;
	}

	/**
	 * Gets the ad flake key.
	 * 
	 * @param context
	 *            the context
	 * @return the ad flake key
	 */
	protected String getAdFlakeKey(Context context)
	{
		if (this.isInEditMode())
			return "EDITMODE";
		
		final String packageName = context.getPackageName();
		final String activityName = context.getClass().getName();
		final PackageManager pm = context.getPackageManager();
		Bundle bundle = null;
		// Attempts to retrieve Activity-specific AdFlake key first. If not
		// found, retrieve Application-wide AdFlake key.
		try
		{
			ActivityInfo activityInfo = pm.getActivityInfo(new ComponentName(packageName, activityName), PackageManager.GET_META_DATA);
			bundle = activityInfo.metaData;
			if (bundle != null)
			{
				return bundle.getString(AdFlakeLayout.ADFLAKE_KEY);
			}
		}
		catch (NameNotFoundException exception)
		{
			// Activity cannot be found. Shouldn't be here.
			return null;
		}

		try
		{
			ApplicationInfo appInfo = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
			bundle = appInfo.metaData;
			if (bundle != null)
			{
				return bundle.getString(AdFlakeLayout.ADFLAKE_KEY);
			}
		}
		catch (NameNotFoundException exception)
		{
			// Application cannot be found. Shouldn't be here.
			return null;
		}
		return null;
	}

	/**
	 * Initializes this instance.
	 * 
	 * @param context
	 *            the context
	 * @param adFlakeKey
	 *            the ad flake key
	 */
	protected void init(final Activity context, final String adFlakeKey)
	{
		
		this.activityReference = new WeakReference<Activity>(context);
		this.superViewReference = new WeakReference<RelativeLayout>(this);
		this._adFlakeKey = adFlakeKey;
		this._hasWindow = true;
		this._isScheduled = true;
		

		if (!this.isInEditMode()) 
		{
			scheduler.schedule(new UpdateAdFlakeConfigurationRunnable(this, adFlakeKey), 0, TimeUnit.SECONDS);
		}
		setHorizontalScrollBarEnabled(false);
		setVerticalScrollBarEnabled(false);

		this._maximumWidth = 0;
		this._maximumHeight = 0;
	}

	/**
	 * On measure.
	 * 
	 * @param widthMeasureSpec
	 *            the width measure spec
	 * @param heightMeasureSpec
	 *            the height measure spec
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		if (_maximumWidth > 0 && widthSize > _maximumWidth)
		{
			widthMeasureSpec = MeasureSpec.makeMeasureSpec(_maximumWidth, MeasureSpec.AT_MOST);
		}

		if (_maximumHeight > 0 && heightSize > _maximumHeight)
		{
			heightMeasureSpec = MeasureSpec.makeMeasureSpec(_maximumHeight, MeasureSpec.AT_MOST);
		}

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	/**
	 * On window visibility changed.
	 * 
	 * @param visibility
	 *            the visibility
	 */
	@Override
	protected void onWindowVisibilityChanged(int visibility)
	{
		if (visibility == VISIBLE && !this.isInEditMode())
		{
			this._hasWindow = true;
			if (!this._isScheduled)
			{
				this._isScheduled = true;

				if (this.extra != null)
				{
					rotateThreadedNow();
				}
				else
				{
					scheduler.schedule(new UpdateAdFlakeConfigurationRunnable(this, _adFlakeKey), 0, TimeUnit.SECONDS);
				}
			}
		}
		else
		{
			this._hasWindow = false;
		}
	}

	/**
	 * Rotate ad.
	 */
	public void rotateAd()
	{
		if (!this._hasWindow)
		{
			this._isScheduled = false;
			return;
		}

		Log.i(AdFlakeUtil.ADFLAKE, "Rotating Ad");
		nextRation = adFlakeManager.getDartedRation();

		handler.post(new HandleAdRunnable(this));
	}

	/**
	 * Initialize the proper ad view from nextRation.
	 */
	@SuppressLint("DefaultLocale")
	private void handleAd()
	{
		// We shouldn't ever get to a state where nextRation is null unless all
		// networks fail
		if (nextRation == null)
		{
			Log.e(AdFlakeUtil.ADFLAKE, "nextRation is null!");
			rotateThreadedDelayed();
			return;
		}

		String rationInfo = String.format("Showing ad:\n\tnid: %s\n\tname: %s\n\ttype: %d\n\tkey: %s\n\tkey2: %s", nextRation.nid, nextRation.name, nextRation.type, nextRation.key, nextRation.key2);
		Log.d(AdFlakeUtil.ADFLAKE, rationInfo);

		try
		{
			// Tell the previous adapter that its view will be destroyed.
			if (this._previousAdapter != null)
			{
				this._previousAdapter.willDestroy();
			}
			this._previousAdapter = this._currentAdapter;
			this._currentAdapter = AdFlakeAdapter.getAdapterForRation(this, nextRation);
		}
		catch (Throwable t)
		{
			Log.w(AdFlakeUtil.ADFLAKE, "Caught an exception in adapter:", t);
			rollover();
			return;
		}
	}

	/**
	 * Rotate immediately.
	 */
	public void rotateThreadedNow()
	{
		scheduler.schedule(new RotateAdRunnable(this), 0, TimeUnit.SECONDS);
	}

	/**
	 * Rotate in extra.cycleTime seconds.
	 */
	public void rotateThreadedDelayed()
	{
		Log.d(AdFlakeUtil.ADFLAKE, "Will call rotateAd() in " + extra.cycleTime + " seconds");
		scheduler.schedule(new RotateAdRunnable(this), extra.cycleTime, TimeUnit.SECONDS);
	}

	/**
	 * Remove old views and push the new adView.
	 * 
	 * @param subView
	 *            the sub view
	 */
	public void pushSubView(ViewGroup subView)
	{
		RelativeLayout superView = superViewReference.get();
		if (superView == null)
		{
			return;
		}
		superView.removeAllViews();

		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		superView.addView(subView, layoutParams);

		Log.d(AdFlakeUtil.ADFLAKE, "Added subview");

		this.activeRation = nextRation;
		sendImpressionToMetricServer();

		if (this.adFlakeInterface != null)
		{
			this.adFlakeInterface.adFlakeDidPushAdSubView(this);
		}
	}

	/**
	 * Rollover.
	 */
	public void rollover()
	{
		nextRation = adFlakeManager.getRationForCurrentRolloverListPosition();
		handler.post(new HandleAdRunnable(this));
	}

	/**
	 * Send impression to metric server.
	 */
	private void sendImpressionToMetricServer()
	{
		if (activeRation != null)
		{
			String url = String.format(AdFlakeUtil.urlImpression, adFlakeManager.adFlakeKey, activeRation.nid, activeRation.type, adFlakeManager.deviceIDHash, adFlakeManager.localeString, AdFlakeUtil.VERSION);
			scheduler.schedule(new PingUrlRunnable(url), 0, TimeUnit.SECONDS);
		}
	}

	/**
	 * Send ad click to metric server.
	 */
	private void sendAdClickToMetricServer()
	{
		if (activeRation != null)
		{
			String url = String.format(AdFlakeUtil.urlClick, adFlakeManager.adFlakeKey, activeRation.nid, activeRation.type, adFlakeManager.deviceIDHash, adFlakeManager.localeString, AdFlakeUtil.VERSION);
			scheduler.schedule(new PingUrlRunnable(url), 0, TimeUnit.SECONDS);
		}
	}

	/**
	 * On intercept touch event. We intercept clicks to provide raw metrics.
	 * 
	 * @param event
	 *            the event
	 * @return true, if successful
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent event)
	{
		if (this.isInEditMode())
			return false;
		
		switch (event.getAction())
		{
		// Sending on an ACTION_DOWN isn't 100% correct... user could have
		// touched
		// down and dragged out. Unlikely though.
			case MotionEvent.ACTION_DOWN:
				Log.d(AdFlakeUtil.ADFLAKE, "Intercepted ACTION_DOWN event");
				if (activeRation != null)
				{
					sendAdClickToMetricServer();

					if (activeRation.type == 9)
					{
						if (currentCustom != null && currentCustom.link != null)
						{
							Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentCustom.link));
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							try
							{
								if (activityReference == null)
								{
									return false;
								}
								Activity activity = activityReference.get();
								if (activity == null)
								{
									return false;
								}
								activity.startActivity(intent);
							}
							catch (Exception e)
							{
								Log.w(AdFlakeUtil.ADFLAKE, "Could not handle click to " + currentCustom.link, e);
							}
						}
						else
						{
							Log.w(AdFlakeUtil.ADFLAKE, "In onInterceptTouchEvent(), but custom or custom.link is null");
						}
					}
					break;
				}
		}

		// Return false so subViews can process event normally.
		return false;
	}

	/**
	 * The Interface AdFlakeInterface provides methods to react to changes of a
	 * AdFlakeLayout.
	 */
	public interface AdFlakeInterface
	{

		/**
		 * Invoked when a GenericAdapter instance is handled.
		 */
		public void adFlakeGeneric();

		/**
		 * Invoked when a banner ad's view has been pushed into the
		 * AdFlakeLayout.
		 * 
		 * @param layout
		 *            the layout
		 */
		public void adFlakeDidPushAdSubView(AdFlakeLayout layout);
	}

	/**
	 * Sets the ad flake interface.
	 * 
	 * @param i
	 *            the new ad flake interface
	 */
	public void setAdFlakeInterface(AdFlakeInterface i)
	{
		this.adFlakeInterface = i;
	}

	/**
	 * The Class UpdateAdFlakeConfigurationRunnable fetches the AdFlake
	 * configuration from the server.
	 */
	private static class UpdateAdFlakeConfigurationRunnable implements Runnable
	{
		private WeakReference<AdFlakeLayout>	_adFlakeLayoutReference;
		private String							_adFlakeKey;

		/**
		 * Instantiates a new update ad flake configuration runnable.
		 * 
		 * @param adFlakeLayout
		 *            the ad flake layout
		 * @param adFlakeKey
		 *            the ad flake key
		 */
		public UpdateAdFlakeConfigurationRunnable(AdFlakeLayout adFlakeLayout, String adFlakeKey)
		{
			_adFlakeLayoutReference = new WeakReference<AdFlakeLayout>(adFlakeLayout);
			_adFlakeKey = adFlakeKey;
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run()
		{
			AdFlakeLayout adFlakeLayout = _adFlakeLayoutReference.get();
			if (adFlakeLayout != null)
			{
				Activity activity = adFlakeLayout.activityReference.get();
				if (activity == null)
				{
					return;
				}

				if (adFlakeLayout.adFlakeManager == null)
				{
					adFlakeLayout.adFlakeManager = new AdFlakeManager(new WeakReference<Context>(activity.getApplicationContext()), _adFlakeKey);
				}

				if (!adFlakeLayout._hasWindow)
				{
					adFlakeLayout._isScheduled = false;
					return;
				}

				adFlakeLayout.adFlakeManager.fetchConfigFromServer();
				adFlakeLayout.extra = adFlakeLayout.adFlakeManager.getExtra();

				if (adFlakeLayout.adFlakeManager.sleeperMode == true)
				{
					// NOTE: if we do not have a configuration it's highly probable that
					// we're in sleeper more, so only check every 10 minutes
					adFlakeLayout.scheduler.schedule(this, 600, TimeUnit.SECONDS);
				}
				else if (adFlakeLayout.extra == null)
				{
					adFlakeLayout.scheduler.schedule(this, 30, TimeUnit.SECONDS);
				}
				else
				{
					adFlakeLayout.rotateAd();
				}
			}
		}
	}

	/**
	 * The Class HandleAdRunnable Callback for external networks.
	 */
	private static class HandleAdRunnable implements Runnable
	{
		private WeakReference<AdFlakeLayout>	adFlakeLayoutReference;

		/**
		 * Instantiates a new handle ad runnable.
		 * 
		 * @param adFlakeLayout
		 *            the ad flake layout
		 */
		public HandleAdRunnable(AdFlakeLayout adFlakeLayout)
		{
			adFlakeLayoutReference = new WeakReference<AdFlakeLayout>(adFlakeLayout);
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run()
		{
			AdFlakeLayout adFlakeLayout = adFlakeLayoutReference.get();
			if (adFlakeLayout != null)
			{
				adFlakeLayout.handleAd();
			}
		}
	}

	/**
	 * The Class PushAdViewRunnable inserts the specified ViewGroup into as
	 * primary view into the AdFlakeLayout.
	 */
	public static class PushAdViewRunnable implements Runnable
	{
		private WeakReference<AdFlakeLayout>	adFlakeLayoutReference;
		private ViewGroup						nextView;

		/**
		 * Instantiates a new view ad runnable.
		 * 
		 * @param adFlakeLayout
		 *            the ad flake layout
		 * @param nextView
		 *            the next view
		 */
		public PushAdViewRunnable(AdFlakeLayout adFlakeLayout, ViewGroup nextView)
		{
			adFlakeLayoutReference = new WeakReference<AdFlakeLayout>(adFlakeLayout);
			this.nextView = nextView;
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run()
		{
			AdFlakeLayout adFlakeLayout = adFlakeLayoutReference.get();
			if (adFlakeLayout != null)
			{
				adFlakeLayout.pushSubView(nextView);
			}
		}
	}

	/**
	 * The Class RotateAdRunnable rotates the current ad.
	 */
	private static class RotateAdRunnable implements Runnable
	{
		private WeakReference<AdFlakeLayout>	adFlakeLayoutReference;

		/**
		 * Instantiates a new rotate ad runnable.
		 * 
		 * @param adFlakeLayout
		 *            the ad flake layout
		 */
		public RotateAdRunnable(AdFlakeLayout adFlakeLayout)
		{
			adFlakeLayoutReference = new WeakReference<AdFlakeLayout>(adFlakeLayout);
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run()
		{
			AdFlakeLayout adFlakeLayout = adFlakeLayoutReference.get();
			if (adFlakeLayout != null)
			{
				adFlakeLayout.rotateAd();
			}
		}
	}

	/**
	 * The Class PingUrlRunnable performs a HTTP get with the specified URL and
	 * ignores the result. This is basically used to trigger a remote operation
	 * on the server.
	 */
	private static class PingUrlRunnable implements Runnable
	{
		private String	url;

		/**
		 * Instantiates a new ping url runnable.
		 * 
		 * @param url
		 *            the url
		 */
		public PingUrlRunnable(String url)
		{
			this.url = url;
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run()
		{
			Log.d(AdFlakeUtil.ADFLAKE, "Pinging URL: " + url);

			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(url);

			try
			{
				httpClient.execute(httpGet);
			}
			catch (ClientProtocolException e)
			{
				Log.e(AdFlakeUtil.ADFLAKE, "Caught ClientProtocolException in PingUrlRunnable", e);
			}
			catch (IOException e)
			{
				Log.e(AdFlakeUtil.ADFLAKE, "Caught IOException in PingUrlRunnable", e);
			}
		}
	}

	/**
	 * Adapter did fail to receive ad with error. This method should be invoked
	 * by adapter when an error occured during the retrieval of a remote ad.
	 * 
	 * @param adapter
	 *            the adapter
	 * @param string
	 *            the string
	 */
	public void adapterDidFailToReceiveAdWithError(AdFlakeAdapter adapter, String string)
	{
		this.rollover();

		Log.e(AdFlakeUtil.ADFLAKE, "ERROR: " + adapter.getRation().name + ": " + string);
	}

	/**
	 * Adapter did receive ad. This method should be invoked by adapter when a
	 * remote ad has been fetched successfully and the specified view is ready
	 * to be displayed in the AdWhirlLayout.
	 * 
	 * @param adapter
	 *            the adapter
	 * @param view
	 *            the view
	 */
	public void adapterDidReceiveAd(AdFlakeAdapter adapter, ViewGroup view)
	{
		this.adFlakeManager.resetRollover();
		this.handler.post(new PushAdViewRunnable(this, view));
		this.rotateThreadedDelayed();
	}

	private String			_adFlakeKey;

	/** Added so we can tell the previous adapter that it is being destroyed. */
	private AdFlakeAdapter	_previousAdapter;
	private AdFlakeAdapter	_currentAdapter;

	private boolean			_hasWindow;
	private boolean			_isScheduled;

	private int				_maximumWidth;

	private int				_maximumHeight;
}
