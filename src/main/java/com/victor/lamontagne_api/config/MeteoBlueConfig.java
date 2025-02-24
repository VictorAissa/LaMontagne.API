package com.victor.lamontagne_api.config;

import io.netty.channel.ChannelOption;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
@ConfigurationProperties(prefix = "meteoblue")
@Data
public class MeteoBlueConfig {
    private String url;
    private String apiKey;

    private String format = "json";
    private int connectTimeout = 5000;
    private int readTimeout = 5000;

    @Bean
    public WebClient meteoblueWebClient() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout)
                .responseTimeout(Duration.ofMillis(readTimeout));

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
                .build();
    }

    public String buildRequestUrl(double latitude, double longitude) {
        return UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("lat", latitude)
                .queryParam("lon", longitude)
                .queryParam("apikey", apiKey)
                .queryParam("format", format)
                .build()
                .toUriString();
    }
}
