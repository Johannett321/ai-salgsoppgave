import {SalgsoppgaveBemerkning} from "./SalgsoppgaveBemerkning";

export interface Salgsoppgave {
    id: string;

    gateNavn?: string;
    gateNummer?: string; // string as it can be 18b
    postNummer?: number;
    postSted?: string;
    longtitude?: number
    latitude?: number

    prisAntydning?: number;
    totalPris?: number;
    bruksAreal?: number;
    byggeAar?: number;

    bemerkninger?: SalgsoppgaveBemerkning[];
    oppsummering: string
    // TG3: FeilInfo[];

}