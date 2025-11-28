"use client"

export default function TitleCheckout(props: { text: string }) {
    const { text = "" } = props;
    return(
        <>
            <h2 className="text-[19px] font-[550] mb-[10px]">{text}</h2>
        </>
    )
}