/**
 * EventAdapter.java (AdFlakeSDK-Android)
 *
 * Copyright © 2013 MADE GmbH - All Rights Reserved.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * unless otherwise noted in the License section of this document header.
 *
 * @file EventAdapter.java
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
import com.adflake.AdFlakeLayout.AdFlakeInterface;
import com.adflake.obj.Ration;
import com.adflake.util.AdFlakeUtil;

import android.util.Log;

import java.lang.reflect.Method;

/**
 * The EventAdapter class manages custom events as configured on the AdFlake
 * website.
 */
public class EventAdapter extends AdFlakeAdapter
{

	/**
	 * Instantiates a new event adapter.
	 * 
	 * @param adFlakeLayout
	 *            the ad flake layout
	 * @param ration
	 *            the ration
	 */
	public EventAdapter(AdFlakeLayout adFlakeLayout, Ration ration)
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
		Log.d(AdFlakeUtil.ADFLAKE, "Event notification request initiated");

		AdFlakeLayout adFlakeLayout = _adFlakeLayoutReference.get();
		if (adFlakeLayout == null)
		{
			return;
		}

		// If the user set a handler for notifications, call it
		if (adFlakeLayout.adFlakeInterface != null)
		{
			String key = this._ration.key;
			String method = null;
			if (key == null)
			{
				Log.w(AdFlakeUtil.ADFLAKE, "Event key is null");
				adFlakeLayout.rollover();
				return;
			}

			int methodIndex = key.indexOf("|;|");
			if (methodIndex < 0)
			{
				Log.w(AdFlakeUtil.ADFLAKE, "Event key separator not found");
				adFlakeLayout.rollover();
				return;
			}

			method = key.substring(methodIndex + 3);

			Class<? extends AdFlakeInterface> listenerClass = adFlakeLayout.adFlakeInterface.getClass();
			Method listenerMethod;
			try
			{
				listenerMethod = listenerClass.getMethod(method, (Class[]) null);
				listenerMethod.invoke(adFlakeLayout.adFlakeInterface, (Object[]) null);
			}
			catch (Exception e)
			{
				Log.e(AdFlakeUtil.ADFLAKE, "Caught exception in handle()", e);
				adFlakeLayout.rollover();
				return;
			}
		}
		else
		{
			Log.w(AdFlakeUtil.ADFLAKE, "Event notification would be sent, but no interface is listening");
			adFlakeLayout.rollover();
			return;
		}

		// In your custom event code, you'll want to call some of the below
		// methods.
		//
		// On success:
		// adFlakeLayout.adapterDidReceiveAd
		//
		// On failure:
		// adFlakeLayout.adapterDidFailToReceiveAdWithError
	}
}
