package com.bjknrt.health.indicator.api

import com.bjknrt.doctor.patient.management.vo.Gender
import com.bjknrt.doctor.patient.management.vo.PatientInfoResponse
import com.bjknrt.framework.api.vo.PagedResult
import com.bjknrt.framework.util.AppIdUtil
import com.bjknrt.health.indicator.AbstractContainerBaseTest
import com.bjknrt.health.indicator.utils.*
import com.bjknrt.health.indicator.vo.*
import com.bjknrt.health.scheme.api.ClockInApi
import com.bjknrt.health.scheme.vo.ClockInRequest
import com.bjknrt.health.scheme.vo.HealthPlanType
import com.bjknrt.wechat.service.api.PatientApi
import com.bjknrt.wechat.service.vo.IndicatorNotify
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.mockito.kotlin.capture
import org.mockito.kotlin.times
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext
import java.math.BigDecimal
import java.math.BigInteger
import java.nio.charset.Charset
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters

@AutoConfigureMockMvc
class IndicatorTest : AbstractContainerBaseTest() {

    @Autowired
    lateinit var api: IndicatorApi

    @Autowired
    lateinit var webApplicationContext: WebApplicationContext

    @Autowired
    lateinit var objectMapper: ObjectMapper

    lateinit var mvc: MockMvc

    @MockBean
    lateinit var clockInRpcService: ClockInApi

    @MockBean
    lateinit var notifyRpcService: PatientApi

    @MockBean
    lateinit var patientRpcService: com.bjknrt.doctor.patient.management.api.PatientApi

    val patientId = AppIdUtil.nextId()

    @BeforeEach
    fun before() {
        //获取mockmvc对象实例
        mvc = MockMvcBuilders
            .webAppContextSetup(webApplicationContext)
            .defaultResponseCharacterEncoding<DefaultMockMvcBuilder>(Charset.forName("UTF-8"))
            .build()
    }

    /**
     * To test IndicatorApi.addBMI
     */
    @Test
    @Transactional
    fun addBMITest() {
        Mockito.reset(notifyRpcService)
        val addBMIParam = AddBMIParam(
            knPatientId = patientId,
            knBmi = BigDecimal.valueOf(29.0),
            fromTag = FromTag.PATIENT_SELF,
            knMeasureAt = LocalDateTime.now().withDayOfMonth(1)
        )

        // test addBMI
        mvc.perform(
            MockMvcRequestBuilders.post("/indicator/addBMI")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(addBMIParam)
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)

        //拦截bmi指标异常预警推送参数，进行验证
        this.indicatorAlertNotifyVerify(
            BMI_NAME,
            listOf(Indicator(indicatorName = BMI_NAME, indicatorValue = addBMIParam.knBmi.setScale(1), unit = KG_M2)),
            addBMIParam.knPatientId,
            BMI_EXCEPTION_MESSAGE,
            BMI_EXCEPTION_MESSAGE_FAT_TAG
        )

        //  test bMIList
        val (startTime: LocalDateTime, endTime: LocalDateTime) = getCurrentMonthStartAndEndTime()

        val findBMIListParam = FindListParam(startTime, endTime, patientId)

        val result = api.bMIList(findBMIListParam)

        Assertions.assertEquals(1, result.size)
        Assertions.assertEquals(addBMIParam.knBmi, result[0].knBmi)

        mvc.perform(
            MockMvcRequestBuilders.post("/indicator/bMIList")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(findBMIListParam)
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(result)))

        //  test bMIPageList
        val findBMIPageListParam = FindPageListParam(patientId, 1, 10)

        val pageResult = api.bMIPageList(findBMIPageListParam)

        Assertions.assertEquals(1, pageResult.total)
        Assertions.assertEquals(addBMIParam.knBmi, pageResult._data?.get(0)?.knBmi)

