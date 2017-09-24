package org.openbase.bco.bcozy.controller;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;

import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * @author vdasilva
 */

public class ButtonTableCell<S, T> extends TableCell<S, T> {

    final Button btn = new Button();

    public ButtonTableCell(BiConsumer<S, Integer> onAction, Node graphic) {
        Objects.requireNonNull(onAction);
        Objects.requireNonNull(graphic);

        btn.setOnAction(event -> onAction.accept(getTableView().getItems().get(getIndex()), getIndex()));
        btn.setGraphic(graphic);
    }

    @Override
    public void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
            setText(null);
        } else {
            setGraphic(btn);
            setText(null);
        }
    }
}
