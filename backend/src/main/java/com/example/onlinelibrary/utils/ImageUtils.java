package com.example.onlinelibrary.utils;

import java.io.IOException;
import java.util.Base64;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ImageUtils {

    public String toDataUri(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Thumbnail must be an image");
        }
        try {
            return "data:" + contentType + ";base64," + Base64.getEncoder().encodeToString(file.getBytes());
        } catch (IOException exception) {
            throw new IllegalArgumentException("Could not read thumbnail image");
        }
    }

    public String fallbackCover(String title, String accentColor) {
        String svg = """
                <svg xmlns='http://www.w3.org/2000/svg' width='480' height='640' viewBox='0 0 480 640'>
                  <rect width='480' height='640' fill='#0f172a'/>
                  <rect x='36' y='36' width='408' height='568' rx='32' fill='%s'/>
                  <rect x='76' y='90' width='328' height='12' rx='6' fill='#0f172a' opacity='.18'/>
                  <rect x='76' y='120' width='230' height='12' rx='6' fill='#0f172a' opacity='.18'/>
                  <text x='76' y='338' font-family='Arial, sans-serif' font-size='42' font-weight='800' fill='#0f172a'>%s</text>
                  <text x='76' y='520' font-family='Arial, sans-serif' font-size='20' font-weight='700' fill='#0f172a' opacity='.65'>BOOKIFY</text>
                </svg>
                """.formatted(accentColor, escapeXml(shortTitle(title)));
        return "data:image/svg+xml;base64," + Base64.getEncoder().encodeToString(svg.getBytes());
    }

    private String shortTitle(String title) {
        if (title == null || title.isBlank()) {
            return "Libro";
        }
        return title.length() > 14 ? title.substring(0, 14) + "..." : title;
    }

    private String escapeXml(String value) {
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
