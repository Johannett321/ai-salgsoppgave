import React, {ReactNode} from "react";

interface DividerWithTextProps {
    children: ReactNode
    classname?: string;
}

const DividerWithText: React.FC<DividerWithTextProps> = (props: DividerWithTextProps) => {
    return (
        <>
            <div className="absolute inset-0 flex items-center" aria-hidden="true">
                <div className="w-full border-t border-gray-200"/>
            </div>
            <div className="relative flex justify-center text-xs font-medium leading-6">
                <span className="bg-white px-6 text-gray-500">
                    {props.children}
                </span>
            </div>
        </>
    )
}
export default DividerWithText;