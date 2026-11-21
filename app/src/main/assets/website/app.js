(function () {
  var APK_URL = "";

  var menuBtn = document.getElementById("menuToggleBtn");
  var navLinks = document.getElementById("navLinks");
  if (menuBtn && navLinks) {
    menuBtn.addEventListener("click", function () {
      navLinks.classList.toggle("open");
    });
    navLinks.querySelectorAll("a").forEach(function (link) {
      link.addEventListener("click", function () {
        navLinks.classList.remove("open");
      });
    });
  }

  document.querySelectorAll(".faq-q").forEach(function (btn) {
    btn.addEventListener("click", function () {
      var item = btn.closest(".faq-item");
      if (item) {
        item.classList.toggle("open");
      }
    });
  });

  document.querySelectorAll("[data-apk-download-btn]").forEach(function (btn) {
    btn.addEventListener("click", function () {
      var targetId = btn.getAttribute("data-status-target");
      var noticeEl = targetId ? document.getElementById(targetId) : null;
      if (!APK_URL) {
        if (noticeEl) {
          noticeEl.textContent =
            "The official KINGS EARNS APK download URL is currently unavailable. Please check back once the release file is published.";
          noticeEl.style.display = "block";
        }
        return;
      }
      window.open(APK_URL, "_blank", "noopener,noreferrer");
    });
  });

  var screenModal = document.getElementById("screenModal");
  var screenTitle = document.getElementById("screenModalTitle");
  var screenCaption = document.getElementById("screenModalCaption");
  var closeScreenBtn = document.getElementById("closeScreenModalBtn");

  document.querySelectorAll("[data-screen-title]").forEach(function (btn) {
    btn.addEventListener("click", function () {
      if (screenModal && screenTitle && screenCaption) {
        screenTitle.textContent = btn.getAttribute("data-screen-title") || "";
        screenCaption.textContent = btn.getAttribute("data-screen-caption") || "";
        screenModal.classList.add("open");
      }
    });
  });

  if (closeScreenBtn && screenModal) {
    closeScreenBtn.addEventListener("click", function () {
      screenModal.classList.remove("open");
    });
    screenModal.addEventListener("click", function (e) {
      if (e.target === screenModal) screenModal.classList.remove("open");
    });
  }

  var legalModal = document.getElementById("legalModal");
  var legalTitle = document.getElementById("legalModalTitle");
  var legalBody = document.getElementById("legalModalBody");
  var closeLegalBtn = document.getElementById("closeLegalModalBtn");

  var LEGAL_CONTENT = {
    privacy: {
      title: "PRIVACY POLICY",
      body: "This website serves solely as the official informational and Android APK download portal for KINGS EARNS. Visitors are not required to register, log in, or submit personal contact information to view this website or download the official Android APK package."
    },
    terms: {
      title: "TERMS AND CONDITIONS",
      body: "This website distributes only the unmodified official KINGS EARNS Android APK (com.aistudio.kingsearns.rkxmpq). Installation requires Android 7.0+ (API 24+). Visitors should download strictly from the official HTTPS website."
    }
  };

  document.querySelectorAll("[data-legal-open]").forEach(function (trigger) {
    trigger.addEventListener("click", function (e) {
      e.preventDefault();
      var key = trigger.getAttribute("data-legal-open");
      var doc = LEGAL_CONTENT[key];
      if (doc && legalModal && legalTitle && legalBody) {
        legalTitle.textContent = doc.title;
        legalBody.textContent = doc.body;
        legalModal.classList.add("open");
      }
    });
  });

  if (closeLegalBtn && legalModal) {
    closeLegalBtn.addEventListener("click", function () {
      legalModal.classList.remove("open");
    });
    legalModal.addEventListener("click", function (e) {
      if (e.target === legalModal) legalModal.classList.remove("open");
    });
  }
})();
