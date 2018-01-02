package org.westfield.thetvdb;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("squid:ClassVariableVisibilityCheck")
public class SearchResponse
{
    public List<SeriesItem> data;

    SearchResponse() {
        data = new ArrayList<>();
    }
}
