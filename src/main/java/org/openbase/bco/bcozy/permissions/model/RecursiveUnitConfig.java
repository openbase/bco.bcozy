package org.openbase.bco.bcozy.permissions.model;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleObjectProperty;
import rst.domotic.unit.UnitConfigType;

import java.util.Objects;

/**
 * @author vdasilva
 */
public class RecursiveUnitConfig extends RecursiveTreeObject<RecursiveUnitConfig> {

    final private SimpleObjectProperty<UnitConfigType.UnitConfig> unit = new SimpleObjectProperty<>();

    public RecursiveUnitConfig(UnitConfigType.UnitConfig unitConfig) {
        this.setUnit(unitConfig);
    }

    public UnitConfigType.UnitConfig getUnit() {
        return unit.get();
    }

    public void setUnit(UnitConfigType.UnitConfig unitConfig) {
        this.unit.set(Objects.requireNonNull(unitConfig));

    }

    public SimpleObjectProperty unitProperty() {
        return unit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RecursiveUnitConfig that = (RecursiveUnitConfig) o;

        return this.unit.get() != null && that.unit.get() != null
                && this.unit.get().getId().equals(that.unit.get().getId());
    }

    @Override
    public int hashCode() {
        return unit.get() != null ? unit.get().getId().hashCode() : 0;
    }
}
