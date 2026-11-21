import Link from "next/link";
import { Navbar } from "@/components/Navbar";
import { Footer } from "@/components/FaqAndFooter";
import { DOWNLOAD_CONFIG } from "@/config/downloadConfig";

export default function PrivacyPolicyPage() {
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
            PRIVACY POLICY
          </h1>
          <div className="dark-card mt-6 space-y-5 rounded-2xl p-6 text-sm leading-relaxed text-lavender">
            <p>
              This website serves solely as the official informational and Android APK download portal for <strong>{DOWNLOAD_CONFIG.appName}</strong>.
            </p>
            <div>
              <h2 className="text-base font-bold text-white">No Mandatory Visitor Data Collection</h2>
              <p className="mt-1">
                Visitors are not required to register, log in, or submit personal contact information to view this website or download the official Android APK package.
              </p>
            </div>
            <div>
              <h2 className="text-base font-bold text-white">External APK Hosting Links</h2>
              <p className="mt-1">
                Downloading the Android application connects over HTTPS to the official release package configured by the publisher. Standard web server access logs may record technical request metadata as required for delivery and security.
              </p>
            </div>
            <div>
              <h2 className="text-base font-bold text-white">Contact</h2>
              <p className="mt-1">
                For privacy inquiries regarding this download website, contact:{" "}
                <span className="font-mono text-gold-bright">
                  {DOWNLOAD_CONFIG.contactEmailPlaceholder}
                </span>
              </p>
            </div>
          </div>
        </div>
      </main>
      <Footer />
    </div>
  );
}
