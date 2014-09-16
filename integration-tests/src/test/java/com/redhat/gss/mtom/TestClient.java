package com.redhat.gss.mtom;

import java.net.URL;
import java.util.Map;
import java.util.HashMap;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.BindingProvider;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.jboss.logging.Logger;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import javax.xml.ws.spi.Provider;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import javax.xml.ws.soap.SOAPBinding;
import javax.activation.DataHandler;
import java.io.InputStream;
import java.security.MessageDigest;
import javax.activation.URLDataSource;
import java.math.BigInteger;

public class TestClient {
  private Logger log = Logger.getLogger(this.getClass().getName());

  @Test
  public void testEndpoint() throws Exception {
    // URL wsdl        = new URL("http://localhost:8080/mtom-endpoint/HashServerImpl?wsdl");
    URL wsdl        = getClass().getResource("/the.wsdl");
    QName serviceNS = new QName("http://mtom.gss.redhat.com/", "HashServerImplService");
    QName portNS    = new QName("http://mtom.gss.redhat.com/", "HashServerImplPort");

    Service service = Service.create(wsdl, serviceNS);
    HashServer port = service.getPort(portNS, HashServer.class);

    SOAPBinding binding = (SOAPBinding)((BindingProvider)port).getBinding();
    binding.setMTOMEnabled(true);
    // DataHandler dh = new DataHandler(new URLDataSource(getClass().getResource("/jboss-eap-6.2.4-full-build.zip")));
    DataHandler dh = new DataHandler(new URLDataSource(getClass().getResource("/shadowman.jpg")));
    ContentDataType data = new ContentDataType();
    data.setContentData(dh);

    String hash = port.calcHash(data);

    // assertEquals("Hash not equal", calcHash(getClass().getResource("/jboss-eap-6.2.4-full-build.zip")), hash);
    assertEquals("Hash not equal", calcHash(getClass().getResource("/shadowman.jpg")), hash);
  }

  private static String calcHash(URL url) throws Exception {
    InputStream input = url.openStream();
    MessageDigest digest = MessageDigest.getInstance("MD5");
    byte[] bb = new byte[1024];
    int length = 0;
    while((length = input.read(bb)) > 0) {
      digest.update(bb, 0, length);
    }
    return toHexString(digest.digest());
  }

  private static String toHexString(byte[] hash) {
    BigInteger bi = new BigInteger(1, hash);
    return String.format("%0" + (hash.length << 1) + "x", bi);
  }

  @Test
  public void testProvider() {
    Provider p = Provider.provider();
    String pName = p.getClass().getName();
    assertEquals("JBossWS is not being used: " + pName, "org.jboss.wsf.stack.cxf.client.ProviderImpl", pName);
  }
}
