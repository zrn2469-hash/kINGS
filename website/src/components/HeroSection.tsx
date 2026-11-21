import { DownloadButton } from "./DownloadButton";
import { CrownIcon } from "./CrownIcon";

export function HeroSection() {
  return (
    <section id="home" className="relative overflow-hidden py-14 md:py-20">
      <div
        aria-hidden="true"
        className="pointer-events-none absolute right-[8%] top-8 h-[460px] w-[460px] rounded-full bg-royal-purple/15 blur-[130px]"
      />

      <div className="relative mx-auto grid max-w-6xl items-center gap-12 px-5 lg:grid-cols-12">
        {/* Left Column */}
        <div className="lg:col-span-7">
          <div className="mb-8 flex items-center gap-4">
            <div className="flex h-[74px] w-[74px] shrink-0 items-center justify-center rounded-2xl border border-[#2C214A] bg-gradient-to-b from-[#171128] to-[#0B0814] p-2 shadow-[0_12px_30px_rgba(113,59,255,0.22)]">
              <img
                src="/images/kings-earns-icon.jpg"
                alt="KINGS EARNS Official Crown Icon"
                className="h-full w-full rounded-xl object-cover"
              />
            </div>
            <div>
              <div className="text-xs font-bold uppercase tracking-[0.12em] text-gold-bright sm:text-sm">
                KINGS EARNS <span className="mx-1 text-gold-bright/70">&middot;</span> WATCH. EARN. REWARD.
              </div>
              <p className="mt-1 text-xs font-medium text-lavender sm:text-sm">
                Official Android Application Distribution
              </p>
            </div>
          </div>

          <h1 className="text-4xl font-extrabold leading-[1.06] tracking-tight sm:text-5xl lg:text-[58px]">
            <span className="block text-white">WELCOME TO</span>
            <span className="mt-1 block gold-gradient-text">KINGS EARNS</span>
          </h1>

          <p className="mt-6 max-w-xl text-base leading-relaxed text-lavender sm:text-lg">
            Discover the KINGS EARNS mobile experience. Download the app and explore its features.
          </p>

          <div className="mt-8 flex flex-wrap items-start gap-4">
            <DownloadButton label="DOWNLOAD APP" />

            <a
              href="#features"
              className="inline-flex min-h-[56px] items-center justify-center gap-2 rounded-xl border border-white/15 bg-plum px-6 py-3.5 text-sm font-bold text-white transition-colors hover:border-gold-bright/50 hover:text-gold-bright"
            >
              <span>Explore Features</span>
              <svg className="h-4 w-4" fill="none" stroke="currentColor" strokeWidth={2} viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" d="M19 14l-7 7m0 0l-7-7m7 7V3" />
              </svg>
            </a>
          </div>
        </div>

        {/* Right-Side Phone Mockup — Matched to Reference Screenshot */}
        <div className="flex justify-center lg:col-span-5 lg:justify-end">
          <div className="w-full max-w-[330px] rounded-[42px] border-[4px] border-[#261E3B] bg-[#0B0814] p-3 shadow-purpleGlow">
            {/* Top camera/speaker notch */}
            <div className="mx-auto mb-3 flex items-center justify-center">
              <span className="h-2.5 w-2.5 rounded-full bg-[#1C162E] ring-1 ring-white/10" />
            </div>

            <div className="rounded-[32px] border border-white/[0.07] bg-gradient-to-b from-[#110C1F] via-[#0B0814] to-[#08060E] p-5">
              {/* Mockup Header */}
              <div className="flex items-center justify-between pb-4">
                <div className="flex items-center gap-2">
                  <CrownIcon className="h-4 w-4 text-gold-bright" />
                  <span className="text-xs font-extrabold tracking-wide text-white">
                    KINGS EARNS
                  </span>
                </div>
                <span className="text-xs font-semibold text-gold-bright">Android</span>
              </div>

              {/* Inner Slogan Summary Card */}
              <div className="mt-2 rounded-2xl border border-gold-bright/25 bg-[#120D21] p-4">
                <div className="flex items-center gap-2 text-xs font-bold tracking-wider text-gold-bright">
                  <CrownIcon className="h-3.5 w-3.5 text-gold-bright" />
                  <span>WATCH. EARN. REWARD.</span>
                </div>
                <p className="mt-2 text-xs leading-relaxed text-lavender">
                  Simple navigation for daily activities, account overview, and reward tracking.
                </p>
              </div>

              {/* 2x2 Feature Tiles Grid */}
              <div className="mt-4 grid grid-cols-2 gap-3">
                <div className="rounded-2xl border border-white/[0.08] bg-[#100C1C] p-3.5">
                  <svg className="h-4 w-4 text-gold-bright" fill="none" stroke="currentColor" strokeWidth={2} viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" d="M4 5a1 1 0 011-1h4a1 1 0 011 1v4a1 1 0 01-1 1H5a1 1 0 01-1-1V5zm10 0a1 1 0 011-1h4a1 1 0 011 1v4a1 1 0 01-1 1h-4a1 1 0 01-1-1V5zM4 15a1 1 0 011-1h4a1 1 0 011 1v4a1 1 0 01-1 1H5a1 1 0 01-1-1v-4zm10 0a1 1 0 011-1h4a1 1 0 011 1v4a1 1 0 01-1 1h-4a1 1 0 01-1-1v-4z" />
                  </svg>
                  <div className="mt-2.5 text-xs font-bold text-white">Dashboard</div>
                  <div className="mt-0.5 text-[11px] text-lavender/75">Account overview</div>
                </div>

                <div className="rounded-2xl border border-white/[0.08] bg-[#100C1C] p-3.5">
                  <svg className="h-4 w-4 text-gold-bright" fill="none" stroke="currentColor" strokeWidth={2} viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                  </svg>
                  <div className="mt-2.5 text-xs font-bold text-white">Activities</div>
                  <div className="mt-0.5 text-[11px] text-lavender/75">Daily modules</div>
                </div>

                <div className="rounded-2xl border border-white/[0.08] bg-[#100C1C] p-3.5">
                  <svg className="h-4 w-4 text-gold-bright" fill="none" stroke="currentColor" strokeWidth={2} viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" d="M12 3l2.2 4.8L19 10l-4.8 2.2L12 17l-2.2-4.8L5 10l4.8-2.2L12 3z" />
                  </svg>
                  <div className="mt-2.5 text-xs font-bold text-white">Reward Points</div>
                  <div className="mt-0.5 text-[11px] text-lavender/75">Activity summary</div>
                </div>

                <div className="rounded-2xl border border-white/[0.08] bg-[#100C1C] p-3.5">
                  <svg className="h-4 w-4 text-gold-bright" fill="none" stroke="currentColor" strokeWidth={2} viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                  <div className="mt-2.5 text-xs font-bold text-white">History</div>
                  <div className="mt-0.5 text-[11px] text-lavender/75">Past records</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}
