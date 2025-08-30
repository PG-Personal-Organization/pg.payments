package pg.payments.application.imports;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pg.imports.plugin.api.data.PluginCode;
import pg.imports.plugin.api.importing.ImportingComponentsProvider;
import pg.imports.plugin.api.parsing.ParsedRecord;
import pg.imports.plugin.api.parsing.ParsingComponentsProvider;
import pg.imports.plugin.api.strategies.db.RecordData;
import pg.imports.plugin.api.strategies.self.SelfStoringRecordsPlugin;
import pg.imports.plugin.api.writing.PluginRecordsWriter;
import pg.payments.application.imports.importing.AccountTransfersImportingComponentsProvider;
import pg.payments.application.imports.parsing.AccountTransfersParsingComponentsProvider;
import pg.payments.application.imports.self.writer.AccountTransferPluginRecordsWriter;

@RequiredArgsConstructor
public class AccountTransfersPlugin implements SelfStoringRecordsPlugin<AccountTransferRecord> {
    public static final String PLUGIN_CODE = "ACCOUNT_TRANSFERS";

    private final AccountTransfersParsingComponentsProvider parsingComponentsProvider;

    private final AccountTransfersImportingComponentsProvider importingComponentsProvider;

    private final AccountTransferPluginRecordsWriter accountTransferPluginRecordsWriter;

    @Override
    public @NonNull PluginCode getCode() {
        return new PluginCode(PLUGIN_CODE);
    }

    @Override
    public @NonNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public @NonNull String getCodeIdPrefix() {
        return "ACCT";
    }

    @Override
    public @NonNull PluginRecordsWriter<AccountTransferRecord, ? extends ParsedRecord<AccountTransferRecord>> getRecordsWriter() {
        return accountTransferPluginRecordsWriter;
    }

    @Override
    public Class<? extends RecordData> getRecordClass() {
        return AccountTransferRecord.class;
    }

    @Override
    public @NonNull ParsingComponentsProvider<AccountTransferRecord, ? extends ParsedRecord<AccountTransferRecord>> getParsingComponentProvider() {
        return parsingComponentsProvider;
    }

    @Override
    public @NonNull ImportingComponentsProvider<AccountTransferRecord, ? extends ParsedRecord<AccountTransferRecord>> getImportingComponentsProvider() {
        return importingComponentsProvider;
    }
}
