import com.sap.gateway.ip.core.customdev.util.Message
import java.util.*;
import com.sap.gateway.core.ip.component.commons.ODataMethod.*;
import com.sap.gateway.ip.core.customdev.logging.*;
import com.sap.gateway.core.ip.component.commons.*;
import com.sap.gateway.ip.core.customdev.api.*;
def Message processData(Message message) {
    def map = message.getProperties()
 if(input.startsWith("ISA"))
{   
    // Read the property with name "NRO"
    def nroProperty = map.get("NRO")
    def nroValue = nroProperty ? nroProperty.toInteger() : 0

    // Increment the value by 1
    def incrementedValue = nroValue + 1
    def incrementedValueST= nroValue + 3
    def incrementedValueGS = nroValue + 2
    // Prefix with 5 zeroes (format as a six-digit number)
    def formattedNroValue = String.format("%06d", incrementedValue)

    // Retrieve the header value "SAP_EDI_Interchange_Control_Number" with a specified type
    def interchangeControlNumberHeader = message.getHeader("SAP_EDI_Interchange_Control_Number", String)
    def STControlNumberHeader = message.getHeader("ST_control_number", String)
    def GSControlNumberHeader = message.getHeader("SAP_EDI_GS_Control_Number", String);
    def interchangeControlNumber = interchangeControlNumberHeader ? interchangeControlNumberHeader.toInteger() : 0
    def STControlNumber = STControlNumberHeader ? STControlNumberHeader.toInteger() : 0
    def GSControlNumber = GSControlNumberHeader ? GSControlNumberHeader.toInteger() : 0
    // Compare the incremented and formatted value with the header value
    if ((formattedNroValue.toInteger() == interchangeControlNumber) && (incrementedValueST.toInteger() == STControlNumber) && ( incrementedValueGS.toInteger() == GSControlNumber ) )  {
        // Proceed with your logic if they match
      //  message.setBody("Values match: ${formattedNroValue}")
       
       return message;
    } else {
         //message.setHeader("ValidationStatus", "Mismatch");
      throw new Exception("Wrong format exception")
    }
}
   return message;
}
