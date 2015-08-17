package com.chargingsystem.server

import java.net.InetSocketAddress

import com.twitter.finagle.builder.ServerBuilder
import com.twitter.finagle.http.{Request, _}
import com.twitter.server.TwitterServer

object Server extends TwitterServer with Router with DataStore {

  def main() {
    HttpMuxer.addRichHandler("/", router)

    ServerBuilder()
      .codec(RichHttp[Request](Http.get().enableTracing(false)))
      .bindTo(new InetSocketAddress("0.0.0.0", 8088))
      .name("Charging-System API")
      .build(router)
  }
}

