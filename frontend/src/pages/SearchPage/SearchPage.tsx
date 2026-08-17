import { useNavigate } from "react-router";
import MainButton from "../../components/MainButton";
import MainTitle from "../../components/MainTitle";
import HeadingDescriptor from "../../components/HeadingDescriptor";
import axios from "axios";
import { FieldValues, useForm } from "react-hook-form";
import {SalgsoppgaveJob} from "../../types/SalgsoppgaveJob";
import toast from "react-hot-toast";
import {useUser} from "../../providers/UserProvider";
import {useSalgsoppgaveJob} from "../../providers/SalgsoppgaveJobProvider";
import {getBackendURL} from "../../utils/EnvironmentsManager";
import {UserSalgsoppgaveJob} from "../../types/UserSalgsoppgaveJob";

const SearchPage = () => {
    const {currentUser} = useUser();
    const {loadSalgsoppgaveJob} = useSalgsoppgaveJob()

    const { register, handleSubmit, watch, formState: { errors } } = useForm();

    const navigate = useNavigate()

    const onSubmit = (values:FieldValues) => {
        if (!currentUser) {
            navigate("/register");
            return;
        }

        axios.post(getBackendURL() + "/api/v1/job/from-finn-url", {
            "finnUrl": values.finnUrl
        }).then((response) => {
            let userSalgsoppgaveJob:UserSalgsoppgaveJob = response.data;
            loadSalgsoppgaveJob(userSalgsoppgaveJob.id)

            navigate("/view/" + userSalgsoppgaveJob.id)
        }).catch(error => {
            if (error.response.status === 302 || error.response.status === 404) {
                navigate("/register")
                return;
            }else if (error.response.status === 400) {
                toast.error("Kunne ikke behandle lenken. Er det en linke til finn.no?")
                return;
            }
            toast.error("En feil har oppstått!")
        })
    };

    return (
        <>
            <div className={"w-full mt-[50vh] -translate-y-[50%] flex flex-col justify-center items-center"}>
                <div className="mx-auto flex flex-col justify-center items-center md:w-[500px]">
                    <MainTitle>
                        Scann en salgsoppgave
                    </MainTitle>
                    <HeadingDescriptor>
                        Lim inn linken til Finn annonsen nedenfor for å oppsummere
                        salgsoppgaven
                    </HeadingDescriptor>
                </div>
                <form onSubmit={handleSubmit(onSubmit)} className="flex flex-col mx-auto w-full md:w-[600px]">
                    <input
                        className="mt-4 py-3 px-4 rounded-3xl border-[#305345] border-4"
                        placeholder="https://www.finn.no/realestate/homes/ad.html?finnKode=340066219"
                        {...register("finnUrl", {required: true})}
                    />
                    <div className="">
                        <MainButton className="mt-4">
                            Scann salgsoppgave
                        </MainButton>
                    </div>
                </form>
            </div>
        </>
    );
};

export default SearchPage;
