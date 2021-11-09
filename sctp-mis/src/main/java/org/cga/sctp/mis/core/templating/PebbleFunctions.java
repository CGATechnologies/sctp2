/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2021, CGATechnologies
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.cga.sctp.mis.core.templating;

import com.mitchellbosecke.pebble.extension.Function;
import com.mitchellbosecke.pebble.spring.extension.function.bindingresult.GetFieldErrorsFunction;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import org.cga.sctp.mis.core.templating.functions.*;
import org.cga.sctp.mis.utils.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.Map;

@Configuration
public class PebbleFunctions {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ApplicationContext appContext;

    @Bean
    Function printDate() {
        return new PrintDate();
    }

    @Bean
    public SelectOptionRegistry selectOptionRegistry() {
        return new SelectOptionRegistryImpl(appContext);
    }

    @Bean
    public Function hasAuthority() {
        return new HasAuthority();
    }

    @Bean
    public Function breadcrumb() {
        return new BreadcrumbFn();
    }

    @Bean
    public Function hasRole() {
        return new HasRole();
    }

    @Bean
    public Function optionsMenu() {
        return new OptionsMenu();
    }

    @Bean
    public Function printLogMessage() {
        return new PrintLogMessage();
    }

    @Bean
    public PebbleFunctionImpl now() {
        return new PebbleFunctionImpl("now") {
            @Override
            public Object execute(Map<String, Object> args, PebbleTemplate self, EvaluationContext context, int lineNumber) {
                return LocalDate.now();
            }
        };
    }

    @Bean
    public PebbleFunctionImpl age() {
        return new Age();
    }

    @Bean
    public Function inputText() {
        return new InputText();
    }

    @Bean
    public Function textArea() {
        return new TextArea();
    }

    @Bean
    public Function formSelect() {
        return new FormSelect(selectOptionRegistry());
    }

    @Bean
    public Function formMultiSelect() {
        return new FormSelect(selectOptionRegistry(), true);
    }

    @Bean
    public Function yesOrNo() {
        return new YesOrNo();
    }

    @Bean
    public Function isAuthenticated() {
        return new PebbleFunctionImpl("isAuthenticated") {
            @Override
            public Object execute(Map<String, Object> args, PebbleTemplate self, EvaluationContext context, int lineNumber) {
                return SpringUtils.isPrincipalAuthenticated();
            }
        };
    }

    @Bean
    public Function principal() {
        return new PebbleFunctionImpl("principal") {
            @Override
            public Object execute(Map<String, Object> args, PebbleTemplate self, EvaluationContext context, int lineNumber) {
                return SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            }
        };
    }

    @Bean
    public Function principalDetails() {
        return new PebbleFunctionImpl("principalDetails") {
            @Override
            public Object execute(Map<String, Object> args, PebbleTemplate self, EvaluationContext context, int lineNumber) {
                return SecurityContextHolder.getContext().getAuthentication().getDetails();
            }
        };
    }

    @Bean
    public Function showMessageBox() {
        return new ShowMessageBox();
    }

    @Bean
    Function getFieldErrorsEx() {
        return new GetFieldErrorsFunction(messageSource);
    }

    @Bean
    Function isEmpty() {
        return new IsEmpty();
    }

    @Bean
    public Function formCsrf() {
        return new FormCSRF();
    }
}
