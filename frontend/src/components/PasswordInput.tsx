import React, { useCallback, useEffect, useState } from 'react';
import { FormControl, InputLabel, OutlinedInput, InputAdornment, IconButton } from '@mui/material';
import { FieldValues, RegisterOptions, useFormContext } from "react-hook-form";
import ErrorText from "./ErrorText";
import {EyeIcon, EyeSlashIcon} from "@heroicons/react/24/solid";

interface PasswordInputProps {
    name: string;
    label: string;
    value?: string;
    style?: React.CSSProperties;
    validation?: RegisterOptions<FieldValues, string>;
    onValueChange?: (e: React.ChangeEvent<HTMLInputElement>) => void;
    min?: number;
}

const PasswordInput: React.FC<PasswordInputProps> = ({ label, value, onValueChange, name, style, validation, min }) => {
    const { register, setValue: setFormValue, setError: setErrorMessage, formState: { errors } } = useFormContext();
    const [showPassword, setShowPassword] = useState(false);
    const [fieldValue, setFieldValue] = useState<string | null | undefined>(value ? value : '');

    useEffect(() => {
        setFormValue(name, fieldValue || '');
    }, [fieldValue, name, setFormValue]);

    useEffect(() => {
        if (min && Number(fieldValue) < min) {
            setFieldValue(String(min));
        }
    }, [fieldValue, min]);

    const handleClickShowPassword = () => {
        setShowPassword(!showPassword);
    };

    const handleMouseDownPassword = (event: React.MouseEvent<HTMLButtonElement>) => {
        event.preventDefault();
    };

    const defaultValidation: RegisterOptions<FieldValues, string> = {
        required: "Passord er obligatorisk",
        minLength: {
            value: 8,
            message: "Passordet må være minst 8 tegn"
        },
        maxLength: {
            value: 50,
            message: "Passordet kan ikke være mer enn 80 tegn"
        },
        validate: {
            hasNumber: value => /\d/.test(value) || "Passordet må inkludere et tall",
            hasUpper: value => /[A-Z]/.test(value) || "Passordet må inkludere både store og må bokstaver",
            hasLower: value => /[a-z]/.test(value) || "Passordet må inkludere både store og må bokstaver",
        }
    };

    const { ref, onChange, ...rest } = register(name, validation || defaultValidation);

    const handleChange = useCallback(
        (e: React.ChangeEvent<HTMLInputElement>) => {
            setFieldValue(e.target.value);
            onValueChange && onValueChange(e);
            onChange(e);
        },
        [onValueChange, rest, setFieldValue]
    );

    return (
        <FormControl style={{ marginTop: 20, ...style }} variant="outlined">
            <InputLabel htmlFor="outlined-adornment-password">{label}</InputLabel>
            <OutlinedInput
                id="outlined-adornment-password"
                type={showPassword ? 'text' : 'password'}
                value={fieldValue ||
                    ''}
                onChange={handleChange}
                inputRef={ref}
                endAdornment={
                    <InputAdornment position="end">
                        <IconButton
                            aria-label="toggle password visibility"
                            onClick={handleClickShowPassword}
                            onMouseDown={handleMouseDownPassword}
                            edge="end"
                        >
                            {showPassword ? <EyeSlashIcon /> : <EyeIcon />}
                        </IconButton>
                    </InputAdornment>
                }
                label={label}
                {...rest}
            />
            <ErrorText errors={errors} name={name} />
        </FormControl>
    );
};

export default PasswordInput;