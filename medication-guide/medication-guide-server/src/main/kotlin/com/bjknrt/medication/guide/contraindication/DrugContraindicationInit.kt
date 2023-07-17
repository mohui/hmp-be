package com.bjknrt.medication.guide.contraindication

import com.bjknrt.medication.guide.*
import com.bjknrt.medication.guide.tree.TreeNode
import me.danwi.sqlex.core.query.arg
import me.danwi.sqlex.core.query.`in`
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component

@Component
class DrugContraindicationInit(
    val diseaseTable: MedicationGuideDiseaseTable,
    val ingredientTable: MedicationGuideIngredientTable,
    val categoryTable: MedicationGuideCategoryTable,
) :
    ApplicationListener<ApplicationReadyEvent?> {
    /**
     * 初始化各分类关系树
     */
    lateinit var categoryTreeData: Map<Long, TreeNode<Long>>

    /**
     * 初始化分类表
     */
    lateinit var categoryDataList: List<MedicationGuideCategory>

    /**
     * 初始化成分关系树
     */
    lateinit var ingredientTreeData: TreeNode<Long>

    /**
     * 成分基础表
     */
    lateinit var ingredientDataList: List<MedicationGuideIngredient>

    /**
     * 所有疾病
     */
    lateinit var diseaseList: List<MedicationGuideDisease>
    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        categoryDataList =
            categoryTable.select().where(MedicationGuideCategoryTable.CategoryTypeId `in` listOf(arg(0), arg(1))).find()

        ingredientDataList = ingredientTable.select().find()

        diseaseList = diseaseTable.select().find()

        //处理分类信息
        val data = mutableMapOf<Long, TreeNode<Long>>()

        for (type in categoryDataList.map { it.categoryTypeId }.distinct()) {

            val treeNode = TreeNode(0L)

            fun deepCategory(data: TreeNode<Long>, id: Long?) {
                if (categoryDataList.any { it.categoryTypeId == type && it.categoryParentId == id }) {
                    categoryDataList.filter { it.categoryTypeId == type && it.categoryParentId == id }.map {
                        val childNode = data.addChild(it.categoryId)
                        if (categoryDataList.any { i -> i.categoryParentId == it.categoryId && i.categoryTypeId == it.categoryTypeId })
                            deepCategory(childNode, it.categoryId)
                    }
                }
            }

            deepCategory(treeNode, 0L)

            data[type] = treeNode
        }
        categoryTreeData = data.toMap()

        //处理成分树
        ingredientTreeData = TreeNode(0L)
        fun deepIngredient(data: TreeNode<Long>, id: Long?) {
            if (ingredientDataList.any { i -> i.knParentIngredientId == id }) {
                ingredientDataList.filter { it.knParentIngredientId == id }.map {
                    val childNode = data.addChild(it.knIngredientId)
                    if (ingredientDataList.any { i -> i.knParentIngredientId == it.knIngredientId })
                        deepIngredient(childNode, it.knIngredientId)
                }
            }

        }
        deepIngredient(ingredientTreeData, 0L)

    }
}