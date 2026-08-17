export const getBackendURL = () => {
    return process.env.REACT_APP_BACKEND_URL;
};

export const getFrontendURL = () => {
    return process.env.REACT_APP_FRONTEND_URL;
};

export const isDevelopmentEnv = () => {
    return window.location.hostname == "localhost"
}