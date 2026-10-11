# 04 – UML Diagrams (Task B)

All diagrams are in **PlantUML**. Render them at https://www.plantuml.com/plantuml, in the VS Code / IntelliJ
PlantUML plugin, or redraw them in draw.io / StarUML for the report. Under each diagram, add one
paragraph on the **design decisions** (the rubric rewards justification).

---

## 1. Use case diagram

```plantuml
@startuml
left to right direction
skinparam packageStyle rectangle
actor Guest
actor Customer
actor "Scheduler\n(System)" as Sys
actor "Support Staff" as Staff
Guest <|-- Customer

rectangle PrintXpress {
  usecase "Register" as UC1
  usecase "Login (email / phone)" as UC2
  usecase "Browse & Filter Products" as UC3
  usecase "View Product Details" as UC4
  usecase "View Design Gallery" as UC5
  usecase "Read Print Guidelines" as UC6
  usecase "Read FAQs" as UC7
  usecase "Configure Product Specs" as UC8
  usecase "Upload Artwork" as UC9
  usecase "Enter Custom Text" as UC10
  usecase "Manage Cart" as UC11
  usecase "Place Order" as UC12
  usecase "Choose Pickup / Delivery\n& Schedule" as UC13
  usecase "Apply Promo Code" as UC14
  usecase "Track Order" as UC15
  usecase "Reschedule Order" as UC16
  usecase "Cancel Order" as UC17
  usecase "View Notifications" as UC18
  usecase "View Offers" as UC19
  usecase "Manage Profile" as UC20
  usecase "Manage Addresses" as UC21
  usecase "Manage Saved Designs" as UC22
  usecase "View Order History" as UC23
  usecase "Contact Support / Raise Ticket" as UC24
  usecase "Update Order Status" as UC25
  usecase "Send Notification" as UC26
  usecase "Validate Order State\n(before PRINTING)" as UC27
}

Guest --> UC1
Guest --> UC2
Guest --> UC3
Guest --> UC4
Guest --> UC5
Guest --> UC6
Guest --> UC7
Customer --> UC11
Customer --> UC12
Customer --> UC15
Customer --> UC18
Customer --> UC19
Customer --> UC20
Customer --> UC23
Customer --> UC24
UC3 <.. UC4 : <<extend>>
UC12 ..> UC8 : <<include>>
UC12 ..> UC13 : <<include>>
UC9 ..> UC8 : <<extend>>
UC10 ..> UC8 : <<extend>>
UC14 ..> UC12 : <<extend>>
UC15 <.. UC16 : <<extend>>
UC15 <.. UC17 : <<extend>>
UC16 ..> UC27 : <<include>>
UC17 ..> UC27 : <<include>>
UC20 <.. UC21 : <<extend>>
UC20 <.. UC22 : <<extend>>
Sys --> UC25
Sys --> UC26
UC25 ..> UC26 : <<include>>
UC24 --> Staff
@enduml
```

**Design decision notes:** Customer specialises Guest (inheritance of actors), so browsing is open
without an account and there is less friction. Cancel and Reschedule `<<include>>` a state check because BR-7
applies to both. The Scheduler is modelled as a system actor because there is no admin app.

---

## 2. Class diagram (domain + key architecture)

