package pl.edu.icm.openoxides.app

import eu.unicore.samly2.SAMLUtils
import org.apache.xmlbeans.SimpleValue
import xmlbeans.org.oasis.saml2.assertion.AssertionDocument
import xmlbeans.org.oasis.saml2.assertion.AttributeStatementType
import xmlbeans.org.oasis.saml2.assertion.AttributeType
import xmlbeans.org.oasis.saml2.protocol.ResponseDocument

/**
 * Created by Rafal on 2015-03-24.
 */
class SamlResponseTestApp {
    private static final String EXAMPLE_SAML_RESPONSE = 'src/test/resources/example-of-saml-response-document.xml'

    public static void main(String[] args) {
        def content = new Scanner(new File(EXAMPLE_SAML_RESPONSE)).useDelimiter('\\Z').next()
        def responseDocument = ResponseDocument.Factory.parse(content);
        def response = responseDocument.getResponse();

        AssertionDocument[] assertionDocuments = SAMLUtils.getAssertions(response);
        assertionDocuments.each { assertionDocument ->
            def assertion = assertionDocument.getAssertion()
            println assertion.getID()

            if (assertion.getAuthnStatementArray().size() > 0) {
                println " -> authn"
            }
            if (assertion.getAttributeStatementArray().size() > 0) {
                println " -> attrs"
                assertion.getAttributeStatementArray().each { AttributeStatementType attribute ->
                    attribute.getAttributeArray().each { AttributeType attr ->
                        println "   * " + attr.getName()
                        attr.getAttributeValueArray().each { value ->
                            SimpleValue simpleValue = (SimpleValue) value
                            println "    ** " + simpleValue.getStringValue().replaceAll("\n", "").trim()
                        }
                    }
                }
            }
        }
    }
}
