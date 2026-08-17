import React from 'react';
import MainButton from "../../components/MainButton";
import {useNavigate} from "react-router";

const NotFoundPage: React.FC = () => {
    const navigate = useNavigate()

    return (
        <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100 p-6">
            <img
                src="/images/not-found.png"
                alt="Ikke funnet"
                className="max-w-md mb-2"
            />
            <h1 className="text-4xl font-bold text-gray-800 mb-4">
                Siden ble ikke funnet
            </h1>
            <p className="text-lg text-gray-600 mb-4">
                Beklager, men siden du leter etter finnes ikke.
            </p>
            <MainButton
                className={"w-[200px]"}
                onClick={() => navigate("/")}
            >
                Gå til Hjem
            </MainButton>
        </div>
    );
};

export default NotFoundPage;
