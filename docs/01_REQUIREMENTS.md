# 01 – Requirements Specification: PrintXpress

Source: `icbthdcse5011writ1-1013.docx` (CSE5011 Mobile Application Development, WRIT1, Semester 03 2026).

---

## 1. Scenario summary

PrintXpress is a digital printing service that lets individuals and small businesses across Sri Lanka
place print orders for business cards, posters, banners, flyers, stickers and customised merchandise
(mugs, t-shirts). The mobile app is for **customers**. They use it to:

1. Browse printing products and services by **type, material and size**.
2. Place orders: choose specifications (quantity, paper type…), **upload files or enter text**, and
   pick **pickup or home delivery** with a schedule.
3. Manage orders and receive **notifications** about order progress and promotional offers.
4. View a **gallery of sample designs**, **print guidelines** and **seasonal promotion packages**.

**Hard constraint:** native technologies only. Android Studio is allowed. Flutter, React Native and
Ionic are **not permitted**.

---

## 2. Project (academic) requirements

| Task | LO | Deliverable | Marks | Where we cover it |
|------|----|-------------|-------|-------------------|
| A | LO1 | Critically compare mobile OSs, dev tools and technologies for this app (~1000 words) | 10 | `docs/10` |
| B | LO1 | System + DB design: Use Case, Class, Activity, ER diagram + normalised relational schema, with design-decision explanations | 10 | `docs/03`, `docs/04` |
| C | LO2 | Attractive, consistent, intuitive UI design | 20 | `docs/05`, `docs/06`, `docs/07` |
| D | LO3 | Interactive app with database integration and robust validation | **40** | `docs/02`, `docs/03`, `TASK.md` |
| E | LO4 | Test plan, test data, applied testing with evidence | 10 | `docs/09` |
| F | LO4 | User and technical documentation | 10 | `docs/11` |

**Submission:** one PDF through Turnitin (Moodle), named like `st12345678CSE5011WRIT1`, by 2.00 pm on
the deadline day. Harvard referencing. Word count 3000 (appendices excluded).
**Pass mark:** 40% (undergraduate).

### 2.1 What "Excellent" needs (rubric top band)

- **A (8–10):** detailed, well-justified, *critical* evaluation of the choices, not just a description.
- **B (8–10):** detailed, accurate diagrams and a clear justification for every design decision.
- **C (16–20):** visually appealing, intuitive, consistent and fully functional, with attention to detail.
- **D (33–40):** fully functional, seamless DB integration, **robust validation**, smooth performance.
- **E (8–10):** comprehensive cases and test data, thoroughly applied, covering all scenarios.
- **F (8–10):** comprehensive, well-structured user *and* technical documentation.

### 2.2 EDGE attributes to show

- **Ethical:** protect user data (hashed passwords, local-only storage, permission minimisation,
  privacy notice, data-deletion option).
- **Digital:** modular design with control structures; complex problems split into small
  components (layers, reusable components).
- **Entrepreneurial:** show how the app improves the business process (online ordering, less
  walk-in load, promotions drive bulk orders).

---

## 3. Stakeholders and actors

| Actor | Description |
|-------|-------------|
| Guest | Unregistered user. Can view onboarding, catalogue, gallery, guidelines and FAQs. |
| Customer | Registered user. Has full ordering, tracking, profile and notification features. |
| System (Scheduler) | Background worker. Advances order status (simulated back-office) and sends notifications and seasonal promos. |
| Support staff (external) | Reached through call, email or WhatsApp intents, or a support ticket stored locally. |

---

## 4. Functional requirements

Priority uses MoSCoW: **M** Must, **S** Should, **C** Could.

### FR-1 Authentication and profile

| ID | Requirement | P |
|----|-------------|---|
| FR-1.1 | Register with full name, email **or** Sri Lankan mobile number, password + confirm, accept terms | M |
| FR-1.2 | Log in with email **or** phone number + password (toggle) | M |
| FR-1.3 | Persist the session (remember me) and log out | M |
| FR-1.4 | View and edit profile (name, email, phone, profile photo) | M |
| FR-1.5 | Change password (verify current password) | S |
| FR-1.6 | Manage delivery addresses: add, edit, delete, set default (district drives delivery fee) | M |
| FR-1.7 | Saved designs library: list, preview, delete, reuse when ordering | M |
| FR-1.8 | Order history (completed and cancelled orders) | M |
| FR-1.9 | Reset password locally (verify registered email + phone pair) | C |
| FR-1.10 | Delete account and all local data (ethical requirement) | S |

