import { useFormContext, Controller } from 'react-hook-form';
import { useCallback, useEffect, useState } from 'react';
import { Accept, useDropzone, FileRejection, FileError } from 'react-dropzone';
import { CheckCircleIcon } from '@heroicons/react/24/solid';

interface UploadFileDropZoneProps {
    idleText?: string;
    dropHoverText?: string;
    name: string;
    accept?: Accept;
    onFileDropped?: (droppedFile: File) => void;
    onError?: (error: FileError) => void;
}

const UploadFileDropZone = ({
                                idleText,
                                dropHoverText,
                                name,
                                accept,
                                onFileDropped,
                                onError
                            }: UploadFileDropZoneProps) => {
    const { control, setValue, getValues, register } = useFormContext();
    const [selectedFile, setSelectedFile] = useState<File | null>(null);
    const [errorMessage, setErrorMessage] = useState<string | null>(null);

    const onDrop = useCallback(
        (acceptedFiles: File[], fileRejections: FileRejection[]) => {
            if (fileRejections.length > 0) {
                const rejection = fileRejections[0]; // Just show the first error, could be multiple
                const error = rejection.errors[0]; // Show the first error message
                setErrorMessage(error.message);
                setSelectedFile(null); // Reset the selected file if invalid
                onError && onError(error);
            } else {
                setErrorMessage(null); // Clear any previous error
                setValue(name, acceptedFiles); // This sets the file in react-hook-form's state
                setSelectedFile(acceptedFiles[0]); // Assuming only one file is allowed
                onFileDropped && onFileDropped(acceptedFiles[0]);
            }
        },
        [setValue, name, onError, onFileDropped]
    );

    const { getRootProps, getInputProps, isDragActive } = useDropzone({
        onDrop,
        multiple: false,
        accept: accept,
    });

    // Register the field when the component is mounted
    useEffect(() => {
        register(name); // Ensure the field is registered with react-hook-form
        const currentFile = getValues(name);
        if (currentFile && currentFile.length > 0) {
            setSelectedFile(currentFile[0]);
        }
    }, [getValues, name, register]);

    return (
        <Controller
            name={name}
            control={control}
            render={({ fieldState: { error } }) => (
                <div className="flex flex-col items-center w-full">
                    <div
                        {...getRootProps()}
                        className={`border-2 border-dashed rounded-lg p-6 w-full text-center cursor-pointer flex flex-col items-center justify-center ${
                            isDragActive
                                ? 'border-main-color bg-indigo-50'
                                : 'border-gray-300'
                        }`}
                    >
                        <input {...getInputProps()} />
                        {selectedFile ? (
                            <div className="flex items-center justify-center space-x-2">
                                <CheckCircleIcon className="h-6 w-6 text-green-500" />
                                <p>{selectedFile.name}</p>
                            </div>
                        ) : (
                            <p>
                                {isDragActive
                                    ? dropHoverText || 'Slipp filene her'
                                    : idleText || 'Slipp filer her, eller klikk for å velge'}
                            </p>
                        )}

                        {/* Show error inside the dropzone */}
                        {(error || errorMessage) && (
                            <p className="text-red-500 text-sm mt-2">
                                {errorMessage || error?.message}
                            </p>
                        )}
                    </div>
                </div>
            )}
        />
    );
};

export default UploadFileDropZone;
