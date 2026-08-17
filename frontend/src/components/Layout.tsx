import {Outlet} from "react-router";
import Sidebar from "./Sidebar";
import {SalgsoppgaveJobProvider} from "../providers/SalgsoppgaveJobProvider";
import {UserProvider} from "../providers/UserProvider";

const Layout = () => {
    return (
        <>
            <UserProvider>
                <SalgsoppgaveJobProvider>
                    <div className={"flex flex-col md:flex-row"}>
                        <Sidebar/>
                        <div className={"w-full md:ml-[300px] overflow-x-hidden"}>
                            <div className={"mx-5"}>
                                <Outlet/>
                            </div>
                        </div>
                    </div>
                </SalgsoppgaveJobProvider>
            </UserProvider>
        </>
    )
}

export default Layout
