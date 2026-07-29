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
		// Published straight to the git backed maven-repo: the repository is
		// cloned into the build directory, published into, committed and
		// pushed - all handled by ./gradlew publish.
		maven {
			name = "gitMavenRepo"
			url = uri(layout.buildDirectory.dir("maven-repo"))
		}
	}
}

val mavenRepoGitUri = providers.gradleProperty("mavenRepoGitUri")
	.getOrElse("git@github.com:OllieMilton/maven-repo.git")
val mavenRepoDir = layout.buildDirectory.dir("maven-repo").get().asFile

val cloneMavenRepo = tasks.register<Exec>("cloneMavenRepo") {
	description = "Clones the git backed maven repository ready for publishing."
	doFirst { mavenRepoDir.deleteRecursively() }
	commandLine("git", "clone", "--depth", "1", mavenRepoGitUri, mavenRepoDir.absolutePath)
}

val pushMavenRepo = tasks.register<Exec>("pushMavenRepo") {
	description = "Commits and pushes newly published artefacts to the git backed maven repository."
	workingDir = mavenRepoDir
	commandLine("bash", "-c",
		"git add -A && (git diff --cached --quiet || (git commit -m 'Published ${project.group}:${project.name}:${project.version}' && git push))")
}

tasks.withType<PublishToMavenRepository>().configureEach {
	dependsOn(cloneMavenRepo)
	finalizedBy(pushMavenRepo)
}
