import {useEffect, useRef, useState} from "react";
import {AnimatePresence, motion} from "framer-motion";
import {FieldValues, FormProvider, useForm} from "react-hook-form";
import axios from "axios";
import {getBackendURL} from "../../../utils/EnvironmentsManager";
import {useSalgsoppgaveJob} from "../../../providers/SalgsoppgaveJobProvider";
import toast from "react-hot-toast";
import {ChatConversation} from "../../../types/ChatConversation";
import {ChatMessage} from "../../../types/ChatMessage";
import {MinusIcon} from "@heroicons/react/16/solid";
import {JobStatus} from "../../../enums/JobStatus";
import {Spinner} from "../../../components/Spinner";
import {PaperAirplaneIcon} from "@heroicons/react/24/solid";

const FloatingChatComponent = () => {
    const {salgsoppgaveJob, userSalgsoppgaveJob} = useSalgsoppgaveJob()
    const {...methods} = useForm()

    const [chatReady, setChatReady] = useState<boolean>(false)

    const [conversation, setConversation] = useState<ChatConversation | undefined>()

    const [expanded, setExpanded] = useState<boolean>(false);

    const [waitingForAnswer, setWaitingForAnswer] = useState<boolean>(false)

    const messageContainerRef = useRef<HTMLDivElement>(null)
    const [isUserScrolledUp, setIsUserScrolledUp] = useState(false);

    const handleSubmit = (values: FieldValues) => {
        if (waitingForAnswer) {
            return
        }

        let messages: ChatMessage[] | undefined = conversation?.messages

        messages?.push({
            id: "unknown",
            role: "USER",
            message: values["question"],
            timestamp: new Date(Date.now())
        })

        let newConversation: ChatConversation = {...conversation!, messages: messages!}

        setConversation(newConversation)
        setWaitingForAnswer(true)

        axios.post(getBackendURL() + "/api/v1/chat/job/" + salgsoppgaveJob?.id + "/question", {
            "question": values["question"]
        }).then(result => {
            let conversation: ChatConversation = result.data

            setConversation(injectWelcomeMessage(conversation))
        }).catch(error => {
            if (error.response.status === 429) {
                toast.error("Vennligst vent litt før du stiller flere spørsmål")
                return
            }
            toast.error("En feil har oppstått")
        }).finally(() => {
            setWaitingForAnswer(false)
        })

        methods.reset({question: ""})
    }

    useEffect(() => {
        if (salgsoppgaveJob?.jobStatus === JobStatus.LLM_IN_PROGRESS || salgsoppgaveJob?.jobStatus === JobStatus.COMPLETED) {
            setChatReady(true)
        }
    }, [salgsoppgaveJob]);

    useEffect(() => {
        console.log(userSalgsoppgaveJob)
        setConversation(injectWelcomeMessage(userSalgsoppgaveJob?.chatConversation))
    }, [userSalgsoppgaveJob]);

    useEffect(() => {
        if (messageContainerRef.current && !isUserScrolledUp) {
            messageContainerRef.current.scrollTop = messageContainerRef.current.scrollHeight;
        }
    }, [conversation,conversation?.messages, isUserScrolledUp]);

    const handleScroll = () => {
        if (messageContainerRef.current) {
            const {scrollTop, scrollHeight, clientHeight} = messageContainerRef.current;
            setIsUserScrolledUp(scrollHeight - scrollTop > clientHeight + 20);  // threshold for "near bottom"
        }
    };

    const openChat = () => {
        setExpanded(true)
        setTimeout(() => {
            if (messageContainerRef.current) {
                messageContainerRef.current.scrollTop = messageContainerRef.current.scrollHeight;
            }
        }, 40)
    }

    function formatNorwegianDateTime(dateInput: Date | string): string {
        const date = new Date(dateInput);

        // If the date is invalid, return an empty string
        if (isNaN(date.getTime())) return '';

        // Check if the date is from today
        const isToday = new Date().toDateString() === date.toDateString();

        // Define formatting options based on whether it’s today or not
        const options: Intl.DateTimeFormatOptions = isToday
            ? {hour: '2-digit', minute: '2-digit'}
            : {day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit'};

        // Format and return the date using Norwegian locale
        return new Intl.DateTimeFormat('no-NO', options).format(date);
    }

    const injectWelcomeMessage = (conversation:ChatConversation | undefined) => {

        if (!conversation) {
            console.log("UNDEFINED")
        }else {
            console.log(conversation)
        }

        const initialMessage = "Hei! 👋 Jeg er her for å hjelpe deg med spørsmål om salgsoppgaven du ser på. Enten det gjelder kostnader, beliggenhet, planløsning eller andre detaljer, er det bare å spørre! 🏠💬"

        if (!conversation) {
            conversation = {
                id: "unknown",
                messages: [
                    {
                        id: "welcome",
                        role: "ASSISTANT",
                        message: initialMessage,
                        timestamp: new Date(Date.now())
                    }
                ]
            }
        }else {
            if (!conversation?.messages?.some(m => m.message.includes("Jeg er her for å"))) {
                conversation?.messages?.unshift({
                    id: "welcome",
                    role: "ASSISTANT",
                    message: initialMessage,
                    timestamp: new Date(Date.now())
                })
            }
        }

        return conversation
    }

    return (
        <div className={"fixed z-30  md:h-auto md:w-auto md:top-auto md:left-auto bottom-5 right-5 " + (expanded ? "top-0 left-0  h-screen w-screen" : "")}>
            <AnimatePresence>
                {!expanded && (
                    <motion.button
                        key={"chatbutton"}
                        initial={{opacity: 0}}
                        animate={{opacity: 1}}
                        exit={{opacity: 0}}
                        transition={{duration: 0.3}}
                        type={"button"}
                        className={"absolute w-64 right-5 bottom-5 py-2 bg-main cursor-pointer text-white rounded-full text-center transition-all md:right-0 md:bottom-0 hover:bg-main-darker"}
                        onClick={openChat}
                    >
                        <div className={"flex gap-x-1 justify-center"}>
                            <img src={"/images/icon_white.png"} className={"w-5"}/>
                            Chat med salgsoppgaven
                        </div>

                    </motion.button>
                )}

                {expanded && (
                    <motion.div
                        key={"chatbox"}
                        initial={{originY: 1.5, scaleY: 0, opacity: 0}}
                        animate={{originY: 1, scaleY: 1, opacity: 1}}
                        exit={{originY: 1.5, scaleY: 0, opacity: 0}}
                        transition={{duration: 0.3, ease: "easeOut"}}
                        className={"flex flex-col h-screen w-full bg-white rounded-xl md:h-auto md:w-[500px]"}
                    >
                        <div className={"px-3 py-1 w-full bg-main rounded-t-xl shadow-lg"}>
                            <h1 className={"text-white py-4 px-5 text-lg font-bold"}>Still spørsmål om
                                salgsoppgaven</h1>
                            <div
                                className={"absolute right-5 top-5 text-white cursor-pointer hover:text-gray-200"}
                                onClick={() => setExpanded(false)}
                            >
                                <MinusIcon className={"w-8 scale-y-125"}/>
                            </div>
                        </div>
                        {chatReady ? (
                            <>
                                <div
                                    className={"p-5 flex-grow w-full h-full space-y-5 overflow-y-scroll md:flex-none md:h-[600px] no-scrollbar"}
                                    ref={messageContainerRef}
                                    onScroll={handleScroll}
                                >
                                    {conversation && conversation.messages && conversation?.messages?.map((message, index) => (
                                        <div key={message.id}>
                                            <div
                                                className={"text-gray-500 mb-1 " + (message.role === "ASSISTANT" ? "" : "text-right")}>
                                                {message.role === "ASSISTANT" ? "AiSalgsoppgave" : "Deg"}
                                            </div>
                                            <div key={index}
                                                 className={"py-2 px-4 rounded-xl " + (message.role === "ASSISTANT" ? "bg-gray-200 mr-20" : "bg-green-300 ml-20")}>
                                                {message.message}
                                            </div>

                                            {index !== 0 && (
                                                <div
                                                    className={"text-gray-500 mt-1 " + (message.role === "ASSISTANT" ? "" : "text-right")}>
                                                    {formatNorwegianDateTime(message.timestamp)}
                                                </div>
                                            )}
                                        </div>
                                    ))}

                                    {waitingForAnswer && (
                                        <div className={"text-gray-500"}>
                                            AISalgsoppgave skriver...
                                        </div>
                                    )}
                                </div>
                                <FormProvider {...methods}>
                                    <form className={"relative flex flex-row items-center"} onSubmit={methods.handleSubmit(handleSubmit)}>
                                        <input
                                            type={"text"}
                                            className={"w-full bg-green-50 py-3 px-4 border-[1px] " + (methods.formState.errors.question ? "border-red-500 focus:outline-none focus:ring-0" : "border-main")}
                                            placeholder={"Er det noen fukt i boligen?"}
                                            maxLength={200}
                                            {...methods.register("question", {required: {value: true, message: "Du må skrive en melding"}, maxLength: {value: 200, message: "Meldingen er for lang"}})}
                                        />
                                        <button
                                            type={"submit"}
                                            className={"px-2 py-3 h-full text-white border-main border-[1px] " + (waitingForAnswer ? "bg-gray-500 cursor-not-allowed" : "bg-main hover:bg-main-darker")}
                                            disabled={waitingForAnswer}
                                        >
                                            <PaperAirplaneIcon className={"w-6 -rotate-90"} />
                                        </button>
                                    </form>
                                </FormProvider>
                            </>
                        ) : (
                            <>
                                <div
                                    className={"p-5 flex flex-col justify-center items-center flex-grow h-full space-y-3 overflow-y-scroll md:flex-none md:h-[600px]"}
                                    ref={messageContainerRef}
                                    onScroll={handleScroll}
                                >
                                    <h1 className={"text-main text-2xl font-semibold"}>Straks klar!</h1>
                                    <p className={"text-gray-500 text-center md:w-64"}>
                                        Må bare lese litt til før jeg klarer å svare på spørsmål
                                    </p>
                                    <Spinner className={"text-main opacity-50"} />
                                </div>
                            </>
                        )}
                    </motion.div>
                )}
            </AnimatePresence>
        </div>
    )
}

export default FloatingChatComponent