import {createContext, useContext, useEffect, useState} from "react";
import {User} from "../types/User";
import axios from "axios";
import {getBackendURL} from "../utils/EnvironmentsManager";
import {useNavigate} from "react-router";

export interface UserHolder {
    currentUser?: User;
    refreshUser: () => void;
}

export const UserContext = createContext<UserHolder>({
    refreshUser: () => {}
})

export const useUser = () => {
    const context = useContext(UserContext);
    if (context === null) {
        throw new Error("Must be used as useUser");
    }
    return context;
}

export interface UserContextProviderProps {
    children: React.ReactNode
}

export const UserProvider = ({children}: UserContextProviderProps) => {
    const [currentUser, setCurrentUser] = useState<User | undefined>()
    const navigate = useNavigate()

    useEffect(() => {
        refreshUser()
    }, []);

    const refreshUser = () => {
        axios.get(getBackendURL() + "/api/v1/user/me").then(results => {
            let user:User = results.data
            setCurrentUser(user)
        }).catch(error => {
            console.log(error)
            navigate("/register")
        })
    }

    return (
        <UserContext.Provider value={{currentUser, refreshUser}}>
            {children}
        </UserContext.Provider>
    )
}