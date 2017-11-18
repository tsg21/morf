package com.chpconsulting.cryo.view;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Messages resource implementation for providing translatable strings.
 *
 * <p>The structure of this class was automatically generated using Eclipse.</p>
 *
 * @author Copyright (c) CHP Consulting Ltd. 2008
 */
final class Messages {

  /**
   * Standard resource bundle object.
   */
  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(Messages.class.getName().toLowerCase());

  /**
   * Suffix used on message keys for retrieving captions.
   */
  private static final String CAPTION_SUFFIX = ".caption";

  /**
   * Suffix used on message keys for user prompts.
   */
  private static final String PROMPT_SUFFIX = ".prompt";

  /**
   * Private constructor since this is a static implementation.
   */
  private Messages() {
    super();
  }


  /**
   * Retrieve the display string for a given key.
   *
   * @param key Message identifier.
   * @return Message text from the resource bundle.
   */
  private static String getString(String key) {
    try {
      return RESOURCE_BUNDLE.getString(key);
    } catch (MissingResourceException e) {
      return '!' + key + '!';
    }
  }


  /**
   * Retrieves a message that has the <var>args</var> substituted into it.
   *
   * @param key Message key.
   * @param args Substitution values.
   * @return Message text from the resource bundle.
   */
  public static String getMessage(String key, Object... args) {
    String result = getString(key);
    for (int i = 0; i < args.length; i++) {
      result = result.replaceAll("\\{" + i + "\\}", args[i].toString());
    }
    return result;
  }


  /**
   * @param enumeration The enumeration for which a user facing decode is required.
   * @return The user facing name for this enumeration.
   */
  public static String getEnumerationCaption(Object enumeration) {
    return getCaption(enumeration.getClass().getName().replaceAll(".*\\.(\\w+).*", "$1") + "." + enumeration.toString());
  }

  /**
   * Wrapper method for {@link #getString(String)} that appends .caption to key requests. This
   * is intended to standardise the format of caption strings.
   *
   * @param key Message key.
   * @return Message text from the resource bundle.
   */
  public static String getCaption(String key) {
    return getString(key + CAPTION_SUFFIX);
  }


  /**
   * Wrapper method for {@link #getString(String)} that appends .prompt to key requests. This
   * is intended to standardise the format of caption strings.
   *
   * @param key Message key.
   * @return Message text from the resource bundle.
   */
  public static String getPrompt(String key) {
    return getString(key + PROMPT_SUFFIX);
  }

}
