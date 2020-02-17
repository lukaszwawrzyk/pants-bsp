package com.virtuslab

import java.util.concurrent.CompletableFuture

import ch.epfl.scala.bsp4j
import ch.epfl.scala.bsp4j.BuildClient

class ForwardingBspServer extends BspServer {

  var target: BspServer = _

  override def onConnectWithClient(client: BuildClient): Unit =
    target.onConnectWithClient(client)

  override def buildInitialize(
      params: bsp4j.InitializeBuildParams
  ): CompletableFuture[bsp4j.InitializeBuildResult] = {
    target.buildInitialize(params)
  }

  override def onBuildInitialized(): Unit = {
    target.onBuildInitialized()
  }

  override def buildShutdown(): CompletableFuture[AnyRef] =
    target.buildShutdown()

  override def onBuildExit(): Unit = target.onBuildExit()

  override def workspaceBuildTargets(): CompletableFuture[bsp4j.WorkspaceBuildTargetsResult] =
    target.workspaceBuildTargets()

  override def buildTargetSources(
      params: bsp4j.SourcesParams
  ): CompletableFuture[bsp4j.SourcesResult] = target.buildTargetSources(params)

  override def buildTargetInverseSources(
      params: bsp4j.InverseSourcesParams
  ): CompletableFuture[bsp4j.InverseSourcesResult] =
    target.buildTargetInverseSources(params)

  override def buildTargetDependencySources(
      params: bsp4j.DependencySourcesParams
  ): CompletableFuture[bsp4j.DependencySourcesResult] =
    target.buildTargetDependencySources(params)

  override def buildTargetResources(
      params: bsp4j.ResourcesParams
  ): CompletableFuture[bsp4j.ResourcesResult] =
    target.buildTargetResources(params)

  override def buildTargetCompile(
      params: bsp4j.CompileParams
  ): CompletableFuture[bsp4j.CompileResult] = target.buildTargetCompile(params)

  override def buildTargetTest(
      params: bsp4j.TestParams
  ): CompletableFuture[bsp4j.TestResult] = target.buildTargetTest(params)

  override def buildTargetRun(
      params: bsp4j.RunParams
  ): CompletableFuture[bsp4j.RunResult] = target.buildTargetRun(params)

  override def buildTargetCleanCache(
      params: bsp4j.CleanCacheParams
  ): CompletableFuture[bsp4j.CleanCacheResult] =
    target.buildTargetCleanCache(params)

  override def buildTargetScalacOptions(
      params: bsp4j.ScalacOptionsParams
  ): CompletableFuture[bsp4j.ScalacOptionsResult] =
    target.buildTargetScalacOptions(params)

  override def buildTargetScalaTestClasses(
      params: bsp4j.ScalaTestClassesParams
  ): CompletableFuture[bsp4j.ScalaTestClassesResult] =
    target.buildTargetScalaTestClasses(params)

  override def buildTargetScalaMainClasses(
      params: bsp4j.ScalaMainClassesParams
  ): CompletableFuture[bsp4j.ScalaMainClassesResult] =
    target.buildTargetScalaMainClasses(params)
}
