package pg.payments.application.imports.self.writer;

import lombok.RequiredArgsConstructor;
import pg.imports.plugin.api.data.ImportContext;
import pg.imports.plugin.api.parsing.ParsedRecord;
import pg.imports.plugin.api.writing.PluginRecordsWriter;
import pg.payments.application.imports.AccountTransferRecord;
import pg.payments.infrastructure.persistence.imports.record.ParsedAccountTransferRecord;
import pg.payments.infrastructure.persistence.imports.record.ParsedAccountTransferRecordRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class AccountTransferPluginRecordsWriter implements PluginRecordsWriter<AccountTransferRecord, ParsedRecord<AccountTransferRecord>> {
    private final ParsedAccountTransferRecordRepository parsedAccountTransferRecordRepository;

    @Override
    public void write(final List<ParsedRecord<AccountTransferRecord>> parsedRecords, final ImportContext importContext) {
        var accountTransferRecords = parsedRecords.stream().map(this::toDbRecord).toList();
        parsedAccountTransferRecordRepository.saveAll(accountTransferRecords);
    }

    @Override
    public void writeRecordError(final String recordId, final String errorMessage) {
        var parsedRecord = parsedAccountTransferRecordRepository.findById(recordId).orElseThrow(() -> new ParsedAccountTransferRecordNotFoundException(recordId));
        parsedRecord.getErrorMessages().add(errorMessage);
        parsedAccountTransferRecordRepository.save(parsedRecord);
    }

    private ParsedAccountTransferRecord toDbRecord(final ParsedRecord<AccountTransferRecord> parsedRecord) {
        var record = parsedRecord.getRecord();
        return ParsedAccountTransferRecord.builder()
                .id(parsedRecord.getRecordId())
                .importId(parsedRecord.getImportId())
                .recordStatus(parsedRecord.getRecordStatus().name())
                .ordinal(Math.toIntExact(parsedRecord.getOrdinal()))
                .errorMessages(parsedRecord.getErrorMessages() != null ? parsedRecord.getErrorMessages() : Collections.emptyList())
                .createdOn(LocalDateTime.now())
                .creditedAccountNumber(record.getCreditedAccountNumber())
                .transferAccountNumber(record.getTransferAccountNumber())
                .amount(record.getAmount())
                .currency(record.getCurrency())
                .description(record.getDescription())
                .build();
    }
}
