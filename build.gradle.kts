plugins {
    java
    `maven-publish`
    id ("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "top.mrxiaom.miao"
version = "1.0.2"
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
    compileOnly("io.izzel.taboolib:bukkit-util:6.2.3") // TrMenu

    implementation("org.jetbrains:annotations:21.0.0")
    implementation("top.mrxiaom:PluginBase:1.3.1")
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
            "top.mrxiaom.pluginbase" to "base",
        ).forEach { (original, target) ->
            relocate(original, "$shadowGroup.$target")
        }
        // TODO: 将 TrMenu 支持移到单独模块，TabooLib 对于外部插件对接太不友好了
        relocate("taboolib.platform.util", "me.arasple.mc.trmenu.taboolib.platform.util")

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
