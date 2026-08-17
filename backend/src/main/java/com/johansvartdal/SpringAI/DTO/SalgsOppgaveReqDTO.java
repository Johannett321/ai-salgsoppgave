package com.johansvartdal.SpringAI.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SalgsOppgaveReqDTO {
    private String finnUrl;
    private String pdfPath;
}
