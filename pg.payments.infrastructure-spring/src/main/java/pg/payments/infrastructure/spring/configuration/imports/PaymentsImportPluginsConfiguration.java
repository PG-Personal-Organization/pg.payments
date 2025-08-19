package pg.payments.infrastructure.spring.configuration.imports;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pg.imports.plugin.infrastructure.spring.common.ImportPluginConfiguration;

@Configuration
@Import({
        ImportPluginConfiguration.class,
})
public class PaymentsImportPluginsConfiguration {

}
