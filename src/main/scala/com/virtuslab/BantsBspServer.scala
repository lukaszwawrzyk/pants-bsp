package com.virtuslab

import java.util.concurrent.Executors

import org.eclipse.lsp4j.jsonrpc.Launcher

object Bloop {
  private val bloopCmd = Seq(
    "/home/lukasz/dev/intellij-bsp/duplicate-b/duplicate-b/.bsp/coursier",
    "launch",
    "ch.epfl.scala:bloop-launcher-core_2.12:1.4.0-RC1",
    "--",
    "1.4.0-RC1"
  )

  def startBloopServer(): (ForwardingBspClient, BspServer) = {
    val pb = new ProcessBuilder(bloopCmd: _*).start()

    val client = new ForwardingBspClient

    val launcher = new Launcher.Builder[BspServer]()
      .setRemoteInterface(classOf[BspServer])
      .setLocalService(client)
      .setExecutorService(Executors.newCachedThreadPool())
      .setInput(pb.getInputStream)
      .setOutput(pb.getOutputStream)
      .create()

    val proxy = launcher.getRemoteProxy

    launcher.startListening()
    (client, proxy)
  }
}

object PantsBsp {

  def startPantsServer(): (ForwardingBspServer, BspClient) = {
    val server = new ForwardingBspServer

    val launcher = new Launcher.Builder[BspClient]()
      .setRemoteInterface(classOf[BspClient])
      .setLocalService(server)
      .setExecutorService(Executors.newCachedThreadPool())
      .setOutput(System.out)
      .setInput(System.in)
      .create()

    val proxy = launcher.getRemoteProxy

    launcher.startListening()

    (server, proxy)
  }
}

object Main {
  def main(args: Array[String]): Unit = {
    println("Starting server")
    val (forwardingClient, bloopServer) = Bloop.startBloopServer()
    val (forwardingServer, pantsClient) = PantsBsp.startPantsServer()
    forwardingClient.target = pantsClient
    forwardingServer.target = bloopServer
    Thread.sleep(Long.MaxValue)

    /*
    val home = Paths.get(".pants-bsp").toAbsolutePath
    Files.createDirectories(home)
    val log = home.resolve("pants-bsp.log")
    val trace = home.resolve("pants-bsp.trace.json")
    val logStream = new PrintStream(
      Files.newOutputStream(
        log,
        StandardOpenOption.CREATE,
        StandardOpenOption.TRUNCATE_EXISTING
      )
    )
    System.setOut(logStream)
    System.setErr(logStream)

    val executor = Executors.newCachedThreadPool()

    val server = new ForwardingBspServer(Bloop.startBloopServer())

    val traceWrites = new PrintWriter(
      Files.newOutputStream(
        trace,
        StandardOpenOption.CREATE,
        StandardOpenOption.TRUNCATE_EXISTING
      )
    )

    val launcher = new Launcher.Builder[BuildClient]()
      .traceMessages(traceWrites)
      .setOutput(stdout)
      .setInput(stdin)
      .setLocalService(server)
      .setRemoteInterface(classOf[BuildClient])
      .setExecutorService(executor)
      .create()

    server.client = launcher.getRemoteProxy
    val listening = launcher.startListening()
    listening.get()
   */
  }

}
