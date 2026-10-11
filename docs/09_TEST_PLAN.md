# 09 – Test Plan (Task E)

## 1. Scope and objectives

Verify that every functional requirement (docs/01 §4), every business rule (§7) and every validation
rule (§8) behaves correctly with valid, invalid and boundary data, and that the app remains stable and usable.

## 2. Test levels and tools

| Level | Tool | Target | Location |
|-------|------|--------|----------|
| Unit (JVM) | JUnit 4 + Mockito | Validation rules, `PriceCalculator`/strategies, `OrderState` transitions, `ScheduleCalculator`, `PasswordHasher`, `OrderNumberGenerator` | `app/src/test/java` |
| Integration (instrumented) | AndroidX Test + in-memory `DatabaseHelper` | DAOs, FK/CHECK constraints, order transaction, repositories | `app/src/androidTest/java` |
| UI (instrumented) | Espresso (+ Espresso-Intents for the file picker/dialer) | Login, register validation, add to cart, place order, cancel | `app/src/androidTest/java` |
| System / manual | Test case table below | Full user journeys, notifications, dark mode, rotation | `evidence/` screenshots |
| Non-functional | Android Profiler, Accessibility Scanner, lint | Start-up time, jank, contrast, touch targets | report appendix |
| Compatibility | Emulators API 24, 30, 35/36 + 1 physical device | Layout, permissions | – |

**Entry criteria:** feature merged and the build compiles. **Exit criteria:** 100% of High-priority cases pass,
≥ 95% overall, and no open critical defects.

**Defect log columns:** ID · Test case · Description · Severity (Critical/Major/Minor) · Status · Fix ref.

## 3. Test data

| Set | Data |
|-----|------|
| Valid user | Name `Nimal Perera`, email `nimal.perera@gmail.com`, phone `0771234567`, password `Print@2026` |
| Second user | `Kavindi Silva`, `+94712345678`, `Secure#99x` |
| Invalid emails | `nimal@`, `nimal.gmail.com`, `@gmail.com`, `nimal@gmail`, empty |
| Invalid phones | `077123456` (9 digits), `0871234567` (not mobile), `+9477123456a`, `07712345678` (11 digits) |
| Weak passwords | `password`, `Pass123` (7 chars), `PASSWORD1!` (no lower), `Password!` (no digit) |
| Names | valid `A. B. Fernando`; invalid `Ni`, `Nimal123`, 61-char string |
| Postal codes | valid `10300`; invalid `1030`, `10300a`, `103000` |
| Quantity (Standard Business Card: min 100, max 5000, step 50) | valid 100, 150, 5000; invalid 99, 125, 5001, 0, -5, empty, `abc` |
| Files | valid `logo.pdf` (2 MB), `card.png` (5 MB), `photo.jpg` (24.9 MB); invalid `design.psd`, `doc.docx`, `big.pdf` (26 MB), 0-byte file |
| Custom text | valid 1–250 chars; invalid 251 chars, whitespace only |
| Dates (today = test day, turnaround 2) | valid today+2 (not Sunday); invalid today, today+1, a Sunday, past date |
| Promo codes | valid `WELCOME10`; expired `AVURUDU26` (outside April); invalid `ABC`, `welcome10` (lower → normalised), `NOTREAL1`; `BULK500` under Rs 20,000 |

## 4. Test cases

Columns for execution: **Actual result** and **Pass/Fail** are filled in while testing. Attach the evidence screenshot ID.

### 4.1 Authentication and profile

| ID | Req | Description | Steps / Data | Expected result | Pri |
|----|-----|-------------|--------------|-----------------|-----|
| TC-01 | FR-1.1 | Register with valid email | Valid user set | Account created, redirected to Home, row in `users` with a hashed password | H |
| TC-02 | FR-1.1 | Register with phone only | Name + `0771234567` + valid password | Account created | H |
| TC-03 | BR-1 | Register with neither email nor phone | Both empty | Error "Enter an email or mobile number" | H |
| TC-04 | §8 | Invalid email formats | Each invalid email | Inline error `err_email_invalid`, no DB insert | H |
| TC-05 | §8 | Invalid SL phone formats | Each invalid phone | Inline error `err_phone_invalid` | H |
| TC-06 | BR-2 | Weak passwords | Each weak password | Error lists the missing criteria; strength meter shows red | H |
| TC-07 | §8 | Confirm password mismatch | `Print@2026` / `Print@2025` | `err_password_mismatch` | H |
| TC-08 | BR-1 | Duplicate email | Register `nimal.perera@gmail.com` twice | "Email already registered" | H |
| TC-09 | FR-1.2 | Login with email | Valid creds | Home opens, session saved | H |
| TC-10 | FR-1.2 | Login with phone | `0771234567` + password | Home opens | H |
| TC-11 | FR-1.2 | Wrong password | `Print@2027` | "Invalid credentials", stays on Login | H |
| TC-12 | FR-1.3 | Session persists | Login, kill app, reopen | Opens Home directly | M |
| TC-13 | FR-1.3 | Logout | Profile › Logout › Confirm | Returns to Login, session cleared | M |
| TC-14 | FR-1.4 | Edit profile name | Change to `Nimal K. Perera` | Saved, header updates | M |
| TC-15 | FR-1.6 | Add address valid | Home, `No. 12, Galle Rd`, Colombo, district Colombo, `00300` | Saved, listed | H |
| TC-16 | §8 | Add address invalid postal | `1030` | `err_postal_invalid` | M |
| TC-17 | FR-1.6 | Set default address | Mark second address default | Only one default in DB | M |
| TC-18 | FR-1.5 | Change password with wrong current | Wrong current password | Error, password unchanged | M |
| TC-19 | FR-1.10 | Delete account | Settings › Delete › Confirm | User row + cascaded rows removed, back to Login | M |

