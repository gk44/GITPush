import com.sap.gateway.ip.core.customdev.util.Message;
import java.util.HashMap;
import java.util.regex.Matcher;

def Message processData(Message message) {
 
 //Body 
 def body = message.getBody(java.lang.String.class);
 if(!body.contains("0997"))
  throw new Exception("Airline ID doesn't match");
 return message;
}