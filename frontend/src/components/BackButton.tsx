import {useNavigate} from "react-router";
import {ArrowLeftIcon} from "@heroicons/react/24/solid";

const BackButton = () => {
    const navigate = useNavigate()
    return (
        <button
            className={"flex flex-row items-center gap-x-2 hover:text-main hover:font-medium transition-all"}
            onClick={() => navigate(-1)}
        >
            <ArrowLeftIcon className={"w-5"} />
            Tilbake
        </button>
    )
}

export default BackButton