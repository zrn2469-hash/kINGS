const FEATURES = [
  {
    title: "Simple Interface",
    description:
      "A clear dark-navy and gold layout built for comfortable everyday use on Android screens.",
    iconPath: "M4 6h16M4 12h10m-10 6h16"
  },
  {
    title: "Easy Navigation",
    description:
      "Move cleanly between sections using straightforward bottom tabs and clear labels.",
    iconPath: "M9 20l-5.447-2.724A1 1 0 013 16.382V5.618a1 1 0 011.447-.894L9 7m0 13l6-3m-6 3V7m6 10l5.447 2.724A1 1 0 0021 18.382V7.618a1 1 0 00-.553-.894L15 4m0 13V4m0 0L9 7"
  },
  {
    title: "Account Overview",
    description:
      "Check your profile details and organized status summaries in a single view.",
    iconPath: "M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0h6"
  },
  {
    title: "Daily Activities",
    description:
      "Browse daily modules and available in-app activities directly from your mobile device.",
    iconPath: "M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"
  },
  {
    title: "Reward Points",
    description:
      "View point summaries recorded from eligible completed actions inside the app.",
    iconPath: "M12 3l2.2 4.8L19 10l-4.8 2.2L12 17l-2.2-4.8L5 10l4.8-2.2L12 3z"
  },
  {
    title: "Activity History",
    description:
      "Review chronological records and past activity entries whenever needed.",
    iconPath: "M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"
  }
];

export function AboutAndFeaturesSection() {
  return (
    <>
      <section id="about" className="border-t border-white/[0.06] py-16 md:py-20">
        <div className="mx-auto max-w-4xl px-5">
          <div className="dark-card-gold rounded-3xl p-8 text-center sm:p-10">
            <span className="inline-flex rounded-full border border-gold-bright/30 bg-gold-bright/10 px-3.5 py-1 text-xs font-bold uppercase tracking-[0.16em] text-gold-bright">
              Mobile Experience
            </span>
            <h2 className="mt-4 text-3xl font-extrabold tracking-tight text-white sm:text-4xl">
              ABOUT <span className="gold-gradient-text">KINGS EARNS</span>
            </h2>
            <p className="mx-auto mt-4 max-w-2xl text-base leading-relaxed text-lavender sm:text-lg">
              KINGS EARNS is an Android application designed with a simple interface and convenient navigation. Explore the application&apos;s features through a smooth mobile experience.
            </p>
          </div>
        </div>
      </section>

      <section id="features" className="py-10 pb-20">
        <div className="mx-auto max-w-6xl px-5">
          <div className="mb-12 text-center">
            <h2 className="text-2xl font-extrabold tracking-tight text-white sm:text-3xl">
              APP FEATURES
            </h2>
            <p className="mt-2 text-sm text-lavender">
              Designed for clean mobile navigation and clear status visibility.
            </p>
          </div>

          <div className="grid gap-5 sm:grid-cols-2 lg:grid-cols-3">
            {FEATURES.map((feat) => (
              <div
                key={feat.title}
                className="dark-card rounded-2xl p-6 transition-all duration-200 hover:-translate-y-1 hover:border-gold-bright/35"
              >
                <div className="mb-4 inline-flex h-11 w-11 items-center justify-center rounded-xl border border-gold-bright/30 bg-gold-bright/10 text-gold-bright">
                  <svg className="h-5 w-5" fill="none" stroke="currentColor" strokeWidth={2} viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" d={feat.iconPath} />
                  </svg>
                </div>
                <h3 className="text-lg font-bold text-white">{feat.title}</h3>
                <p className="mt-2 text-sm leading-relaxed text-lavender">{feat.description}</p>
              </div>
            ))}
          </div>
        </div>
      </section>
    </>
  );
}
