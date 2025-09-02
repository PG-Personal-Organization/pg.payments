package pg.payments.application.imports.importing;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import pg.imports.plugin.api.importing.CompletedImportingCleaner;
import pg.payments.domain.AccountTransferRecordsUtil;
import pg.payments.infrastructure.persistence.PaymentEntity;
import pg.payments.infrastructure.persistence.PaymentRepository;

import java.util.List;

@Log4j2
@RequiredArgsConstructor
public class AccountTransfersCompletedImportingCleaner implements CompletedImportingCleaner {
    private final AccountTransfersRecordsImportingErrorHandler accountTransfersRecordsImportingErrorHandler;
    private final PaymentRepository paymentRepository;

    @Override
    public void handleCleaningSuccessfulRecords(final @NonNull List<String> recordIds) {
        var paymentIds = recordIds.stream().map(AccountTransferRecordsUtil.recordIdToPaymentIdMapper).toList();
        var payments = paymentRepository.findAllById(paymentIds);
        log.debug("Payments to complete processing: {}", paymentIds);
        payments.forEach(PaymentEntity::completeProcessing);
        paymentRepository.saveAll(payments);
    }

    @Override
    public void handleCleaningFailedRecords(final @NonNull List<String> errorRecordIds) {
        // recordIds are converted to paymentIds in AccountTransfersRecordsImportingErrorHandler
        accountTransfersRecordsImportingErrorHandler.handleImportingError(errorRecordIds);
    }
}
