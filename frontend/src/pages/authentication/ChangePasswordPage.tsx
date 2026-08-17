import {FieldValues, FormProvider, useForm} from "react-hook-form";
import React, {useState} from "react";
import {useSearchParams} from "react-router-dom";
import axios from "axios";
import {getBackendURL} from "../../utils/EnvironmentsManager";
import {useNavigate} from "react-router";
import PageHeading from "../../components/PageHeading";
import HeadingDescriptor from "../../components/HeadingDescriptor";
import PasswordInput from "../../components/PasswordInput";
import MainButton from "../../components/MainButton";
import toast from "react-hot-toast";

const ChangePasswordPage = () => {
    const methods = useForm()
    const [password, setPassword] = useState('');
    const navigate = useNavigate()
    const [queryParameters] = useSearchParams()

    const handlePasswordChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setPassword(e.target.value);
    };

    const onSubmit = (values: FieldValues) => {
        axios.put(getBackendURL() + "/api/v1/user/reset-password", {
            "token": queryParameters.get("token"),
            "password": values.password
        }).then((response) => {
            navigate("/login?success=Password changed successfully")
        }).catch((error) => {
            toast.error("En feil har oppstått")
        });
    };

    return (
        <>
            <FormProvider {...methods} >
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
                            <form className="space-y-4" onSubmit={methods.handleSubmit(onSubmit)}>
                                <PageHeading>
                                    Nullstill ditt passord
                                </PageHeading>
                                <HeadingDescriptor>Skriv inn et nytt passord under for å endre passordet ditt.</HeadingDescriptor>
                                <PasswordInput
                                    style={{width: "100%"}}
                                    name={"password"}
                                    label="Nytt passord"
                                    value={password}
                                    onValueChange={handlePasswordChange}
                                />
                                <div>
                                    <MainButton className={"w-full"}>Endre passord</MainButton>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </FormProvider>
        </>
    )
}
export default ChangePasswordPage;