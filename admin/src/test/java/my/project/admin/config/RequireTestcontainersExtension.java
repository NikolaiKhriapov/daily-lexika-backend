package my.project.admin.config;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionConfigurationException;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.junit.jupiter.Container;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class RequireTestcontainersExtension implements BeforeAllCallback {

    @Override
    public void beforeAll(ExtensionContext context) {
        Class<?> testClass = context.getRequiredTestClass();
        if (!hasStaticContainerField(testClass)) {
            throw new ExtensionConfigurationException("Integration test class " + testClass.getName() + " must declare a static @Container field for per-class Testcontainers.");
        }
    }

    private boolean hasStaticContainerField(Class<?> type) {
        for (Class<?> current = type; current != null; current = current.getSuperclass()) {
            for (Field field : current.getDeclaredFields()) {
                if (field.isAnnotationPresent(Container.class)
                        && Modifier.isStatic(field.getModifiers())) {
                    return true;
                }
            }
        }
        return false;
    }
}
