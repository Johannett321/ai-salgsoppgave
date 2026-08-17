import { ReactNode } from "react";
import {AlternativeSpinner} from "./AlternativeSpinner";

interface MainButtonProps {
    children?: ReactNode;
    className?: string;
    loading?: boolean
    onClick?: () => void;
    disabled?: boolean;
}

const MainButton = ({ children, className, loading, onClick, disabled }: MainButtonProps) => {
    return (
        <button
            type="submit"
            className={
                "mx-3 px-4 py-2 text-white rounded-3xl transition-all " +
                (disabled ? "bg-gray-400 cursor-not-allowed" : "bg-gradient-to-r from-[#6DD1A5] to-[#669080] hover:opacity-75") + " " +
                className
            }
            onClick={!disabled ? onClick : undefined}
            disabled={disabled}
        >
            {loading ? (
                <div className={"flex flex-col justify-center items-center "}>
                    <AlternativeSpinner />
                </div>

            ) : (
                <>
                    {children}
                </>
            )}

        </button>
    );
};

export default MainButton;
