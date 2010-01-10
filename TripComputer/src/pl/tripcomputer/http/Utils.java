package pl.tripcomputer.http;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Utils
{
	//fields
	private final static SimpleDateFormat dateFormat = new SimpleDateFormat();


	//methods
	public static String timeToString(long lTimeMs)
	{
		return dateFormat.format(new Date(lTimeMs));
	}

	public static String bytesToHex(final byte bytes[])
	{
    StringBuffer hexString = new StringBuffer();    
  	
    for (int i=0; i < bytes.length; i++)
  	{
  		hexString.append(Integer.toHexString(0xFF & bytes[i]));
  	}
  	
		return hexString.toString();
	}
	
	public static String hashString(String sData)
	{    
		try
		{
			MessageDigest sha = MessageDigest.getInstance("SHA");

	    sha.reset();
	    sha.update(sData.getBytes());
	    
	    final byte resultBytes[] = sha.digest();
	    
	    final String hexString = bytesToHex(resultBytes);
	    
			return hexString;
		} catch (NoSuchAlgorithmException e) {
		}
		
		return "";
	}

	public static String hashStringShort(String sData)
	{
		sData = hashString(sData);
		if (sData.length() > 16)
			return sData.substring(0, 16);
		return sData;
	}
		
	public static String getAccessToken(String sUserEmail, String sAccessCode)
	{
		if (sUserEmail == null)
			return null;
		
		if (sAccessCode == null)
			return null;
		
		final String sHashData = sUserEmail + "," + sAccessCode;

		return Utils.hashString(sHashData);
	}
	
}
