package com.bjknrt.statistic.analysis.controller

import com.bjknrt.framework.api.AppBaseController
import com.bjknrt.security.client.AppSecurityUtil
import com.bjknrt.statistic.analysis.ExcelDataSource
import com.bjknrt.statistic.analysis.ExcelHeaderNode
import com.bjknrt.statistic.analysis.ExcelSheetTable
import com.bjknrt.statistic.analysis.StatisticPeopleDao
import com.bjknrt.statistic.analysis.StatisticPeopleDao.FindRegionOrHospitalResult
import com.bjknrt.statistic.analysis.api.StatisticPeopleApi
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.RestController
import java.math.BigInteger
import java.net.URLEncoder


@RestController("com.bjknrt.statistic.analysis.api.StatisticPeopleController")
class StatisticPeopleController(val dao: StatisticPeopleDao) : AppBaseController(), StatisticPeopleApi {
    //数据库中的全国编码
    val cnRegionCode: BigInteger = BigInteger.valueOf(999999999999)
    override fun statisticPeopleHtnT2dmGet(id: String?) {
        val hospitalOrRegion = if (id.isNullOrEmpty()) {
            if (AppSecurityUtil.jwtUser()?.isSuperAdmin() == true) cnRegionCode
            else (getCurrentUserHospitalOrRegion() ?: return)
        } else id.toBigInteger()
        //解析参数，区分行政区域与机构
        val params = parseParameters(hospitalOrRegion)
        val hospitalPatientIndicatorResults = dao.findHospitalPatientIndicator(params.regionCode, params.hospitalIds)
        val dataSource = mutableListOf<ExcelSheetTable>()
        val hypertensionRows = mutableListOf<List<Any>>()
        hypertensionRows.add(
            listOf(
                "区域/机构",
                "高血压患者数量",
                "30天内BMI正常的患者数量",
                "过去30天内测量血压人数",
                "最近一次血压达标人数（30天内）",
                "合并糖尿病患者人数",
                "合并冠心病患者人数",
                "合并脑卒中患者人数"
            )
        )
        val diabetesRows = mutableListOf<List<Any>>()
        diabetesRows.add(
            listOf(
                "区域/机构",
                "糖尿病患者数量",
                "30天内BMI正常的患者数量",
                "过去30天内测量空腹血糖人数",
                "最近一次空腹血糖达标人数（30天内）",
                "过去6个月内测量糖化血红蛋白人数",
                "最近一次糖化血红蛋白达标人数（30天内）",
                "合并高血压患者人数",
                "糖尿病肾病患者人数",
                "糖尿病视网膜病变患者人数",
                "糖尿病足患者人数"
            )
        )
        //行政区域结果无本级
        params.rows
            .filter {
                if (params.isRegionAccess == true && id.isNullOrEmpty()) it.id == hospitalOrRegion
                else if (params.isRegionAccess == true && !id.isNullOrEmpty()) it.id != hospitalOrRegion
                else true
            }
            .forEach { rh ->
                //获取当前维度的统计数据
                var rowData: List<StatisticPeopleDao.FindHospitalPatientIndicatorResult> = listOf()
                if (rh.type == "org") {
                    rowData = hospitalPatientIndicatorResults.filter { it.knHospitalId == rh.id }
                } else {
                    if (id.isNullOrEmpty()) {
                        rowData = hospitalPatientIndicatorResults
                    } else {
                        rowData =
                            hospitalPatientIndicatorResults.filter {
                                val regions = it.regionPath.split(".")
                                if (regions.size < 3) false
                                else
                                    regions[2] == rh.id.toString()
                            }
                    }
                }
                //处理excel数据
                hypertensionRows.add(
                    listOf(
                        rh.name ?: rh.id,
                        rowData.sumOf { it.hypertensionCount }.toInt(),
                        rowData.sumOf { it.hypertensionBmiCount }.toInt(),
                        rowData.sumOf { it.hypertensionPressureCount }.toInt(),
                        rowData.sumOf { it.hypertensionPressureCorrectCount }.toInt(),
                        rowData.sumOf { it.hypertensionDiabetesCount }.toInt(),
                        rowData.sumOf { it.hypertensionAcuteCoronaryCount }.toInt(),
                        rowData.sumOf { it.hypertensionCerebralStrokeCount }.toInt()
                    )
                )
                diabetesRows.add(
                    listOf(
                        rh.name ?: rh.id,
                        rowData.sumOf { it.diabetesCount }.toInt(),
                        rowData.sumOf { it.diabetesBmiCount }.toInt(),
                        rowData.sumOf { it.diabetesSugarCount }.toInt(),
                        rowData.sumOf { it.diabetesSugarCorrectCount }.toInt(),
                        rowData.sumOf { it.diabetesHbA1cCount }.toInt(),
                        rowData.sumOf { it.diabetesHbA1cCount1 }.toInt(),
                        rowData.sumOf { it.hypertensionDiabetesCount }.toInt(),
                        rowData.sumOf { it.diabetesNephropathyCount }.toInt(),
                        rowData.sumOf { it.diabetesRetinopathyCount }.toInt(),
                        rowData.sumOf { it.diabetesFootCount }.toInt()
                    )
                )
            }
        var index = 0
        hypertensionRows.add(
            listOf(
                "合计",
                *(hypertensionRows[0] as List<*>).subList(1, hypertensionRows[0].size).map {
                    index++
                    hypertensionRows.subList(1, hypertensionRows.size).sumOf { r -> r[index].toString().toInt() }
                }.toTypedArray()
            )
        )
        dataSource.add(ExcelSheetTable("高血压", null, hypertensionRows))
        index = 0
        diabetesRows.add(
            listOf(
                "合计",
                *(diabetesRows[0] as List<*>).subList(1, diabetesRows[0].size).map {
                    index++
                    diabetesRows.subList(1, diabetesRows.size).sumOf { r -> r[index].toString().toInt() }
                }.toTypedArray()
            )
        )
        dataSource.add(ExcelSheetTable("糖尿病", null, diabetesRows))
        exportExcel("管理效果统计", dataSource)
        return
    }

