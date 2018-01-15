package by.dev.madhead.jarvis.util;

import by.dev.madhead.jarvis.model.BuildStatus;
import hudson.model.Result;
import hudson.model.Run;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class BuildStatusQualifierTest {

    private Run run = mock(Run.class);

    private Run previousRun = mock(Run.class);

    private Result currentResult;
    private Result previousResult;
    private BuildStatus buildStatus;

    public BuildStatusQualifierTest(Result currentResult, Result previousResult, BuildStatus buildStatus) {
        this.currentResult = currentResult;
        this.previousResult = previousResult;
        this.buildStatus = buildStatus;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {Result.SUCCESS, null, BuildStatus.PASSED},
                {Result.SUCCESS, Result.SUCCESS, BuildStatus.PASSED},
                {Result.SUCCESS, Result.FAILURE, BuildStatus.FIXED},
                {Result.SUCCESS, Result.NOT_BUILT, BuildStatus.FIXED},
                {Result.UNSTABLE, null, BuildStatus.BROKEN},
                {Result.NOT_BUILT, null, BuildStatus.BROKEN},
                {Result.UNSTABLE, Result.SUCCESS, BuildStatus.BROKEN},
                {Result.NOT_BUILT, Result.SUCCESS, BuildStatus.BROKEN},
                {Result.UNSTABLE, Result.UNSTABLE, BuildStatus.STILL_BROKEN},
                {Result.UNSTABLE, Result.NOT_BUILT, BuildStatus.STILL_BROKEN},
                {Result.NOT_BUILT, Result.UNSTABLE, BuildStatus.STILL_BROKEN},
                {Result.NOT_BUILT, Result.NOT_BUILT, BuildStatus.STILL_BROKEN},
                {Result.FAILURE, null, BuildStatus.FAILED},
                {Result.FAILURE, Result.NOT_BUILT, BuildStatus.FAILED},
                {Result.FAILURE, Result.SUCCESS, BuildStatus.FAILED},
                {Result.FAILURE, Result.FAILURE, BuildStatus.STILL_FAILING},
                {Result.ABORTED, Result.FAILURE, BuildStatus.ABORTED},
                {Result.ABORTED, null, BuildStatus.ABORTED}
        });
    }

    @Before
    public void setUp() {
        when(run.getResult()).thenReturn(currentResult);

        if (previousResult != null) {
            when(run.getPreviousBuild()).thenReturn(previousRun);
            when(previousRun.getResult()).thenReturn(previousResult);
        } else {
            when(run.getPreviousBuild()).thenReturn(null);
        }
    }

    @Test
    public void defineBuildResult() {
        Assert.assertEquals(buildStatus, BuildStatusQualifier.defineBuildStatus(run));
    }

}