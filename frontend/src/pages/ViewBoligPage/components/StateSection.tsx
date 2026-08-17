import PageHeading from "../../../components/PageHeading";

interface StateSectionProps {
    className?: string
}

const StateSection = ({className}: StateSectionProps) => {
    return (
        <div className={"" + className}>
            <PageHeading>Tilstand</PageHeading>
        </div>
    )
}

export default StateSection;