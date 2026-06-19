package com.example.demo.pojo.source;

public class SourceAE {
    private String reportedEventTerm;
    private String subjectId;
    private String product;
    private String reporterSeriousness;

    public String getReporterSeriousness() {
        return reporterSeriousness;
    }

    public void setReporterSeriousness(String reporterSeriousness) {
        this.reporterSeriousness = reporterSeriousness;
    }

    public String getReportedEventTerm() {
        return reportedEventTerm;
    }

    public void setReportedEventTerm(String reportedEventTerm) {
        this.reportedEventTerm = reportedEventTerm;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }
}
