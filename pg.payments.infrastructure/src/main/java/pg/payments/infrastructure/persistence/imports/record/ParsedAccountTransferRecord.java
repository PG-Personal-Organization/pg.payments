package pg.payments.infrastructure.persistence.imports.record;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "parsed_account_transfers")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ParsedAccountTransferRecord {
    @Id
    private String id;

    private String importId;

    private String creditedAccountNumber;

    private String transferAccountNumber;

    private BigDecimal amount;

    private String currency;

    private String description;

    private int ordinal;

    private String recordStatus;

    @ElementCollection
    private List<String> errorMessages;

    @Column(nullable = false)
    private LocalDateTime createdOn;

    private LocalDateTime modifiedOn;
}
