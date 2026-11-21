import Link from "next/link";
import { Navbar } from "@/components/Navbar";
import { Footer } from "@/components/FaqAndFooter";
import { DOWNLOAD_CONFIG } from "@/config/downloadConfig";

export default function TermsPage() {
  return (
    <div className="min-h-screen flex flex-col">
      <Navbar />
      <main className="flex-1 py-16">
        <div className="mx-auto max-w-3xl px-5">
          <Link
            href="/"
            className="text-xs font-bold uppercase tracking-wider text-gold-bright hover:underline"
          >
            &larr; Back to Home
          </Link>
          <h1 className="mt-4 text-3xl font-extrabold text-gold-bright">
            TERMS AND CONDITIONS
          </h1>
          <div className="dark-card mt-6 space-y-5 rounded-2xl p-6 text-sm leading-relaxed text-lavender">
            <p>
              By accessing this official download website for <strong>{DOWNLOAD_CONFIG.appName}</strong>, you agree to the terms outlined below.
            </p>
            <div>
              <h2 className="text-base font-bold text-white">Official Software Distribution</h2>
              <p className="mt-1">
                This website distributes only the unmodified official Android APK (<span className="font-mono">{DOWNLOAD_CONFIG.packageName}</span>) provided by the publisher. Redistributing modified or tampered APK files is strictly prohibited.
              </p>
            </div>
            <div>
              <h2 className="text-base font-bold text-white">Device Compatibility</h2>
              <p className="mt-1">
                Installation requires {DOWNLOAD_CONFIG.supportedAndroidVersion}. Visitors should verify that they are downloading directly from this HTTPS domain before installing.
              </p>
            </div>
            <div>
              <h2 className="text-base font-bold text-white">Updates</h2>
              <p className="mt-1">
                Release version notes, compatibility details, and download links may be updated when new official Android builds are released.
              </p>
            </div>
          </div>
        </div>
      </main>
      <Footer />
    </div>
  );
}
