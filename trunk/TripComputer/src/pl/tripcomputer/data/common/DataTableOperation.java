package pl.tripcomputer.data.common;

import pl.tripcomputer.Command;


public class DataTableOperation
{
	public final static int OP_NONE = 0;
	public final static int OP_INSERT = 1;
	public final static int OP_UPDATE = 2;
	public final static int OP_DELETE = 3;
	
	public long lRowId = -1;
	public int iOperation = OP_NONE;
	public int iCommand = Command.CMD_NONE;
	public String sTrackName = null;
}
