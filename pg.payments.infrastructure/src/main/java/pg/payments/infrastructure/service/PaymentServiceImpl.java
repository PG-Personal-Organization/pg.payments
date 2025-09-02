package pg.payments.infrastructure.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pg.payments.domain.AccountTransferRecordsUtil;
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
    private final PaymentRepository paymentRepository;

    @Override
    public String createNewAccountTransferPayment(final @NonNull String recordId, final @NonNull AccountTransferDto data, final @NonNull BigDecimal amount,
                                                  final @NonNull String currency, final @NonNull String description, final @NonNull String userId) {
        var accountTransferData = new AccountTransferData(
                data.getCreditedAccountId(), data.getCreditedAccountNumber(), data.getTransferAccountId(), data.getTransferAccountNumber(), data.getBookingId());
        var paymentEntity = PaymentEntity.createNewAccountTransfer(
                AccountTransferRecordsUtil.recordIdMapper.apply(recordId), accountTransferData, amount, currency, description, userId);
        return paymentRepository.save(paymentEntity).getId();
    }

    @Override
    public void deletePayments(final @NonNull List<String> paymentIds) {
        var payments = paymentRepository.findAllById(paymentIds.stream().map(AccountTransferRecordsUtil.recordIdMapper).toList());
        payments.forEach(PaymentEntity::rejectCreating);
        paymentRepository.saveAll(payments);
    }

}
