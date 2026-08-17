import {Link} from "react-router-dom";
import React from "react";
import HeadingDescriptor from "../../../components/HeadingDescriptor";
import PageHeading from "../../../components/PageHeading";

interface ForgotPasswordSuccessProps {
    onTryDifferentEmail: () => void;
}

const ForgotPasswordSuccessComponent: React.FC<ForgotPasswordSuccessProps> = ({ onTryDifferentEmail }) => {
    return (
        <>
            <div className="flex min-h-full flex-1 flex-col justify-center py-12 sm:px-6 lg:px-8">
                <div className="sm:mx-auto sm:w-full sm:max-w-md">
                    <img
                        className="mx-auto h-10 w-auto"
                        src="/images/logo_dark.webp"
                        alt="AISalgsoppgave"
                    />
                </div>
                <div className="mt-10 sm:mx-auto sm:w-full sm:max-w-[480px]">
                    <div className="bg-white px-6 py-12 shadow sm:rounded-lg sm:px-12">
                        <PageHeading>
                            Glemt passord?
                        </PageHeading>
                        <HeadingDescriptor>
                            Takk! Hvis e-post adressen samsvarer med en e-post vi har registrert,
                            så har vi sendt deg en e-post med videre instruksjoner for å tilbakestille
                            passordet ditt.
                        </HeadingDescriptor>
                        <HeadingDescriptor>
                            Hvis du ikke har mottatt en e-post innen 5 minutter, vennligst sjekk spam-mappen din
                        </HeadingDescriptor>
                        <button onClick={onTryDifferentEmail} style={{color: 'blue', cursor: 'pointer'}}>
                            prøve en annen e-post
                        </button>
                    </div>
                </div>
            </div>
        </>
    )
}
export default ForgotPasswordSuccessComponent;