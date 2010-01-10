package pl.tripcomputer;

import pl.tripcomputer.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Message;


public class UserAlert
{
	//fields
	public enum Result
	{
		UNKNOWN,
		SUCCESS,

		DB_ACCESS_ERROR,
		DATA_INSERT_ERROR,
		DATA_UPDATE_ERROR,
		DATA_DELETE_ERROR,
		DATA_FIND_ERROR,
		CREATE_TABLE_ERROR,
		TABLE_NOT_EXISTS,
		ENTER_ALL_DATA,
		TABLE_ACCESS_ERROR,
		DATA_VALUES_ERROR,
		MORE_DATA_REQUIRED,
		WRONG_VALUES,
		
		LON_OUT_OF_RANGE,
		LAT_OUT_OF_RANGE,		
		
		CLOSE_TRACK_ERROR,
		RESUME_RECORDING_ERROR,
		
		NAME_TOO_LONG,
		DESC_TOO_LONG,
		
		SERVER_REJECTED_DATA,
	};
	
	private static String sAlertTitle = null;
	private static String sOK = null;
	private static String sYes = null;
	private static String sNo = null;

	
	//methods
	private static void init(Context context)
	{
		UserAlert.sAlertTitle = context.getResources().getString(R.string.titleAlert);
		UserAlert.sOK = context.getResources().getString(R.string.alert_dialog_ok);
		UserAlert.sYes = context.getResources().getString(R.string.alert_dialog_yes);
		UserAlert.sNo = context.getResources().getString(R.string.alert_dialog_no);
	}

	public static void show(Context context, String sText)
	{
		init(context);
		
		AlertDialog alert = new AlertDialog.Builder(context).create();
		alert.setTitle(sAlertTitle);
		alert.setMessage(sText);
		alert.setIcon(android.R.drawable.ic_dialog_alert);
		alert.setButton(AlertDialog.BUTTON_POSITIVE, sOK, (Message)null);
		alert.show();
	}

	public static void show(Context context, Result result)
	{
		final String sMsg = getDescription(context, result);
		show(context, sMsg);
	}

	public static void show(Context context, String sText, DialogInterface.OnClickListener onButtonClicked)
	{
		init(context);

		AlertDialog alert = new AlertDialog.Builder(context).create();
		alert.setTitle(sAlertTitle);
		alert.setMessage(sText);
		alert.setIcon(android.R.drawable.ic_dialog_alert);
		alert.setButton(AlertDialog.BUTTON_POSITIVE, sOK, onButtonClicked);		
		alert.show();
	}

	public static void show(Context context, String sText, DialogInterface.OnClickListener onButtonClicked, DialogInterface.OnDismissListener onDismiss)
	{
		init(context);

		AlertDialog alert = new AlertDialog.Builder(context).create();
		alert.setTitle(sAlertTitle);
		alert.setMessage(sText);
		alert.setIcon(android.R.drawable.ic_dialog_alert);
		alert.setButton(AlertDialog.BUTTON_POSITIVE, sOK, onButtonClicked);		
		alert.setOnDismissListener(onDismiss);
		alert.show();
	}
	
	public static void question(Context context, String sText, DialogInterface.OnClickListener onButtonClicked)
	{
		init(context);
		
		AlertDialog alert = new AlertDialog.Builder(context).create();		
		alert.setTitle(sAlertTitle);
		alert.setMessage(sText);
		alert.setIcon(android.R.drawable.ic_dialog_alert);
		alert.setButton(AlertDialog.BUTTON_POSITIVE, sYes, onButtonClicked);
		alert.setButton(AlertDialog.BUTTON_NEGATIVE, sNo, (DialogInterface.OnClickListener)null);
		alert.show();
	}

	public static void question(Context context, int res_string_id, DialogInterface.OnClickListener onButtonClicked)
	{			
		final String sMsg = context.getString(res_string_id);
		question(context, sMsg, onButtonClicked);
	}
	
	private static String getDescription(Context context, Result result)
	{
		switch (result)
		{
			case SUCCESS: return context.getString(R.string.alert_msg_ok);
	
			case DB_ACCESS_ERROR: return context.getString(R.string.alert_msg_db_access_error);
			case DATA_INSERT_ERROR: return context.getString(R.string.alert_msg_data_insert_error);
			case DATA_UPDATE_ERROR: return context.getString(R.string.alert_msg_data_update_error);
			case DATA_DELETE_ERROR: return context.getString(R.string.alert_msg_data_delete_error);
			case CREATE_TABLE_ERROR: return context.getString(R.string.alert_msg_create_table_error);
			case TABLE_NOT_EXISTS: return context.getString(R.string.alert_msg_table_not_exists);
			case ENTER_ALL_DATA: return context.getString(R.string.alert_msg_enter_all_data);
			case TABLE_ACCESS_ERROR: return context.getString(R.string.alert_msg_table_access_error);
			case DATA_VALUES_ERROR: return context.getString(R.string.alert_msg_data_values_error);
			case MORE_DATA_REQUIRED: return context.getString(R.string.alert_msg_more_data_required);
			case WRONG_VALUES: return context.getString(R.string.alert_msg_wrong_values);			
			case DATA_FIND_ERROR: return context.getString(R.string.alert_msg_data_find_error);
			
			case LON_OUT_OF_RANGE: return context.getString(R.string.alert_msg_lon_out);
			case LAT_OUT_OF_RANGE: return context.getString(R.string.alert_msg_lat_out);
			
			case CLOSE_TRACK_ERROR: return context.getString(R.string.alert_msg_close_track_error);
			case RESUME_RECORDING_ERROR: return context.getString(R.string.alert_msg_resume_recording_error);
			
			case NAME_TOO_LONG: return context.getString(R.string.alert_msg_name_too_long);
			case DESC_TOO_LONG: return context.getString(R.string.alert_msg_desc_too_long);
			
			case SERVER_REJECTED_DATA: return context.getString(R.string.alert_msg_server_rejected_data);
		}
		return "";
	}

	
}
