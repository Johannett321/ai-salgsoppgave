import {JobStatus} from "../../../enums/JobStatus";
import {motion} from "framer-motion";
import {useSalgsoppgaveJob} from "../../../providers/SalgsoppgaveJobProvider";

const AIReadingTag = () => {
    const {salgsoppgaveJob} = useSalgsoppgaveJob()

    return (
        <>
            {(salgsoppgaveJob === undefined || salgsoppgaveJob.jobStatus !== JobStatus.COMPLETED) && (
                <motion.div
                    animate={{opacity: [1, 0.5, 1]}}
                    transition={{repeat: Infinity, duration: 2}}
                    className={"mt-5 right-16 top-16 bg-main text-white px-4 py-2 rounded-xl md:mt-0 md:absolute"}>
                    AI leser fortsatt salgsoppgaven...
                </motion.div>
            )}
        </>
    )
}

export default AIReadingTag;