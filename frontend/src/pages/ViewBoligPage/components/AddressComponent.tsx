import MainTitle from "../../../components/MainTitle";
import Tag from "../../../components/Tag";
import {JobStatus} from "../../../enums/JobStatus";
import Skeleton from "react-loading-skeleton";
import {useSalgsoppgaveJob} from "../../../providers/SalgsoppgaveJobProvider";

const AddressComponent = () => {
    const {salgsoppgaveJob} = useSalgsoppgaveJob()

    return (
        <>
            {(salgsoppgaveJob?.salgsoppgave?.gateNavn && salgsoppgaveJob?.salgsoppgave?.gateNummer && salgsoppgaveJob.salgsoppgave?.postNummer && salgsoppgaveJob.salgsoppgave?.postSted) ? (
                <>
                    <MainTitle>{salgsoppgaveJob.salgsoppgave.gateNavn} {salgsoppgaveJob.salgsoppgave.gateNummer}</MainTitle>
                    <Tag>{salgsoppgaveJob.salgsoppgave.postNummer} {salgsoppgaveJob.salgsoppgave.postSted}</Tag>
                </>
            ) : (
                <div className={"w-64"}>
                    {salgsoppgaveJob?.jobStatus === JobStatus.COMPLETED ? (
                        <>
                            <p className={"font-bold"}>FEILET</p>
                        </>
                    ) : (
                        <>
                            <Skeleton height={30} count={1}></Skeleton>
                            <Skeleton width={150} height={20} count={1}></Skeleton>
                        </>
                    )}
                </div>
            )}
        </>
    )
}

export default AddressComponent