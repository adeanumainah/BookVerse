package com.perpustakaan.dean.service;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PdfService {

    private final SpringTemplateEngine templateEngine;

    public byte[] generatePdfFromTemplate(String templateName, Map<String, Object> variables) {
        Context ctx = new Context();
        if (variables != null) ctx.setVariables(variables);

        String html = templateEngine.process(templateName, ctx);

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(html, null); 
            builder.toStream(os);
            builder.run();
            return os.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Gagal membuat PDF: " + e.getMessage(), e);
        }
    }
}

