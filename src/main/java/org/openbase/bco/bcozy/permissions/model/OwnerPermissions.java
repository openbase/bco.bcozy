package org.openbase.bco.bcozy.permissions.model;

import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.extension.type.processing.LabelProcessor;
import org.openbase.type.language.LabelType.Label;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Locale;

/**
 * ViewModel for Owner of any Unit.
 *
 * @author vdasilva
 */
public class OwnerPermissions extends AbstractPermissions {

    public final static Owner NULL_OWNER = new Owner("-", LabelProcessor.addLabel(Label.newBuilder(), Locale.getDefault(), "").build());

    private final Owner currentOwner;

    public List<Owner> owners;

    public Owner owner;


    public OwnerPermissions(@Nonnull Owner owner, List<Owner> possibleOwners, boolean read, boolean write, boolean access) {
        super(owner.label, read, write, access);
        this.currentOwner = owner;
        this.owner = owner;
        this.owners = possibleOwners;
    }

    @Override
    public boolean changed() {
        return super.changed() || currentOwner != owner;
    }

    public static class Owner {
        private final String userId;

        private final Label label;

        public Owner(String userId, Label label) {
            this.userId = userId;
            this.label = label;
        }

        @Override
        public String toString() {
            return LabelProcessor.getBestMatch(getLabel(), "?");
        }

        public String getUserId() {
            return userId;
        }

        public Label getLabel() {
            return label;
        }
    }
}
