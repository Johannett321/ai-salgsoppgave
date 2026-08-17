import {useParams} from "react-router-dom";
import MainButton from "../../components/MainButton";
import {FieldValues, FormProvider, useForm} from "react-hook-form";
import HeadingDescriptor from "../../components/HeadingDescriptor";
import {useState} from "react";
import axios from "axios";
import {getBackendURL} from "../../utils/EnvironmentsManager";
import toast from "react-hot-toast";
import {ArrowLeftIcon} from "@heroicons/react/24/solid";
import {ErrorMessage} from "@hookform/error-message";

const MarketingDownload = () => {
    const {formState: {errors, ...formState}, ...methods} = useForm()
    const {id} = useParams()
    const [loading, setLoading] = useState<boolean>()
    const [success, setSuccess] = useState<boolean>(false)

    const submitForm = (values:FieldValues) => {
        setLoading(true)
        axios.post(getBackendURL() + "/api/v1/marketing/download", {
            "fileName": id,
            "firstName": values["firstName"],
            "email": values["email"]
        }).then(result => {
            setSuccess(true)
        }).catch(error => {
            toast.error("En ukjent feil har oppstått");
        }).finally(() => {
            setLoading(false)
        })
    }

    return (
        <div className={"w-screen h-screen flex flex-col justify-center items-center space-y-10"}>
            <img src={"/images/logo_dark.webp"} className={"h-16"} />
            <div className={"p-10 bg-white max-w-[500px] rounded-lg shadow-md"}>
                {success ? (
                    <>
                        <h1 className={"text-main text-2xl font-bold mb-3"}>Eposten er sendt!</h1>
                        <HeadingDescriptor>
                            Sjekk innboksen din for å laste ned førstegangskjøp håndbok
                        </HeadingDescriptor>
                        <div>
                            <a href={"/"} className={"flex flex-row items-center justify-center mt-4 text-main gap-x-4 hover:text-main-darker"}>
                                <ArrowLeftIcon className={"w-10"} />
                                Gå til aisalgsoppgave.no
                            </a>

                        </div>
                    </>
                ) : (
                    <>
                        <h1 className={"text-main text-2xl font-bold mb-3"}>Last ned {id}</h1>
                        <HeadingDescriptor>Skriv din epost adresse i feltet under så vi kan sende deg
                            filen</HeadingDescriptor>
                        <FormProvider formState={{errors, ...formState}} {...methods}>
                            <form onSubmit={methods.handleSubmit(submitForm)} className={"flex flex-col mt-5 space-y-5"}>
                                <input
                                    type={"name"}
                                    placeholder={"Fornavn"}
                                    className={"text-center rounded-full border-main border-[1px] p-2 focus:outline-main-darker"}
                                    {...methods.register("firstName", {
                                        required: {value: true, message: 'Fornavn er obligatorisk'},
                                        minLength: {value: 2, message: 'Fornavn er obligatorisk'},
                                        maxLength: {value: 50, message: 'Fornavn er for langt'}
                                    })}
                                />
                                <ErrorMessage
                                    errors={errors}
                                    name="firstName"
                                    as={"p"}
                                    render={({ message }) => <p className={"text-red-500 font-bold"}>{message}</p>}
                                />
                                <input
                                    type={"email"}
                                    placeholder={"Din epostadresse"}
                                    className={"text-center rounded-full border-main border-[1px] p-2 focus:outline-main-darker"}
                                    {...methods.register("email", {
                                        required: {value: true, message: 'Epost er obligatorisk'},
                                        minLength: {value: 2, message: 'Eposten er ikke gyldig'},
                                        maxLength: {value: 200, message: 'Eposten er for lang'}
                                    })}
                                />
                                <ErrorMessage
                                    errors={errors}
                                    name="email"
                                    as={"p"}
                                    render={({ message }) => <p className={"text-red-500 font-bold"}>{message}</p>}
                                />
                                <div>
                                    <input
                                        id={"acceptMail"}
                                        type={"checkbox"}
                                        {...methods.register("acceptMail", {
                                            required: {value: true, message: "Feltet er obligatorisk"}
                                        })}
                                    />
                                    &nbsp;
                                    <label htmlFor={"accept-mail"}>Jeg aksepterer å få e-poster fra AISalgsoppgave</label>
                                    <ErrorMessage
                                        errors={errors}
                                        name="acceptMail"
                                        as={"p"}
                                        render={({ message }) => <p className={"text-red-500 font-bold"}>{message}</p>}
                                    />
                                </div>
                                <MainButton loading={loading}>
                                    Last ned
                                </MainButton>
                            </form>
                        </FormProvider>
                    </>
                )}
            </div>
        </div>
    )
}

export default MarketingDownload