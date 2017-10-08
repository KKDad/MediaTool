package org.westfield.configuration;

import java.util.List;
import java.util.Map;

public class MediaToolConfig {

    private String source;
    private String destination;
    private List<String> actions;
    private Map< String, String > renameAction;

    public String getSource() { return this.source; }
    public String getDestination()  { return this.destination; }
    public List<String> getActions()  { return this.actions; }
    public Map< String, String > getRenameAction()  { return this.renameAction; }

    public void setSource(String source) { this.source = source; }
    public void setDestination(String destination) { this.destination = destination; }
    public void setActions(List<String> actions) { this.actions = actions; }
    public void setRenameAction(Map< String, String > renameAction) { this.renameAction = renameAction; }

    @Override
    public String toString()
    {
        return "\n" +
                String.format("Source: %s%n", this.source) +
                String.format("Destination: %s%n", this.destination) +
                String.format("Actions: %s%n", this.actions) +
                String.format("RenameAction: %s%n", this.renameAction);
    }
}
