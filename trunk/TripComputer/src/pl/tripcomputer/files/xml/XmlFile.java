package pl.tripcomputer.files.xml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import pl.tripcomputer.files.TextFile;


public class XmlFile extends TextFile
{
	//fields
	private final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
	private DocumentBuilder docBuilder = null;
	
	
	//methods
	public XmlFile()
	{
		//init factory
		docFactory.setNamespaceAware(false);
		docFactory.setValidating(false);
		docFactory.setIgnoringComments(false);
		docFactory.setIgnoringElementContentWhitespace(false);
		docFactory.setExpandEntityReferences(true);
		docFactory.setCoalescing(false);
	}

	protected DocumentBuilder getBuilder()
	{
		if (docBuilder == null)
		{		
			try
			{
				docBuilder = docFactory.newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				return null;
			}
		}
		return docBuilder;
	}

	protected boolean saveToFile(Document doc, String sFileName)
	{
		try
		{
			final DomDocumentToXml xml = new DomDocumentToXml(doc);
			return saveStringToFile(sFileName, xml.toString());						
		} catch (Exception e) {
		}
		return false;
	}
		
	protected String toString(Document doc)
	{
		try
		{
			final DomDocumentToXml xml = new DomDocumentToXml(doc);									
			return xml.toString();
		} catch (Exception e) {
		}
		return "";
	}
	
	public Element createElement(Document doc, Element elParent, String sName)
	{
		Element element = doc.createElement(sName);	  
		elParent.appendChild(element);
		return element;
	}

	public Element createElement(Document doc, Element elParent, String sName, String sValue)
	{
		Element element = doc.createElement(sName);	  
		elParent.appendChild(element);		
		element.appendChild(doc.createTextNode(sValue));
		return element;
	}

	public Element createElementCDATA(Document doc, Element elParent, String sName, String sValue)
	{
		Element element = doc.createElement(sName);
		elParent.appendChild(element);
		element.appendChild(doc.createCDATASection(sValue));
		return element;
	}
	
}
