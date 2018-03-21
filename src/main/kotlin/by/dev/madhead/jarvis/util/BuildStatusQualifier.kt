package by.dev.madhead.jarvis.util

import by.dev.madhead.jarvis.model.BuildStatus
import hudson.model.Result
import hudson.model.Run

class BuildStatusQualifier {

    fun defineBuildStatus(run: Run<*, *>): BuildStatus {
        val currentResult = run.getResult()
        val previousResult = run.getPreviousBuild()?.getResult()

        return when (currentResult) {
            Result.SUCCESS ->
                when (previousResult) {
                    null, Result.SUCCESS -> BuildStatus.PASSED
                    else -> BuildStatus.FIXED
                }
            Result.UNSTABLE, Result.NOT_BUILT ->
                when (previousResult) {
                    null, Result.SUCCESS -> BuildStatus.BROKEN
                    Result.UNSTABLE, Result.NOT_BUILT -> BuildStatus.STILL_BROKEN
                    else -> BuildStatus.UNKNOWN
                }
            Result.FAILURE ->
                when (previousResult) {
                    Result.FAILURE -> BuildStatus.STILL_FAILING
                    else -> BuildStatus.FAILED
                }
            Result.ABORTED -> BuildStatus.ABORTED
            else -> BuildStatus.UNKNOWN
        }
    }

}
