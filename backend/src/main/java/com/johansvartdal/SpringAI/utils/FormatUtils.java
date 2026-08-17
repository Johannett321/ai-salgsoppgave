package com.johansvartdal.SpringAI.utils;

public class FormatUtils {

    public static String convertToCamelCase(String str) {
        String[] parts = str.split(" ");
        StringBuilder camelCaseString = new StringBuilder();
        for (String part : parts) {
            camelCaseString.append(toProperCase(part));
        }
        return camelCaseString.toString();
    }

    private static String toProperCase(String s) {
        return s.substring(0, 1).toUpperCase() +
                s.substring(1).toLowerCase();
    }

    public static String removeHTTPProtocolAndTrailingSlash(String url) {
        // Remove the "http://" or "https://" prefix if present
        if (url.startsWith("http://")) {
            url = url.substring(7);
        } else if (url.startsWith("https://")) {
            url = url.substring(8);
        }

        // Remove any trailing slashes
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        return url;
    }
}
