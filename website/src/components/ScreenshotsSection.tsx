"use client";

import { useState } from "react";
import { CrownIcon } from "./CrownIcon";

interface ScreenPreview {
  title: string;
  caption: string;
  badge: string;
  rows: string[];
}

const SCREEN_FRAMES: ScreenPreview[] = [
  {
    title: "Account Overview",
    caption: "Clean dashboard showing organized quick actions and status summary.",
    badge: "Dashboard",
    rows: ["Navigation Bar", "Overview Header", "Quick Module Tiles"]
  },
  {
    title: "Daily Activities",
    caption: "Clear list of daily activities and eligibility details.",
    badge: "Activities",
    rows: ["Daily Check-In Card", "Available Activities", "Completion Rules"]
  },
  {
    title: "Activity History",
    caption: "Chronological activity log with clean reference identifiers.",
    badge: "History",
    rows: ["Timestamped Entries", "Category Labels", "Status Summary"]
  }
];

export function ScreenshotsSection() {
  const [expanded, setExpanded] = useState<ScreenPreview | null>(null);

  return (
    <section id="screenshots" className="border-t border-white/[0.06] py-16 md:py-20">
      <div className="mx-auto max-w-6xl px-5">
        <div className="mb-12 text-center">
          <span className="inline-flex rounded-full border border-gold-bright/30 bg-gold-bright/10 px-3.5 py-1 text-xs font-bold uppercase tracking-[0.16em] text-gold-bright">
            Interface Gallery
          </span>
          <h2 className="mt-4 text-3xl font-extrabold tracking-tight text-white sm:text-4xl">
            APP <span className="gold-gradient-text">SCREENSHOTS</span>
          </h2>
          <p className="mt-2 text-sm text-lavender">
            Tap any frame to inspect the KINGS EARNS Android layout cleanly.
          </p>
        </div>

        <div className="grid gap-8 md:grid-cols-3">
          {SCREEN_FRAMES.map((screen) => (
            <button
              key={screen.title}
              type="button"
              onClick={() => setExpanded(screen)}
              className="group flex flex-col items-center text-left focus:outline-none"
            >
              <div className="w-full max-w-[280px] rounded-[38px] border-[3px] border-[#261E3B] bg-[#0B0814] p-2.5 shadow-purpleGlow transition-all duration-300 group-hover:-translate-y-2 group-hover:border-gold-bright/45">
                <div className="mx-auto mb-2.5 h-2 w-2 rounded-full bg-[#1C162E] ring-1 ring-white/10" />
                <div className="rounded-[28px] border border-white/[0.07] bg-gradient-to-b from-[#110C1F] to-[#08060E] p-4">
                  <div className="flex items-center justify-between border-b border-white/[0.08] pb-3">
                    <div className="flex items-center gap-2">
                      <CrownIcon className="h-3.5 w-3.5 text-gold-bright" />
                      <span className="text-xs font-bold text-white">{screen.title}</span>
                    </div>
                    <span className="text-[10px] font-semibold text-gold-bright">{screen.badge}</span>
                  </div>

                  <div className="mt-4 overflow-hidden rounded-xl border border-gold-bright/20">
                    <img
                      src="/images/kings-earns-crown.png"
                      alt={`${screen.title} preview`}
                      className="h-28 w-full object-cover"
                    />
                  </div>

                  <div className="mt-4 space-y-2">
                    {screen.rows.map((row) => (
                      <div
                        key={row}
                        className="rounded-lg border border-white/[0.08] bg-[#100C1C] px-3 py-2 text-xs font-medium text-lavender"
                      >
                        {row}
                      </div>
                    ))}
                  </div>
                </div>
              </div>

              <h3 className="mt-5 text-base font-bold text-white group-hover:text-gold-bright">
                {screen.title}
              </h3>
              <p className="mt-1 max-w-[270px] text-center text-xs leading-relaxed text-lavender/80">
                {screen.caption}
              </p>
            </button>
          ))}
        </div>
      </div>

      {expanded && (
        <div
          role="dialog"
          aria-modal="true"
          className="fixed inset-0 z-50 flex items-center justify-center bg-black/80 px-5 backdrop-blur-md"
          onClick={() => setExpanded(null)}
        >
          <div
            className="dark-card-gold w-full max-w-sm rounded-3xl p-6"
            onClick={(e) => e.stopPropagation()}
          >
            <div className="flex items-center justify-between border-b border-white/10 pb-3">
              <div className="flex items-center gap-2">
                <CrownIcon className="h-4 w-4 text-gold-bright" />
                <span className="font-bold text-white">{expanded.title}</span>
              </div>
              <button
                type="button"
                onClick={() => setExpanded(null)}
                aria-label="Close preview"
                className="rounded-lg border border-white/10 px-2.5 py-1 text-xs text-lavender hover:text-gold-bright"
              >
                Close
              </button>
            </div>
            <img
              src="/images/kings-earns-crown.png"
              alt={expanded.title}
              className="mt-4 h-44 w-full rounded-2xl border border-gold-bright/20 object-cover"
            />
            <p className="mt-4 text-sm text-lavender">{expanded.caption}</p>
          </div>
        </div>
      )}
    </section>
  );
}
