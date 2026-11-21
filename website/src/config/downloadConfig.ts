export interface AppDownloadConfig {
  appName: string;
  slogan: string;
  packageName: string;
  shortDescription: string;
  currentVersion: string;
  supportedAndroidVersion: string;
  lastUpdated: string;
  /**
   * Provide your verified HTTPS APK URL when hosted (e.g., "https://your-domain.com/kings-earns-v1.0.apk").
   * Leave empty while APK upload is pending so visitors see an honest availability notice instead of a broken link.
   */
  apkUrl: string;
  /**
   * Exact APK file size once built & uploaded (e.g., "18.4 MB"). Never fabricate a placeholder size.
   */
  apkFileSize: string;
  /**
   * Optional Google Play Store HTTPS listing URL. Hidden until a real listing exists.
   */
  googlePlayUrl: string;
  /**
   * Support email placeholder until real contact info is added.
   */
  contactEmailPlaceholder: string;
}

export const DOWNLOAD_CONFIG: AppDownloadConfig = {
  appName: "KINGS EARNS",
  slogan: "WATCH. EARN. REWARD.",
  packageName: "com.aistudio.kingsearns.rkxmpq",
  shortDescription:
    "KINGS EARNS is an Android application designed with a simple interface and convenient navigation.",
  currentVersion: "1.0",
  supportedAndroidVersion: "Android 7.0+ (API 24+)",
  lastUpdated: "2026-11-21",
  apkUrl: process.env.NEXT_PUBLIC_APK_DOWNLOAD_URL ?? "",
  apkFileSize: process.env.NEXT_PUBLIC_APK_FILE_SIZE ?? "",
  googlePlayUrl: process.env.NEXT_PUBLIC_GOOGLE_PLAY_URL ?? "",
  contactEmailPlaceholder: "[Contact Email Pending]"
};

export function isValidHttpsUrl(url: string): boolean {
  if (!url || !url.trim()) return false;
  try {
    const parsed = new URL(url.trim());
    return parsed.protocol === "https:";
  } catch {
    return false;
  }
}
