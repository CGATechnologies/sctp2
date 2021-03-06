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

package org.cga.sctp.mis.core.navigation;

import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BreadcrumbHandlerInterceptor implements HandlerInterceptor {
    private static final String BREADCRUMB_KEY = "breadcrumb_chain";
    private static final String PARENT_BREADCRUMB_KEY = "parent_breadcrumb_chain";
    private static final Breadcrumb homeNode = new Breadcrumb("/", "Home", true);

    static BreadCrumbChain getBeforeBreadcrumbChain(HttpServletRequest request) {
        return getParentBreadcrumbChain(request);
    }

    static void addBeforeBreadcrumbChain(HttpServletRequest request, BreadCrumbChain breadCrumbChain) {
        BreadCrumbChain breadcrumbs = (BreadCrumbChain) request.getAttribute(PARENT_BREADCRUMB_KEY);
        if (breadcrumbs == null) {
            request.setAttribute(PARENT_BREADCRUMB_KEY, breadCrumbChain);
        }
    }

    private BreadcrumbDefinition getControllerAnnotation(HandlerMethod method) {
        return method.getMethod().getDeclaringClass().getAnnotation(BreadcrumbDefinition.class);
    }

    private boolean hasBreadcrumb(Object handler) {
        if (handler instanceof HandlerMethod) {
            return getControllerAnnotation((HandlerMethod) handler) != null;
        }
        return false;
    }

    private BreadCrumbChain getBreadcrumbChain(HttpServletRequest request) {
        BreadCrumbChain breadCrumbChain = (BreadCrumbChain) request.getAttribute(BREADCRUMB_KEY);
        if (breadCrumbChain == null) {
            request.setAttribute(BREADCRUMB_KEY, breadCrumbChain = new BreadCrumbChain().add(homeNode));
        }
        return breadCrumbChain;
    }

    private static BreadCrumbChain getParentBreadcrumbChain(HttpServletRequest request) {
        return (BreadCrumbChain) request.getAttribute(PARENT_BREADCRUMB_KEY);
    }

    public static BreadCrumbChain getBreadcrumbs(HttpServletRequest request) {
        return (BreadCrumbChain) request.getAttribute(BREADCRUMB_KEY);
    }

    @Override
    public void postHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, ModelAndView modelAndView) throws Exception {
        if (hasBreadcrumb(handler)) {
            HandlerMethod method = (HandlerMethod) handler;
            BreadcrumbDefinition root = getControllerAnnotation(method);
            BreadcrumbPath path = method.getMethodAnnotation(BreadcrumbPath.class);
            BreadCrumbChain breadCrumbChain = getBreadcrumbChain(request)
                    .add(new Breadcrumb(root.module()))
                    .add(new Breadcrumb(root.index()));
            if (path != null) {
                if (path.bindings().length != 0 && StringUtils.hasText(path.link())) {
                    String link = path.link();
                    // noinspection unchecked
                    Map<String, String> urlParameters = (Map<String, String>) request
                            .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

                    for (VarBinding binding : path.bindings()) {
                        link = link.replace(binding.variable(), urlParameters.get(binding.lookupKey()));
                    }
                    breadCrumbChain.add(new Breadcrumb(link, path.title(), path.navigable()));
                } else {
                    breadCrumbChain.add(new Breadcrumb(path));
                }
            }
            if (modelAndView != null) {
                modelAndView.addObject("navigation_breadcrumbs", breadCrumbChain);
            }
        }
    }

    private static final Pattern variableMatcher = Pattern.compile("(\\{([a-zA-Z0-9\\-]{1,50})})");

    private Map<String, String> parameters(String path) {
        if (!StringUtils.hasText(path)) {
            return Map.of();
        }
        Map<String, String> map = new LinkedHashMap<>();
        try {
            Matcher matcher = variableMatcher.matcher(path);
            while (matcher.matches()) {
                String region = matcher.group(1);
                String name = matcher.group(2);
                map.put(name, region);
            }
        } catch (Exception ignore) {

        }
        return map;
    }
}
