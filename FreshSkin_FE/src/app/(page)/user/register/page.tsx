"use client"

import FormButton from "@/app/components/Form/FormButton";
import FormFaceGoogle from "@/app/components/Form/FormFaceGoogle";
import FormInput from "@/app/components/Form/FormInput";
import { Alert } from "@mui/material";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useState } from "react";
import { MdNavigateNext } from "react-icons/md";

export default function RegisterPage() {
    const router = useRouter();
    const [alert, setAlert] = useState<any>();


    const handleSubmit = async (event: any) => {
        event.preventDefault();

        const formData = new FormData(event.currentTarget);

        if (!formData.get("firstName")) {
            setAlert({
                severity: "error",
                content: "Vui lòng nhập họ của bạn"
            });

            setTimeout(() => {
                setAlert({
                    severity: "",
                    content: ""
                })
            }, 3000);
            return;
        }

        if (!formData.get("lastName")) {
            setAlert({
                severity: "error",
                content: "Vui lòng nhập tên của bạn"
            });

            setTimeout(() => {
                setAlert({
                    severity: "",
                    content: ""
                })
            }, 3000);
            return;
        }

        if (!formData.get("username")) {
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

        if (!formData.get("password")) {
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

       
        if (!formData.get("phone")) {
            setAlert({
                severity: "error",
                content: "Vui lòng nhập số điện thoại"
            });

            setTimeout(() => {
                setAlert({
                    severity: "",
                    content: ""
                })
            }, 3000);
            return;
        }

        if (!formData.get("email")) {
            setAlert({
                severity: "error",
                content: "Vui lòng nhập email"
            });

            setTimeout(() => {
                setAlert({
                    severity: "",
                    content: ""
                })
            }, 3000);
            return;
        }

        const request = {
            username: formData.get("username"),
            password: formData.get("password"),
            firstName: formData.get("firstName"),
            lastName: formData.get("lastName"),
            email: formData.get("email"),
            phone: formData.get("phone"),
            address: formData.get("address"),
        };

        formData.append("request", JSON.stringify(request));

        const response = await fetch('https://freshskinweb.onrender.com/admin/account/create', {
            method: "POST",
            body: formData
        });

        const dataResponse = await response.json();

        if (dataResponse.code == 200) {
            setAlert({
                severity: "success",
                content: "Đăng ký tài khoản thành công"
            });
            router.push("/user/login");
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
                        Đăng ký tài khoản
                    </li>
                </ul>
                <form onSubmit={handleSubmit} className="container mx-auto w-[432px] bg-[#fff] p-[10px] mt-[35px] text-center rounded-[10px] relative">
                    <h1 className="text-primary text-[26px] font-[400] uppercase mb-[35px] mt-[10px] register">Đăng ký</h1>
                    <p className="text-[#00090f] text-[14px] mb-[15px]">Đã có tài khoản, đăng nhập <Link href="/user/login" className="text-primary hover:text-[#00090f]">tại đây</Link></p>
                    <FormInput
                        placeholder="Họ"
                        name="firstName"
                    />
                    <FormInput
                        placeholder="Tên"
                        name="lastName"
                    />
                    <FormInput
                        placeholder="Tên tài khoản"
                        name="username"
                    />
                    <FormInput
                        type="password"
                        placeholder="Mật khẩu"
                        name="password"
                    />
                    <FormInput
                        placeholder="Số điện thoại"
                        name="phone"
                    />
                    <FormInput
                        placeholder="Email"
                        name="email"
                        type="email"
                    />
                    {/* Alert */}
                    {alert && (
                        <Alert style={{ marginBottom: "10px" }} severity={alert.severity}>{alert.content}</Alert>
                    )}
                    <FormButton text="Đăng ký" />
                    <FormFaceGoogle info="Hoặc đăng nhập bằng" />
                </form>
            </div>
        </>
    )
}