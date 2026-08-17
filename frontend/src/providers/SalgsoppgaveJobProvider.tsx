import {createContext, useContext, useEffect, useState} from "react";
import {SalgsoppgaveJob} from "../types/SalgsoppgaveJob";
import axios from "axios";
import {JobStatus} from "../enums/JobStatus";
import toast from "react-hot-toast";
import {getBackendURL} from "../utils/EnvironmentsManager";
import {UserSalgsoppgaveJob} from "../types/UserSalgsoppgaveJob";
import {ChatConversation} from "../types/ChatConversation";

export interface SalgsoppgaveJobHolder {
    userSalgsoppgaveJob?: UserSalgsoppgaveJob
    loadSalgsoppgaveJob: (id: string) => void
    salgsoppgaveJob?: SalgsoppgaveJob | undefined
}

export const SalgsoppgaveJobContext = createContext<SalgsoppgaveJobHolder>({
    loadSalgsoppgaveJob: () => {}
})

export const useSalgsoppgaveJob = () => {
    const context = useContext(SalgsoppgaveJobContext);
    if (context === null) {
        throw new Error("Must be used as useSalgsoppgaveJobProvider");
    }
    return context
}

interface SalgsoppgaveJobProviderProps {
    children: any
}

export const SalgsoppgaveJobProvider = ({children}: SalgsoppgaveJobProviderProps) => {
    const [userSalgsoppgaveJob, setUserSalgsoppgaveJob] = useState<UserSalgsoppgaveJob | undefined>()
    const [salgsoppgaveJob, setSalgsoppgaveJob] = useState<SalgsoppgaveJob | undefined>()

    const loadSalgsoppgaveJob = (id: string) => {
        if (userSalgsoppgaveJob !== undefined && id != userSalgsoppgaveJob?.id) {
            console.log(id + " does not match " + userSalgsoppgaveJob.id)
            setUserSalgsoppgaveJob(undefined)
            setSalgsoppgaveJob(undefined)
        }

        axios.get(getBackendURL()+ "/api/v1/job/" + id).then(result => {
            let userSalgsoppgaveJob: UserSalgsoppgaveJob = result.data

            setUserSalgsoppgaveJob(userSalgsoppgaveJob)
            setSalgsoppgaveJob(userSalgsoppgaveJob.salgsoppgaveJob)
        }).catch(error => {
            console.log(error)
            toast.error("Får ikke kontakt med AiSalgsoppgave.no")
        })
    }

    useEffect(() => {
        if (userSalgsoppgaveJob === undefined) {
            return
        }

        // reload if not complete
        if (userSalgsoppgaveJob.salgsoppgaveJob.jobStatus != JobStatus.COMPLETED && userSalgsoppgaveJob.salgsoppgaveJob.jobStatus != JobStatus.FAILED) {
            const timeoutId = setTimeout(() => {
                loadSalgsoppgaveJob(userSalgsoppgaveJob.id)
            }, 1000)

            return () => clearTimeout(timeoutId);
        }
    }, [userSalgsoppgaveJob]);

    return (
        <SalgsoppgaveJobContext.Provider value={{userSalgsoppgaveJob, loadSalgsoppgaveJob, salgsoppgaveJob}}>
            {children}
        </SalgsoppgaveJobContext.Provider>
    )
}