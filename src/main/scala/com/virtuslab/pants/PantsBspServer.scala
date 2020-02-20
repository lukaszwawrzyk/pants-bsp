package com.virtuslab.pants

import java.util.concurrent.CompletableFuture

import ch.epfl.scala.bsp4j.WorkspaceBuildTargetsResult
import com.virtuslab.pants.bsp.ForwardingBspServer

class PantsBspServer extends ForwardingBspServer {

  override def workspaceBuildTargets(): CompletableFuture[WorkspaceBuildTargetsResult] = {
    FileWatcher.ensureBloopUpToDate()
    super.workspaceBuildTargets()
  }

}
