"use client"

export default function ButtonPay(props: { className?: string }) {
    const { className = "" } = props;

    return (
        <>
            <button
                className={"my-[10px] rounded-[4px] uppercase w-full h-[40px] bg-primary hover:bg-white text-white hover:text-primary border border-solid border-primary " + className}
                type="submit"
            >
                Thanh toán
            </button>
        </>
    )
}