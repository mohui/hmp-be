package com.bjknrt.medication.guide.vo

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * 
 * @param drugId 药品编号 
 * @param drugName 药品名称 
 * @param contraindicationId 禁忌编号 
 * @param contraindicationType  
 * @param contraindicationLevel 禁忌等级 
 * @param contraindicationContent 禁忌描述 
 * @param conflictDrugId 冲突药品编号 
 */
data class DrugContraindicationInner(
    
    @field:JsonProperty("drug_id", required = true) val drugId: kotlin.Long,
    
    @field:JsonProperty("drug_name", required = true) val drugName: kotlin.String,
    
    @field:JsonProperty("contraindication_id", required = true) val contraindicationId: kotlin.Long,
    
    @field:Valid
    @field:JsonProperty("contraindication_type", required = true) val contraindicationType: ContraindicationType,
    
    @field:JsonProperty("contraindication_level", required = true) val contraindicationLevel: kotlin.Int,
    
    @field:JsonProperty("contraindication_content", required = true) val contraindicationContent: kotlin.String,

    @field:JsonProperty("conflict_drug_id") val conflictDrugId: kotlin.Long? = null
) {

}

