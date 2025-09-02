package pg.payments.api.payments.management;


import lombok.*;
import pg.lib.cqrs.command.Command;

import java.math.BigDecimal;


@ToString
@EqualsAndHashCode
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CreateNewAccountTransferPaymentCommand implements Command<String> {
    @NonNull
    private String recordId;

    @NonNull
    private String creditedAccountNumber;

    @NonNull
    private String transferAccountNumber;

    @NonNull
    private BigDecimal amount;

    @NonNull
    private String currency;

    @NonNull
    private String description;

    @NonNull
    private String userId;
}
