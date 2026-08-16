package com.example.data.local

import com.example.data.model.Drug

object DefaultDrugs {
    val INITIAL_DRUGS = listOf(
        // Domna Agency
        Drug(
            id = "DOM-001",
            tradeName = "دومنول 500 ملغ (Domnol)",
            scientificName = "Paracetamol 500mg",
            agencyId = "domna",
            agencyName = "دومنا (Domna)",
            publicPrice = 6500.0,
            pharmacistPrice = 5200.0,
            netPrice = 4300.0,
            netCode = "NET-DOM-10",
            bonusRatio = "10 + 2 مجاناً",
            isAvailable = true,
            description = "مسكن للآلام ومخفد للحرارة سريع الفعالية وآمن على المعدة.",
            composition = "باراسيتامول 500 ملغ",
            dosage = "قرص كل 6 إلى 8 ساعات حسب الحاجة",
            companyCode = "DOM500"
        ),
        Drug(
            id = "DOM-002",
            tradeName = "دومنا بروفين 400 ملغ",
            scientificName = "Ibuprofen 400mg",
            agencyId = "domna",
            agencyName = "دومنا (Domna)",
            publicPrice = 11000.0,
            pharmacistPrice = 8800.0,
            netPrice = 7000.0,
            netCode = "NET-DOM-20",
            bonusRatio = "10 + 1 مجاناً",
            isAvailable = true,
            description = "مضاد التهاب غير ستيرويدي ومسكن لآلام المفاصل والأسنان.",
            composition = "آيبوبروفين 400 ملغ",
            dosage = "قرص بعد الطعام 3 مرات يومياً",
            companyCode = "DOM400"
        ),

        // Barakat Agency
        Drug(
            id = "BAR-001",
            tradeName = "بركات سيفاليكسين 500 ملغ",
            scientificName = "Cephalexin 500mg",
            agencyId = "barakat",
            agencyName = "بركات (Barakat)",
            publicPrice = 22000.0,
            pharmacistPrice = 17600.0,
            netPrice = 14000.0,
            netCode = "NET-BAR-01",
            bonusRatio = "12 + 3 مجاناً",
            isAvailable = true,
            description = "مضاد حيوي واسع الطيف للالتهابات الجرثومية والتنفسية.",
            composition = "سيفاليكسين هيدرات 500 ملغ",
            dosage = "كبسولة كل 6 ساعات بعد الاستشارة الطبية",
            companyCode = "BAR500"
        ),
        Drug(
            id = "BAR-002",
            tradeName = "بركات أموكسيكلاف 1 غرام",
            scientificName = "Amoxicillin + Clavulanic Acid",
            agencyId = "barakat",
            agencyName = "بركات (Barakat)",
            publicPrice = 38000.0,
            pharmacistPrice = 30400.0,
            netPrice = 24200.0,
            netCode = "NET-BAR-02",
            bonusRatio = "10 + 2 مجاناً",
            isAvailable = true,
            description = "مضاد حيوي قوي للعدوى البكتيرية المعقدة واللوزتين.",
            composition = "أموكسيسيلين 875ملغ + حمض الكلافولانيك 125ملغ",
            dosage = "قرص كل 12 ساعة بعد الطعام",
            companyCode = "BAR1000"
        ),

        // Medico Agency
        Drug(
            id = "MED-001",
            tradeName = "ميديكو أوميبرازول 20 ملغ",
            scientificName = "Omeprazole 20mg",
            agencyId = "medico",
            agencyName = "ميديكو (Medico)",
            publicPrice = 12000.0,
            pharmacistPrice = 9600.0,
            netPrice = 7800.0,
            netCode = "NET-MED-05",
            bonusRatio = "10 + 2 مجاناً",
            isAvailable = true,
            description = "مثبط لمضخة البروتون لعلاج قرحة المعدة وحموضة المريء.",
            composition = "أوميبرازول 20 ملغ",
            dosage = "كبسولة صباحاً قبل الإفطار بـ 30 دقيقة",
            companyCode = "MED20"
        ),

        // Allied Agency
        Drug(
            id = "ALL-001",
            tradeName = "المتحدة لوراتادين 10 ملغ",
            scientificName = "Loratadine 10mg",
            agencyId = "allied",
            agencyName = "المتحدة (Allied)",
            publicPrice = 9000.0,
            pharmacistPrice = 7200.0,
            netPrice = 5800.0,
            netCode = "NET-ALL-11",
            bonusRatio = "10 + 3 مجاناً",
            isAvailable = true,
            description = "مضاد حساسيات غير مسبب للنعاس للحساسية الموسمية والجلدية.",
            composition = "لوراتادين 10 ملغ",
            dosage = "قرص واحد يومياً",
            companyCode = "ALL10"
        ),

        // Ibn Rushd Agency
        Drug(
            id = "IBN-001",
            tradeName = "ابن رشد ديكلوفيناك 50 ملغ",
            scientificName = "Diclofenac Sodium 50mg",
            agencyId = "ibn_rushd",
            agencyName = "ابن رشد (Ibn Rushd)",
            publicPrice = 10000.0,
            pharmacistPrice = 8000.0,
            netPrice = 6500.0,
            netCode = "NET-IBN-07",
            bonusRatio = "10 + 2 مجاناً",
            isAvailable = true,
            description = "مضاد تورم ومسكن آلام الروماتيزم والعظام.",
            composition = "ديكلوفيناك الصوديوم 50 ملغ",
            dosage = "قرص مرتان يومياً بعد الأكل",
            companyCode = "IBN50"
        ),

        // Lama Agency
        Drug(
            id = "LAM-001",
            tradeName = "لاما فيتامين سي + زنك",
            scientificName = "Vitamin C 1000mg + Zinc",
            agencyId = "lama",
            agencyName = "لاما (Lama)",
            publicPrice = 22000.0,
            pharmacistPrice = 17600.0,
            netPrice = 14000.0,
            netCode = "NET-LAM-03",
            bonusRatio = "10 + 2 مجاناً",
            isAvailable = true,
            description = "أقراص فوارة لدعم المناعة ومقاومة أعراض البرد والرَشح.",
            composition = "فيتامين سي 1000 ملغ + زنك 10 ملغ",
            dosage = "قرص فوار في كاس ماء يومياً",
            companyCode = "LAM1000"
        ),

        // Happy Cure Agency
        Drug(
            id = "HAP-001",
            tradeName = "هابي كيور بنادول كولد & فلو",
            scientificName = "Paracetamol + Phenylephrine + Vitamin C",
            agencyId = "happy_cure",
            agencyName = "هابي كيور (Happy Cure)",
            publicPrice = 15000.0,
            pharmacistPrice = 12000.0,
            netPrice = 9600.0,
            netCode = "NET-HAP-09",
            bonusRatio = "10 + 2 مجاناً",
            isAvailable = true,
            description = "علاج متكامل لأعراض الرشح والاحتقان والآلام المصاحبة للإنفلونزا.",
            composition = "باراسيتامول 500ملغ + فينيليفرين 10ملغ",
            dosage = "قرصين عند الحاجة كل 8 ساعات",
            companyCode = "HAP500"
        ),

        // Celia Milk Agency
        Drug(
            id = "CEL-001",
            tradeName = "حليب سيليا المرحلة 1 (Celia 1)",
            scientificName = "Infant Formula Stage 1 (0-6 Months)",
            agencyId = "celia",
            agencyName = "حليب سيليا (Celia Milk)",
            publicPrice = 14.00,
            pharmacistPrice = 11.50,
            netPrice = 10.20,
            netCode = "NET-CEL-01",
            bonusRatio = "24 + 4 مجاناً",
            isAvailable = true,
            description = "تركيبة حليب بريميوم فرنسية عالية الجودة للأطفال حديثي الولادة حتى 6 أشهر غنية بالحديد والبروبيوتيك.",
            composition = "بروتين حليب البقر المطور + DHA + أوميغا 3 و6",
            dosage = "مكيال لكل 30 مل ماء دافئ تعقيم",
            companyCode = "CELIA1"
        ),
        Drug(
            id = "CEL-002",
            tradeName = "حليب سيليا المرحلة 2 (Celia 2)",
            scientificName = "Follow-up Formula Stage 2 (6-12 Months)",
            agencyId = "celia",
            agencyName = "حليب سيليا (Celia Milk)",
            publicPrice = 14.00,
            pharmacistPrice = 11.50,
            netPrice = 10.20,
            netCode = "NET-CEL-02",
            bonusRatio = "24 + 4 مجاناً",
            isAvailable = true,
            description = "تركيبة متابعة للأطفال من عمر 6 أشهر إلى سنة لدعم النمو والذكاء والمناعة.",
            composition = "فيتامينات متعددة + زنك + كالسيوم + حديد",
            dosage = "حسب الجدول المدون على العبوة",
            companyCode = "CELIA2"
        ),

        // Masoud Serum Agency
        Drug(
            id = "MAS-001",
            tradeName = "سيروم مسعود ملحي 0.9% (Normal Saline)",
            scientificName = "Sodium Chloride 0.9% IV Infusion 500ml",
            agencyId = "masoud_serum",
            agencyName = "سيروم مسعود (Masoud Serum)",
            publicPrice = 2.50,
            pharmacistPrice = 1.90,
            netPrice = 1.50,
            netCode = "NET-MAS-01",
            bonusRatio = "50 + 10 مجاناً",
            isAvailable = true,
            description = "محلول سيروم ملحي معقم للحقن الوريدي والتعويض عن السوائل والشوارد.",
            composition = "كلوريد الصوديوم 0.9 جرام لكل 100 مل",
            dosage = "تسريب وريدي حسب إرشاد الطبيب",
            companyCode = "MAS09"
        ),
        Drug(
            id = "MAS-002",
            tradeName = "سيروم مسعود سكري 5% (Dextrose 5%)",
            scientificName = "Dextrose 5% Water Infusion 500ml",
            agencyId = "masoud_serum",
            agencyName = "سيروم مسعود (Masoud Serum)",
            publicPrice = 2.50,
            pharmacistPrice = 1.90,
            netPrice = 1.50,
            netCode = "NET-MAS-02",
            bonusRatio = "50 + 10 مجاناً",
            isAvailable = true,
            description = "محلول ديكستروز سكري معقم لإمداد الجسم بالطاقة والسوائل وريدياً.",
            composition = "ديكستروز مونوهيدرات 5 جرام لكل 100 مل",
            dosage = "تسريب وريدي تحت إشراف طببي",
            companyCode = "MASD5"
        )
    )
}
