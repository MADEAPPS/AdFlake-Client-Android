/**
 * AdFlakeManager.java (AdFlakeSDK-Android)
 *
 * Copyright � 2013 MADE GmbH - All Rights Reserved.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * unless otherwise noted in the License section of this document header.
 *
 * @file AdFlakeManager.java
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings.Secure;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.adflake.adapters.AdColonyVideoAdsAdapter;
import com.adflake.obj.Custom;
import com.adflake.obj.Extra;
import com.adflake.obj.Ration;
import com.adflake.util.AdFlakeUtil;

/**
 * The AdFlakeManager class manages the AdFlake configuration. If necessary the
 * class will download it from a remote server.
 */
public class AdFlakeManager
{
	public String adFlakeKey;

	public String localeString;
	public String deviceIDHash;

	public boolean sleeperMode;
	public boolean videoAdsAvailable;

	public Location location;

	/**
	 * Instantiates a new ad flake manager.
	 * 
	 * @param contextReference
	 *            the context reference
	 * @param adFlakeKey
	 *            the ad flake key
	 */
	public AdFlakeManager(WeakReference<Context> contextReference, String adFlakeKey)
	{
		Log.i(AdFlakeUtil.ADFLAKE, "Creating adFlakeManager...");
		this._contextReference = contextReference;
		this.adFlakeKey = adFlakeKey;

		this.sleeperMode = false;

		localeString = Locale.getDefault().toString();
		Log.d(AdFlakeUtil.ADFLAKE, "Locale is: " + localeString);

		MessageDigest md;
		try
		{
			md = MessageDigest.getInstance("MD5");
			StringBuffer deviceIDString = new StringBuffer(Secure.ANDROID_ID);
			deviceIDString.append("AdFlake");
			deviceIDHash = AdFlakeUtil.convertToHex(md.digest(deviceIDString.toString().getBytes()));
		}
		catch (NoSuchAlgorithmException e)
		{
			deviceIDHash = "00000000000000000000000000000000";
		}
		Log.d(AdFlakeUtil.ADFLAKE, "Hashed device ID is: " + deviceIDHash);

		Log.i(AdFlakeUtil.ADFLAKE, "Finished creating adFlakeManager");
	}

	/**
	 * Sets the config expire timeout.
	 * 
	 * @param configExpireTimeout
	 *            the new config expire timeout
	 */
	public static void setConfigExpireTimeout(long configExpireTimeout)
	{
		AdFlakeManager._configExpireTimeout = configExpireTimeout;
	}

	/**
	 * Gets the extra.
	 * 
	 * @return the extra
	 */
	public Extra getExtra()
	{
		if (_totalWeight <= 0)
		{
			return null;
		}
		else
		{
			return this._extra;
		}
	}

	/**
	 * Gets the darted ration.
	 * 
	 * @return the darted ration
	 */
	public Ration getDartedRation()
	{
		Random random = new Random();

		double r = random.nextDouble() * _totalWeight;
		double s = 0;

		Log.d(AdFlakeUtil.ADFLAKE, "Dart is <" + r + "> of <" + _totalWeight + ">");

		Iterator<Ration> it = this._rationsList.iterator();
		Ration ration = null;
		while (it.hasNext())
		{
			ration = it.next();
			s += ration.weight;

			if (s >= r)
			{
				break;
			}
		}

		return ration;
	}

	/**
	 * Gets the ration for current rollover list position.
	 * 
	 * @return the ration for current rollover list position
	 */
	public Ration getRationForCurrentRolloverListPosition()
	{
		if (this._rollovers == null)
		{
			return null;
		}

		Ration ration = null;
		if (this._rollovers.hasNext())
		{
			ration = this._rollovers.next();
		}

		return ration;
	}

	/**
	 * Reset rollover list pointer to the beginning of the list.
	 */
	public void resetRollover()
	{
		this._rollovers = this._rationsList.iterator();
	}

