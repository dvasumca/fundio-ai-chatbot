
package com.fundio.aichatbot.service;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class PdfExtractionServiceTest {

    private PdfExtractionService pdfExtractionService;

    @BeforeEach
    void setUp() {
        pdfExtractionService = new PdfExtractionService();
    }

    @Test
    void shouldExtractTextFromPdfPages() throws IOException {

        byte[] pdfBytes;

        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {

            PDType1Font font =
                    new PDType1Font(Standard14Fonts.FontName.HELVETICA);

            for (String text : List.of(
                    "Survey number 372 contains 2 acres.",
                    "Owner name: Anthaiah")) {

                PDPage page = new PDPage();
                document.addPage(page);

                try (PDPageContentStream stream =
                             new PDPageContentStream(document, page)) {

                    stream.beginText();
                    stream.setFont(font, 12);
                    stream.newLineAtOffset(50, 700);
                    stream.showText(text);
                    stream.endText();
                }
            }

            document.save(output);
            pdfBytes = output.toByteArray();
        }

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "Land_Record.pdf",
                "application/pdf",
                pdfBytes
        );

        List<PdfExtractionService.PageContent> pages =
                pdfExtractionService.extractText(file);

        assertEquals(2, pages.size());

        assertEquals(1, pages.get(0).pageNumber());
        assertTrue(pages.get(0).text().contains(
                "Survey number 372 contains 2 acres."));

        assertEquals(2, pages.get(1).pageNumber());
        assertTrue(pages.get(1).text().contains(
                "Owner name: Anthaiah"));
    }

    @Test
    void shouldRejectEmptyPdfFile() {

        MockMultipartFile file = new MockMultipartFile(
                "file", "empty.pdf", "application/pdf", new byte[0]
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> pdfExtractionService.extractText(file)
        );
    }

    @Test
    void shouldRejectNonPdfFile() {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "document.txt",
                "text/plain",
                "Sample text".getBytes()
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> pdfExtractionService.extractText(file)
        );
    }
}
