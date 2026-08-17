import {useNavigate} from "react-router";

const Logo = ({className}:{className?: string}) => {
    const navigate = useNavigate()
    return (
        <div
            onClick={() => navigate("/")}
            className="relative">
            <img className={"cursor-pointer " + className} src={"/images/logo.webp"} alt="logo" />
        </div>
    )
}

export default Logo;
