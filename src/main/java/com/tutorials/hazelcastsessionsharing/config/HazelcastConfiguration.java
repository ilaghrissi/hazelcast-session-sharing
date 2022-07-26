package com.tutorials.hazelcastsessionsharing.config;

import com.hazelcast.config.AttributeConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.IndexConfig;
import com.hazelcast.config.IndexType;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.FlushMode;
import org.springframework.session.SaveMode;
import org.springframework.session.config.SessionRepositoryCustomizer;
import org.springframework.session.hazelcast.Hazelcast4IndexedSessionRepository;
import org.springframework.session.hazelcast.Hazelcast4PrincipalNameExtractor;
import org.springframework.session.hazelcast.config.annotation.SpringSessionHazelcastInstance;
import org.springframework.session.hazelcast.config.annotation.web.http.EnableHazelcastHttpSession;

@Configuration
@EnableHazelcastHttpSession
public class HazelcastConfiguration {

  private final String SESSIONS_MAP_NAME = "crp-session-map";

  @Bean
  @SpringSessionHazelcastInstance
  public HazelcastInstance hazelcastInstance() {
    Config config = new Config();
    config.setClusterName("dev");

    AttributeConfig attributeConfig = new AttributeConfig()
        .setName(Hazelcast4IndexedSessionRepository.PRINCIPAL_NAME_ATTRIBUTE)
        .setExtractorClassName(Hazelcast4PrincipalNameExtractor.class.getName());

    config.getMapConfig(SESSIONS_MAP_NAME)
        .addAttributeConfig(attributeConfig).addIndexConfig(
            new IndexConfig(IndexType.HASH, Hazelcast4IndexedSessionRepository.PRINCIPAL_NAME_ATTRIBUTE));

    config.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(true);
    config.getNetworkConfig().getJoin().getTcpIpConfig().addMember("192.168.1.78");

  /*  config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
    config.getNetworkConfig().getJoin().getKubernetesConfig().setEnabled(true)
        .setProperty("namespace", "development")
        .setProperty("service-name", "hz-service");*/

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

