package foodmap.V2.config;

import groovy.util.logging.Slf4j;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ReactorResourceFactory;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.util.function.Function;

@Configuration
@Slf4j
public class WebClientConfig {

    @Bean
    public ReactorResourceFactory resourceFactory() {
        ReactorResourceFactory factory = new ReactorResourceFactory();
        factory.setUseGlobalResources(false);
        return factory;
    }
    @Bean
    public WebClient webClient() {
        Function<HttpClient, HttpClient> mapper = client -> client;
        ClientHttpConnector connector =
                new ReactorClientHttpConnector(resourceFactory(), mapper);
        return WebClient.builder().clientConnector(connector).build();
    }
}
