/*
 *  AppDynamics Onboarding APIs.
 *
 *  Copyright 2022 Cisco
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

/*
 *
 * Cross Site script validation like <script>alert('XSS')</script>
 *
 */

package com.cisco.maas.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is used for automation and validation of XSS vulnerabilities.
 *
 * It is used in the create, update and view functions of the Controller API.
 */
public class XSSValidationUtil {
	
   private XSSValidationUtil() {
	    throw new IllegalStateException("Utility class");
	  }
	
   private static Pattern[] patternList =
      new Pattern[] {
        Pattern.compile(".*<script>(.*?)</script>.*", Pattern.CASE_INSENSITIVE),
        Pattern.compile(
            ".*src[\r\n]*=[\r\n]*\\\'(.*?)\\\'.*",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        Pattern.compile(
            ".*src[\r\n]*=[\r\n]*\\\"(.*?)\\\".*",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        Pattern.compile("", Pattern.CASE_INSENSITIVE),
        Pattern.compile(
            ".*<script(.*?).*>.*", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        Pattern.compile("", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        Pattern.compile(
            ".*eval\\((.*?)\\).*", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        Pattern.compile(
            ".*expression\\((.*?)\\).*", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        Pattern.compile("javascript:.*", Pattern.CASE_INSENSITIVE),
        Pattern.compile(".*<script>.*", Pattern.CASE_INSENSITIVE),
        Pattern.compile("vbscript:.*", Pattern.CASE_INSENSITIVE),
        Pattern.compile(
            "onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL)
      };

  public static boolean xSSAttackValidation(String value) {

    if (value != null) {
      for (Pattern scriptPattern : patternList) {
        Matcher patternMatcher = scriptPattern.matcher(value);
        if (patternMatcher.matches()) {
        	return false;
        }
      }
    }
    return true;
  }
}
