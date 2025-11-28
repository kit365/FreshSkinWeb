"use client"

import { useState, useEffect } from "react";

export default function FormInputCheckout(props: {
    label?: string,
    type?: string,
    name?: string,
    id?: string,
    required?: boolean,
    value?: string
}) {
    const { label = "", type = "text", name = "", id = "", required = false, value = "" } = props;

    const [inputIsFocused, setInputIsFocused] = useState(value !== "");
    const [currentValueInput, setCurrentValueInput] = useState(value);

    useEffect(() => {
        if (value !== "") {
            setInputIsFocused(true);
            setCurrentValueInput(value);
        }
    }, [value]);

    const handleFocusInput = () => setInputIsFocused(true);

    const handleInput = (event: any) => setCurrentValueInput(event.target.value);

    const handleBlurInput = () => {
        if (currentValueInput.length === 0) {
            setInputIsFocused(false);
        }
    };

    return (
        <div className="relative">
            <label
                htmlFor={id}
                className={`text-[#999] absolute left-[11px] transition-all duration-200 ${
                    inputIsFocused ? "text-[13px] top-[5px]" : "text-[15px] top-[12px]"
                }`}
            >
                {label}
            </label>
            <input
                type={type}
                name={name}
                id={id}
                className="pt-[16px] pb-[2px] px-[11px] w-full h-[44px] bg-white text-[#333] rounded-[4px] border border-solid border-[#d9d9d9] focus:border-[#72a834] outline-none input-checkout mb-[10px] text-[14px] font-[450]"
                onFocus={handleFocusInput}
                onBlur={handleBlurInput}
                onInput={handleInput}
                required={required}
                value={currentValueInput}
                onChange={handleInput}
            />
        </div>
    );
}
