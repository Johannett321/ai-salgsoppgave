import { ReactNode } from "react"

interface HeadingDescriptorProps {
    children: ReactNode
    className?: string
}

const HeadingDescriptor = ({children, className}: HeadingDescriptorProps) => {
    return (
        <p className={"text-[#799289] text-lg " + className}>
            {children}
        </p>
    )
}

export default HeadingDescriptor