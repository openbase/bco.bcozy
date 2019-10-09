package org.openbase.bco.bcozy.util;

import org.openbase.jul.extension.type.processing.LabelProcessor;
import org.openbase.type.language.LabelType;

/**
 * Simple wrapper class which offers a toString() implementation which always returns a language selection sensitive label.
 */
public class LabelWrapper {

    private final LabelType.Label label;

    public LabelWrapper(LabelType.Label label) {
        this.label = label;
    }

    public LabelType.Label getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return LabelProcessor.getBestMatch(label, "?");
    }
}
