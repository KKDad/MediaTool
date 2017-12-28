package org.westfield;

import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.List;

public class Args {
    @Parameter
    public List<String> parameters = new ArrayList<>();

    @Parameter(names = { "-v", "--verbose" }, description = "Level of verbosity.")
    public Integer verbose = 1;

    @Parameter(names = { "-c", "--config"}, description = "Configuration file MediaTool to use.", required = true)
    public String config;

    @Parameter(names = {"-exit-on-error"}, description = "Stop immediately when an error is detected. Set false to continue processing files.")
    public boolean failOnError = true;

    @Parameter(names = {"-p", "--process"}, description = "Process the specified file and then exit")
    public String process;

    @Parameter(names = {"-s", "--showOrder"}, description = "Show the order that the actions will be applied to the media files, then exit")
    public boolean showOrder;


    @Parameter(names = {"-?", "-h", "--help"}, help = true, description = "Display this help and exit.")
    public boolean help = false;
}

