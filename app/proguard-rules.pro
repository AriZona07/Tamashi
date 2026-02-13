# Keep annotation classes
-keep class javax.annotation.Nullable
-keep class javax.annotation.Nonnull

# Keep class members that are annotated
-keepclassmembers class ** {
    @javax.annotation.Nullable <fields>;
    @javax.annotation.Nullable <methods>;
    @javax.annotation.Nonnull <fields>;
    @javax.annotation.Nonnull <methods>;
}
