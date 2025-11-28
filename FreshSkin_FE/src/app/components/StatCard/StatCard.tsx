"use client"

import { ReactNode } from "react";

interface StatCardProps {
  value: string | number;
  label: string;
  icon: ReactNode;
  onClick?: () => void;  
  style?: React.CSSProperties;  
}

export function StatCard({ value, label, icon, onClick, style }: StatCardProps) {
  return (
    <div 
      className="flex items-center bg-white shadow-md rounded-lg p-4 w-55 cursor-pointer"
      onClick={onClick} 
      style={style}
    >
      <div className="text-2xl">{value}</div>
      <div className="ml-2">
        <div className="text-gray-500 text-sm">{label}</div>
      </div>
      <div className="ml-auto text-gray-600 text-2xl">{icon}</div>
    </div>
  );
}
