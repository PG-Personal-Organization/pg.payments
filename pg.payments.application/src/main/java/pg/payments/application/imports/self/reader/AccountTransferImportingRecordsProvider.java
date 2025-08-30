package pg.payments.application.imports.self.reader;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pg.imports.plugin.api.data.ImportRecordStatus;
import pg.imports.plugin.api.importing.ImportingRecordsProvider;
import pg.imports.plugin.api.parsing.ParsedRecord;
import pg.imports.plugin.api.parsing.ReadOnlyParsedRecord;
import pg.payments.application.imports.AccountTransferRecord;
import pg.payments.infrastructure.persistence.imports.record.ParsedAccountTransferRecord;
import pg.payments.infrastructure.persistence.imports.record.ParsedAccountTransferRecordRepository;

import java.util.List;

@RequiredArgsConstructor
public class AccountTransferImportingRecordsProvider implements ImportingRecordsProvider<ParsedRecord<AccountTransferRecord>> {
    private final List<String> recordIds;
    private final ParsedAccountTransferRecordRepository parsedAccountTransferRecordRepository;

    @Override
    public List<ParsedRecord<AccountTransferRecord>> getRecords() {
        return parsedAccountTransferRecordRepository.findAllById(recordIds).stream()
                .map(this::toParsedRecord)
                .toList();
    }

    private ParsedRecord<AccountTransferRecord> toParsedRecord(final @NonNull ParsedAccountTransferRecord transferRecord) {
        return ReadOnlyParsedRecord.<AccountTransferRecord>builder()
                .recordId(transferRecord.getId())
                .importId(transferRecord.getImportId())
                .ordinal(transferRecord.getOrdinal())
                .recordData(AccountTransferRecord.builder()
                        .creditedAccountNumber(transferRecord.getCreditedAccountNumber())
                        .transferAccountNumber(transferRecord.getTransferAccountNumber())
                        .amount(transferRecord.getAmount())
                        .currency(transferRecord.getCurrency())
                        .description(transferRecord.getDescription())
                        .build())
                .recordStatus(ImportRecordStatus.valueOf(transferRecord.getRecordStatus()))
                .errorMessages(transferRecord.getErrorMessages())
                .build();
    }
}
