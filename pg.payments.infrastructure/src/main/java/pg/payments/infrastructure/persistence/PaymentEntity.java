package pg.payments.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.*;
import pg.payments.domain.PaymentState;
import pg.payments.domain.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity(name = "payments")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class PaymentEntity {
    @Id
    private String id;

    @Column(nullable = false)
    private LocalDateTime createdOn;

    private LocalDateTime startedProcessingOn;

    private LocalDateTime rejectedOn;

    private LocalDateTime completedOn;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentState state;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentType type;

    private BigDecimal amount;

    private String currency;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "creditedAccountId", column = @Column(name = "credited_account_id", nullable = false)),
            @AttributeOverride(name = "creditedAccountNumber", column = @Column(name = "credited_account_number", nullable = false)),
            @AttributeOverride(name = "transferAccountId", column = @Column(name = "transfer_account_id", nullable = false)),
            @AttributeOverride(name = "transferAccountNumber", column = @Column(name = "transfer_account_number", nullable = false)),
            @AttributeOverride(name = "bookingId", column = @Column(name = "transfer_account_booking_id", nullable = false))
    })
    private AccountTransferData accountTransferData;

    @Column(nullable = false)
    private String userId;

    private String description;

    private String rejectedReason;

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PaymentEntity that = (PaymentEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(createdOn, that.createdOn) && Objects.equals(startedProcessingOn, that.startedProcessingOn)
                && Objects.equals(rejectedOn, that.rejectedOn)  && Objects.equals(completedOn, that.completedOn) && state == that.state
                && type == that.type && Objects.equals(amount, that.amount) && Objects.equals(currency, that.currency)
                && Objects.equals(accountTransferData, that.accountTransferData) && Objects.equals(userId, that.userId)
                && Objects.equals(description, that.description) && Objects.equals(rejectedReason, that.rejectedReason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createdOn, startedProcessingOn, rejectedOn, completedOn, state, type, amount, currency, accountTransferData, userId, description, rejectedReason);
    }

    public void startProcessing() {
        this.state = PaymentState.IN_PROGRESS;
        this.startedProcessingOn = LocalDateTime.now();
    }

    public void completeProcessing() {
        this.state = PaymentState.COMPLETED;
        this.completedOn = LocalDateTime.now();
    }

    public void rejectCreating() {
        this.state = PaymentState.REJECTED;
        this.rejectedOn = LocalDateTime.now();
    }

    public void rejectProcessing() {
        this.state = PaymentState.REJECTED_PROCESSING;
        this.rejectedOn = LocalDateTime.now();
        // TODO pass reason from import record in pg.imports.plugin.infrastructure.importing.CompletedImportImportingMessageHandler.clean
//        this.rejectedReason = reason;
    }

    public static PaymentEntity createNewAccountTransfer(final @NonNull String id, final @NonNull AccountTransferData accountTransferData, final @NonNull BigDecimal amount,
                                                         final @NonNull String currency, final @NonNull String userId) {
        return PaymentEntity.builder()
                .id(id)
                .createdOn(LocalDateTime.now())
                .type(PaymentType.ACCOUNT_TRANSFER)
                .state(PaymentState.NEW)
                .amount(amount)
                .currency(currency)
                .accountTransferData(accountTransferData)
                .userId(userId)
                .build();
    }
}
