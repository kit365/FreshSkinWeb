"use client"

export default function FormInput(props: any) {
    const { type = "text", name = "", placeholder = "", className = "w-[412px]", id = "", readonly = false, value = "" } = props;
    return (
        <>
            <input
                type={type}
                placeholder={placeholder}
                name={name}
                id={id}
                defaultValue={value}
                readOnly={readonly}
                className={"outline-none mb-[15px] h-[45px] placeholder-[#757575] px-[20px] rounded-[4px] border border-solid border-[#e1e1e1] " + className}
            />
        </>
    )
}