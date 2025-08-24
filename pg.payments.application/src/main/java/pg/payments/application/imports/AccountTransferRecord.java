package pg.payments.application.imports;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.beanio.annotation.Field;
import pg.imports.plugin.api.strategies.db.RecordData;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("checkstyle:MagicNumber")
@org.beanio.annotation.Record
public class AccountTransferRecord implements RecordData {
    @Field(at = 0)
    private String creditedAccountNumber;
    @Field(at = 1)
    private String transferAccountNumber;
    @Field(at = 2)
    private BigDecimal amount;
    @Field(at = 3)
    private String currency;
    @Field(at = 4)
    private String description;
}
