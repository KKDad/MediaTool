package org.westfield.action;

import org.apache.commons.lang3.StringUtils;
import org.westfield.media.IMediaDetails;

import java.util.Map;

public class RenameAction implements IAction
{
    private String format_string;
    private String[] format_string_tokens;

    @Override
    public boolean configure(Map<String, String> config)
    {
        this.format_string = config.get("format");
        this.format_string_tokens = StringUtils.split(this.format_string,"{}");
        return true;
    }

    @Override
    public IMediaDetails process(IMediaDetails details)
    {

        return details;
    }

    private static StringBuilder AppendMediaToken(StringBuilder builder, String token)
    {
        return builder;
    }
}
