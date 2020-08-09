import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	`maven-publish`

	id("org.springframework.boot") version "2.3.2.RELEASE"
	id("io.spring.dependency-management") version "1.0.9.RELEASE"
	kotlin("jvm") version "1.3.72"
	kotlin("plugin.spring") version "1.3.72"
}

group = "nl.lengrand"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
	jcenter()
	maven {
		name = "GitHubPackages"
		url = uri("https://maven.pkg.github.com/jlengrand/assistant-conversation-java")
		credentials {
			username = System.getenv("GH_NAME")
			password = System.getenv("GH_TOKEN")
		}
	}
}

dependencies {


	implementation("assistant.conversation.schema:assistant-conversation-java:1.0")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.github.kittinunf.fuel:fuel:2.2.3")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	developmentOnly("org.springframework.boot:spring-boot-devtools")
	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}
