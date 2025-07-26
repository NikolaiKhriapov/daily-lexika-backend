package my.project.dailylexika.config;

import my.project.library.dailylexika.interfaces.user.HasInterfaceLanguage;
import my.project.library.dailylexika.enumerations.Language;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Locale;
import java.util.ResourceBundle;

public class I18nUtil {

    private I18nUtil() {}

    public static final Locale DEFAULT_LOCALE = new Locale("EN");
    public static final String RESOURCE_BUNDLES_DIRECTORY = "resourcebundles/";

    public static String getMessage(String propertyKey, Object... args) {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Locale locale = DEFAULT_LOCALE;
            if (principal instanceof HasInterfaceLanguage langProvider) {
                Language interfaceLanguage = langProvider.getInterfaceLanguage();
                if (interfaceLanguage != null) {
                    locale = switch (interfaceLanguage) {
                        case ENGLISH -> new Locale("EN");
                        case RUSSIAN -> new Locale("RU");
                        case CHINESE -> new Locale("CH");
                    };
                }
            }
            return getResourceBundleMessage(locale, propertyKey, args);
        } catch (RuntimeException e) {
            return getResourceBundleMessage(DEFAULT_LOCALE, propertyKey, args);
        }
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
