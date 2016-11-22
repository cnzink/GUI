package com.softworkz.dna;

/**
 * this class is the native interface to the DNAINT library. For the details, please refer to the DNAINT library
 * @author Song Han
 */
public class DNAINT {
    
    public final static int ERR_NO_ERROR			= 0;
    public final static int ERR_DNA_DISABLE			= -2;
    public final static int ERR_VALIDATION_WARNING		= -1;
    public final static int ERR_NO_CONNECTION			= 1;
    public final static int ERR_CONNECTION_LOST			= 2;
    public final static int ERR_LOCKOUT				= 3;
    public final static int ERR_INVALID_CDM			= 4;
    public final static int ERR_INVALID_PRODUCT_KEY		= 5;
    public final static int ERR_INVALID_ACTIVATION_CODE		= 6;
    public final static int ERR_INVALID_PASSWORD		= 7;
    public final static int ERR_ACTIVATION_EXPECTED		= 8;
    public final static int ERR_REACTIVATION_EXPECTED		= 9;
    public final static int ERR_BANNED_ACTIVATION_CODE		= 10;
    public final static int ERR_NO_EMAIL_PROVIDED		= 11;
    public final static int ERR_INVALID_BUILD_NO			= 12;
    public final static int ERR_EVAL_CODE_ALREADY_SENT	= 13;
    public final static int ERR_EVAL_CODE_UNAVAILABLE		= 14;
    public final static int ERR_CDM_HAS_EXPIRED			= 15;
    public final static int ERR_CODE_HAS_EXPIRED		= 16;
    public final static int ERR_CANCELLED_BY_USER		= 98;
    public final static int ERR_OPERATION_FAILED		= 99;

    public native int DNAActivate(String product_key, String activation_code, String password, String email);
    public native int DNAActivateOffline(String product_key, String activation_code);
    public native int DNAReactivate(String product_key, String activation_code, String password, String new_password);
    public native int DNAValidate(String product_key);
    public native int DNAValidate2(String product_key);
    public native int DNAValidate3(String product_key);
    public native int DNAValidate4(String product_key);
    public native int DNAValidate5(String product_key);
    public native int DNADeactivate(String product_key, String password);
    public native int DNASendPassword(String product_key, String activation_code);
    public native int DNAQuery(String product_key, String activation_code);
    public native int DNAInfoTag(String product_key, String activation_code, String tag);
    public native int DNASetBuildNo(String build_no);
    public native int DNASendEvalCode(String product_key, String email, int Use_MachineID);
    public native int DNASetCDMPathName(String path_name);
    public native int DNASetINIPathName(String path_name);
    public native int DNAProtectionOK(String product_key, int Request_EvalCode, int Use_MachineID);
    public native int DNASetProxy(String server, String port, String username, String password);
    public native int DNASetLanguage(int language);
    public native int DNAEvaluateNow(String product_key);
    public native int DNAUseIESettings(int Use_IESettings);
    public native String DNAError(int error_no);
    public native String DNAParam(String param);

    static {
            System.loadLibrary("DNA");
    }
}
