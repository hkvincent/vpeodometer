# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\Administrator.ZGC-20130905TJJ\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
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
-keep class com.badlogic.gdx.** { *; }
-keep class org.cocos2d.gdi.**{*;}
-keep class lecho.lib.hellocharts.**{*;}
-keep class com.litesuits.orm.**{*;}
-keep class com.nineoldandroids.**{*;}
-ignorewarnings

#-libraryjars libs/cocos2d-android.jar
#-libraryjars libs/hellocharts-library-1.5.8.jar
#-libraryjars libs/lite-orm-1.7.0.jar
#-libraryjars libs/nineoldandroids-2.4.0.jar