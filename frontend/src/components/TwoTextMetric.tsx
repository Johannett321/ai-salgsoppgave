import Skeleton from "react-loading-skeleton";
import "react-loading-skeleton/dist/skeleton.css";

interface TwoTextMetricProps {
    title: string;
    value?: string | number;
    sup?: string
    ending?: string
    className?: string
}

const TwoTextMetric = ({title, value, sup, ending, className}: TwoTextMetricProps) => {
    return (
        <div
            className={"p-4 bg-[#305345] text-white rounded-xl flex flex-col justify-center items-center " + className}>
            <h3 className="text-center min-w-24">
                {value ? title : <Skeleton height={15}/>}
            </h3>
            <p className="block text-center text-xl font-bold min-w-32">
                {value || <Skeleton height={30} count={1}/>}
                {value && sup && (
                    <sup>
                        {sup}
                    </sup>
                )}
                {value && (ending)}
            </p>
        </div>
    )
}

export default TwoTextMetric;