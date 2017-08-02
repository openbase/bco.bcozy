package org.openbase.bco.bcozy.view.mainmenupanes;

import javafx.collections.ObservableList;
import javafx.scene.AccessibleAttribute;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @autor vdasilva
 */
class TabPaneSelectionModelImpl extends SingleSelectionModel<Tab> {

    public void setClickOnSelectedTabListener(ClickOnSelectedTabListener clickOnSelectedTabListener) {
        this.clickOnSelectedTabListener = clickOnSelectedTabListener;
    }

    /**
     * Listener, which ist called if an already selected tab is again selected.
     */
    interface ClickOnSelectedTabListener {
        /**
         * Called if an already selected tab is again selected.
         *
         * @param tab the selected Tab
         */
        void clickOnSelectedTab(Tab tab);
    }

    private ClickOnSelectedTabListener clickOnSelectedTabListener;

    public TabPaneSelectionModelImpl(final TabPane t) {
        this.tabPane = Objects.requireNonNull(t);
    }

    private final TabPane tabPane;

    /**
     * {@inheritDoc}
     */
    @Override
    public void select(int index) {
        if (index < 0 || (getItemCount() > 0 && index >= getItemCount()) ||
                (index == getSelectedIndex() && getModelItem(index).isSelected())) {
            //Tab already selected, call listener and return
            if (Objects.nonNull(clickOnSelectedTabListener)) {
                clickOnSelectedTabListener.clickOnSelectedTab(getModelItem(index));
            }
            return;
        }
        // Unselect the old tab
        setSelected(false);

        setSelectedIndex(index);

        Tab tab = getModelItem(index);
        if (tab != null) {
            setSelectedItem(tab);
        }

        // Select the new tab
        setSelected(true);

            /* Does this get all the change events */
        tabPane.notifyAccessibleAttributeChanged(AccessibleAttribute.FOCUS_ITEM);
    }

    private void setSelected(boolean selected) {

        if (getSelectedIndex() >= 0 && getSelectedIndex() < tabPane.getTabs().size()) {
            // protected, use reflection
            // tabPane.getTabs().get(getSelectedIndex()).setSelected(false);

            Tab tab = tabPane.getTabs().get(getSelectedIndex());
            setSelected(tab, selected);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void select(Tab tab) {
        final int itemCount = getItemCount();

        for (int i = 0; i < itemCount; i++) {
            final Tab value = getModelItem(i);
            if (value != null && value.equals(tab)) {
                select(i);
                return;
            }
        }
        if (tab != null) {
            setSelectedItem(tab);
        }
    }

    /**
     * Sets the tab as {@code selected}.
     *
     * @param newTab   the tab
     * @param selected if the tab is selected
     */
    private void setSelected(final Tab newTab, final boolean selected) {
        try {
            Method m = newTab.getClass().getDeclaredMethod("setSelected", boolean.class);
            m.setAccessible(true);
            m.invoke(newTab, selected);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Exception while access TabäsetSelected with Reflection, should never happen", e);
        }
    }

    @Override
    protected Tab getModelItem(int index) {
        final ObservableList<Tab> items = tabPane.getTabs();
        if (items == null) {
            return null;
        }
        if (index < 0 || index >= items.size()) {
            return null;
        }
        return items.get(index);
    }

    @Override
    protected int getItemCount() {
        final ObservableList<Tab> items = tabPane.getTabs();
        return items == null ? 0 : items.size();
    }

}