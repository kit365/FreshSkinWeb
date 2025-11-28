"use client";

import { Inter } from "next/font/google";
import "../globals.css";
import { Provider } from "react-redux";
import { usePathname } from "next/navigation";
import { useMemo } from "react";
import Header from "../components/header/Header";
import Footer from "../components/footer/Footer";
import { store } from "../store";
import { createContext, useEffect, useState } from "react";
import { useAuth } from "../hooks/useAuth";

const inter = Inter({
  variable: "--font-inter",
  subsets: ["latin"],
});

interface Setting {
  websiteName: string;
  logo: string;
  phone: string;
  email: string;
  address: string;
  copyright: string;
  facebook: string;
  twitter: string;
  youtube: string;
  instagram: string;
  policy1: string;
  policy2: string;
  policy3: string;
  policy4: string;
  policy5: string;
  policy6: string;
  support1: string;
  support2: string;
  support3: string;
  support4: string;
  support5: string;
}

interface Profile {
  userID: number,
  address: string;
  avatar: string;
  createdAt: string;
  email: string;
  firstName: string;
  lastName: string;
  phone: string;
  username: string;
  orders: string[];
  skinType: string;
  productComparisonId: {
    id: number,
    products: any[]
  };
}

interface SettingProfile {
  setting: Setting;
  profile: Profile;
}

export const SettingProfileContext = createContext<SettingProfile | undefined>(undefined);

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  const { isAuthenticated, user, fetchProfile } = useAuth();

  const [setting, setSetting] = useState({
    websiteName: '',
    logo: '',
    phone: '',
    email: '',
    address: '',
    copyright: '',
    facebook: '',
    twitter: '',
    youtube: '',
    instagram: '',
    policy1: '',
    policy2: '',
    policy3: '',
    policy4: '',
    policy5: '',
    policy6: '',
    support1: '',
    support2: '',
    support3: '',
    support4: '',
    support5: ''
  });



  const pathname = usePathname();

  useEffect(() => {
    const fetchSettings = async () => {
      const response = await fetch('https://freshskinweb.onrender.com/setting/show');
      const data = await response.json();

      setSetting(data.data[0]);
    };

    fetchSettings();
  }, []);

  useEffect(() => {
    if (!pathname.startsWith("/user/login") && !pathname.startsWith("/user/register") && !pathname.startsWith("/user/otp")) {
      if (isAuthenticated && !user) {
        fetchProfile();
      }
    }
  }, [isAuthenticated, user]);

  const contextValue = useMemo(() => ({
    setting,
    profile: user ? {
      userID: user.userID,
      address: user.address || "",
      avatar: user.avatar?.[0] || "",
      createdAt: user.createdAt || "",
      email: user.email || "",
      firstName: user.firstName || "",
      lastName: user.lastName || "",
      phone: user.phone || "",
      username: user.username || "",
      orders: [], // Map from user.orders if needed
      skinType: user.skinType || "",
      productComparisonId: {
        id: user.productComparisonId?.id || 0,
        products: user.productComparisonId?.products || []
      }
    } : {
      userID: 0,
      address: '',
      avatar: '',
      createdAt: '',
      email: '',
      firstName: '',
      lastName: '',
      phone: '',
      username: '',
      orders: [],
      skinType: '',
      productComparisonId: {
        id: 0,
        products: []
      }
    }
  }), [setting, user]);

  return (
    <html lang="en">
      <body
        className={`${inter.className} antialiased`}
      >
        <SettingProfileContext.Provider value={contextValue}
        >
          <Provider store={store}>
            {!pathname.startsWith("/order") && pathname != "/quiz/result" && <Header />}
            {children}
            {!pathname.startsWith("/order") && pathname != "/quiz/result" && <Footer />}
          </Provider>
        </SettingProfileContext.Provider>
      </body>
    </html>
  );
}
