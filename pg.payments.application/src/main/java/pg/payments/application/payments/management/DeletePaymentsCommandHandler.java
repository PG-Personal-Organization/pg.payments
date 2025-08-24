package pg.payments.application.payments.management;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import pg.lib.cqrs.command.CommandHandler;
import pg.payments.api.payments.management.DeletePaymentsCommand;
import pg.payments.domain.management.PaymentsService;

@RequiredArgsConstructor
public class DeletePaymentsCommandHandler implements CommandHandler<DeletePaymentsCommand, Void> {
    private final PaymentsService paymentsService;

    @Override
    @Transactional
    public Void handle(final DeletePaymentsCommand command) {
        paymentsService.deletePayments(command.getPaymentIds());
        return null;
    }
}
