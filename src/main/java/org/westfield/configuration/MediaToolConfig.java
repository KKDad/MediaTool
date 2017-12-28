package org.westfield.configuration;

import java.util.List;
import java.util.Map;

public class MediaToolConfig {


    private String source;
    private String destination;
    private List<String> actions;
    private Map< String, String > processDelay;
    private Map< String, String > backupOriginal;
    private Map< String, String > renameMedia;
    private Map< String, String > infoCreator;
    private Map< String, String > showLookup;
    private Map< String, String > commercialDetect;
    private Map< String, String > removeCommercials;
    private Map< String, String > transcode;

    private List<LookupHint> lookupHints;

    public String getSource() { return this.source; }
    public String getDestination()  { return this.destination; }
    public List<String> getActions()  { return this.actions; }
    public Map< String, String> getProcessDelay()  { return this.processDelay; }
    public Map< String, String> getBackupOriginal()  { return this.backupOriginal; }
    public Map< String, String> getRenameMedia()  { return this.renameMedia; }
    public Map< String, String> getInfoCreator()  { return this.infoCreator; }
    public Map< String, String> getShowLookup()  { return this.showLookup; }
    public Map< String, String> getCommercialDetect()  { return this.commercialDetect; }
    public Map< String, String> getRemoveCommercials()  { return this.removeCommercials; }
    public Map< String, String> getTranscode()  { return this.transcode; }
    public List<LookupHint> getLookupHints()  { return this.lookupHints; }

    public void setSource(String source) { this.source = source; }
    public void setDestination(String destination) { this.destination = destination; }
    public void setActions(List<String> actions) { this.actions = actions; }
    public void setProcessDelay(Map< String, String > processDelay) { this.processDelay = processDelay; }
    public void setBackupOriginal(Map< String, String > backupOriginal) { this.backupOriginal = backupOriginal; }
    public void setRenameMedia(Map< String, String > renameAction) { this.renameMedia = renameAction; }
    public void setInfoCreator(Map< String, String > infoCreator) { this.infoCreator = infoCreator; }
    public void setShowLookup(Map< String, String > showLookup) { this.showLookup = showLookup; }
    public void setCommercialDetect(Map< String, String > commercialDetect) { this.commercialDetect = commercialDetect; }
    public void setRemoveCommercials(Map< String, String > removeCommercials) { this.removeCommercials = removeCommercials; }
    public void setTranscode(Map< String, String > transcode) { this.transcode = transcode; }
    public void setLookupHints(List<LookupHint> hints)  { this.lookupHints = hints; }

    @Override
    public String toString()
    {
        return "\n" +
                String.format("Source: %s%n", this.source) +
                String.format("Destination: %s%n", this.destination) +
                String.format("--------------------------------------------%n") +
                String.format("Actions: %s%n", this.actions) +
                String.format("--------------------------------------------%n") +
                String.format("ProcessDelay: %s%n", this.processDelay) +
                String.format("RenameMedia: %s%n", this.renameMedia) +
                String.format("BackupOriginal: %s%n", this.backupOriginal) +
                String.format("InfoCreator: %s%n", this.infoCreator) +
                String.format("ShowLookup: %s%n", this.showLookup) +
                String.format("CommercialDetect: %s%n", this.commercialDetect) +
                String.format("RemoveCommercials: %s%n", this.removeCommercials) +
                String.format("Transcode: %s%n", this.transcode) +
                String.format("LookupHints: %s%n", this.lookupHints);
    }
}