### FR-2 Product browsing and ordering

| ID | Requirement | P |
|----|-------------|---|
| FR-2.1 | Show product categories (Business Cards, Flyers, Posters, Banners, Stickers, Mugs, T-Shirts) | M |
| FR-2.2 | List products per category with image, starting price and turnaround | M |
| FR-2.3 | Filter by type, material and size, sort by price, search by name | M |
| FR-2.4 | Product detail: images, description, specs, price table, sample previews | M |
| FR-2.5 | Configure specs: quantity (min/max), size, paper/material, finish, sides and colour, with a live price | M |
| FR-2.6 | Upload artwork (PDF/PNG/JPG, ≤ 25 MB) through the system file picker, **or** enter custom text | M |
| FR-2.7 | Save the uploaded design to "Saved designs" | S |
| FR-2.8 | Add configured item to cart; edit or remove cart items | M |
| FR-2.9 | Choose fulfilment: Pickup (branch) or Home delivery (address) | M |
| FR-2.10 | Schedule date + time slot (≥ today + product turnaround days; no Sundays or holidays) | M |
| FR-2.11 | Apply promo code; show subtotal, discount, delivery fee and total | M |
| FR-2.12 | Choose payment method (Pay at Pickup / Cash on Delivery) and place the order | M |
| FR-2.13 | Order confirmation screen with order number | M |

### FR-3 Order management

| ID | Requirement | P |
|----|-------------|---|
| FR-3.1 | List orders in Active and Past tabs, with status badge | M |
| FR-3.2 | Order detail with a status timeline (Pending → Confirmed → Processing → Printing → Ready/Out for delivery → Completed) | M |
| FR-3.3 | Reschedule pickup or delivery date/slot **only before PRINTING** | M |
| FR-3.4 | Cancel order with a reason **only before PRINTING** | M |
| FR-3.5 | Reorder a past order | C |
| FR-3.6 | Status history is stored with timestamps | M |

### FR-4 Notifications and offers

| ID | Requirement | P |
|----|-------------|---|
| FR-4.1 | Local notification on order confirmation | M |
| FR-4.2 | Notification on each status change, including completion or ready for pickup | M |
| FR-4.3 | Seasonal promotion notifications (e.g. Avurudu, Vesak, Christmas, bulk discounts) | M |
| FR-4.4 | In-app notification centre (read/unread, mark all read, deep link to order or offer) | M |
| FR-4.5 | Offers screen listing active promotions and packages with a copyable code | M |
| FR-4.6 | Notification preferences (orders on/off, promos on/off) | S |

### FR-5 Design support and resources

| ID | Requirement | P |
|----|-------------|---|
| FR-5.1 | Print guidelines: file formats, resolution (300 DPI), colour modes (CMYK vs RGB), bleed and safe margins | M |
| FR-5.2 | Sample design gallery by category, with a full-screen viewer | M |
| FR-5.3 | Searchable, expandable FAQs | M |
| FR-5.4 | Customer support: call, email, WhatsApp intents + support ticket form (optionally linked to an order) | M |
| FR-5.5 | Downloadable/shareable bleed template image | C |

---

## 5. Non-functional requirements

| ID | Category | Requirement |
|----|----------|-------------|
| NFR-1 | Platform | Native Android (Java), minSdk 24 (≈97% of devices), targetSdk 36 |
| NFR-2 | Performance | Cold start < 2 s. List scrolling at 60 fps. All DB work off the main thread |
| NFR-3 | Usability | Material 3 guidelines, touch targets ≥ 48 dp, max 3 taps to start an order |
| NFR-4 | Accessibility | `contentDescription` on images, contrast ≥ 4.5:1, supports font scaling |
| NFR-5 | Security | PBKDF2 password hashing + salt, parameterised SQL, app-private file storage, no plaintext secrets |
| NFR-6 | Privacy | Minimal permissions (POST_NOTIFICATIONS, no storage permission thanks to the SAF picker), in-app privacy notice, account delete |
| NFR-7 | Reliability | FK constraints on, transactions for multi-table writes (order + items + options + history) |
| NFR-8 | Maintainability | MVVM layering, reusable components, Javadoc on all public APIs, no `//` comments |
| NFR-9 | Offline | Fully functional offline (local SQLite) |
| NFR-10 | Localisation-ready | All text in `strings.xml` (English now; Sinhala/Tamil could be added) |
| NFR-11 | Theming | Light + dark themes from one token set |