```plantuml
@startuml
skinparam classAttributeIconSize 0
package model {
  class User {
    -userId:long
    -fullName:String
    -email:String
    -phone:String
    -passwordHash:String
    -passwordSalt:String
    +getters/setters()
  }
  class Address {
    -addressId:long
    -userId:long
    -districtId:long
    -label:String
    -line1:String
    -city:String
    -postalCode:String
    -isDefault:boolean
  }
  class District {
    -districtId:long
    -name:String
    -deliveryFee:Money
  }
  class Branch {
    -branchId:long
    -name:String
    -address:String
  }
  class Category {
    -categoryId:long
    -name:String
    -iconRes:String
  }
  class Product {
    -productId:long
    -categoryId:long
    -name:String
    -basePrice:Money
    -minQty:int
    -maxQty:int
    -qtyStep:int
    -turnaroundDays:int
  }
  class OptionType {
    -optionTypeId:long
    -name:String
  }
  class ProductOption {
    -optionId:long
    -value:String
    -priceModifier:Money
  }
  class Design {
    -designId:long
    -title:String
    -filePath:String
    -mimeType:String
    -customText:String
  }
  class CartItem {
    -cartItemId:long
    -quantity:int
    -options:List<ProductOption>
    +Builder
  }
  class Order {
    -orderId:long
    -orderNumber:String
    -fulfilmentType:FulfilmentType
    -paymentMethod:PaymentMethod
    -scheduledDate:LocalDate
    -slot:String
    -breakdown:PriceBreakdown
    -state:OrderState
    +cancel(reason)
    +reschedule(date,slot)
    +advance()
    +Builder
  }
  class OrderItem {
    -orderItemId:long
    -quantity:int
    -unitPrice:Money
    -lineTotal:Money
  }
  class OrderStatusHistory {
    -status:OrderStatus
    -remark:String
    -changedAt:LocalDateTime
  }
  class Promotion {
    -promoCode:String
    -discountType:DiscountType
    -discountValue:double
    -startDate:LocalDate
    -endDate:LocalDate
    +isValidOn(date):boolean
  }
  class AppNotification {
    -type:NotificationType
    -title:String
    -message:String
    -isRead:boolean
  }
  class Money <<value object>> {
    -amount:BigDecimal
    +plus(Money)
    +times(int)
    +percent(double)
  }
  enum OrderStatus {
    PENDING
    CONFIRMED
    PROCESSING
    PRINTING
    READY_FOR_PICKUP
    OUT_FOR_DELIVERY
    COMPLETED
    CANCELLED
  }
  enum FulfilmentType {
    PICKUP
    DELIVERY
  }
}

User "1" -- "0..*" Address
Address "*" -- "1" District
User "1" -- "0..*" Order
User "1" -- "0..*" Design
User "1" -- "0..*" CartItem
Category "1" -- "0..*" Product
Product "1" -- "0..*" ProductOption
OptionType "1" -- "0..*" ProductOption
Order "1" *-- "1..*" OrderItem
Order "1" *-- "1..*" OrderStatusHistory
OrderItem "*" -- "1" Product
OrderItem "*" -- "0..*" ProductOption
OrderItem "*" -- "0..1" Design
Order "*" -- "0..1" Promotion
Order "*" -- "0..1" Address
Order "*" -- "0..1" Branch
User "1" -- "0..*" AppNotification

package domain.order {
  interface OrderState {
    +getStatus():OrderStatus
    +canCancel():boolean
    +canReschedule():boolean
    +next(FulfilmentType):OrderState
  }
  abstract class AbstractOrderState
  class PendingState
  class ConfirmedState
  class ProcessingState
  class PrintingState
  class ReadyForPickupState
  class OutForDeliveryState
  class CompletedState
  class CancelledState
  class OrderStateFactory {
    +{static} from(OrderStatus):OrderState
  }
}
OrderState <|.. AbstractOrderState
AbstractOrderState <|-- PendingState
AbstractOrderState <|-- ConfirmedState
AbstractOrderState <|-- ProcessingState
AbstractOrderState <|-- PrintingState
AbstractOrderState <|-- ReadyForPickupState
AbstractOrderState <|-- OutForDeliveryState
AbstractOrderState <|-- CompletedState
AbstractOrderState <|-- CancelledState
Order o-- OrderState

package domain.pricing {
  interface PricingStrategy {
    +apply(PricingContext, Money):Money
  }
  class OptionModifierPricing
  class BulkDiscountPricing
  class PromoCodePricing
  class CompositePricingStrategy {
    -strategies:List<PricingStrategy>
  }
  class PriceCalculator {
    +calculate(List<CartItem>, Promotion, FulfilmentStrategy):PriceBreakdown
  }
}
PricingStrategy <|.. OptionModifierPricing
PricingStrategy <|.. BulkDiscountPricing
PricingStrategy <|.. PromoCodePricing
PricingStrategy <|.. CompositePricingStrategy
CompositePricingStrategy o-- PricingStrategy
PriceCalculator --> CompositePricingStrategy

package domain.fulfilment {
  interface FulfilmentStrategy {
    +deliveryFee():Money
    +earliestDate(int):LocalDate
  }
  class PickupStrategy
  class HomeDeliveryStrategy
}
FulfilmentStrategy <|.. PickupStrategy
FulfilmentStrategy <|.. HomeDeliveryStrategy
PriceCalculator --> FulfilmentStrategy

package core.validation {
  interface ValidationRule {
    +validate(String):ValidationResult
  }
  class FormValidator {
    +addField(PxTextInput, ValidationRule...)
    +validateAll():boolean
  }
  class EmailRule
  class SriLankanPhoneRule
  class PasswordStrengthRule
  class RangeRule
}
ValidationRule <|.. EmailRule
ValidationRule <|.. SriLankanPhoneRule
ValidationRule <|.. PasswordStrengthRule
ValidationRule <|.. RangeRule
FormValidator o-- ValidationRule

package data {
  class DatabaseHelper <<Singleton>> {
    -{static} instance
    +{static} getInstance(Context)
  }
  abstract class "BaseDao<T>" as BaseDao {
    #db:DatabaseHelper
    #{abstract} map(Cursor):T
    #{abstract} toValues(T):ContentValues
  }
  class OrderDao
  class OrderRepository {
    +placeOrder(Order):LiveData<Resource<Long>>
    +cancelOrder(long,String)
    +reschedule(long,LocalDate,String)
    +getActiveOrders(long)
  }
}
BaseDao <|-- OrderDao
OrderDao --> DatabaseHelper
OrderRepository --> OrderDao

package ui {
  abstract class "BaseViewModel" as BVM
  class OrderDetailViewModel
  class OrderDetailFragment
  class ViewModelFactory <<Factory>>
}
BVM <|-- OrderDetailViewModel
OrderDetailViewModel --> OrderRepository
OrderDetailFragment ..> OrderDetailViewModel : observes LiveData
ViewModelFactory ..> OrderDetailViewModel : creates
@enduml
```

