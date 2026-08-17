import {ReactNode} from "react";

interface MainTitleProps {
    className?: string;
    children: ReactNode;
}

const MainTitle = ({className, children}: MainTitleProps) => {
    return (
        <h1 className={"text-4xl text-[#305345] font-semibold " + className}>
            {children}
        </h1>
    )
}

export default MainTitle;