package com.johansvartdal.SpringAI.enums;

public enum JobStatus {
    NOT_STARTED,
    VISITING_FINN,
    VISITING_MEGLER,
    DOWNLOADING_PROSPECT,
    CREATING_EMBEDDINGS,
    LLM_IN_PROGRESS,
    FAILED,
    COMPLETED
}
