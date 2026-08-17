package com.johansvartdal.SpringAI.service;

import com.johansvartdal.SpringAI.enums.JobStatus;
import com.johansvartdal.SpringAI.listener.SalgsoppgaveJobCreatedListener;
import com.johansvartdal.SpringAI.model.SalgsoppgaveJob;
import com.johansvartdal.SpringAI.provider.DataProvider;
import com.johansvartdal.SpringAI.repository.SalgsoppgaveJobRepo;
import com.johansvartdal.SpringAI.utils.ProviderUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class DataProvidersManagerService {

    private final ArrayList<SalgsoppgaveJobCreatedListener> salgsoppgaveJobCreatedListeners = new ArrayList<>();
    private final SalgsoppgaveJobRepo salgsoppgaveJobRepo;

    public DataProvidersManagerService(SalgsoppgaveJobRepo salgsoppgaveJobRepo) {
        this.salgsoppgaveJobRepo = salgsoppgaveJobRepo;
    }

    public void subscribeToNewSalgsoppgaveJob(SalgsoppgaveJobCreatedListener salgsoppgaveJobCreatedListener) {
        salgsoppgaveJobCreatedListeners.add(salgsoppgaveJobCreatedListener);
    }

    private ArrayList<List<SalgsoppgaveJob>> getListsOfSalgsoppgaverFromProviders() {
        ArrayList<List<SalgsoppgaveJob>> allLists = new ArrayList();
        for (SalgsoppgaveJobCreatedListener provider : salgsoppgaveJobCreatedListeners) {
            if (provider instanceof DataProvider) {
                DataProvider dataProvider = (DataProvider) provider;
                allLists.add(dataProvider.getFinished());
            }
        }
        return allLists;
    }

    public void notifySalgsoppgaveJobCreatedListeners(SalgsoppgaveJob salgsoppgaveJob) {
        Thread thread = new Thread(() -> {
            salgsoppgaveJobCreatedListeners.stream()
                    .sorted(Comparator.comparing(SalgsoppgaveJobCreatedListener::getOrder))
                    .forEach(listener -> {
                        listener.onSalgsoppgaveCreated(salgsoppgaveJob);
                    });
        });
        thread.start();
    }

    @Scheduled(fixedRate = 2000)
    public void scheduledMarkCompleted() {
        ArrayList<List<SalgsoppgaveJob>> allLists = getListsOfSalgsoppgaverFromProviders();
        Set<SalgsoppgaveJob> commonSalgsoppgaver = ProviderUtils.findCommonElements(allLists);

        commonSalgsoppgaver.forEach(salgsoppgaveJob -> {
            salgsoppgaveJob.setJobStatus(JobStatus.COMPLETED);
            salgsoppgaveJobRepo.save(salgsoppgaveJob);

            log.info("Marked salgsoppgaveJob {} as completed", salgsoppgaveJob.getId());
        });

        ProviderUtils.removeCommonElements(allLists, commonSalgsoppgaver);
    }
}
