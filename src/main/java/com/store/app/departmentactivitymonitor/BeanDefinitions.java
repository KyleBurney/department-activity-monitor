package com.store.app.departmentactivitymonitor;

import com.store.app.departmentactivitymonitor.config.ActivityMonitorConfig;
import com.store.app.departmentactivitymonitor.streaming.ActivityMonitorService;
import com.store.app.departmentactivitymonitor.streaming.ProducerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class BeanDefinitions {

    /**
     * Will provide a logger with a name associated to the declaring class.
     */
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Logger logger(InjectionPoint ip) {
        return logger(ip.getMember().getDeclaringClass());
    }

    /**
     * Helper intended for internal usage (not a bean)
     *
     * Will take an array of Objects, toString and concat them together separated by a hyphen '-'
     * to use as the name for a logger.
     */
    protected Logger logger(Object... args) {
        String[] strings = Arrays.stream(args).map(x -> {
            if (x instanceof Class) {
                return ((Class)x).getSimpleName();
            }
            return x.toString();
        }).toArray(String[]::new);
        String joined = String.join("-", strings);
        return LoggerFactory.getLogger(joined);
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        builder.setConnectTimeout(5000);
        builder.additionalMessageConverters(new StringHttpMessageConverter());
        return builder.build();
    }

    /**
     * The application service pool, each app service will run in a separate thread.
     */
    @Bean
    public AppServicePool appServicePool(ActivityMonitorConfig config,
                                         ListableBeanFactory beanFactory,
                                         ProducerFactory producerFactory) {
        List<AppService> services = new ArrayList<>(beanFactory.getBeansOfType(AppService.class).values());
        Logger logger = logger(ActivityMonitorService.class);
        services.add(new ActivityMonitorService(config, logger, producerFactory.createAvroProducer()));
        return new AppServicePool(LoggerFactory.getLogger(AppServicePool.class), services);
    }
}
