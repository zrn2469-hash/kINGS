import type { Config } from "tailwindcss";

const config: Config = {
  content: [
    "./src/app/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/components/**/*.{js,ts,jsx,tsx,mdx}"
  ],
  theme: {
    extend: {
      colors: {
        midnight: "#07060B",
        plum: "#100B1B",
        plumCard: "#130E22",
        gold: {
          bright: "#E7C347",
          metallic: "#C49B26"
        },
        lavender: "#B4ACC8",
        royal: {
          purple: "#713BFF",
          soft: "#B294FF"
        }
      },
      fontFamily: {
        display: ["var(--font-cinzel)", "Georgia", "serif"],
        sans: ["var(--font-jakarta)", "system-ui", "sans-serif"],
        mono: ["var(--font-jetbrains)", "ui-monospace", "monospace"]
      },
      boxShadow: {
        goldGlow: "0 16px 45px rgba(231, 195, 71, 0.16)",
        purpleGlow: "0 0 90px rgba(113, 59, 255, 0.18)"
      }
    }
  },
  plugins: []
};

export default config;
