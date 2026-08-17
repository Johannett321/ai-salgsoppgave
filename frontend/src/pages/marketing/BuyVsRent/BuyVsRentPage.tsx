import React, {FormEventHandler, useEffect, useState} from 'react';
import MainButton from "../../../components/MainButton";

const InputField = ({label, value, onInput}: {label: string, value: number, onInput?: FormEventHandler}) => {
    return (
        <div className={"my-4"}>
            <div className={"font-semibold"}>{label}:</div>
            <input
                className={"border-main border-[1px] rounded-full p-2 w-full"}
                defaultValue={value}
                onInput={onInput}
            />
        </div>
    )
}

// Constants
const initialSalary = 32717;
const keepHouseForYears = 5;
const inflation = 0.02;
const salaryYearlyIncrease = 1.05;
const stipend = 8600;
const studyLoanPerMonth = 2000;
const moneyNotToInvest = 40000;
const yearlyReturnInvestments = 1.12;
const investmentsTaxPercent = 0.3784;

// House Loan
const houseValueInitial = 2000000;
const payUpfront = 150000;
const annualInterestRate = 0.045;
const loanTermYears = 25;
const houseValueIncrease = 1.04;

// Expenses
const regExpenses = 3338;
const foodElec = 6000;
const fellesUtgifter = 3343;
const monthlyExpensesInitial = regExpenses + foodElec + fellesUtgifter;

// Utilities
const formatCurrency = (money: number) =>
    money.toLocaleString('no-NO', {
        style: 'currency',
        currency: 'NOK',
        minimumFractionDigits: 2,
    });

const performYearlyInvestment = (money: number): number => {
    const moneyToInvest = money - moneyNotToInvest;
    if (moneyToInvest > 0) {
        let sumAfterInvest = moneyToInvest * yearlyReturnInvestments;
        const taxAmount = (sumAfterInvest - moneyToInvest) * investmentsTaxPercent;
        sumAfterInvest -= taxAmount;
        return sumAfterInvest + moneyNotToInvest;
    }
    return money;
};

// React Component
const BuyVsRentPage: React.FC = () => {
    const [balance, setBalance] = useState<number>(40000);
    const [houseValue, setHouseValue] = useState<number>(houseValueInitial);
    const [loanAmount, setLoanAmount] = useState<number>();
    const [totalInvestmentReturns, setTotalInvestmentReturns] = useState<number>(0);
    const [totalInterestPaid, setTotalInterestPaid] = useState<number>(0);
    const [totalSkattefradrag, setTotalSkattefradrag] = useState<number>(0);

    useEffect(() => {
        setLoanAmount(houseValue - payUpfront)
    }, [houseValue, payUpfront]);

    useEffect(() => {
        calculate()
    }, [balance]);

    const calculate = () => {
        let salary = initialSalary;
        let monthlyExpenses = monthlyExpensesInitial;
        let currentBalance = balance;
        let currentHouseValue = houseValue;
        let currentLoanAmount = loanAmount;
        let totalPaidInterest = 0;
        let totalInvestmentReturn = 0;
        let totalSkattefradragRenter = 0;

        const monthlyInterestRate = annualInterestRate / 12;
        const loanTermMonths = loanTermYears * 12;
        const annuityFactor =
            (monthlyInterestRate * Math.pow(1 + monthlyInterestRate, loanTermMonths)) /
            (Math.pow(1 + monthlyInterestRate, loanTermMonths) - 1);
        const monthlyPayment = currentLoanAmount! * annuityFactor;

        for (let year = 0; year < keepHouseForYears; year++) {
            let yearlyInterestPaid = 0;
            let yearlyPrincipalPaid = 0;
            let yearlySkattefradrag = 0;

            for (let month = 0; month < 12; month++) {
                // Calculate interest and principal
                const interest = currentLoanAmount! * monthlyInterestRate;
                yearlyInterestPaid += interest;
                const principal = monthlyPayment - interest;
                currentLoanAmount! -= principal;
                yearlyPrincipalPaid += principal;

                // Skattefradrag
                const skattefradrag = interest * 0.22;
                currentBalance += skattefradrag;
                yearlySkattefradrag += skattefradrag;

                // Stipend and study loan
                if (year < 1 || (year === 1 && month < 4)) {
                    currentBalance += stipend;
                } else {
                    currentBalance -= studyLoanPerMonth;
                }

                // Adjust balance
                currentBalance += salary;
                currentBalance -= monthlyPayment;
                currentBalance -= monthlyExpenses;
            }

            // Increase house value
            currentHouseValue *= houseValueIncrease;

            // Invest balance
            const investmentReturn = performYearlyInvestment(currentBalance) - currentBalance;
            totalInvestmentReturn += investmentReturn;
            currentBalance += investmentReturn;

            // Update totals
            totalPaidInterest += yearlyInterestPaid;
            totalSkattefradragRenter += yearlySkattefradrag;

            // Salary increase and inflation
            salary *= salaryYearlyIncrease;
            monthlyExpenses *= 1 + inflation;
        }

        // Final balance adjustments (selling house and paying loan)
        currentBalance += currentHouseValue;
        currentBalance -= currentLoanAmount!;

        setBalance(currentBalance);
        setHouseValue(currentHouseValue);
        setLoanAmount(currentLoanAmount);
        setTotalInterestPaid(totalPaidInterest);
        setTotalInvestmentReturns(totalInvestmentReturn);
        setTotalSkattefradrag(totalSkattefradragRenter);
    };

    return (
        <div className={"mt-10"}>
            <img className={"mx-auto h-20 mb-10"} src={"/images/logo_dark.webp"} alt={"Logo"}/>
            <div className="p-6 max-w-4xl mx-auto bg-white shadow-lg rounded-lg">
                <h1 className="text-2xl font-bold mb-4">House Loan and Investment Calculator</h1>

                <div className={"grid grid-cols-2 text-left gap-x-10"}>
                    <InputField
                        label={"Egenkapital"}
                        value={balance}
                        onInput={(e) => {
                            // @ts-ignore
                            setBalance(e.target.value)
                        }}
                    />
                    <InputField
                        label={"Kjøpesum"}
                        value={houseValue}
                        onInput={(e) => {
                            // @ts-ignore
                            setHouseValue(e.target.value)
                        }}
                    />
                </div>

                <MainButton
                    onClick={calculate}
                >
                    Regn ut
                </MainButton>

                <div className="mt-6">
                    <h2 className="text-xl font-semibold">Results:</h2>
                    <p>Current Balance: {formatCurrency(balance)}</p>
                    <p>House Value: {formatCurrency(houseValue)}</p>
                    <p>Total Investment Returns: {formatCurrency(totalInvestmentReturns)}</p>
                    <p>Total Interest Paid: {formatCurrency(totalInterestPaid)}</p>
                    <p>Total Skattefradrag (Tax Deductions on Interest): {formatCurrency(totalSkattefradrag)}</p>
                </div>
            </div>
        </div>
    );
};

export default BuyVsRentPage;
