import {useEffect, useState} from "react";
import {useParams} from "react-router-dom";
import {useSalgsoppgaveJob} from "./SalgsoppgaveJobProvider";
import {JobStatus} from "../enums/JobStatus";
import LoadingPage from "../pages/LoadingPage/LoadingPage";
import MainTitle from "../components/MainTitle";
import HeadingDescriptor from "../components/HeadingDescriptor";
import MainButton from "../components/MainButton";
import {Outlet, useNavigate} from "react-router";
import UploadFileDropZone from "../components/UploadFileDropZone";
import {FieldValues, FormProvider, useForm} from "react-hook-form";
import BackButton from "../components/BackButton";
import axios from "axios";
import {getBackendURL} from "../utils/EnvironmentsManager";
import toast from "react-hot-toast";

const DashboardWrapper = () => {
    const navigate = useNavigate();
    const methods = useForm()

    const {id} = useParams()
    const {userSalgsoppgaveJob, salgsoppgaveJob, loadSalgsoppgaveJob} = useSalgsoppgaveJob()
    const [showLoading, setShowLoading] = useState<boolean>(true)
    const [isUploading, setIsUploading] = useState<boolean>(false)

    const [fileSelected, setFileSelected] = useState<File | null>(null);

    useEffect(() => {
        id && loadSalgsoppgaveJob(id)
    }, [id]);

    useEffect(() => {
        if (
            salgsoppgaveJob?.jobStatus == JobStatus.LLM_IN_PROGRESS ||
            salgsoppgaveJob?.jobStatus == JobStatus.CREATING_EMBEDDINGS ||
            salgsoppgaveJob?.jobStatus == JobStatus.FAILED ||
            salgsoppgaveJob?.jobStatus == JobStatus.COMPLETED
        ) {
            setShowLoading(false)
        }
    }, [salgsoppgaveJob]);

    const onSubmit = (values: FieldValues) => {
        setIsUploading(true)

        // Access the uploaded file
        const uploadedFile = values.salgsoppgavePDF && values.salgsoppgavePDF[0];

        if (uploadedFile) {
            const formData = new FormData();
            formData.append('salgsoppgavePDF', uploadedFile);

            axios.post(getBackendURL() + "/api/v1/job/" + userSalgsoppgaveJob?.id + "/manual-pdf", formData, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            }).then(response => {
                // Handle success
                window.location.reload()
            }).catch(error => {
                // Handle error
                toast.error("En ukjent feil har oppstått")
            }).finally(() => {
                setIsUploading(false)
            })
        } else {
            console.log('Ingen fil er valgt.');
            setIsUploading(false)
        }
    };

    return (
        <>
            {showLoading ? (
                <LoadingPage/>
            ) : (
                <>
                    {salgsoppgaveJob && salgsoppgaveJob.jobStatus !== JobStatus.FAILED && (
                        <div className="flex">
                            <div className="w-full">
                                <Outlet />
                            </div>
                        </div>
                    )}

                    {salgsoppgaveJob && salgsoppgaveJob.jobStatus == JobStatus.FAILED && (
                        <div className={"w-full mt-[50vh] -translate-y-[50%] flex flex-col justify-center items-center"}>
                            <div className={"max-w-[600px] bg-red-50 p-8 shadow-md rounded-md"}>
                                <MainTitle className={"mb-2"}>Oisann, her gikk noe galt</MainTitle>
                                <HeadingDescriptor>
                                    Kunstig intelligens er ikke perfekt.
                                    Noen ganger oppfører den seg litt annerledes, slik som i dette tilfellet.
                                    Her er grunnen til at det gikk galt denne gangen:
                                </HeadingDescriptor>
                                <div className={"bg-red-300 mt-5 p-2 rounded-md opacity-70 font-bold"}>
                                    {salgsoppgaveJob.failedReason}
                                </div>

                                {salgsoppgaveJob.failedReason && salgsoppgaveJob.failedReason.includes("manuelt") && (
                                    <div className={"mx-auto w-96 mt-8 space-y-3"}>
                                        <h1 className={"font-bold text-main text-lg"}>Last opp PDF manuelt</h1>
                                        <FormProvider {...methods}>
                                            <form className={"space-y-3"} onSubmit={methods.handleSubmit(onSubmit)}>
                                                <UploadFileDropZone
                                                    name={"salgsoppgavePDF"}
                                                    accept={{'application/pdf': []}}
                                                    onFileDropped={(file) => setFileSelected(file)}
                                                />
                                                <MainButton
                                                    loading={isUploading}
                                                    disabled={!fileSelected}
                                                >
                                                    Scann PDF
                                                </MainButton>
                                            </form>
                                        </FormProvider>
                                    </div>
                                )}
                            </div>
                            <div className={"mt-5"}>
                                <BackButton />
                            </div>
                        </div>
                    )}
                </>
            )}
        </>
    );
};

export default DashboardWrapper;
