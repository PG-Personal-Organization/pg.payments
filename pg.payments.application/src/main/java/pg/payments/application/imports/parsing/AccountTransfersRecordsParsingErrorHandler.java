package pg.payments.application.imports.parsing;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pg.imports.plugin.api.parsing.RecordsParsingErrorHandler;
import pg.lib.cqrs.service.ServiceExecutor;
import pg.payments.api.payments.management.DeletePaymentsCommand;
import pg.payments.domain.AccountTransferRecordsUtil;

import java.util.List;

@RequiredArgsConstructor
public class AccountTransfersRecordsParsingErrorHandler implements RecordsParsingErrorHandler {
    private final ServiceExecutor serviceExecutor;

    @Override
    public void handleError(final @NonNull List<String> recordIds) {
        var paymentIds = recordIds.stream().map(AccountTransferRecordsUtil.recordIdToPaymentIdMapper).toList();
        serviceExecutor.executeCommand(DeletePaymentsCommand.of(paymentIds));
    }
}
