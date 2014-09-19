package com.redhat.gss.mtom;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Collection;
import javax.activation.DataHandler;
import javax.annotation.Resource;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.soap.MTOM;
import org.apache.cxf.attachment.AttachmentDataSource;
import org.apache.cxf.jaxws.context.WrappedMessageContext;
import org.apache.cxf.message.Message;
import org.jboss.logging.Logger;
import javax.jws.HandlerChain;

@MTOM
@WebService(endpointInterface="com.redhat.gss.mtom.HashServer")
@HandlerChain(file="/handlers.xml")
public class HashServerImpl implements HashServer {

  private static Logger log = Logger.getLogger(HashServer.class);

  @Resource
  WebServiceContext ctx;

  public String calcHash(ContentDataType data) throws Exception {
    try {
      /*
      Message message = ((WrappedMessageContext)ctx.getMessageContext()).getWrappedMessage();
      Collection c = message.getAttachments();
      for(Object o : c) {
        //Don't do anything
        //Just iterating through the attachments will force CXF to cache them
        //Which will make it analyze the size vs threshold
      }
      */

      log.info("Sleeping...");
      Thread.sleep(10000);
      DataHandler dh = data.getContentData();
      InputStream input = dh.getInputStream();

      MessageDigest digest = MessageDigest.getInstance("MD5");
      byte[] bb = new byte[2048];
      int totalLength = 0;
      int length = 0;
      while((length = input.read(bb)) > 0) {
        totalLength += length;
        digest.update(bb, 0, length);
      }
      input.close();
      log.info("Total attachment length: " + totalLength);
      String s = toHexString(digest.digest());
      log.debug("Digest: " + s);
      return s;
    } catch(Exception e) {
      log.error("", e);
    }
    return null;
  }

  private String toHexString(byte[] hash) {
    BigInteger bi = new BigInteger(1, hash);
    return String.format("%0" + (hash.length << 1) + "x", bi);
  }
}
