package pg.payments.domain;

import lombok.experimental.UtilityClass;

import java.util.function.UnaryOperator;

@UtilityClass
public class AccountTransferRecordsUtil {
    public final String paymentIdPrefix = "PMT_";
    public final UnaryOperator<String> recordIdMapper = id -> paymentIdPrefix + id;
}