---

## 6. Assumptions and scope limits (state these in the report)

1. **No server:** the brief asks for "database integration", so we use on-device SQLite. There is no
   admin app, so back-office progress is **simulated** by `OrderProgressWorker` (WorkManager), which
   advances statuses and raises notifications. A developer switch can speed it up for demos.
2. **Payments:** no payment gateway. The app supports Pay at Pickup and Cash on Delivery only.
3. **Phone login:** no SMS gateway, so there is no OTP. The phone number is used as a login identifier with a password.
4. **Uploads:** files are copied from the Storage Access Framework URI into app-private storage
   (`files/designs/`). Only the internal path goes in the DB.
5. **Promotions/catalogue:** seeded by `DatabaseSeeder` on first run.
6. **Pickup branch:** a small seeded `branches` list (Colombo, Kandy, Galle).
7. Delivery fee depends on district (25 Sri Lankan districts are seeded).

---

## 7. Business rules

| ID | Rule |
|----|------|
| BR-1 | Email must be unique. Phone must be unique. At least one of them is required. |
| BR-2 | Password ≥ 8 characters with an upper-case letter, a lower-case letter, a digit and a symbol. |
| BR-3 | Quantity must be between the product `min_qty` and `max_qty` and must be a multiple of `qty_step` (e.g. cards in 50s). |
| BR-4 | Bulk discount: automatic 10% for ≥ 500 units, 15% for ≥ 1000 (`BulkDiscountPricing`). |
| BR-5 | Only one promo code per order. The code must be active, within its dates, and meet the minimum order value. |
| BR-6 | Earliest schedule date = today + product turnaround days (max over cart items). No Sundays. |
| BR-7 | Cancel/reschedule are allowed only in PENDING, CONFIRMED or PROCESSING. |
| BR-8 | Home delivery requires an address. Pickup requires a branch. |
| BR-9 | Each cart item must have either an uploaded file or custom text (not neither). |
| BR-10 | Upload: MIME in {application/pdf, image/png, image/jpeg}, size ≤ 25 MB. |
| BR-11 | Order number format `PX-YYMMDD-NNNN`. |

---

## 8. Validation matrix (supports the Task D "robust validation" criterion)

| Field | Rules (classes in `core/validation`) | Error string key |
|-------|--------------------------------------|------------------|
| Full name | Required, Length 3–60, Regex letters/space/.' | `err_name_invalid` |
| Email | Required-if-no-phone, EmailRule, Unique (repo) | `err_email_invalid`, `err_email_taken` |
| Phone | Required-if-no-email, SriLankanPhoneRule, Unique (repo) | `err_phone_invalid` |
| Password | Required, PasswordStrengthRule | `err_password_weak` |
| Confirm password | MatchRule(password) | `err_password_mismatch` |
| Address line 1 | Required, Length 5–100 | `err_address_invalid` |
| City | Required, letters | `err_city_invalid` |
| District | Required (dropdown) | `err_district_required` |
| Postal code | Regex `^\d{5}$` | `err_postal_invalid` |
| Quantity | Required, Numeric, Range(min,max), Step | `err_qty_range` |
| Custom text | Max 250 chars; required if no file | `err_design_required` |
| Upload | FileTypeRule, FileSizeRule | `err_file_type`, `err_file_size` |
| Schedule date | Required, MinDateRule(turnaround), NotSundayRule | `err_date_invalid` |
| Time slot | Required | `err_slot_required` |
| Promo code | Regex `^[A-Z0-9]{4,12}$`, exists/active (repo) | `err_promo_invalid` |
| Ticket subject/message | Required, Length 5–80 / 10–1000 | `err_ticket_invalid` |
| Cancel reason | Required (radio) + optional text ≤ 200 | `err_reason_required` |
