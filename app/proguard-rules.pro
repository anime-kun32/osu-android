# Retrofit
-keepattributes Signature
-keepattributes Exceptions
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# Moshi
-keep class com.squareup.moshi.** { *; }
-keep @com.squareup.moshi.JsonClass class * { *; }
-keepclassmembers class * {
    @com.squareup.moshi.FromJson *;
    @com.squareup.moshi.ToJson *;
}

# OkHttp
-dontwarn okhttp3.**
-keep class okhttp3.** { *; }

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# Keep data models
-keep class com.osu.client.data.model.** { *; }

# Kotlin serialization
-keepattributes *Annotation*
-keepattributes InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
