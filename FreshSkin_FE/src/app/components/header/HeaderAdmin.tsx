"use client"

import { useEffect, useState, useContext, useRef } from "react";
import { FaBell } from "react-icons/fa";
import { IoSearch } from "react-icons/io5";
import { FaArrowRightFromBracket } from "react-icons/fa6";
import Cookies from "js-cookie";
import Link from "next/link";
import { ProfileAdminContext } from "@/app/admin/layout";
import { Trash2 } from "lucide-react";
import dayjs from "dayjs";
import relativeTime from "dayjs/plugin/relativeTime";
import "dayjs/locale/vi";
import utc from "dayjs/plugin/utc";
import timezone from "dayjs/plugin/timezone";
import { useRouter } from "next/navigation";

dayjs.extend(utc);
dayjs.extend(timezone);
dayjs.extend(relativeTime);
dayjs.locale("vi");

interface Notification {
  id: number;
  message: string;
  time: string;
  isRead: boolean;
  image: string;
  slugProduct?: string;
}

export default function HeaderAdmin() {
  const [unreadCount, setUnreadCount] = useState<number>(0);
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [isDropdownOpen, setIsDropdownOpen] = useState<boolean>(false);
  const dataProfile = useContext(ProfileAdminContext);
  const wsRef = useRef<WebSocket | null>(null);
  const router = useRouter();
  // 🚀 Fetch danh sách thông báo từ API
  const fetchNotifications = async () => {
    if (!dataProfile || !dataProfile.roleId) return;
  
    const roleId = dataProfile.roleId;
  
    try {
      const res = await fetch(`https://freshskinweb.onrender.com/admin/notify/${roleId}`);

      let data: Notification[] = [];
      try {
        data = await res.json();
      } catch (err) {
        console.error("Lỗi parse JSON:", err);
        return;
      }

      if (!Array.isArray(data)) {
        console.warn("Dữ liệu trả về không phải là danh sách:", data);
        return;
      }

      setNotifications(data);
      setUnreadCount(data.filter((n) => !n.isRead).length);
    } catch (error) {
      console.error("Lỗi khi fetch thông báo:", error);
    }
  
  };

  
  useEffect(() => {
    if (dataProfile?.roleId) {
      fetchNotifications();
    }
  }, [dataProfile]);

  useEffect(() => {
    function connectWebSocket() {
      if (wsRef.current) return;

      const ws = new WebSocket("wss://freshskinweb.onrender.com/ws/notify");

      ws.onopen = () => {
        console.log(" WebSocket đã kết nối!");
        wsRef.current = ws;
      };
      ws.onmessage = (event) => {
        console.log(" Nhận thông báo:", event.data);
        const data: Notification = JSON.parse(event.data);
        if (!data.id || !data.message) return;
        setNotifications((prev) => [data, ...prev]);
        setUnreadCount((prev) => prev + 1);
      };

      ws.onclose = () => {
        wsRef.current = null;
        setTimeout(connectWebSocket, 5000);
      };
    }

    connectWebSocket();

    return () => {
      if (wsRef.current) {
        wsRef.current.close();
        wsRef.current = null;
      }
    };
  }, []);

  const handleClickLogout = () => {
    Cookies.remove("token");
    location.href = "/admin/auth/login";
  };

  const toggleDropdown = () => {
    setIsDropdownOpen(!isDropdownOpen);
  };

  const markAsRead = async (id?: number, slugProduct?: string) => {
    if (!id) {
      return;
    }

    await fetch(`https://freshskinweb.onrender.com/admin/notify/update/${id}`, {
      method: "GET",
    });

    setNotifications((prev: Notification[]) =>
      prev.map((n: Notification) => (n.id === id ? { ...n, isRead: true } : n))
    );
    setUnreadCount((prev: number) => Math.max(prev - 1, 0));

    if (slugProduct) {
      // Điều hướng đến sản phẩm
      router.push(`/detail/${slugProduct}`);

      setTimeout(() => {
        const ratingSection = document.getElementById("rating-section");
        if (ratingSection) {
          ratingSection.scrollIntoView({ behavior: "smooth" });
        }
      }, 500);
    }
  };

  const removeNotification = async (id?: number, e?: React.MouseEvent) => {
    if (e) e.stopPropagation();
    if (!id) {
      return;
    }

    const response = await fetch(
      `https://freshskinweb.onrender.com/admin/notify/delete/${id}`,
      {
        method: "DELETE",
      }
    );

    if (response.ok) {
      setNotifications((prev: Notification[]) =>
        prev.filter((n: Notification) => n.id !== id)
      );
      setUnreadCount((prev) => (prev > 0 ? prev - 1 : 0));
    }
  };

  const clearAllNotifications = async () => {
    // 🔍 Lọc danh sách ID thông báo đã đọc
    const readNotificationIds =
      notifications?.filter((n) => n.isRead)?.map((n) => n.id) || [];

    if (readNotificationIds.length === 0) {
      alert("Không có thông báo đã đọc để xóa!");
      return;
    }

    // Gọi API xóa thông báo đã đọc
    const response = await fetch(
      `https://freshskinweb.onrender.com/admin/notify/deleteAll/${dataProfile?.roleId}`,
      {
        method: "DELETE",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ notificationIds: readNotificationIds }),
      }
    );

    // Kiểm tra response trước khi gọi .json()
    let responseData = null;
    if (response.status !== 204) {
      responseData = await response.json().catch(() => null);
    }

    console.log("Phản hồi từ server:", responseData);

    if (response.ok) {
      // 🏷️ Cập nhật lại danh sách thông báo (loại bỏ thông báo đã đọc)
      setNotifications((prev) => prev.filter((n) => !n.isRead));
      setUnreadCount((prev) => prev - readNotificationIds.length);
    }
  };

  const formatRelativeTime = (timestamp: string): string => {
    const time = dayjs.utc(timestamp).tz("Asia/Ho_Chi_Minh"); // Chuyển timestamp về múi giờ VN
    const now = dayjs();

    if (now.diff(time, "minute") < 1) return "Vừa xong";
    if (now.diff(time, "hour") < 1)
      return `${now.diff(time, "minute")} phút trước`;
    if (now.diff(time, "day") < 1) return `${now.diff(time, "hour")} giờ trước`;
    if (now.diff(time, "day") === 1) return "Hôm qua";

    return time.format("DD/MM/YYYY");
  };

  return (
    <div className="flex items-center justify-between bg-white p-4 w-full">
      <div className="relative w-80">
        <input
          type="text"
          placeholder="Search"
          className="w-full pl-10 pr-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-green-400"
        />
        <IoSearch className="absolute left-3 top-3 text-gray-500" size={18} />
      </div>

      <div className="flex items-center space-x-6">
        {/*  Chuông thông báo */}
        <div className="relative">
          <FaBell
            className={`text-gray-600 text-[20px] cursor-pointer hover:text-green-400 ${
              unreadCount > 0 ? "animate-bounce text-red-500" : ""
            }`}
            onClick={toggleDropdown}
          />
          {unreadCount > 0 && (
            <span className="absolute top-0 right-0 w-3 h-3 bg-red-500 rounded-full text-white text-xs flex items-center justify-center">
              {unreadCount}
            </span>
          )}
        </div>

        {/*   Dropdown thông báo */}
        {isDropdownOpen && (
          <div className="absolute right-5 top-14 w-96 bg-white shadow-lg rounded-md overflow-hidden border border-gray-300 z-50">
            <div className="px-4 py-2 flex justify-between items-center bg-gray-100">
              <span className="font-semibold">Thông báo</span>
              {notifications.length > 0 && (
                <button
                  onClick={clearAllNotifications}
                  className="text-red-500 text-sm hover:underline"
                >
                  Xóa tất cả
                </button>
              )}
            </div>
            {notifications.length === 0 ? (
              <div className="p-4 text-gray-500 text-center">
                Không có thông báo
              </div>
            ) : (
              <ul className="max-h-60 overflow-y-auto">
                {notifications.map((notification, index) => (
                  <li
                    key={notification.id || `notification-${index}`}
                    className={`px-4 py-2 border-b flex justify-between items-center hover:bg-gray-100 cursor-pointer ${
                      notification.isRead ? "" : "bg-gray-200"
                    }`}
                    onClick={() =>
                      markAsRead(notification.id, notification.slugProduct)
                    }
                  >
                    <img
                      src={notification.image || "https://png.pngtree.com/png-vector/20190228/ourmid/pngtree-check-mark-icon-design-template-vector-isolated-png-image_711429.jpg"}
                      className="w-12 h-12 rounded-lg object-cover mr-3"
                      alt="Product Image"
                    />

                    <div className="flex items-center space-x-2">
                      <div>
                        <p className="text-sm">{notification.message}</p>
                        <p className="text-xs text-gray-500">
                          {formatRelativeTime(notification.time)}
                        </p>
                      </div>
                    </div>

                    {/*  Nút xóa thông báo + Dấu chấm xanh */}
                    <div className="relative">
                      {!notification.isRead && (
                        <span className="absolute -top-5 -right-0 w-2 h-2 bg-blue-500 rounded-full"></span>
                      )}
                      <button
                        onClick={(e) => {
                          e.stopPropagation();
                          removeNotification(notification.id);
                        }}
                        className="text-red-500 hover:text-red-700"
                      >
                        <Trash2 className="w-4 h-4" />
                      </button>
                    </div>
                  </li>
                ))}
              </ul>
            )}
          </div>
        )}

        {/* 👤 Avatar Admin */}
        {dataProfile?.avatar && (
          <Link href="/admin/profile" className="w-[40px] aspect-square">
            <img
              src={dataProfile.avatar}
              className="w-full h-full object-cover rounded-full"
              alt="Avatar"
            />
          </Link>
        )}

        {/* 🚪 Nút Đăng xuất */}
        <span onClick={handleClickLogout} className="cursor-pointer">
          <FaArrowRightFromBracket className="text-[#6D7587] text-[20px] hover:text-red-500" />
        </span>
      </div>
    </div>
  );
}
