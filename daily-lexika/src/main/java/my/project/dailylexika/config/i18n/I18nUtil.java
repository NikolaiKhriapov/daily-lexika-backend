package my.project.dailylexika.config.i18n;

import my.project.library.dailylexika.enumerations.Language;
import my.project.dailylexika.user.model.entities.User;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Locale;
import java.util.ResourceBundle;

public class I18nUtil {

    private I18nUtil() {}

    public static final Locale DEFAULT_LOCALE = new Locale("EN");
    public static final String RESOURCE_BUNDLES_DIRECTORY = "resourcebundles/";

    public static String getMessage(String propertyKey, Object... args) {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Language interfaceLanguage = user.getInterfaceLanguage();

            Locale locale = DEFAULT_LOCALE;
            if (interfaceLanguage != null) {
                switch (interfaceLanguage) {
                    case ENGLISH -> locale = new Locale("EN");
                    case RUSSIAN -> locale = new Locale("RU");
                    case CHINESE -> locale = new Locale("CH");
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
