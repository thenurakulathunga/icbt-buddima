/**
 * Converts Hugeicons Free (Stroke Rounded) icons from the npm package
 * `@hugeicons/core-free-icons` into Android vector drawables named by meaning (docs/05 §5).
 *
 * Usage: node tools/hugeicons_to_vector.mjs <path-to-package/dist/cjs> <res/drawable dir>
 *
 * `currentColor` becomes #FF000000 and the vector root gets a theme tint, so each icon follows
 * ?attr/colorOnSurface by default and can be re-tinted with app:tint in layouts.
 */
import { createRequire } from 'node:module';
import { writeFileSync } from 'node:fs';
import { join, resolve } from 'node:path';

const [, , iconDir, outDir] = process.argv;
const require = createRequire(import.meta.url);

/** Semantic resource name -> Hugeicons export name (without the Icon suffix). */
const ICONS = {
  ic_nav_home: 'Home01', ic_nav_catalog: 'GridView', ic_nav_orders: 'Invoice01',
  ic_nav_support: 'CustomerSupport', ic_nav_profile: 'UserCircle',
  ic_notifications: 'Notification01', ic_cart: 'ShoppingCart01', ic_search: 'Search01',
  ic_back: 'ArrowLeft01', ic_close: 'Cancel01', ic_more: 'MoreVertical',
  ic_filter: 'FilterHorizontal', ic_sort: 'Sorting01',
  ic_cat_business_cards: 'IdentityCard', ic_cat_flyers: 'File01', ic_cat_posters: 'Image01',
  ic_cat_banners: 'Flag01', ic_cat_stickers: 'Sticker', ic_cat_mugs: 'Coffee01', ic_cat_tshirts: 'TShirt',
  ic_upload: 'FileUpload', ic_text: 'TextFont', ic_add: 'Add01', ic_minus: 'MinusSign',
  ic_delete: 'Delete02', ic_edit: 'PencilEdit01', ic_image: 'Image01', ic_pdf: 'Pdf01',
  ic_offer: 'DiscountTag01', ic_calendar: 'Calendar03', ic_time: 'Clock01', ic_pickup: 'Store01',
  ic_delivery: 'DeliveryTruck01', ic_payment: 'Cash01', ic_check: 'CheckmarkCircle02',
  ic_copy: 'Copy01', ic_chevron_right: 'ArrowRight01', ic_chevron_down: 'ArrowDown01',
  ic_chevron_up: 'ArrowUp01', ic_share: 'Share08',
  ic_status_pending: 'Clock01', ic_status_confirmed: 'CheckmarkCircle02', ic_status_processing: 'Loading03',
  ic_status_printing: 'Printer', ic_status_ready: 'Store01', ic_status_delivery: 'DeliveryTruck01',
  ic_status_completed: 'TaskDone01', ic_status_cancelled: 'CancelCircle',
  ic_mail: 'Mail01', ic_phone: 'SmartPhone01', ic_lock: 'Lock', ic_visibility: 'View',
  ic_visibility_off: 'ViewOff', ic_person: 'UserCircle', ic_location: 'Location01',
  ic_saved_designs: 'Folder01', ic_history: 'History', ic_logout: 'Logout01', ic_settings: 'Settings02',
  ic_dark_mode: 'Moon02', ic_light_mode: 'Sun03', ic_privacy: 'Shield01', ic_camera: 'Camera01',
  ic_guidelines: 'Book02', ic_faq: 'HelpCircle', ic_whatsapp: 'Chat01', ic_call: 'Call',
  ic_ticket: 'Ticket01', ic_colour: 'Palette', ic_bleed: 'Crop', ic_dpi: 'Hd', ic_font: 'TextFont',
  ic_info: 'InformationCircle', ic_warning: 'Alert02', ic_error: 'AlertCircle',
  ic_offline: 'WifiDisconnected01', ic_empty: 'Inbox', ic_promo: 'Megaphone01', ic_gift: 'Gift',
  ic_refresh: 'Refresh', ic_download: 'Download04', ic_mark_all_read: 'TickDouble02',
  ic_licence: 'License', ic_delete_account: 'UserRemove01', ic_speed: 'Flash', ic_package: 'Package',
  ic_note: 'Note01', ic_home_address: 'Home09', ic_work_address: 'Building03', ic_zoom: 'ZoomInArea',
  ic_tag: 'Tag01', ic_send: 'Sent', ic_layers: 'Layers01', ic_brush: 'PaintBrush04',
  ic_user_add: 'UserAdd01', ic_login: 'Login01', ic_sparkles: 'Sparkles', ic_idea: 'Idea01',
  ic_password: 'LockPassword',
};

