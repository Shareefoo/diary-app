package com.shareefoo.diaryapp.app;

import android.content.Context;
import android.util.Log;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.sigv4.CognitoUserPoolsAuthProvider;
import com.shareefoo.diaryapp.data.SPManager;

/**
 * Created by shareefoo
 */

public class ClientFactory {

    private static volatile AWSAppSyncClient client;

    public static synchronized void init(final Context context) {
        if (client == null) {
            final AWSConfiguration awsConfiguration = new AWSConfiguration(context);
            client = AWSAppSyncClient.builder()
                    .context(context)
                    .awsConfiguration(awsConfiguration)
                    .cognitoUserPoolsAuthProvider(new CognitoUserPoolsAuthProvider() {
                        @Override
                        public String getLatestAuthToken() {
                            try {
                                String sub = AWSMobileClient.getInstance().getTokens().getIdToken().getClaim("sub");
                                Log.i("USER_ID", sub);

                                // Store user id (sub) in shared preferences
                                SPManager.getInstance(context).putString("user_id", sub);

                                return AWSMobileClient.getInstance().getTokens().getIdToken().getTokenString();
                            } catch (Exception e) {
                                Log.e("APPSYNC_ERROR", e.getLocalizedMessage());
                                return e.getLocalizedMessage();
                            }
                        }
                    }).build();
        }
    }

    public static synchronized AWSAppSyncClient appSyncClient() {
        return client;
    }

}
