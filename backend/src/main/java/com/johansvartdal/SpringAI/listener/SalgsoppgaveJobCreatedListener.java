package com.johansvartdal.SpringAI.listener;

import com.johansvartdal.SpringAI.model.SalgsoppgaveJob;

public interface SalgsoppgaveJobCreatedListener {

    int getOrder();
    void onSalgsoppgaveCreated(SalgsoppgaveJob salgsoppgaveJob);
}
