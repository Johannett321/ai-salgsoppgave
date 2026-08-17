import { ReactNode } from "react";

interface TitleDescCardProps {
    title?: string;
    desc?: string;
    children: ReactNode;
    className?: string;
}

const TitleDescCard = ({
    title,
    desc,
    children,
    className,
}: TitleDescCardProps) => {
    return (
        <div className={"p-4 bg-white shadow-md rounded-xl " + className}>
            <h3 className="text-xl font-bold text-main">{title}</h3>
            {desc && (
                <p className="text-gray-400">{desc}</p>
            )}
            <div className="my-4">{children}</div>
        </div>
    );
};

export default TitleDescCard;
