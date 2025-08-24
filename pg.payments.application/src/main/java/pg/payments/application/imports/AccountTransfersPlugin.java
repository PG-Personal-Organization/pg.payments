package pg.payments.application.imports;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pg.imports.plugin.api.ImportPlugin;
import pg.imports.plugin.api.data.PluginCode;
import pg.imports.plugin.api.importing.ImportingComponentsProvider;
import pg.imports.plugin.api.parsing.ParsedRecord;
import pg.imports.plugin.api.parsing.ParsingComponentsProvider;
import pg.imports.plugin.api.strategies.db.RecordData;
import pg.payments.application.imports.parsing.AccountTransfersRecordsParsingComponentsProvider;

@RequiredArgsConstructor
public class AccountTransfersPlugin implements ImportPlugin<AccountTransferRecord> {
    public static final String PLUGIN_CODE = "ACCOUNT_TRANSFERS";

    public final AccountTransfersRecordsParsingComponentsProvider parsingComponentsProvider;

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
    public Class<? extends RecordData> getRecordClass() {
        return AccountTransferRecord.class;
    }

    @Override
    public @NonNull ParsingComponentsProvider<AccountTransferRecord, ? extends ParsedRecord<AccountTransferRecord>> getParsingComponentProvider() {
        return parsingComponentsProvider;
    }

    @Override
    public @NonNull <RECORD extends RecordData, IN extends ParsedRecord<RECORD>> ImportingComponentsProvider<RECORD, IN> getImportingComponentsProvider() {
        return ImportPlugin.super.getImportingComponentsProvider();
    }
}
