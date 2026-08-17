import MetadataSection from "./components/MetadataSection";
import {SkeletonTheme} from "react-loading-skeleton";
import AddressComponent from "./components/AddressComponent";
import BackButton from "../../components/BackButton";
import AIReadingTag from "./components/AIReadingTag";
import FaultsSection from "./components/FaultsSection";
import ComingSoonComponents from "./components/ComingSoonComponents";
import FloatingChatComponent from "./components/FloatingChatComponent";
import AIWarning from "../../components/AIWarning";

const MainDashboard = () => {
    return (
        <SkeletonTheme baseColor={"#3d7364"} highlightColor={"#4c8e6a"}>
            <div className="container mx-auto flex flex-col mt-5 text-left justify-center">
                {/*--------------------------- HEADING ---------------------------*/}
                <BackButton/>
                <AddressComponent/>
                <AIReadingTag/>

                {/*------------------------- PAGE CONTENT -------------------------*/}
                <div className="mt-5 font-medium space-y-10">
                    <MetadataSection/>
                    <FaultsSection/>
                    <AIWarning/>
                    <ComingSoonComponents/>
                </div>

                {/*----------------------------- OTHER -----------------------------*/}
                <FloatingChatComponent/>
            </div>
        </SkeletonTheme>
    );
};

export default MainDashboard;
