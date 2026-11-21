interface CrownIconProps {
  className?: string;
}

export function CrownIcon({ className = "h-5 w-5" }: CrownIconProps) {
  return (
    <svg
      className={className}
      viewBox="0 0 24 24"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
      aria-hidden="true"
    >
      <path
        d="M3 7.5L7.5 12L12 5L16.5 12L21 7.5L19 18H5L3 7.5Z"
        fill="currentColor"
      />
      <rect x="5" y="19.2" width="14" height="1.8" rx="0.9" fill="currentColor" />
    </svg>
  );
}
