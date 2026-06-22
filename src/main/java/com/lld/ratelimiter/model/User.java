package com.lld.ratelimiter.model;

import com.lld.ratelimiter.enums.UserTier;
import lombok.AllArgsConstructor;
import lombok.Data;
/**
 * Author  AAgarwal
 * <p>
 * Date   6/21/2026
 */

@Data
@AllArgsConstructor
public class User
{
    private String userId;
    private UserTier userTier;
}