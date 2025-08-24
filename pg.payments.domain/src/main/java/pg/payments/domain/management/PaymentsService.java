package pg.payments.domain.management;

import lombok.NonNull;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentsService {
    String createNewAccountTransferPayment(@NonNull String recordId, @NonNull AccountTransferDto accountTransferDto, @NonNull BigDecimal amount, @NonNull String currency,
                                           @NonNull String userId);

    void deletePayments(@NonNull List<String> paymentIds);
}
