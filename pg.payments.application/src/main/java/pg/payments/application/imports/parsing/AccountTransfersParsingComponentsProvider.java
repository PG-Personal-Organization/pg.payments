package pg.payments.application.imports.parsing;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.beanio.builder.CsvParserBuilder;
import org.beanio.builder.StreamBuilder;
import pg.imports.plugin.api.parsing.*;
import pg.payments.application.imports.AccountTransferRecord;

import java.nio.charset.Charset;

@RequiredArgsConstructor
public class AccountTransfersParsingComponentsProvider implements ParsingComponentsProvider<AccountTransferRecord, ReadOnlyParsedRecord<AccountTransferRecord>> {
    private final AccountTransfersRecordParser accountTransfersRecordParser;
    private final AccountTransfersRecordsParsingErrorHandler accountTransfersRecordsParsingErrorHandler;

    @Override
    public @NonNull ReaderDefinition getReaderDefinition() {
        return BeanIoReaderDefinition.builder()
                .name("accountTransfersReader")
                .charset(Charset.defaultCharset())
                .streamBuilder((new StreamBuilder("accountTransfersReader")
                        .format("csv")
                        .parser(new CsvParserBuilder().delimiter(','))
                        .addRecord(AccountTransferRecord.class)
                ))
                .build();
    }

    @Override
    public @NonNull RecordParser<AccountTransferRecord, ReadOnlyParsedRecord<AccountTransferRecord>> getRecordParser() {
        return accountTransfersRecordParser;
    }

    @Override
    public @NonNull RecordsParsingErrorHandler getRecordsParsingErrorHandler() {
        return accountTransfersRecordsParsingErrorHandler;
    }
}
