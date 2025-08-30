package pg.payments.infrastructure.spring.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pg.accounts.api.AccountQuery;
import pg.accounts.api.AccountViewResponse;
import pg.lib.cqrs.query.QueryHandler;
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

    @Bean
    public QueryHandler<AccountQuery, AccountViewResponse> accountQueryAccountViewResponseQueryHandler() {
        return new QueryHandler<AccountQuery, AccountViewResponse>() {
            @Override
            public AccountViewResponse handle(final AccountQuery query) {
                return null;
            }
        };
    }
}
