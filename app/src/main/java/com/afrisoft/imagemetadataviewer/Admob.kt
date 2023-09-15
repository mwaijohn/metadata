package com.afrisoft.imagemetadataviewer

import android.app.Activity
import android.content.Context
import android.content.pm.ApplicationInfo
import com.applovin.sdk.AppLovinSdk
import com.applovin.sdk.AppLovinSdkConfiguration
import com.facebook.ads.AdSettings
import com.facebook.ads.AudienceNetworkAds
import com.google.android.gms.ads.*
import com.google.android.gms.ads.admanager.AdManagerAdView


class Admob {
    companion object {

        fun isDebuggable(context: Context): Boolean {
            val appInfo = context.applicationInfo
            return appInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE !== 0
        }

        fun banner(context: Context): AdManagerAdView {
            val adView = AdManagerAdView(context)

            adView.setAdSizes(AdSize.BANNER)

            val addUnitId = if (isDebuggable(context)) {
                context.getString(R.string.admob_banner_test)
            }else {
                context.getString(R.string.admob_banner)
            }

            adView.adUnitId = addUnitId

            adView.loadAd(AdRequest.Builder().build())
            return adView
        }

        fun initMediationSdk(context: Context){
            MobileAds.initialize(context) {}

            AppLovinSdk.getInstance( context ).mediationProvider = "max"
            AdSettings.setDataProcessingOptions( arrayOf<String>() )
            AppLovinSdk.getInstance( context ).initializeSdk {
                when (it.consentDialogState) {
                    AppLovinSdkConfiguration.ConsentDialogState.APPLIES -> {
                        // Show user consent dialog
                        val userService = AppLovinSdk.getInstance( context ).userService
                        userService.showConsentDialog( context as Activity) {
                        }
                    }
                    AppLovinSdkConfiguration.ConsentDialogState.DOES_NOT_APPLY -> {
                        // No need to show consent dialog, proceed with initialization
                    }
                    else -> {
                        // Consent dialog state is unknown. Proceed with initialization, but check if the consent
                        // dialog should be shown on the next application initialization
                    }
                }
            }

            AudienceNetworkAds
                .buildInitSettings(context)
                .initialize()
        }

    }
}