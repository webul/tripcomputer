package pl.tripcomputer.files.xml;

import org.w3c.dom.Element;

import pl.tripcomputer.Main;
import pl.tripcomputer.R;
import pl.tripcomputer.Utils;
import pl.tripcomputer.activities.ActivityMain;
import pl.tripcomputer.common.CommonActivity;
import pl.tripcomputer.data.items.DataItemTrack;
import pl.tripcomputer.loader.TrackItem;


public class ExportTrackToKML extends ExportToKML
{
	//fields
	private final static String STYLE_TRACK = "styleTrack";
	private final static String STYLE_POINT_START = "stylePointStart";
	private final static String STYLE_POINT_END = "stylePointEnd";
	private final static String COLOR_TRACK = "ffaa6600"; //AABBGGRR
	private final static String TRACK_WIDTH = "4";
	
	//fields
	protected CommonActivity parent = null;
	private TrackItem itemTrack = null;
	
	private String sTrackName = null;
	private String sTrackDesc = null;
	private String sInformation = null;
	
	private String sLabelStart = null;
	private String sLabelFinish = null;
	private String sLabelPoint = null;
	
	
	//methods	
	public ExportTrackToKML(CommonActivity parent, final TrackItem itemTrack)	
	{
		this.parent = parent;
		
		if (itemTrack != null)
		{		
			this.itemTrack = itemTrack;		
		
			synchronized(itemTrack.dataTrack)
			{
				//track name
				sTrackName = itemTrack.dataTrack.getName();
				
				//track desc
				sTrackDesc = itemTrack.dataTrack.getDescription();

				sInformation = "";
				
				itemTrack.dataTrack.setDistance(itemTrack.getTotalDistanceInMeters());
				
				//information
				addInfoItem(R.string.export_track_info_time, itemTrack.dataTrack.getTotalTimeAsString());
				
				addInfoItem(R.string.export_track_info_distance, itemTrack.dataTrack.getDistanceAsString(parent.getPrefs()));

				addInfoItem(R.string.export_track_info_type, DataItemTrack.trackTypeToString(parent, itemTrack.dataTrack.getType()));

				addInfoItem(R.string.export_track_info_start_time, itemTrack.dataTrack.getStartTimeAsString());

				final float fAvgSpeed = ActivityMain.loader.stats.getTrackAverageSpeed(itemTrack);
				if (fAvgSpeed != 0)
				{
					final String sAverageSpeed = Utils.getSpeedAsString(parent.getPrefs(), fAvgSpeed);				
					addInfoItem(R.string.export_track_info_avg_speed, sAverageSpeed);
				}

				addInfoItem(R.string.export_track_info_points, Integer.toString(itemTrack.dataTrack.getPointCount()));
				
				sInformation += Main.WEB_PAGE; 				
			}
		}		

		sLabelStart = parent.getString(R.string.label_track_start);
		sLabelFinish = parent.getString(R.string.label_track_finish);
		sLabelPoint = parent.getString(R.string.label_track_point);		
	}
	
	private void addInfoItem(int iResPrefixId, String sValue)
	{
		sInformation += parent.getString(iResPrefixId) + ": <strong>" + sValue + "</strong>";
		sInformation += "<br/>";
	}
	
	public void createNodes(Element elRoot)
	{
		//main document element
		final Element elDocument = createDocumentElement(elRoot, sTrackName, sTrackDesc);
		
		//styles
		createStyleTrack(elDocument, COLOR_TRACK, TRACK_WIDTH, STYLE_TRACK);
		createStylePoint(elDocument, STYLE_POINT_START, "green-dot.png");
		createStylePoint(elDocument, STYLE_POINT_END, "red-dot.png");
		
		//placemark track
		createPlacemarkTrack(elDocument, sTrackName, sInformation, STYLE_TRACK, itemTrack.getPointsListString());
		
		//placemark start
		createPlacemarkPoint(elDocument, sLabelStart, sLabelStart + " " + sLabelPoint, STYLE_POINT_START, itemTrack.getStartPoint());

		//placemark finish
		createPlacemarkPoint(elDocument, sLabelFinish, sLabelFinish + " " + sLabelPoint, STYLE_POINT_END, itemTrack.getFinishPoint());
	}
	
}
