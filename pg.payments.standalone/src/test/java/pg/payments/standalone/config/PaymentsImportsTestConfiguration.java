package pg.payments.standalone.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import pg.lib.awsfiles.infrastructure.config.InMemoryMockConfiguration;
import pg.payments.application.imports.importing.AccountTransfersImportingComponentsProvider;
import pg.payments.application.imports.parsing.AccountTransfersParsingComponentsProvider;
import pg.payments.application.imports.self.writer.AccountTransferPluginRecordsWriter;
import pg.payments.standalone.plugins.*;

@Import({
        InMemoryMockConfiguration.class,
})
@TestConfiguration
@RequiredArgsConstructor(onConstructor = @__({@Autowired, @Lazy}))
public class PaymentsImportsTestConfiguration {
    private final AccountTransfersParsingComponentsProvider parsingComponentsProvider;
    private final AccountTransfersImportingComponentsProvider importingComponentsProvider;
    private final AccountTransferPluginRecordsWriter pluginRecordsWriter;

    @Bean
    public DistributedAccountTransfersPlugin distributedAccountTransfersPlugin() {
        return new DistributedAccountTransfersPlugin(parsingComponentsProvider, importingComponentsProvider, pluginRecordsWriter);
    }
    
    @Bean
    public DistributedMongoStoringAccountTransfersPlugin distributedMongoStoringAccountTransfersPlugin() {
        return new DistributedMongoStoringAccountTransfersPlugin(parsingComponentsProvider, importingComponentsProvider, pluginRecordsWriter);
    }
    
    @Bean
    public ParallelAccountTransfersPlugin parallelAccountTransfersPlugin() {
        return new ParallelAccountTransfersPlugin(parsingComponentsProvider, importingComponentsProvider, pluginRecordsWriter);
    }
    
    @Bean
    public SimpleAccountTransfersPlugin simpleAccountTransfersPlugin() {
        return new SimpleAccountTransfersPlugin(parsingComponentsProvider, importingComponentsProvider, pluginRecordsWriter);
    }
    
    @Bean
    public SimpleSelfStoringAccountTransfersPlugin simpleSelfStoringAccountTransfersPlugin() {
        return new SimpleSelfStoringAccountTransfersPlugin(parsingComponentsProvider, importingComponentsProvider, pluginRecordsWriter);
    }

}
