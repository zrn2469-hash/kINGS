import type { Metadata } from "next";
import { Cinzel, JetBrains_Mono, Plus_Jakarta_Sans } from "next/font/google";
import "./globals.css";

const cinzel = Cinzel({
  subsets: ["latin"],
  weight: ["600", "700", "800"],
  variable: "--font-cinzel"
});

const plusJakarta = Plus_Jakarta_Sans({
  subsets: ["latin"],
  weight: ["400", "500", "600", "700", "800"],
  variable: "--font-jakarta"
});

const jetbrainsMono = JetBrains_Mono({
  subsets: ["latin"],
  weight: ["500", "700"],
  variable: "--font-jetbrains"
});

export const metadata: Metadata = {
  title: "KINGS EARNS — WATCH. EARN. REWARD. | Official Android App Download",
  description:
    "Discover the KINGS EARNS mobile experience. Download the official KINGS EARNS Android application and explore its features.",
  icons: {
    icon: "/images/kings-earns-icon.jpg",
    apple: "/images/kings-earns-icon.jpg"
  },
  openGraph: {
    title: "KINGS EARNS — Official Android App",
    description:
      "Discover the KINGS EARNS mobile experience. Download the official Android APK.",
    type: "website"
  }
};

export default function RootLayout({
  children
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html
      lang="en"
      className={`${cinzel.variable} ${plusJakarta.variable} ${jetbrainsMono.variable}`}
    >
      <body className="font-sans text-white antialiased selection:bg-gold-bright selection:text-midnight">
        {children}
      </body>
    </html>
  );
}