    override fun statisticPeopleManagerGet(id: String?) {
        val hospitalOrRegion = if (id.isNullOrEmpty()) {
            if (AppSecurityUtil.jwtUser()?.isSuperAdmin() == true) cnRegionCode
            else (getCurrentUserHospitalOrRegion() ?: return)
        } else id.toBigInteger()
        val dataSource = mutableListOf<ExcelSheetTable>()
        val commonHeaderGenerator = arrayOf(
            ExcelHeaderNode("管理总人数"),
            ExcelHeaderNode(
                "患者",
                ExcelHeaderNode("高血压", ExcelHeaderNode("管理人数"), ExcelHeaderNode("服务包订阅人数")),
                ExcelHeaderNode("糖尿病", ExcelHeaderNode("管理人数"), ExcelHeaderNode("服务包订阅人数")),
                ExcelHeaderNode("冠心病", ExcelHeaderNode("管理人数"), ExcelHeaderNode("服务包订阅人数")),
                ExcelHeaderNode("脑卒中", ExcelHeaderNode("管理人数"), ExcelHeaderNode("服务包订阅人数")),
                ExcelHeaderNode("慢阻肺", ExcelHeaderNode("管理人数"), ExcelHeaderNode("服务包订阅人数"))
            )
        )
        //解析参数，区分行政区域与机构
        val params = parseParameters(hospitalOrRegion)
        if (params.rows.size > 1) {
            val hospitalManagerPatientResults = dao.findHospitalManagerPatient(params.regionCode, params.hospitalIds)
            //区域/机构sheet
            val excelDataRows1 = mutableListOf<List<Any>>()
            params.rows
                .filter {
                    if (params.isRegionAccess == true && id.isNullOrEmpty()) it.id == hospitalOrRegion
                    else if (params.isRegionAccess == true && !id.isNullOrEmpty()) it.id != hospitalOrRegion
                    else true
                }
                .forEach { rh ->
                    //获取当前维度的角色用户数量、医护用户数量
                    var patientResults: List<StatisticPeopleDao.FindHospitalManagerPatientResult> = listOf()
                    if (rh.type == "org") {
                        patientResults = hospitalManagerPatientResults.filter { it.knHospitalId == rh.id }
                    } else {
                        if (id.isNullOrEmpty()) {
                            patientResults = hospitalManagerPatientResults
                        } else {
                            patientResults =
                                hospitalManagerPatientResults.filter {
                                    val regions = it.regionPath.split(".")
                                    if (regions.size < 3) false
                                    else
                                        regions[2] == rh.id.toString()
                                }
                        }
                    }
                    //处理excel数据
                    excelDataRows1.add(
                        listOf(
                            rh.name,
                            //管理总人数
                            patientResults.sumOf { it.count }.toInt(),
                            // 高血压管理人数
                            patientResults.sumOf { it.hypertensionDiseaseExists }.toInt(),
                            // 高血压服务包订阅人数
                            patientResults.sumOf { it.servicesHypertensionDiseaseExists }.toInt(),
                            // 糖尿病管理人数
                            patientResults.sumOf { it.diabetesDiseaseExists }.toInt(),
                            // 糖尿病服务包订阅人数
                            patientResults.sumOf { it.servicesDiabetesDiseaseExists }.toInt(),
                            // 冠心病管理人数
                            patientResults.sumOf { it.acuteCoronaryDiseaseExists }.toInt(),
                            // 冠心病服务包订阅人数
                            patientResults.sumOf { it.servicesAcuteCoronaryDiseaseExists }.toInt(),
                            // 脑卒中管理人数
                            patientResults.sumOf { it.cerebralStrokeDiseaseExists }.toInt(),
                            // 脑卒中服务包订阅人数
                            patientResults.sumOf { it.servicesCerebralStrokeDiseaseExists }.toInt(),
                            // 慢阻肺管理人数
                            patientResults.sumOf { it.copdDiseaseExists }.toInt(),
                            // 慢阻肺服务包订阅人数
                            patientResults.sumOf { it.servicesCopdDiseaseExists }.toInt()
                        )
                    )
                }
            var index = 0
            excelDataRows1.add(
                listOf(
                    "合计",
                    *(excelDataRows1[0] as List<*>).subList(1, excelDataRows1[0].size).map {
                        index++
                        excelDataRows1.subList(0, excelDataRows1.size).sumOf { r -> r[index].toString().toInt() }
                    }.toTypedArray()
                )
            )
            val sheet1 = ExcelSheetTable(
                "区域或机构",
                ExcelHeaderNode(null, ExcelHeaderNode("区域/机构"), *commonHeaderGenerator),
                excelDataRows1
            )
            dataSource.add(sheet1)
        }
        //统一使用性别年龄划分sheet
        val excelDataRows2 = mutableListOf<List<Any>>()
        val patientGenderAgeResults = dao.findHospitalManagerPatientGenderAge(params.regionCode, params.hospitalIds)
        //男
        excelDataRows2.add(
            listOf(
                "男",
                //管理总人数
                patientGenderAgeResults.filter { it.knGender == "MAN" }.sumOf { it.count },
                // 高血压管理人数
                patientGenderAgeResults.filter { it.knGender == "MAN" }
                    .sumOf { it.hypertensionDiseaseExists18 + it.hypertensionDiseaseExists44 + it.hypertensionDiseaseExists59 + it.hypertensionDiseaseExists79 + it.hypertensionDiseaseExists80 }
                    .toInt(),
                // 高血压服务包订阅人数
                patientGenderAgeResults.filter { it.knGender == "MAN" }
                    .sumOf { it.servicesHypertensionDiseaseExists18 + it.servicesHypertensionDiseaseExists44 + it.servicesHypertensionDiseaseExists59 + it.servicesHypertensionDiseaseExists79 + it.servicesHypertensionDiseaseExists80 }
                    .toInt(),
                // 糖尿病管理人数
                patientGenderAgeResults.filter { it.knGender == "MAN" }
                    .sumOf { it.diabetesDiseaseExists18 + it.diabetesDiseaseExists44 + it.diabetesDiseaseExists59 + it.diabetesDiseaseExists79 + it.diabetesDiseaseExists80 }
                    .toInt(),
                // 糖尿病服务包订阅人数
                patientGenderAgeResults.filter { it.knGender == "MAN" }
                    .sumOf { it.servicesDiabetesDiseaseExists18 + it.servicesDiabetesDiseaseExists44 + it.servicesDiabetesDiseaseExists59 + it.servicesDiabetesDiseaseExists79 + it.servicesDiabetesDiseaseExists80 }
                    .toInt(),
                // 冠心病管理人数
                patientGenderAgeResults.filter { it.knGender == "MAN" }
                    .sumOf { it.acuteCoronaryDiseaseExists18 + it.acuteCoronaryDiseaseExists44 + it.acuteCoronaryDiseaseExists59 + it.acuteCoronaryDiseaseExists79 + it.acuteCoronaryDiseaseExists80 }
                    .toInt(),
                // 冠心病服务包订阅人数
                patientGenderAgeResults.filter { it.knGender == "MAN" }
                    .sumOf { it.servicesAcuteCoronaryDiseaseExists18 + it.servicesAcuteCoronaryDiseaseExists44 + it.servicesAcuteCoronaryDiseaseExists59 + it.servicesAcuteCoronaryDiseaseExists79 + it.servicesAcuteCoronaryDiseaseExists80 }
                    .toInt(),
                // 脑卒中管理人数
                patientGenderAgeResults.filter { it.knGender == "MAN" }
                    .sumOf { it.cerebralStrokeDiseaseExists18 + it.cerebralStrokeDiseaseExists44 + it.cerebralStrokeDiseaseExists59 + it.cerebralStrokeDiseaseExists79 + it.cerebralStrokeDiseaseExists80 }
                    .toInt(),
                // 脑卒中服务包订阅人数
                patientGenderAgeResults.filter { it.knGender == "MAN" }
                    .sumOf { it.servicesCerebralStrokeDiseaseExists18 + it.servicesCerebralStrokeDiseaseExists44 + it.servicesCerebralStrokeDiseaseExists59 + it.servicesCerebralStrokeDiseaseExists79 + it.servicesCerebralStrokeDiseaseExists80 }
                    .toInt(),
                // 慢阻肺管理人数
                patientGenderAgeResults.filter { it.knGender == "MAN" }
                    .sumOf { it.copdDiseaseExists18 + it.copdDiseaseExists44 + it.copdDiseaseExists59 + it.copdDiseaseExists79 + it.copdDiseaseExists80 }
                    .toInt(),
                // 慢阻肺服务包订阅人数
                patientGenderAgeResults.filter { it.knGender == "MAN" }
                    .sumOf { it.servicesCopdDiseaseExists18 + it.servicesCopdDiseaseExists44 + it.servicesCopdDiseaseExists59 + it.servicesCopdDiseaseExists79 + it.servicesCopdDiseaseExists80 }
                    .toInt()
            )
        )
        //女
        excelDataRows2.add(
            listOf(
                "女",
                //管理总人数
                patientGenderAgeResults.filter { it.knGender == "WOMAN" }.sumOf { it.count },
                // 高血压管理人数
                patientGenderAgeResults.filter { it.knGender == "WOMAN" }
                    .sumOf { it.hypertensionDiseaseExists18 + it.hypertensionDiseaseExists44 + it.hypertensionDiseaseExists59 + it.hypertensionDiseaseExists79 + it.hypertensionDiseaseExists80 }
                    .toInt(),
                // 高血压服务包订阅人数
                patientGenderAgeResults.filter { it.knGender == "WOMAN" }
                    .sumOf { it.servicesHypertensionDiseaseExists18 + it.servicesHypertensionDiseaseExists44 + it.servicesHypertensionDiseaseExists59 + it.servicesHypertensionDiseaseExists79 + it.servicesHypertensionDiseaseExists80 }
                    .toInt(),
                // 糖尿病管理人数
                patientGenderAgeResults.filter { it.knGender == "WOMAN" }
                    .sumOf { it.diabetesDiseaseExists18 + it.diabetesDiseaseExists44 + it.diabetesDiseaseExists59 + it.diabetesDiseaseExists79 + it.diabetesDiseaseExists80 }
                    .toInt(),
                // 糖尿病服务包订阅人数
                patientGenderAgeResults.filter { it.knGender == "WOMAN" }
                    .sumOf { it.servicesDiabetesDiseaseExists18 + it.servicesDiabetesDiseaseExists44 + it.servicesDiabetesDiseaseExists59 + it.servicesDiabetesDiseaseExists79 + it.servicesDiabetesDiseaseExists80 }
                    .toInt(),
                // 冠心病管理人数
                patientGenderAgeResults.filter { it.knGender == "WOMAN" }
                    .sumOf { it.acuteCoronaryDiseaseExists18 + it.acuteCoronaryDiseaseExists44 + it.acuteCoronaryDiseaseExists59 + it.acuteCoronaryDiseaseExists79 + it.acuteCoronaryDiseaseExists80 }
                    .toInt(),
                // 冠心病服务包订阅人数
                patientGenderAgeResults.filter { it.knGender == "WOMAN" }
                    .sumOf { it.servicesAcuteCoronaryDiseaseExists18 + it.servicesAcuteCoronaryDiseaseExists44 + it.servicesAcuteCoronaryDiseaseExists59 + it.servicesAcuteCoronaryDiseaseExists79 + it.servicesAcuteCoronaryDiseaseExists80 }
                    .toInt(),
                // 脑卒中管理人数
                patientGenderAgeResults.filter { it.knGender == "WOMAN" }
                    .sumOf { it.cerebralStrokeDiseaseExists18 + it.cerebralStrokeDiseaseExists44 + it.cerebralStrokeDiseaseExists59 + it.cerebralStrokeDiseaseExists79 + it.cerebralStrokeDiseaseExists80 }
                    .toInt(),
                // 脑卒中服务包订阅人数
                patientGenderAgeResults.filter { it.knGender == "WOMAN" }
                    .sumOf { it.servicesCerebralStrokeDiseaseExists18 + it.servicesCerebralStrokeDiseaseExists44 + it.servicesCerebralStrokeDiseaseExists59 + it.servicesCerebralStrokeDiseaseExists79 + it.servicesCerebralStrokeDiseaseExists80 }
                    .toInt(),
                // 慢阻肺管理人数
                patientGenderAgeResults.filter { it.knGender == "WOMAN" }
                    .sumOf { it.copdDiseaseExists18 + it.copdDiseaseExists44 + it.copdDiseaseExists59 + it.copdDiseaseExists79 + it.copdDiseaseExists80 }
                    .toInt(),
                // 慢阻肺服务包订阅人数
                patientGenderAgeResults.filter { it.knGender == "WOMAN" }
                    .sumOf { it.servicesCopdDiseaseExists18 + it.servicesCopdDiseaseExists44 + it.servicesCopdDiseaseExists59 + it.servicesCopdDiseaseExists79 + it.servicesCopdDiseaseExists80 }
                    .toInt()
            )
        )
        //小于18
        excelDataRows2.add(
            listOf(
                "<18岁",
                //管理总人数
                patientGenderAgeResults.sumOf { it.count18 }.toInt(),
                // 高血压管理人数
                patientGenderAgeResults.sumOf { it.hypertensionDiseaseExists18 }.toInt(),
                // 高血压服务包订阅人数
                patientGenderAgeResults.sumOf { it.servicesHypertensionDiseaseExists18 }.toInt(),
                // 糖尿病管理人数
                patientGenderAgeResults.sumOf { it.diabetesDiseaseExists18 }.toInt(),
                // 糖尿病服务包订阅人数
                patientGenderAgeResults.sumOf { it.servicesDiabetesDiseaseExists18 }.toInt(),
                // 冠心病管理人数
                patientGenderAgeResults.sumOf { it.acuteCoronaryDiseaseExists18 }.toInt(),
                // 冠心病服务包订阅人数
                patientGenderAgeResults.sumOf { it.servicesAcuteCoronaryDiseaseExists18 }.toInt(),
                // 脑卒中管理人数
                patientGenderAgeResults.sumOf { it.cerebralStrokeDiseaseExists18 }.toInt(),
                // 脑卒中服务包订阅人数
                patientGenderAgeResults.sumOf { it.servicesCerebralStrokeDiseaseExists18 }.toInt(),
                // 慢阻肺管理人数
                patientGenderAgeResults.sumOf { it.copdDiseaseExists18 }.toInt(),
                // 慢阻肺服务包订阅人数
                patientGenderAgeResults.sumOf { it.servicesCopdDiseaseExists18 }.toInt()
            )
        )
        //18-44
        excelDataRows2.add(
            listOf(
                "18-44岁",
                //管理总人数
                patientGenderAgeResults.sumOf { it.count44 }.toInt(),
                // 高血压管理人数
                patientGenderAgeResults.sumOf { it.hypertensionDiseaseExists44 }.toInt(),
                // 高血压服务包订阅人数
                patientGenderAgeResults.sumOf { it.servicesHypertensionDiseaseExists44 }.toInt(),
                // 糖尿病管理人数
                patientGenderAgeResults.sumOf { it.diabetesDiseaseExists44 }.toInt(),
                // 糖尿病服务包订阅人数
                patientGenderAgeResults.sumOf { it.servicesDiabetesDiseaseExists44 }.toInt(),
                // 冠心病管理人数
                patientGenderAgeResults.sumOf { it.acuteCoronaryDiseaseExists44 }.toInt(),
                // 冠心病服务包订阅人数
                patientGenderAgeResults.sumOf { it.servicesAcuteCoronaryDiseaseExists44 }.toInt(),
                // 脑卒中管理人数
                patientGenderAgeResults.sumOf { it.cerebralStrokeDiseaseExists44 }.toInt(),
                // 脑卒中服务包订阅人数
                patientGenderAgeResults.sumOf { it.servicesCerebralStrokeDiseaseExists44 }.toInt(),
                // 慢阻肺管理人数
                patientGenderAgeResults.sumOf { it.copdDiseaseExists44 }.toInt(),
                // 慢阻肺服务包订阅人数
                patientGenderAgeResults.sumOf { it.servicesCopdDiseaseExists44 }.toInt()
            )
        )
        //45-59
        excelDataRows2.add(
            listOf(
                "45-59岁",
                //管理总人数
                patientGenderAgeResults.sumOf { it.count59 }.toInt(),
                // 高血压管理人数
                patientGenderAgeResults.sumOf { it.hypertensionDiseaseExists59 }.toInt(),
                // 高血压服务包订阅人数
                patientGenderAgeResults.sumOf { it.servicesHypertensionDiseaseExists59 }.toInt(),
                // 糖尿病管理人数
                patientGenderAgeResults.sumOf { it.diabetesDiseaseExists59 }.toInt(),
                // 糖尿病服务包订阅人数
                patientGenderAgeResults.sumOf { it.servicesDiabetesDiseaseExists59 }.toInt(),
                // 冠心病管理人数
                patientGenderAgeResults.sumOf { it.acuteCoronaryDiseaseExists59 }.toInt(),
                // 冠心病服务包订阅人数
                patientGenderAgeResults.sumOf { it.servicesAcuteCoronaryDiseaseExists59 }.toInt(),
                // 脑卒中管理人数
                patientGenderAgeResults.sumOf { it.cerebralStrokeDiseaseExists59 }.toInt(),
                // 脑卒中服务包订阅人数
                patientGenderAgeResults.sumOf { it.servicesCerebralStrokeDiseaseExists59 }.toInt(),
                // 慢阻肺管理人数
                patientGenderAgeResults.sumOf { it.copdDiseaseExists59 }.toInt(),
                // 慢阻肺服务包订阅人数
                patientGenderAgeResults.sumOf { it.servicesCopdDiseaseExists59 }.toInt()
            )
        )
        //60-79
        excelDataRows2.add(
            listOf(
                "60-79岁",
                //管理总人数
                patientGenderAgeResults.sumOf { it.count79 }.toInt(),
                // 高血压管理人数
                patientGenderAgeResults.sumOf { it.hypertensionDiseaseExists79 }.toInt(),
                // 高血压服务包订阅人数
                patientGenderAgeResults.sumOf { it.servicesHypertensionDiseaseExists79 }.toInt(),
                // 糖尿病管理人数
                patientGenderAgeResults.sumOf { it.diabetesDiseaseExists79 }.toInt(),
                // 糖尿病服务包订阅人数
                patientGenderAgeResults.sumOf { it.servicesDiabetesDiseaseExists79 }.toInt(),
                // 冠心病管理人数
                patientGenderAgeResults.sumOf { it.acuteCoronaryDiseaseExists79 }.toInt(),
                // 冠心病服务包订阅人数
                patientGenderAgeResults.sumOf { it.servicesAcuteCoronaryDiseaseExists79 }.toInt(),
                // 脑卒中管理人数
                patientGenderAgeResults.sumOf { it.cerebralStrokeDiseaseExists79 }.toInt(),
                // 脑卒中服务包订阅人数
                patientGenderAgeResults.sumOf { it.servicesCerebralStrokeDiseaseExists79 }.toInt(),
                // 慢阻肺管理人数
                patientGenderAgeResults.sumOf { it.copdDiseaseExists79 }.toInt(),
                // 慢阻肺服务包订阅人数
                patientGenderAgeResults.sumOf { it.servicesCopdDiseaseExists79 }.toInt()
            )
        )
        //大于等于80
        excelDataRows2.add(
            listOf(
                ">=80岁",
                //管理总人数
                patientGenderAgeResults.sumOf { it.count80 }.toInt(),
                // 高血压管理人数
                patientGenderAgeResults.sumOf { it.hypertensionDiseaseExists80 }.toInt(),
                // 高血压服务包订阅人数
                patientGenderAgeResults.sumOf { it.servicesHypertensionDiseaseExists80 }.toInt(),
                // 糖尿病管理人数
                patientGenderAgeResults.sumOf { it.diabetesDiseaseExists80 }.toInt(),
                // 糖尿病服务包订阅人数
                patientGenderAgeResults.sumOf { it.servicesDiabetesDiseaseExists80 }.toInt(),
                // 冠心病管理人数
                patientGenderAgeResults.sumOf { it.acuteCoronaryDiseaseExists80 }.toInt(),
                // 冠心病服务包订阅人数
                patientGenderAgeResults.sumOf { it.servicesAcuteCoronaryDiseaseExists80 }.toInt(),
                // 脑卒中管理人数
                patientGenderAgeResults.sumOf { it.cerebralStrokeDiseaseExists80 }.toInt(),
                // 脑卒中服务包订阅人数
                patientGenderAgeResults.sumOf { it.servicesCerebralStrokeDiseaseExists80 }.toInt(),
                // 慢阻肺管理人数
                patientGenderAgeResults.sumOf { it.copdDiseaseExists80 }.toInt(),
                // 慢阻肺服务包订阅人数
                patientGenderAgeResults.sumOf { it.servicesCopdDiseaseExists80 }.toInt()
            )
        )
        var index = 0
        excelDataRows2.add(
            listOf(
                "总计",
                *(excelDataRows2[0] as List<*>).subList(1, excelDataRows2[0].size).map {
                    index++
                    excelDataRows2.subList(0, 2).sumOf { r -> r[index].toString().toInt() }
                }.toTypedArray()
            )
        )
        val sheet2 = ExcelSheetTable(
            "人群画像",
            ExcelHeaderNode(null, ExcelHeaderNode("统计维度"), *commonHeaderGenerator),
            excelDataRows2
        )
        dataSource.add(sheet2)
        val hospitalRows = params.rows.firstOrNull { it.id == hospitalOrRegion && it.type == "org" }
        //使用医生统计sheet
        if (hospitalRows != null) {
            val excelDataRows3 = mutableListOf<List<Any>>()
            val physicianPatientResult = dao.findHospitalPhysicianPatient(listOf(hospitalOrRegion))
            physicianPatientResult.forEach { row ->
                excelDataRows3.add(
                    listOf(
                        row.knDoctorName,
                        //管理总人数
                        row.count.toInt(),
                        // 高血压管理人数
                        row.hypertensionDiseaseExists.toInt(),
                        // 高血压服务包订阅人数
                        row.servicesHypertensionDiseaseExists.toInt(),
                        // 糖尿病管理人数
                        row.diabetesDiseaseExists.toInt(),
                        // 糖尿病服务包订阅人数
                        row.servicesDiabetesDiseaseExists.toInt(),
                        // 冠心病管理人数
                        row.acuteCoronaryDiseaseExists.toInt(),
                        // 冠心病服务包订阅人数
                        row.servicesAcuteCoronaryDiseaseExists.toInt(),
                        // 脑卒中管理人数
                        row.cerebralStrokeDiseaseExists.toInt(),
                        // 脑卒中服务包订阅人数
                        row.servicesCerebralStrokeDiseaseExists.toInt(),
                        // 慢阻肺管理人数
                        row.copdDiseaseExists.toInt(),
                        // 慢阻肺服务包订阅人数
                        row.servicesCopdDiseaseExists.toInt()
                    )
                )
            }
            var index = 0
            excelDataRows3.add(
                listOf(
                    "合计",
                    *(excelDataRows3[0] as List<*>).subList(1, excelDataRows3[0].size).map {
                        index++
                        excelDataRows3.subList(0, excelDataRows3.size).sumOf { r -> r[index].toString().toInt() }
                    }.toTypedArray()
                )
            )
            val sheet3 = ExcelSheetTable(
                hospitalRows.name ?: "机构医生",
                ExcelHeaderNode(null, ExcelHeaderNode("机构医生"), *commonHeaderGenerator),
                excelDataRows3
            )
            dataSource.add(sheet3)
        }
        exportExcel("服务包数量统计", dataSource)
        return
    }

