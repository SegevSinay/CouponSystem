package com.main;

import com.exceptions.DatabaseException;
import com.utilityclasses.DatabaseUtility;

public class Reset {
public static void main(String[] args) throws DatabaseException {
	
	DatabaseUtility.resetAllDBTables();
}
}
