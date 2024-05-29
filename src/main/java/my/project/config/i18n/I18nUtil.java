package my.project.config.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

public class I18nUtil {

    private I18nUtil() {}

    public static final Locale DEFAULT_LOCALE = new Locale("EN");
    public static final String RESOURCE_BUNDLES_DIRECTORY = "resourcebundles/";

    public static String getMessage(String propertyKey, Object... args) {
        return getResourceBundleMessage(DEFAULT_LOCALE, propertyKey, args);
    }

    public static String getMessage(Locale locale, String propertyKey, Object... args) {
        return getResourceBundleMessage(locale, propertyKey, args);
    }

    private static String getResourceBundleMessage(Locale locale, String propertyKey, Object... args) {
        int indexOfFirstDot = propertyKey.indexOf(".");
        String bundleName = propertyKey.substring(0, indexOfFirstDot);
        String bundleFullName = RESOURCE_BUNDLES_DIRECTORY + bundleName;
        return ResourceBundle
                .getBundle(bundleFullName, locale)
                .getString(propertyKey)
                .formatted(args);
    }
}
