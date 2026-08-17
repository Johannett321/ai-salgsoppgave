export interface TooltipTextProps {
    tooltip: string;
    children?: React.ReactNode;
    className?: string;
    underLine?: boolean;
}

const TooltipText = ({tooltip, children, className, underLine}: TooltipTextProps) => {
    return (
        <>
            <span className={"relative has-tooltip " + className}>
                <span className={"w-80 absolute tooltip left-[50%] -translate-x-[50%] rounded-xl bg-main -translate-y-[calc(100%+20px)] shadow-md p-4 text-md font-medium text-white"}>
                    {tooltip}
                </span>
                <span className={"cursor-help " + (underLine ? "border-dotted border-b-[2px] border-gray-500" : "")}>
                    {children}
                 </span>
            </span>

        </>
    )
}

export default TooltipText;