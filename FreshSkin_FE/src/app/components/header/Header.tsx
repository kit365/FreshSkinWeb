"use client"

import { useEffect, useState } from "react";
import AboveHeader from "./AboveHeader";
import Section1 from "./Section1";
import Section2 from "./Section2";


export default function Header() {
    const [isSticky, setIsSticky] = useState(false);

    const handleScroll = () => {
        const offset = window.scrollY;
        setIsSticky(offset > 0);
    };

    useEffect(() => {
        window.addEventListener('scroll', handleScroll);
        return () => {
            window.removeEventListener('scroll', handleScroll);
        };
    }, []);

    return (
        <>
            <AboveHeader />
            <header className={`sticky z-[999999] top-0 left-0 bg-white header ${isSticky ? "shadow" : ""}`}>                
                <Section1 />
                <Section2 />
            </header>
        </>
    );
}
