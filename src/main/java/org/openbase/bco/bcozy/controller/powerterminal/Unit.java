package org.openbase.bco.bcozy.controller.powerterminal;

public enum Unit {
    ENERGY, LIGHTBULB, MONEY;

    @Override
    public String toString() {
        return super.toString().substring(0, 1) + super.toString().substring(1).toLowerCase();
    }
}
