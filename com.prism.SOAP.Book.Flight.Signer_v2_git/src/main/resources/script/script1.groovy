/* Refer the link below to learn more about the use cases of script.
https://help.sap.com/viewer/368c481cd6954bdfa5d0435479fd4eaf/Cloud/en-US/148851bf8192412cba1f9d2c17f4bd25.html

If you want to know more about the SCRIPT APIs, refer the link below
https://help.sap.com/doc/a56f52e1a58e4e2bac7f7adbf45b2e26/Cloud/en-US/index.html */
import com.sap.gateway.ip.core.customdev.util.Message
import groovy.xml.XmlUtil

def Message processData(Message message) {

    def body = message.getBody(String)

    //def xml = new XmlSlurper(false, false).parseText(body)
    def root = new XmlSlurper(false, false).parseText(body)
    def message1 = root.children()[0]

    String result = message1.children().collect { XmlUtil.serialize(it) }.join("\n")

    //def payloadNode = xml.'multimap:Message1'[0]

    //def result = XmlUtil.serialize(payloadNode.children()[0])

    message.setBody(result)

    return message
}