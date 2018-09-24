package org.openbase.bco.bcozy.util;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.openbase.bco.bcozy.model.LanguageSelection;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.extension.rst.processing.LabelProcessor;
import org.openbase.jul.iface.Shutdownable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.language.LabelType.Label;
import rst.language.LabelType.LabelOrBuilder;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

/**
 * Helper class to synchronize a label with a text property which keeps updated with the currently configured locale of this vm instance.
 */
public class LabelSynchronizer implements Shutdownable {

    private static final Logger LOGGER = LoggerFactory.getLogger(LabelSynchronizer.class);

    private final StringProperty textProperty = new SimpleStringProperty();
    private final Label.Builder labelBuilder;
    private final Observer languageSelectionObserver;

    /**
     * Constructor creates a new label synchronizer.
     * The text property will be empty until a label is passed via the {@code updateLabel(final LabelOrBuilder label)} method.
     */
    public LabelSynchronizer() {
        this(null);
    }

    /**
     * Constructor creates a new label synchronizer preconfigured with the given label.
     *
     * @param label this message is used to setup the initial label. Any passed builder instance will not be modified.
     */
    public LabelSynchronizer(final LabelOrBuilder label) {

        // create internal builder
        if(label instanceof Label) {
            this.labelBuilder = ((Label) label).toBuilder();
        } else if(label instanceof Label.Builder) {
            this.labelBuilder = Label.newBuilder(((Label.Builder) label).build());
        } else {
            this.labelBuilder = Label.newBuilder();
        }

        // init the internal builder
        updateLabel(label);

        // create observer
        this.languageSelectionObserver = (o, arg) -> {
            synchronizeLabel();
        };

        // register language selection observer
        LanguageSelection.getInstance().addObserver(languageSelectionObserver);

        // perform initial sync
        synchronizeLabel();
    }

    private void synchronizeLabel() {

        // set neutral string if no label is available
        if (labelBuilder.getEntryCount() == 0) {
            textProperty.set("");
        }

        // lookup value
        try {
            textProperty.setValue(LabelProcessor.getLabelByLanguage(Locale.getDefault(), labelBuilder));
        } catch (NotAvailableException ex) {
            // apply fallback value
            try {
                textProperty.setValue(LabelProcessor.getBestMatch(labelBuilder));
            } catch (NotAvailableException exx) {
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
        if (label != null) {
            labelBuilder.addAllEntry(label.getEntryList());
        }
        synchronizeLabel();
    }

    public void clearLabels() {
        labelBuilder.clearEntry();
        synchronizeLabel();
    }

    public StringProperty textProperty() {
        return textProperty;
    }

    @Override
    public void shutdown() {
        LanguageSelection.getInstance().deleteObserver(languageSelectionObserver);
    }
}

