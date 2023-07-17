package com.bjknrt.health.scheme.constant

import com.bjknrt.health.scheme.entity.DietPlanArticleEntity
import com.bjknrt.health.scheme.entity.DietPlanContextEntity
import com.bjknrt.health.scheme.entity.PictureEntity
import com.bjknrt.health.scheme.enums.DietPlanTypeEnum

// 主食, 文章第一条
private val stapleFood1 = DietPlanArticleEntity(
    title = "主食：每日主食中应有1/4~1/3为全谷物。",
    contexts = listOf(
        DietPlanContextEntity(
            title = "全谷物类常见食物有：",
            context = "小米、玉米、燕麦、全麦粉等作为主食或粥类。"
        ),
        DietPlanContextEntity(
            title = "参考食用方法：",
            context = "早餐吃小米粥、燕麦粥、八宝粥等；午餐、晚餐时，可在白米中放一把糙米、燕麦等（适宜比例：全谷物1/4~1/3）来烹制米饭。"
        ),
        DietPlanContextEntity(
            title = "杂豆类常见食物有：",
            context = "红豆、绿豆、芸豆、花豆等。"
        ),
        DietPlanContextEntity(
            title = "参考食用方法：",
            context = "芸豆、花豆、红豆煮松软后适当调味，制成美味凉菜；使用各种豆类制成豆馅包子；豆浆机制成五谷豆浆等。"
        )
    )
)

private val stapleFood2 = DietPlanArticleEntity(
    title = "主食：每日主食中应有1/4~1/3为全谷物。注意控制主食量和进餐方式（定点进餐，少食多餐）。",
    contexts = listOf(
        DietPlanContextEntity(
            title = "全谷物类常见食物有：",
            context = "小米、玉米、燕麦、全麦粉等作为主食或粥类。"
        ),
        DietPlanContextEntity(
            title = "参考食用方法：",
            context = "早餐吃小米粥、燕麦粥、八宝粥等；午餐、晚餐时，可在白米中放一把糙米、燕麦等（适宜比例：全谷物1/4~1/3）来烹制米饭。"
        ),
        DietPlanContextEntity(
            title = "杂豆类常见食物有：",
            context = "红豆、绿豆、芸豆、花豆等。"
        ),
        DietPlanContextEntity(
            title = "参考食用方法：",
            context = "芸豆、花豆、红豆煮松软后适当调味，制成美味凉菜；使用各种豆类制成豆馅包子；豆浆机制成五谷豆浆等。"
        )
    )
)

private val stapleFood_1_12 = DietPlanArticleEntity(
    title = "主食：每日主食不宜超过100克（2两），全谷类应占总谷类的一半以上。注意控制主食量和进餐方式（定点进餐，少食多餐）。",
    contexts = listOf(
        DietPlanContextEntity(
            title = "全谷物类常见食物有：",
            context = "小米、玉米、燕麦、全麦粉等作为主食或粥类。"
        ),
        DietPlanContextEntity(
            title = "参考食用方法：",
            context = "早餐吃小米粥、燕麦粥、八宝粥等；午餐、晚餐时，可在白米中放一把糙米、燕麦等（适宜比例：全谷物1/4~1/3）来烹制米饭。"
        ),
        DietPlanContextEntity(
            title = "杂豆类常见食物有：",
            context = "红豆、绿豆、芸豆、花豆等。"
        ),
        DietPlanContextEntity(
            title = "参考食用方法：",
            context = "芸豆、花豆、红豆煮松软后适当调味，制成美味凉菜；使用各种豆类制成豆馅包子；豆浆机制成五谷豆浆等。"
        )
    )
)

// 薯类, 文章第二条
private val tubers = DietPlanArticleEntity(
    title = "薯类：每天的摄入量为50~100g。",
    pictures = listOf(
        PictureEntity(
            url = "https://knrt-doctor-app.oss-cn-shanghai.aliyuncs.com/hmp/diet_plan/1.png",
            desc = "以下以红薯为例比照参考："
        )
    ),
    contexts = listOf(
        DietPlanContextEntity(
            title = "薯类常见食物有：",
            context = "甘薯（红薯、紫薯、白薯）、土豆、木薯、豆薯等。"
        )
    )
)

