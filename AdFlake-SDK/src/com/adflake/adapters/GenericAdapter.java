/**
 * GenericAdapter.java (AdFlakeSDK-Android)
 *
 * Copyright © 2013 MADE GmbH - All Rights Reserved.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * unless otherwise noted in the License section of this document header.
 *
 * @file GenericAdapter.java
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

import android.util.Log;

/**
 * The class GenericAdapter invokes the generic notification on the
 * adFlakeInterface listener.
 */
public class GenericAdapter extends AdFlakeAdapter
{
	/**
	 * Instantiates a new generic adapter.
	 *
	 * @param adFlakeLayout the ad flake layout
	 * @param ration the ration
	 */
	public GenericAdapter(AdFlakeLayout adFlakeLayout, Ration ration)
	{
		super(adFlakeLayout, ration);
	}

	/* (non-Javadoc)
	 * @see com.adflake.adapters.AdFlakeAdapter#handle()
	 */
	@Override
	public void handle()
	{
		Log.d(AdFlakeUtil.ADFLAKE, "Generic notification request initiated");

		AdFlakeLayout adFlakeLayout = _adFlakeLayoutReference.get();
		if (adFlakeLayout == null)
		{
			return;
		}

		// If the user set a handler for notifications, call it
		if (adFlakeLayout.adFlakeInterface != null)
		{
			adFlakeLayout.adFlakeInterface.adFlakeGeneric();
		}
		else
		{
			Log.w(AdFlakeUtil.ADFLAKE, "Generic notification sent, but no interface is listening");
		}

		adFlakeLayout.adFlakeManager.resetRollover();
		adFlakeLayout.rotateThreadedDelayed();
	}
}
