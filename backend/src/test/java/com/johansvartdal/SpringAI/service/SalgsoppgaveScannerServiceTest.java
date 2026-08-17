package com.johansvartdal.SpringAI.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SalgsoppgaveScannerServiceTest {

    @Autowired
    private SalgsoppgaveJobService salgsoppgaveJobService;

    /*
    @Test
    public void canFindTotalPris() {
        String result = salgsoppgaveScannerService.search("totalpris");
        assertTrue(result.contains("4 603 250"));
    }

    @Test
    public void canFindPrisAntydning() {
        String result = salgsoppgaveScannerService.search("prisantydning");
        assertTrue(result.contains("4 490 000"));
    }

    @Test
    public void canFindFelleskostnader() {
        String result = salgsoppgaveScannerService.search("felleskostnader");
        assertTrue(result.contains("nei"));
    }
     */
}