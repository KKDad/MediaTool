package org.westfield.media;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;

@SuppressWarnings({"squid:S00101", "squid:ClassVariableVisibilityCheck", "squid:S00116"})
class FFProbeWrapper
{
    private static final Logger logger = LoggerFactory.getLogger(FFProbeWrapper.class);
    private static final Gson json = new Gson();

    private static final String FFPROBE = "/usr/bin/ffprobe";

    private FFProbeWrapper() {
    }

    protected class Format {
        String filename;
        Integer nb_streams;
        Integer nb_programs;
        String format_name;
        String format_long_name;
        String duration;
        String size;
        String bit_rate;
        Integer probe_score;

        @Override
        public String toString()
        {
            return String.format("filename='%s', nb_streams=%d, format_name='%s', duration='%s'", filename, nb_streams, format_name, duration);
        }
    }

    protected class ShowFormat {
        FFProbeWrapper.Format format;


        @Override
        public String toString()
        {
            return String.format("ShowFormat{%s}", format);
        }
    }

    static ShowFormat probe(File file)
    {
        try {
            ProcessBuilder pb = new ProcessBuilder(FFPROBE, "-v", "quiet", "-print_format", "json", "-show_format", file.getAbsolutePath());
            final Process p=pb.start();
            int errCode = p.waitFor();
            if (logger.isDebugEnabled())
                logger.debug("File processed successfully?: {}", (errCode == 0 ? "No" : "Yes"));
            if (errCode > 0) {
                InputStream in = new BufferedInputStream(p.getErrorStream());
                String error = IOUtils.toString(in, StandardCharsets.UTF_8.name());
                in.close();
                if (!error.isEmpty())
                    logger.debug("Error: {}", error);
            } else {
                InputStream in = new BufferedInputStream(p.getInputStream());
                String result = IOUtils.toString(in, StandardCharsets.UTF_8.name());
                in.close();

                result = result.replaceAll("\r", "").replaceAll("\n", "").replaceAll("\t", "");

                ShowFormat format = json.fromJson(result, ShowFormat.class);
                logger.debug("Probe Result: {}", format);
                return format;
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return null;
    }
}
