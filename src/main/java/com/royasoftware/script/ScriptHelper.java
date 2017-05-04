package com.royasoftware.script;

import java.io.File;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

/**
 * <p>
 * Description: AutoIt Script helper for development purposes
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: Roya Software
 * </p>
 * 
 * @author RB
 * @version 1.0
 */
public abstract class ScriptHelper {
	private static final Logger logger = Logger.getLogger(ScriptHelper.class);
	static private boolean ACTIVE = false;

	static public final int MAGO_STOP = 0;
	static public final int REPORT_DIALOG = 1;
	static public final int ORGANISATION_FORM = 2;
	static public final int CONTACTPERSON_FORM = 3;
	static public final int PRIVATECUSTOMER_FORM = 4;
	static public final int LABORUSER_FORM = 5;
	static public final int LOGIN_DIALOG = 6;
	static public final int ACTUAL_ACTION_ORDER = 9;
	static public final int IMPORT_ACTION_ORDER = 10;
	static public final int CREATE_ORDER_ACTION = 11;
	// static public final int GENETICSFRAME_START = 12;
	static public final int ACTUAL_ACTION_GENETICS = 13;
	static public final int NEW_MARKERALLEL = 14;
	static public final int ACTUAL_ACTION_IMEX = 17;
	static public final int IMPORT_ACTION_GTF = 18;
	static public final int IMPORT_ACTION_INDIVIDUAL_DATA_DIALOG = 19;
	static public final int IMPORT_ACTION_MARKER = 20;
	static public final int WINDOW_CLOSER = 21;
	static public final int ACTUAL_ACTION_SERVICES = 22;
	static public final int ALERT_DEFENSE_CLOSER = 23;
	static public final int RUN_WEB_APP = 24;

	// static public final int IMEXFRAME_START = 15;
	// static public final int ANIMALINFOFRAME_START = 16;
	// static public final int BARCODEFRAME_START = 19;
	// static public final int SERVICESFRAME_START = 7;
	// static public final int ORDERFRAME_START = 8;

	static public final int ACTUAL_ACTION_BARCODE = 30;
	static public final int PRINT_BARCODE = 31;
	static public final int CANCEL_DIALOG_ORDER = 32;
	static public final int ACTUAL_ACTION_ANIMALINFO = 33;
	static public final int REPORT_PARAMETER_AND_SEARCH = 34;
	static public final int CUSTOMER_BILLING_REPORT_PDF = 35;
	static public final int ACTUAL_ACTION_OPTIONS = 36;
	static public final int CONFIG_INDIVIDUAL_DATA = 37;

	static public final int MAGO_START = 50;

