package com.kit.maximus.freshskinweb.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.opensearch.client.RestClient;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.OpenSearchTransport;
import org.opensearch.client.transport.rest_client.RestClientTransport;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:application.properties")
@Configuration
public class OpenSearchConfig {

    @Value("${opensearch.host}")
    private String openSearchHost;

    @Value("${opensearch.port}")
    private int openSearchPort; // Dùng kiểu số cho cổng

    @Value("${opensearch.username}")
    private String username;

    @Value("${opensearch.password}")
    private String password;

    @Bean
    public OpenSearchClient openSearchClient() {
        // Khởi tạo HttpHost với host và port từ properties
        HttpHost httpHost = new HttpHost(openSearchHost, openSearchPort, "https");

        // Cấu hình Basic Auth
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));

        // Tạo RestClient với Basic Auth
//        RestClient restClient = RestClient.builder(httpHost)
//                .setHttpClientConfigCallback(httpClientBuilder ->
//                        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider))
//                .build();

        RestClient restClient = RestClient.builder(httpHost)
                .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
                        .setDefaultCredentialsProvider(credentialsProvider)
                        .setMaxConnTotal(50) // Tối đa 50 kết nối
                        .setMaxConnPerRoute(20) // Mỗi route tối đa 20 kết nối
                )
                .build();

        // Tạo OpenSearchTransport
        OpenSearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());

        return new OpenSearchClient(transport);
    }




}