// 水果, 文章第三条
private val fruits = DietPlanArticleEntity(
    title = "水果：首选新鲜应季水果，每天的摄入量为200~350g（注意避免升糖快且强的水果）。",
    pictures = listOf(
        PictureEntity(
            url = "https://knrt-doctor-app.oss-cn-shanghai.aliyuncs.com/hmp/diet_plan/2.png",
            desc = "以下为水果重量比照参考："
        )
    ),
    contexts = listOf(
        DietPlanContextEntity(
            title = "含糖量不高的水果：",
            context = "草莓、哈密瓜、芒果、樱桃、李子、苹果……"
        ),
        DietPlanContextEntity(
            title = "富含钾的水果（肾功能不全患者少吃）：",
            context = "香蕉、芒果、橘子、苹果、梨子、枣……"
        ),
        DietPlanContextEntity(
            title = "达标小贴士：",
            context = "水果可放在家中或工作单位里容易看到和方便拿到的地方；可以将水果加到酸奶中，作为饭前饭后必须的食物。"
        )
    )
)

// 水果, 文章第三条
private val fruits_1_12_a = DietPlanArticleEntity(
    title = "水果：首选新鲜应季水果，每天的摄入量为250g（注意避免升糖快且强的水果）。",
    pictures = listOf(
        PictureEntity(
            url = "https://knrt-doctor-app.oss-cn-shanghai.aliyuncs.com/hmp/diet_plan/2.png",
            desc = "以下为水果重量比照参考："
        )
    ),
    contexts = listOf(
        DietPlanContextEntity(
            title = "含糖量不高的水果：",
            context = "草莓、哈密瓜、芒果、樱桃、李子、苹果……"
        ),
        DietPlanContextEntity(
            title = "富含钾的水果（肾功能不全患者少吃）：",
            context = "香蕉、芒果、橘子、苹果、梨子、枣……"
        ),
        DietPlanContextEntity(
            title = "达标小贴士：",
            context = "水果可放在家中或工作单位里容易看到和方便拿到的地方；可以将水果加到酸奶中，作为饭前饭后必须的食物。"
        )
    )
)

// 水果, 文章第三条
private val fruits_1_12_b = DietPlanArticleEntity(
    title = "水果：首选新鲜应季水果，每天可以吃150g左右。如果每天吃新鲜水果的量达到200-250g，就要从全天的主食中减掉25g（注意避免升糖快且强的水果）。",
    pictures = listOf(
        PictureEntity(
            url = "https://knrt-doctor-app.oss-cn-shanghai.aliyuncs.com/hmp/diet_plan/2.png",
            desc = "以下为水果重量比照参考："
        )
    ),
    contexts = listOf(
        DietPlanContextEntity(
            title = "含糖量不高的水果：",
            context = "草莓、哈密瓜、芒果、樱桃、李子、苹果……"
        ),
        DietPlanContextEntity(
            title = "富含钾的水果（肾功能不全患者少吃）：",
            context = "香蕉、芒果、橘子、苹果、梨子、枣……"
        ),
        DietPlanContextEntity(
            title = "达标小贴士：4",
            context = "当血糖控制良好（空腹血糖< 7.8mmol/L，或餐后2小时血糖< 10mol/L,或糖化血红蛋白< 7.5%），且病情稳定，不经常出现血糖较大波动时，可于两餐之间吃水果。 同时，适当减少主餐食物摄入总量。"
        )
    )
)

// 蔬菜, 文章第四条
private val vegetables = DietPlanArticleEntity(
    title = "蔬菜：每日摄入量为300~500g，且每日至少吃3种，多吃油菜、菠菜、土豆、西红柿、金针菇、西葫芦、芹菜、鲜蘑菇及各种绿叶蔬菜等富含钾的蔬菜。",
    contexts = listOf(
        DietPlanContextEntity(
            title = "达标小贴士：",
            context = "中、晚餐时每餐至少有2个蔬菜的菜肴；适合生吃的蔬菜可作为饭前饭后的“零食”和“茶点”；在一餐的食物中，首先保证蔬菜重量大约占1/2（占餐盘的1/2，估算即可）。"
        )
    )
)

