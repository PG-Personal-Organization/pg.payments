package pg.payments.infrastructure.spring.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pg.context.auth.api.context.provider.ContextProvider;
import pg.imports.plugin.infrastructure.spring.common.ImportPluginConfiguration;
import pg.lib.cqrs.service.ServiceExecutor;
import pg.payments.api.accounts.AccountsService;
import pg.payments.application.imports.AccountTransfersPlugin;
import pg.payments.application.imports.importing.AccountTransfersImportingComponentsProvider;
import pg.payments.application.imports.importing.AccountTransfersCompletedImportingCleaner;
import pg.payments.application.imports.importing.AccountTransfersRecordsImporter;
import pg.payments.application.imports.importing.AccountTransfersRecordsImportingErrorHandler;
import pg.payments.application.imports.parsing.AccountTransfersRecordParser;
import pg.payments.application.imports.parsing.AccountTransfersParsingComponentsProvider;
import pg.payments.application.imports.parsing.AccountTransfersRecordsParsingErrorHandler;
import pg.payments.application.imports.self.writer.AccountTransferPluginRecordsWriter;
import pg.payments.infrastructure.persistence.PaymentRepository;
import pg.payments.infrastructure.persistence.imports.record.ParsedAccountTransferRecordRepository;

@Configuration
@Import({
        ImportPluginConfiguration.class,
})
public class PaymentsImportPluginsConfiguration {

    @Bean
    public AccountTransfersPlugin accountTransfersPlugin(final AccountTransfersParsingComponentsProvider accountTransfersParsingComponentsProvider,
                                                         final AccountTransfersImportingComponentsProvider accountTransfersImportingComponentsProvider,
                                                         final AccountTransferPluginRecordsWriter accountTransferPluginRecordsWriter) {
        return new AccountTransfersPlugin(accountTransfersParsingComponentsProvider, accountTransfersImportingComponentsProvider, accountTransferPluginRecordsWriter);
    }

    @Bean
    public AccountTransferPluginRecordsWriter accountTransferPluginRecordsWriter(final ParsedAccountTransferRecordRepository parsedAccountTransferRecordRepository) {
        return new AccountTransferPluginRecordsWriter(parsedAccountTransferRecordRepository);
    }

    // Parsing components ------------------------------------------------------------

    @Bean
    public AccountTransfersParsingComponentsProvider accountTransfersParsingComponentsProvider(final AccountTransfersRecordParser accountTransfersRecordParser,
                                                                                                      final AccountTransfersRecordsParsingErrorHandler errorHandler) {
        return new AccountTransfersParsingComponentsProvider(accountTransfersRecordParser, errorHandler);
    }

    @Bean
    public AccountTransfersRecordParser accountTransfersRecordParser(final ServiceExecutor serviceExecutor, final ContextProvider contextProvider) {
        return new AccountTransfersRecordParser(serviceExecutor, contextProvider);
    }

    @Bean
    public AccountTransfersRecordsParsingErrorHandler accountTransfersRecordsParsingErrorHandler(final ServiceExecutor serviceExecutor) {
        return new AccountTransfersRecordsParsingErrorHandler(serviceExecutor);
    }

    // Importing components ------------------------------------------------------------

    @Bean
    public AccountTransfersImportingComponentsProvider accountTransferRecordsImportingProvider(final AccountTransfersRecordsImporter accountTransfersRecordsImporter,
                                                                                               final AccountTransfersRecordsImportingErrorHandler importingErrorHandler,
                                                                                               final AccountTransfersCompletedImportingCleaner completedImportingCleaner,
                                                                                               final ParsedAccountTransferRecordRepository parsedAccountTransferRecordRepository) {
        return new AccountTransfersImportingComponentsProvider(accountTransfersRecordsImporter, importingErrorHandler, completedImportingCleaner,
                parsedAccountTransferRecordRepository);
    }

    @Bean
    public AccountTransfersRecordsImporter accountTransfersRecordsImporter(final PaymentRepository paymentRepository, final AccountsService accountsService) {
        return new AccountTransfersRecordsImporter(paymentRepository, accountsService);
    }

    @Bean
    public AccountTransfersRecordsImportingErrorHandler accountTransfersRecordsImportingErrorHandler(final AccountsService accountsService,
                                                                                                     final PaymentRepository paymentRepository) {
        return new AccountTransfersRecordsImportingErrorHandler(accountsService, paymentRepository);
    }

    @Bean
    public AccountTransfersCompletedImportingCleaner accountTransfersCompletedImportingCleaner(final AccountTransfersRecordsImportingErrorHandler errorHandler,
                                                                                               final PaymentRepository paymentRepository) {
        return new AccountTransfersCompletedImportingCleaner(errorHandler, paymentRepository);
    }
}