    override fun statisticPeopleUserGet(id: String?) {
        val hospitalOrRegion = if (id.isNullOrEmpty()) {
            if (AppSecurityUtil.jwtUser()?.isSuperAdmin() == true) cnRegionCode
            else (getCurrentUserHospitalOrRegion() ?: return)
        } else id.toBigInteger()
        //excel标题行
        val excelHeader = mutableListOf<String>()
        excelHeader.add("区域/机构")
        val hospitalRows = dao.findHospitalSelfAndChildren(hospitalOrRegion)
        //当请求数据为医院，无cdc的显示
        if (hospitalRows.isEmpty())
            excelHeader.add("卫健委/CDC人员数量")
        excelHeader.addAll(
            listOf(
                "医务人员数量",
                "医生数量",
                "住院医师",
                "主治医师",
                "副主任医师",
                "主任医师",
                "护士数量",
                "初级护士",
                "初级护师",
                "主管护师",
                "副主任护师",
                "主任护师"
            )
        )
        //解析参数，区分行政区域与机构
        val params = parseParameters(hospitalOrRegion)
        //获取机构角色用户数量
        val orgRoleUserList = dao.roleUser(params.regionCode, params.hospitalIds)
        //获取医护人员数量
        val medicalStaff = dao.medicalStaff(params.regionCode, params.hospitalIds)
        val excelDataRows = mutableListOf<List<Any>>()
        excelDataRows.add(excelHeader)
        params.rows
            .filter {
                if (params.isRegionAccess == true && id.isNullOrEmpty()) it.id == hospitalOrRegion
                else if (params.isRegionAccess == true && !id.isNullOrEmpty()) it.id != hospitalOrRegion
                else true
            }
            .forEach { rh ->
                //获取当前维度的角色用户数量、医护用户数量
                var roleData: List<StatisticPeopleDao.RoleUserResult> = listOf()
                var medicalStaffData: List<StatisticPeopleDao.MedicalStaffResult> = listOf()
                if (rh.type == "org") {
                    roleData = orgRoleUserList.filter { it.knHospitalId == rh.id }
                    medicalStaffData = medicalStaff.filter { it.knHospitalId == rh.id }
                } else {
                    if (id.isNullOrEmpty()) {
                        roleData = orgRoleUserList
                        medicalStaffData = medicalStaff
                    } else {
                        roleData = orgRoleUserList.filter {
                            val regions = it.regionPath.split(".")
                            if (regions.size < 3) false
                            else
                                regions[2] == rh.id.toString()
                        }
                        medicalStaffData = medicalStaff.filter {
                            val regions = it.regionPath.split(".")
                            if (regions.size < 3) false
                            else
                                regions[2] == rh.id.toString()
                        }
                    }
                }
                //处理excel数据
                val excelRow = mutableListOf<Any>()
                excelRow.add(rh.name ?: rh.id)
                if (excelHeader.contains("卫健委/CDC人员数量")) excelRow.add(roleData.sumOf { or -> or.region.toInt() })
                //医护
                excelRow.add(roleData.sumOf { or -> or.medical.toInt() })
                //医生
                excelRow.add(roleData.sumOf { or -> or.physician.toInt() })
                excelRow.add(medicalStaffData.filter { ms -> ms.knDoctorLevel == "RESIDENT_PHYSICIAN" }
                    .sumOf { or -> or.count })
                excelRow.add(medicalStaffData.filter { ms -> ms.knDoctorLevel == "IN_CHARGE_PHYSICIAN" }
                    .sumOf { or -> or.count })
                excelRow.add(medicalStaffData.filter { ms -> ms.knDoctorLevel == "ASSOCIATE_CHIEF_PHYSICIAN" }
                    .sumOf { or -> or.count })
                excelRow.add(medicalStaffData.filter { ms -> ms.knDoctorLevel == "CHIEF_PHYSICIAN" }
                    .sumOf { or -> or.count })
                //护士
                excelRow.add(roleData.sumOf { or -> or.nurse.toInt() })
                excelRow.add(medicalStaffData.filter { ms -> ms.knDoctorLevel == "JUNIOR_NURSE" }
                    .sumOf { or -> or.count })
                excelRow.add(medicalStaffData.filter { ms -> ms.knDoctorLevel == "JUNIOR_SENIORNURSE" }
                    .sumOf { or -> or.count })
                excelRow.add(medicalStaffData.filter { ms -> ms.knDoctorLevel == "CHARGE_SENIORNURSE" }
                    .sumOf { or -> or.count })
                excelRow.add(medicalStaffData.filter { ms -> ms.knDoctorLevel == "ASSOCIATE_CHIEF_SENIORNURSE" }
                    .sumOf { or -> or.count })
                excelRow.add(medicalStaffData.filter { ms -> ms.knDoctorLevel == "CHIEF_SENIORNURSE" }
                    .sumOf { or -> or.count })
                excelDataRows.add(excelRow)
            }
        var index = 0
        excelDataRows.add(
            listOf(
                "合计",
                *(excelDataRows[0] as List<*>).subList(1, excelDataRows[0].size).map {
                    index++
                    excelDataRows.subList(1, excelDataRows.size).sumOf { r -> r[index].toString().toInt() }
                }.toTypedArray()
            )
        )
        exportExcel("用户统计", listOf(ExcelSheetTable("用户统计", null, excelDataRows)))
        return
    }

