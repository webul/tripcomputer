package pl.tripcomputer.files;

import java.io.File;

import pl.tripcomputer.R;
import pl.tripcomputer.common.CommonActivity;

import android.os.Environment;
import android.widget.Toast;


public class FileUtils
{
	//fields
	private final static String STORAGE_DIR = "TripComputer"; 
	private final static String EXPORT_DIR = "export";
	private final static String STORAGE_EXPORT_DIR = STORAGE_DIR + "//" + EXPORT_DIR + "//";
	private final static String EXT_KML = "kml"; 

	
	//methods
	public static String getStoragExportPathKML(String sName)
	{
		return getStoragExportPath(sName, EXT_KML);
	}
	
	public static String getStoragExportPath(String sName, String sExt)
	{
		final File fileStorage = Environment.getExternalStorageDirectory();
		if (fileStorage.canWrite())
		{
			final String sAlternateName = "Track " + Long.toString(System.currentTimeMillis());

			sName = stringToFileName(sName, sAlternateName);			
			
			final String sFileName = STORAGE_EXPORT_DIR + sName + "." + sExt;

			final File fileTarget = new File(fileStorage, sFileName);
		
			return fileTarget.getPath();
		}
		return null;
	}

	private static String stringToFileName(String sName, String sAlternate)
	{
		String s = "";
		
		sName = sName.trim();
		
		for (int index = 0; index < sName.length(); index++)
		{
			final char c = sName.charAt(index);

			if (Character.isLetterOrDigit(c))
				s += c;
			if (Character.isLetterOrDigit('_'))
				s += c;
			if (Character.isLetterOrDigit('-'))
				s += c;
		}
		
		if (s.length() == 0)
			s = sAlternate;			
		
		return s;
	}

	public static void showSaveToast(CommonActivity parent, String sFileName, boolean bSuccess)
	{
		final String sLabelFile = parent.getString(R.string.label_file);			
		final String sLabelSavedIn = parent.getString(R.string.label_saved_in);			
		final String sLabelSaveError = parent.getString(R.string.label_file_save_error);
		
		if (bSuccess)
		{
			File file = new File(sFileName);
			String sPath = file.getParent();
			String sName = file.getName();
		
			Toast.makeText(parent, sLabelFile + " " + sName + " " + sLabelSavedIn + "\n" + sPath, Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(parent, sLabelSaveError, Toast.LENGTH_LONG).show();				
		}
	}

	public static void createPath(File file)
	{
	  if (file.getParent() != null)
	  {
		  final File path = new File(file.getParent());
		  if (!path.exists())
		  	path.mkdirs();
	  }		
	}
	
}
