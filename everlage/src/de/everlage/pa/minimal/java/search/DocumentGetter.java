/*
 * Created on Mar 26, 2003
 * File DocumentGetter.java
 * 
 */
package de.everlage.pa.minimal.java.search;

import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.SAXException;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

/**
 * @author waffel
 */
public class DocumentGetter {

	public byte[] getDocument(String url) {
    byte[] content = null;
		try {
      System.out.println("URL: "+url);
			WebConversation wc = new WebConversation();
			WebRequest req = new GetMethodWebRequest("http://www11.in.tum.de/cgi-bin/webgate?port=8900&ip_address=www11.in.tum.de&database_name=java&type=HTML&docid=%03%1eF6205%3a1048703956%3a%20%28%20String%20%29%20%20%07%01%00&byte_count=125345");
			WebResponse res = wc.getResponse(req);
      content=res.getText().getBytes();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return content;
	}

}
