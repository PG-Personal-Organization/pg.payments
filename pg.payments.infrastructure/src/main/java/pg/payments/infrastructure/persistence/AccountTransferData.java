package pg.payments.infrastructure.persistence;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
@Embeddable
public class AccountTransferData implements Serializable {
    @NonNull
    private String creditedAccountId;
    @NonNull
    private String creditedAccountNumber;

    @NonNull
    private String transferAccountId;
    @NonNull
    private String transferAccountNumber;
}
