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
import org.cga.sctp.mis.core.templating.*;
import org.cga.sctp.mis.utils.HtmlElement;
import org.cga.sctp.mis.utils.ReflectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FormSelect extends PebbleFunctionImpl {

    private final SelectOptionRegistry selectOptionRegistry;
    private final boolean multiSelect;
    private int level;

    private SelectOption getSelectionAnnotation(Object object) {
        return object.getClass().getAnnotation(SelectOption.class);
    }

    String buildSelectionElement(String id, Object values, Object selectedItem, boolean required, Integer size) {
        if (values instanceof Iterable) {
            return buildListFromIterable(id, (Iterable<?>) values, selectedItem, required, size);
        } else if (ReflectionUtils.isArray(values)) {
            return buildListFromIterable(id, new ReflectionUtils.ArrayIterable(values), selectedItem, required, size);
        } else {
            return selectWithError(id, "Not iterable/array.");
        }
    }

    private String buildListFromIterable(String id, Iterable<?> iterable, Object selectedItem, boolean required, Integer size) {
        HtmlElement select;
        HtmlElement defaultOption;

        select = new HtmlElement("select")
                .id(id)
                .name(id)
                .classes("input");

        defaultOption = new HtmlElement("option")
                .text("Select Option")
                .attribute("disabled");

        if (multiSelect) {
            select.attribute("multiple").attribute("size", size);
        } else {
            select.addChild(defaultOption);
        }

        if (required) {
            select.attribute("required");
        }

        boolean selectionFound;
        final Object selectedValue;
        final SelectOption selectedItemOption;

        selectionFound = false;

        if (selectedItem != null) {
            selectedItemOption = getSelectionAnnotation(selectedItem);
            if (selectedItemOption == null) {
                final SelectOptionEntry entry = selectOptionRegistry.getForObject(selectedItem);
                if (entry != null) {
                    selectedValue = getItemValue(entry.selectOption(), selectedItem);
                } else {
                    selectedValue = selectedItem;
                }
            } else {
                selectedValue = getItemValue(selectedItemOption, selectedItem);
            }
        } else {
            selectedValue = null;
        }

        for (Object item : iterable) {
            boolean skipOption = false;
            boolean fromRegistry = false;
            SelectOption selectOption = getSelectionAnnotation(item);

            if (selectOption == null) {
                final SelectOptionEntry entry = selectOptionRegistry.getForObject(item);
                if (entry == null) {
                    return selectWithError(id, "List Item: @SelectOption missing and not found in registry.");
                }
                fromRegistry = true;
                skipOption = entry.skip();
                selectOption = entry.selectOption();
            }

            if (fromRegistry) {
                if (skipOption) {
                    continue;
                }
            } else {
                if (shouldSkip(item)) {
                    continue;
                }
            }

            final Object value = getItemValue(selectOption, item);
            final String text = getItemText(selectOption, item);
            final boolean selected = Objects.equals(value, selectedValue);

            final HtmlElement option = new HtmlElement("option")
                    .text(escape(text))
                    .attribute("value", escape(Objects.toString(value)));

            select.addChild(option);

            if (selected) {
                selectionFound = true;
                option.attribute("selected");
            }
        }

        if (!selectionFound) {
            defaultOption.attribute("selected");
        }

        return select.build();
    }

    private boolean shouldSkip(Object item) {
        final Class<?> clazz;
        SkipOption skipOption;

        clazz = item.getClass();
        if (clazz.isAnnotationPresent(SkipOption.class)) {
            return true;
        }
        skipOption = null;
        if (item instanceof Enum<?>) {
            try {
                skipOption = clazz.getField(((Enum<?>) item).name()).getAnnotation(SkipOption.class);
            } catch (NoSuchFieldException ignore) {
            }
        }
        return skipOption != null;
    }

    private Object getItemValue(SelectOption selectOption, Object item) {
        return new ReflectionUtils.SymbolResolver(item, selectOption.value()).getValue();
    }

    private String getItemText(SelectOption selectOption, Object item) {
        return Objects.toString(new ReflectionUtils.SymbolResolver(item, selectOption.text()).getValue());
    }

    private String selectWithError(String id, String text) {
        return new HtmlElement("select")
                .id(id)
                .name(id)
                .addChild(
                        new HtmlElement("option")
                                .attribute("disabled")
                                .attribute("selected")
                                .text(text)
                ).build();
    }


    public FormSelect(SelectOptionRegistry registry) {
        this(registry, false);
    }

    private static final List<String> singleArgs = List.of("id", "values", "selected", "required");
    private static final List<String> multiArgs = List.of("id", "values", "selected", "required", "size");

    public FormSelect(SelectOptionRegistry registry, boolean multiSelect) {
        super("formSelect", multiSelect ? multiArgs : singleArgs);
        this.selectOptionRegistry = registry;
        this.multiSelect = multiSelect;
    }

    @Override
    public Object execute(Map<String, Object> args, PebbleTemplate self, EvaluationContext context, int lineNumber) {
        String id;
        Long size;
        Object values;
        Object selected;
        Boolean required;

        id = getArgument(args, "id", null);
        values = getArgument(args, "values", List.of());
        selected = getArgument(args, "selected", null);
        required = getArgument(args, "required", true);
        size = multiSelect ? getArgument(args, "size", -1L) : -1L;
        return buildSelectionElement(id, values, selected, required, size.intValue());
    }
}
