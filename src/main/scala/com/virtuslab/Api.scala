package com.virtuslab

import ch.epfl.scala.bsp4j.BuildClient
import ch.epfl.scala.bsp4j.BuildServer
import ch.epfl.scala.bsp4j.ScalaBuildServer

trait BspServer extends BuildServer with ScalaBuildServer

trait BspClient extends BuildClient
