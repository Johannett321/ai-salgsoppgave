import {JobStatus} from "../enums/JobStatus";

interface StatusTagProps {
    status?: JobStatus
}

const StatusTag = ({status}: StatusTagProps) => {

    const getTextForStatus = (status:JobStatus) => {
        switch (status) {
            case JobStatus.COMPLETED:
                return "Ferdig"
            case JobStatus.DOWNLOADING_PROSPECT:
                return "Laster ned salgsoppgave"
            case JobStatus.FAILED:
                return "Feilet"
            case JobStatus.LLM_IN_PROGRESS:
                return "Leser salgsoppgave"
            case JobStatus.NOT_STARTED:
                return "I kø"
            case JobStatus.VISITING_FINN:
            case JobStatus.VISITING_MEGLER:
                return "Finner salgsoppgave"
            case JobStatus.CREATING_EMBEDDINGS:
                return "Prøver å forstå salgsoppgaven"
        }
    }

    const getColorForStatus = (status:JobStatus) => {
        switch (status) {
            case JobStatus.COMPLETED:
                return "bg-green-400"
            case JobStatus.VISITING_FINN:
            case JobStatus.VISITING_MEGLER:
            case JobStatus.DOWNLOADING_PROSPECT:
            case JobStatus.CREATING_EMBEDDINGS:
            case JobStatus.LLM_IN_PROGRESS:
            case JobStatus.NOT_STARTED:
                return "bg-orange-200"
            case JobStatus.FAILED:
                return "bg-red-300"
        }
    }

    return (
        <div>
            {status && (
                <div className={"rounded-2xl px-4 py-1 " + getColorForStatus(status)}>
                    {getTextForStatus(status)}
                </div>
            )}
        </div>
    )
}

export default StatusTag