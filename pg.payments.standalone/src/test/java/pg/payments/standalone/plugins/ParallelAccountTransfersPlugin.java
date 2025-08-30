package pg.payments.standalone.plugins;

import lombok.NonNull;
import pg.imports.plugin.api.data.PluginCode;
import pg.payments.application.imports.AccountTransfersPlugin;
import pg.payments.application.imports.importing.AccountTransfersImportingComponentsProvider;
import pg.payments.application.imports.parsing.AccountTransfersParsingComponentsProvider;
import pg.payments.application.imports.self.writer.AccountTransferPluginRecordsWriter;

public class ParallelAccountTransfersPlugin extends AccountTransfersPlugin {

    public ParallelAccountTransfersPlugin(final AccountTransfersParsingComponentsProvider parsingComponentsProvider,
                                          final AccountTransfersImportingComponentsProvider importingComponentsProvider,
                                          final AccountTransferPluginRecordsWriter accountTransferPluginRecordsWriter) {
        super(parsingComponentsProvider, importingComponentsProvider, accountTransferPluginRecordsWriter);
    }

    @Override
    public @NonNull PluginCode getCode() {
        return new PluginCode("PARALLEL_" + super.getCode().code());
    }
}
