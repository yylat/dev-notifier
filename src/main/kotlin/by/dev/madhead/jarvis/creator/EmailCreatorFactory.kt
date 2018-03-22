package by.dev.madhead.jarvis.creator

import hudson.FilePath
import hudson.model.AbstractBuild
import hudson.model.BuildListener
import hudson.model.TaskListener
import org.jenkinsci.plugins.workflow.job.WorkflowRun

fun EmailCreator(run: AbstractBuild<*, *>, listener: BuildListener)
        : EmailCreator = ClassicEmailCreator(run, listener)


fun EmailCreator(run: WorkflowRun, listener: TaskListener, workspace: FilePath)
        : EmailCreator = PipelineEmailCreator(run, listener, workspace)
