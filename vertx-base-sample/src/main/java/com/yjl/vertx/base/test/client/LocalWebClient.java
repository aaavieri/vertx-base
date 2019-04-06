package com.yjl.vertx.base.test.client;

import com.yjl.vertx.base.com.anno.Param;
import com.yjl.vertx.base.webclient.anno.component.RequestClient;
import com.yjl.vertx.base.webclient.anno.request.Request;
import com.yjl.vertx.base.webclient.anno.request.RequestData;
import com.yjl.vertx.base.webclient.enumeration.RequestDataType;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;


@RequestClient(port = 4103, host = "localhost")
public interface LocalWebClient {

	@Request(path = "test/1", method = HttpMethod.POST)
	@RequestData(type = RequestDataType.QUERY_PARAM, key = "a")
	@RequestData(type = RequestDataType.FORM_PARAM, key = "b")
	@RequestData(type = RequestDataType.FORM_PARAM, key = "d")
	Future<JsonObject> testFirst(@Param("a")String a, @Param("b")String b, @Param("d")int c);
}
