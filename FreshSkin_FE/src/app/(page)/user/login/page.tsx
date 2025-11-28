"use client"

import FormButton from "@/app/components/Form/FormButton";
import FormFaceGoogle from "@/app/components/Form/FormFaceGoogle";
import FormInput from "@/app/components/Form/FormInput";
import Link from "next/link";
import { useState } from "react";
import { MdNavigateNext } from "react-icons/md";
import { Alert } from "@mui/material";
import { useForm } from "react-hook-form";
import { useAuth } from "@/app/hooks/useAuth";
import { AuthenticationRequest } from "@/app/types/auth";
import { useRouter } from "next/navigation";

export default function LoginPage() {
    const [resetPassword, setResetPassword] = useState(false);
    const router = useRouter();
    
    // React Hook Form
    const {
        register,
        handleSubmit,
        formState: { errors }
    } = useForm<AuthenticationRequest>();

    // Auth hook
    const { login, isLoading, error, clearError } = useAuth();

    const handleSubmitLogin = async (data: AuthenticationRequest) => {
        try {
            clearError();
            await login(data);
            router.push('/');
        } catch (error: any) {
            console.error('Login failed:', error);
            // Error được handle trong useAuth hook
        }
    };

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
                    <form onSubmit={handleSubmit(handleSubmitLogin)} className=" mt-[15px] text-center rounded-[10px] relative">
                        <h1 className="text-primary text-[26px] font-[400] uppercase mb-[35px] mt-[10px] login">Đăng nhập</h1>
                        
                        <div className="mb-4">
                            <input
                                {...register("username", { 
                                    required: "Vui lòng nhập tên người dùng" 
                                })}
                                placeholder="Tên tài khoản"
                                className="w-full p-3 border rounded-lg"
                            />
                            {errors.username && (
                                <p className="text-red-500 text-sm mt-1">{errors.username.message}</p>
                            )}
                        </div>

                        <div className="mb-4">
                            <input
                                {...register("password", { 
                                    required: "Vui lòng nhập mật khẩu" 
                                })}
                                type="password"
                                placeholder="Mật khẩu"
                                className="w-full p-3 border rounded-lg"
                            />
                            {errors.password && (
                                <p className="text-red-500 text-sm mt-1">{errors.password.message}</p>
                            )}
                        </div>

                        {/* Error Alert */}
                        {error && (
                            <Alert style={{marginBottom: "10px"}} severity="error">{error}</Alert>
                        )}
                        
                        <button
                            type="submit"
                            disabled={isLoading}
                            className="mb-[15px] w-full h-[45px] border boder-solid border-primary uppercase text-white bg-primary text-[12px] text-center py-[10px] rounded-[4px] transi hover:text-primary hover:bg-white disabled:opacity-50 disabled:cursor-not-allowed"
                        >
                            {isLoading ? "Đang đăng nhập..." : "Đăng nhập"}
                        </button>
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