package com.example.data.model

data class WarehouseOffer(
    val id: String,
    val title: String,
    val description: String,
    val badge: String,
    val date: String,
    val agencyName: String? = null,
    val isNew: Boolean = true
)

val DEFAULT_OFFERS = listOf(
    WarehouseOffer(
        id = "offer_1",
        title = "عرض وكالة دومنا الحصري 🎁",
        description = "احصل على بونص إضافي 20% على جميع أصناف المضادات والمسكنات من دومنا للصناعات الدوائية.",
        badge = "بونص خاص",
        date = "اليوم",
        agencyName = "دومنا",
        isNew = true
    ),
    WarehouseOffer(
        id = "offer_2",
        title = "وصول دفعة جديدة من حليب سيليا 🍼",
        description = "توفر كامل أصناف حليب سيليا للأطفال (1 و 2 و 3 وديجيست و AC) بأسعار الجملة الرسمية.",
        badge = "توفر حديث",
        agencyName = "سيليا",
        date = "أمس",
        isNew = true
    ),
    WarehouseOffer(
        id = "offer_3",
        title = "تحديث أسعار أدوية مختبرات ميديكو 📋",
        description = "تم تحديث لائحة أسعار وبونصات أصناف ميديكو المعتمدة مع إمكانية التثبيت الفوري للطلبيات.",
        badge = "تحديث أسعار",
        agencyName = "ميديكو",
        date = "منذ يومين",
        isNew = false
    ),
    WarehouseOffer(
        id = "offer_4",
        title = "سيروم مسعود الطبي - شحن مجاني 🚚",
        description = "شحن مجاني وتوصيل سريع لكافة صيدليات المنطقة عند طلب كميات سيروم مسعود الطبي.",
        badge = "عرض شحن",
        agencyName = "مسعود",
        date = "هذا الأسبوع",
        isNew = false
    )
)
