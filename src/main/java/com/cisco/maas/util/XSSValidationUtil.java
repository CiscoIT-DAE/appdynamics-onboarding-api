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
