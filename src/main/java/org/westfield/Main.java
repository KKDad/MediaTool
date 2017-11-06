package org.westfield;

import org.westfield.configuration.MediaToolConfig;
import org.westfield.configuration.YamlConfigLoader;

import java.io.File;
import java.nio.file.Paths;

@SuppressWarnings("squid:S106")
public class Main {

    public static void main(String[] args)
    {
        if (args.length < 1) {
            Main.help();
            return;
        }

        File configFile = Paths.get(args[0]).toFile();
        if (!configFile.isFile()) {
            System.out.printf("%s does not exist or is not a valid configuration file.%n%n", args[0]);
            Main.help();
            return;
        }
        if (!configFile.canRead()) {
            System.out.printf("Cannot read %s.%n%n", args[0]);
            Main.help();
            return;
        }
        MediaToolConfig config  = YamlConfigLoader.get(configFile.toPath());
        if (args.length == 2)
        new MediaTool(config)
                .setup()
                .run(args[1]);
        else
            new MediaTool(config)
                    .setup()
                    .run();

    }

    private static void help()
    {
        System.out.printf("Usage:%n");
        System.out.printf("\tMediaTool <configuration.yaml>%n");
    }
}
