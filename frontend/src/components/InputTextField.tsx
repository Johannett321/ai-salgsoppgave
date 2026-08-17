import React, { useEffect, useState } from 'react';
import { FieldValues, RegisterOptions, useFormContext } from 'react-hook-form';
import {InputAdornment, FormControl, TextField} from '@mui/material';
import ErrorText from './ErrorText';

interface InputTextFieldProps {
    name: string;
    type: string;
    label: string;
    value?: string | null;
    placeholder?: string;
    autoSelectToday?: boolean;
    style?: React.CSSProperties;
    validation?: RegisterOptions<FieldValues, string>;
    onChange?: (value: string) => void;
    disableEditing?: boolean;
}

const InputTextField = (props: InputTextFieldProps) => {
    const { register, setValue: setFormValue, resetField, setError: setErrorMessage, formState: { errors } } = useFormContext();
    const [fieldValue, setFieldValue] = useState<string>(props.value ? props.value : '');

    // @ts-ignore
    const isRequired = props.validation?.required && (props.validation?.required === true || props.validation.required.value === true);

    useEffect(() => {
        setFormValue(props.name, fieldValue);
    }, [fieldValue, props.name, setFormValue]);

    const { onChange, ...rest } = register(props.name, props.validation);

    return (
        <>
            <TextField
                style={{ ...props.style }}
                error={!!errors[props.name]}
                label={props.type === "date" ? "" : isRequired ? `${props.label}*` : props.label}
                variant="outlined"
                type={props.type}
                value={fieldValue}
                placeholder={props.placeholder}
                onChange={(e) => {
                    let newFieldValue = e.target.value;
                    setFieldValue(newFieldValue);
                    props.onChange && props.onChange(e.target.value);
                    onChange(e);
                }}
                disabled={props.disableEditing}
                {...rest}
            />
            <ErrorText errors={errors} name={props.name} />
        </>
    );
};

export default InputTextField;