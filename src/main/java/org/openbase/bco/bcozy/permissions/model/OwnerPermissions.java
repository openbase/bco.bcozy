package org.openbase.bco.bcozy.permissions.model;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * ViewModel for Owner of any Unit.
 *
 * @author vdasilva
 */
public class OwnerPermissions extends AbstractPermissions {

    public final static Owner NULL_OWNER = new Owner("-", "-");

    private final Owner currentOwner;

    public List<Owner> owners;

    public Owner owner;


    public OwnerPermissions(@Nonnull Owner owner, List<Owner> possibleOwners, boolean read, boolean write, boolean access) {
        super(owner.name, read, write, access);
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

        private final String name;

        public Owner(String userId, String name) {
            this.userId = userId;
            this.name = name;
        }

        @Override
        public String toString() {
            return getName();
        }

        public String getUserId() {
            return userId;
        }

        public String getName() {
            return name;
        }
    }
}
