
package com.fundio.aichatbot.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PdfExtractionService {

    public record PageContent(Integer pageNumber, String text) {}

    public List<PageContent> extractText(MultipartFile file) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("PDF file is empty");
        }

        String filename = file.getOriginalFilename();

        if (filename == null ||
                !filename.toLowerCase(Locale.ROOT).endsWith(".pdf")) {
            throw new IllegalArgumentException("Only PDF files are allowed");
        }

        List<PageContent> pages = new ArrayList<>();

        try (PDDocument document = Loader.loadPDF(file.getBytes())) {

            if (document.isEncrypted()) {
                throw new IllegalArgumentException(
                        "Encrypted PDFs are not supported");
            }

            PDFTextStripper stripper = new PDFTextStripper();

            for (int page = 1; page <= document.getNumberOfPages(); page++) {

                stripper.setStartPage(page);
                stripper.setEndPage(page);

                String text = stripper.getText(document).trim();

                if (!text.isBlank()) {
                    pages.add(new PageContent(page, text));
                }
            }
        }

        if (pages.isEmpty()) {
            throw new IllegalArgumentException(
                    "No extractable text found in PDF");
        }

        return pages;
    }
}
