package com.tutorials.hazelcastsessionsharing.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;

@Configuration
public class CustomWebSecurityConfigurerAdapter<S extends Session> extends
    WebSecurityConfigurerAdapter {

  @Autowired
  private FindByIndexNameSessionRepository<S> sessionRepository;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // @formatter:off
    http
        // other config goes here...
        .sessionManagement((sessionManagement) -> sessionManagement
            .maximumSessions(2)
            .sessionRegistry(sessionRegistry())
        );
    // @formatter:on
  }

  @Bean
  public SpringSessionBackedSessionRegistry<S> sessionRegistry() {
    return new SpringSessionBackedSessionRegistry<>(this.sessionRepository);
  }

}