plugins {
    java
    `maven-publish`
    id ("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "top.mrxiaom.miao"
version = "1.0.3"
val targetJavaVersion = 8
val shadowGroup = "top.mrxiaom.miao.libs"

repositories {
    mavenLocal()
    mavenCentral()
    maven("http://sacredcraft.cn:8081/repository/releases/") {
        isAllowInsecureProtocol = true
    }
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://jitpack.io")
    maven("https://repo.rosewooddev.io/repository/public/")
    maven("https://repo.cyr1en.com/snapshots/") {
        mavenContent {
            includeGroup("com.cyr1en")
        }
    }
    maven("https://oss.sonatype.org/content/groups/public/")
}

@Suppress("VulnerableLibrariesLocal")
dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20-R0.1-SNAPSHOT")
    // compileOnly("org.spigotmc:spigot:1.20") // NMS

    compileOnly("org.maxgamer:QuickShop:5.1.0.5-SNAPSHOT") { isTransitive = false }
    compileOnly("com.cyr1en:CommandPrompter:2.12.0")
    compileOnly(files("libs/MockTrMenu-bukkit-util-6.2.3.jar"))

    implementation("org.jetbrains:annotations:24.0.0")
    implementation("com.github.technicallycoded:FoliaLib:0.4.4")
    implementation("top.mrxiaom:PluginBase:1.3.8")
}
java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
}
tasks {
    shadowJar {
        archiveClassifier.set("")
        mapOf(
            "org.intellij.lang.annotations" to "annotations.intellij",
            "org.jetbrains.annotations" to "annotations.jetbrains",
            "com.tcoded.folialib" to "folialib",
            "top.mrxiaom.pluginbase" to "base",
        ).forEach { (original, target) ->
            relocate(original, "$shadowGroup.$target")
        }

        exclude("top/mrxiaom/pluginbase/func/AbstractGui*")
        exclude("top/mrxiaom/pluginbase/func/gui/*")
        exclude("top/mrxiaom/pluginbase/utils/Adventure*")
        exclude("top/mrxiaom/pluginbase/utils/Bytes*")
        exclude("top/mrxiaom/pluginbase/utils/IA*")
        exclude("top/mrxiaom/pluginbase/utils/ItemStackUtil*")
    }
    build {
        dependsOn(shadowJar)
    }
    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
            options.release.set(targetJavaVersion)
        }
    }
    processResources {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        from(sourceSets.main.get().resources.srcDirs) {
            expand(mapOf("version" to version))
            include("plugin.yml")
        }
    }
}
publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components.getByName("java"))
            groupId = project.group.toString()
            artifactId = rootProject.name
            version = project.version.toString()
        }
    }
}
