import {useEffect, useState} from "react";
import axios from "axios";
import {getBackendURL} from "../../utils/EnvironmentsManager";
import {SalgsoppgaveJob} from "../../types/SalgsoppgaveJob";
import {Spinner} from "../../components/Spinner";
import {formatAsNOK} from "../../utils/FormatUtils";
import {useNavigate} from "react-router";
import EmptyState from "../../components/EmptyState";
import HeadingDescriptor from "../../components/HeadingDescriptor";
import StatusTag from "../../components/StatusTag";
import PageHeading from "../../components/PageHeading";
import MainButton from "../../components/MainButton";
import RealEstateGridEmptyState from "../../components/RealEstateGridEmptyState";

const SavedPage = () => {

    return (
        <div className={"container mx-auto"}>
            <h1 className={"font-bold text-3xl mt-10 text-main text-left"}>Lagrede boliger</h1>
            <HeadingDescriptor className={"text-left"}>Dette er salgsoppgavene du har lagret</HeadingDescriptor>
            <RealEstateGridEmptyState>
                <div className={"grid grid-cols-1 xl:grid-cols-2 2xl:grid-cols-3"}>
                    <img src={"/templates/saved.webp"}/>
                    <img src={"/templates/saved.webp"}/>
                    <img src={"/templates/saved.webp"} className={"hidden md:block"}/>
                    <img src={"/templates/saved.webp"} className={"hidden 2xl:block"}/>
                </div>
            </RealEstateGridEmptyState>
        </div>
    )
}

export default SavedPage;