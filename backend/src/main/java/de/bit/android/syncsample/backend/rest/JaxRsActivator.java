package de.bit.android.syncsample.backend.rest;

import static java.util.Arrays.*;
import static org.codehaus.jackson.annotate.JsonAutoDetect.Visibility.*;
import static org.codehaus.jackson.map.SerializationConfig.Feature.*;
import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.introspect.JacksonAnnotationIntrospector;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;

@ApplicationPath("/rest")
public class JaxRsActivator extends Application {

	public Set<java.lang.Class<?>> getClasses() {
		return new HashSet<Class<?>>(asList(TodoResource.class));
	};

	@Override
	public Set<Object> getSingletons() {
		JacksonJaxbJsonProvider jsonProvider = createJsonProvider();
		return Collections.singleton((Object) jsonProvider);
	}

	private JacksonJaxbJsonProvider createJsonProvider() {

		ObjectMapper objectMapper = new ObjectMapper();

		AnnotationIntrospector annotationIntrospector = new AnnotationIntrospector.Pair(
				new JaxbAnnotationIntrospector(),
				new JacksonAnnotationIntrospector());
		objectMapper.setAnnotationIntrospector(annotationIntrospector);
		objectMapper.getSerializationConfig().setSerializationInclusion(
				NON_NULL);
		objectMapper.setVisibilityChecker(objectMapper.getVisibilityChecker()
				.withFieldVisibility(ANY));

		objectMapper.configure(AUTO_DETECT_FIELDS, true);
		objectMapper.configure(AUTO_DETECT_GETTERS, false);
		objectMapper.configure(AUTO_DETECT_IS_GETTERS, false);

		JacksonJaxbJsonProvider jsonProvider = new JacksonJaxbJsonProvider(
				objectMapper, null);
		return jsonProvider;
	}

}
