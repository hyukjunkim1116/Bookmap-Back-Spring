package foodmap.V2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
@Configuration
@EnableWebSocketSecurity
public class SecurityWebSocketConfig  {

    @Bean
    AuthorizationManager<Message<?>> messageAuthorizationManager(MessageMatcherDelegatingAuthorizationManager.Builder messages) {
        messages
                .nullDestMatcher().permitAll()
                .simpDestMatchers("/notification/**","/webchat/**").permitAll()
                .anyMessage().denyAll();
        return messages.build();
    }
}

