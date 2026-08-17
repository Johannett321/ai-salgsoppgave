import Logo from "./Logo";
import SidebarItem from "./SidebarItem";
import {useUser} from "../providers/UserProvider";
import MainButton from "./MainButton";
import {Avatar} from "@mui/material";
import {useSearchParams} from "react-router-dom";
import {useNavigate} from "react-router";
import {useState} from "react";

const Sidebar = ({className}: {className?: string}) => {
    const {currentUser} = useUser()
    const navigate = useNavigate()

    const [isOpen, setIsOpen] = useState(false);

    return (
        <>
            {/* MOBILE */}
            <div className={"w-10 h-16 md:hidden"} />
            <div className={"fixed w-full md:hidden z-30 " + className}>
                {isOpen && (
                    <div
                        onClick={() => setIsOpen(false)}
                        className={"absolute bg-black w-screen h-screen top-0 left-0 opacity-40 -z-10"}/>
                )}

                <div className="relative bg-[#305345] w-full flex items-center justify-between px-4 py-3 h-16">
                    <div className={"block"}>
                        <Logo className={"h-8"}/>
                    </div>
                    <button
                        onClick={() => setIsOpen(!isOpen)}
                        className="text-white focus:outline-none"
                    >
                        {/* Hamburger icon */}
                        <svg className="h-6 w-6 fill-current" viewBox="0 0 24 24">
                            {isOpen ? (
                                <path
                                    fillRule="evenodd"
                                    clipRule="evenodd"
                                    d="M18.36 6.64L17 5.28L12 10.28L7 5.28L5.64 6.64L10.64 11.64L5.64 16.64L7 18L12 13L17 18L18.36 16.64L13.36 11.64L18.36 6.64Z"
                                />
                            ) : (
                                <path
                                    fillRule="evenodd"
                                    clipRule="evenodd"
                                    d="M4 6H20V8H4V6ZM4 11H20V13H4V11ZM4 16H20V18H4V16Z"
                                />
                            )}
                        </svg>
                    </button>
                </div>
                {/* Mobile menu */}
                {isOpen && (
                    <div className="bg-[#305345] text-white z-40">
                        <SidebarItem
                            title="Scann ny bolig"
                            onClick={() => {
                                navigate("/");
                                setIsOpen(false);
                            }}
                            selected={window.location.pathname === "/"}
                        />

                        <SidebarItem
                            title="Lagrede boliger"
                            onClick={() => {
                                navigate("/lagret");
                                setIsOpen(false);
                            }}
                            selected={window.location.pathname.includes("lagret")}
                        />

                        <SidebarItem
                            title="Historikk"
                            onClick={() => {
                                navigate("/historikk");
                                setIsOpen(false);
                            }}
                            selected={window.location.pathname.includes("historikk")}
                        />

                        <div
                            className={
                                "flex py-3 px-3 text-white font-bold items-center cursor-pointer hover:bg-main-darker transition-all"
                            }
                        >
                            {currentUser ? (
                                <>
                                    <Avatar className={"mr-2"}/>
                                    {currentUser.firstName} {currentUser.lastName}
                                </>
                            ) : (
                                <div
                                    className={"w-full flex justify-center"}
                                    onClick={() => navigate("/register")}
                                >
                                    <MainButton>Opprett bruker</MainButton>
                                </div>
                            )}
                        </div>
                    </div>
                )}
            </div>
            {/* PC */}
            <div
                className={"bg-[#305345] flex-col pt-10 text-left text-white fixed w-[300px] h-screen hidden md:flex " + className}>
                <div className={" px-10 mb-4"}>
                    <Logo/>
                </div>
                <SidebarItem
                    title="Scann ny bolig"
                    onClick={() => navigate("/")}
                    selected={window.location.pathname == "/"}
                />

                <SidebarItem
                    title="Lagrede boliger"
                    onClick={() => navigate("/lagret")}
                    selected={window.location.pathname.includes("lagret")}
                />

                <SidebarItem
                    title="Historikk"
                    onClick={() => navigate("/historikk")}
                    selected={window.location.pathname.includes("historikk")}
                />

                <div className={"flex flex-grow"}/>

                <div
                    className={"flex py-3 px-3 text-white font-bold items-center cursor-pointer hover:bg-main-darker transition-all"}>
                    {currentUser ? (
                        <>
                            <Avatar className={"mr-2"}/>
                            {currentUser.firstName} {currentUser.lastName}
                        </>
                    ) : (
                        <div className={"w-full flex justify-center"} onClick={() => navigate("/register")}>
                            <MainButton>Opprett bruker</MainButton>
                        </div>
                    )}
                </div>
            </div>
        </>
    );
};

export default Sidebar;
