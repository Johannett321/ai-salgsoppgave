import React, {useEffect, useState} from "react";
import {FormProvider, useForm} from "react-hook-form";
import {getBackendURL} from "../../utils/EnvironmentsManager";
import {NavLink, useSearchParams} from "react-router-dom";
import {useNavigate} from "react-router";
import InputTextField from "../../components/InputTextField";
import PasswordInput from "../../components/PasswordInput";
import MainButton from "../../components/MainButton";
import {useUser} from "../../providers/UserProvider";
import {motion} from "framer-motion";


const SignInPage = () => {
    const methods = useForm()
    const {currentUser} = useUser()
    const [password, setPassword] = useState('');
    const [queryParameters] = useSearchParams()
    const navigate = useNavigate()

    const handlePasswordChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setPassword(e.target.value);
    };

    useEffect(() => {
        if (currentUser) {
            navigate("/")
        }
    }, [currentUser]);

    return (
        <>
            <FormProvider {...methods} >
                <motion.div
                    initial={{opacity: 0, scale: 0.9}}
                    animate={{opacity: 1, scale: 1}}
                    transition={{duration: 1, ease: "easeOut"}}
                    className="flex min-h-screen flex-1 flex-col justify-center py-12 sm:px-6 lg:px-8">
                    <div className="sm:mx-auto sm:w-full sm:max-w-md">
                        <img
                            className="mx-auto h-16 w-auto"
                            src="/images/logo_dark.webp"
                            alt="AISalgsoppgave"
                        />
                    </div>

                    <div className="mt-10 mx-4 sm:max-w-[480px] sm:mx-auto sm:w-full">
                        <div className="bg-white px-6 py-12 shadow-lg sm:rounded-lg sm:px-12">
                            <h1 className={"text-main text-3xl mb-10 font-bold"}>
                                Logg inn
                            </h1>

                            {queryParameters.get("error") && <div className={"font-bold text-red-500 text-center"}>{queryParameters.get("error")}</div>}
                            {queryParameters.get("success") && <div className={"font-bold text-green-500 text-center"}>{queryParameters.get("success")}</div>}
                            <form className="space-y-4" action={getBackendURL() + "/login"} method={"post"}>
                                <InputTextField
                                    name={"email"}
                                    type={"email"}
                                    label={"E-post"}
                                    style={{width: "100%"}}
                                    validation={{
                                        required: {value: true, message: "Epost er obligatorisk"},
                                        maxLength: {value: 50, message: "Eposten er for lang"}
                                    }}/>

                                <PasswordInput
                                    name={"password"}
                                    label="Passord"
                                    value={password}
                                    style={{width: "100%"}}
                                    onValueChange={handlePasswordChange}
                                />

                                <div className="text-center">
                                    <NavLink to={"/forgot"} className={"ml-1 font-semibold leading-6 text-main hover:opacity-75 cursor-pointer"}>
                                        Har du glemt passordet?
                                    </NavLink>
                                </div>
                                <MainButton className={"w-full"}>Logg inn</MainButton>
                            </form>
                            {/*<div>
                                <div className="relative mt-5">
                                    <DividerWithText>eller fortsett med</DividerWithText>
                                </div>

                                <div className="mt-5 grid grid-cols-1 gap-4">  TODO: ENDRE TIL GRID-COLS-2 NÅR FB LOGIN ER PÅ PLASS
                                    <GooglePlaceholderButton/>
                                    <FacebookPlaceholderButton/>
                                </div>
                            </div>*/}
                            <p className={" text-sm text-gray-400 text-center mt-5"}>
                                Har du ikke konto?
                                <NavLink to={"/register"} className={"ml-1 font-semibold leading-6 text-main hover:opacity-75 cursor-pointer"}>
                                    Få en 10 dagers gratis prøveperiode
                                </NavLink>
                            </p>
                        </div>
                    </div>
                </motion.div>
            </FormProvider>
        </>
    )
}
export default SignInPage;