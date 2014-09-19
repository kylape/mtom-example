package com.redhat.gss.mtom;

import java.util.Collection;
import java.util.Iterator;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.LogicalMessageContext;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import org.apache.cxf.attachment.AttachmentDataSource;
import org.apache.cxf.attachment.AttachmentImpl;
import org.apache.cxf.jaxws.context.WrappedMessageContext;
import org.apache.cxf.message.Message;
import org.jboss.logging.Logger;
import org.jboss.wsf.common.handler.GenericSOAPHandler;
import java.io.InputStream;

public class TestHandler extends GenericSOAPHandler<LogicalMessageContext> {
  private static Logger log = Logger.getLogger(TestHandler.class);

  @Override
  protected boolean handleInbound(MessageContext ctx) {
    log.info("handler invoked!");
    SOAPMessage message = ((SOAPMessageContext)ctx).getMessage();
    Iterator iter = message.getAttachments();
    while(iter.hasNext()) {
      
      try {
        AttachmentPart a = (AttachmentPart)iter.next();
        AttachmentDataSource ads = (AttachmentDataSource) a.getDataHandler().getDataSource();
        ads.hold();
        InputStream input = ads.getInputStream();
        input.close();
        ads.release();
        log.info("Invoked hold on ADS: " + ads);
      } catch (Exception e) {
        log.error("", e);
      }
      //Don't do anything
      //Just iterating through the attachments will force CXF to cache them
      //Which will make it analyze the size vs threshold
    }
    return true;
  }
}
