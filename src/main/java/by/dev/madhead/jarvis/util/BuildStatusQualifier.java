package by.dev.madhead.jarvis.util;

import by.dev.madhead.jarvis.model.BuildStatus;
import hudson.model.Result;
import hudson.model.Run;

public class BuildStatusQualifier {

    private BuildStatusQualifier() {
    }

    public static BuildStatus defineBuildStatus(Run<?, ?> run) {
        Result currentResult = run.getResult();
        Result previousResult = run.getPreviousBuild() != null ? run.getPreviousBuild().getResult() : null;

        if (Result.SUCCESS.equals(currentResult)
                && (previousResult == null || Result.SUCCESS.equals(previousResult))) {
            return BuildStatus.PASSED;
        }

        if (Result.SUCCESS.equals(currentResult)
                && (previousResult != null && !Result.SUCCESS.equals(previousResult))) {
            return BuildStatus.FIXED;
        }

        if ((Result.UNSTABLE.equals(currentResult) || Result.NOT_BUILT.equals(currentResult))
                && (previousResult == null || Result.SUCCESS.equals(previousResult))) {
            return BuildStatus.BROKEN;
        }

        if ((Result.UNSTABLE.equals(currentResult) || Result.NOT_BUILT.equals(currentResult))
                && (previousResult != null
                && (Result.UNSTABLE.equals(previousResult) || Result.NOT_BUILT.equals(previousResult)))) {
            return BuildStatus.STILL_BROKEN;
        }

        if (Result.FAILURE.equals(currentResult)
                && (previousResult == null || !Result.FAILURE.equals(previousResult))) {
            return BuildStatus.FAILED;
        }

        if (Result.FAILURE.equals(currentResult)
                && (previousResult != null && Result.FAILURE.equals(previousResult))) {
            return BuildStatus.STILL_FAILING;
        }

        if (Result.ABORTED.equals(currentResult)) {
            return BuildStatus.ABORTED;
        }

        return BuildStatus.UNKNOWN;
    }

}