package com.johansvartdal.SpringAI.provider;

import com.johansvartdal.SpringAI.model.SalgsoppgaveJob;
import com.johansvartdal.SpringAI.utils.PdfUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;

@SpringBootTest
@Disabled("""
        Testen er utdatert og feiler.

        Den setter pdfContent, men MetricsProvider leser finnContent – provideren
        henter nøkkeltallene ved å parse HTML-en fra finn.no med Jsoup-selektorer,
        ikke fra PDF-teksten. Resultatet er en NullPointerException i Jsoup.parse.

        For å få den til å virke igjen trengs:
          1. en finn.no-HTML-fixture lagt inn under src/test/resources,
          2. en Salgsoppgave koblet på jobben (provideren kaller
             salgsoppgave.setPrisAntydning(...) uten nullsjekk),
          3. en kjørende database, siden provideren lagrer til slutt
             (docker compose up -d postgres).

        Se CONTRIBUTING.md – dette er en fin førsteoppgave.
        """)
class MetricsProviderTest {

    @Autowired
    private MetricsProvider metricsProvider;

    @Value("${salgsoppgave.datadir}")
    public String datadir;

    @Test
    void onSalgsoppgaveCreated() throws IOException {
        SalgsoppgaveJob salgsoppgaveJob = new SalgsoppgaveJob();
        salgsoppgaveJob.setPdfContent(PdfUtils.extractTextFromPDF(datadir + File.separator + "/EksempelOppgave2.txt"));
        metricsProvider.onSalgsoppgaveCreated(salgsoppgaveJob);
    }
}
