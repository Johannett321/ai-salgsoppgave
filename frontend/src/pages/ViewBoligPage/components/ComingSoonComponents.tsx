const ComingSoonComponents = () => {
    return (
        <>
            <div className={"flex flex-row !mt-24 !mb-28 justify-center items-center w-full"}>
                <hr className={"border-2 border-main flex-grow"}/>
                <div className={"flex flex-col w-64 mx-4 md:w-auto md:mx-10"}>
                    <h2 className={"text-center text-3xl text-main"}>Kommer snart</h2>
                    <p className={"text-center py-1"}>
                        Disse visningene er under arbeid.
                    </p>
                </div>
                <hr className={"border-2 border-main flex-grow"}/>
            </div>
            <img className={"w-full opacity-50 blur-sm"} src={"/templates/tilstandsgrad.jpg"} alt={"Tilstandsgrad"}/>
            <img className={"w-full opacity-50 blur-sm"} src={"/templates/om_bolig_og_naeromraade.jpg"} alt={"Om bolig og nærområde"}/>
            <img className={"w-full opacity-50 blur-sm"} src={"/templates/egenerklaering.jpg"} alt={"Egenerklæring"}/>
        </>
    )
}

export default ComingSoonComponents;
