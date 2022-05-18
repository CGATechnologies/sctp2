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
import org.cga.sctp.utils.CollectionUtils;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TargetingService extends TransactionalService {
    private static final int PAGE_SIZE = 200;

    @Autowired
    private CbtRankingRepository cbtRankingRepository;

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

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EnrolmentSessionRepository enrolmentRepository;

    @Autowired
    private EligibleHouseholdDetailsRepository eligibleHouseholdDetailsRepository;

    @Autowired
    private TargetingResultRepository targetingResultRepository;

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

    public Slice<CbtRankingResult> getCbtRanking(TargetingSessionView session, Pageable pageable) {
        return cbtRankingRepository.findByCbtSessionId(session.getId(), pageable);
    }

    public TargetingSessionView findSessionViewById(Long sessionId) {
        return viewRepository.findById(sessionId).orElse(null);
    }

    public void closeTargetingSession(TargetingSessionView session, Long userId) {
        sessionRepository.closeSession(session.getId(), userId, LocalDateTime.now(),
                TargetingSessionBase.SessionStatus.Closed.name());

        enrolmentRepository.sendToEnrolment(session.getId(), (long) 0, userId);

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


    private String placeholder(CriteriaFilterInfo info) {
        return format("%s_%s_%s%d",
                info.getConjunction().name().toLowerCase(),
                info.getTableName(), info.getColumnName(), info.getId()
        );
    }

    public void compileFilterQuery(Criterion criterion) {
        List<CriteriaFilterInfo> criteriaFilterInfoList =
                criteriaFilterRepository.getFilterValuesForCriterion(criterion.getId());
        if (criteriaFilterInfoList.isEmpty()) {
            return;
        }

        StringBuilder builder;
        List<CriteriaFilterInfo> ands;
        List<CriteriaFilterInfo> ors;

        boolean hasHouseholdFilters;
        boolean hasIndividualFilters;

        ands = criteriaFilterInfoList.stream()
                .filter(cfi -> cfi.getConjunction() == CriteriaFilterObject.Conjunction.And || cfi.getConjunction() == CriteriaFilterObject.Conjunction.None)
                .collect(Collectors.toList());

        ors = criteriaFilterInfoList.stream()
                .filter(cfi -> cfi.getConjunction() == CriteriaFilterObject.Conjunction.Or)
                .collect(Collectors.toList());

        hasIndividualFilters = criteriaFilterInfoList.stream()
                .anyMatch(cfi -> cfi.getTableName().equalsIgnoreCase("individuals"));

        hasHouseholdFilters = criteriaFilterInfoList.stream()
                .anyMatch(cfi -> cfi.getTableName().equalsIgnoreCase("households"));

        StringBuilder clauseBuilder = new StringBuilder();

        if (!ands.isEmpty()) {
            clauseBuilder.append('(');
            boolean first = true;
            for (CriteriaFilterInfo info : ands) {
                if (!first) {
                    clauseBuilder.append(" AND ");
                }
                first = false;
                clauseBuilder.append(info.getTableName())
                        .append(".")
                        .append(info.getColumnName())
                        .append(" = :").append(placeholder(info));
            }
            clauseBuilder.append(')');
        }

        if (!ors.isEmpty()) {
            if (!clauseBuilder.isEmpty()) {
                clauseBuilder.append(" OR ");
            }
            clauseBuilder.append('(');
            boolean first = true;
            for (CriteriaFilterInfo info : ors) {
                if (!first) {
                    clauseBuilder.append(" OR ");
                }
                first = false;
                clauseBuilder.append(info.getTableName())
                        .append(".")
                        .append(info.getColumnName())
                        .append(" = :").append(placeholder(info));
            }
            clauseBuilder.append(')');
        }

        // Compile the query only. The actual run and parameter binding will be done later.

        builder = new StringBuilder("SELECT households.household_id, households.location_code, households.ta_code,")
                .append("households.zone_code, households.cluster_code FROM households");

        if (hasHouseholdFilters) {
            if (hasIndividualFilters) {
                builder.append(" JOIN individuals ON individuals.household_id = households.household_id");
            }
        } else {
            builder.append(" JOIN individuals ON individuals.household_id = households.household_id");
        }

        builder.append(" WHERE (").append(clauseBuilder).append(") GROUP BY household_id");

        String query = builder.toString();

        criterion.setCompiledAt(LocalDateTime.now());
        criterion.setCompiledQuery(query);

        saveTargetingCriterion(criterion);
    }

    public long getCriterionFilterCount(CriterionObject criterion) {
        return criteriaFilterRepository.countByCriterionId(criterion.getId());
    }

    public EligibilityVerificationSessionView findVerificationViewById(Long id) {
        return verificationSessionViewRepository.findById(id).orElse(null);
    }

    public void saveEligibilityVerificationSession(EligibilityVerificationSession session) {
        verificationSessionRepository.save(session);
    }

    public void addEligibilityVerificationSession(EligibilityVerificationSession session, Criterion criterion, Long userId) {
        if (session instanceof HibernateProxy) {
            return;
        }
        verificationSessionRepository.save(session);
        runEligibilityVerification(session, criterion, userId);
        verificationSessionRepository.calculateHouseholdCount(session.getId());
    }

    /**
     * Runs the eligibility verification. This method must only be called when creating a {@link EligibilityVerificationSession}
     *
     * @param session   A managed entity recently persisted.
     * @param criterion Criterion from which filters will be used to evaluate households
     * @param userId    The user who initiated this run.
     */
    private void runEligibilityVerification(EligibilityVerificationSession session, Criterion criterion, Long userId) {

        List<CriteriaFilterInfo> criteriaFilterInfoList = criteriaFilterRepository
                .getFilterValuesForCriterion(session.getCriterionId());

        StringBuilder builder = new StringBuilder(" INSERT INTO eligible_households (session_id, household_id, created_at, run_by)")
                .append(" WITH _insert_ AS (").append(criterion.getCompiledQuery()).append(")")
                .append(" SELECT :sessionId, household_id, :createdAt, :runBy")
                .append(" FROM _insert_")
                .append(" WHERE location_code = :districtCode AND ta_code = :taCode")
                .append(" AND FIND_IN_SET(cluster_code, :clusterCodes)");

        String sql = builder.toString();

        Query query = entityManager.createNativeQuery(sql);

        query.setParameter("runBy", userId)
                .setParameter("taCode", session.getTaCode())
                .setParameter("sessionId", session.getId())
                .setParameter("createdAt", LocalDateTime.now())
                .setParameter("districtCode", session.getDistrictCode())
                .setParameter("clusterCodes", CollectionUtils.join(session.getClusters()));

        for (CriteriaFilterInfo info : criteriaFilterInfoList) {
            query.setParameter(placeholder(info), info.getFilterValue());
        }

        query.executeUpdate();
    }

    public List<EligibilityVerificationSessionView> getVerificationSessionViews() {
        return verificationSessionViewRepository.findAll();
    }

    public Criterion getActiveTargetingCriterionById(Long criterion) {
        return criterionRepository.findById(criterion).orElse(null);
    }

    public EligibilityVerificationSession getVerificationSessionById(Long id) {
        return verificationSessionRepository.findById(id).orElse(null);
    }

    public void closeVerificationSession(EligibilityVerificationSession session,
                                         VerificationSessionDestination destination, Long userId) {
        session.setStatus(EligibilityVerificationSessionBase.Status.Closed);
        verificationSessionRepository.save(session);

        // Send to next module if the session did have households that matched
        if (session.getHouseholds() > 0) {
            switch (destination) {
                case enrolment -> enrolmentRepository.sendToEnrolment(0L, session.getId(), userId);
                case targeting -> {
                    // 1. Create a targeting session
                    TargetingSession targetingSession = new TargetingSession();
                    targetingSession.setCreatedBy(userId);
                    targetingSession.setTaCode(session.getTaCode());
                    targetingSession.setPevSession(session.getId());
                    targetingSession.setCreatedAt(LocalDateTime.now());
                    targetingSession.setClusters(session.getClusters());
                    targetingSession.setProgramId(session.getProgramId());
                    targetingSession.setDistrictCode(session.getDistrictCode());
                    targetingSession.setStatus(TargetingSessionBase.SessionStatus.Review);

                    saveTargetingSession(targetingSession);

                    // 2. Run CBT on the households selected
                    sessionRepository.runCommunityBasedTargetingRankingOnEligibleHouseholds(targetingSession.getId(), session.getId());
                }
            }
        } else {
            LOG.warn("Pre-eligibility session with id {} did not have any households to be sent to either enrolment or targeting",
                    session.getId());
        }
    }

    public CriterionView findCriterionViewById(Long id) {
        return criterionViewRepository.findById(id).orElse(null);
    }

    public Long getCriterionUsageCount(Criterion criterion) {
        return criterionRepository.getUsageCount(criterion.getId());
    }

    public List<EligibleHousehold> getEligibleHouseholds(EligibilityVerificationSessionBase session) {
        return verificationSessionRepository.getEligibleHouseholds(session.getId());
    }

    public Page<EligibleHouseholdDetails> getEligibleHouseholdsDetails(Long sessionId, int page) {
        return eligibleHouseholdDetailsRepository.getBySessionId(
                sessionId,
                Pageable.ofSize(PAGE_SIZE).withPage(page)
        );
    }

    public Page<EligibilityVerificationSessionView> getOpenVerificationSessionsByLocation(
            long districtCode
            , Long taCode
            , Long villageClusterCode
            , Long zoneCode
            , Long villageCode
            , int page) {
        if (isCodeSet(taCode) && isCodeSet(villageClusterCode)) {
            return verificationSessionViewRepository.findByOpenByLocation(
                    EligibilityVerificationSessionBase.Status.Review.name()
                    , districtCode
                    , taCode
                    , villageClusterCode
                    , Pageable.ofSize(PAGE_SIZE).withPage(page)
            );
        }
        if (isCodeSet(taCode) && !isCodeSet(villageClusterCode)) {
            return verificationSessionViewRepository.findByOpenByLocation(
                    EligibilityVerificationSessionBase.Status.Review.name()
                    , districtCode
                    , taCode
                    , Pageable.ofSize(PAGE_SIZE).withPage(page)
            );
        }
        return verificationSessionViewRepository.findByOpenByLocation(
                EligibilityVerificationSessionBase.Status.Review.name()
                , districtCode
                , Pageable.ofSize(PAGE_SIZE).withPage(page)
        );
    }

    private boolean isCodeSet(Long code) {
        return code != null && code > 0L;
    }

    public void saveTargetingResult(TargetingResult targetingResult) {
        targetingResultRepository.save(targetingResult);
    }

    public TargetingResult findTargetingResultByHouseholdId(Long sessionId, Long household) {
        return targetingResultRepository.findByTargetingSessionAndHousehold(sessionId, household);
    }
}
