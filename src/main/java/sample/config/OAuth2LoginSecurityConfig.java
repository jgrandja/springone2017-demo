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
package sample.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

/**
 * @author Joe Grandja
 */
@EnableWebSecurity
public class OAuth2LoginSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private ClientRegistrationRepository clientRegistrationRepository;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.authorizeRequests()
					.mvcMatchers("/create-message").hasAuthority("CREATE_MESSAGE")
					.mvcMatchers("/read-message").hasAuthority("READ_MESSAGE")
					.mvcMatchers("/update-message").hasAuthority("UPDATE_MESSAGE")
					.mvcMatchers("/delete-message").hasAuthority("DELETE_MESSAGE")
					.mvcMatchers("/manage-message").hasAuthority("MANAGE_MESSAGE")
					.anyRequest().authenticated()
					.and()
				.oauth2Login()
					.userInfoEndpoint()
						.userAuthoritiesMapper(this.oktaUserAuthoritiesMapper());
	}

	@Bean
	public OAuth2AuthorizedClientService authorizedClientService() {
		return new InMemoryOAuth2AuthorizedClientService(this.clientRegistrationRepository);
	}

	private GrantedAuthoritiesMapper oktaUserAuthoritiesMapper() {
		String issuerClaim = "https://dev-829719.oktapreview.com";
		String authoritiesClaimName = "authorities";

		return (authorities) ->
				authorities.stream()
						.filter(OidcUserAuthority.class::isInstance)
						.map(OidcUserAuthority.class::cast)
						.filter(userAuthority -> userAuthority.getIdToken().getIssuer().toString().equals(issuerClaim))
						.filter(userAuthority -> userAuthority.getUserInfo().containsClaim(authoritiesClaimName))
						.map(userAuthority -> userAuthority.getUserInfo().getClaimAsStringList(authoritiesClaimName))
						.flatMap(Collection::stream)
						.map(SimpleGrantedAuthority::new)
						.collect(Collectors.toCollection(LinkedHashSet::new));
	}
}
