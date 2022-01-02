# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
#查看完整混淆规则
-printconfiguration build/intermediates/proguard-files/full-config.txt
#-------------------------------------------定制化区域----------------------------------------------
#---------------------------------1.实体类---------------------------------
-keep class com.example.bonjour_campus.model.*{ *; }
-keep class com.example.bonjour_campus.Information_Bean{ *; }
-keep class com.example.bonjour_campus.Data{ *; }
-keep class com.example.bonjour_campus.App{ *; }
-keep class com.example.bonjour_campus.Trace{ *; }
#-------------------------------------------------------------------------

#---------------------------------2.第三方包-------------------------------
#-------------------------------------------baseQuickAdapter----------------------------------------------
-keep class com.chad.library.adapter.** {
*;
}
-keep public class * extends com.chad.library.adapter.base.BaseQuickAdapter
-keep public class * extends com.chad.library.adapter.base.BaseViewHolder
#不要混淆BaseViewHolder所有子类的属性与方法
-keepclasseswithmembers class * extends com.chad.library.adapter.base.BaseViewHolder{
    <fields>;
    <methods>;
}
#内部类
-keepclassmembers  class **$** extends com.chad.library.adapter.base.BaseViewHolder {
     <init>(...);
}

#-------------------------------------------okhttp3------------------------------------------------------
# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**
# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*
# OkHttp platform used only on JVM and when Conscrypt dependency is available.
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-keep class com.google.gson.** { *; }
-dontwarn okio.**

#-------------------------------------------glide------------------------------------------------------
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
  *** rewind();
}

# for DexGuard only
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule

#-------------------------------------------tpush------------------------------------------------------
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep class com.tencent.android.tpush.** {*;}
-keep class com.tencent.tpns.baseapi.** {*;}
-keep class com.tencent.tpns.mqttchannel.** {*;}
-keep class com.tencent.tpns.dataacquisition.** {*;}
#-------------------------------------------------------------------------

#---------------------------------3.与js互相调用的类------------------------



#-------------------------------------------------------------------------

#---------------------------------4.反射相关的类和方法-----------------------



#----------------------------------------------------------------------------
#---------------------------------------------------------------------------------------------------

