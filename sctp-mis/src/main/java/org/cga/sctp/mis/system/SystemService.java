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

package org.cga.sctp.mis.system;

import org.cga.sctp.application.SystemInformation;
import org.cga.sctp.core.BaseService;
import org.cga.sctp.mis.config.HostConfigOverride;
import org.cga.sctp.mis.config.ServerConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class SystemService extends BaseService {

    @Autowired
    private SystemInformation systemInformation;

    @Autowired
    private ServerConfiguration serverConfig;

    public SystemInformation getSystemInformation() {
        return systemInformation;
    }

    public void setSystemInformation(SystemInformation systemInformation) {
        this.systemInformation = systemInformation;
    }

    public ServerConfiguration getServerConfig() {
        return serverConfig;
    }

    public void setServerConfig(ServerConfiguration serverConfig) {
        this.serverConfig = serverConfig;
    }

    public String makeUrl(Object... paths) {
        if (getServerConfig().getHostInfo() == null) {
            StringBuilder builder = new StringBuilder();
            if (getServerConfig().isSslEnabled()) {
                builder.append("https://");
            } else {
                builder.append("http://");
            }
            builder.append(serverConfig.getHost());
            if (!serverConfig.isStandardPort()) {
                builder.append(":").append(serverConfig.getPort());
            }
            for (Object pathObject : paths) {
                String path = Objects.toString(pathObject);
                if (!path.startsWith("/")) {
                    builder.append('/');
                }
                builder.append(path);
            }
            return builder.toString();
        } else {
            return makeUrlWithDownstreamProxyConfig(paths);
        }
    }

    private String makeUrlWithDownstreamProxyConfig(Object... paths) {
        StringBuilder builder = new StringBuilder();
        HostConfigOverride config = getServerConfig().getHostInfo();
        if (config.isSslOn()) {
            builder.append("https://");
        } else {
            builder.append("http://");
        }
        builder.append(config.getHost());
        if (!config.isStandardPort()) {
            builder.append(":").append(config.getPort());
        }
        for (Object pathObject : paths) {
            String path = Objects.toString(pathObject);
            if (!path.startsWith("/")) {
                builder.append('/');
            }
            builder.append(path);
        }
        return builder.toString();
    }
}
