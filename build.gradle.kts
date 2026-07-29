plugins {
	`java-library`
	`maven-publish`
}

group = "ollie.utils"
version = "1.74"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
	withSourcesJar()
}

repositories {
	mavenCentral()
	// Personal artefacts (serialiser) - no checkout needed, consumed over raw HTTP.
	maven {
		url = uri("https://raw.githubusercontent.com/OllieMilton/maven-repo/main")
	}
}

dependencies {
	// log4j types appear in the LogEntryAppender public API.
	api("org.apache.logging.log4j:log4j-api:2.24.3")
	api("org.apache.logging.log4j:log4j-core:2.24.3")
	// The @Serialisable annotation on LogEntry is part of the public API.
	api("serialiser:serialiser:1.29")
	implementation("commons-logging:commons-logging:1.2")
	implementation("commons-lang:commons-lang:2.4")

	testImplementation("junit:junit:4.13.2")
	testImplementation("org.apache.logging.log4j:log4j-jcl:2.24.3")
}

tasks.test {
	useJUnit()
	testLogging {
		events("failed", "skipped")
	}
}

publishing {
	publications {
		create<MavenPublication>("maven") {
			from(components["java"])
		}
	}
	repositories {
		// Publishing writes into a sibling checkout of
		// github.com/OllieMilton/maven-repo - commit and push it afterwards.
		maven {
			name = "gitMavenRepo"
			url = uri(layout.projectDirectory.dir("../maven-repo"))
		}
	}
}