	static {
		try {			
			File f = new File("C:\\Programme\\AutoIt3\\AutoIt3.exe");
			if(f.exists() && !f.isDirectory()) { 
				ResourceBundle rb = ResourceBundle.getBundle("application");
				String scriptActive = rb.getString("script.active");
				ACTIVE = new Boolean(scriptActive).booleanValue();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			ACTIVE = false;
		}
	}

	public static void run(int scriptId) {
		run(scriptId, null);
	}

	public static void run(int scriptId, String param) {
		if (!ACTIVE)
			return;
		Process process = null;

		try {
			switch (scriptId) {
			case MAGO_STOP:
				process = Runtime.getRuntime()
						.exec("C:\\Programme\\AutoIt3\\AutoIt3.exe C:\\MyAutoItMacros\\mago\\stop.au3");
				process.waitFor();
				break;
			case MAGO_START:
				process = Runtime.getRuntime()
						.exec("C:\\Programme\\AutoIt3\\AutoIt3.exe C:\\MyAutoItMacros\\mago\\start.au3");
				break;
			case REPORT_DIALOG:
				process = Runtime.getRuntime().exec(
						"C:\\Programme\\AutoIt3\\AutoIt3.exe C:\\MyAutoItMacros\\mago\\imex\\ReportFileDialog.au3");
				break;
			case ORGANISATION_FORM:
				process = Runtime.getRuntime().exec(
						"C:\\Programme\\AutoIt3\\AutoIt3.exe C:\\MyAutoItMacros\\mago\\user\\organisationForm.au3");
				break;
			case CONTACTPERSON_FORM:
				process = Runtime.getRuntime().exec(
						"C:\\Programme\\AutoIt3\\AutoIt3.exe C:\\MyAutoItMacros\\mago\\user\\contactPersonForm.au3");
				break;
			case PRIVATECUSTOMER_FORM:
				process = Runtime.getRuntime().exec(
						"C:\\Programme\\AutoIt3\\AutoIt3.exe C:\\MyAutoItMacros\\mago\\user\\privateCustomerForm.au3");
				break;
			case LABORUSER_FORM:
				process = Runtime.getRuntime()
						.exec("C:\\Programme\\AutoIt3\\AutoIt3.exe C:\\MyAutoItMacros\\mago\\user\\laborUserForm.au3");
				break;
			case LOGIN_DIALOG:
				process = Runtime.getRuntime()
						.exec("C:\\Programme\\AutoIt3\\AutoIt3.exe C:\\MyAutoItMacros\\mago\\loginDialog.au3");
				break;
			case ACTUAL_ACTION_SERVICES:
				process = Runtime.getRuntime().exec(
						"C:\\Programme\\AutoIt3\\AutoIt3.exe C:\\MyAutoItMacros\\mago\\services\\actualActionServices.au3");
				break;
			case ACTUAL_ACTION_ORDER:
				process = Runtime.getRuntime().exec(
						"C:\\Programme\\AutoIt3\\AutoIt3.exe C:\\MyAutoItMacros\\mago\\order\\actualActionOrder.au3");
				break;
			case IMPORT_ACTION_ORDER:
				process = Runtime.getRuntime().exec(
						"C:\\Programme\\AutoIt3\\AutoIt3.exe C:\\MyAutoItMacros\\mago\\order\\ImportDialogAction.au3");
				break;
			case CREATE_ORDER_ACTION:
				process = Runtime.getRuntime()
						.exec("C:\\Programme\\AutoIt3\\AutoIt3.exe C:\\MyAutoItMacros\\mago\\order\\createOrder.au3");
				break;
			case ACTUAL_ACTION_GENETICS:
				process = Runtime.getRuntime().exec(
						"C:\\Programme\\AutoIt3\\AutoIt3.exe C:\\MyAutoItMacros\\mago\\genetics\\actualActionGenetics.au3");
				break;
			case NEW_MARKERALLEL:
				process = Runtime.getRuntime().exec(
						"C:\\Programme\\AutoIt3\\AutoIt3.exe C:\\MyAutoItMacros\\mago\\genetics\\NewMarkerallel.au3");
				break;
			case ACTUAL_ACTION_IMEX:
				process = Runtime.getRuntime().exec(
						"C:\\Programme\\AutoIt3\\AutoIt3.exe C:\\MyAutoItMacros\\mago\\imex\\actualActionImex.au3");
				break;
			case IMPORT_ACTION_GTF:
				process = Runtime.getRuntime().exec(
						"C:\\Programme\\AutoIt3\\AutoIt3.exe C:\\MyAutoItMacros\\mago\\imex\\importGtfDialogAction.au3");
				break;
			case IMPORT_ACTION_INDIVIDUAL_DATA_DIALOG:
				process = Runtime.getRuntime().exec(
						"C:\\Programme\\AutoIt3\\AutoIt3.exe C:\\MyAutoItMacros\\mago\\imex\\importIndDataDialogAction.au3");
				break;
			case ACTUAL_ACTION_BARCODE:
				process = Runtime.getRuntime().exec(
						"C:\\Programme\\AutoIt3\\AutoIt3.exe C:\\MyAutoItMacros\\mago\\barcodes\\actualActionBarcodes.au3");
				break;
			case PRINT_BARCODE:
				process = Runtime.getRuntime()
						.exec("C:\\Programme\\AutoIt3\\AutoIt3.exe C:\\MyAutoItMacros\\mago\\barcodes\\Print.au3");
				break;
			case CANCEL_DIALOG_ORDER:
				process = Runtime.getRuntime().exec(
						"C:\\Programme\\AutoIt3\\AutoIt3.exe C:\\MyAutoItMacros\\mago\\order\\CancelDialogKiller.au3");
				break;
			case ACTUAL_ACTION_ANIMALINFO:
				process = Runtime.getRuntime().exec(
						"C:\\Programme\\AutoIt3\\AutoIt3.exe C:\\MyAutoItMacros\\mago\\individualinfo\\actualActionIndividualInfo.au3");
				break;
			case ACTUAL_ACTION_OPTIONS:
				process = Runtime.getRuntime().exec(
						"C:\\Programme\\AutoIt3\\AutoIt3.exe C:\\MyAutoItMacros\\mago\\options\\actualActionOptions.au3");
				break;
			case REPORT_PARAMETER_AND_SEARCH:
				process = Runtime.getRuntime().exec(
						"C:\\Programme\\AutoIt3\\AutoIt3.exe C:\\MyAutoItMacros\\mago\\imex\\SetParameterAndSearch.au3");
				break;
			case CONFIG_INDIVIDUAL_DATA:
				process = Runtime.getRuntime().exec(
						"C:\\Programme\\AutoIt3\\AutoIt3.exe C:\\MyAutoItMacros\\mago\\imex\\ConfigIndDataImport.au3");
				break;
			case CUSTOMER_BILLING_REPORT_PDF:
				process = Runtime.getRuntime()
						.exec("C:\\Programme\\AutoIt3\\AutoIt3.exe C:\\MyAutoItMacros\\mago\\imex\\CBReportPdf.au3");
				break;
			case IMPORT_ACTION_MARKER:
				process = Runtime.getRuntime().exec(
						"C:\\Programme\\AutoIt3\\AutoIt3.exe C:\\MyAutoItMacros\\mago\\imex\\importMarkerDialogAction.au3");
				break;
			case WINDOW_CLOSER:
				process = Runtime.getRuntime().exec(
						"C:\\Programme\\AutoIt3\\AutoIt3.exe C:\\MyAutoItMacros\\WinTracker\\Closer.au3 " + param);
				break;
			case ALERT_DEFENSE_CLOSER:
				process = Runtime.getRuntime().exec(
						"C:\\Programme\\AutoIt3\\AutoIt3.exe C:\\MyAutoItMacros\\WinTracker\\Closer.au3 " + param);
				break;
			case RUN_WEB_APP:
//				process = Runtime.getRuntime().exec(
//						"C:\\Programme\\AutoIt3\\AutoIt3.exe D:\\RP\\Tests\\SpringBoot_Training\\relaunchwebapp.au3");
				break;

			default:
				break;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