**Design decision notes:** Composition (`*--`) between Order and OrderItem/History, because lines have no
life without the order (this matches `ON DELETE CASCADE`). The State pattern encapsulates BR-7. The Strategy
+ Composite patterns allow new discount types without changing existing code (Open/Closed principle). `Money`
uses `BigDecimal` to avoid floating-point rounding errors on currency.

---

## 3. Activity diagram – Place an order

```plantuml
@startuml
start
:Open product detail;
:Select size, paper/material, finish, sides;
repeat
  :Enter quantity;
repeat while (qty within min/max and step?) is (no) not (yes)
:Live price recalculated (PricingStrategy);
if (Provide design by?) then (Upload file)
  :Open system file picker (SAF);
  if (type in PDF/PNG/JPG and size <= 25 MB?) then (yes)
    :Copy to app storage; optionally save to Saved Designs;
  else (no)
    :Show validation error;
    stop
  endif
else (Custom text)
  :Enter text (<= 250 chars);
endif
:Add to cart;
if (Logged in?) then (no)
  :Redirect to Login / Register;
endif
:Open checkout;
if (Fulfilment?) then (Home delivery)
  :Select / add address;
  :Delivery fee from district;
else (Pickup)
  :Select branch;
endif
:Pick date (>= today + turnaround, not Sunday) and time slot;
if (Promo code entered?) then (yes)
  if (Valid & active & min value met?) then (yes)
    :Apply discount;
  else (no)
    :Show "invalid promo" error;
  endif
endif
:Choose payment (Pay at pickup / COD);
:Review summary;
:Place order;
fork
  :DB transaction: orders + items + options + history; clear cart;
fork again
  :Post "Order confirmed" notification;
fork again
  :Enqueue OrderProgressWorker;
end fork
:Show Order Success (PX-YYMMDD-NNNN);
stop
@enduml
```

