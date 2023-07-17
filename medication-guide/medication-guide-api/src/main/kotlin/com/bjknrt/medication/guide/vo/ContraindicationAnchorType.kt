/**
 * 禁忌锚
 * 通用名-1 药理分类-2 药品-3 报警药品分类-4 成分分类-5
 * Values: 1,2,3
 */
enum class ContraindicationAnchorType {
    NONE, GENERIC, CATEGORY, DRUG, CONFLICT_DRUG_CATEGORY, INGREDIENT_CATEGORY
}