// 蔬菜, 文章第四条
private val vegetables_1_12 = DietPlanArticleEntity(
    title = "蔬菜：每日摄入量为400~500g，且每日至少吃3种，多吃油菜、菠菜、土豆、西红柿、金针菇、西葫芦、芹菜、鲜蘑菇及各种绿叶蔬菜等富含钾的蔬菜。",
    contexts = listOf(
        DietPlanContextEntity(
            title = "达标小贴士：",
            context = "中、晚餐时每餐至少有2个蔬菜的菜肴；适合生吃的蔬菜可作为饭前饭后的“零食”和“茶点”；在一餐的食物中，首先保证蔬菜重量大约占1/2（占餐盘的1/2，估算即可）。"
        )
    )
)

// 动物性食物, 肉类, 文章第五条
private val animalFood = DietPlanArticleEntity(
    title = "动物性食物平均每日摄入总量为120-200g，分散在各餐中食用。优先选择鱼和禽肉类食物，少吃肥肉、烟熏和腌制肉制品。",
    pictures = listOf(
        PictureEntity(
            url = "https://knrt-doctor-app.oss-cn-shanghai.aliyuncs.com/hmp/diet_plan/3.png",
            desc = "以下为鱼的重量比照参考："
        ),
        PictureEntity(
            url = "https://knrt-doctor-app.oss-cn-shanghai.aliyuncs.com/hmp/diet_plan/4.png",
            desc = "以下为鸡蛋与乒乓球的比照："
        ),
        PictureEntity(
            url = "https://knrt-doctor-app.oss-cn-shanghai.aliyuncs.com/hmp/diet_plan/5.png",
            desc = "以下为畜禽肉类的比照参考："
        )
    ),
    contexts = listOf(
        DietPlanContextEntity(
            title = "达标小贴士：",
            context = "平均每日摄入总量为120-200g相当于每周吃鱼2次或300-500g（参考下图）、蛋类300-500g（参考下图）、畜禽肉类300-500g（参考下图）。"
        )
    )
)

// 低脂奶, 奶制品, 文章第六条
private val dairy = DietPlanArticleEntity(
    title = "低脂奶或脱脂奶及奶制品每天300~500g。",
    pictures = listOf(
        PictureEntity(
            url = "https://knrt-doctor-app.oss-cn-shanghai.aliyuncs.com/hmp/diet_plan/6.png",
            desc = "以下为奶量比照参考："
        )
    ),
    contexts = listOf(
        DietPlanContextEntity(
            title = "常见的奶类：",
            context = "液态奶、奶粉、酸奶、奶酪、奶皮、炼乳等，注意乳饮料不属于奶制品。"
        )
    )
)

// 低脂奶, 奶制品, 文章第六条
private val dairy_1_12 = DietPlanArticleEntity(
    title = "低脂奶或脱脂奶及奶制品每天250g。",
    pictures = listOf(
        PictureEntity(
            url = "https://knrt-doctor-app.oss-cn-shanghai.aliyuncs.com/hmp/diet_plan/6.png",
            desc = "以下为奶量比照参考："
        )
    ),
    contexts = listOf(
        DietPlanContextEntity(
            title = "常见的奶类：",
            context = "液态奶、奶粉、酸奶、奶酪、奶皮、炼乳等，注意乳饮料不属于奶制品。"
        )
    )
)

// 蛋类
private val egg_1_12 = DietPlanArticleEntity(
    title = "蛋类每周3-4个。",
    pictures = listOf(
        PictureEntity(
            url = "https://knrt-doctor-app.oss-cn-shanghai.aliyuncs.com/hmp/diet_plan/4.png",
            desc = ""
        )
    )
)

