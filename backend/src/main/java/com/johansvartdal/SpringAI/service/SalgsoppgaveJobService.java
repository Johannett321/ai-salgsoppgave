package com.johansvartdal.SpringAI.service;

import com.johansvartdal.SpringAI.enums.JobStatus;
import com.johansvartdal.SpringAI.exception.BadRequestException;
import com.johansvartdal.SpringAI.exception.NotFoundException;
import com.johansvartdal.SpringAI.model.*;
import com.johansvartdal.SpringAI.repository.*;
import com.johansvartdal.SpringAI.utils.PdfUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
public class SalgsoppgaveJobService {

    private final FinnScraper finnScraper;
    private final SalgsoppgaveJobRepo salgsoppgaveJobRepo;
    private final SalgsoppgaveRepo salgsoppgaveRepo;


    private final UserService userService;
    private final UserSalgsoppgaveJobRepo userSalgsoppgaveJobRepo;
    private final DataProvidersManagerService dataProvidersManagerService;

    public SalgsoppgaveJobService(FinnScraper finnScraper, SalgsoppgaveJobRepo salgsoppgaveJobRepo, SalgsoppgaveRepo salgsoppgaveRepo, UserService userService, UserSalgsoppgaveJobRepo userSalgsoppgaveJobRepo, DataProvidersManagerService dataProvidersManagerService) {
        this.finnScraper = finnScraper;
        this.salgsoppgaveJobRepo = salgsoppgaveJobRepo;
        this.salgsoppgaveRepo = salgsoppgaveRepo;
        this.userService = userService;
        this.userSalgsoppgaveJobRepo = userSalgsoppgaveJobRepo;
        this.dataProvidersManagerService = dataProvidersManagerService;
    }

    public UserSalgsoppgaveJob getJob(String id) {
        return userSalgsoppgaveJobRepo.findById(id).orElseThrow(NotFoundException::new);
    }

    public List<UserSalgsoppgaveJob> listHistoryForUser() {
        return userService.getCurrentUser().getSalgsoppgaveJobber();
    }

    public UserSalgsoppgaveJob createJob(String finnUrl) {
        finnUrl = finnUrl.trim().toLowerCase();

        //validate
        if (!finnUrl.contains("finn.no")) {
            log.warn("Could not process {}. Did not contain: finn.no", finnUrl);
            throw new BadRequestException();
        }

        // check if it has already been processed
        Optional<SalgsoppgaveJob> presentSalgsoppgaveJob = salgsoppgaveJobRepo.findFirstByFinnUrlOrderByStartDateDesc(finnUrl);
        if (presentSalgsoppgaveJob.isPresent()) {
            SalgsoppgaveJob salgsoppgaveJob = presentSalgsoppgaveJob.get();
            return addSalgsoppgaveJobToUser(salgsoppgaveJob);
        }

        // create job
        SalgsoppgaveJob salgsoppgaveJob = createSalgsoppgaveJob(finnUrl);
        UserSalgsoppgaveJob userSalgsoppgaveJob = addSalgsoppgaveJobToUser(salgsoppgaveJob);

        String finalFinnUrl = finnUrl;
        CompletableFuture.runAsync(() -> {
            try {
                downloadSalgsoppgave(salgsoppgaveJob);
                salgsoppgaveJobRepo.save(salgsoppgaveJob);

                dataProvidersManagerService.notifySalgsoppgaveJobCreatedListeners(salgsoppgaveJob);
            }catch (NotFoundException e) {
                log.warn("Klarte ikke laste ned salgsoppgave fra Finnurl '{}'", finalFinnUrl);
                salgsoppgaveJob.setJobStatus(JobStatus.FAILED);
                salgsoppgaveJob.setFailedReason("Klarte ikke laste ned salgsoppgave. Prøv å laste opp salgsoppgave PDF manuelt");
                salgsoppgaveJobRepo.save(salgsoppgaveJob);
            }
        });

        return userSalgsoppgaveJob;
    }

    private SalgsoppgaveJob createSalgsoppgaveJob(String finnUrl) {
        SalgsoppgaveJob salgsoppgaveJob = new SalgsoppgaveJob();
        salgsoppgaveJob.setFinnUrl(finnUrl);
        salgsoppgaveJob.setJobStatus(JobStatus.NOT_STARTED);
        salgsoppgaveJob.setStartDate(LocalDateTime.now());
        return salgsoppgaveJobRepo.save(salgsoppgaveJob);
    }

