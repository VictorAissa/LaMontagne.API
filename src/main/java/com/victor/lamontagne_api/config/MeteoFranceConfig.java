package com.victor.lamontagne_api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.netty.http.client.HttpClient;

import io.netty.channel.ChannelOption;
import java.time.Duration;

@Configuration
@ConfigurationProperties(prefix = "meteo.meteofrance")
@Data
public class MeteoFranceConfig {
    private String url;
    private String apikey;
    private boolean enabled = true;

    private int connectTimeout = 5000;
    private int readTimeout = 5000;

    @Bean
    public WebClient meteoFranceWebClient() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout)
                .responseTimeout(Duration.ofMillis(readTimeout));

        ExchangeStrategies strategies = ExchangeStrategies
                .builder()
                .codecs(clientCodecConfigurer -> {
                    clientCodecConfigurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024);
                })
                .build();

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(strategies)
                .build();
    }

    public String buildBraRequestUrl(String massifId) {
        return UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("id-massif", massifId)
                .queryParam("format", "xml")
                .queryParam("apikey", apikey)
                .build()
                .toUriString();
    }
}