// 水, 文章第七条
private val water = DietPlanArticleEntity(
    title = "足量饮水，推荐白开水，1500~1700ml，少量多次饮用。",
    pictures = listOf(
        PictureEntity(
            url = "https://knrt-doctor-app.oss-cn-shanghai.aliyuncs.com/hmp/diet_plan/10.png",
            desc = ""
        )
    )
)

// 食盐, 文章第八条
private val salt = DietPlanArticleEntity(
    title = "减少食盐摄入量，每日＜5.0g。",
    pictures = listOf(
        PictureEntity(
            url = "https://knrt-doctor-app.oss-cn-shanghai.aliyuncs.com/hmp/diet_plan/11.png",
            desc = "控制盐量建议使用专业控盐勺，见下图："
        )
    ),
    contexts = listOf(
        DietPlanContextEntity(
            title = "达标小贴士：",
            context = "选用新鲜食材，巧用醋、花椒、八角、辣椒、葱、姜、蒜等调味料来调味，减少对咸味的依赖；合理运用烹调方法，等到快出锅或关火后再加盐；对于炖、煮菜肴，由于汤水较多，更要减少食盐用量；注意“隐形盐”，指酱油、酱类、咸菜以及食盐含量高的饼干、面包等加工食品等。"
        )
    )
)

// 高脂肪食物, 文章第九条
private val highFatFood = DietPlanArticleEntity(
    title = "控制高脂肪食物摄入，每日烹调用油量应控制在20-30g。伴有血脂异常者应减少胆固醇和饱和脂肪高的食物摄入（相当于隔天/每2天吃一个全蛋或者每周不超过3个蛋黄）。",
    pictures = listOf(
        PictureEntity(
            url = "https://knrt-doctor-app.oss-cn-shanghai.aliyuncs.com/hmp/diet_plan/7.png",
            desc = "食用油用量可通过量杯等工具进行控制，见下图："
        )
    ),
    contexts = listOf(
        DietPlanContextEntity(
            title = "胆固醇或饱和脂肪高的食物：",
            context = "猪蹄、猪大排、猪肋条肉、鸡腿、鸡爪等。"
        ),
        DietPlanContextEntity(
            title = "达标小贴士：",
            context = "选择合理的烹调方法，如蒸、煮、炖、焖、水滑、熘、拌等；选购优质植物油，即外观看着透明度高、无沉淀、无悬浮物，无刺激性气味的油；少吃油炸食品；薯条、土豆片、饼干、蛋糕、加工肉制品等容易含有大量饱和脂肪酸，因此需尽量避免。"
        )
    )
)

// 精制糖, 文章第十条
private val refinedSugar = DietPlanArticleEntity(
    title = "控制精制糖摄入：添加糖的摄入量每日＜50g，最好控制在＜25g。少喝、不喝含糖饮料，减少食用添加大量精制糖的甜点。",
    pictures = listOf(
        PictureEntity(
            url = "https://knrt-doctor-app.oss-cn-shanghai.aliyuncs.com/hmp/diet_plan/8.png",
            desc = "控制糖量建议使用瓷勺，见下图："
        )
    )
)

// 烟酒, 文章第十一条
private val tobaccoAlcohol = DietPlanArticleEntity(
    title = "戒烟戒酒。",
    pictures = listOf(
        PictureEntity(
            url = "https://knrt-doctor-app.oss-cn-shanghai.aliyuncs.com/hmp/diet_plan/12.png",
            desc = ""
        )
    )
)

// 豆类, 文章第十二条
private val soybean = DietPlanArticleEntity(
    title = "长期应用激素的患者，饮食注意补钙，在每日充足的奶制品摄入基础上，如多吃大豆、卤水豆腐（北豆腐）、石膏豆腐（南豆腐）等豆制品，建议每天吃100g为宜。",
    pictures = listOf(
        PictureEntity(
            url = "https://knrt-doctor-app.oss-cn-shanghai.aliyuncs.com/hmp/diet_plan/9.png",
            desc = "以下为豆制品比照参考："
        )
    )
)

