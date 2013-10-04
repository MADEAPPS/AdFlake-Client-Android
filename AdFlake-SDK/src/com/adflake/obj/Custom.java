/**
 * Custom.java (AdFlakeSDK-Android)
 *
 * Copyright © 2013 MADE GmbH - All Rights Reserved.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * unless otherwise noted in the License section of this document header.
 *
 * @file Custom.java
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

package com.adflake.obj;

import android.graphics.drawable.Drawable;

/**
 * The Custom class stores House-Ad configuration data as received from the
 * AdFlake server.
 */
public class Custom
{
	public int		type;
	public String	link;
	public Drawable	image;
	public String	imageLink;
	public String	imageLink480x75;
	public String	imageLink640x100;
	public String	description;

	public Custom()
	{
	}
}
