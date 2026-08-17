import React, { useState } from 'react';
import axios from "axios";
import {useNavigate} from "react-router";
import {getBackendURL} from "../../../utils/EnvironmentsManager";

const UploadPDF: React.FC = () => {
    const [selectedFile, setSelectedFile] = useState<File | null>(null);
    const navigate = useNavigate();

    const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        // @ts-ignore
        setSelectedFile(event.target.files?.[0]);
    };

    const handleSubmit = async (event: React.FormEvent) => {
        event.preventDefault(); // Prevent default form submission behavior
        if (!selectedFile) {
            alert('Please select a PDF file.');
            return;
        }

        try {
            const formData = new FormData();
            formData.append('file', selectedFile);

            axios.post(getBackendURL() + "/api/v1/job/upload-pdf", formData).then(result => {
                navigate("/view/" + result.data.id)
            })
        } catch (error) {
            console.error('Error during upload:', error);
            alert('An error occurred while uploading the file.');
        }
    };

    return (
        <div>
            <h1>PDF Upload</h1>
            <form onSubmit={handleSubmit}>
                <input type="file" accept=".txt" onChange={handleFileChange} />
                <button type="submit">Upload</button>
            </form>
        </div>
    );
};

export default UploadPDF;