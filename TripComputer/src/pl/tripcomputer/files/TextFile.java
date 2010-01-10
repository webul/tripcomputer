package pl.tripcomputer.files;

import java.io.File;
import java.io.FileWriter;


public class TextFile
{

	private void createPath(File file)
	{
	  if (file.getParent() != null)
	  {
		  final File path = new File(file.getParent());
		  if (!path.exists())
		  	path.mkdirs();
	  }		
	}
	
	protected boolean saveStringToFile(final String sFileName, final String sData)
	{
		try
		{
		  final File file = new File(sFileName);
		  
		  createPath(file);
		  
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