	/**
	 * Fetch a custom banner from server with the specifeid network id.
	 * 
	 * @param nid
	 *            the nid
	 * @return the custom
	 */
	public Custom fetchCustomBannerFromServerWithNetworkID(String nid)
	{
		HttpClient httpClient = new DefaultHttpClient();

		String locationString;
		if (_extra.locationOn == 1)
		{
			location = getCurrentLocation();
			if (location != null)
			{
				locationString = String.format(AdFlakeUtil.locationString, location.getLatitude(), location.getLongitude(), location.getTime());
			}
			else
			{
				locationString = "";
			}
		}
		else
		{
			location = null;
			locationString = "";
		}

		String url = String.format(AdFlakeUtil.urlCustom, this.adFlakeKey, nid, deviceIDHash, localeString, locationString, AdFlakeUtil.VERSION);
		HttpGet httpGet = new HttpGet(url);

		HttpResponse httpResponse;
		try
		{
			httpResponse = httpClient.execute(httpGet);

			Log.d(AdFlakeUtil.ADFLAKE, httpResponse.getStatusLine().toString());

			HttpEntity entity = httpResponse.getEntity();

			if (entity != null)
			{
				InputStream inputStream = entity.getContent();
				String jsonString = convertStreamToString(inputStream);
				return parseCustomJsonString(jsonString);
			}
		}
		catch (ClientProtocolException e)
		{
			Log.e(AdFlakeUtil.ADFLAKE, "Caught ClientProtocolException in getCustom()", e);
		}
		catch (IOException e)
		{
			Log.e(AdFlakeUtil.ADFLAKE, "Caught IOException in getCustom()", e);
		}

		return null;
	}

	/**
	 * Fetch the ADFlake config from the remote server.
	 * 
	 * @note If the config timeout has not been hit, the previously downloaded
	 *       config will be reused. If testmode is enabled, the config will
	 *       always be downloaded.
	 */
	public void fetchConfigFromServer()
	{
		Context context = _contextReference.get();

		// If the context is null here something went wrong with initialization.
		if (context == null)
		{
			return;
		}

		SharedPreferences adFlakePrefs = context.getSharedPreferences(adFlakeKey, Context.MODE_PRIVATE);
		String jsonString = adFlakePrefs.getString(PREFS_STRING_CONFIG, null);
		long timestamp = adFlakePrefs.getLong(PREFS_STRING_TIMESTAMP, -1);

		Log.d(AdFlakeUtil.ADFLAKE, "Prefs{" + adFlakeKey + "}: {\"" + PREFS_STRING_CONFIG + "\": \"" + jsonString + "\", \"" + PREFS_STRING_TIMESTAMP + "\": " + timestamp + "}");

		if (jsonString == null || _configExpireTimeout == -1 || System.currentTimeMillis() >= timestamp + _configExpireTimeout || AdFlakeTargeting.getTestMode() == true)
		{
			Log.i(AdFlakeUtil.ADFLAKE, "Stored config info not present or expired, fetching fresh data");

			HttpClient httpClient = new DefaultHttpClient();

			String url = String.format(AdFlakeUtil.urlConfig, this.adFlakeKey, AdFlakeUtil.VERSION);
			HttpGet httpGet = new HttpGet(url);

			HttpResponse httpResponse;
			try
			{
				httpResponse = httpClient.execute(httpGet);

				Log.d(AdFlakeUtil.ADFLAKE, httpResponse.getStatusLine().toString());

				HttpEntity entity = httpResponse.getEntity();

				if (entity != null)
				{
					InputStream inputStream = entity.getContent();
					jsonString = convertStreamToString(inputStream);

					SharedPreferences.Editor editor = adFlakePrefs.edit();
					editor.putString(PREFS_STRING_CONFIG, jsonString);
					editor.putLong(PREFS_STRING_TIMESTAMP, System.currentTimeMillis());
					editor.commit();
				}
			}
			catch (ClientProtocolException e)
			{
				Log.e(AdFlakeUtil.ADFLAKE, "Caught ClientProtocolException in fetchConfig()", e);
			}
			catch (IOException e)
			{
				Log.e(AdFlakeUtil.ADFLAKE, "Caught IOException in fetchConfig()", e);
			}
		}
		else
		{
			Log.i(AdFlakeUtil.ADFLAKE, "Using stored config data");
		}

		parseConfigurationString(jsonString);
	}

