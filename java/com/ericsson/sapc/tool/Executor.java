package com.ericsson.sapc.tool;

public class Executor {
	
	public static String FILENAME = "C:\\Kevin\\tc_05_01_02_SessionReauth_By_PRA_status_Update.ttcn";
//	public static String FILENAME = "/Users/kevinzhong/ttcn/script/tc_05_01_02_SessionReauth_By_PRA_status_Update.ttcn";
//	public static String FILENAME = "/Users/kevinzhong/ttcn/script/tc_02_15_01_BasicUseCase.ttcn";

	public static void main(String[] args) {
		BufferMgr buffMgr = new BufferMgr();
		buffMgr.readInputFromFile(FILENAME);
		
		buffMgr.showDiagramFromBuffer();

	}

}