        mvc.perform(
            MockMvcRequestBuilders.post("/indicator/bMIPageList")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(findBMIPageListParam)
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(pageResult)))

    }

    /**
     * To test IndicatorApi.addBloodLipids
     */
    @Test
    @Transactional
    fun addBloodLipidsTest() {
        Mockito.reset(notifyRpcService)
        val addBloodLipidsParam = AddBloodLipidsParam(
            knPatientId = patientId,
            fromTag = FromTag.PATIENT_SELF,
            knTotalCholesterol = BigDecimal.valueOf(10.0),
            knTriglycerides = BigDecimal.valueOf(10.0),
            knLowDensityLipoprotein = BigDecimal.valueOf(1.0),
            knHighDensityLipoprotein = BigDecimal.valueOf(2.0),
            knMeasureAt = LocalDateTime.now().withDayOfMonth(2)
        )

        // test addBloodLipids
        mvc.perform(
            MockMvcRequestBuilders.post("/indicator/addBloodLipids")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(addBloodLipidsParam)
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)

        //拦截血脂指标异常预警推送参数，进行验证
        this.indicatorAlertNotifyVerify(
            BLOOD_LIPID_NAME,
            listOf(
                Indicator(
                    indicatorName = TOTAL_CHOLESTEROL_NAME,
                    indicatorValue = addBloodLipidsParam.knTotalCholesterol ?: BigDecimal.valueOf(10.0),
                    unit = M_MOL_L
                )
            ),
            addBloodLipidsParam.knPatientId,
            BLOOD_LIPID_EXCEPTION_MESSAGE
        )

        // test bloodLipidsList
        val (startTime: LocalDateTime, endTime: LocalDateTime) = getCurrentMonthStartAndEndTime()

        val findBloodLipidsListParam = FindListParam(startTime, endTime, patientId)

        val result = api.bloodLipidsList(findBloodLipidsListParam)

        Assertions.assertTrue(result.isNotEmpty())
        Assertions.assertEquals(addBloodLipidsParam.knTotalCholesterol, result[0].knTotalCholesterol)
        Assertions.assertEquals(addBloodLipidsParam.knTriglycerides, result[0].knTriglycerides)
        Assertions.assertEquals(addBloodLipidsParam.knLowDensityLipoprotein, result[0].knLowDensityLipoprotein)
        Assertions.assertEquals(addBloodLipidsParam.knHighDensityLipoprotein, result[0].knHighDensityLipoprotein)

        mvc.perform(
            MockMvcRequestBuilders.post("/indicator/bloodLipidsList")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(findBloodLipidsListParam)
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(result)))

        // test bloodLipidsPageList
        val findBloodLipidsPageListParam = FindPageListParam(patientId, 1, 10)

        val pageResult = api.bloodLipidsPageList(findBloodLipidsPageListParam)

        Assertions.assertTrue(pageResult.total >= 1)
        Assertions.assertEquals(addBloodLipidsParam.knTotalCholesterol, pageResult._data?.get(0)?.knTotalCholesterol)
        Assertions.assertEquals(addBloodLipidsParam.knTriglycerides, pageResult._data?.get(0)?.knTriglycerides)
        Assertions.assertEquals(
            addBloodLipidsParam.knLowDensityLipoprotein,
            pageResult._data?.get(0)?.knLowDensityLipoprotein
        )
        Assertions.assertEquals(
            addBloodLipidsParam.knHighDensityLipoprotein,
            pageResult._data?.get(0)?.knHighDensityLipoprotein
        )


        mvc.perform(
            MockMvcRequestBuilders.post("/indicator/bloodLipidsPageList")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(findBloodLipidsPageListParam)
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(pageResult)))


    }

    /**
     * To test IndicatorApi.addBloodPressure
     */
    @Test
    @Transactional
    fun addBloodPressureTest() {
        Mockito.reset(notifyRpcService)
        Mockito.reset(patientRpcService)
        Mockito.reset(clockInRpcService)
        val addBloodPressureParam = AddBloodPressureParam(
            knPatientId = patientId,
            fromTag = FromTag.PATIENT_SELF,
            knSystolicBloodPressure = BigDecimal.valueOf(150.0),
            knDiastolicBloodPressure = BigDecimal.valueOf(80.0),
            knMeasureAt = LocalDateTime.now().withDayOfMonth(3)
        )

        // 测血压打卡注释
        val clockInRequest = ClockInRequest(
            addBloodPressureParam.knPatientId,
            HealthPlanType.BLOOD_PRESSURE_MEASUREMENT,
            LocalDateTime.now()
        )
        Mockito.doNothing().`when`(clockInRpcService).saveClockIn(clockInRequest)

        //mock患者信息
        mockPatientInfo(addBloodPressureParam.knPatientId)

        // test addBloodPressure
        mvc.perform(
            MockMvcRequestBuilders.post("/indicator/addBloodPressure")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(addBloodPressureParam)
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)

        //拦截患者信息参数，进行验证
        captorPatientInfo(addBloodPressureParam.knPatientId)

        // 拦截血压打卡参数进行验证
        val captor: ArgumentCaptor<ClockInRequest> = ArgumentCaptor.forClass(ClockInRequest::class.java)
        Mockito.verify(clockInRpcService).saveClockIn(capture(captor))
        Assertions.assertEquals(clockInRequest.patientId, captor.value.patientId)

        //拦截血压指标异常预警推送参数，进行验证
        this.indicatorAlertNotifyVerify(
            BLOOD_PRESSURE_NAME,
            listOf(
                Indicator(
                    indicatorName = SYSTOLIC_BLOOD_PRESSURE_NAME,
                    indicatorValue = addBloodPressureParam.knSystolicBloodPressure,
                    unit = MM_HG
                )
            ),
            addBloodPressureParam.knPatientId,
            BLOOD_PRESSURE_ABOVE_CONTROL_TARGET_EXCEPTION_MESSAGE,
            BLOOD_PRESSURE_ALERT_RULE_ABOVE_CONTROL_TARGET_TAG
        )

        // test bloodPressureList
        val (startTime: LocalDateTime, endTime: LocalDateTime) = getCurrentMonthStartAndEndTime()

        val findBloodPressureListParam = FindListParam(startTime, endTime, patientId)

        val result = api.bloodPressureList(findBloodPressureListParam)

        Assertions.assertTrue(result.isNotEmpty())
        Assertions.assertEquals(addBloodPressureParam.knSystolicBloodPressure, result[0].knSystolicBloodPressure)

        mvc.perform(
            MockMvcRequestBuilders.post("/indicator/bloodPressureList")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(findBloodPressureListParam)
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(result)))

        // test bloodPressurePageList
        val findBloodPressurePageListParam = FindPageListParam(patientId, 1, 10)

        val pageResult = api.bloodPressurePageList(findBloodPressurePageListParam)

        Assertions.assertTrue(pageResult.total >= 1)
        Assertions.assertEquals(
            addBloodPressureParam.knSystolicBloodPressure,
            pageResult._data?.get(0)?.knSystolicBloodPressure
        )

        mvc.perform(
            MockMvcRequestBuilders.post("/indicator/bloodPressurePageList")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(findBloodPressurePageListParam)
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(pageResult)))
    }

    /**
     * To test IndicatorApi.addBloodSugar
     */
    @Test
    @Transactional
    fun addBloodSugarTest() {
        Mockito.reset(notifyRpcService)
        Mockito.reset(patientRpcService)
        Mockito.reset(clockInRpcService)
        val addBloodSugarParam =
            AddBloodSugarParam(
                knPatientId = patientId,
                fromTag = FromTag.PATIENT_SELF,
                knFastingBloodSandalwood = BigDecimal.valueOf(8.0),
                knBeforeLunchBloodSugar = null,
                knBeforeDinnerBloodSugar = null,
                knAfterMealBloodSugar = null,
                knAfterLunchBloodSugar = null,
                knAfterDinnerBloodSugar = null,
                knBeforeSleepBloodSugar = null,
                knRandomBloodSugar = null,
                knMeasureAt = LocalDateTime.now().withDayOfMonth(4)
            )

        //模拟患者信息
        mockPatientInfo(addBloodSugarParam.knPatientId)

        //测试调用血糖添加接口
        mvc.perform(
            MockMvcRequestBuilders.post("/indicator/addBloodSugar")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(addBloodSugarParam)
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)

        //拦截患者信息参数，进行验证
        captorPatientInfo(addBloodSugarParam.knPatientId)

        //拦截mock空腹血糖打卡参数进行验证
        captorClockRequest(addBloodSugarParam.knPatientId, HealthPlanType.FASTING_BLOOD_GLUCOSE)

        //拦截血糖指标异常预警推送参数，进行验证
        this.indicatorAlertNotifyVerify(
            BLOOD_SUGAR_NAME,
            listOf(
                Indicator(
                    indicatorName = FASTING_BLOOD_SUGAR_NAME,
                    indicatorValue = addBloodSugarParam.knFastingBloodSandalwood ?: BigDecimal.valueOf(8.0),
                    unit = M_MOL_L
                )
            ),
            addBloodSugarParam.knPatientId,
            BLOOD_SUGAR_ABOVE_CONTROL_TARGET_EXCEPTION_MESSAGE,
            BLOOD_SUGAR_ALERT_RULE_ABOVE_CONTROL_STANDARD_TAG
        )

        // test bloodSugarList
        val (startTime: LocalDateTime, endTime: LocalDateTime) = getCurrentMonthStartAndEndTime()

        val findBloodSugarListParam = FindListParam(startTime, endTime, patientId)

        val result = api.bloodSugarList(findBloodSugarListParam)

        Assertions.assertTrue(result.isNotEmpty())
        Assertions.assertEquals(addBloodSugarParam.knFastingBloodSandalwood, result[0].knFastingBloodSandalwood)
        Assertions.assertEquals(addBloodSugarParam.knBeforeLunchBloodSugar, result[0].knBeforeLunchBloodSugar)
        Assertions.assertEquals(addBloodSugarParam.knBeforeDinnerBloodSugar, result[0].knBeforeDinnerBloodSugar)
        Assertions.assertEquals(addBloodSugarParam.knAfterMealBloodSugar, result[0].knAfterMealBloodSugar)
        Assertions.assertEquals(addBloodSugarParam.knAfterLunchBloodSugar, result[0].knAfterLunchBloodSugar)
        Assertions.assertEquals(addBloodSugarParam.knAfterDinnerBloodSugar, result[0].knAfterDinnerBloodSugar)
        Assertions.assertEquals(addBloodSugarParam.knBeforeSleepBloodSugar, result[0].knBeforeSleepBloodSugar)
        Assertions.assertEquals(addBloodSugarParam.knRandomBloodSugar, result[0].knRandomBloodSugar)

        mvc.perform(
            MockMvcRequestBuilders.post("/indicator/bloodSugarList")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(findBloodSugarListParam)
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(result)))

        // TODO: test bloodSugarPageList
        val findBloodSugarPageListParam = FindPageListParam(patientId, 1, 10)

        val pageResult = api.bloodSugarPageList(findBloodSugarPageListParam)

        Assertions.assertTrue(pageResult.total >= 1)
        Assertions.assertEquals(
            addBloodSugarParam.knFastingBloodSandalwood,
            pageResult._data?.get(0)?.knFastingBloodSandalwood
        )
        Assertions.assertEquals(
            addBloodSugarParam.knBeforeLunchBloodSugar,
            pageResult._data?.get(0)?.knBeforeLunchBloodSugar
        )
        Assertions.assertEquals(
            addBloodSugarParam.knBeforeDinnerBloodSugar,
            pageResult._data?.get(0)?.knBeforeDinnerBloodSugar
        )
        Assertions.assertEquals(
            addBloodSugarParam.knAfterMealBloodSugar,
            pageResult._data?.get(0)?.knAfterMealBloodSugar
        )
        Assertions.assertEquals(
            addBloodSugarParam.knAfterLunchBloodSugar,
            pageResult._data?.get(0)?.knAfterLunchBloodSugar
        )
        Assertions.assertEquals(
            addBloodSugarParam.knAfterDinnerBloodSugar,
            pageResult._data?.get(0)?.knAfterDinnerBloodSugar
        )
        Assertions.assertEquals(
            addBloodSugarParam.knBeforeSleepBloodSugar,
            pageResult._data?.get(0)?.knBeforeSleepBloodSugar
        )
        Assertions.assertEquals(
            addBloodSugarParam.knRandomBloodSugar,
            pageResult._data?.get(0)?.knRandomBloodSugar
        )

        mvc.perform(
            MockMvcRequestBuilders.post("/indicator/bloodSugarPageList")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(findBloodSugarPageListParam)
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(pageResult)))
    }

    /**
     * To test IndicatorApi.addBodyHeight
     */
    @Test
    @Transactional
    fun addBodyHeightTest() {
        Mockito.reset(notifyRpcService)
        // test addBodyHeight
        val addBodyHeightParam = AddBodyHeightParam(
            knPatientId = patientId,
            knBodyHeight = BigDecimal.valueOf(1.0),
            fromTag = FromTag.PATIENT_SELF,
            knMeasureAt = LocalDateTime.now().withDayOfMonth(5)
        )

        // add addBodyWeight for calculate bmi
        val addBodyWeightParam = AddBodyWeightParam(
            knPatientId = patientId,
            knBodyWeight = BigDecimal.ONE,
            fromTag = FromTag.PATIENT_SELF,
            knMeasureAt = LocalDateTime.now().withDayOfMonth(6)
        )
        mvc.perform(
            MockMvcRequestBuilders.post("/indicator/addBodyWeight")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(addBodyWeightParam)
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)
        val weightIndicator = patientRecentlyValidIndicatorResult(IndicatorEnum.BODY_WEIGHT)
        val bmiValue = getBmiValue(addBodyHeightParam.knBodyHeight, weightIndicator[0]._value)

        Mockito.reset(notifyRpcService)

        mvc.perform(
            MockMvcRequestBuilders.post("/indicator/addBodyHeight")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(addBodyHeightParam)
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)

        //拦截bmi指标异常预警推送参数，进行验证
        this.indicatorAlertNotifyVerify(
            BMI_NAME,
            listOf(
                Indicator(
                    indicatorName = BMI_NAME,
                    indicatorValue = bmiValue.setScale(1),
                    unit = KG_M2
                )
            ),
            addBodyHeightParam.knPatientId,
            BMI_EXCEPTION_MESSAGE,
            BMI_EXCEPTION_MESSAGE_FAT_TAG
        )

        // test bodyHeightList
        val (startTime: LocalDateTime, endTime: LocalDateTime) = getCurrentMonthStartAndEndTime()

        val findBodyHeightListParam = FindListParam(startTime, endTime, patientId)

        val result = api.bodyHeightList(findBodyHeightListParam)

        Assertions.assertTrue(result.isNotEmpty())
        Assertions.assertEquals(addBodyHeightParam.knBodyHeight, result[0].knBodyHeight)

        mvc.perform(
            MockMvcRequestBuilders.post("/indicator/bodyHeightList")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(findBodyHeightListParam)
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(result)))

        // test bodyHeightPageList
        val findBodyHeightPageListParam = FindPageListParam(patientId, 1, 10)

        val pageResult = api.bodyHeightPageList(findBodyHeightPageListParam)

        Assertions.assertEquals(1, pageResult.total)
        Assertions.assertEquals(addBodyHeightParam.knBodyHeight, pageResult._data?.get(0)?.knBodyHeight)

        mvc.perform(
            MockMvcRequestBuilders.post("/indicator/bodyHeightPageList")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(findBodyHeightPageListParam)
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(pageResult)))


    }

    /**
     * To test IndicatorApi.addBodyWeight
     */
    @Test
    @Transactional
    fun addBodyWeightTest() {
        Mockito.reset(notifyRpcService)
        val addBodyWeightParam = AddBodyWeightParam(
            knPatientId = patientId,
            knBodyWeight = BigDecimal.valueOf(1.0),
            fromTag = FromTag.PATIENT_SELF,
            knMeasureAt = LocalDateTime.now().withDayOfMonth(6)
        )

        // add addBodyHeight for calculate bmi
        val addBodyHeightParam = AddBodyHeightParam(
            knPatientId = patientId,
            knBodyHeight = BigDecimal.ONE,
            fromTag = FromTag.PATIENT_SELF,
            knMeasureAt = LocalDateTime.now().withDayOfMonth(5)
        )
        mvc.perform(
            MockMvcRequestBuilders.post("/indicator/addBodyHeight")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(addBodyHeightParam)
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)

        Mockito.reset(notifyRpcService)

        val heightIndicator = patientRecentlyValidIndicatorResult(IndicatorEnum.BODY_HEIGHT)
        val bmiValue = getBmiValue(heightIndicator[0]._value, addBodyWeightParam.knBodyWeight)

        // test addBodyWeight
        mvc.perform(
            MockMvcRequestBuilders.post("/indicator/addBodyWeight")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(addBodyWeightParam)
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)

        //拦截bmi指标异常预警推送参数，进行验证
        this.indicatorAlertNotifyVerify(
            BMI_NAME,
            listOf(
                Indicator(
                    indicatorName = BMI_NAME,
                    indicatorValue = bmiValue.setScale(1),
                    unit = KG_M2
                )
            ),
            addBodyWeightParam.knPatientId,
            BMI_EXCEPTION_MESSAGE,
            BMI_EXCEPTION_MESSAGE_FAT_TAG
        )

        // test bodyWeightList
        val (startTime: LocalDateTime, endTime: LocalDateTime) = getCurrentMonthStartAndEndTime()

        val findBodyWeightListParam = FindListParam(startTime, endTime, patientId)

        val result = api.bodyWeightList(findBodyWeightListParam)

        Assertions.assertTrue(result.isNotEmpty())
        Assertions.assertEquals(addBodyWeightParam.knBodyWeight, result[0].knBodyWeight)

        mvc.perform(
            MockMvcRequestBuilders.post("/indicator/bodyWeightList")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(findBodyWeightListParam)
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(result)))

        // test bodyWeightPageList
        val findBodyWeightPageListParam = FindPageListParam(patientId, 1, 10)

        val pageResult = api.bodyWeightPageList(findBodyWeightPageListParam)

        Assertions.assertTrue(pageResult.total >= 1)
        Assertions.assertEquals(addBodyWeightParam.knBodyWeight, pageResult._data?.get(0)?.knBodyWeight)

        mvc.perform(
            MockMvcRequestBuilders.post("/indicator/bodyWeightPageList")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(findBodyWeightPageListParam)
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(pageResult)))
    }

    /**
     * To test IndicatorApi.addDrinking
     */
    @Test
    @Transactional
    fun addDrinkingTest() {
        val addDrinkingParam =
            AddDrinkingParam(
                knPatientId = patientId,
                knBeer = BigDecimal.ONE,
                knWhiteSpirit = BigDecimal.TEN,
                knWine = BigDecimal.ZERO,
                knYellowRichSpirit = BigDecimal.ZERO,
                fromTag = FromTag.PATIENT_SELF,
                knMeasureAt = LocalDateTime.now().withDayOfMonth(7)
            )

        // TODO: test addDrinking
        mvc.perform(
            MockMvcRequestBuilders.post("/indicator/addDrinking")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(addDrinkingParam)
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)

        // TODO: test drinkingList
        val (startTime: LocalDateTime, endTime: LocalDateTime) = getCurrentMonthStartAndEndTime()

        val findDrinkingListParam = FindListParam(startTime, endTime, patientId)

        val result = api.drinkingList(findDrinkingListParam)

        Assertions.assertEquals(1, result.size)
        Assertions.assertEquals(BigDecimal.valueOf(1.0), result[0].knBeer)

        mvc.perform(
            MockMvcRequestBuilders.post("/indicator/drinkingList")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(findDrinkingListParam)
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(result)))

        // TODO: test drinkingPageList
        val findDrinkingPageListParam = FindPageListParam(patientId, 1, 10)

        val pageResult = api.drinkingPageList(findDrinkingPageListParam)

        Assertions.assertEquals(1, pageResult.total)
        Assertions.assertEquals(BigDecimal.valueOf(1.0), pageResult._data?.get(0)?.knBeer)

        mvc.perform(
            MockMvcRequestBuilders.post("/indicator/drinkingPageList")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(findDrinkingPageListParam)
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(pageResult)))
    }

    /**
     * To test IndicatorApi.addHeartRate
     */
    @Test
    @Transactional
    fun addHeartRateTest() {
        Mockito.reset(notifyRpcService)
        val addHeartRateParam = AddHeartRateParam(
            knPatientId = patientId,
            knHeartRate = 50,
            fromTag = FromTag.PATIENT_SELF,
            knMeasureAt = LocalDateTime.now().withDayOfMonth(8)
        )

        // test addHeartRate
        mvc.perform(
            MockMvcRequestBuilders.post("/indicator/addHeartRate")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(addHeartRateParam)
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)

        //拦截心率指标异常预警推送参数，进行验证
        this.indicatorAlertNotifyVerify(
            HEART_RATE_NAME,
            listOf(
                Indicator(
                    indicatorName = HEART_RATE_NAME,
                    indicatorValue = addHeartRateParam.knHeartRate.toBigDecimal().setScale(1),
                    unit = MINUTES_TIMES
                )
            ),
            addHeartRateParam.knPatientId,
            HEART_RATE_EXCEPTION_MESSAGE,
            HEART_RATE_ALERT_RULE_TOO_SLOW_TAG
        )

        // test heartRateList
        val (startTime: LocalDateTime, endTime: LocalDateTime) = getCurrentMonthStartAndEndTime()

        val findHeartRateListParam = FindListParam(startTime, endTime, patientId)

        val result = api.heartRateList(findHeartRateListParam)

        Assertions.assertTrue(result.isNotEmpty())
        Assertions.assertEquals(addHeartRateParam.knHeartRate, result[0].knHeartRate)

        mvc.perform(
            MockMvcRequestBuilders.post("/indicator/heartRateList")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(findHeartRateListParam)
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(result)))

        // test heartRatePageList
        val findHeartRatePageListParam = FindPageListParam(patientId, 1, 10)

        val pageResult = api.heartRatePageList(findHeartRatePageListParam)

        Assertions.assertTrue(pageResult.total >= 1)
        Assertions.assertEquals(addHeartRateParam.knHeartRate, pageResult._data?.get(0)?.knHeartRate)

        mvc.perform(
            MockMvcRequestBuilders.post("/indicator/heartRatePageList")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(findHeartRatePageListParam)
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(pageResult)))
    }

    /**
     * To test IndicatorApi.addPulse
     */
    @Test
    @Transactional
    fun addPulseTest() {
        Mockito.reset(notifyRpcService)
        val addPulseParam = AddPulseParam(
            knPatientId = patientId,
            knPulse = 50,
            fromTag = FromTag.PATIENT_SELF,
            knMeasureAt = LocalDateTime.now().withDayOfMonth(9)
        )

        // test addPulse
        mvc.perform(
            MockMvcRequestBuilders.post("/indicator/addPulse")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(addPulseParam)
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)

        //拦截脉搏指标异常预警推送参数，进行验证
        this.indicatorAlertNotifyVerify(
            PULSE_NAME,
            listOf(
                Indicator(
                    indicatorName = PULSE_NAME,
                    indicatorValue = addPulseParam.knPulse.toBigDecimal().setScale(1),
                    unit = MINUTES_TIMES
                )
            ),
            addPulseParam.knPatientId,
            PULSE_EXCEPTION_MESSAGE,
            PULSE_ALERT_RULE_TOO_SLOW_TAG
        )

        // test pulseList
        val (startTime: LocalDateTime, endTime: LocalDateTime) = getCurrentMonthStartAndEndTime()

        val findPulseListParam = FindListParam(startTime, endTime, patientId)

        val result = api.pulseList(findPulseListParam)

        Assertions.assertTrue(result.isNotEmpty())
        Assertions.assertEquals(addPulseParam.knPulse, result[0].knPulse)

        mvc.perform(
            MockMvcRequestBuilders.post("/indicator/pulseList")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(findPulseListParam)
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(result)))

        // test pulsePageList
        val findPulsePageListParam = FindPageListParam(patientId, 1, 10)

        val pageResult = api.pulsePageList(findPulsePageListParam)

        Assertions.assertTrue(pageResult.total >= 1)
        Assertions.assertEquals(addPulseParam.knPulse, pageResult._data?.get(0)?.knPulse)

        mvc.perform(
            MockMvcRequestBuilders.post("/indicator/pulsePageList")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(findPulsePageListParam)
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(pageResult)))
    }

    /**
     * To test IndicatorApi.addSmoke
     */
    @Test
    @Transactional
    fun addSmokeTest() {
        val addSmokeParam = AddSmokeParam(
            knPatientId = patientId,
            knNum = 1,
            fromTag = FromTag.PATIENT_SELF,
            knMeasureAt = LocalDateTime.now().withDayOfMonth(10)
        )

        // TODO: test addSmoke
        mvc.perform(
            MockMvcRequestBuilders.post("/indicator/addSmoke")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(addSmokeParam)
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)

        // TODO: test smokeList
        val (startTime: LocalDateTime, endTime: LocalDateTime) = getCurrentMonthStartAndEndTime()

        val findSmokeListParam = FindListParam(startTime, endTime, patientId)

        val result = api.smokeList(findSmokeListParam)

        Assertions.assertEquals(1, result.size)
        Assertions.assertEquals(1, result[0].knNum)

        mvc.perform(
            MockMvcRequestBuilders.post("/indicator/smokeList")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(findSmokeListParam)
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(result)))

        // TODO: test smokePageList
        val findSmokePageListParam = FindPageListParam(patientId, 1, 10)

        val pageResult = api.smokePageList(findSmokePageListParam)

        Assertions.assertEquals(1, pageResult.total)
        Assertions.assertEquals(1, pageResult._data?.get(0)?.knNum)

        mvc.perform(
            MockMvcRequestBuilders.post("/indicator/smokePageList")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(findSmokePageListParam)
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(pageResult)))
    }

    /**
     * To test IndicatorApi.addWaistline
     */
    @Test
    @Transactional
    fun addWaistlineTest() {
        Mockito.reset(patientRpcService)
        Mockito.reset(notifyRpcService)
        val addWaistlineParam = AddWaistlineParam(
            knPatientId = patientId,
            knWaistline = BigDecimal.valueOf(95.0),
            fromTag = FromTag.PATIENT_SELF,
            knMeasureAt = LocalDateTime.now().withDayOfMonth(11)
        )

        //mock获取患者信息
        mockPatientInfo(addWaistlineParam.knPatientId)

        // test addWaistline
        mvc.perform(
            MockMvcRequestBuilders.post("/indicator/addWaistline")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(addWaistlineParam)
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)

        //拦截患者信息参数，进行验证
        captorPatientInfo(addWaistlineParam.knPatientId)

        //拦截腰围指标异常预警推送参数，进行验证
        this.indicatorAlertNotifyVerify(
            WAISTLINE_NAME,
            listOf(
                Indicator(
                    indicatorName = WAISTLINE_NAME,
                    indicatorValue = addWaistlineParam.knWaistline,
                    unit = CM
                )
            ),
            addWaistlineParam.knPatientId,
            WAISTLINE_EXCEPTION_MESSAGE,
            WAISTLINE_ALERT_RULE_OVERWEIGHT_TAG
        )

        // test waistLineList
        val (startTime: LocalDateTime, endTime: LocalDateTime) = getCurrentMonthStartAndEndTime()

        val findWaistLineListParam = FindListParam(startTime, endTime, patientId)

        val result = api.waistLineList(findWaistLineListParam)

        Assertions.assertEquals(1, result.size)
        Assertions.assertEquals(addWaistlineParam.knWaistline, result[0].knWaistline)

        mvc.perform(
            MockMvcRequestBuilders.post("/indicator/waistLineList")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(findWaistLineListParam)
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(result)))

        // test waistLinePageList
        val findWaistLinePageListParam = FindPageListParam(patientId, 1, 10)

        val pageResult = api.waistLinePageList(findWaistLinePageListParam)

        Assertions.assertEquals(1, pageResult.total)
        Assertions.assertEquals(addWaistlineParam.knWaistline, pageResult._data?.get(0)?.knWaistline)

        mvc.perform(
            MockMvcRequestBuilders.post("/indicator/waistLinePageList")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(findWaistLinePageListParam)
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(pageResult)))
    }

    /**
     * To test IndicatorApi.addBodyTemperature
     */
    @Test
    @Transactional
    fun addBodyTemperatureTest() {
        Mockito.reset(notifyRpcService)
        val addBodyTemperatureParam = AddBodyTemperatureParam(
            knPatientId = patientId,
            knCelsius = BigDecimal.valueOf(37.5),
            fromTag = FromTag.PATIENT_SELF,
            knMeasureAt = LocalDateTime.now().withDayOfMonth(12)
        )

        // test addBodyTemperature
        mvc.perform(
            MockMvcRequestBuilders.post("/indicator/addBodyTemperature")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(addBodyTemperatureParam)
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)

        //拦截脉搏指标异常预警推送参数，进行验证
        this.indicatorAlertNotifyVerify(
            BODY_TEMPERATURE_NAME,
            listOf(
                Indicator(
                    indicatorName = BODY_TEMPERATURE_NAME,
                    indicatorValue = addBodyTemperatureParam.knCelsius,
                    unit = O_C
                )
            ),
            addBodyTemperatureParam.knPatientId,
            BODY_TEMPERATURE_EXCEPTION_MESSAGE,
            BODY_TEMPERATURE_ALERT_RULE_LOW_HEAT_TAG
        )

        // test bodyTemperatureList
        val (startTime: LocalDateTime, endTime: LocalDateTime) = getCurrentMonthStartAndEndTime()

        val findBodyTemperatureListParam = FindListParam(startTime, endTime, patientId)

        val result = api.bodyTemperatureList(findBodyTemperatureListParam)

        Assertions.assertTrue(result.isNotEmpty())
        Assertions.assertEquals(addBodyTemperatureParam.knCelsius, result[0].knCelsius)

        mvc.perform(
            MockMvcRequestBuilders.post("/indicator/bodyTemperatureList")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(findBodyTemperatureListParam)
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(result)))

        // test bodyTemperaturePageList
        val findBodyTemperaturePageListParam = FindPageListParam(patientId, 1, 10)

        val pageResult = api.bodyTemperaturePageList(findBodyTemperaturePageListParam)

        Assertions.assertTrue(pageResult.total >= 1)
        Assertions.assertEquals(addBodyTemperatureParam.knCelsius, pageResult._data?.get(0)?.knCelsius)

        mvc.perform(
            MockMvcRequestBuilders.post("/indicator/bodyTemperaturePageList")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(findBodyTemperaturePageListParam)
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(pageResult)))
    }

    /**
     * To test IndicatorApi.addPulseOximetry
     */
    @Test
    @Transactional
    fun addPulseOximetryTest() {
        Mockito.reset(clockInRpcService)
        Mockito.reset(notifyRpcService)
        val addPulseOximetryParam = AddPulseOximetryParam(
            knPatientId = patientId,
            knPulseOximetry = BigDecimal.valueOf(1.0),
            fromTag = FromTag.PATIENT_SELF,
            knMeasureAt = LocalDateTime.now().withDayOfMonth(13)
        )

        // test addPulseOximetry
        mvc.perform(
            MockMvcRequestBuilders.post("/indicator/addPulseOximetry")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(addPulseOximetryParam)
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)

        // 拦截mock打卡参数进行验证
        captorClockRequest(addPulseOximetryParam.knPatientId, HealthPlanType.PULSE_OXYGEN_SATURATION_PLAN)

        val captor2: ArgumentCaptor<IndicatorNotify> = ArgumentCaptor.forClass(IndicatorNotify::class.java)
        Mockito.verify(notifyRpcService).patientNotifyIndicatorWarningPost(capture(captor2))
        Assertions.assertEquals(addPulseOximetryParam.knPatientId, captor2.value.patientId)
        Assertions.assertEquals(PULSE_OXYGEN_NAME, captor2.value.name)
        Assertions.assertEquals(addPulseOximetryParam.knPulseOximetry, captor2.value.indicators[0].value)
        Assertions.assertEquals(
            LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES),
            captor2.value.date.truncatedTo(ChronoUnit.MINUTES)
        )
        Assertions.assertEquals(PULSE_OXYGEN_EXCEPTION_MESSAGE, captor2.value.message)

        //  test pulseOximetryList
        val (startTime: LocalDateTime, endTime: LocalDateTime) = getCurrentMonthStartAndEndTime()

        val findPulseOximetryListParam = FindListParam(startTime, endTime, patientId)

        val result = api.pulseOximetryList(findPulseOximetryListParam)
        val format = DecimalFormat("#0.0000")
        Assertions.assertEquals(1, result.size)
        Assertions.assertEquals(format.format(1).toBigDecimal(), result[0].knPulseOximetry)

        mvc.perform(
            MockMvcRequestBuilders.post("/indicator/pulseOximetryList")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(findPulseOximetryListParam)
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(result)))

        //  test pulseOximetryPageList
        val findPulseOximetryPageListParam = FindPageListParam(patientId, 1, 10)

        val pageResult = api.pulseOximetryPageList(findPulseOximetryPageListParam)

        Assertions.assertEquals(1, pageResult.total)
        Assertions.assertEquals(format.format(1).toBigDecimal(), pageResult._data?.get(0)?.knPulseOximetry)

        mvc.perform(
            MockMvcRequestBuilders.post("/indicator/pulseOximetryPageList")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(findPulseOximetryPageListParam)
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(pageResult)))
    }

    /**
     * To test IndicatorApi.selectIndicatorByPatientId
     */
    @Test
    fun selectIndicatorByPatientId() {
        //准备批量添加指标的入参
        val batchAddIndicatorParam = BatchIndicator(
            patientId = patientId,
            knSystolicBloodPressure = BigDecimal.valueOf(10.0),
            knDiastolicBloodPressure = BigDecimal.valueOf(10.0),
            fromTag = FromTag.PATIENT_SELF,
            knBodyHeight = BigDecimal.valueOf(180.0),
            knBodyWeight = BigDecimal.valueOf(62.0),
            knBmi = BigDecimal.valueOf(10.0),
            knWaistline = BigDecimal.valueOf(10.0),
            knFastingBloodSandalwood = BigDecimal.valueOf(10.0),
            knNum = 10,
            knTotalCholesterol = BigDecimal.valueOf(10.0),
            knTriglycerides = BigDecimal.valueOf(10.0),
            knHighDensityLipoprotein = BigDecimal.valueOf(10.0),
            knLowDensityLipoprotein = BigDecimal.valueOf(10.0),
            knWhiteSpirit = BigDecimal.valueOf(10.0),
            knPulseOximetry = BigDecimal.valueOf(10.0),
            knHeartRate = 1,
            knPulse = 2,
            knCelsius = BigDecimal.valueOf(10.0)
        )
        //mock患者信息
        this.mockPatientInfo(batchAddIndicatorParam.patientId)

        api.batchAddIndicator(batchAddIndicatorParam)

        //拦截患者信息参数，进行验证（批量添加接口中目前有腰围，血压，血糖模拟患者）
        captorPatientInfo(batchAddIndicatorParam.patientId, 3)

        val selectPatientIndicatorParam = SelectPatientIndicatorParam(patientId)
        val result = api.selectIndicatorbyPatientId(selectPatientIndicatorParam)

        Assertions.assertEquals(batchAddIndicatorParam.knBodyWeight, result.knBodyWeight)
        Assertions.assertEquals(batchAddIndicatorParam.knBodyHeight, result.knBodyHeight)
        Assertions.assertEquals(batchAddIndicatorParam.knWaistline, result.knWaistline)
        Assertions.assertEquals(batchAddIndicatorParam.knDiastolicBloodPressure, result.knDiastolicBloodPressure)
        Assertions.assertEquals(batchAddIndicatorParam.knSystolicBloodPressure, result.knSystolicBloodPressure)
        Assertions.assertEquals(batchAddIndicatorParam.knFastingBloodSandalwood, result.knFastingBloodSandalwood)
        Assertions.assertEquals(batchAddIndicatorParam.knNum, result.knNum)
        Assertions.assertEquals(batchAddIndicatorParam.knTotalCholesterol, result.knTotalCholesterol)
        Assertions.assertEquals(batchAddIndicatorParam.knTriglycerides, result.knTriglycerides)
        Assertions.assertEquals(batchAddIndicatorParam.knHighDensityLipoprotein, result.knHighDensityLipoprotein)
        Assertions.assertEquals(batchAddIndicatorParam.knLowDensityLipoprotein, result.knLowDensityLipoprotein)
        Assertions.assertEquals(batchAddIndicatorParam.knWhiteSpirit, result.knWhiteSpirit)
        val format = DecimalFormat("#0.0000")
        Assertions.assertEquals(format.format(10).toBigDecimal(), result.knPulseOximetry)
        Assertions.assertEquals(batchAddIndicatorParam.knHeartRate, result.knHeartRate)
        Assertions.assertEquals(batchAddIndicatorParam.knPulse, result.knPulse)
        Assertions.assertEquals(batchAddIndicatorParam.knCelsius, result.knCelsius)

        // test validations
        mvc.perform(
            MockMvcRequestBuilders.post("/indicator/selectIndicatorByPatientId")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(selectPatientIndicatorParam)
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(result)))
    }

    /**
     * To test IndicatorApi.selectAnyIndicatorListForDpm
     */
    @Test
    @Transactional
    fun selectAnyIndicatorListForDpm() {
        //准备批量添加接口的入参
        val batchAddIndicatorParam = BatchIndicator(
            patientId = patientId,
            knSystolicBloodPressure = BigDecimal.valueOf(10.0),
            knDiastolicBloodPressure = BigDecimal.valueOf(10.0),
            fromTag = FromTag.PATIENT_SELF,
            knBodyHeight = BigDecimal.valueOf(10.0),
            knBodyWeight = BigDecimal.valueOf(10.0),
            knBmi = BigDecimal.valueOf(10.0),
            knWaistline = BigDecimal.valueOf(10.0),
            knFastingBloodSandalwood = BigDecimal.valueOf(10.0),
            knNum = 10
        )
        //mock患者信息
        this.mockPatientInfo(batchAddIndicatorParam.patientId)

        api.batchAddIndicator(batchAddIndicatorParam)

        //拦截患者信息参数，进行验证（批量添加接口中目前有腰围，血压，血糖模拟患者）
        captorPatientInfo(batchAddIndicatorParam.patientId, 3)

        val (startTime: LocalDateTime, endTime: LocalDateTime) = getCurrentMonthStartAndEndTime()

        val findListParam = FindListParam(startTime, endTime, patientId)

        val result = api.selectAnyIndicatorListForDpm(findListParam)

        Assertions.assertEquals(1, result.bodyHeightListResult.size)
        Assertions.assertEquals(batchAddIndicatorParam.knBodyHeight, result.bodyHeightListResult[0].knBodyHeight)

        Assertions.assertEquals(1, result.bodyWeightListResult.size)
        Assertions.assertEquals(batchAddIndicatorParam.knBodyWeight, result.bodyWeightListResult[0].knBodyWeight)

        Assertions.assertEquals(1, result.knWaistlineListResult.size)
        Assertions.assertEquals(batchAddIndicatorParam.knWaistline, result.knWaistlineListResult[0].knWaistline)

        Assertions.assertEquals(1, result.bloodSugarListResult.size)
        Assertions.assertEquals(
            batchAddIndicatorParam.knFastingBloodSandalwood,
            result.bloodSugarListResult[0].knFastingBloodSandalwood
        )

        Assertions.assertEquals(1, result.bloodPressureListResult.size)
        Assertions.assertEquals(
            batchAddIndicatorParam.knSystolicBloodPressure,
            result.bloodPressureListResult[0].knSystolicBloodPressure
        )

        Assertions.assertEquals(1, result.smokeListResult.size)
        Assertions.assertEquals(batchAddIndicatorParam.knNum, result.smokeListResult[0].knNum)

        // TODO: test validations
        mvc.perform(
            MockMvcRequestBuilders.post("/indicator/selectAnyIndicatorListForDpm")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    objectMapper.writeValueAsString(findListParam)
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(result)))
    }

    /**
     * To test IndicatorApi.selectLeastIndicatorMeasureAtByPatientId
     */
    @Test
    @Transactional
    fun selectLeastIndicatorMeasureAtByPatientId() {
        addBMITest()
        addBloodLipidsTest()
        addBloodPressureTest()
        addBloodSugarTest()
        addBodyHeightTest()
        addBodyWeightTest()
        addDrinkingTest()
        addHeartRateTest()
        addPulseTest()
        addSmokeTest()
        addWaistlineTest()
        addBodyTemperatureTest()
        addPulseOximetryTest()

        val (startTime: LocalDateTime, endTime: LocalDateTime) = getCurrentMonthStartAndEndTime()
        val findBodyTemperatureListParam = FindListParam(startTime, endTime, patientId)
        val result = api.pulseOximetryList(findBodyTemperatureListParam)

        val leastMeasureAt = api.selectLeastIndicatorMeasureAtByPatientId(patientId)

        Assertions.assertEquals(result[0].knMeasureAt, leastMeasureAt.measureDateTime)
    }

    /**
     * To test IndicatorApi.recentlyValidIndicatorPageListByEnumType
     */
    @Test
    @Transactional
    fun recentlyValidIndicatorPageListByEnumType() {
        addBMITest()
        addBloodLipidsTest()
        addBloodPressureTest()
        addBloodSugarTest()
        addBodyHeightTest()
        addBodyWeightTest()
        addDrinkingTest()
        addHeartRateTest()
        addPulseTest()
        addSmokeTest()
        addWaistlineTest()
        addBodyTemperatureTest()
        addPulseOximetryTest()

        val bodyHeightListResult = patientRecentlyValidIndicatorResult(IndicatorEnum.BODY_HEIGHT)
        val bodyHeightPageResult = indicatorPagedResultByIndicatorEnum(IndicatorEnum.BODY_HEIGHT)
        Assertions.assertTrue(bodyHeightPageResult.total >= 1)
        Assertions.assertEquals(bodyHeightListResult[0]._value, bodyHeightPageResult._data?.get(0)?._value)

        val bodyWeightListResult = patientRecentlyValidIndicatorResult(IndicatorEnum.BODY_WEIGHT)
        val bodyWeightPageResult = indicatorPagedResultByIndicatorEnum(IndicatorEnum.BODY_WEIGHT)
        Assertions.assertTrue(bodyWeightPageResult.total >= 1)
        Assertions.assertEquals(bodyWeightListResult[0]._value, bodyWeightPageResult._data?.get(0)?._value)

        val bmiListResult = patientRecentlyValidIndicatorResult(IndicatorEnum.BMI)
        val bmiPageResult = indicatorPagedResultByIndicatorEnum(IndicatorEnum.BMI)
        Assertions.assertTrue(bmiPageResult.total >= 1)
        Assertions.assertEquals(bmiListResult[0]._value, bmiPageResult._data?.get(0)?._value)

        val waistListResult = patientRecentlyValidIndicatorResult(IndicatorEnum.WAISTLINE)
        val waistPageResult = indicatorPagedResultByIndicatorEnum(IndicatorEnum.WAISTLINE)
        Assertions.assertTrue(waistPageResult.total >= 1)
        Assertions.assertEquals(waistListResult[0]._value, waistPageResult._data?.get(0)?._value)

        val bodyTemperatureListResult = patientRecentlyValidIndicatorResult(IndicatorEnum.BODY_TEMPERATURE)
        val bodyTemperaturePageResult = indicatorPagedResultByIndicatorEnum(IndicatorEnum.BODY_TEMPERATURE)
        Assertions.assertTrue(bodyTemperaturePageResult.total >= 1)
        Assertions.assertEquals(bodyTemperatureListResult[0]._value, bodyTemperaturePageResult._data?.get(0)?._value)

        val systolicListResult = patientRecentlyValidIndicatorResult(IndicatorEnum.SYSTOLIC_BLOOD_PRESSURE)
        val systolicPageResult = indicatorPagedResultByIndicatorEnum(IndicatorEnum.SYSTOLIC_BLOOD_PRESSURE)
        Assertions.assertTrue(systolicPageResult.total >= 1)
        Assertions.assertEquals(systolicListResult[0]._value, systolicPageResult._data?.get(0)?._value)

        val diastolicListResult = patientRecentlyValidIndicatorResult(IndicatorEnum.DIASTOLIC_BLOOD_PRESSURE)
        val diastolicPageResult = indicatorPagedResultByIndicatorEnum(IndicatorEnum.DIASTOLIC_BLOOD_PRESSURE)
        Assertions.assertTrue(diastolicPageResult.total >= 1)
        Assertions.assertEquals(
            diastolicListResult[0]._value,
            diastolicPageResult._data?.get(0)?._value
        )

        val fastingListResult = patientRecentlyValidIndicatorResult(IndicatorEnum.FASTING_BLOOD_SUGAR)
        val fastingPageResult = indicatorPagedResultByIndicatorEnum(IndicatorEnum.FASTING_BLOOD_SUGAR)
        Assertions.assertTrue(fastingPageResult.total >= 1)
        Assertions.assertEquals(fastingListResult[0]._value, fastingPageResult._data?.get(0)?._value)

        /* 目前需求只允许填写一个血糖值，所以这部分断言暂时注释
         val beforeLunchListResult = patientRecentlyValidIndicatorResult(IndicatorEnum.BEFORE_LUNCH_BLOOD_SUGAR)
          val beforeLunchPageResult = indicatorPagedResultByIndicatorEnum(IndicatorEnum.BEFORE_LUNCH_BLOOD_SUGAR)
          Assertions.assertTrue(beforeLunchPageResult.total >= 1)
          Assertions.assertEquals(
              beforeLunchListResult[0]._value,
              beforeLunchPageResult._data?.get(0)?._value
          )

          val beforeDinnerListResult = patientRecentlyValidIndicatorResult(IndicatorEnum.BEFORE_DINNER_BLOOD_SUGAR)
          val beforeDinnerPageResult = indicatorPagedResultByIndicatorEnum(IndicatorEnum.BEFORE_DINNER_BLOOD_SUGAR)
          Assertions.assertTrue(beforeDinnerPageResult.total >= 1)
          Assertions.assertEquals(
              beforeDinnerListResult[0]._value,
              beforeDinnerPageResult._data?.get(0)?._value
          )

          val randomSugarListResult = patientRecentlyValidIndicatorResult(IndicatorEnum.RANDOM_BLOOD_SUGAR)
          val randomSugarPageResult = indicatorPagedResultByIndicatorEnum(IndicatorEnum.RANDOM_BLOOD_SUGAR)
          Assertions.assertTrue(randomSugarPageResult.total >= 1)
          Assertions.assertEquals(
              randomSugarListResult[0]._value,
              randomSugarPageResult._data?.get(0)?._value
          )

          val afterMealListResult = patientRecentlyValidIndicatorResult(IndicatorEnum.AFTER_MEAL_BLOOD_SUGAR)
          val afterMealPageResult = indicatorPagedResultByIndicatorEnum(IndicatorEnum.AFTER_MEAL_BLOOD_SUGAR)
          Assertions.assertTrue(afterMealPageResult.total >= 1)
          Assertions.assertEquals(afterMealListResult[0]._value, afterMealPageResult._data?.get(0)?._value)

          val afterLunchListResult = patientRecentlyValidIndicatorResult(IndicatorEnum.AFTER_LUNCH_BLOOD_SUGAR)
          val afterLunchLPageResult = indicatorPagedResultByIndicatorEnum(IndicatorEnum.AFTER_LUNCH_BLOOD_SUGAR)
          Assertions.assertTrue(afterLunchLPageResult.total >= 1)
          Assertions.assertEquals(
              afterLunchListResult[0]._value,
              afterLunchLPageResult._data?.get(0)?._value
          )

          val afterDinnerListResult = patientRecentlyValidIndicatorResult(IndicatorEnum.AFTER_DINNER_BLOOD_SUGAR)
          val afterDinnerLPageResult = indicatorPagedResultByIndicatorEnum(IndicatorEnum.AFTER_DINNER_BLOOD_SUGAR)
          Assertions.assertTrue(afterDinnerLPageResult.total >= 1)
          Assertions.assertEquals(
              afterDinnerListResult[0]._value,
              afterDinnerLPageResult._data?.get(0)?._value
          )

          val beforeSleepListResult = patientRecentlyValidIndicatorResult(IndicatorEnum.BEFORE_SLEEP_BLOOD_SUGAR)
          val beforeSleepLPageResult = indicatorPagedResultByIndicatorEnum(IndicatorEnum.BEFORE_SLEEP_BLOOD_SUGAR)
          Assertions.assertTrue(beforeSleepLPageResult.total >= 1)
          Assertions.assertEquals(
              beforeSleepListResult[0]._value,
              beforeSleepLPageResult._data?.get(0)?._value
          )*/

        val totalCholesterolListResult =
            patientRecentlyValidIndicatorResult(IndicatorEnum.BLOOD_LIPIDS_TOTAL_CHOLESTEROL)
        val totalCholesterolPageResult =
            indicatorPagedResultByIndicatorEnum(IndicatorEnum.BLOOD_LIPIDS_TOTAL_CHOLESTEROL)
        Assertions.assertTrue(totalCholesterolPageResult.total >= 1)
        Assertions.assertEquals(
            totalCholesterolListResult[0]._value,
            totalCholesterolPageResult._data?.get(0)?._value
        )

        val triglyceridesListResult = patientRecentlyValidIndicatorResult(IndicatorEnum.BLOOD_LIPIDS_TRIGLYCERIDES)
        val triglyceridesPageResult = indicatorPagedResultByIndicatorEnum(IndicatorEnum.BLOOD_LIPIDS_TRIGLYCERIDES)
        Assertions.assertTrue(triglyceridesPageResult.total >= 1)
        Assertions.assertEquals(
            triglyceridesListResult[0]._value,
            triglyceridesPageResult._data?.get(0)?._value
        )

        val lowDensityListResult =
            patientRecentlyValidIndicatorResult(IndicatorEnum.BLOOD_LIPIDS_LOW_DENSITY_LIPOPROTEIN)
        val lowDensityPageResult =
            indicatorPagedResultByIndicatorEnum(IndicatorEnum.BLOOD_LIPIDS_LOW_DENSITY_LIPOPROTEIN)
        Assertions.assertTrue(lowDensityPageResult.total >= 1)
        Assertions.assertEquals(
            lowDensityListResult[0]._value,
            lowDensityPageResult._data?.get(0)?._value
        )

        val highDensityListResult =
            patientRecentlyValidIndicatorResult(IndicatorEnum.BLOOD_LIPIDS_HIGH_DENSITY_LIPOPROTEIN)
        val highDensityPageResult =
            indicatorPagedResultByIndicatorEnum(IndicatorEnum.BLOOD_LIPIDS_HIGH_DENSITY_LIPOPROTEIN)
        Assertions.assertTrue(lowDensityPageResult.total >= 1)
        Assertions.assertEquals(
            highDensityListResult[0]._value,
            highDensityPageResult._data?.get(0)?._value
        )

        val heartListResult = patientRecentlyValidIndicatorResult(IndicatorEnum.HEART_RATE)
        val heartPageResult = indicatorPagedResultByIndicatorEnum(IndicatorEnum.HEART_RATE)
        Assertions.assertTrue(heartPageResult.total >= 1)
        Assertions.assertEquals(heartListResult[0]._value, heartPageResult._data?.get(0)?._value)

        val pulseListResult = patientRecentlyValidIndicatorResult(IndicatorEnum.PULSE)
        val pulsePageResult = indicatorPagedResultByIndicatorEnum(IndicatorEnum.PULSE)
        Assertions.assertTrue(pulsePageResult.total >= 1)
        Assertions.assertEquals(pulseListResult[0]._value, pulsePageResult._data?.get(0)?._value)

        val smokeListResult = patientRecentlyValidIndicatorResult(IndicatorEnum.SMOKE)
        val smokePageResult = indicatorPagedResultByIndicatorEnum(IndicatorEnum.SMOKE)
        Assertions.assertTrue(smokePageResult.total >= 1)
        Assertions.assertEquals(smokeListResult[0]._value, smokePageResult._data?.get(0)?._value)

        val drinkListResult = patientRecentlyValidIndicatorResult(IndicatorEnum.DRINKING_BEER)
        val drinkPageResult = indicatorPagedResultByIndicatorEnum(IndicatorEnum.DRINKING_BEER)
        Assertions.assertTrue(drinkPageResult.total >= 1)
        Assertions.assertEquals(drinkListResult[0]._value, drinkPageResult._data?.get(0)?._value)

        val whiteSpiritListResult = patientRecentlyValidIndicatorResult(IndicatorEnum.DRINKING_WHITE_SPIRIT)
        val whiteSpiritPageResult = indicatorPagedResultByIndicatorEnum(IndicatorEnum.DRINKING_WHITE_SPIRIT)
        Assertions.assertTrue(whiteSpiritPageResult.total >= 1)
        Assertions.assertEquals(whiteSpiritListResult[0]._value, whiteSpiritPageResult._data?.get(0)?._value)

        val wineListResult = patientRecentlyValidIndicatorResult(IndicatorEnum.DRINKING_WINE)
        val winePageResult = indicatorPagedResultByIndicatorEnum(IndicatorEnum.DRINKING_WINE)
        Assertions.assertTrue(winePageResult.total >= 1)
        Assertions.assertEquals(wineListResult[0]._value, winePageResult._data?.get(0)?._value)

        val yellowListResult = patientRecentlyValidIndicatorResult(IndicatorEnum.DRINKING_YELLOW_RICE_SPIRIT)
        val yellowPageResult = indicatorPagedResultByIndicatorEnum(IndicatorEnum.DRINKING_YELLOW_RICE_SPIRIT)
        Assertions.assertTrue(yellowPageResult.total >= 1)
        Assertions.assertEquals(yellowListResult[0]._value, yellowPageResult._data?.get(0)?._value)

        val pulseOximetryListResult = patientRecentlyValidIndicatorResult(IndicatorEnum.PULSE_OXIMETRY)
        val pulseOximetryPageResult = indicatorPagedResultByIndicatorEnum(IndicatorEnum.PULSE_OXIMETRY)
        Assertions.assertTrue(pulseOximetryPageResult.total >= 1)
        Assertions.assertEquals(pulseOximetryListResult[0]._value, pulseOximetryPageResult._data?.get(0)?._value)
    }

    private fun patientRecentlyValidIndicatorResult(indicatorEnum: IndicatorEnum): List<PatientRecentlyValidIndicatorResultInner> =
        api.selectRecentlyValidIndicatorByType(
            SelectRecentlyValidPatientIndicatorParam(
                patientId,
                listOf(indicatorEnum)
            )
        )

    private fun indicatorPagedResultByIndicatorEnum(indicatorEnum: IndicatorEnum): PagedResult<PatientRecentlyValidIndicatorResultInner> {
        return api.recentlyValidIndicatorPageListByEnumType(
            IndicatorEnumTypeParam(
                patientId,
                indicatorEnum,
                1,
                10
            )
        )
    }

    private fun getCurrentMonthStartAndEndTime(): Pair<LocalDateTime, LocalDateTime> {
        val startTime: LocalDateTime = LocalDateTime.of(
            LocalDate.from(LocalDateTime.now().with(TemporalAdjusters.firstDayOfMonth())),
            LocalTime.MIN
        )

        val endTime: LocalDateTime = LocalDateTime.of(
            LocalDate.from(LocalDateTime.now().with(TemporalAdjusters.lastDayOfMonth())),
            LocalTime.MAX
        )
        return Pair(startTime, endTime)
    }

    private fun indicatorAlertNotifyVerify(
        indicatorName: String,
        indicators: List<Indicator>,
        patientId: BigInteger,
        exceptionMessage: String,
        exceptionTag: String? = null
    ) {
        val captor: ArgumentCaptor<IndicatorNotify> = ArgumentCaptor.forClass(IndicatorNotify::class.java)
        Mockito.verify(notifyRpcService).patientNotifyIndicatorWarningPost(capture(captor))
        Assertions.assertEquals(patientId, captor.value.patientId)
        Assertions.assertEquals(indicatorName, captor.value.name)
        Assertions.assertEquals(indicators[0].indicatorName, captor.value.indicators[0].name)
        Assertions.assertEquals(indicators[0].indicatorValue, captor.value.indicators[0].value)
        Assertions.assertEquals(indicators[0].unit, captor.value.indicators[0].unit)
        Assertions.assertEquals(
            LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES),
            captor.value.date.truncatedTo(ChronoUnit.MINUTES)
        )
        Assertions.assertEquals(
            String.format(exceptionMessage, exceptionTag),
            captor.value.message
        )
    }

    //模拟远程请求
    private fun mockPatientInfo(patientId: BigInteger) {
        Mockito.`when`(patientRpcService.getPatientInfo(patientId)).thenReturn(
            PatientInfoResponse(
                patientId,
                "zs",
                Gender.MAN,
                "123",
                "123",
                LocalDateTime.of(2000, 1, 1, 15, 0, 0),
                24
            )
        )
    }

    //拦截远程请求
    private fun captorPatientInfo(patientId: BigInteger, invocationNum: Int = 1) {
        val captor: ArgumentCaptor<BigInteger> = ArgumentCaptor.forClass(BigInteger::class.java)
        Mockito.verify(patientRpcService, times(invocationNum)).getPatientInfo(capture(captor))
        Assertions.assertEquals(patientId, captor.value)
    }

    private fun captorClockRequest(patientId: BigInteger, healthPlanType: HealthPlanType, invocationNum: Int = 1) {
        val captor: ArgumentCaptor<ClockInRequest> = ArgumentCaptor.forClass(ClockInRequest::class.java)
        Mockito.verify(clockInRpcService, times(invocationNum)).saveClockIn(capture(captor))
        Assertions.assertEquals(patientId, captor.value.patientId)
        Assertions.assertEquals(healthPlanType, captor.value.healthPlanType)
    }
}