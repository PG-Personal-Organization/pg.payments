package pg.payments.infrastructure.spring.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pg.payments.api.accounts.AccountsProvider;
import pg.payments.api.accounts.AccountsService;
import pg.payments.application.payments.management.CreateNewAccountTransferPaymentCommandHandler;
import pg.payments.application.payments.management.DeletePaymentsCommandHandler;
import pg.payments.domain.management.PaymentsService;

@Configuration
public class PaymentsCqrsConfiguration {

    @Bean
    public CreateNewAccountTransferPaymentCommandHandler createNewAccountTransferPaymentCommandHandler(final PaymentsService paymentsService,
                                                                                                       final AccountsProvider accountsProvider,
                                                                                                       final AccountsService accountsService) {
        return new CreateNewAccountTransferPaymentCommandHandler(paymentsService, accountsProvider, accountsService);
    }

    @Bean
    public DeletePaymentsCommandHandler deletePaymentsCommandHandler(final PaymentsService paymentsService) {
        return new DeletePaymentsCommandHandler(paymentsService);
    }
}
