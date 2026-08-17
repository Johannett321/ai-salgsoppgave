import PageHeading from "../../../components/PageHeading";
import TitleDescCard from "../../../components/TitleDescCard";

interface TechnicalStateSectionProps {
    className?: string;
}

const TechnicalStateSection = ({ className }: TechnicalStateSectionProps) => {
    return (
        <div className={"" + className}>
            <PageHeading>Tekniske tilstandsgrader</PageHeading>
            <div className="flex">
                <div className="flex flex-col basis-2/3 gap-y-6">
                    <TitleDescCard
                        className="mt-3"
                        title="TG3"
                        desc="Totalt: 549 000 kr"
                    >
                        Hei
                    </TitleDescCard>
                    <TitleDescCard
                        className="mt-3"
                        title="Anbefalte oppgraderinger"
                    >
                        <p className="text-md">
                            Hvis du har boet i samme hus i flere år, kan det
                            være lett å overse tegnene på at dets tid å
                            oppgradere. Men, selv om det er bekvæmt og kjent,
                            kan en sliten bygning også være risikabel for din
                            sikkerhet og komfort.

                            Hvis du har boet i samme hus i flere år, kan det
                            være lett å overse tegnene på at dets tid å
                            oppgradere. Men, selv om det er bekvæmt og kjent,
                            kan en sliten bygning også være risikabel for din
                            sikkerhet og komfort.

                            Hvis du har boet i samme hus i flere år, kan det
                            være lett å overse tegnene på at dets tid å
                            oppgradere. Men, selv om det er bekvæmt og kjent,
                            kan en sliten bygning også være risikabel for din
                            sikkerhet og komfort.
                        </p>
                    </TitleDescCard>
                </div>
                <div className="pl-10 flex flex-col basis-1/3">
                    <TitleDescCard
                        className="mt-3"
                        title="TG2"
                        desc="Totalt: 549 000 kr"
                    >
                        Hei
                    </TitleDescCard>
                </div>
            </div>
        </div>
    );
};

export default TechnicalStateSection;
