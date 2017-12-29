package org.westfield.action;

public abstract class Action implements IAction {

    protected boolean enabled = false;

    @Override
    public boolean enabled() { return this.enabled; }

}
