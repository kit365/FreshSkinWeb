"use client";
import { useContext, useState } from "react";
import { ProfileAdminContext } from "../../layout";
import Cookies from "js-cookie";
import Alert from "@mui/material/Alert";
interface Profile {
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
}

export default function EditProfileAdmin() {
  const dataProfile = useContext(ProfileAdminContext);
  const [alertMessage, setAlertMessage] = useState<string>("");
  const [alertSeverity, setAlertSeverity] = useState<
    "success" | "error" | "info" | "warning"
  >("info");
  const [formData, setFormData] = useState<Profile>({
    firstName: dataProfile?.firstName ?? "",
    lastName: dataProfile?.lastName ?? "",
    email: dataProfile?.email ?? "",
    phone: dataProfile?.phone ?? "",
  });
  

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    try {
      const token = Cookies.get("token");
      const response = await fetch(
        "https://freshskinweb.onrender.com/auth/updateUser",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            token: token,
            firstName: formData.firstName,
            lastName: formData.lastName,
            email: formData.email,
            phone: formData.phone,
          }),
        }
      );

      const dataResponse = await response.json();
      if (dataResponse.code === 200) {
        setAlertMessage(dataResponse.message);
        setAlertSeverity("success");
        setTimeout(() => location.reload(), 2000);
    } else {
        setAlertMessage(dataResponse.message);
        setAlertSeverity("error");
    }
    } catch (error) {
      console.error("Lỗi khi cập nhật thông tin:", error);
      alert("Có lỗi xảy ra, vui lòng thử lại sau.");
    }
  };
  return (
    <>
    {
    alertMessage && (
      <Alert severity={alertSeverity} sx={{ mb: 2 }}>
        {alertMessage}
      </Alert>
    )
  }

    <div className="p-4 bg-white shadow rounded-lg max-w-md mx-auto">
      <h2 className="text-xl font-bold mb-4">Chỉnh sửa thông tin cá nhân</h2>
      <form onSubmit={handleSubmit}>
        <div className="mb-4">
          <label className="block text-sm font-medium text-gray-700">Họ</label>
          <input
            type="text"
            name="firstName"
            value={formData.firstName}
            onChange={handleChange}
            className="mt-1 p-2 w-full border rounded-lg"
          />
        </div>
        <div className="mb-4">
          <label className="block text-sm font-medium text-gray-700">Tên</label>
          <input
            type="text"
            name="lastName"
            value={formData.lastName}
            onChange={handleChange}
            className="mt-1 p-2 w-full border rounded-lg"
          />
        </div>
        <div className="mb-4">
          <label className="block text-sm font-medium text-gray-700">
            Email
          </label>
          <input
            type="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            className="mt-1 p-2 w-full border rounded-lg"
          />
        </div>
        <div className="mb-4">
          <label className="block text-sm font-medium text-gray-700">
            Số điện thoại
          </label>
          <input
            type="text"
            name="phone"
            value={formData.phone}
            onChange={handleChange}
            className="mt-1 p-2 w-full border rounded-lg"
          />
        </div>
        <button
          type="submit"
          className="bg-blue-500 text-white p-2 rounded-lg w-full hover:bg-blue-600"
        >
          Lưu thay đổi
        </button>
      </form>
    </div>
    </>
  );

}
