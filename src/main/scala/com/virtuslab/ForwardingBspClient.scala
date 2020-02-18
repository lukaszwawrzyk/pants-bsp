package com.virtuslab

import ch.epfl.scala.bsp4j.DidChangeBuildTarget
import ch.epfl.scala.bsp4j.LogMessageParams
import ch.epfl.scala.bsp4j.PublishDiagnosticsParams
import ch.epfl.scala.bsp4j.ShowMessageParams
import ch.epfl.scala.bsp4j.TaskFinishParams
import ch.epfl.scala.bsp4j.TaskProgressParams
import ch.epfl.scala.bsp4j.TaskStartParams

class ForwardingBspClient extends BspClient {

  var target: BspClient = _

  override def onBuildShowMessage(params: ShowMessageParams): Unit =
    target.onBuildShowMessage(params)

  override def onBuildLogMessage(params: LogMessageParams): Unit =
    target.onBuildLogMessage(params)

  override def onBuildTaskStart(params: TaskStartParams): Unit =
    target.onBuildTaskStart(params)

  override def onBuildTaskProgress(params: TaskProgressParams): Unit =
    target.onBuildTaskProgress(params)

  override def onBuildTaskFinish(params: TaskFinishParams): Unit =
    target.onBuildTaskFinish(params)

  override def onBuildPublishDiagnostics(params: PublishDiagnosticsParams): Unit =
    target.onBuildPublishDiagnostics(params)

  override def onBuildTargetDidChange(params: DidChangeBuildTarget): Unit =
    target.onBuildTargetDidChange(params)
}
