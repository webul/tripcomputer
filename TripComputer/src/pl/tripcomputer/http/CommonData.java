package pl.tripcomputer.http;


public class CommonData
{
	//result codes
	public final static int RESULT_OK = 0;
	public final static int RESULT_ERROR = -1;
	
	public final static int RESULT_EXECUTE_ERRROR = 1;
	
	public final static int RESULT_RESPONSE_ERRROR = 2;
	public final static int RESULT_RESPONSE_STATUS_ERRROR = 3;
	public final static int RESULT_RESPONSE_DATA_ERRROR = 4;

	public final static int RESULT_REQUEST_DATA_ERRROR = 5;

	
	//commands
	public static final String NAME_COMMAND = "data_command";
	public static final String NAME_USER_EMAIL = "data_user_email";
	public static final String NAME_ACCESS_TOKEN = "data_access_token";
	public static final String NAME_CLIENT = "data_client";
	public static final String NAME_CLIENT_VERSION = "data_client_version";
	public static final String NAME_CLIENT_LANGUAGE = "data_client_language";

	public static final String CMD_GET_ACCESS_CODE = "get_access_code";
	public static final String CMD_GET_ACCESS_TOKEN = "get_access_token";
	public static final String CMD_GET_WAYPOINT = "get_waypoint";
	public static final String CMD_GET_WAYPOINT_LIST = "get_waypoint_list";
	public static final String CMD_GET_WAYPOINT_COLLECTION = "get_waypoint_collection";
	public static final String CMD_POST_WAYPOINT = "post_waypoint";
	

	//server
	public final static String sAdminEmail = "tripcomputerservice@gmail.com";
	
	public final static String sServletURL = "http://tripcomputerwebservice.appspot.com/main";
		
	//transport
	public final static String REQUEST_DATA_TYPE_JSON = "application/json";
	
	public final static String USER_AGENT = "TripComputer";
	
}
