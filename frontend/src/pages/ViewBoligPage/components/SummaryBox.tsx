import {useSalgsoppgaveJob} from "../../../providers/SalgsoppgaveJobProvider";
import {Spinner} from "../../../components/Spinner";
import {JobStatus} from "../../../enums/JobStatus";

const SummaryBox = () => {
    const {salgsoppgaveJob} = useSalgsoppgaveJob()

    return (
        <div className="min-h-[300px]">
            <div className={"bg-white p-4 mt-3 rounded-xl shadow-lg"}>
                {(salgsoppgaveJob?.salgsoppgave?.oppsummering) ? (
                    <div className={"h-[400px] w-full overflow-y-scroll"}>
                        <p className={"font-bold pb-4 text-main text-lg"}>Oppsummering</p>
                        {salgsoppgaveJob?.salgsoppgave?.oppsummering}
                    </div>
                ) : (
                    <div
                        className={"w-full h-[300px] flex flex-col justify-center items-center space-y-4 text-main opacity-50"}>
                        <p className={"max-w-[300px] text-center"}>Skriver en oppsummering. Vent litt...</p>
                        <Spinner/>
                    </div>
                )}
            </div>
        </div>
    );
};

export default SummaryBox;
