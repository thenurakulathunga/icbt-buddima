# 10 – Task A Guide: Critical Comparison of Mobile OSs, Tools and Technologies (~1000 words)

> This is an **outline and list of talking points**, not the essay. Write the final text in your own words
> and cite sources in Harvard style. Copying it verbatim would count as unfair practice under the brief.
> The rubric rewards *critical evaluation* and *justified choices* over description.

## Suggested structure and word budget

| Section | Words |
|---------|-------|
| 1. Introduction: PrintXpress needs (file upload, notifications, offline DB, Sri Lankan market) | 80 |
| 2. Mobile OS comparison | 280 |
| 3. Development approaches (native vs cross-platform vs hybrid/PWA) | 180 |
| 4. Languages (Java vs Kotlin; Swift for iOS) | 130 |
| 5. IDEs and tools | 110 |
| 6. Local data storage technologies | 150 |
| 7. Justified final selection + limitations | 70 |

## 1. Evaluation criteria (define these first, then use them in every table)

Market reach in Sri Lanka · cost and hardware requirements · performance · device and API access
(file picker, notifications) · developer skill/learning curve · tooling · offline support · security ·
the brief's constraint (native only).

## 2. Mobile OS comparison – points to cover

| Criterion | Android | iOS | HarmonyOS / others |
|-----------|---------|-----|---------------------|
| Market share (Sri Lanka) | Dominant (find the current StatCounter figure, roughly 75–85%) | Minority, premium users | Negligible locally |
| Development cost | Free SDK, Windows/Linux/macOS, USD 25 one-time Play fee | Needs a Mac + USD 99/yr | Limited ecosystem |
| Openness | Open (AOSP), sideloading, many OEMs | Closed, strict review | Huawei devices only |
| Fragmentation | High: many screen sizes and API levels (weakness → handle with minSdk 24 and responsive layouts) | Low | – |
| File access | Storage Access Framework, flexible | Document picker, sandboxed | – |
| Notifications | Channels, runtime permission since Android 13 | APNs, permission prompt | – |
| Security | Play Protect, app sandbox, scoped storage | Strong sandbox, strict review | – |

**Critical angle:** Android's reach among small businesses in Sri Lanka and its lower development cost outweigh its
fragmentation costs. iOS would be a phase-2 extension, not a first release.

## 3. Development approaches

| Approach | Examples | Pros | Cons for PrintXpress |
|----------|----------|------|----------------------|
| Native | Java/Kotlin (Android), Swift (iOS) | Best performance, full API access, first-party tools | Two codebases for two OSs |
| Cross-platform | Flutter, React Native, KMP | One codebase | **Forbidden by the brief**. Extra abstraction layer, plugin dependency for file/notification APIs |
| Hybrid/Web | Ionic, Cordova, PWA | Web skills | Forbidden (Ionic). Weaker offline DB and notification support |

## 4. Language: Java vs Kotlin

- Kotlin: Google's preferred language (Kotlin-first since 2019), null safety, concise syntax, coroutines.
- Java: mature, huge learning base, explicit OOP (easier to show classes, interfaces and patterns for
  marking), Javadoc tooling, 100% interoperable with Kotlin.
- **Choice:** Java, for team familiarity, clear OOP and design-pattern demonstration, and Javadoc documentation.
  Limitation: more boilerplate and no built-in null safety (mitigated with `@NonNull`/`@Nullable` annotations).

## 5. IDEs and tools

Android Studio (official, emulator, Layout Inspector, Database Inspector, Profiler, lint) vs
IntelliJ IDEA / VS Code (lighter, weaker Android tooling) vs Xcode (iOS only, macOS only).
Also mention Gradle, Git/GitHub, Figma (UI design), and PlantUML/draw.io (UML).

## 6. Local storage technologies

| Tech | Type | Pros | Cons |
|------|------|------|------|
| **SQLite (SQLiteOpenHelper)** | Embedded relational | Built into Android, SQL, relational integrity (FKs, CHECK), offline, no dependency | Boilerplate, manual mapping, no compile-time query checks |
| Room | ORM over SQLite | Compile-time checks, less boilerplate, LiveData | Extra abstraction, annotation processing |
| Firebase Firestore/Realtime DB | Cloud NoSQL | Sync, multi-device, real admin panel | Needs internet, cost, not relational, vendor lock-in |
| Realm | Object DB | Fast, easy objects | Third-party, weaker relational modelling |
| SharedPreferences / DataStore | Key-value | Simple settings | Not for relational data (we use it only for the session) |

**Critical angle:** the data is highly relational (orders → items → options, users → addresses → districts),
so a relational engine fits it best. SQLite gives offline use and full control over normalisation. A production version
would add a cloud backend (REST API + MySQL/PostgreSQL, or Firebase) for the shop's admin side and use
SQLite as the offline cache.

## 7. Final justified stack

Android · Java 17 · Android Studio · XML + Material 3 · MVVM · SQLite · WorkManager · Glide.
Close with limitations: Android-only reach, no cloud sync, simulated back office, and how you would address them.

## Suggested references (check them and add access dates)

- Android Developers (2026) *Guide to app architecture*. developer.android.com/topic/architecture
- Android Developers (2026) *Save data using SQLite*. developer.android.com/training/data-storage/sqlite
- Android Developers (2026) *Access documents and other files from shared storage*.
- Android Developers (2026) *Notification runtime permission*.
- Apple (2026) *Apple Developer Program*. developer.apple.com/programs
- StatCounter (2026) *Mobile Operating System Market Share Sri Lanka*. gs.statcounter.com
- Gamma, E., Helm, R., Johnson, R. and Vlissides, J. (1994) *Design Patterns*. Addison-Wesley.
- Google (2019) *Kotlin-first Android development* (Android Developers Blog).
- SQLite (2026) *About SQLite*. sqlite.org/about.html
