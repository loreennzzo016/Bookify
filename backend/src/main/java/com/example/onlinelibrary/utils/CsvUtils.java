package com.example.onlinelibrary.utils;

import com.example.onlinelibrary.model.Book;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CsvUtils {

    public byte[] booksToCsv(List<Book> books) {
        StringBuilder csv = new StringBuilder("id,title,author,year,assignedTo\n");
        for (Book book : books) {
            csv.append(book.getId()).append(',')
                    .append(escape(book.getTitle())).append(',')
                    .append(escape(book.getAuthor())).append(',')
                    .append(book.getYear()).append(',')
                    .append(escape(book.getAssignedTo() == null ? "" : book.getAssignedTo().getUsername()))
                    .append('\n');
        }
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        String escaped = value.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }
}
