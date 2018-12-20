package org.openbase.bco.bcozy.util;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.openbase.bco.bcozy.model.LanguageSelection;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.extension.rst.processing.LabelProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openbase.type.language.LabelType.Label;
import org.openbase.type.language.LabelType.LabelOrBuilder;

import java.util.*;

/**
 * Helper class to synchronize a list of labels with a list of text properties which keeps updated with the currently configured locale of this vm instance.
 */
public class MultiLabelSynchronizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiLabelSynchronizer.class);

    private final Map<javafx.scene.control.Label, LabelSynchronizer> labelSynchronizerMap;

    /**
     * Constructor creates a new label synchronizer.
     */
    public MultiLabelSynchronizer(){
        this.labelSynchronizerMap = new HashMap<>();
    }

    public javafx.scene.control.Label register(final Label label) {
        return register(label, new javafx.scene.control.Label());
    }

    public javafx.scene.control.Label register(final Label label, final javafx.scene.control.Label fxlabel) {
        final LabelSynchronizer labelSynchronizer = new LabelSynchronizer(label);
        labelSynchronizerMap.put(fxlabel, labelSynchronizer);
        fxlabel.textProperty().bind(labelSynchronizer.textProperty());
        return fxlabel;
    }

    public void remove(final javafx.scene.control.Label fxlabel) {
        LabelSynchronizer labelSynchronizer = labelSynchronizerMap.get(fxlabel);
        labelSynchronizer.textProperty().unbindBidirectional(fxlabel.textProperty());
        labelSynchronizer.shutdown();
        labelSynchronizerMap.remove(fxlabel);
    }

    public Collection<javafx.scene.control.Label> removeAll() {
        final ArrayList<javafx.scene.control.Label> labels = new ArrayList<>(getLabels());
        for (final javafx.scene.control.Label label : labels) {
            remove(label);
        }
        return labels;
    }

    public Map<javafx.scene.control.Label, LabelSynchronizer> getLabelSynchronizerMap() {
        return labelSynchronizerMap;
    }

    public Collection<javafx.scene.control.Label> getLabels() {
        return labelSynchronizerMap.keySet();
    }

    /**
     * Updates the label which is than synchronized with the text property.
     *
     * @param label the label to set.
     */
    public synchronized void updateLabel(final LabelOrBuilder label, final javafx.scene.control.Label fxlabel) {
        labelSynchronizerMap.get(fxlabel).updateLabel(label);
    }

    public void clearLabels() {
        for (LabelSynchronizer labelSynchronizer : labelSynchronizerMap.values()) {
            labelSynchronizer.clearLabels();
        }
    }
}

