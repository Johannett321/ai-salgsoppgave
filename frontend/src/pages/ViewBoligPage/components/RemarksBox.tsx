import {useSalgsoppgaveJob} from "../../../providers/SalgsoppgaveJobProvider";
import {Spinner} from "../../../components/Spinner";
import {JobStatus} from "../../../enums/JobStatus";

const RemarksBox = () => {
    const {salgsoppgaveJob} = useSalgsoppgaveJob()

    return (
        <div className="min-h-[300px]">
            <div className={"bg-white p-4 mt-3 rounded-xl shadow-lg"}>
                {(salgsoppgaveJob?.jobStatus == JobStatus.COMPLETED && salgsoppgaveJob?.salgsoppgave?.bemerkninger) ? (
                    <div className={"h-[400px] w-full overflow-y-scroll"}>
                        <p className={"font-bold pb-4 text-main text-lg"}>Bemerkninger</p>
                        {salgsoppgaveJob?.salgsoppgave?.bemerkninger?.map(bemerkning => (
                            <div key={bemerkning.id} className={"py-4 border-t-[1px]"}>
                                {bemerkning.bemerkning}
                            </div>
                        ))}
                    </div>
                ) : (
                    <div
                        className={"w-full h-[300px] flex flex-col justify-center items-center space-y-4 text-main opacity-50"}>
                        <p className={"max-w-[300px] text-center"}>Noterer ned bemerkninger... Dette tar gjerne opptil
                            10 minutter</p>
                        <Spinner/>
                    </div>
                )}
            </div>
        </div>
    );
};

export default RemarksBox;
