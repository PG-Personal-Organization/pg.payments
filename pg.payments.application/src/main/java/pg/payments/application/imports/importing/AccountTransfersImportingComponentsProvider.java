package pg.payments.application.imports.importing;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pg.imports.plugin.api.importing.*;
import pg.imports.plugin.api.parsing.ParsedRecord;
import pg.payments.application.imports.AccountTransferRecord;
import pg.payments.application.imports.self.reader.AccountTransferImportingRecordsProvider;
import pg.payments.infrastructure.persistence.imports.record.ParsedAccountTransferRecordRepository;

import java.util.List;

@RequiredArgsConstructor
public class AccountTransfersImportingComponentsProvider implements ImportingComponentsProvider<AccountTransferRecord, ParsedRecord<AccountTransferRecord>> {

    private final AccountTransfersRecordsImporter importer;

    private final AccountTransfersRecordsImportingErrorHandler importingErrorHandler;

    private final AccountTransfersCompletedImportingCleaner completedImportingCleaner;

    private final ParsedAccountTransferRecordRepository parsedAccountTransferRecordRepository;

    @Override
    public @NonNull ImportingRecordsProvider<ParsedRecord<AccountTransferRecord>> getPluginImportingRecordsProvider(final List<String> successfulRecordIds) {
        return new AccountTransferImportingRecordsProvider(successfulRecordIds, parsedAccountTransferRecordRepository);
    }

    @Override
    public @NonNull RecordImporter<AccountTransferRecord, ParsedRecord<AccountTransferRecord>> getRecordImporter() {
        return importer;
    }

    @Override
    public @NonNull RecordsImportingErrorHandler getRecordsImportingErrorHandler() {
        return importingErrorHandler;
    }

    @Override
    public @NonNull CompletedImportingCleaner getCompletedImportingCleaner() {
        return completedImportingCleaner;
    }
}
