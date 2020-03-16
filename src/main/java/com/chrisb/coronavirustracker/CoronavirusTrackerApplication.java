package com.chrisb.coronavirustracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

@SpringBootApplication
@EnableScheduling
public class CoronavirusTrackerApplication implements WebMvcConfigurer {

    public static void main(String[] args) {
	SpringApplication.run(CoronavirusTrackerApplication.class, args);
    }

    @Bean
    public LocaleResolver localeResolver() {
	return new CookieLocaleResolver();
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
	LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
	lci.setParamName("lang");
	return lci;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
	registry.addInterceptor(localeChangeInterceptor());
    }

    @Bean
    public MessageSource messageSource() {
	ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
	messageSource.setBasename("i18n/messages");
	messageSource.setDefaultEncoding("UTF-8");
	return messageSource;
    }
}
