package pg.payments.application.imports.parsing;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pg.imports.plugin.api.parsing.RecordsParsingErrorHandler;
import pg.lib.cqrs.service.ServiceExecutor;
import pg.payments.api.payments.management.DeletePaymentsCommand;

import java.util.List;

@RequiredArgsConstructor
public class AccountTransfersRecordsParsingErrorHandler implements RecordsParsingErrorHandler {
    private final ServiceExecutor serviceExecutor;

    @Override
    public void handleError(final @NonNull List<String> recordIds) {
        serviceExecutor.executeCommand(DeletePaymentsCommand.of(recordIds));
    }
}
