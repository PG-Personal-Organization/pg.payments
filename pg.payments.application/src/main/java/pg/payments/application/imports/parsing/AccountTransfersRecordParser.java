package pg.payments.application.imports.parsing;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pg.context.auth.api.context.provider.ContextProvider;
import pg.context.auth.domain.context.UserContext;
import pg.imports.plugin.api.data.ImportContext;
import pg.imports.plugin.api.data.ImportRecordStatus;
import pg.imports.plugin.api.parsing.ReadOnlyParsedRecord;
import pg.imports.plugin.api.parsing.ReaderOutputItem;
import pg.imports.plugin.api.parsing.RecordParser;
import pg.lib.cqrs.service.ServiceExecutor;
import pg.payments.api.payments.management.CreateNewAccountTransferPaymentCommand;
import pg.payments.application.imports.AccountTransferRecord;
import pg.payments.application.imports.parsing.exception.UserContextNotAvailableException;

import java.util.Collections;
import java.util.Optional;

@RequiredArgsConstructor
public class AccountTransfersRecordParser implements RecordParser<AccountTransferRecord, ReadOnlyParsedRecord<AccountTransferRecord>> {
    private final ServiceExecutor serviceExecutor;
    private final ContextProvider contextProvider;

    @Override
    @SuppressWarnings("unchecked")
    public @NonNull ReadOnlyParsedRecord<AccountTransferRecord> parse(final ReaderOutputItem<Object> item, final ImportContext importContext) {
        final var data = (AccountTransferRecord) item.getRawItem();
        try {
            final Optional<UserContext> userContext = contextProvider.tryToGetUserContext();
            if (userContext.isEmpty()) {
                throw new UserContextNotAvailableException();
            }

            final var recordBuilder = ReadOnlyParsedRecord.<AccountTransferRecord>builder()
                    .recordId(item.getId())
                    .importId(importContext.getImportId().id())
                    .recordData(data)
                    .ordinal(item.getItemNumber());

            if (data.getTransferAccountNumber().equals(data.getCreditedAccountNumber())) {
                return recordBuilder
                        .recordStatus(ImportRecordStatus.PARSING_FAILED)
                        .errorMessages(Collections.singletonList("Credited account number cannot be the same as transfer account number."))
                        .build();
            }

            var command = CreateNewAccountTransferPaymentCommand.of(
                    item.getId(), data.getCreditedAccountNumber(), data.getTransferAccountNumber(), data.getAmount(), data.getCurrency(), userContext.get().getUserId());
            serviceExecutor.executeCommand(command);

            return recordBuilder
                    .recordStatus(ImportRecordStatus.PARSED)
                    .errorMessages(Collections.emptyList())
                    .build();
        } catch (final Throwable e) {
            return ReadOnlyParsedRecord.<AccountTransferRecord>builder()
                    .recordId(item.getId())
                    .importId(importContext.getImportId().id())
                    .recordData(data)
                    .ordinal(item.getItemNumber())
                    .recordStatus(ImportRecordStatus.PARSING_FAILED)
                    .errorMessages(Collections.singletonList(e.getMessage()))
                    .build();
        }

    }
}
