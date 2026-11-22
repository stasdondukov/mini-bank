package bank.adapters.file;

import bank.adapters.dto.TransactionDto;
import bank.domain.Transaction;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TransactionFileExporter {
    public void export(Path path, List<Transaction> transactions) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("id;from;to;amount;status");
        lines.addAll(transactions.stream()
            .map(TransactionDto::fromDomain)
            .map(this::formatLine)
            .toList());

        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.write(path, lines, StandardCharsets.UTF_8);
    }

    private String formatLine(TransactionDto transaction) {
        String from = transaction.from() == null ? "" : transaction.from();
        String to = transaction.to() == null ? "" : transaction.to();
        return transaction.id() + ";" + from + ";" + to + ";" + transaction.amount() + ";" + transaction.status();
    }
}
