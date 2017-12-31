package org.westfield;

import org.slf4j.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ProcessingLoggingHandler extends Thread {

    private final Logger logger;
    private final InputStream inputStream;
    private final List<String> filterList;
    private final String prefix;

    private StringBuilder output = new StringBuilder();

    /**
     * @param inputStream - Stream to process. All output is saved to output
     * @param logger - Logger to log output to, optional. Log logging if not supplied
     * @param filterList - List to filter output to the logger. If any regex matches, line is omitted
     */
    public ProcessingLoggingHandler(String prefix, InputStream inputStream, Logger logger, List<String> filterList)
    {
        this.inputStream = inputStream;
        this.logger = logger;
        this.filterList = new ArrayList<>();
        this.prefix = prefix;
        if (filterList != null)
                this.filterList.addAll(filterList);
    }

    @Override
    public void run() {
        try (Scanner br = new Scanner(new InputStreamReader(inputStream))) {
            String line;
            while (br.hasNextLine()) {
                line = br.nextLine();
                logOutput(line);
                output.append(line).append(System.getProperty("line.separator"));
            }
        }
    }

    /**
     * Log the line to the logger if it does not match any of the specified filters
     * @param line
     */
    private void logOutput(String line)
    {
        if (logger != null && logger.isInfoEnabled()) {
            if (!this.filterList.isEmpty())
                for (String filter : this.filterList)
                    if (line.contains(filter))
                        return;
            logger.trace("{}: {}", this.prefix, line);
        }
    }

    /**
     * Return a StringBuilder with all of the unfiltered output from the process
     * @return
     */
    public StringBuilder getOutput()
    {
        return output;
    }
}