### 4.2 Browsing and ordering

| ID | Req | Description | Steps / Data | Expected result | Pri |
|----|-----|-------------|--------------|-----------------|-----|
| TC-20 | FR-2.1 | Categories shown | Open Catalog | 7 categories with icons | H |
| TC-21 | FR-2.3 | Filter by material | Banners › Filter › PVC | Only PVC banners listed | H |
| TC-22 | FR-2.3 | Search no result | "zzzz" | Empty state shown | M |
| TC-23 | FR-2.3 | Sort by price | Sort low → high | Ascending order | M |
| TC-24 | FR-2.4 | Product detail | Tap Standard Business Card | Images, specs, price table, samples | H |
| TC-25 | BR-3 | Quantity boundaries | 99, 100, 125, 5000, 5001 | 100 and 5000 accepted; others show `err_qty_range` | H |
| TC-26 | FR-2.5 | Live price with options | 500 qty, Double-sided (+40%), Matte | Price = base × qty × modifiers − 10% bulk (BR-4), shown in the summary | H |
| TC-27 | BR-4 | Bulk discount thresholds | qty 499 / 500 / 1000 | 0% / 10% / 15% | H |
| TC-28 | BR-10 | Upload valid PDF | `logo.pdf` 2 MB | Preview with name and size, file copied to app storage | H |
| TC-29 | BR-10 | Upload invalid type | `design.psd` | `err_file_type`, nothing stored | H |
| TC-30 | BR-10 | Upload oversize | `big.pdf` 26 MB | `err_file_size` | H |
| TC-31 | BR-9 | Neither file nor text | Tap Add to cart | `err_design_required` | H |
| TC-32 | FR-2.6 | Custom text 251 chars | Paste 251 chars | Input capped/error | M |
| TC-33 | FR-2.8 | Add to cart | Valid configuration | Cart badge +1, row in `cart_items` + options | H |
| TC-34 | FR-2.8 | Update cart qty / remove | Stepper +50, then remove | Totals update; empty state after removal | M |
| TC-35 | FR-2.9 | Delivery requires address | Choose Delivery with no address | Prompt to add address, Place order disabled | H |
| TC-36 | FR-2.9 | Delivery fee by district | Address in Colombo vs Jaffna | Rs 300 vs Rs 600 | M |
| TC-37 | BR-6 | Date picker restrictions | Try today, today+1, Sunday | Disabled/not selectable; earliest valid date pre-selected | H |
| TC-38 | FR-2.11 | Valid promo | `WELCOME10` | 10% discount line shown | H |
| TC-39 | BR-5 | Invalid/expired promo | `NOTREAL1`, expired code, `BULK500` below min | Specific error each time, no discount | H |
| TC-40 | FR-2.12 | Place order (pickup) | Full valid flow | Success screen with `PX-YYMMDD-NNNN`, cart cleared, order + items + history rows, notification posted | H |
| TC-41 | NFR-7 | Transaction rollback | Force a failure mid-insert (test hook in the instrumented test) | No partial order rows remain | H |
| TC-42 | – | Guest checkout attempt | Add to cart as guest | Redirect to Login, cart kept after login | M |

### 4.3 Order management

