package com.redhat.gss.mtom;

import java.io.InputStream;
import javax.xml.ws.soap.MTOM;
import javax.activation.DataHandler;
import javax.jws.WebService;
import java.security.MessageDigest;
import org.jboss.logging.Logger;
import java.math.BigInteger;
import org.apache.cxf.annotations.EndpointProperty;
import org.apache.cxf.annotations.EndpointProperties;

@MTOM
@WebService(endpointInterface="com.redhat.gss.mtom.HashServer")
@EndpointProperties({
  @EndpointProperty(key="attachment-memory-threshold", value="4096")
})
public class HashServerImpl implements HashServer
{
  private static Logger log = Logger.getLogger(HashServer.class);

	public String calcHash(ContentDataType data) throws Exception {
    DataHandler dh = data.getContentData();
    InputStream input = dh.getInputStream();
    MessageDigest digest = MessageDigest.getInstance("MD5");
    byte[] bb = new byte[1024];
    int totalLength = 0;
    int length = 0;
    while((length = input.read(bb)) > 0) {
      totalLength += length;
      digest.update(bb, 0, length);
    }
    log.info("Total attachment length: " + totalLength);
    String s = toHexString(digest.digest());
    log.debug("Digest: " + s);
    return s;
	}

  private String toHexString(byte[] hash) {
    BigInteger bi = new BigInteger(1, hash);
    return String.format("%0" + (hash.length << 1) + "x", bi);
  }
}
