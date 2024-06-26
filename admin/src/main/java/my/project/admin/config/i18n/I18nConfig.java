package my.project.admin.config.i18n;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class I18nConfig implements WebMvcConfigurer {

    @Bean
    public MessageSource messageSource() throws IOException {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath*:resourcebundles/*.properties");
        List<String> baseNames = new ArrayList<>();
        for (Resource resource : resources) {
            String uri = resource.getURI().toString();
            String baseName = uri.substring(uri.indexOf("resourcebundles/"), uri.lastIndexOf("."));
            baseNames.add("classpath:" + baseName);
        }

        messageSource.setBasenames(baseNames.toArray(new String[0]));
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
