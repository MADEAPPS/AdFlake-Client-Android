/**
 * AdFlakeAdapter.java (AdFlakeSDK-Android)
 *
 * Copyright © 2013 MADE GmbH - All Rights Reserved.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * unless otherwise noted in the License section of this document header.
 *
 * @file AdFlakeAdapter.java
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

import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import android.util.Log;

import com.adflake.AdFlakeLayout;
import com.adflake.obj.Ration;
import com.adflake.util.AdFlakeUtil;

/**
 * The abstract AdFlakeAdapter class manages the abstraction between the AdFlake
 * client SDK and the AD network provider SDK.
 */
public abstract class AdFlakeAdapter
{
	/**
	 * Instantiates a new ad flake adapter.
	 * 
	 * @param adFlakeLayout
	 *            the ad flake layout
	 * @param ration
	 *            the ration
	 */
	public AdFlakeAdapter(AdFlakeLayout adFlakeLayout, Ration ration)
	{
		_adFlakeLayoutReference = new WeakReference<AdFlakeLayout>(adFlakeLayout);
		_ration = ration;
	}

	/**
	 * Gets the ration.
	 * 
	 * @return the ration
	 */
	public Ration getRation()
	{
		return _ration;
	}

	/**
	 * Gets the adapter for the specified Ration.
	 * 
	 * @param adFlakeLayout
	 *            the ad flake layout
	 * @param ration
	 *            the ration
	 * @return the adapter
	 */
	private static AdFlakeAdapter getAdapter(AdFlakeLayout adFlakeLayout, Ration ration)
	{
		try
		{				
			switch (ration.type)
			{
				case AdFlakeUtil.NETWORK_TYPE_ADMOB:
					return getInstanceOfNetworkAdapterWithClassName("com.adflake.adapters.GoogleAdMobAdsAdapter", adFlakeLayout, ration);

				case AdFlakeUtil.NETWORK_TYPE_INMOBI:
					return getInstanceOfNetworkAdapterWithClassName("com.adflake.adapters.InMobiAdapter", adFlakeLayout, ration);

				case AdFlakeUtil.NETWORK_TYPE_MILLENNIAL:
					return getInstanceOfNetworkAdapterWithClassName("com.adflake.adapters.MillennialAdapter", adFlakeLayout, ration);

				case AdFlakeUtil.NETWORK_TYPE_KOMLIMOBILE:
					return getInstanceOfNetworkAdapterWithClassName("com.adflake.adapters.KomliMobileAdAdapter", adFlakeLayout, ration);

				case AdFlakeUtil.NETWORK_TYPE_AMAZONADS:
					return getInstanceOfNetworkAdapterWithClassName("com.adflake.adapters.AmazonAdsAdapter", adFlakeLayout, ration);

				case AdFlakeUtil.NETWORK_TYPE_JUMPTAP:
					return getInstanceOfNetworkAdapterWithClassName("com.adflake.adapters.JumptapAdapter", adFlakeLayout, ration);

				case AdFlakeUtil.NETWORK_TYPE_GREYSTRIP:
					return getInstanceOfNetworkAdapterWithClassName("com.adflake.adapters.GreystripeAdapter", adFlakeLayout, ration);

				case AdFlakeUtil.NETWORK_TYPE_MOBCLIX:
					return getInstanceOfNetworkAdapterWithClassName("com.adflake.adapters.MobClixAdapter", adFlakeLayout, ration);

				case AdFlakeUtil.NETWORK_TYPE_MDOTM:
					return getInstanceOfNetworkAdapterWithClassName("com.adflake.adapters.MdotMAdapter", adFlakeLayout, ration);

				case AdFlakeUtil.NETWORK_TYPE_NEXAGE:
					return getInstanceOfNetworkAdapterWithClassName("com.adflake.adapters.NexageAdapter", adFlakeLayout, ration);

				case AdFlakeUtil.NETWORK_TYPE_MOBFOX:
					return getInstanceOfNetworkAdapterWithClassName("com.adflake.adapters.MobFoxAdapter", adFlakeLayout, ration);
					
				case AdFlakeUtil.NETWORK_TYPE_TODACELL:
					return getInstanceOfNetworkAdapterWithClassName("com.adflake.adapters.TodacellAdapter", adFlakeLayout, ration);
					
				case AdFlakeUtil.NETWORK_TYPE_APPBRAIN:
					return getInstanceOfNetworkAdapterWithClassName("com.adflake.adapters.AppBrainAppLiftAdapter", adFlakeLayout, ration);
					
				case AdFlakeUtil.NETWORK_TYPE_CUSTOM:
					return new CustomAdapter(adFlakeLayout, ration);

				case AdFlakeUtil.NETWORK_TYPE_GENERIC:
					return new GenericAdapter(adFlakeLayout, ration);

				case AdFlakeUtil.NETWORK_TYPE_EVENT:
					return new EventAdapter(adFlakeLayout, ration);

				default:
					return handleUnknownAdNetwork(adFlakeLayout, ration);
			}
		}
		catch (Exception error)
		{
			Log.e("AdFlake", "Caught Adapter Allocation Exception:", error);
			return handleUnknownAdNetwork(adFlakeLayout, ration);
		}
	}

