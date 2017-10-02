package org.openbase.bco.bcozy.controller;

import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * @author vdasilva
 */
public class ButtonTableCellFactory<S, T> implements Callback<TableColumn<S, T>, TableCell<S, T>> {

    private final BiConsumer<S, Integer> onAction;
    private final Supplier<Node> graphicFactory;

    public ButtonTableCellFactory(BiConsumer<S, Integer> onAction, Supplier<Node> graphicFactory) {
        this.onAction = onAction;
        this.graphicFactory = graphicFactory;
    }

    @Override
    public TableCell<S, T> call(TableColumn<S, T> param) {
        return new ButtonTableCell<>(onAction, graphicFactory.get());
    }
}
