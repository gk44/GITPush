import com.sap.it.api.mapping.*;
import java.io.*;

def Message processData(Message message) {
    // 1. Get the existing headers map
    def headers = message.getHeaders();
    
    // 2. Retrieve the client certificate header captured from the sender
    def clientCert = headers.get("ssl_client_cert");
    
    if (clientCert != null) {
        // 3. Set it explicitly so the SOAP adapter transmits it
        message.setHeader("ssl_client_cert", clientCert);
        
        // Optional: If your backend prefers the standard header name used by reverse proxies:
        // message.setHeader("X-Forwarded-Client-Cert", clientCert);
    }
    
    return message;
}