const num = (v) => Number.parseFloat(v);

/** Converts a circle/ellipse to two arcs. */
function ellipsePath(cx, cy, rx, ry) {
  return `M${cx - rx},${cy}a${rx},${ry} 0 1,0 ${rx * 2},0a${rx},${ry} 0 1,0 ${-rx * 2},0Z`;
}

/** Converts a rounded rectangle to path data. */
function rectPath(x, y, w, h, rx, ry) {
  if (!rx && !ry) return `M${x},${y}h${w}v${h}h${-w}Z`;
  rx = Math.min(rx || ry, w / 2); ry = Math.min(ry || rx, h / 2);
  return `M${x + rx},${y}h${w - 2 * rx}a${rx},${ry} 0 0,1 ${rx},${ry}v${h - 2 * ry}` +
    `a${rx},${ry} 0 0,1 ${-rx},${ry}h${-(w - 2 * rx)}a${rx},${ry} 0 0,1 ${-rx},${-ry}` +
    `v${-(h - 2 * ry)}a${rx},${ry} 0 0,1 ${rx},${-ry}Z`;
}

function toPathData(tag, a) {
  switch (tag) {
    case 'path': return a.d;
    case 'circle': return ellipsePath(num(a.cx), num(a.cy), num(a.r), num(a.r));
    case 'ellipse': return ellipsePath(num(a.cx), num(a.cy), num(a.rx), num(a.ry));
    case 'rect': return rectPath(num(a.x || 0), num(a.y || 0), num(a.width), num(a.height),
      num(a.rx || 0), num(a.ry || 0));
    default: throw new Error(`Unsupported element ${tag}`);
  }
}

const colour = (v) => (v && v !== 'none' ? '#FF000000' : null);

function toVector(elements) {
  const paths = elements.map(([tag, a]) => {
    const attrs = [`android:pathData="${toPathData(tag, a)}"`];
    const fill = colour(a.fill);
    const stroke = colour(a.stroke);
    if (fill) attrs.push(`android:fillColor="${fill}"`);
    if (a.fillRule === 'evenodd') attrs.push('android:fillType="evenOdd"');
    if (stroke) {
      attrs.push(`android:strokeColor="${stroke}"`, `android:strokeWidth="${a.strokeWidth || 1.5}"`);
      if (a.strokeLinecap) attrs.push(`android:strokeLineCap="${a.strokeLinecap}"`);
      if (a.strokeLinejoin) attrs.push(`android:strokeLineJoin="${a.strokeLinejoin}"`);
    }
    if (a.opacity) attrs.push(`android:fillAlpha="${a.opacity}"`, `android:strokeAlpha="${a.opacity}"`);
    return `    <path\n        ${attrs.join('\n        ')} />`;
  });
  return '<?xml version="1.0" encoding="utf-8"?>\n' +
    '<!-- Hugeicons Free, Stroke Rounded (MIT). Generated by tools/hugeicons_to_vector.mjs. -->\n' +
    '<vector xmlns:android="http://schemas.android.com/apk/res/android"\n' +
    '    android:width="24dp"\n    android:height="24dp"\n' +
    '    android:viewportWidth="24"\n    android:viewportHeight="24"\n' +
    '    android:tint="?attr/colorOnSurface">\n' + paths.join('\n') + '\n</vector>\n';
}

for (const [res, name] of Object.entries(ICONS)) {
  const elements = require(resolve(join(iconDir, `${name}Icon.js`)));
  writeFileSync(join(outDir, `${res}.xml`), toVector(elements));
}
console.log(`Wrote ${Object.keys(ICONS).length} icons`);
