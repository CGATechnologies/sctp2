/*
 * Copyright (C) 2021 CGA Technologies, a trading name of Charlie Goldsmith Associates Ltd
 *  All rights reserved, released under the BSD-3 licence.
 *
 * CGA Technologies develop and use this software as part of its work
 *  but the software itself is open-source software; you can redistribute it and/or modify
 *  it under the terms of the BSD licence below
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice, this
 *     list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *  3. Neither the name of the copyright holder nor the names of its contributors
 *     may be used to endorse or promote products derived from this software
 *     without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 *  THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 *  PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS
 *  BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 *  GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 *  HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 *  LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 *  OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 *  DAMAGE.
 *
 * For more information please see http://opensource.org/licenses/BSD-3-Clause
 */

package org.cga.sctp.location;

/**
 * Type of location/region
 */
//
public enum LocationType {
    COUNTRY(0, true, false, "Country", null),
    SUBNATIONAL1(1, false, false, "Subnational 1", COUNTRY), // District
    SUBNATIONAL2(2, false, false, "Subnational 2", SUBNATIONAL1), // TA
    SUBNATIONAL3(3, false, false, "Subnational 3", SUBNATIONAL2), // Cluster
    SUBNATIONAL4(4, false, false, "Subnational 4", SUBNATIONAL3), // Zone
    SUBNATIONAL5(5, false, true, "Subnational 5", SUBNATIONAL4), // Village
    SUBNATIONAL6(6, false, true, "Subnational 6", SUBNATIONAL1); // GVH support (under TA)

    LocationType(int level, boolean isRoot, boolean isLast, String description, LocationType parent) {
        this.level = level;
        this.isRoot = isRoot;
        this.isLast = isLast;
        this.description = description;
        this.parent = parent;
    }

    /**
     * <p>Hierarchical level (0 being the root. The lower the number, the higher the logical level).
     * All based on the following topographic relationship:
     * <b>i.e, District &gt; TA &gt; Village Cluster &gt; Zone &gt; Village</b></p>
     */
    public final int level;
    /**
     * Marks this location as a root location.
     */
    public final boolean isRoot;
    public final boolean isLast;
    public final String description;
    public final LocationType parent;

    public boolean isImmediateChildOf(LocationType parent) {
        return this.parent == parent;
    }
}
