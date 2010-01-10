package pl.tripcomputer.data.common;


public class DataField
{
	//types
	public static final byte TYPE_INT = 0;
	public static final byte TYPE_BOOL = 1;
	public static final byte TYPE_NUMERIC = 2;
	public static final byte TYPE_TEXT = 3;

	//fields
	private int index = 0;
	
	public String sFieldName = null;
	public byte FieldType = TYPE_INT;
	public boolean bCanBeNull = true;
	public boolean bPrimaryKey = false;
	public boolean bForEdit = false;
	
	
	//methods
	public DataField(int index, String sFieldName, byte FieldType, boolean bCanBeNull, boolean bPrimaryKey, boolean bForEdit)
	{
		this.index = index;
		this.sFieldName = sFieldName;
		this.FieldType = FieldType;
		this.bCanBeNull = bCanBeNull;
		this.bPrimaryKey = bPrimaryKey;
		this.bForEdit = bForEdit;
	}
	
	public String getColumnDefinition()
	{
		String s = sFieldName + " " + getSqlType(FieldType);
		if (bPrimaryKey)
			s += " PRIMARY KEY";
		if (!bCanBeNull)
			s += " NOT NULL";
		return s;
	}
	
	public String getSqlType(int value)
	{
		switch (value)
		{
			case TYPE_INT: return "INTEGER";
			case TYPE_BOOL: return "INTEGER";
			case TYPE_NUMERIC: return "REAL";
			case TYPE_TEXT: return "TEXT";
		}
		return "TEXT";
	}
	
	public int getIndex()
	{
		return index;
	}	
	
}
