import { Navbar } from "@/components/Navbar";
import { HeroSection } from "@/components/HeroSection";
import { AboutAndFeaturesSection } from "@/components/AboutAndFeaturesSection";
import { ScreenshotsSection } from "@/components/ScreenshotsSection";
import { HowToAndDownloadSection } from "@/components/HowToAndDownloadSection";
import { FaqSection, Footer } from "@/components/FaqAndFooter";

export default function HomePage() {
  return (
    <div className="min-h-screen flex flex-col">
      <Navbar />
      <main className="flex-1">
        <HeroSection />
        <AboutAndFeaturesSection />
        <ScreenshotsSection />
        <HowToAndDownloadSection />
        <FaqSection />
      </main>
      <Footer />
    </div>
  );
}
