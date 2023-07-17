package com.bjknrt.user.permission.centre.vo

import com.bjknrt.framework.api.vo.Id
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * UserRoleOrg
 * @param id  
 * @param name  
 * @param roles Roles 
 * @param organizations Organizations 
 * @param regions  
 * @param identityTags  
 */
data class UserRoleOrg(
    
    @field:Valid
    @field:JsonProperty("id", required = true) val id: Id,
    
    @field:JsonProperty("name", required = true) val name: kotlin.String,
    
    @field:Valid
    @field:JsonProperty("roles", required = true) val roles: kotlin.collections.List<Role>,
    
    @field:Valid
    @field:JsonProperty("organizations", required = true) val organizations: kotlin.collections.List<Org>,
    
    @field:Valid
    @field:JsonProperty("regions", required = true) val regions: kotlin.collections.List<Region>,
    
    @field:Valid
    @field:JsonProperty("identityTags", required = true) val identityTags: kotlin.collections.List<IdentityTag>
) {

}

