package pg.payments.standalone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import pg.payments.infrastructure.spring.configuration.PaymentsModuleConfiguration;

@SpringBootApplication
@Import({
        PaymentsModuleConfiguration.class
})
public class PaymentsModuleApplication {
    public static void main(final String[] args) {
        SpringApplication.run(PaymentsModuleApplication.class, args);
    }
}
