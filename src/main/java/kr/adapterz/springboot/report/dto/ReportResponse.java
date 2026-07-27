package kr.adapterz.springboot.report.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ReportResponse {
    private Long reportId;
    private Long postId;
    private Long reportCount;
    private Boolean blinded;
}
