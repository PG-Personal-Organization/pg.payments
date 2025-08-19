package pg.payments.infrastructure.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pg.context.auth.api.security.ContextBasedSecurityConfiguration;
import pg.lib.common.spring.config.CommonModuleConfiguration;
import pg.lib.cqrs.config.CommandQueryAutoConfiguration;
import pg.lib.remote.cqrs.config.RemoteModulesCqrsConfiguration;
import pg.payments.infrastructure.spring.configuration.imports.PaymentsImportPluginsConfiguration;
import pg.payments.infrastructure.spring.configuration.persistence.PaymentsPersistenceConfiguration;

@Configuration
@Import({
        PaymentsPersistenceConfiguration.class,
        PaymentsImportPluginsConfiguration.class,

        CommonModuleConfiguration.class,
        CommandQueryAutoConfiguration.class,
        ContextBasedSecurityConfiguration.class,
        RemoteModulesCqrsConfiguration.class,
})
@ComponentScan("pg.payments")
public class PaymentsModuleConfiguration {
}
