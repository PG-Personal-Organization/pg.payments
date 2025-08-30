package pg.payments.domain.management;

import lombok.*;

@ToString
@EqualsAndHashCode
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AccountTransferDto {
    @NonNull
    private String creditedAccountId;
    @NonNull
    private String creditedAccountNumber;

    @NonNull
    private String transferAccountId;
    @NonNull
    private String transferAccountNumber;

    @NonNull
    private String bookingId;
}
