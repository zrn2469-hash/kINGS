# KINGS EARNS — Official Android App Download Website

Built with **Next.js (App Router)**, **React**, **TypeScript**, and **Tailwind CSS** (`output: "export"` for static HTTPS hosting).

## 1. Configure Your Real APK Download URL & Metadata

Open `src/config/downloadConfig.ts` (or set environment variables before building):

```ts
export const DOWNLOAD_CONFIG: AppDownloadConfig = {
  // ...
  apkUrl: "https://your-domain.com/downloads/kings-earns-v1.0.apk",
  apkFileSize: "18.4 MB", // Exact size of your built APK
  googlePlayUrl: "", // Optional: add your Play Store URL when live
  contactEmailPlaceholder: "support@yourdomain.com"
};
```

> **Note:** While `apkUrl` is empty or non-HTTPS, the website automatically shows an **APK UNAVAILABLE** notice instead of a broken link.

## 2. Run Locally

```bash
cd website
npm install
npm run dev
```

## 3. Build Static Output & Deploy Over HTTPS

```bash
cd website
npm run build
```

This generates the static production site in `website/out/`:
- **Vercel / Netlify**: Connect the `website/` directory with build command `npm run build` and output directory `out`.
- **Firebase Hosting**: Run `firebase deploy --only hosting` from the project root (`firebase.json` is configured for `website/out`).
