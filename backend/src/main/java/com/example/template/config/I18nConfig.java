package com.example.template.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
public class I18nConfig {
  @Bean
  MessageSource messageSource() {
    var ms = new ResourceBundleMessageSource();
    ms.setBasenames("i18n/messages");
    ms.setDefaultEncoding("UTF-8");
    return ms;
  }
}
