package org.westfield;

import com.beust.jcommander.JCommander;
import org.westfield.configuration.MediaToolConfig;
import org.westfield.configuration.YamlConfigLoader;

import java.io.File;
import java.nio.file.Paths;

@SuppressWarnings("squid:S106")
public class Main {

    public static void main(String[] argv)
    {
        Args args = new Args();
        JCommander jc = JCommander.newBuilder()
                .addObject(args)
                .build();
        jc.parse(argv);
        if (args.help) {
            jc.usage();
            return;
        }


        File configFile = Paths.get(args.config).toFile();
        if (!configFile.isFile()) {
            System.out.printf("%s does not exist or is not a valid configuration file.%n%n", args.config);
            jc.usage();
            return;
        }
        if (!configFile.canRead()) {
            System.out.printf("Cannot read %s.%n%n", args.config);
            jc.usage();
            return;
        }
        MediaToolConfig config  = YamlConfigLoader.get(configFile.toPath());
        if (args.process != null && !args.process.isEmpty())
            new MediaTool(config)
                    .setOptions(args)
                    .setup()
                    .run(args.process);
        else
            new MediaTool(config)
                    .setOptions(args)
                    .setup()
                    .run();
    }
}