| ID | Req | Description | Steps / Data | Expected result | Pri |
|----|-----|-------------|--------------|-----------------|-----|
| TC-43 | FR-3.1 | Active vs Past tabs | 1 pending, 1 completed | Each appears in the correct tab | H |
| TC-44 | FR-3.2 | Timeline shows history | Advance status via worker (demo speed) | Steps with timestamps; the current step is highlighted | H |
| TC-45 | FR-3.3 | Reschedule in PROCESSING | New valid date/slot | Updated, history remark added, notification | H |
| TC-46 | BR-7 | Reschedule in PRINTING | Order at PRINTING | Buttons disabled + info banner | H |
| TC-47 | FR-3.4 | Cancel without reason | Tap Confirm with no reason | `err_reason_required` | M |
| TC-48 | FR-3.4 | Cancel in CONFIRMED | Reason "Ordered by mistake" | Status CANCELLED, moved to Past, notification | H |
| TC-49 | BR-7 | Cancel after completion | Completed order | No cancel option | H |
| TC-50 | – | Pickup branch after PRINTING | Pickup order | Goes to READY_FOR_PICKUP (not OUT_FOR_DELIVERY) | M |

### 4.4 Notifications, offers, support

| ID | Req | Description | Steps / Data | Expected result | Pri |
|----|-----|-------------|--------------|-----------------|-----|
| TC-51 | FR-4.1 | Confirmation notification | Place order | System notification + entry in the notification centre | H |
| TC-52 | FR-4.4 | Tap notification deep link | Tap order notification | Opens that order's detail | M |
| TC-53 | FR-4.4 | Mark all read | Tap "Mark all read" | Bell badge 0, `is_read = 1` | M |
| TC-54 | FR-4.6 | Promo notifications off | Toggle off, trigger PromotionWorker | No promo notification posted | M |
| TC-55 | NFR-6 | Permission denied (API 33+) | Deny POST_NOTIFICATIONS | App works; the in-app centre still records notifications | M |
| TC-56 | FR-4.5 | Copy promo code | Tap code chip | Clipboard contains the code + snackbar | L |
| TC-57 | FR-5.1 | Guidelines content | Open Bleed guideline | Illustration + 3 mm/5 mm text | M |
| TC-58 | FR-5.3 | FAQ search | "cancel" | Filters to cancellation FAQs | M |
| TC-59 | FR-5.4 | Contact intents | Tap Call / Email / WhatsApp | Correct external app opens with data | M |
| TC-60 | FR-5.4 | Ticket validation | Subject "Hi" | `err_ticket_subject` (message under 10 characters: `err_ticket_invalid`) | M |
| TC-61 | FR-5.2 | Gallery viewer | Tap tile, swipe, pinch | Full screen, swipe works | L |

### 4.5 Non-functional

| ID | Req | Description | Expected | Pri |
|----|-----|-------------|----------|-----|
| TC-62 | NFR-2 | Cold start time (Profiler) | < 2 s on a mid-range device | M |
| TC-63 | NFR-2 | Rotation during checkout | Entered data kept (ViewModel) | M |
| TC-64 | NFR-4 | Accessibility Scanner on 5 key screens | No critical issues | M |
| TC-65 | NFR-11 | Dark mode | All screens readable, no hard-coded colours | M |
| TC-66 | NFR-5 | Password stored hashed | Inspect DB with Database Inspector | No plaintext password | H |
| TC-67 | NFR-5 | SQL injection attempt | Login identifier `' OR 1=1 --` | Login fails normally | H |
| TC-68 | NFR-1 | API 24 emulator smoke test | Full order flow works | H |

## 5. Automated unit tests to write (examples)

- `EmailRuleTest`, `SriLankanPhoneRuleTest`, `PasswordStrengthRuleTest`, `RangeRuleTest`, `StepRuleTest`,
  `PostalCodeRuleTest`, `MinDateRuleTest`, `NotSundayRuleTest`, `FileTypeRuleTest`, `FileSizeRuleTest`
- `BulkDiscountPricingTest` (499/500/1000), `PromoCodePricingTest` (percent, fixed, min value),
  `PriceCalculatorTest` (full breakdown), `MoneyTest` (rounding)
- `OrderStateTest` (every transition; `canCancel`/`canReschedule` true only for the first 3 states)
- `ScheduleCalculatorTest`, `PasswordHasherTest` (same password + salt → same hash; different salt → different hash)
- Instrumented: `UserDaoTest` (unique constraint), `OrderDaoTest` (transaction + cascade), `PromotionDaoTest` (date validity)
- Espresso: `LoginFlowTest`, `RegisterValidationTest`, `PlaceOrderFlowTest`, `CancelOrderTest`

## 6. Evidence

Save screenshots as `evidence/TC-xx.png` and put the executed table in the report appendix with
Actual Result, Pass/Fail and defect IDs. Include a JUnit HTML report screenshot
(`app/build/reports/tests/testDebugUnitTest/index.html`).
