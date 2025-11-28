"use client"

import InforOrder from "./InforOrder";
import Thanks from "./Thanks";
import ButtonContinue from "./ButtonContinue";

export default function Section1() {
    return (
        <>
            <div className="w-[714px]">
                <Thanks />
                <InforOrder />
                <ButtonContinue />
            </div>
        </>
    )
}