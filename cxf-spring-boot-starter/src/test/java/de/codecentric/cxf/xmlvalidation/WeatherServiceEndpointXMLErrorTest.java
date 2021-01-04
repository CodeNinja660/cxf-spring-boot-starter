package de.codecentric.cxf.xmlvalidation;

import de.codecentric.cxf.TestApplication;
import de.codecentric.cxf.common.BootStarterCxfException;
import de.codecentric.cxf.soaprawclient.SoapRawClient;
import de.codecentric.cxf.soaprawclient.SoapRawClientResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest(
		classes=TestApplication.class,
		webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
		properties = { "server.port:8087"})
public class WeatherServiceEndpointXMLErrorTest {

	@Autowired
	private SoapRawClient soapRawClient;
	
	
	@Value(value="classpath:requests/xmlErrorNotXmlSchemeCompliantUnderRootElementTest.xml")
	private Resource xmlErrorNotXmlSchemeCompliantUnderRootElementTestXml;
	
	@Value(value="classpath:requests/xmlErrorNotXmlSchemeCompliantRootElementTest.xml")
	private Resource xmlErrorNotXmlSchemeCompliantRootElementTestXml;
	
	@Value(value="classpath:requests/xmlErrorSoapHeaderMissingSlash.xml")
	private Resource xmlErrorSoapHeaderMissingSlashXml;
	
	@Value(value="classpath:requests/xmlErrorSoapBodyTagMissingBracketTest.xml")
	private Resource xmlErrorSoapBodyTagMissingBracketTestXml;
	
	@Value(value="classpath:requests/xmlErrorSoapHeaderTagMissingBracketTest.xml")
	private Resource xmlErrorSoapHeaderTagMissingBracketTestXml;
	
	@Value(value="classpath:requests/xmlErrorSoapEnvelopeTagMissingBracketTest.xml")
	private Resource xmlErrorSoapEnvelopeTagMissingBracketTestXml;
	
	@Value(value="classpath:requests/xmlErrorXMLHeaderDefinitionMissingBracket.xml")
	private Resource xmlErrorXMLHeaderDefinitionMissingBracketXml;
	
	@Value(value="classpath:requests/xmlErrorXMLTagNotClosedInsideBodyTest.xml")
	private Resource xmlErrorXMLTagNotClosedInsideBodyTestXml;
	
	
	
	/*
	 * Non-Scheme-compliant Errors
	 */
	
	@Test
	public void xmlErrorNotXmlSchemeCompliantUnderRootElementTest() throws BootStarterCxfException, IOException {
		checkXMLErrorNotSchemeCompliant(xmlErrorNotXmlSchemeCompliantUnderRootElementTestXml);
	}
	
	@Test
	public void xmlErrorNotXmlSchemeCompliantRootElementTest() throws BootStarterCxfException, IOException {
		checkXMLErrorNotSchemeCompliant(xmlErrorNotXmlSchemeCompliantRootElementTestXml);
	}
	
	@Test
	public void xmlErrorSoapHeaderMissingSlash() throws BootStarterCxfException, IOException {
		checkXMLErrorNotSchemeCompliant(xmlErrorSoapHeaderMissingSlashXml);
	}
	
	private void checkXMLErrorNotSchemeCompliant(Resource testFile) throws BootStarterCxfException, IOException {
		checkXMLError(testFile, TestableCustomIds.NON_XML_COMPLIANT);
	}	
	
	/*
	 * Errors with syntactically incorrect XML
	 */
	
	@Test
	public void xmlErrorSoapBodyTagMissingBracketTest() throws BootStarterCxfException, IOException {
		checkXMLErrorSyntacticallyIncorrect(xmlErrorSoapBodyTagMissingBracketTestXml);
	}
	
	@Test
	public void xmlErrorSoapHeaderTagMissingBracketTest() throws BootStarterCxfException, IOException {
		checkXMLErrorSyntacticallyIncorrect(xmlErrorSoapHeaderTagMissingBracketTestXml);
	}
	
	@Test
	public void xmlErrorSoapEnvelopeTagMissingBracketTest() throws BootStarterCxfException, IOException {
		checkXMLErrorSyntacticallyIncorrect(xmlErrorSoapEnvelopeTagMissingBracketTestXml);
	}
	
	@Test
	public void xmlErrorXMLHeaderDefinitionMissingBracket() throws BootStarterCxfException, IOException {
		checkXMLErrorSyntacticallyIncorrect(xmlErrorXMLHeaderDefinitionMissingBracketXml);
	}	
	
	@Test
	public void xmlErrorXMLTagNotClosedInsideBodyTest() throws BootStarterCxfException, IOException {
		checkXMLErrorSyntacticallyIncorrect(xmlErrorXMLTagNotClosedInsideBodyTestXml);
	}
	
	
	private void checkXMLErrorSyntacticallyIncorrect(Resource testFile) throws BootStarterCxfException, IOException {
		checkXMLError(testFile, TestableCustomIds.COMPLETE_USELESS_XML);
	}
	
	private void checkXMLError(Resource testFile, TestableCustomIds customId) throws BootStarterCxfException, IOException {
		// Given
		// Resource testFile
		
		// When
		SoapRawClientResponse soapRawResponse = soapRawClient.callSoapService(testFile.getInputStream());
		
		// Then
		assertNotNull(soapRawResponse);
		assertEquals(500, soapRawResponse.getHttpStatusCode(), "500 Internal Server Error expected");
        assertEquals(customId.getMessage(), soapRawResponse.getFaultstringValue());
        
        de.codecentric.namespace.weatherservice.exception.WeatherException weatherException = soapRawResponse.getUnmarshalledObjectFromSoapMessage(de.codecentric.namespace.weatherservice.exception.WeatherException.class);		
		assertNotNull(weatherException, "<soap:Fault><detail> has to contain a de.codecentric.namespace.weatherservice.exception.WeatherException");
		
		assertEquals("ExtremeRandomNumber", weatherException.getUuid());
		assertEquals(customId.getId(), weatherException.getBusinessErrorId(), "The correct BusinessId is missing in WeatherException according to XML-scheme.");
	}
}
