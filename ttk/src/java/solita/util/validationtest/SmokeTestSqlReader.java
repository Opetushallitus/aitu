package solita.util.validationtest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads report SQL from external file.
 */
public class SmokeTestSqlReader {

  private final String file;
  private static final String DEFAULT_QUERY_NAME = "noname00";
  private static final String IGNORE_FAIL_TAG = "@ReportOnly";

  public SmokeTestSqlReader(String file) {
    this.file = file;
  }

  private static class SmokeQueryBuilder {
    private String name = DEFAULT_QUERY_NAME;
    private final StringBuilder query = new StringBuilder();
    private boolean reportOnly = false;

    public void setName(String name) {
      this.name = name;
    }

    public void setIgnoreErrors() {
      this.reportOnly = true;
    }

    public void appendQuery(String moar) {
      query.append(moar);
      query.append(" ");
    }

    public SmokeTestQuery build() {
      return new SmokeTestQuery(name, query.toString(), reportOnly);
    }
  }

  public static List<SmokeTestQuery> readAll(InputStream is) throws IOException {
    BufferedReader br = null;
    List<SmokeTestQuery> queries = new ArrayList<SmokeTestQuery>();
    try {
      br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
      String l = "";

      SmokeQueryBuilder queryBuilder = new SmokeQueryBuilder();
      while (br.ready() && l != null) {
        l = br.readLine();
        if (l != null) {
          if (l.startsWith("--") && l.contains(IGNORE_FAIL_TAG)) {
            queryBuilder.setIgnoreErrors();
          } else if (l.startsWith("--")) {
            queryBuilder.setName(l.substring(2));
          } else {
            queryBuilder.appendQuery(l.replace(';', ' ')); // XXX: strictly not proper, valid literal ; in SQL will fail
            if (l.contains(";")) { // end of SQL statement, stop parsing the current query
              queries.add(queryBuilder.build());
              queryBuilder = new SmokeQueryBuilder();
            }
          }
        }
      }
    } finally {
      is.close();
      if (br != null) {
        br.close();
      }
    }
    return queries;
  }

  public List<SmokeTestQuery> getAll() throws IOException {
    InputStream is = this.getClass().getResourceAsStream("/" + file);
    if (is == null) {
      throw new IllegalStateException("Coud not load SQL file " + file);
    }
    try {
      return readAll(is);
    } catch (IOException ioe) {
      throw new IllegalStateException("Could not read SQL file " + file + "! ", ioe);
    } finally {
      if (is != null) is.close(); 
    }
  }
}
