"use client"

export default function InforOrderTitle(props: { title: string }) {
    const { title } = props;

    return (
        <>
            <div className="text-[20px] text-[#333]">{title}</div>
        </>
    )
}