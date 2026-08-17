interface PageHeadingProps {
    children?: string;
}

const PageHeading = ({children}: PageHeadingProps) => {
    return (
        <>
        <h2 className="text-xl text-[#305345]">{children}</h2>
        </>
    )
}

export default PageHeading