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

package org.cga.sctp.mis.utils;

import cn.apiclub.captcha.Captcha;
import cn.apiclub.captcha.backgrounds.BackgroundProducer;
import cn.apiclub.captcha.backgrounds.TransparentBackgroundProducer;
import cn.apiclub.captcha.noise.CurvedLineNoiseProducer;
import cn.apiclub.captcha.noise.NoiseProducer;
import org.cga.sctp.core.BaseComponent;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Service
public class CaptchaService extends BaseComponent {

    public static final String ATTRIBUTE_NAME = "org.cga.sctp.mis.auth.CAPTCHA";

    private static final int WIDTH = 200, HEIGHT = 50;

    private final long ttlSeconds;
    private final NoiseProducer noiseProducer;
    private final BackgroundProducer backgroundProducer;

    public CaptchaService() {
        ttlSeconds = TimeUnit.MINUTES.toSeconds(2);
        noiseProducer = new CurvedLineNoiseProducer();
        backgroundProducer = new TransparentBackgroundProducer();
    }

    public Captcha build() {
        return new Captcha.Builder(WIDTH, HEIGHT)
                .addText()
                .addNoise(noiseProducer)
                .addNoise(noiseProducer)
                .addBackground(backgroundProducer)
                .build();
    }

    public boolean isExpired(Captcha captcha) {
        Instant now = Instant.now();
        Instant expiryTime = captcha.getTimeStamp().toInstant().plusSeconds(ttlSeconds);
        return now.isAfter(expiryTime);
    }

    public boolean isValidCaptcha(HttpSession session, String answer) {
        if (session == null) {
            return false;
        }
        try {
            Captcha captcha = (Captcha) session.getAttribute(ATTRIBUTE_NAME);
            return captcha.isCorrect(answer) && !isExpired(captcha);
        } catch (Exception e) {
            return false;
        }
    }
}
