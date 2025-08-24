package pg.payments.infrastructure.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pg.payments.domain.management.AccountTransferDto;
import pg.payments.domain.management.PaymentsService;
import pg.payments.infrastructure.persistence.AccountTransferData;
import pg.payments.infrastructure.persistence.PaymentEntity;
import pg.payments.infrastructure.persistence.PaymentRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentsService {
    private static final String PAYMENT_ID_PREFIX = "PMT_";

    private final PaymentRepository paymentRepository;

    @Override
    public String createNewAccountTransferPayment(final @NonNull String recordId, final @NonNull AccountTransferDto data, final @NonNull BigDecimal amount,
                                                  final @NonNull String currency, final @NonNull String userId) {
        var accountTransferData = new AccountTransferData(
                data.getCreditedAccountId(), data.getCreditedAccountNumber(), data.getTransferAccountId(), data.getTransferAccountNumber());
        var paymentEntity = PaymentEntity.createNewAccountTransfer(PAYMENT_ID_PREFIX + recordId, accountTransferData, amount, currency, userId);
        return paymentRepository.save(paymentEntity).getId();
    }

    @Override
    public void deletePayments(final @NonNull List<String> paymentIds) {
        var payments = paymentRepository.findAllById(paymentIds.stream().map(id -> PAYMENT_ID_PREFIX + id).toList());
        payments.forEach(PaymentEntity::rejectCreating);
        paymentRepository.saveAll(payments);
    }

}
