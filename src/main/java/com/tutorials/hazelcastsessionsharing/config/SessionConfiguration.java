package com.tutorials.hazelcastsessionsharing.config;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientUserCodeDeploymentConfig;
import com.hazelcast.config.AttributeConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.IndexConfig;
import com.hazelcast.config.IndexType;
import com.hazelcast.config.IntegrityCheckerConfig;
import com.hazelcast.config.UserCodeDeploymentConfig;
import com.hazelcast.core.Hazelcast;
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
import org.springframework.session.hazelcast.Hazelcast4PrincipalNameExtractor;
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
    Config config = new Config();
    config.setClusterName("hello-world");

    // Add this attribute to be able to query sessions by their PRINCIPAL_NAME_ATTRIBUTE's
    AttributeConfig attributeConfig = new AttributeConfig()
        .setName(Hazelcast4IndexedSessionRepository.PRINCIPAL_NAME_ATTRIBUTE)
        .setExtractorClassName(Hazelcast4PrincipalNameExtractor.class.getName());
    config.setIntegrityCheckerConfig(new IntegrityCheckerConfig().setEnabled(true));
    // Configure the sessions map
    config.getMapConfig(SESSIONS_MAP_NAME)
        .addAttributeConfig(attributeConfig).addIndexConfig(
            new IndexConfig(IndexType.HASH, Hazelcast4IndexedSessionRepository.PRINCIPAL_NAME_ATTRIBUTE));

    /*config.getUserCodeDeploymentConfig()
        .setEnabled(true)
        .setClassCacheMode(UserCodeDeploymentConfig.ClassCacheMode.ETERNAL)
        .setProviderMode(UserCodeDeploymentConfig.ProviderMode.LOCAL_AND_CACHED_CLASSES)
        .setBlacklistedPrefixes("com.arval.crp")
        //.setWhitelistedPrefixes("com.bar.MyClass")
        .setProviderFilter("HAS_ATTRIBUTE:lite");*/

    return Hazelcast.newHazelcastInstance(config);
  }

  @Bean
  public SessionRepositoryCustomizer<Hazelcast4IndexedSessionRepository> customize() {
    return (sessionRepository) -> {
      sessionRepository.setFlushMode(FlushMode.IMMEDIATE);
      sessionRepository.setSaveMode(SaveMode.ALWAYS);
      sessionRepository.setSessionMapName(SESSIONS_MAP_NAME);
      sessionRepository.setDefaultMaxInactiveInterval(900);
    };
  }
}

