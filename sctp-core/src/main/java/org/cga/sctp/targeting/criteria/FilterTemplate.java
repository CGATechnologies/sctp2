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

package org.cga.sctp.targeting.criteria;

import javax.persistence.*;
import java.time.LocalDateTime;

@MappedSuperclass
public abstract class FilterTemplate {

    public enum TargetCategory {
        individuals,
        households
    }

    public enum FieldType {
        Number(true),
        NumberSigned(true),
        Decimal(true),
        Text(true),
        Date(true),
        ListSingle(true),
        ListMultiple(true),
        NumberRange(true),
        DecimalRange(true),
        /*
         This filter has no value, it simply will be used for joining to other tables and views.
         Computation is assumed to have already been done in the foreign linked table/view
         */
        ForeignMappedField(false);

        FieldType(boolean wantsFilterValue) {
            this.wantsFilterValue = wantsFilterValue;
        }

        public final boolean wantsFilterValue;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tableName;
    private String columnName;

    private String sourceTableName;
    private String sourceColumnName;

    /**
     * Display text
     */
    private String label;

    @Enumerated(value = EnumType.STRING)
    private FieldType fieldType;
    private String fieldValues;
    private LocalDateTime createdAt;
    private String hint;

    @Enumerated(EnumType.STRING)
    private Operator operator;

    @Enumerated(EnumType.STRING)
    private TargetCategory targetCategory;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
    }

    public String getFieldValues() {
        return fieldValues;
    }

    public void setFieldValues(String fieldValues) {
        this.fieldValues = fieldValues;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public String getSourceTableName() {
        return sourceTableName;
    }

    public void setSourceTableName(String sourceTableName) {
        this.sourceTableName = sourceTableName;
    }

    public String getSourceColumnName() {
        return sourceColumnName;
    }

    public void setSourceColumnName(String sourceColumnName) {
        this.sourceColumnName = sourceColumnName;
    }

    public TargetCategory getTargetCategory() {
        return targetCategory;
    }

    public void setTargetCategory(TargetCategory targetCategory) {
        this.targetCategory = targetCategory;
    }
}
