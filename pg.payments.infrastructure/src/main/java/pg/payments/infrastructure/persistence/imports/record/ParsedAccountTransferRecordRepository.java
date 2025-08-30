package pg.payments.infrastructure.persistence.imports.record;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ParsedAccountTransferRecordRepository extends JpaRepository<ParsedAccountTransferRecord, String> {
}
