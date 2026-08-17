import {Salgsoppgave} from "./Salgsoppgave";
import {JobStatus} from "../enums/JobStatus";

export interface SalgsoppgaveJob {
    id: string; // UUID
    jobStatus: JobStatus;
    failedReason: string;
    pdfPath: string;
    pdfContent: string; // Assuming this will be a large string, manage it appropriately
    salgsoppgave?: Salgsoppgave;
}