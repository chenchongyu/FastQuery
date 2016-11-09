# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Program Files\Android\android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

#因为使用了360加固   故先不混淆了   这样在代码出问题的时候方便查找问题
#-ignorewarnings
#-dontwarn org.apache.**
#-dontwarn android.**
#-dontwarn android.support.v4.**
#-dontwarn com.alibaba.fastjson.**
#-dontwarn java.nio.file.Files
#-dontwarn java.nio.file.Path
#-dontwarn java.nio.file.OpenOption
#
#-keep class com.amap.api.location.**{*;}
#-keep class com.amap.api.fence.**{*;}
#-keep class com.autonavi.aps.amapapi.model.**{*;}
#
#-keep class io.realm.annotations.RealmModule
#-keep @io.realm.annotations.RealmModule class *
#-keep class io.realm.internal.Keep
#-keep @io.realm.internal.Keep class * { *; }
#-dontwarn javax.**
#-dontwarn io.realm.**
###安沃
#-dontwarn com.immersion.hapticmedia.**
#-dontwarn com.sixth.adwoad.**
#-keep class com.sixth.adwoad.** {*;}
#-keepclasseswithmembernames class * {
#native <methods>;
#}
###mipush
#-keep public class * extends android.content.BroadcastReceiver
#-keep class net.runningcode.MiPushMessageReceiver {*;}

#保留行号的等信息方便崩溃之后还原日志
#-renamesourcefileattribute SourceFile
#-keepattributes SourceFile,LineNumberTable

##友盟
#-keepclassmembers class * {
#   public <init> (org.json.JSONObject);
#}
#-keepclassmembers enum * {
#    public static **[] values();
#    public static ** valueOf(java.lang.String);
#}
#
#-keep public class net.runningcode.R$*{
#    public static final int *;
#}
#-dontwarn com.ut.mini.**
#-dontwarn okio.**
#-dontwarn com.xiaomi.**
#-dontwarn com.squareup.wire.**
#-dontwarn android.support.v4.**
#
#-keepattributes *Annotation*
#
#-keep class android.support.v4.** { *; }
#-keep interface android.support.v4.app.** { *; }
#
#-keep class okio.** {*;}
#-keep class com.squareup.wire.** {*;}
#
#-keep class com.umeng.message.protobuffer.* {
#         public <fields>;
#         public <methods>;
#}
#
#-keep class com.umeng.message.* {
#         public <fields>;
#         public <methods>;
#}
#
#-keep class org.android.agoo.impl.* {
#         public <fields>;
#         public <methods>;
#}
#
#-keep class org.android.agoo.service.* {*;}
#
#-keep class org.android.spdy.**{*;}

