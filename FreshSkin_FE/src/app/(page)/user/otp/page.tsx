"use client"

import FormButton from "@/app/components/Form/FormButton";
import FormInput from "@/app/components/Form/FormInput";
import Link from "next/link";
import { MdNavigateNext } from "react-icons/md";
import { useSearchParams } from "next/navigation";
import Cookies from 'js-cookie';

export default function OtpPage() {
    const searchParams = useSearchParams();
    const email = searchParams.get('email')

    const handleSubmit = async (event: any) => {
        event.preventDefault();

        const response = await fetch('https://freshskinweb.onrender.com/admin/forgot-password/verify', {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                email: email,
                otp: event.target.otp.value
            })
        });
        const dataResponse = await response.json();
        if(dataResponse.code == 400){
            location.href = "/user/login";
        }

        const token = dataResponse.data;

        if(dataResponse.code == 200){
            Cookies.set('tokenUser', token);
            location.href = "/user/reset-password"
        }
    }

    return (
        <>
            <div className="bg-[#F6F6F6] pb-[50px] pt-[15px]">
                <ul className="flex items-center container mx-auto px-8 mb-[40px]">
                    <li>
                        <Link href="/" className="flex items-center">
                            <span className="text-[#333] text-[15px] font-[400] hover:text-secondary">Trang chủ</span>
                            <span><MdNavigateNext className="ml-[10px] text-[18px] mr-[10px]" /></span>
                        </Link>
                    </li>
                    <li className="text-secondary text-[15px] font-[400]">
                        Nhập mã OTP
                    </li>
                </ul>
                <div className="container mx-auto w-[432px] bg-[#fff] p-[10px]">
                    <form onSubmit={handleSubmit} className=" mt-[15px] text-center rounded-[10px] relative">
                        <h1 className="text-primary text-[26px] font-[400] uppercase mb-[35px] mt-[10px] login">Nhập mã OTP</h1>
                        <FormInput
                            placeholder="Email của bạn"
                            name="email"
                            value={email}
                            readonly={true}
                        />
                        <FormInput
                            placeholder="Nhập mã OTP"
                            name="otp"
                        />
                        <FormButton text="Xác thực" />
                    </form>
                    <Link href="/user/login" className="text-[#333] text-[14px] hover:text-primary flex items-center justify-center mb-[15px]">Bạn đã nhớ mật khẩu? Đăng nhập ngay</Link>
                </div>
            </div>

        </>
    )
}