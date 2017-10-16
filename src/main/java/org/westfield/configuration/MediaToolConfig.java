package org.westfield.configuration;

import java.util.List;
import java.util.Map;

public class MediaToolConfig {

    private String source;
    private String destination;
    private List<String> actions;
    private Map< String, String > renameMedia;
    private Map< String, String > infoCreator;

    public String getSource() { return this.source; }
    public String getDestination()  { return this.destination; }
    public List<String> getActions()  { return this.actions; }
    public Map< String, String > getRenameMedia()  { return this.renameMedia; }
    public Map< String, String > getInfoCreator()  { return this.infoCreator; }

    public void setSource(String source) { this.source = source; }
    public void setDestination(String destination) { this.destination = destination; }
    public void setActions(List<String> actions) { this.actions = actions; }
    public void setRenameMedia(Map< String, String > renameAction) { this.renameMedia = renameAction; }
    public void setInfoCreator(Map< String, String > infoCreator) { this.infoCreator = infoCreator; }

    @Override
    public String toString()
    {
        return "\n" +
                String.format("Source: %s%n", this.source) +
                String.format("Destination: %s%n", this.destination) +
                String.format("Actions: %s%n", this.actions) +
                String.format("RenameMedia: %s%n", this.renameMedia) +
                String.format("InfoCreator: %s%n", this.infoCreator);
    }
}
