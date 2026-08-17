import "./App.css";
import {BrowserRouter, Route, Routes} from "react-router-dom";
import SearchPage from "./pages/SearchPage/SearchPage";
import MainDashboard from "./pages/ViewBoligPage/MainDashboard";
import DashboardWrapper from "./providers/DashboardWrapper";
import UploadFilePage from "./pages/SearchPage/UploadFilePage";
import {Toaster} from "react-hot-toast";
import SignInPage from "./pages/authentication/SignInPage";
import SignUpPage from "./pages/authentication/SignUpPage";
import axios from "axios";
import HistoryPage from "./pages/HistoryPage/HistoryPage";
import Layout from "./components/Layout";
import NotFoundPage from "./pages/NotFoundPage/NotFoudPage";
import SavedPage from "./pages/SavedPage/SavedPage";
import ForgotPasswordPage from "./pages/authentication/ForgotPasswordPage";
import ChangePasswordPage from "./pages/authentication/ChangePasswordPage";
import BuyVsRentPage from "./pages/marketing/BuyVsRent/BuyVsRentPage";
import PrivacyPolicy from "./pages/legal/PrivacyPolicy";
import TermsOfService from "./pages/legal/TermsOfService";
import MarketingDownloadPage from "./pages/blog/MarketingDownload";

function App() {
    axios.defaults.withCredentials = true


    return (
        <div className="App">
            <Toaster/>
            <BrowserRouter>
                <Routes>
                    {/* Public Routes */}
                    <Route path={"/login"} element={<SignInPage/>}/>
                    <Route path={"/register"} element={<SignUpPage/>}/>
                    <Route path={"/forgot"} element={<ForgotPasswordPage/>}/>
                    <Route path={"/change-password"} element={<ChangePasswordPage/>}/>

                    <Route path={"/privacy-policy"} element={<PrivacyPolicy/>}/>
                    <Route path={"/terms-of-service"} element={<TermsOfService/>}/>

                    {/* Protected Routes */}
                    <Route element={<Layout/>}>
                        <Route path={"/"} element={<SearchPage/>}/>
                        <Route path={"/upload"} element={<UploadFilePage/>}/>
                        <Route path={"/lagret"} element={<SavedPage/>}/>
                        <Route path={"/historikk"} element={<HistoryPage/>}/>

                        <Route element={<DashboardWrapper/>}>
                            <Route path={"/view/:id"} element={<MainDashboard/>}/>
                        </Route>
                    </Route>

                    {/* Marketing */}
                    <Route path={"/buy-vs-rent"} element={<BuyVsRentPage/>}/>
                    <Route path={"/download/:id"} element={<MarketingDownloadPage/>}/>

                    {/* NotFound Route */}
                    <Route path="*" element={<NotFoundPage/>}/>
                </Routes>
            </BrowserRouter>
        </div>
    );
}

export default App;
