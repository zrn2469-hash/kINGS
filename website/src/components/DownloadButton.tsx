"use client";

import { useState } from "react";
import { DOWNLOAD_CONFIG, isValidHttpsUrl } from "@/config/downloadConfig";

interface DownloadButtonProps {
  label?: string;
  className?: string;
}

export function DownloadButton({
  label = "DOWNLOAD APP",
  className = ""
}: DownloadButtonProps) {
  const [checking, setChecking] = useState(false);
  const [statusMessage, setStatusMessage] = useState<string | null>(null);

  const hasValidApkUrl = isValidHttpsUrl(DOWNLOAD_CONFIG.apkUrl);

  async function handleDownloadClick() {
    setStatusMessage(null);

    if (!hasValidApkUrl) {
      setStatusMessage(
        "The official KINGS EARNS APK download URL is currently unavailable. Please check back once the release file is published."
      );
      return;
    }

    setChecking(true);
    try {
      const response = await fetch(DOWNLOAD_CONFIG.apkUrl, {
        method: "HEAD",
        mode: "cors"
      });

      if (!response.ok) {
        setStatusMessage(
          `Unable to reach the official APK package (status ${response.status}). Please try again later.`
        );
        return;
      }

      const anchor = document.createElement("a");
      anchor.href = DOWNLOAD_CONFIG.apkUrl;
      anchor.setAttribute("download", "kings-earns.apk");
      anchor.rel = "noopener noreferrer";
      document.body.appendChild(anchor);
      anchor.click();
      document.body.removeChild(anchor);
    } catch {
      // Cross-origin servers without CORS HEAD headers still serve direct HTTPS downloads cleanly via user click
      window.open(DOWNLOAD_CONFIG.apkUrl, "_blank", "noopener,noreferrer");
    } finally {
      setChecking(false);
    }
  }

  return (
    <div className="flex flex-col items-start gap-3">
      <button
        type="button"
        onClick={handleDownloadClick}
        disabled={checking}
        className={`gold-gradient-button inline-flex min-h-[56px] items-center justify-center gap-3 rounded-2xl px-8 py-4 text-base font-extrabold tracking-wide text-midnight shadow-goldGlow transition-all hover:-translate-y-0.5 hover:shadow-[0_22px_55px_rgba(255,212,71,0.34)] disabled:cursor-not-allowed disabled:opacity-70 ${className}`}
      >
        {checking ? (
          <>
            <svg className="h-5 w-5 animate-spin" viewBox="0 0 24 24" fill="none">
              <circle
                className="opacity-25"
                cx="12"
                cy="12"
                r="10"
                stroke="currentColor"
                strokeWidth="4"
              />
              <path
                className="opacity-75"
                fill="currentColor"
                d="M4 12a8 8 0 018-8v4a4 4 0 00-4 4H4z"
              />
            </svg>
            <span>PREPARING DOWNLOAD...</span>
          </>
        ) : (
          <>
            <svg className="h-5 w-5" fill="none" stroke="currentColor" strokeWidth={2.5} viewBox="0 0 24 24">
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                d="M12 3v12m0 0l-4-4m4 4l4-4M4 17v2a2 2 0 002 2h12a2 2 0 002-2v-2"
              />
            </svg>
            <span>{hasValidApkUrl ? label : "APK UNAVAILABLE"}</span>
          </>
        )}
      </button>

      {statusMessage && (
        <div
          role="status"
          className="max-w-md rounded-xl border border-gold-bright/30 bg-plum/95 px-4 py-3 text-xs leading-relaxed text-lavender"
        >
          {statusMessage}
        </div>
      )}
    </div>
  );
}
