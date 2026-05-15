package mihon.buildlogic

import org.gradle.api.Project
import java.io.ByteArrayOutputStream
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

// Git is needed in your system PATH for these commands to work.
// If it's not installed, you can return a random value as a workaround
fun Project.getCommitCount(): String {
    return runGitCommand("git rev-list --count HEAD") ?: "0"
}

fun Project.getGitSha(): String {
    return runGitCommand("git rev-parse --short HEAD") ?: "nogit"
}

private val BUILD_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")

/**
 * @param useLastCommitTime If `true`, the build time is based on the timestamp of the last Git commit;
 *                          otherwise, the current time is used. Both are in UTC.
 * @return A formatted string representing the build time. The format used is defined by [BUILD_TIME_FORMATTER].
 */
fun Project.getBuildTime(useLastCommitTime: Boolean): String {
    return if (useLastCommitTime) {
        val epoch = runGitCommand("git log -1 --format=%ct")?.toLongOrNull()
        if (epoch != null) {
            Instant.ofEpochSecond(epoch).atOffset(ZoneOffset.UTC).format(BUILD_TIME_FORMATTER)
        } else {
            LocalDateTime.now(ZoneOffset.UTC).format(BUILD_TIME_FORMATTER)
        }
    } else {
        LocalDateTime.now(ZoneOffset.UTC).format(BUILD_TIME_FORMATTER)
    }
}

private fun Project.runGitCommand(command: String): String? {
    if (!rootProject.file(".git").exists()) {
        return null
    }

    return try {
        val output = ByteArrayOutputStream()
        val result = exec {
            isIgnoreExitValue = true
            workingDir = rootProject.rootDir
            commandLine = command.split(" ")
            standardOutput = output
        }
        if (result.exitValue == 0) {
            output.toString().trim().ifBlank { null }
        } else {
            null
        }
    } catch (_: Exception) {
        null
    }
}
