package unit.com.newmotion.core

import com.twitter.finagle.http.Request
import org.jboss.netty.handler.codec.http.HttpMethod

trait RequestHelper {
  def buildRequest(content: String = "", method: HttpMethod = HttpMethod.GET) = {
    val req = Request()
    req.setContentTypeJson()
    req.setContentString(content)
    req.setMethod(method)

    req
  }
}
