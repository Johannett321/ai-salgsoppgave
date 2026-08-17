package com.johansvartdal.SpringAI.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UrlTitlePairDTO {
    private String url;
    private String title;

    @Override
    public String toString() {
        return "Url: " + url + ", Title: " + title;
    }
}
