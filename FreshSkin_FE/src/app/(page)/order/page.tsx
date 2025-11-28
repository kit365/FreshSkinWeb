"use client"

import { useDispatch, useSelector } from "react-redux";
import FormOrder from "./FormOrder";
import Section2 from "./Section2";
import Link from "next/link";
import { cartReset } from "@/app/(actions)/cart";
import { useContext, useState } from "react";
import { SettingProfileContext } from "../layout";
import { useRouter } from "next/navigation";
import { Alert } from "@mui/material";
import Cookies from "js-cookie";
import { sumShip } from "@/app/(actions)/order";

interface DataSubmit {
    userId?: number,
    email: string,
    firstName: string,
    lastName: string,
    phoneNumber: string,
    address: string,
    totalAmount: number,
    totalPrice: number,
    paymentMethod: string,
    orderItems: any,
    voucherName: string,
    priceShipping: number
}

export default function OrderPage() {
    const router = useRouter();
    const dispatchCart = useDispatch();
    const dispatchOrder = useDispatch();
    const [loading, setLoading] = useState(false);
    const [alertMessage, setAlertMessage] = useState<string>("");
    const [alertSeverity, setAlertSeverity] = useState<"success" | "error" | "info" | "warning">("info");
    const tokenUser = Cookies.get("tokenUser");
    const quantity = useSelector((state: any) => state.cartReducer.totalQuantityInit);
    const totalPrice = useSelector((state: any) => (state.cartReducer.totalPriceInit));
    const voucherName = useSelector((state: any) => (state.cartReducer.voucherTitle));
    const products = useSelector((state: any) => (state.cartReducer.products));
    const feeShip = useSelector((state: any) => (state.orderReducer.feeShip));

    const settingProfile = useContext(SettingProfileContext);

    if (!settingProfile) {
        return null;
    }

    const { profile, setting } = settingProfile;

    const handleSubmitForm = async (event: any) => {
        event.preventDefault();
        setLoading(true);

        if (!event.target.firstName.value) {
            setAlertMessage("Họ không được để trống.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        if (!event.target.lastName.value) {
            setAlertMessage("Tên không được để trống.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        if (!event.target.phone.value) {
            setAlertMessage("Số điện thoại không được để trống.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        if (!event.target.address.value) {
            setAlertMessage("Địa chỉ không được để trống.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        if (!event.target.province.value) {
            setAlertMessage("Tỉnh thành không được để trống.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        if (!event.target.district.value) {
            setAlertMessage("Quận huyện không được để trống.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        if (!event.target.ward.value) {
            setAlertMessage("Phường xã không được để trống.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        if (!event.target.method.value) {
            setAlertMessage("Phương thức thanh toán không được để trống.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        const provinceData = event.target.province.value.split('+')[1];
        const districtData = event.target.district.value.split('+')[1];
        const wardData = event.target.ward.value.split('+')[1];
        const dataAddress = `${event.target.address.value}, ${provinceData}, ${districtData}, ${wardData}`;

        const dataProducts: any = [];
        products.map((item: any) => (
            dataProducts.push({
                productVariantId: item.variantId,
                quantity: item.quantity
            })
        ))

        if (tokenUser) {
            const data: DataSubmit = {
                userId: profile.userID,
                firstName: event.target.firstName.value,
                lastName: event.target.lastName.value,
                email: event.target.email.value,
                address: dataAddress,
                phoneNumber: event.target.phone.value,
                totalAmount: quantity,
                totalPrice: totalPrice + feeShip,
                paymentMethod: event.target.method.value,
                orderItems: dataProducts,
                voucherName: voucherName,
                priceShipping: feeShip
            }

            console.log(data);

            const response = await fetch('https://freshskinweb.onrender.com/home/orders/create', {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(data)
            });

            const dataResponse = await response.json();
            console.log(dataResponse);

            if (dataResponse.code == 200) {
                dispatchCart(cartReset());
                dispatchOrder(sumShip(0))
                if (event.target.method.value == "QR") {
                    const responseVNPAY = await fetch(`https://freshskinweb.onrender.com/api/vnpay/create?orderId=${dataResponse.data.orderId}`);
                    const dataResponseVNPAY = await responseVNPAY.json();
                    if (dataResponseVNPAY.code === 200) {
                        window.location.href = dataResponseVNPAY.data;
                    }
                }
                else {
                    router.push(`/order/success/${dataResponse.data.orderId}`)
                }
            }
            else {
                setAlertMessage(dataResponse.message);
                setAlertSeverity("error");
                setTimeout(() => setAlertMessage(""), 5000);
            }
        }
        else {
            const data: DataSubmit = {
                firstName: event.target.firstName.value,
                lastName: event.target.lastName.value,
                email: event.target.email.value,
                address: dataAddress,
                phoneNumber: event.target.phone.value,
                totalAmount: quantity,
                totalPrice: totalPrice + feeShip,
                paymentMethod: event.target.method.value,
                orderItems: dataProducts,
                voucherName: voucherName,
                priceShipping: feeShip
            }

            console.log(data);

            const response = await fetch('https://freshskinweb.onrender.com/home/orders/create', {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(data)
            });

            const dataResponse = await response.json();
            console.log(dataResponse);
            if (dataResponse.code == 200) {
                dispatchCart(cartReset());
                dispatchOrder(sumShip(0))
                if (event.target.method.value == "QR") {
                    const responseVNPAY = await fetch(`https://freshskinweb.onrender.com/api/vnpay/create?orderId=${dataResponse.data.orderId}`);
                    const dataResponseVNPAY = await responseVNPAY.json();
                    if (dataResponseVNPAY.code === 200) {
                        window.location.href = dataResponseVNPAY.data;
                    }
                }
                else {
                    router.push(`/order/success/${dataResponse.data.orderId}`)
                }
            }
            else {
                setAlertMessage(dataResponse.message);
                setAlertSeverity("error");
                setTimeout(() => setAlertMessage(""), 5000);
            }
        }
    }

    return (
        <>
            {alertMessage && (
                <Alert severity={alertSeverity} sx={{ position: "fixed", width: "600px", height: "80px", right: "5%", top: "5%", fontSize: "16px", zIndex: "999999" }}>
                    {alertMessage}
                </Alert>
            )}
            <form className="flex" onSubmit={handleSubmitForm}>
                <div className="w-[60%] pl-[15%] pr-[2%] py-[25px] flex flex-wrap justify-center">
                    <Link href="/" className="w-[206px] h-[82px] mb-[21px]">
                        <img src={setting.logo || "https://res.cloudinary.com/dr53sfboy/image/upload/v1742357764/product-brand/dsa_20250319-041604_3.png"} className="w-full h-full object-cover" />
                    </Link>
                    <FormOrder />
                </div>
                <Section2 />
            </form>
        </>
    )
}