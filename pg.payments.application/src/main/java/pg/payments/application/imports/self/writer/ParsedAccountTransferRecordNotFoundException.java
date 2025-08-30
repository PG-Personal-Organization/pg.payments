package pg.payments.application.imports.self.writer;

public class ParsedAccountTransferRecordNotFoundException extends RuntimeException {
    public ParsedAccountTransferRecordNotFoundException(final String id) {
        super("Parsed account transfer record with id " + id + " does not exist.");
    }
}