## 4. Activity diagram – Cancel / reschedule

```plantuml
@startuml
start
:Open order detail;
:Tap Cancel or Reschedule;
if (OrderState.canCancel()/canReschedule()?) then (yes: PENDING/CONFIRMED/PROCESSING)
  if (Action?) then (Cancel)
    :Choose reason (required);
    :Confirm dialog;
    :UPDATE status = CANCELLED + history row;
    :Notify "Order cancelled";
  else (Reschedule)
    :Pick new date + slot (validated);
    :UPDATE scheduled_date/slot + history remark;
    :Notify "Order rescheduled";
  endif
else (no: PRINTING or later)
  :Button disabled + info "Printing has started";
endif
stop
@enduml
```

## 5. Activity diagram – Register / Login

```plantuml
@startuml
start
if (Has account?) then (no)
  :Enter name, email or phone, password, confirm;
  :FormValidator.validateAll();
  if (valid?) then (yes)
    if (email/phone unique?) then (yes)
      :Hash password (PBKDF2 + salt);
      :INSERT users;
      :Create session;
    else (no)
      :Show "already registered";
      stop
    endif
  else (no)
    :Inline field errors;
    stop
  endif
else (yes)
  :Enter email/phone + password;
  :Find user by identifier;
  if (hash matches?) then (yes)
    :Create session (SessionManager);
  else (no)
    :Show "Invalid credentials" (count attempts);
    stop
  endif
endif
:Open MainActivity (Home);
stop
@enduml
```

## 6. State machine diagram – Order status

```plantuml
@startuml
[*] --> PENDING : place order
PENDING --> CONFIRMED
CONFIRMED --> PROCESSING
PROCESSING --> PRINTING
PRINTING --> READY_FOR_PICKUP : [PICKUP]
PRINTING --> OUT_FOR_DELIVERY : [DELIVERY]
READY_FOR_PICKUP --> COMPLETED
OUT_FOR_DELIVERY --> COMPLETED
PENDING --> CANCELLED : cancel
CONFIRMED --> CANCELLED : cancel
PROCESSING --> CANCELLED : cancel
PENDING --> PENDING : reschedule
CONFIRMED --> CONFIRMED : reschedule
PROCESSING --> PROCESSING : reschedule
COMPLETED --> [*]
CANCELLED --> [*]
@enduml
```

## 7. Sequence diagram – Place order (for the technical documentation)

```plantuml
@startuml
actor Customer
participant CheckoutFragment as F
participant CheckoutViewModel as VM
participant FormValidator as V
participant PriceCalculator as P
participant OrderRepository as R
participant OrderDao as D
database SQLite as DB
participant NotificationHelper as N
participant WorkManager as W

Customer -> F : tap "Place order"
F -> VM : placeOrder()
VM -> V : validateAll()
V --> VM : valid
VM -> P : calculate(cart, promo, fulfilment)
P --> VM : PriceBreakdown
VM -> R : placeOrder(order)
R -> D : insertOrderWithItems(order) [diskIO]
D -> DB : BEGIN; INSERT orders/items/options/history; DELETE cart; COMMIT
DB --> D : orderId
D --> R : orderId
R -> N : notifyOrderConfirmed(order)
R -> W : enqueue OrderProgressWorker(orderId)
R --> VM : Resource.success(orderId)
VM --> F : LiveData update
F -> Customer : navigate to Order Success
@enduml
```
