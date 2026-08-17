import PageHeading from "../../../components/PageHeading";
import TwoTextMetric from "../../../components/TwoTextMetric";
import {useSalgsoppgaveJob} from "../../../providers/SalgsoppgaveJobProvider";
import {formatAsNOK} from "../../../utils/FormatUtils";
import MapComponent from "../../../components/MapComponent";

const MetadataSection = () => {
    const {salgsoppgaveJob} = useSalgsoppgaveJob()

    return (
        <div className="">
            <PageHeading>Metadata</PageHeading>
            <div className="mt-2 lg:flex lg:flex-row">
                <div
                    className="w-full h-64 flex bg-main rounded-xl text-white items-center justify-center text-center p-2 xl:basis-2/3">
                    <MapComponent zoom={12} />

                </div>
                <div className="w-full grid gap-5 justify-center mt-5 grid-cols-2 lg:mt-0 lg:ml-5 xl:basis-1/3">
                    <TwoTextMetric
                        title="Prisantydning"
                        value={formatAsNOK(salgsoppgaveJob?.salgsoppgave?.prisAntydning)}
                    />
                    <TwoTextMetric
                        title="Totalpris"
                        value={formatAsNOK(salgsoppgaveJob?.salgsoppgave?.totalPris)}
                    />
                    <TwoTextMetric
                        title="Bruksareal"
                        value={salgsoppgaveJob?.salgsoppgave?.bruksAreal && salgsoppgaveJob?.salgsoppgave?.bruksAreal + " m"}
                        sup={"2"}
                        ending={" (BRA-i)"}
                    />
                    <TwoTextMetric
                        title="Byggeår"
                        value={salgsoppgaveJob?.salgsoppgave?.byggeAar}
                    />
                </div>
            </div>
        </div>
    );
};

export default MetadataSection;
