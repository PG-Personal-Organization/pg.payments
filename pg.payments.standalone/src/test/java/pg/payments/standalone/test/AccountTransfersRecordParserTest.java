package pg.payments.standalone.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import pg.context.auth.api.context.provider.ContextProvider;
import pg.context.auth.domain.context.UserContext;
import pg.imports.plugin.api.data.ImportContext;
import pg.imports.plugin.api.data.ImportId;
import pg.imports.plugin.api.data.PluginCode;
import pg.imports.plugin.api.parsing.ReadOnlyParsedRecord;
import pg.imports.plugin.api.parsing.ReaderOutputItem;
import pg.lib.cqrs.service.ServiceExecutor;
import pg.payments.api.payments.management.CreateNewAccountTransferPaymentCommand;
import pg.payments.application.imports.AccountTransferRecord;
import pg.payments.application.imports.parsing.AccountTransfersRecordParser;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AccountTransfersRecordParserTest {
    private ServiceExecutor serviceExecutor;
    private ContextProvider contextProvider;
    private AccountTransfersRecordParser parser;

    private ImportContext importContext;

    @BeforeEach
    void setUp() {
        serviceExecutor = mock(ServiceExecutor.class);
        contextProvider = mock(ContextProvider.class);
        parser = new AccountTransfersRecordParser(serviceExecutor, contextProvider);

        importContext = ImportContext.of(
                new ImportId("IMP-" + UUID.randomUUID()),
                new PluginCode("ACCOUNT_TRANSFERS"),
                UUID.randomUUID(),
                null
        );
    }

    private ReaderOutputItem<Object> item(String id, long ordinal, AccountTransferRecord raw) {
        return ReaderOutputItem.<Object>builder()
                .id(id)
                .itemNumber((int) ordinal)
                .rawItem(raw)
                .partitionId("p-1")
                .chunkNumber(0)
                .build();
    }

    @Test
    void should_mark_as_failed_when_validation_errors() {
        var raw = AccountTransferRecord.builder()
                .creditedAccountNumber("")        // błąd
                .transferAccountNumber("222")
                .amount(BigDecimal.TEN)
                .currency("PLN")
                .description("x")
                .build();

        ReadOnlyParsedRecord<AccountTransferRecord> out =
                parser.parse(item("r-1", 1, raw), importContext);

        assertThat(out.getRecordStatus().name()).isEqualTo("PARSING_FAILED");
        assertThat(out.getErrorMessages()).isNotEmpty();
    }

    @Test
    void should_mark_as_failed_when_same_accounts() {
        var raw = AccountTransferRecord.builder()
                .creditedAccountNumber("111")
                .transferAccountNumber("111")     // to samo konto => błąd
                .amount(BigDecimal.TEN)
                .currency("PLN")
                .description("x")
                .build();

        when(contextProvider.tryToGetUserContext())
                .thenReturn(Optional.of(UserContext.builder().userId("U1").build()));

        ReadOnlyParsedRecord<AccountTransferRecord> out =
                parser.parse(item("r-2", 2, raw), importContext);

        assertThat(out.getRecordStatus().name()).isEqualTo("PARSING_FAILED");
        assertThat(out.getErrorMessages()).containsExactly(
                "Credited account number cannot be the same as transfer account number."
        );
        verifyNoInteractions(serviceExecutor);
    }

    @Test
    void should_mark_as_failed_when_context_missing() {
        var raw = AccountTransferRecord.builder()
                .creditedAccountNumber("111")
                .transferAccountNumber("222")
                .amount(BigDecimal.TEN)
                .currency("PLN")
                .description("ok")
                .build();

        when(contextProvider.tryToGetUserContext()).thenReturn(Optional.empty());

        ReadOnlyParsedRecord<AccountTransferRecord> out =
                parser.parse(item("r-3", 3, raw), importContext);

        assertThat(out.getRecordStatus().name()).isEqualTo("PARSING_FAILED");
        assertThat(out.getErrorMessages()).isNotEmpty();
        verifyNoInteractions(serviceExecutor);
    }

    @Test
    void should_mark_as_parsed_and_send_command() {
        var raw = AccountTransferRecord.builder()
                .creditedAccountNumber("111")
                .transferAccountNumber("222")
                .amount(new BigDecimal("123.45"))
                .currency("PLN")
                .description("desc")
                .build();

        when(contextProvider.tryToGetUserContext())
                .thenReturn(Optional.of(UserContext.builder().userId("U-123").build()));

        ReadOnlyParsedRecord<AccountTransferRecord> out =
                parser.parse(item("REC-9", 9, raw), importContext);

        assertThat(out.getRecordStatus().name()).isEqualTo("PARSED");
        assertThat(out.getErrorMessages()).isEmpty();

        ArgumentCaptor<CreateNewAccountTransferPaymentCommand> captor =
                ArgumentCaptor.forClass(CreateNewAccountTransferPaymentCommand.class);
        verify(serviceExecutor).executeCommand(captor.capture());

        var cmd = captor.getValue();
        assertThat(cmd.getRecordId()).isEqualTo("REC-9");
        assertThat(cmd.getAmount()).isEqualByComparingTo("123.45");
        assertThat(cmd.getCurrency()).isEqualTo("PLN");
        assertThat(cmd.getUserId()).isEqualTo("U-123");
    }
}
