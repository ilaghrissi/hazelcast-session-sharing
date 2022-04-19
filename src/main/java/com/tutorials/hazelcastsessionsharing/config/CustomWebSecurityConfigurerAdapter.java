package com.tutorials.hazelcastsessionsharing.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;

@Configuration
@EnableWebSecurity
public class CustomWebSecurityConfigurerAdapter<S extends Session> extends
    WebSecurityConfigurerAdapter {

  @Autowired
  private FindByIndexNameSessionRepository<S> sessionRepository;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // @formatter:off
    http
        .authorizeRequests()
        .antMatchers("/error").permitAll()
        .anyRequest().authenticated()
        .and().httpBasic().and()
        .csrf().disable()
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

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.inMemoryAuthentication()
        .withUser("imad").password("{noop}1234").roles("user");
  }

}