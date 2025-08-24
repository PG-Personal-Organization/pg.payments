package pg.payments.infrastructure.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pg.context.auth.api.security.ContextBasedSecurityConfiguration;
import pg.lib.common.spring.config.CommonModuleConfiguration;
import pg.lib.cqrs.config.CommandQueryAutoConfiguration;
import pg.lib.remote.cqrs.config.RemoteModulesCqrsConfiguration;
import pg.payments.infrastructure.spring.configuration.PaymentsCqrsConfiguration;
import pg.payments.infrastructure.spring.configuration.PaymentsImportPluginsConfiguration;
import pg.payments.infrastructure.spring.configuration.PaymentsPersistenceConfiguration;
import pg.payments.infrastructure.spring.configuration.PaymentsSecurityConfiguration;

@Configuration
@Import({
        PaymentsPersistenceConfiguration.class,
        PaymentsImportPluginsConfiguration.class,
        PaymentsSecurityConfiguration.class,
        PaymentsCqrsConfiguration.class,

        CommonModuleConfiguration.class,
        CommandQueryAutoConfiguration.class,
        ContextBasedSecurityConfiguration.class,
        RemoteModulesCqrsConfiguration.class,
})
@ComponentScan({"pg.payments.infrastructure.service", "pg.payments.application.provider", "pg.payments.application.service"})
public class PaymentsModuleConfiguration {
}
