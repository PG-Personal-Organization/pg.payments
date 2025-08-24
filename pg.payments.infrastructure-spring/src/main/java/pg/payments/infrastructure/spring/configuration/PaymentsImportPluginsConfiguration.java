package pg.payments.infrastructure.spring.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pg.context.auth.api.context.provider.ContextProvider;
import pg.imports.plugin.infrastructure.spring.common.ImportPluginConfiguration;
import pg.lib.cqrs.service.ServiceExecutor;
import pg.payments.application.imports.AccountTransfersPlugin;
import pg.payments.application.imports.parsing.AccountTransfersRecordParser;
import pg.payments.application.imports.parsing.AccountTransfersRecordsParsingComponentsProvider;
import pg.payments.application.imports.parsing.AccountTransfersRecordsParsingErrorHandler;

@Configuration
@Import({
        ImportPluginConfiguration.class,
})
public class PaymentsImportPluginsConfiguration {

    @Bean
    public AccountTransfersPlugin accountTransfersPlugin(final AccountTransfersRecordsParsingComponentsProvider accountTransfersRecordsParsingComponentsProvider) {
        return new AccountTransfersPlugin(accountTransfersRecordsParsingComponentsProvider);
    }

    @Bean
    public AccountTransfersRecordsParsingComponentsProvider accountTransfersRecordsParsingComponentsProvider(final AccountTransfersRecordParser accountTransfersRecordParser,
                                                                                                             final AccountTransfersRecordsParsingErrorHandler errorHandler) {
        return new AccountTransfersRecordsParsingComponentsProvider(accountTransfersRecordParser, errorHandler);
    }

    @Bean
    public AccountTransfersRecordParser accountTransfersRecordParser(final ServiceExecutor serviceExecutor, final ContextProvider contextProvider) {
        return new AccountTransfersRecordParser(serviceExecutor, contextProvider);
    }

    @Bean
    public AccountTransfersRecordsParsingErrorHandler accountTransfersRecordsParsingErrorHandler(final ServiceExecutor serviceExecutor) {
        return new AccountTransfersRecordsParsingErrorHandler(serviceExecutor);
    }
}
