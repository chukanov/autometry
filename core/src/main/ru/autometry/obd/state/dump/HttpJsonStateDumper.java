package ru.autometry.obd.state.dump;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import ru.autometry.obd.state.OBDState;

/**
 * Created by jeck on 27/04/15
 */
public class HttpJsonStateDumper implements StateDumper {
  private StateSerializer<String> serializer;
  private String loadUrl;
  private String dumpUrl;
  private HttpClientConnectionManager connectionManager;

  public HttpJsonStateDumper(StateSerializer<String> serializer, String loadUrl, String dumpUrl) {
    this.serializer = serializer;
    this.loadUrl = loadUrl;
    this.dumpUrl = dumpUrl;
    connectionManager = new BasicHttpClientConnectionManager();
  }

  public HttpJsonStateDumper(String loadUrl, String dumpUrl) {
    this(new JsonStringStateSerializer(), loadUrl, dumpUrl);
  }


  @Override
  public OBDState load(String user) throws Exception {
    CloseableHttpClient client = HttpClients.createMinimal(connectionManager);
    HttpGet method = new HttpGet(loadUrl);
    HttpResponse httpResponse = null;
    try {
      httpResponse = client.execute(method);
      HttpEntity entity = httpResponse.getEntity();
      byte[] response = EntityUtils.toByteArray(entity);
      String responseString = EntityUtils.toString(entity, "UTF-8");
      return serializer.load(responseString);
    } finally {
      if (httpResponse != null) {
        HttpClientUtils.closeQuietly(httpResponse);
      }
    }
  }

  @Override
  public void dump(String user, OBDState state) throws Exception {
    CloseableHttpClient client = HttpClients.createMinimal(connectionManager);
    HttpPost method = new HttpPost(dumpUrl);
    HttpResponse httpResponse = null;
    String stateData = serializer.serialize(state);
    try {
      StringEntity entity = new StringEntity(stateData, "utf8");
      entity.setContentType("application/json");
      method.setEntity(entity);
      httpResponse = client.execute(method);
    } finally {
      if (httpResponse != null) {
        HttpClientUtils.closeQuietly(httpResponse);
      }
    }
  }
}
