package com.booking.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import jakarta.annotation.PostConstruct;

@Configuration
@EnableElasticsearchRepositories(
        basePackages = "com.booking.repository.elasticsearch"
)
public class ElasticsearchConfig
        extends ElasticsearchConfiguration {

	@Value("${spring.elasticsearch.host:elasticsearch}")
	private String elasticsearchHost;

	@Value("${spring.elasticsearch.port:9200}")
	private int elasticsearchPort;

	@Override
	public ClientConfiguration clientConfiguration() {
	    return ClientConfiguration.builder()
	            .connectedTo(elasticsearchHost + ":" + elasticsearchPort)
	            .withConnectTimeout(Duration.ofSeconds(5))
	            .withSocketTimeout(Duration.ofSeconds(5))
	            .build();
	}
	@PostConstruct
	public void printElasticUrl() {
	    // ✅ Use the new field name
	    System.out.println("ELASTIC URL = " + elasticsearchHost + ":" + elasticsearchPort);
	}
}