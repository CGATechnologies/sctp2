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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TargetingService extends TransactionalService {

    @Autowired
    private TargetingSessionRepository sessionRepository;

    @Autowired
    private TargetingSessionViewRepository viewRepository;

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

    public List<CbtRanking> getCbtRanking(TargetingSessionView session, Pageable pageable) {
        return viewRepository.getCbtRankingResults(session.getId());
    }

    public TargetingSessionView findSessionViewById(Long sessionId) {
        return viewRepository.findById(sessionId).orElse(null);
    }

    public void closeTargetingSession(TargetingSessionView session, Long userId) {
        sessionRepository.closeSession(session.getId(), userId, LocalDateTime.now(),
                TargetingSessionBase.SessionStatus.Closed.name());
    }
}