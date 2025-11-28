"use client"

import { useContext, useEffect, useState } from "react";
import { FaCaretDown } from "react-icons/fa6";
import FormInputCheckout from "@/app/components/Form/FormInputCheckout";
import TitleCheckout from "@/app/components/title/TitleCheckout";
import { FaMoneyBillAlt } from "react-icons/fa";
import { useDispatch, useSelector } from "react-redux";
import { sumShip, provinceChoosen } from "../../(actions)/order";
import { SettingProfileContext } from "../layout";

export default function FormOrder() {
    const [dataProvince, setDataProvince] = useState([]);
    const [districtId, setDistrictId] = useState(0);
    const [wardCode, setWardCode] = useState("");
    const [meeting, setMeeting] = useState(false);
    const [bank, setBank] = useState(false);
    const [dataDistrict, setDataDistrict] = useState([]);
    const [dataWard, setDataWard] = useState([]);
    const products = useSelector((state: any) => state.cartReducer.products);
    const feeShip = useSelector((state: any) => state.orderReducer.feeShip);
    const dispatchOrder = useDispatch();
    
    useEffect(() => {
        const fetchProvince = async () => {
            const response = await fetch('https://online-gateway.ghn.vn/shiip/public-api/master-data/province', {
                headers: {
                    "token": `c35280e3-11de-11f0-8fb0-9ee8abfecd65`
                }
            });
            const data = await response.json()
            setDataProvince(data.data);
        };

        fetchProvince();
    }, []);

    const settingProfile = useContext(SettingProfileContext);

    if (!settingProfile) {
        return null;
    }

    const { profile } = settingProfile;

    const handleChangeProvince = async (event: any) => {
        const provinceId = (event.target.value.split('+'))[2];
        const response = await fetch('https://online-gateway.ghn.vn/shiip/public-api/master-data/district', {
            method: "POST",
            headers: {
                'Content-Type': 'application/json',
                "token": `c35280e3-11de-11f0-8fb0-9ee8abfecd65`
            },
            body: JSON.stringify({
                "province_id": parseInt(provinceId)
            })
        });
        const data = await response.json();
        dispatchOrder(provinceChoosen(true));
        setDataDistrict(data.data);
    }

    const handleChangeDistrict = async (event: any) => {
        setDistrictId(parseInt((event.target.value.split('+'))[2]));
        const response = await fetch('https://online-gateway.ghn.vn/shiip/public-api/master-data/ward', {
            method: "POST",
            headers: {
                'Content-Type': 'application/json',
                "token": `c35280e3-11de-11f0-8fb0-9ee8abfecd65`
            },
            body: JSON.stringify({
                "district_id": parseInt((event.target.value.split('+'))[2])
            })
        });
        const data = await response.json();
        setDataWard(data.data);
    }

    const handleChangeWard = async (event: any) => {
        setWardCode((event.target.value.split('+'))[0]);

        const items: any = [];
        let weight: number = 0;
        for (const item of products){
            weight += item.volume
            const data = {
                name: item.title,
                quantity: item.quantity,
                weight: item.volume
            };
            items.push(data);
        }

        const response = await fetch('https://online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/fee', {
            method: "POST",
            headers: {
                'Content-Type': 'application/json',
                'Token': 'c35280e3-11de-11f0-8fb0-9ee8abfecd65',
                'ShopId': '5718994'
            },
            body: JSON.stringify({
                service_type_id: 2,
                from_district_id: 1542,
                from_ward_code: "21211",
                to_district_id: districtId,
                to_ward_code: wardCode,
                weight: weight,
                items: items
            })
        });
        const dataResponse = await response.json();
        dispatchOrder(sumShip(parseInt(dataResponse.data.total)))
    }

    const handleRadioMeetingChange = (event: any) => {
        if (event.target.checked) {
            setMeeting(true);
            setBank(false);
        }
    };

    const handleRadioBankChange = async (event: any) => {
        if (event.target.checked) {
            setBank(true);
            setMeeting(false);
        }
    };

    const handleClickBank = () => {
        setBank(true);
        setMeeting(false);
    }

    const handleClickMetting = () => {
        setMeeting(true);
        setBank(false);
    }

    return (
        <>
            <div className="w-full grid grid-cols-2 gap-[28px]">
                <div className="">
                    <TitleCheckout text="Thông tin nhận hàng" />
                    {profile.email !== "" ? (
                        <FormInputCheckout label="Email" type="email" name="email" id="email" value={profile.email} />
                    ) : (
                        <FormInputCheckout label="Email" type="email" name="email" id="email" />
                    )}
                    {profile.firstName !== "" ? (
                        <FormInputCheckout label="Họ" name="firstName" id="firstName" value={profile.firstName} />
                    ) : (
                        <FormInputCheckout label="Họ" name="firstName" id="firstName" />
                    )}
                    {profile.lastName !== "" ? (
                        <FormInputCheckout label="Tên" name="lastName" id="lastName" value={profile.lastName} />
                    ) : (
                        <FormInputCheckout label="Tên" name="lastName" id="lastName" />
                    )}
                    {profile.phone !== "" ? (
                        <FormInputCheckout label="Số điện thoại" name="phone" id="phone" value={profile.phone} />
                    ) : (
                        <FormInputCheckout label="Số điện thoại" name="phone" id="phone" />
                    )}
                    {profile.address !== "" ? (
                        <FormInputCheckout label="Địa chỉ" name="address" id="address" value={profile.address} />
                    ) : (
                        <FormInputCheckout label="Địa chỉ" name="address" id="address" />
                    )}
                    <div className="relative">
                        <label htmlFor="province" className="text-[13px] font-[300] text-[#999] absolute top-[5px] left-[11px]">Tỉnh thành</label>
                        <select
                            name="province"
                            id="province"
                            className="pt-[16px] pb-[2px] px-[11px] w-full h-[44px] bg-white text-[#333] rounded-[4px] border border-solid border-[#d9d9d9] focus:border-[#72a834] outline-none input-checkout mb-[10px] text-[14px] font-[450] cursor-pointer appearance-none"
                            onChange={handleChangeProvince}
                        >
                            <option value="">---</option>
                            {dataProvince && dataProvince.length > 0 && dataProvince.map((item: any, index: number) => (
                                <option key={index} value={`${item.Code}+${item.ProvinceName}+${item.ProvinceID}`}>{item.ProvinceName}</option>
                            ))}
                        </select>
                        <label htmlFor="province">
                            <FaCaretDown className="cursor-pointer text-[#878787] pl-[10px] py-[2px] text-[20px] absolute right-[11px] top-[12px] border-l border-solid border-[#D9D9D9]" />
                        </label>
                    </div>
                    <div className="relative">
                        <label htmlFor="district" className="text-[13px] font-[300] text-[#999] absolute top-[5px] left-[11px]">Quận huyện</label>
                        <select
                            name="district"
                            id="district"
                            className={"pt-[16px] pb-[2px] px-[11px] w-full h-[44px] text-[#333] rounded-[4px] border border-solid border-[#d9d9d9] focus:border-[#72a834] outline-none input-checkout mb-[10px] text-[14px] font-[450] cursor-pointer appearance-none " + (dataDistrict?.length > 0 ? "bg-white" : "bg-[#EEEEEE]")}
                            onChange={handleChangeDistrict}
                            disabled={(dataDistrict?.length > 0 ? false : true)}
                        >
                            <option value="">---</option>
                            {dataDistrict && dataDistrict.length > 0 && dataDistrict.map((item: any, index: number) => (
                                <option key={index} value={`${item.Code}+${item.DistrictName}+${item.DistrictID}`}>{item.DistrictName}</option>
                            ))}
                        </select>
                        <label htmlFor="district">
                            <FaCaretDown className="cursor-pointer text-[#878787] pl-[10px] py-[2px] text-[20px] absolute right-[11px] top-[12px] border-l border-solid border-[#D9D9D9]" />
                        </label>
                    </div>
                    <div className="relative">
                        <label htmlFor="ward" className="text-[13px] font-[300] text-[#999] absolute top-[5px] left-[11px]">Phường xã</label>
                        <select
                            name="ward"
                            id="ward"
                            onChange={handleChangeWard}
                            className={"pt-[16px] pb-[2px] px-[11px] w-full h-[44px] text-[#333] rounded-[4px] border border-solid border-[#d9d9d9] focus:border-[#72a834] outline-none input-checkout mb-[10px] text-[14px] font-[450] cursor-pointer appearance-none " + (dataWard?.length > 0 ? "bg-white" : "bg-[#EEEEEE]")}
                            disabled={(dataWard?.length > 0 ? false : true)}
                        >
                            <option value="">---</option>
                            {dataWard && dataWard.map((item: any, index: number) => (
                                <option key={index} value={`${item.WardCode}+${item.WardName}`}>{item.WardName}</option>
                            ))}
                        </select>
                        <label htmlFor="ward">
                            <FaCaretDown className="cursor-pointer text-[#878787] pl-[10px] py-[2px] text-[20px] absolute right-[11px] top-[12px] border-l border-solid border-[#D9D9D9]" />
                        </label>
                    </div>
                </div>
                <div>
                    <TitleCheckout text="Vận chuyển" />
                    {dataDistrict?.length <= 0 ? (
                        <input
                            readOnly
                            placeholder="Vui lòng nhập thông tin giao hàng"
                            className="py-[10px] px-[18px] w-full h-[44px] bg-[#d1ecf1] placeholder:text-[#0c5460] rounded-[4px] border border-solid border-[#bee5eb] outline-none mb-[30px] text-[14px] font-[450]"
                        />
                    ) : (
                        <div className="relative">
                            <input
                                readOnly
                                placeholder="Giao hàng tận nơi"
                                className="py-[10px] px-[45px] w-full h-[44px] bg-white rounded-[4px] border border-solid outline-none mb-[30px] text-[14px] placeholder:text-[#545454] font-[450] cursor-pointer"
                            />
                            <span className="cursor-pointer absolute left-[15px] top-[12px] w-[18px] aspect-square rounded-[50%] bg-[#3072AC]"></span>
                            <span className="text-[14px] font-[450] text-[#545454] absolute top-[10px] right-[15px]">{feeShip.toLocaleString("en-US")}<sup className="underline">đ</sup></span>
                        </div>
                    )}
                    <TitleCheckout text="Thanh toán" />
                    <div className="relative">
                        <div onClick={() => handleClickBank()}>
                            <input
                                readOnly
                                placeholder="Chuyển khoản"
                                className="py-[30px] px-[45px] w-full h-[44px] bg-white rounded-[4px] rounded-bl-none rounded-br-none border border-solid outline-none text-[14px] placeholder:text-[#545454] font-[450] cursor-pointer"
                            />
                        </div>
                        <input type="radio" className="hidden" id="bank" name="method" value="QR" checked={bank} onChange={handleRadioBankChange} />
                        {bank ? (
                            <label htmlFor="bank" className="cursor-pointer absolute left-[15px] top-[22px] w-[18px] aspect-square rounded-[50%] bg-[#3072AC]"></label>
                        ) : (
                            <label htmlFor="bank" className="cursor-pointer absolute left-[15px] top-[22px] w-[18px] aspect-square rounded-[50%] border border-solid border-[#d9d9d9]"></label>
                        )}
                        <FaMoneyBillAlt className="absolute top-[16px] right-[30px] text-[30px] text-[#1990c6]" />
                    </div>
                    <div className="relative">
                        <div onClick={() => handleClickMetting()}>
                            <input
                                readOnly
                                placeholder="Thanh toán khi nhận hàng"
                                className="py-[30px] px-[45px] w-full h-[44px] bg-white rounded-[4px] rounded-tl-none rounded-tr-none border border-solid border-t-0 outline-none text-[14px] placeholder:text-[#545454] font-[450] cursor-pointer"
                            />
                        </div>
                        <input type="radio" className="hidden" id="meeting" name="method" value="CASH" checked={meeting} onChange={handleRadioMeetingChange} />
                        {meeting ? (
                            <label htmlFor="meeting" className="cursor-pointer absolute left-[15px] top-[22px] w-[18px] aspect-square rounded-[50%] bg-[#3072AC]"></label>
                        ) : (
                            <label htmlFor="meeting" className="cursor-pointer absolute left-[15px] top-[22px] w-[18px] aspect-square rounded-[50%] border border-solid border-[#d9d9d9]"></label>
                        )}
                        <FaMoneyBillAlt className="absolute top-[16px] right-[30px] text-[30px] text-[#1990c6]" />
                    </div>
                </div>
            </div>
        </>
    )
}