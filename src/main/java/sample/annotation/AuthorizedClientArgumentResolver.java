/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sample.annotation;

import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.util.Assert;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.annotation.Annotation;

/**
 * @author Joe Grandja
 */
public class AuthorizedClientArgumentResolver implements HandlerMethodArgumentResolver  {
	private OAuth2AuthorizedClientService authorizedClientService;

	public AuthorizedClientArgumentResolver(OAuth2AuthorizedClientService authorizedClientService) {
		Assert.notNull(authorizedClientService, "authorizedClientService cannot be null");
		this.authorizedClientService = authorizedClientService;
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return this.findMethodAnnotation(AuthorizedClient.class, parameter) != null;
	}

	@Nullable
	@Override
	public Object resolveArgument(MethodParameter parameter,
								  @Nullable ModelAndViewContainer mavContainer,
								  NativeWebRequest webRequest,
								  @Nullable WebDataBinderFactory binderFactory) throws Exception {

		Authentication authentication =
				SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			return null;
		}

		AuthorizedClient authorizedClientAnnotation =
				this.findMethodAnnotation(AuthorizedClient.class, parameter);

		String clientRegistrationId = authorizedClientAnnotation.clientRegistrationId();

		OAuth2AuthorizedClient authorizedClient =
				this.authorizedClientService.loadAuthorizedClient(
						clientRegistrationId,
						authentication.getName());

		return authorizedClient;
	}

	private <T extends Annotation> T findMethodAnnotation(Class<T> annotationClass,
														  MethodParameter parameter) {
		T annotation = parameter.getParameterAnnotation(annotationClass);
		if (annotation != null) {
			return annotation;
		}
		Annotation[] annotationsToSearch = parameter.getParameterAnnotations();
		for (Annotation toSearch : annotationsToSearch) {
			annotation = AnnotationUtils.findAnnotation(toSearch.annotationType(),
					annotationClass);
			if (annotation != null) {
				return annotation;
			}
		}
		return null;
	}
}
