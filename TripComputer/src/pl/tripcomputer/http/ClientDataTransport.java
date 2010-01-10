package pl.tripcomputer.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.util.EntityUtils;

import pl.tripcomputer.activities.ActivityMain;


public class ClientDataTransport
{			
	//fields
	private HttpClient httpClient = new DefaultHttpClient();
	private final static String sRequestURL = CommonData.sServletURL;
	
	//fields
	private String sUserEmail = "";
	private String sAccessToken = "";	
	private String sResponseData = null;
	private int iResultCode = CommonData.RESULT_ERROR;
	private int iStatusCode = HttpStatus.SC_OK;

	private String sLanguageId = "en";
	
	
	//methods
	public ClientDataTransport(String sUserEmail, String sLanguageId)
	{
		this.sUserEmail = sUserEmail.trim();
		this.sLanguageId = sLanguageId;
	}

	public void setAccessToken(String sAccessToken)
	{
		this.sAccessToken = sAccessToken;
	}
	
	protected void setAccessCode(String sAccessCode)
	{
		sAccessToken = Utils.getAccessToken(sUserEmail, sAccessCode);
	}

	public String getUserEmail()
	{
		return sUserEmail;
	}

	public String getResponseData()
	{
		if (sResponseData == null)
			return "";
		return sResponseData.trim();
	}
	
	public int getResultCode()
	{
		return iResultCode;
	}

	public int getStatusCode()
	{
		return iStatusCode;
	}
	
	public boolean statusCodeOk()
	{
		return (iStatusCode == HttpStatus.SC_OK);
	}
	
	public String getStatus()
	{
		return "status: " + Integer.toString(iStatusCode) + ", result: " + Integer.toString(iResultCode); 
	}
	
	private boolean makeRequest(HttpRequestBase request)
	{
		iResultCode = CommonData.RESULT_ERROR;
		iStatusCode = HttpStatus.SC_NOT_IMPLEMENTED;
		
		sResponseData = null;
		
		try
		{
			HttpResponse response = httpClient.execute(request);						
			if (response != null)
			{
				iStatusCode = response.getStatusLine().getStatusCode();
				
				if (iStatusCode == HttpStatus.SC_OK)
				{
					HttpEntity entity = response.getEntity();
					if (entity != null)
					{

						try
						{
							sResponseData = EntityUtils.toString(entity);
							iResultCode = CommonData.RESULT_OK;
	  	  		} catch (ParseException e) {
	  	  			iResultCode = CommonData.RESULT_RESPONSE_DATA_ERRROR;
	  	  		} catch (IOException e) {
	  	  			iResultCode = CommonData.RESULT_RESPONSE_DATA_ERRROR;
	  	  		}
	  	  		
	  	  		entity.consumeContent();
	  	  		
					} else {
						iResultCode = CommonData.RESULT_RESPONSE_DATA_ERRROR;
					}
				} else {
					iResultCode = CommonData.RESULT_RESPONSE_STATUS_ERRROR;
				}
				
			} else {
				iResultCode = CommonData.RESULT_RESPONSE_ERRROR;
			}
			
		} catch (ClientProtocolException e) {
			iResultCode = CommonData.RESULT_EXECUTE_ERRROR;
		} catch (IOException e) {
			iResultCode = CommonData.RESULT_EXECUTE_ERRROR;
		}
		
		return (iResultCode == CommonData.RESULT_OK);
	}
	
	private void setHeaders(AbstractHttpMessage msg, String sCommand)
	{
		msg.setHeader(CommonData.NAME_USER_EMAIL, sUserEmail);
		msg.setHeader(CommonData.NAME_ACCESS_TOKEN, sAccessToken);
		msg.setHeader(CommonData.NAME_COMMAND, sCommand);
		msg.setHeader(CommonData.NAME_CLIENT, CommonData.USER_AGENT);
		msg.setHeader(CommonData.NAME_CLIENT_VERSION, ActivityMain.getAppVersion());
		msg.setHeader(CommonData.NAME_CLIENT_LANGUAGE, sLanguageId);

		msg.setHeader("Accept", CommonData.REQUEST_DATA_TYPE_JSON);
		msg.setHeader("Content-Type", CommonData.REQUEST_DATA_TYPE_JSON);
		msg.setHeader("User-Agent", CommonData.USER_AGENT);
	}
	
	//GET method for request with no data
	protected boolean execRequest(String sCommand)
	{
		HttpGet httpGet = new HttpGet(sRequestURL);

		setHeaders(httpGet, sCommand);
		
		return makeRequest(httpGet);
	}

	//POST method for request with data
	protected boolean execRequest(String sCommand, String sData)
	{
		HttpPost httpPost = new HttpPost(sRequestURL);
		
		setHeaders(httpPost, sCommand);
		
		if (sData != null)
		{
			StringEntity entity = null;
			try
			{
				sData = URLEncoder.encode(sData, "UTF-8");

				entity = new StringEntity(sData);
				entity.setContentEncoding("UTF-8");
				entity.setContentType(CommonData.REQUEST_DATA_TYPE_JSON);

				httpPost.setEntity(entity);

				return makeRequest(httpPost);				
			} catch (UnsupportedEncodingException e) {
				iResultCode = CommonData.RESULT_REQUEST_DATA_ERRROR;
				return false;
			}
		} else {
			iResultCode = CommonData.RESULT_REQUEST_DATA_ERRROR;
			return false;
		}
	}	

}
