import HeadingDescriptor from "./HeadingDescriptor";
import MainButton from "./MainButton";
import {useNavigate} from "react-router";
import {ReactNode} from "react";
import {useUser} from "../providers/UserProvider";

interface RealEstateGridEmptyStateProps {
    children?: ReactNode;
}

const RealEstateGridEmptyState = ({children}: RealEstateGridEmptyStateProps) => {
    const navigate = useNavigate()
    const {currentUser} = useUser()

    return (
        <div className={"container relative mx-auto"}>
            <div className={"w-full fixed left-[50%] top-[50%] -translate-x-[50%] -translate-y-[50%] z-10 md:-translate-x-[20%] md:w-auto"}>
                {currentUser ? (
                    <>
                        <h2 className={"text-3xl text-main font-bold"}>
                            Kommer snart
                        </h2>
                        <HeadingDescriptor className={"mt-2 max-w-[500px] mx-auto"}>
                            Denne funksjonen er under utvikling.
                        </HeadingDescriptor>
                    </>
                ) : (
                    <>
                        <h2 className={"text-3xl text-main font-bold"}>
                            Vil du ha tilgang til denne funksjonen?
                        </h2>
                        <HeadingDescriptor className={"mt-2"}>
                            Denne funksjonen er bare tilgjengelig for innloggede brukere
                        </HeadingDescriptor>

                        <MainButton className={"mt-6"} onClick={() => navigate("/register")}>
                            Opprett en bruker
                        </MainButton>
                    </>
                )}

            </div>

            <div className={"opacity-50 -z-10"}>
                {children}
            </div>
        </div>
    )
}

export default RealEstateGridEmptyState;
