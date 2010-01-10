package pl.tripcomputer.files.xml;

import org.w3c.dom.Element;


public abstract class ExportToKML extends ExportToXml
{
	//fields
	private final static String URL_ICONS = "http://maps.google.com/mapfiles/ms/micons/";
	private final static int ICON_SIZE = 32;
	
	
	//methods
	public Element createRoot()
	{
		if (doc != null)
		{
			Element elRoot = doc.createElement("kml");
			elRoot.setAttribute("xmlns", "http://www.opengis.net/kml/2.2");		
			return elRoot;
		}
		return null;
	}
	
	protected Element createPlacemarkElement(Element elDocument, String sName, String sDesc, String sStyle)
	{
		Element elPlacemark = createElement(doc, elDocument, "Placemark");
	
		createElementCDATA(doc, elPlacemark, "name", sName.trim());
		createElementCDATA(doc, elPlacemark, "description", sDesc.trim());
		
		createElement(doc, elPlacemark, "styleUrl", "#" + sStyle);
		
		return elPlacemark;
	}
		
	protected Element createStyleTrack(Element elDocument, String sColor, String sWidth, String sStyle)
	{
		Element elStyle = createElement(doc, elDocument, "Style");
		elStyle.setAttribute("id", sStyle);
				
		Element elLineStyle = createElement(doc, elStyle, "LineStyle");
		
		createElement(doc, elLineStyle, "color", sColor);
		createElement(doc, elLineStyle, "width", sWidth);
				
  	return elStyle; 
	}

	protected Element createStylePoint(Element elDocument, String sID, String sIconName)
	{
		Element elStyle = createElement(doc, elDocument, "Style");
		elStyle.setAttribute("id", sID);
				
		Element elIconStyle = createElement(doc, elStyle, "IconStyle");		
		createElement(doc, elIconStyle, "scale", "1");
		
		Element elIcon = createElement(doc, elIconStyle, "Icon");
		createElement(doc, elIcon, "href", URL_ICONS + sIconName);
				
		Element elhotSpot = createElement(doc, elIconStyle, "hotSpot");
		elhotSpot.setAttribute("x", Integer.toString(ICON_SIZE >> 1));
		elhotSpot.setAttribute("y", "0");
		elhotSpot.setAttribute("xunits", "pixels");
		elhotSpot.setAttribute("yunits", "pixels");
		
  	return elStyle; 
	}
	
	protected Element createPlacemarkTrack(Element elDocument, String sName, String sDesc, String sStyle, String sCoords)
	{
		Element elPlacemark = createPlacemarkElement(elDocument, sName, sDesc, sStyle);

		Element elLineString = createElement(doc, elPlacemark, "LineString");
		
		createElement(doc, elLineString, "tessellate", "1");
		createElement(doc, elLineString, "altitudeMode", "clampToGround");
		createElement(doc, elLineString, "coordinates", sCoords.trim());
		
		return elPlacemark;
	}

	protected Element createPlacemarkPoint(Element elDocument, String sName, String sDesc, String sStyle, String sCoords)
	{
		Element elPlacemark = createPlacemarkElement(elDocument, sName, sDesc, sStyle);
		
		Element elPoint = createElement(doc, elPlacemark, "Point");
		
		createElement(doc, elPoint, "coordinates", sCoords.trim());
		
		return elPlacemark;
	}
	
	protected Element createDocumentElement(Element elRoot, String sName, String sDesc)
	{	
		Element elDocument = createElement(doc, elRoot, "Document");
				
		//document properties
		createElementCDATA(doc, elDocument, "name", sName.trim());
		createElementCDATA(doc, elDocument, "description", sDesc.trim());

		return elDocument;
	}

}
