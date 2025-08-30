package pg.payments.application.imports.importing;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import pg.imports.plugin.api.importing.ImportingRecordsProvider;
import pg.imports.plugin.api.importing.ImportingResult;
import pg.imports.plugin.api.importing.RecordImporter;
import pg.imports.plugin.api.parsing.ParsedRecord;
import pg.payments.api.accounts.AccountsService;
import pg.payments.application.imports.AccountTransferRecord;
import pg.payments.infrastructure.persistence.PaymentRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
public class AccountTransfersRecordsImporter implements RecordImporter<AccountTransferRecord, ParsedRecord<AccountTransferRecord>> {
    private final PaymentRepository paymentRepository;
    private final AccountsService accountsService;

    @Override
    public ImportingResult importRecords(final ImportingRecordsProvider<ParsedRecord<AccountTransferRecord>> provider) {
        List<ParsedRecord<AccountTransferRecord>> recordsToImport;
        try {
            recordsToImport = provider.getRecords();
        } catch (final Exception e) {
            log.error("Failed to get records to import.", e);
            return ImportingResult.error("Error while getting records to import: " + e.getMessage());
        }

        var paymentIds = recordsToImport.stream().map(ParsedRecord::getRecordId).toList();
        var payments = paymentRepository.findAllById(paymentIds);
        final Map<String, String> errorMessages = new HashMap<>();

        payments.forEach(paymentEntity -> {
            try {
                paymentEntity.startProcessing();
                var paymentTransferData = paymentEntity.getAccountTransferData();
                accountsService.processAccountBalanceBooking(paymentTransferData.getTransferAccountId(), paymentTransferData.getBookingId());
            } catch (final Exception e) {
                log.error("Error occurred during processing of payment {}.", paymentEntity.getId(), e);
                errorMessages.put(paymentEntity.getId(), e.getMessage());
            }
        });
        paymentRepository.saveAll(payments);

        if (!errorMessages.isEmpty()) {
            return ImportingResult.error("Error occurred during processing of some payments.", errorMessages);
        }
        return ImportingResult.success();
    }
}
