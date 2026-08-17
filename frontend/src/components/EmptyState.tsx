interface EmptyStateProps {
    title?: string;
    description: string;
    image?: string;
}

const EmptyState = ({title, description, image}: EmptyStateProps) => {
    return (
        <>
            <img className={"w-80"} src={image || "/images/empty.png"} alt={"Empty illustration"}/>
            <div className={"w-[350px] mt-10 space-y-2"}>
                <h1 className={"font-bold text-3xl text-main"}>{title || "Her var det ganske tomt!"}</h1>
                <p className={"text-lg"}>
                    {description}
                </p>
            </div>
        </>
    )
}

export default EmptyState