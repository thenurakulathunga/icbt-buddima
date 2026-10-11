plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.printxpress.app"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.printxpress.app"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.fragment)
    implementation(libs.core)
    implementation(libs.constraintlayout)
    implementation(libs.recyclerview)
    implementation(libs.viewpager2)
    implementation(libs.swiperefreshlayout)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.work.runtime)
    implementation(libs.core.splashscreen)
    implementation(libs.glide)
    implementation(libs.shimmer)

    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.arch.core.testing)

    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.test.core)
    androidTestImplementation(libs.test.runner)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.espresso.intents)
    androidTestImplementation(libs.espresso.contrib)
    androidTestImplementation(libs.arch.core.testing)
    androidTestImplementation(libs.work.testing)
}

androidComponents {
    onVariants(selector().withBuildType("debug")) { variant ->
        val compiledClasses = layout.buildDirectory.dir(
            "intermediates/javac/debug/compileDebugJavaWithJavac/classes"
        )
        tasks.register<Javadoc>("javadocDebug") {
            group = "documentation"
            description = "Generates the PrintXpress API documentation (T-100) into build/docs/javadoc."
            dependsOn("compileDebugJavaWithJavac")
            source(fileTree("src/main/java") { include("**/*.java") })
            classpath = files(androidComponents.sdkComponents.bootClasspath) + variant.compileClasspath +
                files(compiledClasses)
            setDestinationDir(layout.buildDirectory.dir("docs/javadoc").get().asFile)
            isFailOnError = true
            (options as StandardJavadocDocletOptions).apply {
                windowTitle = "PrintXpress 1.0 API"
                docTitle = "PrintXpress 1.0 – Android customer app (CSE5011 WRIT1)"
                encoding = "UTF-8"
                charSet = "UTF-8"
                memberLevel = JavadocMemberLevel.PROTECTED
                addStringOption("Xdoclint:all,-missing", "-quiet")
                addStringOption("Xmaxwarns", "10000")
                links("https://docs.oracle.com/en/java/javase/17/docs/api/")
            }
        }
    }
}
