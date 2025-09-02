package pg.payments.application.imports.parsing;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Log4j2
@RequiredArgsConstructor
public class AccountTransfersRecordParser implements RecordParser<AccountTransferRecord, ReadOnlyParsedRecord<AccountTransferRecord>> {
    private final ServiceExecutor serviceExecutor;
    private final ContextProvider contextProvider;

    @Override
    public @NonNull ReadOnlyParsedRecord<AccountTransferRecord> parse(final ReaderOutputItem<Object> item, final ImportContext importContext) {
        final var data = (AccountTransferRecord) item.getRawItem();
        try {
            final var recordBuilder = ReadOnlyParsedRecord.<AccountTransferRecord>builder()
                    .recordId(item.getId())
                    .importId(importContext.getImportId().id())
                    .recordData(data)
                    .ordinal(item.getItemNumber());

            var errorMessages = validateRecords(data);
            if (!errorMessages.isEmpty()) {
                return recordBuilder
                        .recordStatus(ImportRecordStatus.PARSING_FAILED)
                        .errorMessages(errorMessages)
                        .build();
            }

            final Optional<UserContext> userContext = contextProvider.tryToGetUserContext();
            if (userContext.isEmpty()) {
                throw new UserContextNotAvailableException();
            }

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
        } catch (final Exception e) {
            log.error("Failed to parse record {}", item.getId(), e);
            return ReadOnlyParsedRecord.<AccountTransferRecord>builder()
                    .recordId(item.getId())
                    .importId(importContext.getImportId().id())
                    .recordData(data)
                    .ordinal(item.getItemNumber())
                    .recordStatus(ImportRecordStatus.PARSING_FAILED)
                    .errorMessages(Collections.singletonList(e.getClass().getSimpleName() + " cause: " + e.getMessage()))
                    .build();
        }

    }

    private List<String> validateRecords(final @NonNull AccountTransferRecord dataRecord) {
        var errorMessages = new ArrayList<String>();

        if (StringUtils.isBlank(dataRecord.getCreditedAccountNumber())) {
            errorMessages.add("Credited account number cannot be empty.");
        }

        if (StringUtils.isBlank(dataRecord.getTransferAccountNumber())) {
            errorMessages.add("Transfer account number cannot be empty.");
        }

        if (dataRecord.getAmount() == null || dataRecord.getAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            errorMessages.add("Amount cannot be empty or less than zero.");
        }

        if (StringUtils.isBlank(dataRecord.getCurrency())) {
            errorMessages.add("Currency cannot be empty.");
        }

        if (StringUtils.isBlank(dataRecord.getDescription())) {
            errorMessages.add("Description cannot be empty.");
        }

        return errorMessages;
    }
}
