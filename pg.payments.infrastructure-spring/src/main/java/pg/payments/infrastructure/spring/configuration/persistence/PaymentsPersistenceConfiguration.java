package pg.payments.infrastructure.spring.configuration.persistence;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan("pg.payments")
@EnableJpaRepositories("pg.payments")
public class PaymentsPersistenceConfiguration {
}
