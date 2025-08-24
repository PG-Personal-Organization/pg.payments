package pg.payments.infrastructure.spring.configuration;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan("pg.payments.infrastructure.persistence")
@EnableJpaRepositories("pg.payments.infrastructure.persistence")
public class PaymentsPersistenceConfiguration {
}
