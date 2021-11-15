package com.example.image_chan.core;

import java.util.ArrayList;

public class SearchFormatter {
    /*
    public ArrayList<String> FormatToTags(String unformatted) {
       String lowCase = unformatted.toLowerCase();
       String[] unconvTags = lowCase.split(", ");

       ArrayList<String> converted = new ArrayList<String>();

       for (String tag : unconvTags) {
           String trimmed = tag.trim();

           String formatted = trimmed
                   .replaceAll("\\s+", "_")
                   .replaceAll(",", "");

           converted.add(formatted);
       }

       return converted;
    }*/

    public String FormatToTagsStr(String unformatted) {
        String lowCase = unformatted.toLowerCase();
        String[] unconvTags = lowCase.split(", ");

        ArrayList<String> converted = new ArrayList<String>();

        for (String tag : unconvTags) {
            String trimmed = tag.trim();

            String formatted = trimmed
                    .replaceAll("\\s+", "_")
                    .replaceAll(",", "");

            converted.add(formatted);
        }

        return converted.toString();
    }
}
