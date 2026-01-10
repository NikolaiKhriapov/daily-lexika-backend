package my.project.dailylexika.util;

import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

public final class ValidationTestSupport {

    private ValidationTestSupport() {
    }

    public static <T> T validatedProxy(T target, String beanName, Class<T> iface) {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
        processor.setValidator(validator);
        processor.afterPropertiesSet();
        return iface.cast(processor.postProcessAfterInitialization(target, beanName));
    }
}
