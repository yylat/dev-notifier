package by.dev.madhead.jarvis.creator

import by.dev.madhead.jarvis.model.BuildStatus
import hudson.model.Result
import hudson.model.Run
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.mockito.Mockito

@RunWith(Parameterized::class)
class DefineBuildStatusTest(
        private val currentResult: Result,
        private val previousResult: Result?,
        private val buildStatus: BuildStatus) {

    private val run = Mockito.mock(Run::class.java)
    private val previousRun = Mockito.mock(Run::class.java)

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data() = listOf(
                arrayOf(Result.SUCCESS, null, BuildStatus.PASSED),
                arrayOf(Result.SUCCESS, Result.SUCCESS, BuildStatus.PASSED),
                arrayOf(Result.SUCCESS, Result.FAILURE, BuildStatus.FIXED),
                arrayOf(Result.SUCCESS, Result.NOT_BUILT, BuildStatus.FIXED),
                arrayOf(Result.UNSTABLE, null, BuildStatus.BROKEN),
                arrayOf(Result.NOT_BUILT, null, BuildStatus.BROKEN),
                arrayOf(Result.UNSTABLE, Result.SUCCESS, BuildStatus.BROKEN),
                arrayOf(Result.NOT_BUILT, Result.SUCCESS, BuildStatus.BROKEN),
                arrayOf(Result.UNSTABLE, Result.UNSTABLE, BuildStatus.STILL_BROKEN),
                arrayOf(Result.UNSTABLE, Result.NOT_BUILT, BuildStatus.STILL_BROKEN),
                arrayOf(Result.NOT_BUILT, Result.UNSTABLE, BuildStatus.STILL_BROKEN),
                arrayOf(Result.NOT_BUILT, Result.NOT_BUILT, BuildStatus.STILL_BROKEN),
                arrayOf(Result.FAILURE, null, BuildStatus.FAILED),
                arrayOf(Result.FAILURE, Result.NOT_BUILT, BuildStatus.FAILED),
                arrayOf(Result.FAILURE, Result.SUCCESS, BuildStatus.FAILED),
                arrayOf(Result.FAILURE, Result.FAILURE, BuildStatus.STILL_FAILING),
                arrayOf(Result.ABORTED, Result.FAILURE, BuildStatus.ABORTED),
                arrayOf(Result.ABORTED, null, BuildStatus.ABORTED)
        )
    }

    @Before
    fun setUp() {
        Mockito.`when`(run.getResult()).thenReturn(currentResult)
        Mockito.`when`(run.getPreviousBuild()).thenReturn(previousRun)
        Mockito.`when`(previousRun.getResult()).thenReturn(previousResult)
    }

    @Test
    fun defineBuildResult() {
        Assert.assertEquals(buildStatus, defineBuildStatus(run))
    }

}