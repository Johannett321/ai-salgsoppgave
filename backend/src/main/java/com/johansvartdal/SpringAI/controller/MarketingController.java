package com.johansvartdal.SpringAI.controller;

import com.johansvartdal.SpringAI.DTO.RequestMarketingFileDTO;
import com.johansvartdal.SpringAI.annotation.NoLogin;
import com.johansvartdal.SpringAI.service.MarketingService;
import lombok.SneakyThrows;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/marketing")
public class MarketingController {

    private final MarketingService marketingService;

    public MarketingController(MarketingService marketingService) {
        this.marketingService = marketingService;
    }

    @NoLogin
    @SneakyThrows
    @PostMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestBody RequestMarketingFileDTO requestMarketingFileDTO) {
        marketingService.preRegisterAndEmailFile(requestMarketingFileDTO);
        return ResponseEntity.ok().build();
    }

    @NoLogin
    @GetMapping("/download-file/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        return marketingService.actuallyDownloadFile(filename);
    }
}
