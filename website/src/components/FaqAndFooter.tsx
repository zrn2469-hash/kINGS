"use client";

import { useState } from "react";
import Link from "next/link";
import { DOWNLOAD_CONFIG } from "@/config/downloadConfig";
import { CrownIcon } from "./CrownIcon";

const FAQS = [
  {
    q: "What is KINGS EARNS?",
    a: "KINGS EARNS is an Android application designed with a simple interface, easy navigation, and organized daily modules."
  },
  {
    q: "How can I download KINGS EARNS?",
    a: "Click the DOWNLOAD APP or DOWNLOAD KINGS EARNS FOR ANDROID button on this official website."
  },
  {
    q: "Is KINGS EARNS available for Android?",
    a: `Yes. KINGS EARNS supports devices running ${DOWNLOAD_CONFIG.supportedAndroidVersion}.`
  },
  {
    q: "How do I install the APK?",
    a: "Save the APK from this official website, tap the downloaded file, and follow Android's on-screen installation prompts."
  },
  {
    q: "Where can I download the latest version?",
    a: "The latest official Android build and version notes are always published in the Download section of this website."
  },
  {
    q: "How can I contact support?",
    a: `Reach out using the official contact details listed in the footer (${DOWNLOAD_CONFIG.contactEmailPlaceholder}).`
  }
];

export function FaqSection() {
  const [openIndex, setOpenIndex] = useState<number | null>(0);

  return (
    <section id="faq" className="border-t border-white/[0.06] py-16 md:py-20">
      <div className="mx-auto max-w-4xl px-5">
        <div className="mb-10 text-center">
          <h2 className="text-3xl font-extrabold tracking-tight text-white">
            FREQUENTLY ASKED <span className="gold-gradient-text">QUESTIONS</span>
          </h2>
        </div>

        <div className="dark-card divide-y divide-white/[0.08] rounded-2xl px-6">
          {FAQS.map((item, index) => {
            const isOpen = openIndex === index;
            return (
              <div key={item.q}>
                <button
                  type="button"
                  onClick={() => setOpenIndex(isOpen ? null : index)}
                  aria-expanded={isOpen}
                  className="flex w-full items-center justify-between gap-4 py-5 text-left text-base font-bold text-white transition-colors hover:text-gold-bright"
                >
                  <span>{item.q}</span>
                  <svg
                    className={`h-5 w-5 shrink-0 text-gold-bright transition-transform ${
                      isOpen ? "rotate-180" : ""
                    }`}
                    fill="none"
                    stroke="currentColor"
                    strokeWidth={2}
                    viewBox="0 0 24 24"
                  >
                    <path strokeLinecap="round" strokeLinejoin="round" d="M19 9l-7 7-7-7" />
                  </svg>
                </button>
                {isOpen && (
                  <p className="pb-5 text-sm leading-relaxed text-lavender">{item.a}</p>
                )}
              </div>
            );
          })}
        </div>
      </div>
    </section>
  );
}

export function Footer() {
  return (
    <footer className="border-t border-white/[0.08] bg-midnight py-12">
      <div className="mx-auto max-w-6xl px-5">
        <div className="flex flex-col gap-8 md:flex-row md:items-start md:justify-between">
          <div className="max-w-sm">
            <div className="flex items-center gap-2.5">
              <CrownIcon className="h-5 w-5 text-gold-bright" />
              <span className="text-base font-extrabold tracking-wide text-white">
                KINGS EARNS
              </span>
            </div>
            <p className="mt-3 text-xs leading-relaxed text-lavender/80">
              Official Android application download website for KINGS EARNS.
            </p>
          </div>

          <div className="flex flex-wrap items-center gap-6 text-sm text-lavender">
            <Link href="/#download" className="font-semibold text-gold-bright hover:underline">
              Download App
            </Link>
            <Link href="/#about" className="hover:text-gold-bright">
              About App
            </Link>
            <Link href="/privacy" className="hover:text-gold-bright">
              Privacy Policy
            </Link>
            <Link href="/terms" className="hover:text-gold-bright">
              Terms and Conditions
            </Link>
            <span className="text-lavender/75">
              Contact: <span className="font-mono text-xs text-lavender">{DOWNLOAD_CONFIG.contactEmailPlaceholder}</span>
            </span>
          </div>
        </div>

        <div className="mt-8 flex flex-col gap-2 border-t border-white/[0.08] pt-6 text-xs text-lavender/70 sm:flex-row sm:items-center sm:justify-between">
          <span>&copy; {new Date().getFullYear()} KINGS EARNS. All rights reserved.</span>
          <span className="font-mono">Package: {DOWNLOAD_CONFIG.packageName}</span>
        </div>
      </div>
    </footer>
  );
}
