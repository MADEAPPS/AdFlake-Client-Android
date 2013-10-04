/**
 * AdFlakeTargeting.java (AdFlakeSDK-Android)
 *
 * Copyright © 2013 MADE GmbH - All Rights Reserved.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * unless otherwise noted in the License section of this document header.
 *
 * @file AdFlakeTargeting.java
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

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

/**
 * The AdFlakeTargeting class provides methods to specify targeting that is
 * common among all AdFlakeAdapter implementations.
 */
public class AdFlakeTargeting
{
	/** The Test mode. */
	private static boolean				_testMode;

	/** The Gender. */
	private static Gender				_gender;

	/** The Birth date. */
	private static GregorianCalendar	_birthDate;

	/** The Postal code. */
	private static String				_postalCode;

	/** The Keywords. */
	private static String				_keywords;

	/** The Keyword set. */
	private static Set<String>			_keywordSet;

	/** The Company name. */
	private static String				_companyName;

	static
	{
		resetData();
	}

	/**
	 * The Enum Gender.
	 */
	public static enum Gender
	{

		/** The unknown. */
		UNKNOWN,
		/** The male. */
		MALE,
		/** The female. */
		FEMALE
	}

	/**
	 * Reset data.
	 */
	public static void resetData()
	{
		_testMode = false;
		_gender = Gender.UNKNOWN;
		_birthDate = null;
		_postalCode = null;
		_keywords = null;
		_keywordSet = null;
		_companyName = "";
	}

	/**
	 * Gets the test mode.
	 * 
	 * @return the test mode
	 */
	public static boolean getTestMode()
	{
		return _testMode;
	}

	/**
	 * Sets the test mode.
	 * 
	 * @param testMode
	 *            the new test mode
	 */
	public static void setTestMode(boolean testMode)
	{
		_testMode = testMode;
	}

	/**
	 * Gets the gender.
	 * 
	 * @return the gender
	 */
	public static Gender getGender()
	{
		return _gender;
	}

	/**
	 * Sets the gender.
	 * 
	 * @param gender
	 *            the new gender
	 */
	public static void setGender(Gender gender)
	{
		if (gender == null)
		{
			gender = Gender.UNKNOWN;
		}

		_gender = gender;
	}

	/**
	 * Gets the age.
	 * 
	 * @return the age
	 */
	public static int getAge()
	{
		if (_birthDate != null)
		{
			return Calendar.getInstance().get(Calendar.YEAR) - _birthDate.get(Calendar.YEAR);
		}

		return -1;
	}

	/**
	 * Gets the birth date.
	 * 
	 * @return the birth date
	 */
	public static GregorianCalendar getBirthDate()
	{
		return _birthDate;
	}

	/**
	 * Sets the birth date.
	 * 
	 * @param birthDate
	 *            the new birth date
	 */
	public static void setBirthDate(GregorianCalendar birthDate)
	{
		_birthDate = birthDate;
	}

	/**
	 * Sets the age.
	 * 
	 * @param age
	 *            the new age
	 */
	public static void setAge(int age)
	{
		_birthDate = new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR) - age, 0, 1);
	}

	/**
	 * Gets the postal code.
	 * 
	 * @return the postal code
	 */
	public static String getPostalCode()
	{
		return _postalCode;
	}

	/**
	 * Sets the postal code.
	 * 
	 * @param postalCode
	 *            the new postal code
	 */
	public static void setPostalCode(String postalCode)
	{
		_postalCode = postalCode;
	}

	/**
	 * Gets the keyword set.
	 * 
	 * @return the keyword set
	 */
	public static Set<String> getKeywordSet()
	{
		return _keywordSet;
	}

	/**
	 * Gets the keywords.
	 * 
	 * @return the keywords
	 */
	public static String getKeywords()
	{
		return _keywords;
	}

	/**
	 * Sets the keyword set.
	 * 
	 * @param keywords
	 *            the new keyword set
	 */
	public static void setKeywordSet(Set<String> keywords)
	{
		_keywordSet = keywords;
	}

	/**
	 * Sets the keywords.
	 * 
	 * @param keywords
	 *            the new keywords
	 */
	public static void setKeywords(String keywords)
	{
		_keywords = keywords;
	}

	/**
	 * Adds the keyword.
	 * 
	 * @param keyword
	 *            the keyword
	 */
	public static void addKeyword(String keyword)
	{
		if (_keywordSet == null)
		{
			_keywordSet = new HashSet<String>();
		}
		_keywordSet.add(keyword);
	}

	/**
	 * Gets the company name.
	 * 
	 * @return the company name
	 */
	public static String getCompanyName()
	{
		return _companyName;
	}

	/**
	 * Sets the company name.
	 * 
	 * @param companyName
	 *            the new company name
	 */
	public static void setCompanyName(String companyName)
	{
		_companyName = companyName;
	}
}
