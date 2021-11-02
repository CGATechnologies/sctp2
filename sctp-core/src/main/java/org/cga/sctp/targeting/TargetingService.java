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

package org.cga.sctp.targeting;

import org.cga.sctp.core.TransactionalService;
import org.cga.sctp.targeting.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TargetingService extends TransactionalService {

    @Autowired
    private TargetingSessionRepository sessionRepository;

    @Autowired
    private TargetingSessionViewRepository viewRepository;

    @Autowired
    private CriterionRepository criterionRepository;

    @Autowired
    private CriterionViewRepository criterionViewRepository;

    @Autowired
    private CriteriaFilterTemplateRepository filterTemplateRepository;

    @Autowired
    private CriteriaFilterRepository criteriaFilterRepository;

    @Autowired
    private CriteriaFilterViewRepository criteriaFilterViewRepository;

    @Autowired
    private FilterTemplateListOptionRepository filterTemplateListOptionRepository;

    @Autowired
    private EligibilityVerificationSessionRepository verificationSessionRepository;

    @Autowired
    private EligibilityVerificationSessionViewRepository verificationSessionViewRepository;

    public void saveTargetingSession(TargetingSession targetingSession) {
        sessionRepository.save(targetingSession);
    }

    public void performCommunityBasedTargetingRanking(TargetingSession session) {
        if (session.isOpen()) {
            sessionRepository.runCommunityBasedTargetingRanking(session.getId());
        }
    }

    public List<TargetingSessionView> targetingSessionViewList() {
        return viewRepository.findAll();
    }

    public TargetingSession findSessionById(Long sessionId) {
        return sessionRepository.findById(sessionId).orElse(null);
    }

    public Slice<CbtRanking> getCbtRanking(TargetingSessionView session, Pageable pageable) {
        return viewRepository.getCbtRankingResults(session.getId(), pageable.getPageNumber(), pageable.getPageSize());
    }

    public TargetingSessionView findSessionViewById(Long sessionId) {
        return viewRepository.findById(sessionId).orElse(null);
    }

    public void closeTargetingSession(TargetingSessionView session, Long userId) {
        sessionRepository.closeSession(session.getId(), userId, LocalDateTime.now(),
                TargetingSessionBase.SessionStatus.Closed.name());
    }

    public void saveTargetingCriterion(Criterion criterion) {
        criterionRepository.save(criterion);
    }

    public void deleteTargetingCriterion(Criterion criterion) {
        criterionRepository.delete(criterion);
    }

    public List<CriterionView> getTargetingCriteria() {
        return criterionViewRepository.findAll();
    }

    public List<CriterionView> getActiveTargetingCriteriaViews() {
        return criterionViewRepository.findByActive(true);
    }

    public List<Criterion> getActiveTargetingCriteria() {
        return criterionRepository.findByActive(true);
    }

    public Criterion findCriterionById(Long id) {
        return criterionRepository.findById(id).orElse(null);
    }

    public List<CriteriaFilter> getFiltersByCriterionId(Long criterionId) {
        return criteriaFilterRepository.findByCriterionId(criterionId);
    }

    public List<CriteriaFilterTemplate> getFilterTemplates() {
        return filterTemplateRepository.findAll();
    }

    public List<CriteriaFilterTemplate> getIndividualFilterTemplates() {
        return filterTemplateRepository.getByTableName("individuals");
    }

    public List<CriteriaFilterTemplate> getHouseholdFilterTemplates() {
        return filterTemplateRepository.getByTableName("households");
    }

    public CriteriaFilterTemplate findFilterTemplateById(Long templateId) {
        return filterTemplateRepository.findById(templateId).orElse(null);
    }

    public void saveCriteriaFilter(CriteriaFilter filter) {
        criteriaFilterRepository.save(filter);
    }

    public List<FilterTemplateListOption> getFilterTemplateListOptions(Long templateId) {
        return filterTemplateListOptionRepository.findByTemplateId(templateId);
    }

    public List<CriteriaFilterView> getFilterViewsByCriterionId(Long criterionId) {
        return criteriaFilterViewRepository.findByCriterionId(criterionId);
    }

    public CriteriaFilter findCriteriaFilterById(Long filter, Long criterionId) {
        return criteriaFilterRepository.findByIdAndCriterionId(filter, criterionId);
    }

    public void deleteCriteriaFilter(CriteriaFilter filter) {
        criteriaFilterRepository.delete(filter);
    }

    public void compileFilterQuery(Criterion criterion) {
        // TODO Compile query in the database(?)

    }

    public long getCriterionFilterCount(Criterion criterion) {
        return criteriaFilterRepository.countByCriterionId(criterion.getId());
    }

    public EligibilityVerificationSessionView findVerificationViewById(Long id) {
        return verificationSessionViewRepository.findById(id).orElse(null);
    }

    public void saveEligibilityVerificationSession(EligibilityVerificationSession session) {
        verificationSessionRepository.save(session);
    }

    public List<EligibilityVerificationSessionView> getVerificationSessionViews() {
        return verificationSessionViewRepository.findAll();
    }

    public Criterion getActiveTargetingCriterionById(Long criterion) {
        return criterionRepository.findById(criterion).orElse(null);
    }
}
