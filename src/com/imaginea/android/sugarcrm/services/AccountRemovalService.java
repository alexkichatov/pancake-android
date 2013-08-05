package com.imaginea.android.sugarcrm.services;

import android.accounts.AccountManager;
import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.imaginea.android.sugarcrm.provider.SugarCRMProvider;
import com.imaginea.android.sugarcrm.util.Util;

public class AccountRemovalService extends IntentService {

	// Action used for BroadcastReceiver entry point
    private static final String ACTION_BROADCAST = "broadcast_receiver";

    public AccountRemovalService() {
        // Class name will be the thread name.
        super(AccountRemovalService.class.getName());

        // Intent should be redelivered if the process gets killed before completing the job.
        setIntentRedelivery(true);
    }
    

    private void clearTheDb(){
    	
    	ContentResolver contentResolver = getContentResolver();
    	
    	Uri accountsUrl = Uri.parse("content://" + SugarCRMProvider.AUTHORITY + "/" + Util.ACCOUNTS);
    	contentResolver.delete(accountsUrl, null, null);
    	
    	Uri accountsCasesUrl = Uri.parse("content://" + SugarCRMProvider.AUTHORITY + "/" + Util.ACCOUNTS_CASES);
    	contentResolver.delete(accountsCasesUrl, null, null);
    	
    	Uri accountsContUrl = Uri.parse("content://" + SugarCRMProvider.AUTHORITY + "/" + Util.ACCOUNTS_CONTACTS);
    	contentResolver.delete(accountsContUrl, null, null);
    	
    	Uri accountsOpporUrl = Uri.parse("content://" + SugarCRMProvider.AUTHORITY + "/" + Util.ACCOUNTS_OPPORTUNITIES);
    	contentResolver.delete(accountsOpporUrl, null, null);
    	
    	Uri aclActionsUrl = Uri.parse("content://" + SugarCRMProvider.AUTHORITY + "/" + Util.ACL_ACTIONS);
    	contentResolver.delete(aclActionsUrl, null, null);
    	
    	Uri aclRolesUrl = Uri.parse("content://" + SugarCRMProvider.AUTHORITY + "/" + Util.ACL_ROLES);
    	contentResolver.delete(aclRolesUrl, null, null);
    	
    	Uri alarmUrl = Uri.parse("content://" + SugarCRMProvider.AUTHORITY + "/" + Util.ALARMS);
    	contentResolver.delete(alarmUrl, null, null);
    	
    	Uri callsUrl = Uri.parse("content://" + SugarCRMProvider.AUTHORITY + "/" + Util.CALLS);
    	contentResolver.delete(callsUrl, null, null);
    	
    	Uri campaignsUrl = Uri.parse("content://" + SugarCRMProvider.AUTHORITY + "/" + Util.CAMPAIGNS);
    	contentResolver.delete(campaignsUrl, null, null);
    	
    	Uri casesUrl = Uri.parse("content://" + SugarCRMProvider.AUTHORITY + "/" + Util.CASES);
    	contentResolver.delete(casesUrl, null, null);
    	
    	Uri contactsUrl = Uri.parse("content://" + SugarCRMProvider.AUTHORITY + "/" + Util.CONTACTS);
    	contentResolver.delete(contactsUrl, null, null);
    	
    	Uri contactsCasesUrl = Uri.parse("content://" + SugarCRMProvider.AUTHORITY + "/" + Util.CONTACTS_CASES);
    	contentResolver.delete(contactsCasesUrl, null, null);
    	
    	Uri contactsOpporUrl = Uri.parse("content://" + SugarCRMProvider.AUTHORITY + "/" + Util.CONTACTS_OPPORTUNITIES);
    	contentResolver.delete(contactsOpporUrl, null, null);
    	
    	Uri leadsUrl = Uri.parse("content://" + SugarCRMProvider.AUTHORITY + "/" + Util.LEADS);
    	contentResolver.delete(leadsUrl, null, null);
    	
    	Uri linkFieldsUrl = Uri.parse("content://" + SugarCRMProvider.AUTHORITY + "/" + Util.LINK_FIELDS);
    	contentResolver.delete(linkFieldsUrl, null, null);
    	
    	Uri meetingsUrl = Uri.parse("content://" + SugarCRMProvider.AUTHORITY + "/" + Util.MEETINGS);
    	contentResolver.delete(meetingsUrl, null, null);
    	
    	Uri modulesUrl = Uri.parse("content://" + SugarCRMProvider.AUTHORITY + "/" + Util.MODULES);
    	contentResolver.delete(modulesUrl, null, null);
    	
    	Uri moduleFieldsUrl = Uri.parse("content://" + SugarCRMProvider.AUTHORITY + "/" + Util.MODULES_FIELDS);
    	contentResolver.delete(moduleFieldsUrl, null, null);
    	
    	Uri opportunitiesUrl = Uri.parse("content://" + SugarCRMProvider.AUTHORITY + "/" + Util.OPPORTUNITIES);
    	contentResolver.delete(opportunitiesUrl, null, null);
    	
    	Uri recentUrl = Uri.parse("content://" + SugarCRMProvider.AUTHORITY + "/" + Util.RECENT);
    	contentResolver.delete(recentUrl, null, null);
    	
    	Uri syncUrl = Uri.parse("content://" + SugarCRMProvider.AUTHORITY + "/" + Util.SYNC_TABLE);
    	contentResolver.delete(syncUrl, null, null);
    	
    	Uri uesersUrl = Uri.parse("content://" + SugarCRMProvider.AUTHORITY + "/" + Util.USERS);
    	contentResolver.delete(uesersUrl, null, null);
    	
    }
    
	@Override
	protected void onHandleIntent(Intent intent) {
		final String action = intent.getAction();
        if (ACTION_BROADCAST.equals(action) ) {
        	final Intent broadcastIntent = intent.getParcelableExtra(Intent.EXTRA_INTENT);
            final String broadcastAction = broadcastIntent.getAction();
            if (AccountManager.LOGIN_ACCOUNTS_CHANGED_ACTION.equals(broadcastAction)) {
            	android.accounts.Account[] sugarCRMAccounts = AccountManager.get(this)
                .getAccountsByType("com.imaginea.android.sugarcrm");
            	
            	if( (sugarCRMAccounts != null) && ( sugarCRMAccounts.length == 0) ){
            		clearTheDb(); 
            		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            		prefs.edit().clear().commit();
            	}
            }
        	
        }
		
	}

	public static void processBroadcastIntent(Context context, Intent intent) {
		 Intent i = new Intent(context, AccountRemovalService.class);
	        i.setAction(ACTION_BROADCAST);
	        i.putExtra(Intent.EXTRA_INTENT, intent);
	        context.startService(i);
		
	}

	

}
