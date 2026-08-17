import React from 'react';
import HeadingDescriptor from "../../components/HeadingDescriptor";
import {Link} from "react-router-dom";

const TermsOfService = () => {

    return (
        <>
            <img
                className="mx-auto h-16 w-auto mb-8 mt-16"
                src="/images/logo_dark.webp"
                alt="AISalgsoppgave"/>
            <div className="bg-white p-12 shadow-lg rounded-md sm:mx-auto sm:max-w-4xl my-10 text-left">
                <h1 className={"text-3xl font-medium text-main"}>Vilkår for bruk</h1>
                <HeadingDescriptor>
                    Sist oppdatert: 9. oktober 2024
                </HeadingDescriptor>
                <p className="mb-4">
                    Velkommen til AISalgsoppgave! Ved å bruke våre tjenester, samtykker du i å overholde følgende
                    vilkår. Disse vilkårene beskriver dine rettigheter og ansvar når du bruker vår plattform.
                </p>

                <h2 className="text-2xl font-semibold mb-2">1. Generelt</h2>
                <p className="mb-4">
                    Disse vilkårene gjelder for all bruk av nettstedet og tjenestene som tilbys av AISalgsoppgave. Ved å
                    bruke våre tjenester, samtykker du til å overholde disse vilkårene og eventuelle retningslinjer som
                    er oppført her.
                </p>

                <h2 className="text-2xl font-semibold mb-2">2. Bruk av tjenesten</h2>
                <p className="mb-4">
                    Du kan bruke vår plattform for å analysere og oppsummere salgsoppgaver for eiendommer. Du samtykker
                    i å bruke tjenesten på en lovlig måte og ikke misbruke plattformen til ulovlige eller uetiske
                    formål.
                </p>

                <h2 className="text-2xl font-semibold mb-2">3. Kunstig intelligens og feilmarginer</h2>
                <p className="mb-4">
                    AISalgsoppgave benytter kunstig intelligens (AI) til å analysere og oppsummere salgsoppgaver. Selv
                    om vi gjør vårt beste for å sikre nøyaktige resultater, kan AI-modellen noen ganger gjøre feil eller
                    produsere unøyaktige analyser. Du forstår og aksepterer at resultatene fra AI-tjenesten kan
                    inneholde feil, og at det er ditt ansvar å verifisere den informasjonen som blir generert.
                    AISalgsoppgave påtar seg intet ansvar for beslutninger tatt på bakgrunn av AI-analysene.
                </p>

                <h2 className="text-2xl font-semibold mb-2">4. Tjenestetilgengelighet</h2>
                <p className="mb-4">
                    Vi forbeholder oss retten til å endre, suspendere eller avslutte tjenesten midlertidig eller
                    permanent, med eller uten forvarsel, av hvilken som helst grunn, inkludert, men ikke begrenset til,
                    vedlikehold, tekniske problemer eller forretningsmessige beslutninger. Vi kan ikke holdes ansvarlig
                    for eventuelle konsekvenser brukere måtte oppleve som følge av slike endringer.
                </p>

                <h2 className="text-2xl font-semibold mb-2">5. Kontoregistrering</h2>
                <p className="mb-4">
                    For å bruke AISalgsoppgave kreves det at du registrerer en konto. Du forplikter deg til å oppgi
                    korrekte opplysninger ved registrering. Du er ansvarlig for
                    å holde innloggingsinformasjonen din trygg og umiddelbart varsle oss ved uautorisert bruk av kontoen
                    din. Du er ansvarlig for all aktivitet som skjer under din konto.
                </p>

                <h2 className="text-2xl font-semibold mb-2">6. Pris</h2>
                <p className="mb-4">
                    Tjenesten er gratis. Det finnes ingen abonnement, ingen kvoter og ingen betaling.
                </p>

                <h2 className="text-2xl font-semibold mb-2">7. Tredjepartstjenester</h2>
                <p className="mb-4">
                    Tjenesten integrerer tredjepartstjenester som karttjenester fra <Link
                    to={"https://www.openstreetmap.org"}
                    className="text-main underline cursor-pointer">OpenStreetMap</Link> og språkmodeller fra
                    Anthropic og OpenAI, i tillegg til en rekke andre API-er. Ved å bruke AISalgsoppgave samtykker du også å følge tredjepartenes vilkår. Vi
                    fraskriver oss ethvert ansvar for feil eller avbrudd i tredjepartstjenester som er utenfor vår
                    kontroll.
                </p>

                <h2 className="text-2xl font-semibold mb-2">8. Sikkerhet og uautorisert tilgang</h2>
                <p className="mb-4">
                    Du forplikter deg til å ikke forsøke å omgå eller svekke sikkerheten på AISalgsoppgaves nettsted
                    eller servere. Dette inkluderer, men er ikke begrenset til, forsøk på hacking, distribusjon av
                    skadelig programvare, eller forsøk på å få uautorisert tilgang til systemet. Vi forbeholder oss
                    retten til å sperre tilgang til tjenesten og iverksette rettslige tiltak dersom uautorisert tilgang
                    eller forsøk på hacking oppdages.
                </p>

                <h2 className="text-2xl font-semibold mb-2">9. Personvern</h2>
                <p className="mb-4">
                    Vi tar personvern alvorlig. Vennligst se vår <Link to={"/privacy-policy"}
                                                                       className="text-main underline cursor-pointer">Personvernerklæring</Link> for
                    å få mer informasjon om hvordan vi samler inn, bruker og beskytter dine personlige data.
                </p>

                <h2 className="text-2xl font-semibold mb-2">10. Ansvarsfraskrivelse</h2>
                <p className="mb-4">
                    AISalgsoppgave tilbyr sine tjenester "som de er", og vi gir ingen garantier angående nøyaktigheten
                    eller tilgjengeligheten av tjenesten. Vi fraskriver oss ansvar for eventuelle tap som kan oppstå fra
                    bruken av vår plattform.
                </p>

                <h2 className="text-2xl font-semibold mb-2">11. Oppsigelse</h2>
                <p className="mb-4">
                    Du kan når som helst si opp kontoen din ved å kontakte oss på post@aisalgsoppgave.no.
                </p>

                <h2 className="text-2xl font-semibold mb-2">12. Opphavsrett</h2>
                <p className="mb-4">
                    Alt innhold og materiale som er tilgjengelig på AISalgsoppgave, inkludert design, tekst, bilder og
                    kode, er beskyttet av opphavsrett. Du har ikke lov til å kopiere, distribuere, eller bruke noe av
                    innholdet uten skriftlig tillatelse fra oss eller rettighetshaverne.
                </p>

                <h2 className="text-2xl font-semibold mb-2">13. Force Majeure</h2>
                <p className="mb-4">
                    Vi er ikke ansvarlige for manglende oppfyllelse av våre forpliktelser som skyldes omstendigheter
                    utenfor vår rimelige kontroll, inkludert, men ikke begrenset til, naturkatastrofer, krig, pandemier,
                    eller statlige restriksjoner (Force Majeure).
                </p>

                <h2 className="text-2xl font-semibold mb-2">14. Endringer i vilkårene</h2>
                <p className="mb-4">
                    Vi forbeholder oss retten til å oppdatere disse vilkårene når som helst. Endringer vil bli publisert
                    på denne siden, og det er ditt ansvar å holde deg oppdatert på eventuelle endringer.
                </p>

                <h2 className="text-2xl font-semibold mb-2">15. Kontaktinformasjon</h2>
                <p className="mb-4">
                    Hvis du har spørsmål angående disse vilkårene, vennligst kontakt oss på <a
                    href="mailto:kontakt@aisalgsoppgave.no"
                    className="text-main underline cursor-pointer">post@aisalgsoppgave.no</a>.
                </p>

                <p className="text-center mt-6">
                    © 2024 AISalgsoppgave. Alle rettigheter forbeholdes.
                </p>
            </div>
        </>
    );
};

export default TermsOfService;