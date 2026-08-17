import React, {useEffect, useState} from "react";
import {FieldValues, FormProvider, useForm} from "react-hook-form";
import {useNavigate} from "react-router";
import {Link} from "react-router-dom";
import axios from "axios";
import {getBackendURL, isDevelopmentEnv} from "../../utils/EnvironmentsManager";
import MainButton from "../../components/MainButton";
import InputTextField from "../../components/InputTextField";
import PasswordInput from "../../components/PasswordInput";
import {useUser} from "../../providers/UserProvider";
import {CheckCircleIcon} from "@heroicons/react/20/solid";
import PageHeading from "../../components/PageHeading";
import {motion} from "framer-motion";


const SignUpPage = () => {
    const {currentUser} = useUser()
    const methods = useForm()
    const navigate = useNavigate()
    const [errorMessage, setErrorMessage] = useState<string | null>(null)

    const [password, setPassword] = useState('');

    const handlePasswordChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setPassword(e.target.value);
    };

    const onSubmit = (values: FieldValues) => {
        const data = {
            "firstName": values.firstName,
            "lastName": values.lastName,
            "email": values.email,
            "password": password
        }

        axios.post(getBackendURL() + "/api/v1/user/register",
            data
        ).then((response) => {
            window.location.href = "/"
        }).catch((error) => {
            setErrorMessage(error.response.data.error)
        });
    };

    useEffect(() => {
        if (currentUser) {
            navigate("/")
        }
    }, [currentUser]);

    return (
        <>
            <FormProvider {...methods}>
                <div className="min-h-screen flex flex-col lg:flex-row lg:justify-between">
                    {/* Left Section - Bullet Points */}
                    <div className="hidden lg:flex lg:flex-col lg:w-1/2 lg:justify-center mb-20">
                        <div className="m-auto px-10">
                            <motion.div
                                initial={{opacity: 0}}
                                animate={{opacity: 1}}
                                transition={{duration: 1, ease: "easeOut", delay: 0.3}}
                                className={"space-y-2"}>
                                <div className={"flex items-center gap-5"}>
                                    <img
                                        className="mx-auto h-10 w-auto"
                                        src="/images/logo_dark.webp"
                                        alt="AISalgsoppgave"
                                    />
                                </div>
                                <h1 className="text-xl font-bold">Få <span
                                    className={"text-main"}>kunstig intelligens</span> til å lese salgsoppgaven for deg.
                                </h1>
                            </motion.div>
                            <ul className="mt-4 space-y-4 text-left">
                                <motion.li
                                    initial={{opacity: 0, scale: 0.9}}
                                    animate={{opacity: 1, scale: 1}}
                                    transition={{duration: 1, ease: "easeOut", delay: 0.3}}
                                    className="flex items-start">
                                    <CheckCircleIcon className="text-main-gradient-first h-6 w-8 shrink-0"/>
                                    <div className="ml-2">
                                        <strong>Oppdag mangler tidlig</strong>
                                        <p>Se alle viktige detaljer enkelt oppsummert i et dashboard.</p>
                                    </div>
                                </motion.li>
                                <motion.li
                                    initial={{opacity: 0, scale: 0.9}}
                                    animate={{opacity: 1, scale: 1}}
                                    transition={{duration: 1, ease: "easeOut", delay: 0.4}}
                                    className="flex items-start">
                                    <CheckCircleIcon className="text-main-gradient-first h-6 w-8 shrink-0"/>
                                    <div className="ml-2">
                                        <strong>Spar tid</strong>
                                        <p>La AI lese salgsoppgaven for deg på få minutter.</p>
                                    </div>
                                </motion.li>
                                <motion.li
                                    initial={{opacity: 0, scale: 0.9}}
                                    animate={{opacity: 1, scale: 1}}
                                    transition={{duration: 1, ease: "easeOut", delay: 0.5}}
                                    className="flex items-start">
                                    <CheckCircleIcon className="text-main-gradient-first h-6 w-8 shrink-0"/>
                                    <div className="ml-2">
                                        <strong>Sammenlign flere boliger</strong>
                                        <p>Lagre og sammenlign boliger for å enklere finne drømmeboligen.</p>
                                    </div>
                                </motion.li>
                            </ul>
                        </div>
                    </div>

                    {/* Right Section - Form */}
                    <motion.div
                        initial={{opacity: 0, scale: 0.9}}
                        animate={{opacity: 1, scale: 1}}
                        transition={{duration: 1, ease: "easeOut"}}
                        className="lg:w-1/2 flex flex-col px-4 justify-center">
                        <div className="my-5 sm:max-w-[480px] sm:mx-auto sm:w-full lg:my-0 lg:py-12">
                            <div className="bg-white px-6 py-8 shadow-lg rounded-lg sm:px-12 border border-gray-200">
                                <img
                                    className="mx-auto h-10 w-auto"
                                    src="/images/logo_dark.webp"
                                    alt="AISalgsoppgave"
                                />
                                <div className={"mt-2"}></div>
                                <PageHeading>
                                    Prøv AISalgsoppgave gratis i 10 dager!
                                </PageHeading>

                                {errorMessage &&
                                    <div className={"font-bold text-red-500 text-center"}>{errorMessage}</div>}
                                <form className="mt-5 space-y-4" onSubmit={methods.handleSubmit(onSubmit)}>
                                    <InputTextField
                                        name={"firstName"}
                                        type={"firstName"}
                                        label={"Fornavn"}
                                        style={{width: "100%"}}
                                        validation={{
                                            required: {value: true, message: 'First name is required'},
                                            maxLength: {value: 50, message: "The Name is too long"},
                                        }}
                                    />
                                    <InputTextField
                                        name={"lastName"}
                                        type={"lastName"}
                                        label={"Etternavn"}
                                        style={{width: "100%"}}
                                        validation={{
                                            required: {value: true, message: 'the Last name is required'},
                                            maxLength: {value: 50, message: "The Name is too long"},
                                        }}
                                    />
                                    <InputTextField
                                        name={"email"}
                                        type={"email"}
                                        label={"E-post"}
                                        style={{width: "100%"}}
                                        validation={{
                                            required: {value: true, message: 'Email is required'},
                                            maxLength: {value: 50, message: "The Email is too long"},
                                        }}
                                    />
                                    <PasswordInput
                                        name={"password"}
                                        label="Passord"
                                        value={password}
                                        style={{width: "100%"}}
                                        onValueChange={handlePasswordChange}
                                    />

                                    <div className="flex items-center justify-between">
                                        <input
                                            type={"checkbox"}
                                            name={"terms"}
                                        >
                                        </input>
                                        <div className={"text-left ml-4"}>
                                            Jeg har lest og godtatt{" "}
                                            <Link to="/terms-of-service" target="_blank" rel="noopener noreferrer"
                                                  className={"underline text-main"}>
                                                kjøpsvilkårene
                                            </Link>{" "}
                                            og{" "}
                                            <Link to="/privacy-policy" target="_blank" rel="noopener noreferrer"
                                                  className={"underline text-main"}>
                                                personvernerklæringen
                                            </Link>.
                                        </div>
                                    </div>
                                    <MainButton className={"w-full"}>Registrer konto nå</MainButton>
                                </form>

                                <div>
                                    {/*<div className="relative mt-6">
                                        <DividerWithText>eller registrer deg med</DividerWithText>
                                    </div>
                                    <div className="mt-6 grid grid-cols-2 gap-4">
                                        <GoogleButton />
                                        <FacebookButton />
                                    </div>*/}
                                    <div className={"mt-10 text-center text-md"}>
                                        <p className={"text-gray-800"}>
                                            Har du allerede en konto?&nbsp;
                                            <Link to={"/login"} className={"underline text-main"}>
                                                Logg inn
                                            </Link>
                                        </p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </motion.div>
                </div>
            </FormProvider>
        </>
    )
}
export default SignUpPage;