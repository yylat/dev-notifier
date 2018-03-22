package by.dev.madhead.jarvis.creator

import hudson.FilePath
import hudson.model.AbstractBuild
import hudson.model.BuildListener
import hudson.model.TaskListener
import org.jenkinsci.plugins.workflow.job.WorkflowRun

class EmailCreatorFactory {

    fun getCreator(run: AbstractBuild<*, *>, listener: BuildListener)
            : EmailCreator = ClassicEmailCreator(run, listener)

    fun getCreator(run: WorkflowRun, listener: TaskListener, workspace: FilePath)
            : EmailCreator = PipelineEmailCreator(run, listener, workspace)

}
