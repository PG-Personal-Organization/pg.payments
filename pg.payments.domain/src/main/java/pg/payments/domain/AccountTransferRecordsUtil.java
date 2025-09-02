package pg.payments.domain;

import lombok.experimental.UtilityClass;

import java.util.function.UnaryOperator;

@UtilityClass
public class AccountTransferRecordsUtil {
    public final String paymentIdPrefix = "PMT_";
    public final UnaryOperator<String> recordIdToPaymentIdMapper = id -> paymentIdPrefix + id;
    public final UnaryOperator<String> paymentIdToRecordIdMapper = id -> id.replaceFirst(paymentIdPrefix, "");
}