    fun exportExcel(fileName: String, tables: List<ExcelSheetTable>) {
        val response = getResponse() ?: return
        response.contentType = "application/vnd.ms-excel;charset=utf-8"
        response.setHeader(
            HttpHeaders.CONTENT_DISPOSITION,
            "attachment;filename=${URLEncoder.encode("$fileName.xls", "utf-8")}"
        )
        val outputStream = response.outputStream
        ExcelDataSource(*tables.toTypedArray()).writeTo(outputStream)
        outputStream.close()
    }

    private fun getCurrentUserHospitalOrRegion(): BigInteger? {
        val user = AppSecurityUtil.jwtUser()
        return user?.regionIdSet?.firstOrNull() ?: user?.orgIdSet?.firstOrNull()
    }

    data class RegionHospitalDataClass(
        val rows: List<FindRegionOrHospitalResult>,
        val regionCode: BigInteger?,
        val hospitalIds: List<BigInteger>?,
        /**
         * 是否区域权限
         * null为未知，true为区域id，false为机构id
         */
        val isRegionAccess: Boolean?
    )

    private fun parseParameters(hospitalOrRegion: BigInteger?): RegionHospitalDataClass {
        val nextRegionHospitalList = dao.findRegionOrHospital(hospitalOrRegion)
        val hospitalIds =
            if (nextRegionHospitalList.any { it.type == "region" }) null else nextRegionHospitalList.filter { it.type == "org" }
                .map { it.id }
        val regionCode = if (nextRegionHospitalList.any { it.type == "region" }) hospitalOrRegion else null
        var access: Boolean? = null
        if (nextRegionHospitalList.size > 0) {
            access = nextRegionHospitalList.any { it.type == "region" }
        }
        return RegionHospitalDataClass(nextRegionHospitalList, regionCode, hospitalIds, access)
    }
}
