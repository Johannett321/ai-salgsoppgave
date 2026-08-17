import {useEffect, useState} from "react";
import axios from "axios";
import {getBackendURL} from "../../utils/EnvironmentsManager";
import {SalgsoppgaveJob} from "../../types/SalgsoppgaveJob";
import {Spinner} from "../../components/Spinner";
import {formatAsNOK} from "../../utils/FormatUtils";
import {useNavigate} from "react-router";
import EmptyState from "../../components/EmptyState";
import HeadingDescriptor from "../../components/HeadingDescriptor";
import StatusTag from "../../components/StatusTag";
import {useUser} from "../../providers/UserProvider";
import RealEstateGridEmptyState from "../../components/RealEstateGridEmptyState";
import {UserSalgsoppgaveJob} from "../../types/UserSalgsoppgaveJob";

const HistoryPage = () => {
    const navigate = useNavigate();
    const {currentUser} = useUser()

    const [history, setHistory] = useState<UserSalgsoppgaveJob[] | undefined>(undefined);


    useEffect(() => {
        axios.get(getBackendURL() + "/api/v1/job/history").then(result => {
            setHistory(result.data)
        }).catch(error => {
            console.log(error)
        })
    }, []);

    return (
        <div className={"container mx-auto"}>
            <h1 className={"font-bold text-3xl mt-10 text-main text-left"}>Historikk</h1>
            <HeadingDescriptor className={"text-left"}>Dette er alle salgsoppgavene du har scannet</HeadingDescriptor>
            {currentUser ? (
                <>
                    {history ? (
                        <>
                            {history.length == 0 ? (
                                <div className={"flex flex-col justify-center items-center w-full h-screen"}>
                                    <EmptyState
                                        description={"Dette er sannsynligvis fordi du ikke har søkt opp " +
                                            "noen boliger mens du har hatt en konto."}/>
                                </div>
                            ) : (
                                <>
                                    <div
                                        className={"grid gap-4 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 2xl:grid-cols-5 mt-5 mb-10"}>
                                        {history.map(h => (
                                            <div
                                                key={h.id}
                                                onClick={() => navigate("/view/" + h.id)}
                                                className={"card bg-white p-4 rounded-xl space-y-2 shadow-md cursor-pointer hover:bg-gray-200 transition-all"}
                                            >
                                                <div className={"flex justify-center"}>
                                                    <StatusTag status={h.salgsoppgaveJob.jobStatus}/>
                                                </div>
                                                <h2 className={"font-bold"}>{h.salgsoppgaveJob.salgsoppgave?.gateNavn} {h.salgsoppgaveJob.salgsoppgave?.gateNummer}</h2>
                                                <h3>{h.salgsoppgaveJob.salgsoppgave?.postNummer} {h.salgsoppgaveJob.salgsoppgave?.postSted}</h3>
                                                <h4 className={"text-2xl"}>{formatAsNOK(h.salgsoppgaveJob.salgsoppgave?.totalPris)}</h4>
                                            </div>
                                        ))}
                                    </div>
                                </>
                            )}
                        </>
                    ) : (
                        <div className={"flex mt-20 flex-col justify-center items-center"}>
                            <Spinner/>
                        </div>
                    )}
                </>
            ) : (
                <>
                    <RealEstateGridEmptyState>
                        <div
                            className={"grid md:gap-2 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 2xl:grid-cols-5 mt-5 mb-10"}>
                            <img src={"/templates/history-element-green.webp"} alt={"Example history element"}/>
                            <img src={"/templates/history-element-orange.webp"} alt={"Example history element"}/>
                            <img src={"/templates/history-element-green.webp"} alt={"Example history element"}/>
                            <img src={"/templates/history-element-green.webp"} alt={"Example history element"}/>
                            <img src={"/templates/history-element-orange.webp"} className={"hidden md:block"} alt={"Example history element"}/>

                            <img src={"/templates/history-element-green.webp"} className={"hidden md:block"} alt={"Example history element"}/>
                            <img src={"/templates/history-element-green.webp"} className={"hidden md:block"} alt={"Example history element"}/>
                            <img src={"/templates/history-element-orange.webp"} className={"hidden md:block"} alt={"Example history element"}/>
                            <img src={"/templates/history-element-green.webp"} className={"hidden md:block"} alt={"Example history element"}/>
                            <img src={"/templates/history-element-green.webp"} className={"hidden md:block"} alt={"Example history element"}/>

                            <img src={"/templates/history-element-green.webp"} className={"hidden lg:block"} alt={"Example history element"}/>
                            <img src={"/templates/history-element-orange.webp"} className={"hidden lg:block"} alt={"Example history element"}/>
                            <img src={"/templates/history-element-green.webp"} className={"hidden lg:block"} alt={"Example history element"}/>
                            <img src={"/templates/history-element-orange.webp"} className={"hidden lg:block"} alt={"Example history element"}/>
                            <img src={"/templates/history-element-green.webp"} className={"hidden lg:block"} alt={"Example history element"}/>

                            <img src={"/templates/history-element-green.webp"} className={"hidden lg:block"} alt={"Example history element"}/>
                            <img src={"/templates/history-element-green.webp"} className={"hidden lg:block"} alt={"Example history element"}/>
                            <img src={"/templates/history-element-orange.webp"} className={"hidden xl:block"} alt={"Example history element"}/>
                            <img src={"/templates/history-element-green.webp"} className={"hidden xl:block"} alt={"Example history element"}/>
                            <img src={"/templates/history-element-green.webp"} className={"hidden xl:block"} alt={"Example history element"}/>

                            <img src={"/templates/history-element-green.webp"} className={"hidden xl:block"} alt={"Example history element"}/>
                            <img src={"/templates/history-element-orange.webp"} className={"hidden xl:block"} alt={"Example history element"}/>
                        </div>
                    </RealEstateGridEmptyState>
                </>
            )}
        </div>
    )
}

export default HistoryPage;