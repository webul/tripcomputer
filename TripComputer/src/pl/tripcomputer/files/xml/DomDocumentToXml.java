package pl.tripcomputer.files.xml;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;


public class DomDocumentToXml
{
	//fields
	private Document doc = null;

	
	//methods
	public DomDocumentToXml(Document document)
	{
		this.doc = document;		
	}

	private String getAttribute(Node at)
	{
		return at.getNodeName() + "=\"" + at.getNodeValue() + "\"";
	}
	
	private String getAttributes(Element element)
	{
		if (!element.hasAttributes())
			return "";
		
		String s = "";
		
		final NamedNodeMap attributes = element.getAttributes();		
		if (attributes != null)
		{			
			for (int index = 0; index < attributes.getLength(); index++)
			{
				final Node at = attributes.item(index);				
				s += getAttribute(at) + " ";				
			}
		}
		
		return s.trim();
	}

	private String getElementBegin(Element element)
	{
		if (element.hasAttributes())						
			return "<" + element.getNodeName() + " " + getAttributes(element) + ">";
		else
			return "<" + element.getNodeName() + ">";			
	}

	private String getElementEnd(Element element)
	{
		return "</" + element.getNodeName() + ">";			
	}

	private String addNexLine(Node node, String s)
	{
		if (node.getNodeType() == Node.ELEMENT_NODE)
			if (!s.endsWith("\n"))
				return s += "\n";
		return s;
	}
	
	private String getElement(Node node)
	{
		String s = "";
		
		if (node == null)
			return "";

		//node CDATA
		if (node.getNodeType() == Node.CDATA_SECTION_NODE)
		{
			final CDATASection cdata = (CDATASection)node;
			
			s += "<![CDATA[" + cdata.getNodeValue() + "]]>";			
		}

		//node CDATA
		if (node.getNodeType() == Node.TEXT_NODE)			
		{
			final Text text = (Text)node;

			s += text.getNodeValue();
		}
		
		//node ELEMENT
		if (node.getNodeType() == Node.ELEMENT_NODE)
		{						
			final Element element = (Element)node;

			s += getElementBegin(element);
			
			if (node.hasChildNodes())
			{							
				final NodeList nodes = element.getChildNodes();
				
				for (int index = 0; index < nodes.getLength(); index++)
				{
					final Node nodeItem = nodes.item(index);

					s = addNexLine(nodeItem, s);
					
					s += getElement(nodeItem);
					
					s = addNexLine(nodeItem, s);									
				}			
			}
			
			s += getElementEnd(element);						
		}
		
		return s;
	}
	
	public String toString()
	{
		String s = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		
		s += "\n";
	
		final Node node = doc.getFirstChild();		
		
		s += getElement(node);
						
		return s;
	}	
	
}
