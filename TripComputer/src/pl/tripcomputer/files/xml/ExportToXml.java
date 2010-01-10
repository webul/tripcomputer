package pl.tripcomputer.files.xml;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


public abstract class ExportToXml extends XmlFile
{
	//fields
	protected DocumentBuilder docBuilder = null;
	protected Document doc = null;
	private Element elRoot = null;

	
	//methods
	public ExportToXml()
	{
		docBuilder = getBuilder();
		if (docBuilder != null)
		{
			doc = docBuilder.newDocument();
		}
	}		

	public boolean create()
	{
		if (doc != null)
		{
			elRoot = createRoot();
			if (elRoot != null)
			{
				createNodes(elRoot);
				doc.appendChild(elRoot);			
				return true;
			}
		}
		return false;
	}
	
	public abstract Element createRoot();
	public abstract void createNodes(Element elRoot);
	
	public boolean saveToFile(String sFileName)
	{
		if (doc != null)
		{						
			return saveToFile(doc, sFileName);			
		}
		return false;
	}
	
	public String toString()
	{
		if (doc != null)
			return toString(doc);
		return "";
	}
		
}
