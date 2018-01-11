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
                {Result.SUCCESS, Result.FAILURE, BuildStatus.FIXED}
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