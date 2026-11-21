import { DOWNLOAD_CONFIG, isValidHttpsUrl } from "@/config/downloadConfig";
import { DownloadButton } from "./DownloadButton";

const INSTALL_STEPS = [
  {
    step: "STEP 1",
    description: "Visit the official KINGS EARNS download website."
  },
  {
    step: "STEP 2",
    description: "Click the DOWNLOAD APP button."
  },
  {
    step: "STEP 3",
    description: "Download and install the Android application."
  }
];

export function HowToAndDownloadSection() {
  const hasPlayStore = isValidHttpsUrl(DOWNLOAD_CONFIG.googlePlayUrl);

  return (
    <>
      {/* How to Download */}
      <section className="border-t border-white/[0.06] py-16">
        <div className="mx-auto max-w-6xl px-5">
          <div className="mb-10 text-center">
            <h2 className="text-2xl font-extrabold tracking-tight text-white sm:text-3xl">
              HOW TO DOWNLOAD
            </h2>
            <p className="mt-2 text-sm text-lavender">
              Three simple steps to install KINGS EARNS on your Android device.
            </p>
          </div>

          <div className="grid gap-5 md:grid-cols-3">
            {INSTALL_STEPS.map((item) => (
              <div key={item.step} className="dark-card rounded-2xl p-6">
                <span className="inline-flex rounded-lg border border-gold-bright/30 bg-gold-bright/10 px-3 py-1 font-mono text-xs font-bold text-gold-bright">
                  {item.step}
                </span>
                <p className="mt-4 text-base font-semibold leading-relaxed text-white">
                  {item.description}
                </p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Main Download Section */}
      <section id="download" className="py-16 md:py-24">
        <div className="mx-auto max-w-5xl px-5">
          <div className="dark-card-gold rounded-3xl p-6 sm:p-10">
            <div className="flex flex-col gap-5 sm:flex-row sm:items-center">
              <div className="flex h-20 w-20 shrink-0 items-center justify-center rounded-2xl border border-[#2C214A] bg-[#120D1F] p-2">
                <img
                  src="/images/kings-earns-icon.jpg"
                  alt="KINGS EARNS App Icon"
                  className="h-full w-full rounded-xl object-cover"
                />
              </div>
              <div>
                <span className="inline-flex rounded-full border border-gold-bright/30 bg-gold-bright/10 px-3 py-0.5 text-[11px] font-bold uppercase tracking-wider text-gold-bright">
                  Android Application
                </span>
                <h2 className="mt-2 text-2xl font-extrabold tracking-tight text-white sm:text-3xl">
                  {DOWNLOAD_CONFIG.appName}
                </h2>
                <p className="mt-1.5 max-w-xl text-sm leading-relaxed text-lavender">
                  {DOWNLOAD_CONFIG.shortDescription}
                </p>
              </div>
            </div>

            <div className="mt-8 grid gap-3 sm:grid-cols-2 lg:grid-cols-4">
              <div className="rounded-xl border border-white/[0.08] bg-midnight/80 px-4 py-3">
                <span className="block text-[11px] uppercase tracking-wider text-lavender/75">
                  Current Version
                </span>
                <strong className="mt-1 block text-sm font-bold text-white">
                  {DOWNLOAD_CONFIG.currentVersion}
                </strong>
              </div>
              <div className="rounded-xl border border-white/[0.08] bg-midnight/80 px-4 py-3">
                <span className="block text-[11px] uppercase tracking-wider text-lavender/75">
                  APK File Size
                </span>
                <strong className="mt-1 block text-sm font-bold text-white">
                  {DOWNLOAD_CONFIG.apkFileSize || "Pending upload"}
                </strong>
              </div>
              <div className="rounded-xl border border-white/[0.08] bg-midnight/80 px-4 py-3">
                <span className="block text-[11px] uppercase tracking-wider text-lavender/75">
                  Android Compatibility
                </span>
                <strong className="mt-1 block text-sm font-bold text-white">
                  {DOWNLOAD_CONFIG.supportedAndroidVersion}
                </strong>
              </div>
              <div className="rounded-xl border border-white/[0.08] bg-midnight/80 px-4 py-3">
                <span className="block text-[11px] uppercase tracking-wider text-lavender/75">
                  Last Updated
                </span>
                <strong className="mt-1 block text-sm font-bold text-white">
                  {DOWNLOAD_CONFIG.lastUpdated}
                </strong>
              </div>
            </div>

            <div className="mt-8 flex flex-wrap items-start gap-4">
              <DownloadButton
                label="DOWNLOAD KINGS EARNS FOR ANDROID"
                className="w-full sm:w-auto"
              />

              {hasPlayStore && (
                <a
                  href={DOWNLOAD_CONFIG.googlePlayUrl}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="inline-flex min-h-[56px] items-center justify-center rounded-2xl border border-white/15 bg-plum px-6 py-4 text-sm font-bold text-white transition-colors hover:border-gold-bright/50 hover:text-gold-bright"
                >
                  Available on Google Play
                </a>
              )}
            </div>
          </div>
        </div>
      </section>
    </>
  );
}
