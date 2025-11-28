"use client"

export default function InforOderText(props: { info: string }) {
    const { info } = props;

    return (
        <>
            {info && (
                <div className="text-[14px] text-[#46484a] py-[4px]">{info}</div>
            )}
        </>
    )
}