#-------------------------------------------基本不用动区域--------------------------------------------
#---------------------------------基本指令区----------------------------------
#指定代码的压缩级别，在0-7之间，一般是5，不需要修改
-optimizationpasses 5
#混淆时不使用大小写混合，混淆后的类名为小写
-dontusemixedcaseclassnames
#用于告诉ProGuard，不要跳过对非公开类的处理。默认情况下是跳过的，
#因为程序中不会引用它们，有些情况下人们编写的代码与类库中的类在同一个包下，并且对包中内容加以引用，此时需要加入此条声明
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
# 混淆时是否做预校验（Android不需要preverify，去掉这一步可加快混淆速度）
-dontpreverify
#有了verbose这句话，混淆后就会生成映射文件,包含有类名->混淆后类名的映射关系
-verbose
# 使用printmapping指定映射文件的名称(好像没生效)
-printmapping proguardMapping.txt
#混淆时所采用的算法，后面的参数是一个过滤器，这个过滤器是谷歌推荐的算法，一般不改变
-optimizations !code/simplification/cast,!field/*,!class/merging/*
# 保护代码中的Annotation，内部类不被混淆，这在JSON实体映射时非常重要，比如fastJson
-keepattributes *Annotation*,InnerClasses
#避免混淆泛型，这在JSON实体映射时非常重要，比如fastJson
-keepattributes Signature
#抛出异常时保留代码行号，在异常分析中可以方便定位
-keepattributes SourceFile,LineNumberTable
# proguard的优化选项和java虚拟机中的字节码dex优化有冲突，可能会产生一些未知的问题
-dontoptimize
#不出现can't find referenced class的警告
-ignorewarnings
-dontnote
#----------------------------------------------------------------------------

#---------------------------------默认保留区---------------------------------
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService
-keep class android.support.** {*;}

-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
-keep class **.R$* {
 *;
}
-keepclassmembers class * {
    void *(**On*Event);
}
#----------------------------------------------------------------------------

#---------------------------------webview------------------------------------
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String);
}
-dontwarn android.net.http.**
-keep class android.net.http.** { *;}
#----------------------------------------------------------------------------
#---------------------------------------bugly-------------------------------------------------------
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}
#---------------------------------------------------------------------------------------------------
#---------------------------------------RxJava------------------------------------------------------
-dontwarn java.util.concurrent.Flow*
#---------------------------------------------------------------------------------------------------
-dontwarn org.apache.http.conn.scheme.*
-keep interface org.apache.http.conn.scheme.SocketFactory{*;}
-keep class org.apache.http.conn.scheme.LayeredSocketFactory{*;}
-dontwarn com.facebook.drawee.debug.DebugControllerOverlayDrawable
-keep class com.facebook.drawee.debug.DebugControllerOverlayDrawable{*;}
-dontwarn om.facebook.drawee.backends.pipeline.PipelineDraweeController
-keep class com.facebook.drawee.backends.pipeline.PipelineDraweeController{*;}
-dontwarn dalvik.system.VMStack
-keep class dalvik.system.VMStack{*;}
-dontwarn com.tencent.smtt.export.external.DexLoader
-keep class com.tencent.smtt.export.external.DexLoader{*;}
#------------------------------------------------------------------------------
-keep public class cn.jzvd.JZMediaSystem {*; }
-keep public class cn.jzvd.demo.CustomMedia.CustomMedia {*; }
-keep public class cn.jzvd.demo.CustomMedia.JZMediaIjk {*; }
-keep public class cn.jzvd.demo.CustomMedia.JZMediaSystemAssertFolder {*; }

-keep class tv.danmaku.ijk.media.player.** {*; }
-dontwarn tv.danmaku.ijk.media.player.*
-keep interface tv.danmaku.ijk.media.player.** { *; }
-optimizationpasses 5
-keepattributes Signature
-keep public class * extends android.app.Application
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keepclassmembers class **.R$* {
    *;
}
-keep class * implements java.io.Serializable {*;}
# okhttp retrofit rxjava
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**
-dontwarn rx.**
-keep class com.squareup.okhttp3.**{ *; }
-keep class okio.** {*;}

# json相关
-keepattributes Signature
-keep class com.google.code.gson.** { *; }




# Glide
-dontwarn com.github.bumptech.glide.**
-keep class com.github.bumptech.glide.**{ *; }


# zxing
-keep class com.google.zxing.**{*;}

-keep class com.yalantis.**{*;}
-keep class com.sackcentury.**{*;}
-keep class com.github.arcadefire.**{*;}
-keep class com.nikhilpanju.recyclerviewenhanced.**{*;}
-keep class com.github.chenBingX.**{*;}
-keep class com.wenwenwen888.**{*;}
-keep class com.gauravk.bubblenavigation.**{*;}

-keep class com.vincent.filepicker.**{*;}
-keep class com.flipboard.**{*;}
-keep class cn.hugeterry.coordinatortablayout.**{*;}
-keep class com.google.android.material.**{*;}
-keep class com.gjiazhe.**{*;}
-keep class com.github.sd6352051.**{*;}
-keep class com.thefinestartist.**{*;}
-keep class com.jakewharton.**{*;}
-keep class com.jakewharton.**{*;}
-keep class com.github.jorgecastilloprz.**{*;}
-keep class me.zhanghai.android.materialedittext.**{*;}
-keep class com.squareup.picasso.**{*;}
-keep class com.nineoldandroids.**{*;}
-keep class com.daimajia.slider.**{*;}
-keep class com.github.flavienlaurent.discrollview.**{*;}
-keep class com.quinny898.library.persistentsearch.**{*;}
-keep class com.google.code.gson.**{*;}
-keep class com.abby.app.**{*;}
-keep class com.github.zengzhaoxing.**{*;}
-keep class com.github.CymChad.**{*;}
-keep class com.bm.photoview.**{*;}

-keep class androidx.recyclerview.**{*;}
-keep class androidx.constraintlayout.**{*;}
-keep class androidx.appcompat.**{*;}
-keep class android.os.**{*;}
-keep class com.android.os.**{*;}

-keep class com.scwang.smartrefresh.**{*;}
-keep class com.github.hackware1993.**{*;}
-keep class com.github.zcweng.**{*;}
-keep class com.getbase.**{*;}
-keep class com.gyf.barlibrary.**{*;}
-keep class com.github.zcweng.**{*;}
-keep class com.github.zcweng.**{*;}
-keep class com.github.zcweng.**{*;}
-keep class com.github.zcweng.**{*;}




