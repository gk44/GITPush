import com.sap.gateway.ip.core.customdev.util.Message;
import java.util.*;
import com.sap.gateway.ip.core.customdev.logging.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.io.IOException;

def Message processData(Message message) {
    def mapHeaders = message.getHeaders();
    def mapProperties = message.getProperties();
    def value = mapHeaders.get("EDI_INTERCHANGE_STATUS");
    def status = "failure";

    // Read payload
    BufferedReader br = null;
    StringBuilder sb = new StringBuilder();
    try {
        def bodyStream = message.getBody(java.io.InputStream);
        br = new BufferedReader(new InputStreamReader(bodyStream, Charset.forName("UTF-8")));
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
    } catch (IOException e) {
        throw new Exception("Error reading payload: ${e.getMessage()}")
    }

    def input = sb.toString();
    log.logErrors(LogMessage.TechnicalError, "payload=" + input);

    // Define expected payload patterns
    def EDI_850_5010 = "ST*997*0001,AK1*PO*55312,AK2*850*553110001,AK3*REF*3**8,AK4*1::1*128*7,AK4*4:1:2*128*7,AK5*R*5,AK2*850*553110002,AK5*A,AK9*P*2*2*1,SE*11*0001,";
    def EDI_850_4010 = "ST*997*0001AK1*PO*1AK2*850*10001AK5*AAK2*850*10002AK5*AAK2*850*10003AK5*AAK2*850*10004AK5*AAK9*A*4*4*4SE*12*0001";
    def Edifact = "UNH+1+CONTRL:D:01B:UN'UCI+1+SENDER ID:14+RECIPIENT ID:14+7'UNT+3+1'";
    def Edifact_Error = "UNH+1+CONTRL:D:96A:UN'UCI+1+SENDER ID:14+RECIPIENT ID:14+4+29'UNT+3+1'";

    if (input.startsWith("ISA")) {
        // Handle X12
        def nroProperty = mapProperties.get("NRO")
        def nroValue = nroProperty ? nroProperty.toInteger() : 0
        def incrementedValue = nroValue + 1
        def incrementedValueST = nroValue + 3
        def incrementedValueGS = nroValue + 2
        def formattedNroValue = String.format("%06d", incrementedValue)

        def interchangeControlNumber = (message.getHeader("SAP_EDI_Interchange_Control_Number", String) ?: "0").toInteger()
        def STControlNumber = (message.getHeader("ST_control_number", String) ?: "0").toInteger()
        def GSControlNumber = (message.getHeader("SAP_EDI_GS_Control_Number", String) ?: "0").toInteger()

        if (formattedNroValue.toInteger() != interchangeControlNumber ||
            incrementedValueST != STControlNumber ||
            incrementedValueGS != GSControlNumber) {
            throw new Exception("Control Numbers mismatch!")
        }
    }
    else if (input.startsWith("UNA")) {
        // Handle EDIFACT
        def Input_UNH_segment = input.substring(input.indexOf('UNH'), input.indexOf('UNZ'));

        if (Input_UNH_segment.equalsIgnoreCase(Edifact) || Input_UNH_segment.equalsIgnoreCase(Edifact_Error)) {
            log.logErrors(LogMessage.TechnicalError, "MessageBody=Validation correct");

            if (status == value) {
                throw new Exception("Interchange error has occurred");
            }
        } else {
            throw new Exception("EDIFACT ack didn't match expected value: " + Edifact);
        }
    }
    else {
        throw new Exception("Wrong format exception");
    }

    return message;
}
