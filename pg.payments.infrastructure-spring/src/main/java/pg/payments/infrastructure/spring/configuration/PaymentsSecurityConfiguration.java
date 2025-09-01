package pg.payments.infrastructure.spring.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import pg.imports.plugin.infrastructure.spring.http.ImportsHttpPaths;
import pg.lib.awsfiles.infrastructure.common.HttpServicesPaths;
import pg.lib.common.spring.user.Roles;

@Configuration
public class PaymentsSecurityConfiguration {

    @Bean
    public Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> paymentsRequestCustomizer() {
        return requests -> requests
                        .requestMatchers("/" + HttpServicesPaths.UPLOAD_FULL_PATH).hasRole(Roles.USER.name())
                        .requestMatchers("/" + ImportsHttpPaths.BASE_PATH + "/**").hasRole(Roles.USER.name());
    }
}
