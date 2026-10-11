package com.printxpress.app.data.dao;

import android.content.ContentValues;
import android.database.sqlite.SQLiteOpenHelper;

import com.printxpress.app.core.util.DateTimeUtils;
import com.printxpress.app.data.db.CursorMapper;
import com.printxpress.app.data.db.DbContract.Categories;
import com.printxpress.app.data.db.DbContract.Promotions;
import com.printxpress.app.data.model.Promotion;
import com.printxpress.app.data.model.enums.DiscountType;

import java.time.LocalDate;
import java.util.List;

/**
 * Data access for {@code promotions} (FR-2.11, FR-4.5).
 *
 * @since 1.0
 */
public class PromotionDao extends BaseDao<Promotion> {

    /** Select with optional category join. */
    private static final String SELECT = "SELECT pr.*, c." + Categories.NAME + " AS category_name FROM "
            + Promotions.TABLE + " pr LEFT JOIN " + Categories.TABLE + " c ON c." + Categories.ID + " = pr."
            + Promotions.CATEGORY_ID;

    /** Row mapper for {@link Promotion}. */
    public static final CursorMapper<Promotion> MAPPER = row -> {
        Promotion p = new Promotion();
        p.setPromoId(row.getLong(Promotions.ID));
        p.setCategoryId(row.getNullableLong(Promotions.CATEGORY_ID));
        p.setTitle(row.getString(Promotions.TITLE));
        p.setDescription(row.getString(Promotions.DESCRIPTION));
        p.setPromoCode(row.getString(Promotions.PROMO_CODE));
        p.setDiscountType(DiscountType.from(row.getString(Promotions.DISCOUNT_TYPE)));
        p.setDiscountValue(row.getDouble(Promotions.DISCOUNT_VALUE));
        p.setMinOrderValue(row.getMoney(Promotions.MIN_ORDER_VALUE));
        p.setStartDate(row.getDate(Promotions.START_DATE));
        p.setEndDate(row.getDate(Promotions.END_DATE));
        p.setImageRes(row.getString(Promotions.IMAGE_RES));
        p.setActive(row.getBoolean(Promotions.IS_ACTIVE));
        p.setCategoryName(row.getString("category_name"));
        return p;
    };

    /**
     * Creates the DAO.
     *
     * @param helper the database helper
     */
    public PromotionDao(SQLiteOpenHelper helper) {
        super(helper);
    }

    /** @return the promotions table */
    @Override
    protected String table() {
        return Promotions.TABLE;
    }

    /** @return the promo id column */
    @Override
    protected String idColumn() {
        return Promotions.ID;
    }

    /** @return the promotion mapper */
    @Override
    protected CursorMapper<Promotion> mapper() {
        return MAPPER;
    }

    /**
     * Converts a promotion to values.
     *
     * @param p the promotion
     * @return the values
     */
    @Override
    protected ContentValues toValues(Promotion p) {
        ContentValues v = new ContentValues();
        putNullable(v, Promotions.CATEGORY_ID, p.getCategoryId());
        v.put(Promotions.TITLE, p.getTitle());
        v.put(Promotions.DESCRIPTION, p.getDescription());
        v.put(Promotions.PROMO_CODE, p.getPromoCode());
        v.put(Promotions.DISCOUNT_TYPE, p.getDiscountType().name());
        v.put(Promotions.DISCOUNT_VALUE, p.getDiscountValue());
        v.put(Promotions.MIN_ORDER_VALUE, p.getMinOrderValue().toDouble());
        v.put(Promotions.START_DATE, DateTimeUtils.toDbDate(p.getStartDate()));
        v.put(Promotions.END_DATE, DateTimeUtils.toDbDate(p.getEndDate()));
        v.put(Promotions.IMAGE_RES, p.getImageRes());
        v.put(Promotions.IS_ACTIVE, p.isActive() ? 1 : 0);
        return v;
    }

    /**
     * Returns the id.
     *
     * @param p the promotion
     * @return the id
     */
    @Override
    protected long getId(Promotion p) {
        return p.getPromoId();
    }

    /**
     * Stores the id.
     *
     * @param p  the promotion
     * @param id the id
     */
    @Override
    protected void setId(Promotion p, long id) {
        p.setPromoId(id);
    }

    /**
     * Finds a promotion with its category name.
     *
     * @param id the promotion id
     * @return the promotion, or null
     */
    @Override
    public Promotion findById(long id) {
        return querySingle(SELECT + " WHERE pr." + Promotions.ID + " = ?", String.valueOf(id));
    }

    /**
     * Lists promotions running on a date, ending soonest first.
     *
     * @param today the date
     * @return the running promotions
     */
    public List<Promotion> findActiveOn(LocalDate today) {
        String date = DateTimeUtils.toDbDate(today);
        return queryList(SELECT + " WHERE pr." + Promotions.IS_ACTIVE + " = 1 AND ? BETWEEN pr." + Promotions.START_DATE
                + " AND pr." + Promotions.END_DATE + " ORDER BY pr." + Promotions.END_DATE, date);
    }

    /**
     * Finds a promotion by code regardless of dates, so callers can tell "expired" from "unknown".
     *
     * @param code the upper-case code
     * @return the promotion, or null
     */
    public Promotion findByCode(String code) {
        return querySingle(SELECT + " WHERE pr." + Promotions.PROMO_CODE + " = ?", code);
    }

    /**
     * Finds a code valid on a date (docs/03 §6).
     *
     * @param code  the upper-case code
     * @param today the date
     * @return the promotion, or null when unknown, inactive or outside its dates
     */
    public Promotion findValid(String code, LocalDate today) {
        return querySingle(SELECT + " WHERE pr." + Promotions.PROMO_CODE + " = ? AND pr." + Promotions.IS_ACTIVE
                + " = 1 AND ? BETWEEN pr." + Promotions.START_DATE + " AND pr." + Promotions.END_DATE, code,
                DateTimeUtils.toDbDate(today));
    }
}
