"use client";

import { useState } from "react";
import Link from "next/link";
import { CrownIcon } from "./CrownIcon";

const NAV_ITEMS = [
  { label: "Home", href: "/#home" },
  { label: "About App", href: "/#about" },
  { label: "Screenshots", href: "/#screenshots" },
  { label: "Download", href: "/#download" }
];

export function Navbar() {
  const [mobileOpen, setMobileOpen] = useState(false);

  return (
    <header className="sticky top-0 z-50 border-b border-white/[0.08] bg-midnight/95 backdrop-blur-md">
      <div className="mx-auto flex h-[72px] max-w-6xl items-center justify-between px-5">
        <Link href="/#home" className="flex items-center gap-2.5">
          <CrownIcon className="h-5 w-5 text-gold-bright" />
          <span className="text-base font-extrabold tracking-wide text-white sm:text-lg">
            KINGS EARNS
          </span>
        </Link>

        <nav className="hidden items-center gap-8 md:flex" aria-label="Primary Navigation">
          {NAV_ITEMS.map((item) => (
            <a
              key={item.href}
              href={item.href}
              className="text-sm font-medium text-lavender transition-colors hover:text-gold-bright"
            >
              {item.label}
            </a>
          ))}
        </nav>

        <div className="flex items-center gap-3">
          <a
            href="/#download"
            className="gold-gradient-button inline-flex min-h-[42px] items-center justify-center rounded-xl px-5 py-2 text-sm font-bold text-midnight shadow-goldGlow transition-transform hover:-translate-y-0.5"
          >
            Download App
          </a>

          <button
            type="button"
            onClick={() => setMobileOpen((prev) => !prev)}
            aria-label="Toggle navigation menu"
            aria-expanded={mobileOpen}
            className="inline-flex h-10 w-10 items-center justify-center rounded-xl border border-white/10 bg-plum text-white md:hidden"
          >
            <svg className="h-5 w-5" fill="none" stroke="currentColor" strokeWidth={2} viewBox="0 0 24 24">
              {mobileOpen ? (
                <path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" />
              ) : (
                <path strokeLinecap="round" strokeLinejoin="round" d="M4 6h16M4 12h16M4 18h16" />
              )}
            </svg>
          </button>
        </div>
      </div>

      {mobileOpen && (
        <nav
          className="border-t border-white/[0.08] bg-plum px-5 py-4 md:hidden"
          aria-label="Mobile Navigation"
        >
          <div className="flex flex-col gap-2.5">
            {NAV_ITEMS.map((item) => (
              <a
                key={item.href}
                href={item.href}
                onClick={() => setMobileOpen(false)}
                className="rounded-lg px-3 py-2 text-sm font-medium text-lavender transition-colors hover:bg-white/5 hover:text-gold-bright"
              >
                {item.label}
              </a>
            ))}
          </div>
        </nav>
      )}
    </header>
  );
}
