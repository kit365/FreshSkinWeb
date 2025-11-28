"use client"

import { useContext, useState } from "react";
import { CiShoppingCart } from "react-icons/ci";
import { useDispatch, useSelector } from "react-redux";
import { cartAddNewProduct } from "@/app/(actions)/cart";
import { Context } from "./MiddlewareGetData";
import { IoIosGitCompare } from "react-icons/io";
import { SettingProfileContext } from "../../layout";
import { useRouter } from "next/navigation";
import Link from "next/link";
import FormButton from "@/app/components/Form/FormButton";
import FormFaceGoogle from "@/app/components/Form/FormFaceGoogle";
import FormInput from "@/app/components/Form/FormInput";
import Cookies from 'js-cookie';
import { Alert } from "@mui/material";

interface CartItem {
    image: string,
    title: string,
    price: number,
    link: string,
    variantId: number,
    volume: number,
    unit: string,
    quantity: number,
    stock: number
}

export default function Price() {
    const dispatchCart = useDispatch();

    const router = useRouter();
    const products = useSelector((state: any) => state.cartReducer.products);
    const [alertMessage, setAlertMessage] = useState<string>("");
    const [alertSeverity, setAlertSeverity] = useState<"success" | "error" | "info" | "warning">("info");
    const { productDetail } = useContext(Context);

    const [currentVolume, setCurrentVolume] = useState<any>(productDetail.variants[0]);

    const [quantity, setQuantity] = useState(1);

    const settingProfile = useContext(SettingProfileContext);

    if (!settingProfile) {
        return null;
    }

    const { profile } = settingProfile;

    const isCompared = profile.productComparisonId?.products.find(item => productDetail.id === item.id);

    const handleChange = (event: any): void => {
        const value = parseInt(event.target.value);
        if (value <= currentVolume.stock) {
            setQuantity(value);
        }
    };

    const handleClickDecrease = (): void => {
        if (quantity - 1 >= 1) {
            setQuantity(quantity - 1);
        }
    }

    const handleClickIncrease = (): void => {
        if (quantity < currentVolume.stock) {
            setQuantity(quantity + 1);
        }
    };

    const handleAddNewProductToCart = () => {
        const newCart = [...products];
        const data: CartItem = {
            image: productDetail.thumbnail[0],
            title: productDetail.title,
            price: currentVolume.price * (1 - productDetail.discountPercent / 100),
            link: `/detail/${productDetail.slug}`,
            variantId: currentVolume.id,
            volume: currentVolume.volume,
            unit: currentVolume.unit,
            quantity: quantity,
            stock: currentVolume.stock
        };

        const existProductInCart = newCart.find(
            (item) => item.title === data.title && item.volume === data.volume
        );

        if (existProductInCart) {
            if (existProductInCart.quantity + data.quantity > currentVolume.stock) {
                setAlertMessage("Sản phẩm không đủ số lượng.");
                setAlertSeverity("error");
                setTimeout(() => setAlertMessage(""), 3000);
                return;
            }
            existProductInCart.quantity += data.quantity;
        } else {
            if (data.quantity > currentVolume.stock) {
                setAlertMessage("Sản phẩm không đủ số lượng.");
                setAlertSeverity("error");
                setTimeout(() => setAlertMessage(""), 3000);
                return;
            }
            newCart.push(data);
        }

        dispatchCart(cartAddNewProduct(newCart));
    };

    const handleBuyNow = () => {
        const data: CartItem = {
            image: productDetail.thumbnail[0],
            title: productDetail.title,
            price: currentVolume.price * (1 - productDetail.discountPercent / 100),
            link: `/detail/${productDetail.slug}`,
            variantId: currentVolume.id,
            volume: currentVolume.volume,
            unit: currentVolume.unit,
            quantity: quantity,
            stock: currentVolume.stock
        };

        const newCart = [...products];

        const existProductInCart = newCart.find(
            (item) => item.title === data.title && item.volume === data.volume
        );

        if (existProductInCart) {
            if (existProductInCart.quantity + data.quantity > currentVolume.stock) {
                setAlertMessage("Sản phẩm không đủ số lượng.");
                setAlertSeverity("error");
                setTimeout(() => setAlertMessage(""), 3000);
                return;
            }
            existProductInCart.quantity += data.quantity;
        } else {
            if (data.quantity > currentVolume.stock) {
                setAlertMessage("Sản phẩm không đủ số lượng.");
                setAlertSeverity("error");
                setTimeout(() => setAlertMessage(""), 3000);
                return;
            }
            newCart.push(data);
        }


        dispatchCart(cartAddNewProduct(newCart));
        router.push("/order");
    };

    const handleAddCompare = async (productId: number) => {
        if (!profile.productComparisonId) {
            const response = await fetch(`https://freshskinweb.onrender.com/home/products/comparison/save`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    productId: productId,
                    userID: profile.userID
                })
            });
            const dataResponse = await response.json();
            if (dataResponse.code == 200) {
                setAlert({
                    severity: "success",
                    content: dataResponse.message
                });

                setTimeout(() => {
                    setAlert({
                        severity: "",
                        content: ""
                    });
                    location.reload();
                }, 3000);
            };
            return;
        }

        if (profile.productComparisonId?.products.length <= 2) {
            const response = await fetch(`https://freshskinweb.onrender.com/home/products/comparison/save`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    productId: productId,
                    userID: profile.userID
                })
            });
            const dataResponse = await response.json();
            if (dataResponse.code == 200) {
                setAlert({
                    severity: "success",
                    content: dataResponse.message
                });

                setTimeout(() => {
                    setAlert({
                        severity: "",
                        content: ""
                    });
                    location.reload();
                }, 3000);
            }
        }
        else {
            setAlert({
                severity: "error",
                content: "Bạn chỉ có thể thêm tối đa 3 sản phẩm vào danh sách so sánh."
            });

            setTimeout(() => {
                setAlert({
                    severity: "",
                    content: ""
                });
            }, 3000);
        }
    }

    const handleDelete = async (productId: number) => {
        const response = await fetch(`https://freshskinweb.onrender.com/home/products/comparison/delete`, {
            method: "delete",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                id: profile.productComparisonId.id,
                productId: productId
            })
        });

        const dataResponse = await response.json();

        if (dataResponse.code == 200) {
            setAlert({
                severity: "success",
                content: dataResponse.message
            });

            setTimeout(() => {
                setAlert({
                    severity: "",
                    content: ""
                });
                location.reload();
            }, 3000);
        }
    };

    //Popup
    const [isPopupLoginOpen, setIsPopupLoginOpen] = useState(false);

    const handleOpenPopup = () => {
        setIsPopupLoginOpen(true);
    };

    const handleClosePopup = () => {
        setIsPopupLoginOpen(false);
    };

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
            window.location.href = `/detail/${productDetail.slug}`
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

        if (dataResponse.code == 500) {

        }
        if (dataResponse.code == 200) {
            location.href = `/user/otp?email=${event.target.email.value}`
        }
    }

    return (
        <>
            {alertMessage && (
                <Alert severity={alertSeverity} sx={{ position: "fixed", width: "600px", height: "60px", right: "5%", top: "5%", fontSize: "16px", zIndex: "999999" }}>
                    {alertMessage}
                </Alert>
            )}
            <div className="text-[22px] mb-[15px] font-[600] ">
                {productDetail.title}
            </div>
            <div className="flex items-center pb-[5px] mb-[10px] border-b border-solid border-[#e4e4e4]">
                <div className="text-[14px] text-[#00090f]">Thương hiệu: <span className="text-primary">{productDetail.brand.title}</span></div>
                <div className="text-[14px] text-[#00090f] mx-[10px]">|</div>
                {currentVolume.stock > 0 ? (
                    <div className="text-[14px] text-[#00090f]">Tình trạng: <span className="text-primary">Còn hàng</span></div>

                ) : (
                    <div className="text-[14px] text-[#00090f]">Tình trạng: <span className="text-[#C5332D]">Hết hàng</span></div>
                )}
            </div>
            <div className="flex items-center">
                <div className="text-[32px] font-[500] text-[#cc2020] mr-[20px]">{(currentVolume.price * (1 - productDetail.discountPercent / 100)).toLocaleString('en-US')}<sup className="underline">đ</sup></div>
                {productDetail.discountPercent > 0 && (
                    <div className="text-[20px] font-[400] text-[#9f9f9f] mr-[15px] pt-[6px] line-through">{currentVolume.price.toLocaleString('en-US')}<sup className="underline">đ</sup></div>
                )}
                {productDetail.discountPercent > 0 && (
                    <div className="rounded-[3px] bg-primary px-[5px] py-[1px] text-[12px] min-w-[20px] text-white mt-[7px]">-{productDetail.discountPercent}%</div>
                )}
            </div>

            <div className="my-[5px]">
                <div className="text-[14px] text-[#0090F] mb-[10px]">Dung Tích: <span className="text-secondary font-[600]">{currentVolume.volume}{currentVolume.unit.toLowerCase()}</span></div>
                <div className="flex items-center">
                    {productDetail.variants.map((item: any, index: number) => (
                        <button
                            key={index}
                            className={"text-[14px] mr-[10px] rounded-[5px] min-w-[30px] h-[30px] cursor-pointer border boder-solid text-center p-[5px] " +
                                (item.volume === currentVolume.volume
                                    ? "bg-secondary border-[#ddd] text-white"
                                    : "border-[#e4e4e4] text-[#00090F]"
                                )
                            }
                            onClick={() => setCurrentVolume(item)}
                        >
                            {item.volume}{item.unit.toLowerCase()}
                        </button>
                    ))}
                </div>
            </div>

            <div className="my-[15px]">
                <div className="text-[14px] mb-[10px] text-textColor">Nguồn gốc: <span className="text-secondary font-[600]">{productDetail.origin}</span></div>
                <div className="text-[14px] mb-[10px] text-textColor">Dành cho: <span className="text-secondary font-[600]">{productDetail.skinIssues}</span></div>
            </div>

            {currentVolume.stock > 0 && (
                <div className="text-[14px] font-[500] text-[#00090f] my-[10px]">Số lượng:</div>
            )}
            {currentVolume.stock > 0 && (
                <div className="flex items-center">
                    <div className="mb-[10px] flex">
                        <button
                            className="hover:bg-primary hover:text-white text-[18px] w-[40px] h-[40px] text-[#333] flex justify-center items-center rounded-tl-[40px] rounded-bl-[40px] border border-solid border-[#ddd]"
                            onClick={handleClickDecrease}
                        >
                            -
                        </button>
                        <input
                            type="number"
                            name="quantity"
                            id="quantity"
                            value={quantity}
                            onChange={() => handleChange(event)}
                            className="text-[#00090f] text-[16px] block w-[60px] h-[40px] outline-none text-center border-y border-solid border-[#ddd]"
                        />
                        <button
                            className="hover:bg-primary hover:text-white text-[18px] w-[40px] h-[40px] text-[#333] flex justify-center items-center rounded-tr-[40px] rounded-br-[40px] border border-solid border-[#ddd]"
                            onClick={handleClickIncrease}>
                            +
                        </button>
                    </div>
                    <button
                        className="px-[40px] flex items-center bg-[#000] hover:bg-secondary rounded-[40px] mb-[10px] ml-[15px]"
                        onClick={handleAddNewProductToCart}
                    >
                        <CiShoppingCart className="text-white text-[24px]" />
                        <span className="ml-[5px] text-[12px] uppercase text-white font-[400] h-[40px] w-auto flex items-center">Thêm vào giỏ hàng</span>
                    </button>
                    <button onClick={handleBuyNow} className="buy-now uppercase text-[12px] text-white font-[400] h-[40px] w-auto rounded-[40px] mb-[10px] ml-[15px] px-[30px]">Mua ngay</button>

                    {isCompared == undefined && profile.firstName !== "" && (
                        <div onClick={() => handleAddCompare(productDetail.id)} className="cursor-pointer rounded-full border border-solid hover:border-primary hover:text-primary border-black p-[5px] mb-[10px] ml-[15px]">
                            <IoIosGitCompare className="w-[28px] h-[28px]" />
                        </div>
                    )}
                    {isCompared != undefined && profile.firstName !== "" && (
                        <div onClick={() => handleDelete(productDetail.id)} className="cursor-pointer text-white rounded-full bg-primary border border-solid hover:border-primary hover:bg-white hover:text-primary border-primary p-[5px] mb-[10px] ml-[15px]">
                            <IoIosGitCompare className="w-[28px] h-[28px]" />
                        </div>
                    )}
                    {profile.firstName == "" && (
                        <div onClick={() => { handleOpenPopup() }} className="cursor-pointer rounded-full border border-solid hover:border-primary hover:text-primary border-black p-[5px] mb-[10px] ml-[15px]">
                            <IoIosGitCompare className="w-[28px] h-[28px]" />
                        </div>
                    )}

                </div>
            )}

            {isPopupLoginOpen && (
                <div className="fixed inset-0 bg-black bg-opacity-50 z-[99999999] flex justify-center items-start pt-[8%]" onClick={handleClosePopup}>
                    <div className="container mx-auto w-[432px] bg-[#fff] p-[10px] rounded-[10px]" onClick={(e) => e.stopPropagation()}>
                        <Alert icon={false} severity="success">
                            Bạn cần đăng nhập để thực hiện chức năng này
                        </Alert>
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
                                <Alert style={{ marginBottom: "10px" }} severity={alert.severity}>{alert.content}</Alert>
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
                            <Link onClick={() => setIsPopupLoginOpen(false)} href="/user/register" className="text-[#333] text-[14px] hover:text-primary">Đăng ký tại đây</Link>
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
            )}
        </>
    )
}