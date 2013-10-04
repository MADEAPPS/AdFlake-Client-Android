/**
 * Extra.java (AdFlakeSDK-Android)
 *
 * Copyright © 2013 MADE GmbH - All Rights Reserved.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * unless otherwise noted in the License section of this document header.
 *
 * @file Extra.java
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

/**
 * The Extra class stores Extra configuration data as received from the AdFlake
 * server.
 */
public class Extra
{
	public int	fgRed		= 255;
	public int	fgGreen		= 255;
	public int	fgBlue		= 255;
	public int	fgAlpha		= 1;

	public int	bgRed		= 0;
	public int	bgGreen		= 0;
	public int	bgBlue		= 0;
	public int	bgAlpha		= 1;

	public int	cycleTime	= 30;
	public int	locationOn	= 1;
	public int	transition	= 1;

	/**
	 * Instantiates a new extra.
	 */
	public Extra()
	{
	}
}
