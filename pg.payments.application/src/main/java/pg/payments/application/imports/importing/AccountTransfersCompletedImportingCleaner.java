package pg.payments.application.imports.importing;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pg.imports.plugin.api.importing.CompletedImportingCleaner;
import pg.payments.domain.AccountTransferRecordsUtil;
import pg.payments.infrastructure.persistence.PaymentEntity;
import pg.payments.infrastructure.persistence.PaymentRepository;

import java.util.List;

@RequiredArgsConstructor
public class AccountTransfersCompletedImportingCleaner implements CompletedImportingCleaner {
    private final AccountTransfersRecordsImportingErrorHandler accountTransfersRecordsImportingErrorHandler;
    private final PaymentRepository paymentRepository;

    @Override
    public void handleCleaningSuccessfulRecords(final @NonNull List<String> recordIds) {
        var paymentIds = recordIds.stream().map(AccountTransferRecordsUtil.recordIdMapper).toList();
        var payments = paymentRepository.findAllById(paymentIds);
        payments.forEach(PaymentEntity::completeProcessing);
        paymentRepository.saveAll(payments);
    }

    @Override
    public void handleCleaningFailedRecords(final @NonNull List<String> errorRecordIds) {
        // recordIds are converted to paymentIds in AccountTransfersRecordsImportingErrorHandler
        accountTransfersRecordsImportingErrorHandler.handleImportingError(errorRecordIds);
    }
}