// 高血压文章
private val HYPERTENSION = listOf(
    stapleFood1,
    tubers,
    fruits_1_12_a,
    vegetables_1_12,
    animalFood,
    dairy_1_12,
    egg_1_12,
    water,
    salt,
    highFatFood,
    refinedSugar,
    tobaccoAlcohol
)

// 糖尿病文章 主食和高血压略有不同
private val DIABETES = listOf(
    stapleFood_1_12,
    tubers,
    fruits_1_12_b,
    vegetables,
    animalFood,
    dairy,
    water,
    salt,
    highFatFood,
    refinedSugar,
    tobaccoAlcohol
)

// 慢阻肺 比高血压多一条豆类
private val COPD = listOf(
    stapleFood1,
    tubers,
    fruits,
    vegetables,
    animalFood,
    dairy,
    water,
    salt,
    highFatFood,
    refinedSugar,
    tobaccoAlcohol,
    soybean
)

// 糖尿病+慢阻肺饮食 主食和糖尿病相同,多一条豆类
private val DIABETES_COPD = listOf(
    stapleFood2,
    tubers,
    fruits,
    vegetables,
    animalFood,
    dairy,
    water,
    salt,
    highFatFood,
    refinedSugar,
    tobaccoAlcohol,
    soybean
)

val DIET_PLAN = mapOf(
    DietPlanTypeEnum.HYPERTENSION to HYPERTENSION,
    DietPlanTypeEnum.DIABETES to DIABETES,
    DietPlanTypeEnum.COPD to COPD,
    // 冠心病脑卒中 和 高血压文章一样
    DietPlanTypeEnum.ACUTE_CORONARY_DISEASE_CEREBRAL_STROKE to HYPERTENSION,
    // 高血压+糖尿病饮食 和 糖尿病一样
    DietPlanTypeEnum.HYPERTENSION_DIABETES to DIABETES,
    // 高血压+慢阻肺饮食 和 慢阻肺文章相同
    DietPlanTypeEnum.HYPERTENSION_COPD to COPD,
    //  高血压+冠心病/脑卒中饮食 和 高血压文章一样
    DietPlanTypeEnum.HYPERTENSION_ACUTE_CORONARY_DISEASE_CEREBRAL_STROKE to HYPERTENSION,
    // 糖尿病+慢阻肺饮食
    DietPlanTypeEnum.DIABETES_COPD to DIABETES_COPD,
    // 糖尿病+冠心病/脑卒中饮食 和 糖尿病文章一样
    DietPlanTypeEnum.DIABETES_ACUTE_CORONARY_DISEASE_CEREBRAL_STROKE to DIABETES,
    // 慢阻肺+冠心病/脑卒中饮食 和 慢阻肺文章一样
    DietPlanTypeEnum.COPD_ACUTE_CORONARY_DISEASE_CEREBRAL_STROKE to COPD,
    // 高血压+糖尿病+慢阻肺饮食 和 糖尿病+慢阻肺饮食文章一样
    DietPlanTypeEnum.HYPERTENSION_DIABETES_COPD to DIABETES_COPD,
    // 高血压+糖尿病+冠心病/脑卒中饮食 和 糖尿病一样
    DietPlanTypeEnum.HYPERTENSION_DIABETES_ACUTE_CORONARY_DISEASE_CEREBRAL_STROKE to DIABETES,
    // 高血压+慢阻肺+冠心病/脑卒中饮食 和 慢阻肺文章一样
    DietPlanTypeEnum.HYPERTENSION_COPD_ACUTE_CORONARY_DISEASE_CEREBRAL_STROKE to COPD,
    // 糖尿病+慢阻肺+冠心病/脑卒中 和 糖尿病+慢阻肺饮食文章一样
    DietPlanTypeEnum.DIABETES_COPD_ACUTE_CORONARY_DISEASE_CEREBRAL_STROKE to DIABETES_COPD,
    // 高血压+糖尿病+慢阻肺+冠心病/脑卒中饮食 和 糖尿病+慢阻肺饮食文章一样
    DietPlanTypeEnum.HYPERTENSION_DIABETES_COPD_ACUTE_CORONARY_DISEASE_CEREBRAL_STROKE to DIABETES_COPD,

    )