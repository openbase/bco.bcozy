package org.openbase.bco.bcozy.util;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.openbase.bco.bcozy.model.LanguageSelection;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.extension.rst.processing.LabelProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.configuration.LabelType.Label;
import rst.configuration.LabelType.LabelOrBuilder;

import java.util.Locale;

/**
 * Helper class to synchronize a label with a text property which keeps updated with the currently configured locale of this vm instance.
 */
public class LabelSynchronizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(LabelSynchronizer.class);

    private StringProperty textProperty = new SimpleStringProperty();
    private Label.Builder labelBuilder;

    /**
     * Constructor creates a new label synchronizer.
     * The text property will be empty until a label is passed via the {@code updateLabel(final LabelOrBuilder label)} method.
     */
    public LabelSynchronizer() {
        this(null);
    }

    /**
     * Constructor creates a new label synchronizer preconfigured with the given label.
     * @param labelBuilder
     */
    public LabelSynchronizer(final Label.Builder labelBuilder) {
        this.labelBuilder = labelBuilder;

        LanguageSelection.getInstance().addObserver((localeOld, localeNew) -> {
            synchronizeLabel();
        });
    }

    private void synchronizeLabel() {
        // set neutral string if no label is available
        if (labelBuilder.getEntryCount() == 0) {
            textProperty.set("");
        }

        // lookup value
        try {
            LabelProcessor.getLabelByLanguage(Locale.getDefault(), labelBuilder);
        } catch (NotAvailableException ex) {
            // apply fallback value
            try {
                textProperty.setValue(LabelProcessor.getFirstLabel(labelBuilder));
            } catch (NotAvailableException exx) {
                ExceptionPrinter.printHistory("Label is missing!", exx, LOGGER);
                textProperty.set("");
            }
        }
    }

    public Label getLabel() {
        return labelBuilder.build();
    }

    /**
     * Updates the label which is than synchronized with the test property.
     *
     * @param label the label to set.
     */
    public synchronized void updateLabel(final LabelOrBuilder label) {
        labelBuilder.clearEntry();
        labelBuilder.addAllEntry(label.getEntryList());
        synchronizeLabel();
    }

    public StringProperty textProperty() {
        return textProperty;
    }
}

