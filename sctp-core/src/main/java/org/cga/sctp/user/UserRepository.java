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

package org.cga.sctp.user;

import org.cga.sctp.persistence.StatusCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "select * FROM active_users WHERE email = :email", nativeQuery = true)
    User findByEmail(@Param("email") String email);

    @Query(value = "select * FROM active_users WHERE user_name = :userName", nativeQuery = true)
    User findByUserName(@Param("userName") String userName);

    @Query(value = "select * FROM users WHERE user_name = :userName", nativeQuery = true)
    User findByUserNameEx(@Param("userName") String userName);

    @Query(value = "select * FROM users WHERE user_name = :userName AND session_id = :sessionId", nativeQuery = true)
    User findByUserNameAndSessionId(@Param("userName") String userName, @Param("sessionId") String sessionId);

    /**
     * Searches for a user with the given email address using Spring's DQM (Derived Query Method)
     *
     * @param email The email address to check
     * @return true if email exists otherwise false
     */
    boolean existsUserByEmail(String email);

    /**
     * Searches for a user with the given user name using Spring's DQM (Derived Query Method)
     *
     * @param userName Username to check
     * @return true if email exists otherwise false
     */
    boolean existsUserByUserName(String userName);

    @Query(nativeQuery = true, value = "select * from users where status != :status")
    List<User> getNotByStatus(@Param("status") StatusCode deleted);

    @Query(nativeQuery = true, value = "CALL lookupUsernameAndEmail(:username, :email)")
    UserNameEmailLookUp lookupUserNameAndEmail(@Param("username") String username, @Param("email") String email);

    @Query(nativeQuery = true, value = "select user_name from users where id = :id")
    String findUsernameById(@Param("id") Long userId);
}
