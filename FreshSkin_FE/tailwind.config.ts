import type { Config } from "tailwindcss";

export default {
  content: [
    "./src/pages/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/components/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/app/**/*.{js,ts,jsx,tsx,mdx}",
  ],
  theme: {
    screens: {
      "md": "576px",
      "sm": "768px",
      "lg": "992px",
      "xl": "1287px",
      "2xl": "1287px"
    },
    extend: {
      colors: {
        primary: "#72a834",
        secondary: "#4E7661",
        textColor: "#00090f"
      },
    },
  },
  plugins: [],
} satisfies Config;
