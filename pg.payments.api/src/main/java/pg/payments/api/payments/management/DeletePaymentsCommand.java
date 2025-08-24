package pg.payments.api.payments.management;

import lombok.*;
import pg.lib.cqrs.command.Command;

import java.util.List;

@ToString
@EqualsAndHashCode
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class DeletePaymentsCommand implements Command<Void> {
    private List<String> paymentIds;
}
