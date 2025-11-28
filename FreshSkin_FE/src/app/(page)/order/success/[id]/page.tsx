"use client"

import Link from "next/link";
import Section1 from "./Section1";
import Section2 from "./Section2";
import { useContext, useEffect, useState } from "react";
import { useParams } from "next/navigation";
import { SuccessOrderContext } from "./SuccessOrderContext"
import { SettingProfileContext } from "@/app/(page)/layout";

export default function SuccessPage() {
    const { id } = useParams();
    const [data, setData] = useState({
        firstName: "",
        lastName: "",
        email: "",
        address: "",
        phoneNumber: "",
        totalAmount: 0,
        totalPrice: 0,
        paymentMethod: "",
        orderDate: "",
        orderItems: [],
        discountAmount: 0,
        priceShipping: 0
    });
    const [isLoading, setIsLoading] = useState(true);
    const settingProfile = useContext(SettingProfileContext);

    useEffect(() => {
        const fetchData = async () => {
            const response = await fetch(`https://freshskinweb.onrender.com/home/orders/${id}`);
            const data = await response.json();
            setData(data.data);
            setIsLoading(false);
        };

        fetchData();
    }, [id]);

    if (isLoading) {
        return <div>Loading...</div>;
    }

    if (!settingProfile) {
        return null;
    }

    const { setting } = settingProfile;

    return (
        <div className="container mx-auto pt-[30px]">
            <div className="flex items-center justify-center">
                <Link href="/" className="w-[206px] h-[82px] mb-[21px]">
                    <img src={setting?.logo || 'https://res.cloudinary.com/dr53sfboy/image/upload/v1742010540/product-brand/test_20250315-034900_4.png'} className="w-full h-full object-cover" />
                </Link>
            </div>
            <div className="flex items-start">
                <SuccessOrderContext.Provider
                    value={{
                        fullname: `${data.firstName} ${data.lastName}`,
                        email: data.email,
                        address: data.address,
                        phone: data.phoneNumber,
                        quantity: data.totalAmount,
                        totalPrice: data.totalPrice,
                        method: data.paymentMethod,
                        date: data.orderDate,
                        products: data.orderItems,
                        discountAmount: data.discountAmount,
                        priceShipping: data.priceShipping
                    }}
                >
                    <Section1 />
                    <Section2 />
                </SuccessOrderContext.Provider>
            </div>
        </div>
    );
}
