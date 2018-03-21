package by.dev.madhead.jarvis.model

import java.time.Duration

data class Build(
        val number: Int,
        val branch: String? = null,
        val revision: String,
        val status: BuildStatus,
        val duration: Duration,
        val link: String,
        val changeSet: ChangeSet
) {
    val durationForHumans: String
        get() = when {
            duration.isNegative -> "Who let the Shrike out?"
            duration.toDays() > 7 -> "Took an eternity to finish!"
            else -> {
                var mutable = duration

                val days = if (mutable.toDays() > 1) "${mutable.toDays()} days" else if (mutable.toDays() == 1L) "1 day" else ""

                mutable = mutable.minusDays(mutable.toDays())

                val hours = if (mutable.toHours() > 1) "${mutable.toHours()} hours" else if (mutable.toHours() == 1L) "1 hour" else ""

                mutable = mutable.minusHours(mutable.toHours())

                val minutes = if (mutable.toMinutes() > 1) "${mutable.toMinutes()} minutes" else if (mutable.toMinutes() == 1L) "1 minute" else ""

                mutable = mutable.minusMinutes(mutable.toMinutes())

                val seconds = if (mutable.seconds > 1) "${mutable.seconds} seconds" else if (mutable.seconds == 1L) "1 second" else ""

                "${days} ${hours} ${minutes} ${seconds}".trim().let {
                    if (it.isBlank()) "0 seconds" else it
                }
            }
        }
}

enum class BuildStatus(
        val forHumans: String
) {
    PASSED("Passed"),
    FIXED("Fixed"),
    BROKEN("Broken"),
    STILL_BROKEN("Still broken"),
    FAILED("Failed"),
    STILL_FAILING("Still failing"),
    ABORTED("Aborted"),
    UNKNOWN("Unknown");
}