	/**
	 * Convert stream to string.
	 * 
	 * @param is
	 *            the is
	 * @return the string
	 */
	private String convertStreamToString(InputStream is)
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(is), 8192);
		StringBuilder sb = new StringBuilder();

		String line = null;
		try
		{
			while ((line = reader.readLine()) != null)
			{
				sb.append(line + "\n");
			}
		}
		catch (IOException e)
		{
			Log.e(AdFlakeUtil.ADFLAKE, "Caught IOException in convertStreamToString()", e);
			return null;
		}
		finally
		{
			try
			{
				is.close();
			}
			catch (IOException e)
			{
				Log.e(AdFlakeUtil.ADFLAKE, "Caught IOException in convertStreamToString()", e);
				return null;
			}
		}

		return sb.toString();
	}

	/**
	 * Parses the configuration section from the specified JSON string.
	 * 
	 * @param jsonString
	 *            the json string
	 */
	private void parseConfigurationString(String jsonString)
	{
		Log.d(AdFlakeUtil.ADFLAKE, "Received jsonString: " + jsonString);

		try
		{
			JSONObject json = new JSONObject(jsonString);

			parseExtraJson(json.getJSONObject("extra"));
			parseRationsJson(json.getJSONArray("rations"));
			parseVideoRationsJson(json.getJSONArray("videoRations"));
		}
		catch (JSONException e)
		{
			Log.e(AdFlakeUtil.ADFLAKE, "Unable to parse response from JSON. This may or may not be fatal.", e);
			this._extra = new Extra();
		}
		catch (NullPointerException e)
		{
			Log.e(AdFlakeUtil.ADFLAKE, "Unable to parse response from JSON. This may or may not be fatal.", e);
			this._extra = new Extra();
		}
	}

	/**
	 * Parses the extra section from the specified json object.
	 * 
	 * @param json
	 *            the json
	 */
	private void parseExtraJson(JSONObject json)
	{
		Extra extra = new Extra();

		try
		{
			extra.cycleTime = json.getInt("cycle_time");
			extra.locationOn = json.getInt("location_on");
			extra.transition = json.getInt("transition");

			// Due to legacy clients, the server reports alpha on a scale of
			// 0.0-1.0
			// instead of 0-255

			JSONObject backgroundColor = json.getJSONObject("background_color_rgb");
			extra.bgRed = backgroundColor.getInt("red");
			extra.bgGreen = backgroundColor.getInt("green");
			extra.bgBlue = backgroundColor.getInt("blue");
			extra.bgAlpha = backgroundColor.getInt("alpha") * 255;

			JSONObject textColor = json.getJSONObject("text_color_rgb");
			extra.fgRed = textColor.getInt("red");
			extra.fgGreen = textColor.getInt("green");
			extra.fgBlue = textColor.getInt("blue");
			extra.fgAlpha = textColor.getInt("alpha") * 255;
		}
		catch (JSONException e)
		{
			Log.e(AdFlakeUtil.ADFLAKE, "Exception in parsing config.extra JSON. This may or may not be fatal.", e);
		}

		this._extra = extra;
	}

	/**
	 * Parses the rations section from the specified json object.
	 * 
	 * @param json
	 *            the json
	 */
	private void parseRationsJson(JSONArray json)
	{
		List<Ration> rationsList = new ArrayList<Ration>();

		this._totalWeight = 0;

		try
		{
			int i;
			for (i = 0; i < json.length(); i++)
			{
				JSONObject jsonRation = json.getJSONObject(i);
				if (jsonRation == null)
				{
					continue;
				}

				Ration ration = new Ration();

				ration.nid = jsonRation.getString("nid");
				ration.type = jsonRation.getInt("type");
				ration.name = jsonRation.getString("nname");
				ration.weight = jsonRation.getInt("weight");
				ration.priority = jsonRation.getInt("priority");

				// Quattro has a special key format due to legacy compatibility.
				switch (ration.type)
				{
					case AdFlakeUtil.NETWORK_TYPE_QUATTRO:
						JSONObject keyObj = jsonRation.getJSONObject("key");
						ration.key = keyObj.getString("siteID");
						ration.key2 = keyObj.getString("publisherID");
						break;

					case AdFlakeUtil.NETWORK_TYPE_NEXAGE:
						keyObj = jsonRation.getJSONObject("key");
						ration.key = keyObj.getString("dcn");
						ration.key2 = keyObj.getString("position");
						break;

					default:
						ration.key = jsonRation.getString("key");
						break;
				}

				this._totalWeight += ration.weight;

				rationsList.add(ration);
			}
		}
		catch (JSONException e)
		{
			Log.e(AdFlakeUtil.ADFLAKE, "JSONException in parsing config.rations JSON. This may or may not be fatal.", e);
		}

		Collections.sort(rationsList);

		if (this._totalWeight <= 0)
		{
			Log.i(AdFlakeUtil.ADFLAKE, "Sum of ration weights is 0 - no ads to be shown, sleeper mode enabled");
			this.sleeperMode = true;
		}
		else
		{
			this.sleeperMode = false;
		}

		this._rationsList = rationsList;
		this._rollovers = this._rationsList.iterator();
	}

	/**
	 * Parses the rations section from the specified json object.
	 * 
	 * @param json
	 *            the json
	 */
	private void parseVideoRationsJson(JSONArray json)
	{
		List<Ration> rationsList = new ArrayList<Ration>();

		_totalVideoWeight = 0;

		try
		{
			int i;
			for (i = 0; i < json.length(); i++)
			{
				JSONObject jsonRation = json.getJSONObject(i);
				if (jsonRation == null)
				{
					continue;
				}

				Ration ration = new Ration();

				ration.nid = jsonRation.getString("nid");
				ration.type = jsonRation.getInt("type");
				ration.name = jsonRation.getString("nname");
				ration.weight = jsonRation.getInt("weight");
				ration.priority = jsonRation.getInt("priority");

				switch (ration.type)
				{
					case AdFlakeUtil.NETWORK_TYPE_BEACHFRONT:
					case AdFlakeUtil.NETWORK_TYPE_ADCOLONY:
						ration.key = jsonRation.getString("key");

						int zoneIndex = ration.key.indexOf("|;|");
						if (zoneIndex < 0)
						{
							Log.w(AdFlakeUtil.ADFLAKE, "key separator not found for network=" + ration.name);
							continue;
						}

						ration.key2 = ration.key.substring(zoneIndex + 3);
						ration.key = ration.key.substring(0, zoneIndex);
						break;

					default:
						ration.key = jsonRation.getString("key");
						break;
				}

				this._totalVideoWeight += ration.weight;

				rationsList.add(ration);
			}
		}
		catch (JSONException e)
		{
			Log.e(AdFlakeUtil.ADFLAKE, "JSONException in parsing config.rations JSON. This may or may not be fatal.", e);
		}

		Collections.sort(rationsList);

		if (_totalVideoWeight <= 0)
		{
			Log.i(AdFlakeUtil.ADFLAKE, "Sum of video ration weights is 0 - no video ads available");
			this.videoAdsAvailable = false;
		}
		else
		{
			this.videoAdsAvailable = true;
		}

		this._videoRationsList = rationsList;
	}

	/**
	 * Parses the custom section from the specified json string.
	 * 
	 * @param jsonString
	 *            the json string
	 * @return the custom
	 */
	private Custom parseCustomJsonString(String jsonString)
	{
		Log.d(AdFlakeUtil.ADFLAKE, "Received custom jsonString: " + jsonString);

		Custom custom = new Custom();
		try
		{
			JSONObject json = new JSONObject(jsonString);

			custom.type = json.getInt("ad_type");
			custom.imageLink = json.getString("img_url");
			custom.link = json.getString("redirect_url");
			custom.description = json.getString("ad_text");

			try
			{
				custom.imageLink640x100 = json.getString("img_url_640x100");
			}
			catch (JSONException e)
			{
				custom.imageLink640x100 = null;
			}
			try
			{
				custom.imageLink480x75 = json.getString("img_url_480x75");
			}
			catch (JSONException e)
			{
				custom.imageLink480x75 = null;
			}

			DisplayMetrics metrics = new DisplayMetrics();
			((WindowManager) _contextReference.get().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);

			if (metrics.density >= 2.0 && custom.type == AdFlakeUtil.CUSTOM_TYPE_BANNER && custom.imageLink640x100 != null && custom.imageLink640x100.length() != 0)
			{
				custom.image = fetchImageWithURL(custom.imageLink640x100);
			}
			else if (metrics.density >= 1.5 && custom.type == AdFlakeUtil.CUSTOM_TYPE_BANNER && custom.imageLink480x75 != null && custom.imageLink480x75.length() != 0)
			{
				custom.image = fetchImageWithURL(custom.imageLink480x75);
			}
			else
			{
				custom.image = fetchImageWithURL(custom.imageLink);
			}
		}
		catch (JSONException e)
		{
			Log.e(AdFlakeUtil.ADFLAKE, "Caught JSONException in parseCustomJsonString()", e);
			return null;
		}

		return custom;
	}

	/**
	 * Fetch image with the specified url.
	 * 
	 * @param urlString
	 *            the url string
	 * @return the drawable
	 */
	private Drawable fetchImageWithURL(String urlString)
	{
		try
		{
			URL url = new URL(urlString);
			InputStream is = (InputStream) url.getContent();
			Drawable d = Drawable.createFromStream(is, "src");
			return d;
		}
		catch (Exception e)
		{
			Log.e(AdFlakeUtil.ADFLAKE, "Unable to fetchImage(): ", e);
			return null;
		}
	}

	/**
	 * Gets the current location.
	 * 
	 * @note If location is not enabled, this method will return null
	 * @return the current location or null if location access is not enabled or
	 *         granted by the user.
	 */
	public Location getCurrentLocation()
	{
		if (_contextReference == null)
		{
			return null;
		}

		Context context = _contextReference.get();
		if (context == null)
		{
			return null;
		}

		Location location = null;

		if (context.checkCallingOrSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
		{
			LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		}
		else if (context.checkCallingOrSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
		{
			LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		return location;
	}

	public Ration getNextDartedVideoRation(List<Ration> usedVideoRations)
	{
		Random random = new Random();

		double actualWeight = 0;

		for (Ration ration : _videoRationsList)
		{
			if (usedVideoRations.contains(ration))
				continue;

			actualWeight += ration.weight;
		}

		double r = random.nextDouble() * actualWeight;
		double s = 0;

		Log.d(AdFlakeUtil.ADFLAKE, "Dart is <" + r + "> of <" + actualWeight + "> and total <" + _totalWeight + ">");

		Iterator<Ration> it = this._videoRationsList.iterator();
		Ration ration = null;
		while (it.hasNext())
		{
			ration = it.next();

			if (usedVideoRations.contains(ration))
				continue;

			s += ration.weight;

			if (s >= r)
			{
				break;
			}
		}

		return ration;
	}

	public int getVideoRationCount()
	{
		return _videoRationsList.size();
	}

	public void prepareVideoAdaptersForLayout(AdFlakeLayout adFlakeLayout)
	{
		Log.d(AdFlakeUtil.ADFLAKE, "prepareVideoAdaptersForLayout");
		
		for (Ration ration : _videoRationsList)
		{
			try
			{
				switch (ration.type)
				{
					case AdFlakeUtil.NETWORK_TYPE_ADCOLONY:
						AdColonyVideoAdsAdapter.prepareForRation(ration, adFlakeLayout);
						break;

					default:
						break;
				}
			}
			catch (Throwable ex)
			{
				Log.e(AdFlakeUtil.ADFLAKE, "prepareVideoAdaptersForLayout failed to prepare for ration=" + ration.name + "\n error=" + ex.toString());
			}
		}
	}

	private Extra _extra;
	private List<Ration> _rationsList;
	private List<Ration> _videoRationsList;
	private double _totalWeight = 0;
	private double _totalVideoWeight = 0;
	private WeakReference<Context> _contextReference;

	private Iterator<Ration> _rollovers;

	/** Default config expire timeout is 30 minutes. */
	private static long _configExpireTimeout = 1800000;

	private final static String PREFS_STRING_TIMESTAMP = "timestamp";
	private final static String PREFS_STRING_CONFIG = "config";
}