	/**
	 * Gets a new instance of the network adapter with the specified class name.
	 *
	 * @param networkAdapter the network adapter
	 * @param adFlakeLayout the ad flake layout
	 * @param ration the ration
	 * @return the instance of network adapter with class name
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws InvocationTargetException the invocation target exception
	 * @throws ClassNotFoundException the class not found exception
	 * @throws NoSuchMethodException the no such method exception
	 */
	private static AdFlakeAdapter getInstanceOfNetworkAdapterWithClassName(String networkAdapter, AdFlakeLayout adFlakeLayout, Ration ration) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, ClassNotFoundException, NoSuchMethodException
	{
		AdFlakeAdapter adFlakeAdapter = null;

		@SuppressWarnings("unchecked")
		Class<? extends AdFlakeAdapter> adapterClass = (Class<? extends AdFlakeAdapter>) Class.forName(networkAdapter);

		Class<?>[] parameterTypes = new Class[2];
		parameterTypes[0] = AdFlakeLayout.class;
		parameterTypes[1] = Ration.class;

		Constructor<? extends AdFlakeAdapter> constructor = adapterClass.getConstructor(parameterTypes);

		Object[] args = new Object[2];
		args[0] = adFlakeLayout;
		args[1] = ration;

		adFlakeAdapter = constructor.newInstance(args);

		return adFlakeAdapter;
	}

	/**
	 * Handle an unknown ad network.
	 * 
	 * @param adFlakeLayout
	 *            the ad flake layout
	 * @param ration
	 *            the ration
	 * @return the ad flake adapter
	 */
	private static AdFlakeAdapter handleUnknownAdNetwork(AdFlakeLayout adFlakeLayout, Ration ration)
	{
		Log.w(AdFlakeUtil.ADFLAKE, "Unsupported ration type: " + ration.type);
		return null;
	}

	/**
	 * Handles the instantiation of the specified network.
	 * 
	 * @param adFlakeLayout
	 *            the ad flake layout
	 * @param ration
	 *            the ration
	 * @return the ad flake adapter
	 * @throws Throwable
	 *             the throwable
	 */
	public static AdFlakeAdapter getAdapterForRation(AdFlakeLayout adFlakeLayout, Ration ration) throws Throwable
	{
		AdFlakeAdapter adapter = AdFlakeAdapter.getAdapter(adFlakeLayout, ration);
		if (adapter != null)
		{
			Log.d(AdFlakeUtil.ADFLAKE, "Valid adapter (" + ration.name + "), calling handle()");
			adapter.handle();
		}
		else
		{
			throw new Exception("Invalid adapter for ration (" + ration.name + ")");
		}
		return adapter;
	}

	/**
	 * Handle the construction of the ad network provider's client view and
	 * trigger the retrieval of a banner ad.
	 */
	public abstract void handle();

	/**
	 * Invoked when the adapter's view will be destroyed.
	 */
	public void willDestroy()
	{
		Log.d(AdFlakeUtil.ADFLAKE, "Generic adapter will get destroyed (" + _ration.name + ")");
	}

	protected final WeakReference<AdFlakeLayout>	_adFlakeLayoutReference;
	protected Ration								_ration;
}
