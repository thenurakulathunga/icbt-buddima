package com.printxpress.app.ui.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntConsumer;

/**
 * Makes a set of {@link SelectableOptionCard}s behave like radio buttons.
 *
 * @since 1.0
 */
public final class SelectableCardGroup {

    /** The cards in order. */
    private final List<SelectableOptionCard> cards;
    /** Selection callback, may be null. */
    private IntConsumer listener;

    /**
     * Groups cards.
     *
     * @param cards the cards in order
     */
    private SelectableCardGroup(SelectableOptionCard... cards) {
        this.cards = new ArrayList<>(Arrays.asList(cards));
        for (int i = 0; i < this.cards.size(); i++) {
            final int index = i;
            this.cards.get(i).setOnClickListener(v -> select(index, true));
        }
    }

    /**
     * Creates a group.
     *
     * @param cards the cards in order
     * @return the group
     */
    public static SelectableCardGroup of(SelectableOptionCard... cards) {
        return new SelectableCardGroup(cards);
    }

    /**
     * Selects a card.
     *
     * @param index  the card index, or -1 to clear
     * @param notify whether to call the listener
     */
    public void select(int index, boolean notify) {
        for (int i = 0; i < cards.size(); i++) {
            cards.get(i).setChecked(i == index);
        }
        if (notify && listener != null && index >= 0) {
            listener.accept(index);
        }
    }

    /** @return the selected index, or -1 */
    public int getSelectedIndex() {
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i).isChecked()) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Registers a selection callback.
     *
     * @param onSelected receives the selected index
     */
    public void setOnSelectionChanged(IntConsumer onSelected) {
        this.listener = onSelected;
    }
}
