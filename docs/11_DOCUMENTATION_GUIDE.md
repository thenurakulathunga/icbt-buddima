# 11 – Task F Guide: User and Technical Documentation

Both documents go in the report appendix, with a short summary in the main body. Use screenshots from
the real app (light mode, consistent device frame).

## A. User documentation (User Guide)

1. **Introduction:** what PrintXpress does and who it is for.
2. **System requirements:** Android 7.0 (API 24)+, ~50 MB free space, notifications permission (optional).
3. **Installation:** install the APK (enable "Install unknown apps") or run from Android Studio.
4. **Getting started:** onboarding, register (email or phone), log in, reset password.
5. **Browsing products:** categories, search, filter by type/material/size, product details.
6. **Placing an order** (numbered steps with screenshots): choose specs → quantity → upload a file or enter
   text → add to cart → pickup or delivery → date and slot → promo code → payment method → place order.
7. **File preparation tips:** PDF/PNG/JPG, 300 DPI, CMYK, 3 mm bleed, 25 MB limit.
8. **Managing orders:** tracking timeline, what each status means, reschedule/cancel rules (before printing only).
9. **Notifications and offers:** the notification centre, preferences, using promo codes.
10. **Profile:** edit details, addresses, saved designs, order history, change password, delete account.
11. **Getting help:** guidelines, FAQs, call/email/WhatsApp, support tickets.
12. **Troubleshooting:** table of common problem → solution (file rejected, date not selectable,
    no notifications, cannot cancel).
13. **Privacy:** data is stored only on the device, passwords are hashed, how to delete data.

## B. Technical documentation

1. **Overview and scope:** summary of the requirements and assumptions (docs/01 §6).
2. **Development environment:** Android Studio version, JDK 17, Gradle, SDK levels, dependencies with versions.
3. **Build and run:** clone, open, sync, `gradlew assembleDebug`, run the tests, generate a signed APK.
4. **Architecture:** layer diagram, MVVM data flow, package structure (docs/02 §2, §5).
5. **Design patterns:** table of pattern → class → reason (docs/02 §3).
6. **Database:** ERD, normalised schema, table dictionary (column, type, constraint, description),
   seed data, migration strategy (docs/03).
7. **Key algorithms:** pricing pipeline, order state machine, schedule calculation, password hashing,
   file storage (with Javadoc extracts).
8. **Validation framework:** `ValidationRule` / `FormValidator` design + validation matrix (docs/01 §8).
9. **Background work and notifications:** WorkManager workers, channels, deep links, Android 13 permission.
10. **Security and privacy:** PBKDF2, parameterised SQL, app-private storage, permission minimisation, EDGE ethical points.
11. **Reusable components:** catalogue summary (docs/06).
12. **Testing:** test levels, how to run them, coverage summary (docs/09).
13. **API documentation:** generate Javadoc HTML (Android Studio › Tools › Generate JavaDoc, output
    `docs/javadoc/`) and include screenshots of it.
14. **Known limitations and future work:** cloud backend + admin app, online payments (PayHere),
    SMS OTP, iOS version, Sinhala/Tamil localisation.
