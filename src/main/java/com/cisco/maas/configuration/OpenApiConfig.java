/*
 *  AppDynamics Onboarding APIs.
 *
 *  Copyright 2022 Cisco
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.cisco.maas.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * This class generates openapi specs from the existing code. 
  */

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"org.springdoc"})
@Import({org.springdoc.core.SpringDocConfiguration.class,
         org.springdoc.webmvc.core.SpringDocWebMvcConfiguration.class,
         org.springdoc.webmvc.ui.SwaggerConfig.class,
         org.springdoc.core.SwaggerUiConfigProperties.class,
         org.springdoc.core.SwaggerUiOAuthProperties.class,
         org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration.class})
@OpenAPIDefinition(info = @Info(title = "Service Assurance AppDynamics Onboarding API Documentation", version = "1.0", description = "APIs to automate the onboarding of applications into AppDynamics Controller", contact = @Contact(
        name = "Service Assurance AppDynamics Onboarding API Team",
        email = "TBD",
        url = "http://"
    ), license = @License(name = "TBD",url="http://")), tags = {@Tag(name = "Service Assurance AppDynamics Onboarding API", description = "Service Assurance AppDynamics Onboarding API")})
public class OpenApiConfig implements WebMvcConfigurer {
}