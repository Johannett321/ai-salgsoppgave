import HeadingDescriptor from "../../components/HeadingDescriptor";
import MainTitle from "../../components/MainTitle";
import {Spinner} from "../../components/Spinner";
import {useSalgsoppgaveJob} from "../../providers/SalgsoppgaveJobProvider";
import {JobStatus} from "../../enums/JobStatus";

const LoadingPage = () => {
    const {salgsoppgaveJob} = useSalgsoppgaveJob()

    return (
        <div className="w-full mt-[50vh] -translate-y-[50%] flex flex-col justify-center items-center">
            <MainTitle>Scanner salgsoppgave...</MainTitle>

            <HeadingDescriptor>
                {salgsoppgaveJob?.jobStatus === JobStatus.NOT_STARTED && ("Dette kan ta litt tid...")}
                {salgsoppgaveJob?.jobStatus === JobStatus.VISITING_FINN && ("Leter etter salgsoppgaven på Finn annonsen...")}
                {salgsoppgaveJob?.jobStatus === JobStatus.VISITING_MEGLER && ("Leter etter salgsoppgave på meglers nettside...")}
                {salgsoppgaveJob?.jobStatus === JobStatus.DOWNLOADING_PROSPECT && ("Laster ned salgsoppgaven...")}
            </HeadingDescriptor>
            <Spinner className={"mt-5"}/>
        </div>
    )
}

export default LoadingPage;