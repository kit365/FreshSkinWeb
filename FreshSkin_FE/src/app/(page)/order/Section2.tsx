"use client"

import FeePay from "./FeePay";
import Products from "./Products";
import SaleCode from "./SaleCode";
import TitleSection2 from "./TitleSection2";
import TotalPrice from "./TotalPrice";

export default function Section2() {
    return (
        <>
            <div className="flex-1 bg-[#FAFAFA] pr-[15%] border-l border-solid border-[#e1e1e1]">
                <TitleSection2/>
                <Products/>
                <SaleCode />
                <FeePay />
                <TotalPrice />
            </div>
        </>
    )
}