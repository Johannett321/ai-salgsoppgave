import {QuestionMarkCircleIcon} from "@heroicons/react/24/solid";
import {Tooltip} from "@mui/material";
import TooltipText from "./TooltipText";

const AIWarning = () => {
    return (
        <div className={"text-center"}>
            <TooltipText className={"text-gray-500"}
                         tooltip={"AISalgsoppgave benytter kunstig intelligens for oppsummering av salgsoppgaver. Selvom vi jobber for å være så nøyaktig som mulig, kan allikevel feil forekomme."}>
                <div className={"inline-flex no-underline"}>
                    Feil kan forekomme. Verifiser detaljer <QuestionMarkCircleIcon className={"ml-1 w-4"}/>
                </div>
            </TooltipText>
        </div>
    )
}

export default AIWarning