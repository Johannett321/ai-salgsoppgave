import React, {useState} from "react";
import {FieldValues, FormProvider, useForm} from "react-hook-form";
import {useNavigate} from "react-router";
import axios from "axios";
import {getBackendURL, isDevelopmentEnv} from "../../utils/EnvironmentsManager";
import ForgotPasswordSuccess from "./components/ForgotPasswordSuccess";
import toast from "react-hot-toast";
import PageHeading from "../../components/PageHeading";
import HeadingDescriptor from "../../components/HeadingDescriptor";
import InputTextField from "../../components/InputTextField";
import MainButton from "../../components/MainButton";
import {NavLink} from "react-router-dom";

const ForgotPasswordPage = () => {
    const navigate = useNavigate()
    const methods = useForm()
    const [isSubmitted, setIsSubmitted] = useState(false)
    const [tryDifferentEmail, setTryDifferentEmail] = useState(false);

    const onSubmit = (values: FieldValues) => {
        axios.post(getBackendURL() + "/api/v1/user/forgot-password", {
            "email": values.email
        }).then((response) => {
            setIsSubmitted(true);
        }).catch((error) => {
            toast.error("En feil har oppstått")
        })
    };

    const resetEmailInput = () => {
        setIsSubmitted(false);
        setTryDifferentEmail(!tryDifferentEmail);  // Toggles the state to force a re-render
    };

    if (isSubmitted) {
        return <ForgotPasswordSuccess onTryDifferentEmail={resetEmailInput}/>;
    } else {
        return (
            <>
                <FormProvider {...methods} >
                    <div className="flex min-h-full flex-1 flex-col justify-center py-12 sm:px-6 lg:px-8">
                        <div className="sm:mx-auto sm:w-full sm:max-w-md mb-8">
                            <img
                                className="mx-auto h-10 w-auto"
                                src="/images/logo_dark.webp"
                                alt="AISalgsoppgave"
                            />
                        </div>
                        <div className="mt-10 sm:mx-auto sm:w-full sm:max-w-[480px]">
                            <div className="bg-white px-6 py-12 shadow sm:rounded-lg sm:px-12">
                                <form className="space-y-4" onSubmit={methods.handleSubmit(onSubmit)}>
                                    <PageHeading>
                                        Glemt passord?
                                    </PageHeading>
                                    <HeadingDescriptor>
                                        Skriv inn e-postadressen som er knyttet til kontoen din, så sender vi
                                        deg en lenke for å tilbakestille passordet ditt.
                                    </HeadingDescriptor>
                                    <InputTextField
                                        name={"email"}
                                        type={"email"}
                                        label={"E-post"}
                                        style={{width: "100%"}}
                                        validation={{
                                            required: {value: true, message: 'Email is required'},
                                            maxLength: {value: 50, message: "The Email is too long"}
                                        }}/>
                                    <MainButton className={"w-full mb-5"}>Send lenke</MainButton>

                                    <p className={"text-center"}>
                                        <NavLink to={"/login"} className={"ml-1 font-semibold leading-6 text-main hover:text-indigo-500 cursor-pointer"}>
                                            Tilbake til logg inn
                                        </NavLink>
                                    </p>
                                </form>
                            </div>
                        </div>
                    </div>
                </FormProvider>
            </>
        )
    }
};
export default ForgotPasswordPage;