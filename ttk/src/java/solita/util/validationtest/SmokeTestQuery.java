package solita.util.validationtest;

public class SmokeTestQuery {

  private final String querySql;
  private final String queryTitle;
  private final boolean reportOnly;

  public SmokeTestQuery(String queryTitle, String querySql, boolean reportOnly) {
    this.querySql = querySql;
    this.queryTitle = queryTitle;
    this.reportOnly = reportOnly;
  }

  public String getQuerySql() {
    return querySql;
  }

  public String getQueryTitle() {
    return queryTitle;
  }

  public boolean isReportOnly() {
    return reportOnly;
  }
}
