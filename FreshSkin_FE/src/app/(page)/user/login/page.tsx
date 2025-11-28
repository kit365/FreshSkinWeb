"use client"

import FormButton from "@/app/components/Form/FormButton";
import FormFaceGoogle from "@/app/components/Form/FormFaceGoogle";
import FormInput from "@/app/components/Form/FormInput";
import Link from "next/link";
import { useState } from "react";
import { MdNavigateNext } from "react-icons/md";
import Cookies from 'js-cookie';
import { Alert } from "@mui/material";

export default function LoginPage() {
    const [resetPassword, setResetPassword] = useState(false);
    const [alert, setAlert] = useState<any>();

    const handleSubmitLogin = async (event: any) => {
        event.preventDefault();

        if (!event.target.username.value) {
            setAlert({
                severity: "error",
                content: "Vui lòng nhập tên người dùng"
            });

            setTimeout(() => {
                setAlert({
                    severity: "",
                    content: ""
                })
            }, 3000);
            return;
        }

        if (!event.target.password.value) {
            setAlert({
                severity: "error",
                content: "Vui lòng nhập mật khẩu"
            });

            setTimeout(() => {
                setAlert({
                    severity: "",
                    content: ""
                })
            }, 3000);
            return;
        }

        const response = await fetch('https://freshskinweb.onrender.com/auth/login', {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            credentials: 'include',
            body: JSON.stringify({
                username: event.target.username.value,
                password: event.target.password.value
            })
        });


        const dataResponse = await response.json();

        if (dataResponse.code == 200) {
            const token = dataResponse.data.token;
            Cookies.set('tokenUser', token);
            location.href = "/"
        }
        else {
            setAlert({
                severity: "error",
                content: dataResponse.message
            });

            setTimeout(() => {
                setAlert({
                    severity: "",
                    content: ""
                })
            }, 3000)
        }
    }

    const handleForgotPassword = async (event: any) => {
        event.preventDefault();

        const response = await fetch('https://freshskinweb.onrender.com/admin/forgot-password/request', {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                email: event.target.email.value,
            })
        });
        const dataResponse = await response.json();

        if (dataResponse.code == 200) {
            location.href = `/user/otp?email=${event.target.email.value}`
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
                        Đăng nhập tài khoản
                    </li>
                </ul>
                <div className="container mx-auto w-[432px] bg-[#fff] p-[10px]">
                    <form onSubmit={handleSubmitLogin} className=" mt-[15px] text-center rounded-[10px] relative">
                        <h1 className="text-primary text-[26px] font-[400] uppercase mb-[35px] mt-[10px] login">Đăng nhập</h1>
                        <FormInput
                            placeholder="Tên tài khoản"
                            name="username"
                        />
                        <FormInput
                            type="password"
                            placeholder="Mật khẩu"
                            name="password"
                        />
                        {/* Alert */}
                        {alert && (
                            <Alert style={{marginBottom: "10px"}} severity={alert.severity}>{alert.content}</Alert>
                        )}
                        <FormButton text="Đăng nhập" />
                    </form>
                    <div className="flex items-center justify-between mb-[15px]">
                        <span
                            className="text-[#333] text-[14px] hover:text-primary cursor-pointer"
                            onClick={() => setResetPassword(!resetPassword)}
                        >
                            Quên mật khẩu?
                        </span>
                        <Link href="/user/register" className="text-[#333] text-[14px] hover:text-primary">Đăng ký tại đây</Link>
                    </div>
                    <form onSubmit={handleForgotPassword} className={(resetPassword ? "block" : "hidden")}>
                        <FormInput
                            type="email"
                            placeholder="Email"
                            name="email"
                        />
                        <FormButton text="Lấy lại mật khẩu" />
                    </form>
                    <FormFaceGoogle info="hoặc đăng nhập qua" />
                </div>
            </div>

        </>
    )
}