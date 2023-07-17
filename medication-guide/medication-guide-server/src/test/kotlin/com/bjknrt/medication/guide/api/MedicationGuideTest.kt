package com.bjknrt.medication.guide.api

import ContraindicationAnchorType
import com.bjknrt.doctor.patient.management.api.PatientApi
import com.bjknrt.doctor.patient.management.vo.Gender
import com.bjknrt.doctor.patient.management.vo.PatientInfoResponse
import com.bjknrt.doctor.patient.management.vo.PatientTag
import com.bjknrt.medication.guide.*
import com.bjknrt.medication.guide.vo.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito.doReturn
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDateTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.MethodName::class)
class MedicationGuideTest : AbstractContainerBaseTest() {

    @Autowired
    lateinit var api: MedicationGuideApi

    @Autowired
    lateinit var tableDrug: MedicationGuideDrugTable

    @Autowired
    lateinit var tableGeneric: MedicationGuideGenericTable

    @Autowired
    lateinit var tableMonograph: MedicationGuideDrugMonographTable

    @Autowired
    lateinit var tableMonographSection: MedicationGuideMonographSectionTable

    @Autowired
    lateinit var tableMonographContent: MedicationGuideMonographContentTable

    @Autowired
    lateinit var tableContraindication: MedicationGuideSpecialCrowdContraindicationTable

    @MockBean
    lateinit var patientClient: PatientApi

    @BeforeAll
    fun addData() {

        val medicationGuideGeneric = MedicationGuideGeneric.builder()
            .setKnGenericId(0)
            .setKnGenericName("amxl")
            .build()
        tableGeneric.insertWithoutNull(medicationGuideGeneric)

        val medicationGuideDrug = MedicationGuideDrug.builder()
            .setKnDrugId(0)
            .setKnDrugName("amxl")
            .setKnPinyin("amxl")
            .setKnGenericId(0)
            .build()
        tableDrug.insertWithoutNull(medicationGuideDrug)

        val medicationGuideDrugMonograph = MedicationGuideDrugMonograph.builder()
            .setKnMonographId(0)
            .setKnName("amxl")
            .setKnSearchPinyin("amxl")
            .setKnPinyin("amxl")
            .build()
        tableMonograph.insertWithoutNull(medicationGuideDrugMonograph)

        val medicationGuideMonographSection = MedicationGuideMonographSection.builder()
            .setKnMonographSectionId(0)
            .setKnMonographSectionName("章节")
            .build()
        tableMonographSection.insertWithoutNull(medicationGuideMonographSection)

        val medicationGuideMonographContent = MedicationGuideMonographContent.builder()
            .setKnMonographId(0)
            .setKnSectionId(0)
            .setKnSectionContent("html")
            .build()
        tableMonographContent.insertWithoutNull(medicationGuideMonographContent)


        val contraindication = MedicationGuideSpecialCrowdContraindication.builder()
            .setKnContraindicationId(0)
            .setKnSpecialCrowd("GERIATRIC")
            .setKnDrugAnchorType(ContraindicationAnchorType.GENERIC.ordinal.toLong())
            .setKnDrugAnchorId(0)
            .build()
        tableContraindication.insertWithoutNull(contraindication)
    }

    /**
     * To test MedicationGuideApi.genericList
     */
    @Test
    fun genericListTest() {
        val body: kotlin.String = "amxl"
        val response: List<GenericList200ResponseInner> = api.genericList(body)
        assertEquals(true, response.filter { it.id == 0L }.isNotEmpty())
    }

    /**
     * To test MedicationGuideApi.monographList
     */
    @Test
    fun monographListTest() {
        val body: kotlin.String = "amxl"
        val response: List<Inner> = api.monographList(body)
        assertEquals(true, response.filter { it.id == 0L }.isNotEmpty())
    }

    /**
     * To test MedicationGuideApi.submitGeneric
     */
    @Test
    fun submitGenericTest() {
        val submitGenericRequest: SubmitGenericRequest = SubmitGenericRequest(
            listOf(SubmitGenericRequestGenericInner("阿莫西林", 0)),
            listOf(
                Crowd.PEDIATRIC,
                Crowd.GERIATRIC,
                Crowd.MALE,
                Crowd.FEMALE,
                Crowd.PREGNANCY,
                Crowd.LACTATION
            )
        )
        val response: List<SubmitGeneric200ResponseInner> = api.submitGeneric(submitGenericRequest)
        assertEquals(true, response.isNotEmpty())
    }

    /**
     * To test MedicationGuideApi.monograph
     */
    @Test
    fun monographTest() {
        val body: kotlin.Long = 0
        val response: kotlin.String = api.monograph(body)
        assertEquals(true, response.isNotEmpty())
    }

    @Test
    fun drugTest() {
        doReturn(
            PatientInfoResponse(
                BigInteger.ZERO,
                "name",
                Gender.WOMAN,
                "",
                "",
                LocalDateTime.now(),
                70,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                PatientTag.EXISTS,
                PatientTag.EXISTS,
                PatientTag.EXISTS,
                PatientTag.EXISTS,
                PatientTag.EXISTS
            )
        ).`when`(patientClient).getPatientInfo(BigInteger.ZERO)
        val drugList = api.drugList(Param("amxl", 1, 1))
        assertEquals(1, drugList._data?.size)
        var request = SubmitDrugRequest(
            BigInteger.ZERO, listOf(
                MedicationOrderInner(
                    0,
                    1,
                    BigDecimal.valueOf(25)
                )
            )
        )
        var response = api.submitDrug(request)
        assertEquals(1, response.size)
        request = SubmitDrugRequest(
            BigInteger.ZERO, listOf(
                MedicationOrderInner(
                    100,
                    1,
                    BigDecimal.valueOf(25)
                )
            )
        )
        response = api.submitDrug(request)
        assertEquals(0, response.size)
    }
}
