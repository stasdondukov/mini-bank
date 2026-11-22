package bank.adapters.file;

import bank.adapters.dto.TransactionDto;
import bank.domain.TransactionStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class TransactionFileImporter {
    public long countSuccessful(Path path) throws IOException {
        String successfulStatus = ";" + TransactionStatus.SUCCESS.name();
        try (Stream<String> lines = Files.lines(path)) {
            return lines.filter(line -> line.endsWith(successfulStatus)).count();
        } catch (UncheckedIOException exception) {
            throw exception.getCause();
        }
    }

    public List<TransactionDto> load(Path path) throws IOException {
        List<TransactionDto> transactions = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            if (!TransactionFileExporter.HEADER.equals(reader.readLine())) {
                throw new IOException("Invalid transaction header at line 1");
            }

            int lineNumber = 1;
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                transactions.add(parseLine(line, lineNumber));
            }
        }
        return transactions;
    }

    private TransactionDto parseLine(String line, int lineNumber) throws IOException {
        String[] fields = line.split(";", -1);
        if (fields.length != 5) {
            throw new IOException("Invalid transaction at line " + lineNumber + ": expected five fields");
        }
        try {
            long id = Long.parseLong(fields[0]);
            String from = fields[1].isEmpty() ? null : fields[1];
            String to = fields[2].isEmpty() ? null : fields[2];
            double amount = Double.parseDouble(fields[3]);
            TransactionStatus status = TransactionStatus.valueOf(fields[4]);
            return new TransactionDto(id, from, to, amount, status.name());
        } catch (IllegalArgumentException exception) {
            throw new IOException("Invalid transaction at line " + lineNumber + ": check id, amount or status", exception);
        }
    }
}
