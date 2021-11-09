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

package org.cga.sctp.mis.core.templating.functions;

import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import org.apache.logging.log4j.util.Strings;
import org.cga.sctp.mis.core.navigation.BreadCrumbChain;
import org.cga.sctp.mis.core.navigation.Breadcrumb;
import org.cga.sctp.mis.core.navigation.BreadcrumbHandlerInterceptor;
import org.cga.sctp.mis.core.templating.PebbleFunctionImpl;
import org.cga.sctp.mis.utils.HtmlElement;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public class BreadcrumbFn extends PebbleFunctionImpl {
    public BreadcrumbFn() {
        super("breadcrumbs", List.of("request", "useWideContainer"));
    }

    @Override
    public Object execute(Map<String, Object> args, PebbleTemplate self, EvaluationContext context, int lineNumber) {
        HttpServletRequest request = getArgument(args, "request", null);
        boolean useWideContainer = getArgument(args, "useWideContainer", false);

        HtmlElement container, nav, lastAnchor, lastListItem;
        BreadCrumbChain breadCrumbChain = BreadcrumbHandlerInterceptor.getBreadcrumbs(request);

        if (breadCrumbChain == null || breadCrumbChain.getBreadcrumbs().isEmpty()) {
            return null;
        }

        lastAnchor = lastListItem = null;

        /*<nav class="breadcrumb is-small" aria-label="breadcrumbs">
          <ul>
            <li><a href="#">Bulma</a></li>
            <li><a href="#">Documentation</a></li>
            <li><a href="#">Components</a></li>
            <li class="is-active"><a href="#" aria-current="page">Breadcrumb</a></li>
          </ul>
        </nav>*/
        // breadcrumb is-medium" aria-label="breadcrumbs"
        container = new HtmlElement("div")
                .attribute("style", "margin-top: 1rem")
                .addChild(
                        new HtmlElement("nav")
                                .addClasses("breadcrumb"/*, "is-small"*/)
                                .attribute("aria-label", "breadcrumbs")
                                .addChild(nav = new HtmlElement("ul"))
                );
        if (!useWideContainer) {
            container.addClass("container");
        }
        List<Breadcrumb> breadcrumbs = breadCrumbChain.getBreadcrumbs();
        StringBuilder linkBuilder = new StringBuilder();
        for (Breadcrumb breadcrumb : breadcrumbs) {
            HtmlElement anchor = lastAnchor = new HtmlElement("a")
                    .text(breadcrumb.title());
            if (!Strings.isBlank(breadcrumb.link())) {
                anchor.attribute("href", linkBuilder.append(breadcrumb.link()));
            }
            nav.addChild(lastListItem = new HtmlElement("li").addChild(anchor));
            if (!breadcrumb.active()) {
                lastAnchor.attribute("aria-current", "page");
                lastListItem.classes("is-active");
            }
        }

        return container.build();
    }
}
