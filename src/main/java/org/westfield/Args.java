package org.westfield;

import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.List;

public class Args {
    @Parameter
    List<String> parameters = new ArrayList<>();

    @Parameter(names = { "-v", "--verbose" }, description = "Set the level of verbosity that MediaTool uses as it runs.")
    Integer verbose = 1;

    @Parameter(names = { "-c", "--config"}, description = "Choose which configuration file MediaTool to uses.", required = true)
    String config;

    @Parameter(names = {"-exit-on-error"}, description = "Stop immediately when an error is detected. Set false to continue processing files.")
    boolean failOnError = true;

    @Parameter(names = {"-p", "--process"}, description = "Process the specified file and then exit")
    String process;

    @Parameter(names = {"-s", "--showOrder"}, description = "Show the order that the actions will be applied to the media files, then exit.")
    boolean showOrder;

    @Parameter(names = {"-?", "-h", "--help"}, help = true, description = "Display this help and exit.")
    boolean help = false;
}

