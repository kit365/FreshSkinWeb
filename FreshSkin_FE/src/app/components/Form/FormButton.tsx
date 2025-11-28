"use client"

export default function FormButton(props: { text: string }) {
    const { text = "" } = props;

    return (
        <>
            <button
                type="submit"
                className="mb-[15px] w-full h-[45px] border boder-solid border-primary uppercase text-white bg-primary text-[12px] text-center py-[10px] rounded-[4px] transi hover:text-primary hover:bg-white"
            >
                {text}
            </button>
        </>
    )
}