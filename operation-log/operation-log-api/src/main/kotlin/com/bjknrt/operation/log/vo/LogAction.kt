package com.bjknrt.operation.log.vo

import java.util.Objects
import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.DecimalMax
import javax.validation.constraints.DecimalMin
import javax.validation.constraints.Email
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size
import javax.validation.Valid

/**
* 
* Values: HS_MAKE_HEALTH_SCHEME,DPM_POST_TO_PATIENT_CLIENT,DPM_PATIENT_UN_BINDING_DOCTOR,DPM_PATIENT_BINDING_DOCTOR,UPS_DELETE_USER,UPS_ADD_ADMINISTRATOR,UPS_REGISTER_USER,UPS_RESET_USER_PWD,UPS_UPDATE_USER_PWD,UPS_DELETE_ROLE,UPS_SAVE_ROLE_PERMISSION,UPS_DELETE_ROLE_PERMISSION,PATIENT_CLIENT_DAILY_ACTIVE_USER,HEALTHY_ADD_ARTICLE,HEALTHY_UPDATE_ARTICLE,HEALTHY_DELETE_ARTICLE,HEALTHY_PUBLISH_ARTICLE,HEALTHY_OFF_SHELF_ARTICLE,HEALTHY_ADD_CARD,HEALTHY_UPDATE_CARD,HEALTHY_DELETE_CARD,HEALTHY_PUBLISH_CARD,HEALTHY_OFF_SHELF_CARD
*/
enum class LogAction(val value: kotlin.String) {

    /**
     * 
     */
    @JsonProperty("HS_MakeHealthScheme") HS_MAKE_HEALTH_SCHEME("HS_MakeHealthScheme"),
    
    /**
     * 
     */
    @JsonProperty("DPM_PostToPatientClient") DPM_POST_TO_PATIENT_CLIENT("DPM_PostToPatientClient"),
    
    /**
     * 
     */
    @JsonProperty("DPM_PatientUnBindingDoctor") DPM_PATIENT_UN_BINDING_DOCTOR("DPM_PatientUnBindingDoctor"),
    
    /**
     * 
     */
    @JsonProperty("DPM_PatientBindingDoctor") DPM_PATIENT_BINDING_DOCTOR("DPM_PatientBindingDoctor"),
    
    /**
     * 
     */
    @JsonProperty("UPS_DeleteUser") UPS_DELETE_USER("UPS_DeleteUser"),
    
    /**
     * 
     */
    @JsonProperty("UPS_AddAdministrator") UPS_ADD_ADMINISTRATOR("UPS_AddAdministrator"),
    
    /**
     * 
     */
    @JsonProperty("UPS_RegisterUser") UPS_REGISTER_USER("UPS_RegisterUser"),
    
    /**
     * 
     */
    @JsonProperty("UPS_ResetUserPwd") UPS_RESET_USER_PWD("UPS_ResetUserPwd"),
    
    /**
     * 
     */
    @JsonProperty("UPS_UpdateUserPwd") UPS_UPDATE_USER_PWD("UPS_UpdateUserPwd"),
    
    /**
     * 
     */
    @JsonProperty("UPS_DeleteRole") UPS_DELETE_ROLE("UPS_DeleteRole"),
    
    /**
     * 
     */
    @JsonProperty("UPS_SaveRolePermission") UPS_SAVE_ROLE_PERMISSION("UPS_SaveRolePermission"),
    
    /**
     * 
     */
    @JsonProperty("UPS_DeleteRolePermission") UPS_DELETE_ROLE_PERMISSION("UPS_DeleteRolePermission"),
    
    /**
     * 
     */
    @JsonProperty("PATIENT_CLIENT_DailyActiveUser") PATIENT_CLIENT_DAILY_ACTIVE_USER("PATIENT_CLIENT_DailyActiveUser"),
    
    /**
     * 
     */
    @JsonProperty("HEALTHY_AddArticle") HEALTHY_ADD_ARTICLE("HEALTHY_AddArticle"),
    
    /**
     * 
     */
    @JsonProperty("HEALTHY_UpdateArticle") HEALTHY_UPDATE_ARTICLE("HEALTHY_UpdateArticle"),
    
    /**
     * 
     */
    @JsonProperty("HEALTHY_DeleteArticle") HEALTHY_DELETE_ARTICLE("HEALTHY_DeleteArticle"),
    
    /**
     * 
     */
    @JsonProperty("HEALTHY_PublishArticle") HEALTHY_PUBLISH_ARTICLE("HEALTHY_PublishArticle"),
    
    /**
     * 
     */
    @JsonProperty("HEALTHY_OffShelfArticle") HEALTHY_OFF_SHELF_ARTICLE("HEALTHY_OffShelfArticle"),
    
    /**
     * 
     */
    @JsonProperty("HEALTHY_AddCard") HEALTHY_ADD_CARD("HEALTHY_AddCard"),
    
    /**
     * 
     */
    @JsonProperty("HEALTHY_UpdateCard") HEALTHY_UPDATE_CARD("HEALTHY_UpdateCard"),
    
    /**
     * 
     */
    @JsonProperty("HEALTHY_DeleteCard") HEALTHY_DELETE_CARD("HEALTHY_DeleteCard"),
    
    /**
     * 
     */
    @JsonProperty("HEALTHY_PublishCard") HEALTHY_PUBLISH_CARD("HEALTHY_PublishCard"),
    
    /**
     * 
     */
    @JsonProperty("HEALTHY_OffShelfCard") HEALTHY_OFF_SHELF_CARD("HEALTHY_OffShelfCard")
    
}

