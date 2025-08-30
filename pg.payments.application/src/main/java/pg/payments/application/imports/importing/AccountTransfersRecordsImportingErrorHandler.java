package pg.payments.application.imports.importing;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pg.imports.plugin.api.importing.RecordsImportingErrorHandler;
import pg.payments.api.accounts.AccountsService;
import pg.payments.infrastructure.persistence.PaymentRepository;

import java.util.List;

@RequiredArgsConstructor
public class AccountTransfersRecordsImportingErrorHandler implements RecordsImportingErrorHandler {
    private final AccountsService accountsService;
    private final PaymentRepository paymentRepository;

    @Override
    public void handleImportingError(final @NonNull List<String> allRecordIds) {
        var payments = paymentRepository.findAllById(allRecordIds);
        payments.forEach(paymentEntity -> {
            paymentEntity.rejectProcessing();
            var transferData = paymentEntity.getAccountTransferData();
            accountsService.relieveAccountBalanceBooking(transferData.getTransferAccountId(), transferData.getBookingId());
        });
        paymentRepository.saveAll(payments);
    }
}
