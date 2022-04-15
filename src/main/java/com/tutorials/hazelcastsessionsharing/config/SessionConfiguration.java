package com.tutorials.hazelcastsessionsharing.config;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientUserCodeDeploymentConfig;
import com.hazelcast.core.HazelcastInstance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.security.web.savedrequest.DefaultSavedRequest;
//import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.session.FlushMode;
import org.springframework.session.MapSession;
import org.springframework.session.SaveMode;
import org.springframework.session.Session;
import org.springframework.session.config.SessionRepositoryCustomizer;
import org.springframework.session.hazelcast.Hazelcast4IndexedSessionRepository;
import org.springframework.session.hazelcast.Hazelcast4SessionUpdateEntryProcessor;
import org.springframework.session.hazelcast.config.annotation.SpringSessionHazelcastInstance;
import org.springframework.session.hazelcast.config.annotation.web.http.EnableHazelcastHttpSession;

@Configuration
@EnableHazelcastHttpSession
@Slf4j
public class SessionConfiguration {

  private final String SESSIONS_MAP_NAME = "crp-session-map";

  @Bean
  @SpringSessionHazelcastInstance
  public HazelcastInstance hazelcastInstance() {
    ClientConfig clientConfig = new ClientConfig();
    clientConfig.setClusterName("hello-world");
    //clientConfig.getNetworkConfig().addAddress("172.18.0.2:5701");

    ClientUserCodeDeploymentConfig clientUserCodeDeploymentConfig = new ClientUserCodeDeploymentConfig();
    clientUserCodeDeploymentConfig.setEnabled(true);
    clientUserCodeDeploymentConfig.addClass(Session.class)
        .addClass(MapSession.class)
        .addClass(Hazelcast4SessionUpdateEntryProcessor.class);
    /* clientConfig.setClassLoader(Session.class.getClassLoader());*/

    clientConfig.setUserCodeDeploymentConfig(clientUserCodeDeploymentConfig);
    return HazelcastClient.newHazelcastClient(clientConfig);
  }

  @Bean
  public SessionRepositoryCustomizer<Hazelcast4IndexedSessionRepository>
  customize() {
    return (sessionRepository) -> {
      sessionRepository.setFlushMode(FlushMode.IMMEDIATE);
      sessionRepository.setSaveMode(SaveMode.ALWAYS);
      sessionRepository.setSessionMapName(SESSIONS_MAP_NAME);
      sessionRepository.setDefaultMaxInactiveInterval(900);
    };
  }
}

