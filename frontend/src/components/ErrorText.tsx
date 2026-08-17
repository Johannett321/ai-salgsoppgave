import React from "react";
import {FieldErrors, FieldValues} from "react-hook-form";

interface ErrorTextProps {
    errors: FieldErrors<FieldValues>
    name: string
}
const ErrorText = ({errors, name}:ErrorTextProps) => {
    return (
        <>
            {errors[name]?.message ?
                <div className={"text-red-500"}>
                    {String(errors[name]?.message)}
                </div>
                : ""}
        </>
    )
}

export default ErrorText