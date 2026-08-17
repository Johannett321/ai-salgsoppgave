package com.johansvartdal.SpringAI.controller;

import com.johansvartdal.SpringAI.DTO.SalgsOppgaveReqDTO;
import com.johansvartdal.SpringAI.model.UserSalgsoppgaveJob;
import com.johansvartdal.SpringAI.service.SalgsoppgaveJobService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/job")
public class ScannJobController {

    private final SalgsoppgaveJobService salgsoppgaveJobService;

    public ScannJobController(SalgsoppgaveJobService salgsoppgaveJobService) {
        this.salgsoppgaveJobService = salgsoppgaveJobService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserSalgsoppgaveJob> getJobById(@PathVariable("id") String id) {
        return ResponseEntity.ok(salgsoppgaveJobService.getJob(id));
    }

    @GetMapping("/history")
    public ResponseEntity<List<UserSalgsoppgaveJob>> getJobs() {
        return ResponseEntity.ok(salgsoppgaveJobService.listHistoryForUser());
    }

    @PostMapping("/from-finn-url")
    public ResponseEntity<UserSalgsoppgaveJob> fromFinn(@RequestBody SalgsOppgaveReqDTO reqDTO) {
        return ResponseEntity.ok(salgsoppgaveJobService.createJob(reqDTO.getFinnUrl()));
    }

    @PostMapping("/{userSalgsoppgaveJob}/manual-pdf")
    public ResponseEntity<String> updateTheme(@PathVariable UserSalgsoppgaveJob userSalgsoppgaveJob, @RequestParam("salgsoppgavePDF") MultipartFile salgsoppgavePDF) {
        // Start the async process in the background
        salgsoppgaveJobService.processManualPDF(userSalgsoppgaveJob.getSalgsoppgaveJob(), salgsoppgavePDF);

        // Return the jobID so the frontend can track the progress
        return ResponseEntity.ok().build();
    }
}
