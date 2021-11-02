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

import org.cga.sctp.location.Location;
import org.cga.sctp.location.LocationCode;
import org.cga.sctp.program.Program;
import org.cga.sctp.program.ProgramUser;
import org.cga.sctp.program.ProgramUserCandidate;
import org.cga.sctp.security.permission.UserRole;
import org.cga.sctp.targeting.criteria.CriteriaFilterObject;
import org.cga.sctp.targeting.criteria.CriteriaFilterTemplate;
import org.cga.sctp.targeting.criteria.Criterion;
import org.cga.sctp.user.AccessLevel;
import org.cga.sctp.user.Permission;
import org.cga.sctp.user.SystemRole;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Annotate remote classes with the {@link SelectOption} annotation for use by
 * {@link org.cga.sctp.mis.core.templating.functions.FormSelect}
 */
@Configuration
public class SelectOptionConfigs {

    @Bean
    public SelectOptionEntry userRoleSelectOption() {
        // Use UserRole.id as unique value and description as human-friendly value
        return new SelectOptionEntry(UserRole.class, "id", "description");
    }

    @Bean
    public SelectOptionEntry locationSelectOption() {
        return new SelectOptionEntry(Location.class, "id", "name");
    }

    @Bean
    public SelectOptionEntry systemRoleSelectOption() {
        return new SelectOptionEntry(SystemRole.class, "name()", "label");
    }

    @Bean
    public SelectOptionEntry filterTemplateSelectOption() {
        return new SelectOptionEntry(CriteriaFilterTemplate.class, "getId()", "getLabel()");
    }

    @Bean
    public SelectOptionEntry conjunctionSelectOption() {
        return new SelectOptionEntry(CriteriaFilterObject.Conjunction.class, "name()", "name()");
    }

    @Bean
    public SelectOptionEntry accessLevelSelectOption() {
        return new SelectOptionEntry(AccessLevel.class, "name()", "title");
    }

    @Bean
    public SelectOptionEntry programUserCandidateSelectOption() {
        return new SelectOptionEntry(ProgramUserCandidate.class, "userId", "fullName");
    }

    @Bean
    public SelectOptionEntry programSelectOption() {
        return new SelectOptionEntry(Program.class, "id", "name");
    }

    @Bean
    public SelectOptionEntry locationCodes() {
        return new SelectOptionEntry(LocationCode.class, "code", "name");
    }

    @Bean
    public SelectOptionEntry programUserSelectOption() {
        return new SelectOptionEntry(ProgramUser.class, "userId", "fullName");
    }

    @Bean
    public SelectOptionEntry userPermissionSelectOption() {
        return new SelectOptionEntry(Permission.class, "name()", "title");
    }

    @Bean
    public SelectOptionEntry criterionSelectOption() {
        return new SelectOptionEntry(Criterion.class, "getId()", "getName()");
    }
}
