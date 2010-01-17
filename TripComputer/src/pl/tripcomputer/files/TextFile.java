package pl.tripcomputer.files;

import java.io.File;
import java.io.FileWriter;


public class TextFile
{
	
	protected boolean saveStringToFile(final String sFileName, final String sData)
	{
		try
		{
		  final File file = new File(sFileName);

		  FileUtils.createPath(file);
		  
		  if (file.exists())
		  	file.delete();
		  
	    final FileWriter writer = new FileWriter(file, false);
	    
	    writer.write(sData);
	    writer.flush();
	    writer.close();		  

	    return true;
		} catch (Exception e) {
			return false;
		}
	}

}
