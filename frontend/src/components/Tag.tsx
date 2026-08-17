import { ReactNode } from "react";

interface TagProps {
    children: ReactNode;
}

const Tag = ({children}: TagProps) => {
    return (
        <>
            {children}
        </>
    )
}

export default Tag;