    private UserSalgsoppgaveJob addSalgsoppgaveJobToUser(SalgsoppgaveJob salgsoppgaveJob) {
        // add job to user
        User currentUser = userService.getCurrentUser();
        if (currentUser != null) {
            Optional<UserSalgsoppgaveJob> optionalUserSalgsoppgaveJob = userSalgsoppgaveJobRepo.findByUserAndSalgsoppgaveJob(currentUser, salgsoppgaveJob);
            if (optionalUserSalgsoppgaveJob.isPresent()) {
                return optionalUserSalgsoppgaveJob.get();
            }

            UserSalgsoppgaveJob userSalgsoppgaveJob = new UserSalgsoppgaveJob();
            userSalgsoppgaveJob.setUser(currentUser);
            userSalgsoppgaveJob.setSalgsoppgaveJob(salgsoppgaveJob);
            return userSalgsoppgaveJobRepo.save(userSalgsoppgaveJob);
        }

        return null;
    }

    private SalgsoppgaveJob downloadSalgsoppgave(SalgsoppgaveJob salgsoppgaveJob) throws NotFoundException {
        finnScraper.getKomplettSalgsoppgaveUrl(salgsoppgaveJob);

        // download pdf
        salgsoppgaveJob.setJobStatus(JobStatus.DOWNLOADING_PROSPECT);
        salgsoppgaveJobRepo.save(salgsoppgaveJob);
        String pdfContent = PdfUtils.extractTextFromPDFUrl(salgsoppgaveJob.getSalgsoppgavePDFUrl());
        salgsoppgaveJob.setPdfContent(pdfContent);
        salgsoppgaveJobRepo.save(salgsoppgaveJob);

        // create salgsoppgave
        Salgsoppgave salgsoppgave = new Salgsoppgave();
        salgsoppgave = salgsoppgaveRepo.save(salgsoppgave);

        // connect salgsoppgave to salgsoppgavejob
        salgsoppgaveJob.setSalgsoppgave(salgsoppgave);
        salgsoppgaveJobRepo.save(salgsoppgaveJob);
        return salgsoppgaveJob;
    }

    @Scheduled(fixedRate = 300000)
    public void scheduledTimeout() {
        List<SalgsoppgaveJob> notCompleted = salgsoppgaveJobRepo.findAllByJobStatusNotIn(List.of(JobStatus.COMPLETED, JobStatus.FAILED));
        notCompleted.stream()
                .filter(job -> job.getStartDate().isBefore(LocalDateTime.now().minusMinutes(10)))
                .forEach(job -> {
                    job.setJobStatus(JobStatus.FAILED);
                    job.setFailedReason("Brukte for lang tid på å lese");
                    salgsoppgaveJobRepo.save(job);

                    log.warn("Timed out job with id '{}'", job.getId());
                });
    }

    public void processManualPDF(SalgsoppgaveJob salgsoppgaveJob, MultipartFile salgsoppgavePDF) {
        String content;
        try {
            content = PdfUtils.extractTextFromPDF(salgsoppgavePDF);
        } catch (IOException e) {
            salgsoppgaveJob.setFailedReason("Klarte ikke lese PDF fil");
            salgsoppgaveJob.setJobStatus(JobStatus.FAILED);
            salgsoppgaveJobRepo.save(salgsoppgaveJob);
            throw new RuntimeException(e);
        }

        Salgsoppgave salgsoppgave = new Salgsoppgave();
        salgsoppgave = salgsoppgaveRepo.save(salgsoppgave);

        salgsoppgaveJob.setPdfContent(content);
        salgsoppgaveJob.setStartDate(LocalDateTime.now());
        salgsoppgaveJob.setFailedReason(null);
        salgsoppgaveJob.setJobStatus(JobStatus.CREATING_EMBEDDINGS);
        salgsoppgaveJob.setSalgsoppgave(salgsoppgave);
        salgsoppgaveJob = salgsoppgaveJobRepo.save(salgsoppgaveJob);

        SalgsoppgaveJob finalSalgsoppgaveJob = salgsoppgaveJob;
        Thread thread = new Thread(() -> {
                // notify listeners
                dataProvidersManagerService.notifySalgsoppgaveJobCreatedListeners(finalSalgsoppgaveJob);
        });
        thread.start();
    }
}
