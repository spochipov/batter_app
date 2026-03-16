# Add project specific ProGuard rules here.
# By default, the flags in this version of the SDK are used.
-keepattributes *Annotation*
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
