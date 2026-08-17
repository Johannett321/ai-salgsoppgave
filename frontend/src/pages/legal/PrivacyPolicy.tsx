import React from 'react';
import HeadingDescriptor from "../../components/HeadingDescriptor";
import {Link} from "react-router-dom";

const PrivacyPolicy = () => {
    return (
        <>
            <img
                className="mx-auto h-16 w-auto mb-8 mt-16"
                src="/images/logo_dark.webp"
                alt="AISalgsoppgave"/>
            <div className="bg-white p-12 shadow-lg rounded-md sm:mx-auto sm:max-w-4xl my-10 text-left">
                <h1 className={"text-3xl font-medium text-main"}>Personvernserklæring</h1>
                <HeadingDescriptor className={"mb-5"}>
                    Sist oppdatert: 9. oktober 2024
                </HeadingDescriptor>
                <h2 className="text-2xl font-semibold mb-2">1. Introduksjon</h2>
                <p className="mb-4">
                    Denne personvernserklæringen beskriver hvordan vi samler inn, bruker
                    og beskytter dine personopplysninger når du benytter vår tjeneste. Ved å bruke vår nettside og
                    tjenester, samtykker du til innsamling og bruk av informasjon i henhold til denne erklæringen.
                </p>

                <h2 className="text-2xl font-semibold mb-2">2. Hvilke personopplysninger vi samler inn</h2>
                <p className="mb-4">
                    Vi samler inn følgende typer informasjon når du bruker våre tjenester:
                    <ul className="mb-4 list-disc pl-5">
                        <li>Personopplysninger som navn, e-postadresse og kontaktinformasjon.</li>
                        <li>Brukerdata som IP-adresse, nettleser, og bruksinformasjon.</li>
                    </ul>
                </p>

                <h2 className="text-2xl font-semibold mb-2">3. Hvordan vi bruker personopplysningene</h2>
                <p className="mb-4">
                    Vi bruker de innsamlede opplysningene til følgende formål:
                    <ul className="mb-4 list-disc pl-5">
                        <li>For å gi deg tilgang til og forbedre våre tjenester.</li>
                        <li>For å kontakte deg angående oppdateringer og viktig informasjon om tjenesten.</li>
                        <li>For å analysere bruken av tjenesten og forbedre brukeropplevelsen.</li>
                    </ul>
                </p>

                <h2 className="text-2xl font-semibold mb-2">4. Deling av opplysninger med tredjeparter</h2>
                <p className="mb-4">
                    Vi deler dine personopplysninger med tredjeparter kun når det er nødvendig for å levere våre
                    tjenester eller når det er påkrevd ved lov.
                </p>

                <p className="mb-4">
                    Vi bruker ingen analyse- eller sporingsverktøy. Tjenesten samler ikke inn bruksdata om deg, og
                    sender ingen data til annonsenettverk.
                </p>

                <h3 className="text-lg font-semibold mb-2">Betaling</h3>
                <p className="mb-4">
                    Tjenesten er gratis. Vi tar ikke betalt, og behandler derfor ingen betalingsopplysninger.
                </p>

                <p className="mb-4">
                    Vi forsikrer at alle tredjeparter vi samarbeider med følger relevante lover og forskrifter for
                    personvern, og de har forpliktet seg til å beskytte dine personopplysninger i tråd med våre
                    retningslinjer.
                </p>

                <h3 className="text-lg font-semibold mb-2">Andre tredjeparter</h3>
                <p className="mb-4">
                    I noen tilfeller kan vi også dele dine opplysninger med andre tredjeparter som er nødvendige for å
                    levere våre tjenester, som tjenesteleverandører, konsulenter eller juridiske myndigheter. Dette
                    gjøres alltid i samsvar med lovpålagte krav og kun med parter som har tilfredsstillende
                    sikkerhetstiltak for å beskytte dine data.
                </p>

                <h2 className="text-2xl font-semibold mb-2">5. Sikkerhet</h2>
                <p className="mb-4">
                    Vi iverksetter egnede sikkerhetstiltak for å beskytte dine personopplysninger mot uautorisert
                    tilgang, endring, avsløring eller ødeleggelse. Vi benytter oss av kryptering, sikker lagring og
                    kontinuerlig overvåking for å sikre dine data.
                </p>

                <h2 className="text-2xl font-semibold mb-2">6. Dine rettigheter</h2>
                <p className="mb-4">
                    Du har rett til å få tilgang til, rette eller slette dine personopplysninger som vi lagrer. Hvis du
                    ønsker å utøve noen av dine rettigheter, vennligst kontakt oss på post@aisalgsoppgave.no.
                </p>

                <h2 className="text-2xl font-semibold mb-2">7. Endringer i personvernserklæringen</h2>
                <p className="mb-4">
                    Vi kan oppdatere denne personvernserklæringen fra tid til annen. Enhver endring vil bli publisert på
                    denne siden, og vi vil gi beskjed via e-post eller andre passende kanaler hvis endringene er
                    vesentlige.
                </p>

                <h2 className="text-2xl font-semibold mb-2">8. Kontaktinformasjon</h2>
                <p className="mb-4">
                    Hvis du har spørsmål angående vår personvernpraksis, kan du kontakte oss på:
                    <br/>E-post: <Link to={"mailto:post@aisalgsoppgave.no"}
                                       className={"text-main underline cursor-pointer"}>post@aisalgsoppgave.no</Link>
                </p>

                <p className="text-center mt-6">
                    © 2024 AISalgsoppgave. Alle rettigheter forbeholdes.
                </p>
            </div>
        </>
    );
};

export default PrivacyPolicy;