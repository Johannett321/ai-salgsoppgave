import PageHeading from "../../../components/PageHeading";
import RemarksBox from "./RemarksBox";
import {useSalgsoppgaveJob} from "../../../providers/SalgsoppgaveJobProvider";
import SummaryBox from "./SummaryBox";

const FaultsSection = () => {
    const {salgsoppgaveJob} = useSalgsoppgaveJob()

    return (
        <div className={"w-full grid xl:grid-cols-2 gap-10"}>
            <div>
                <PageHeading>Bemerkninger</PageHeading>
                <p>Dette er bemerkninger AISalgsoppgave gjorde da den leste salgsoppgaven</p>
                <RemarksBox/>
            </div>

            <div>
                <PageHeading>Oppsummering</PageHeading>
                <p>Dette er en oppsummering av salgsoppgaven</p>
                <SummaryBox />
            </div>
        </div>
    )
}

export default FaultsSection