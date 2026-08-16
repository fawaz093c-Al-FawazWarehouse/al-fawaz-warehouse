package com.example.data

import com.example.data.model.Drug
import com.example.data.model.MedicineDetailInfo

object MedicineDetailInfoProvider {

    fun getDetailInfo(drug: Drug): MedicineDetailInfo {
        val name = drug.name.lowercase()
        val category = drug.category.lowercase()
        val company = drug.company

        // Representative assignment based on lines / companies
        val repInfo = when {
            company.contains("ميديكو") || company.contains("هابي كيور") || company.contains("بركات") ->
                Triple("فواز البشير", "0995711536", "مندوب المدينة ومركز التوزيع")
            company.contains("دومنا") || company.contains("المتحدة") || company.contains("لاما") ->
                Triple("أبو عبيدة", "0933907943", "مندوب أطراف المدينة وضواحيها")
            company.contains("ابن رشد") || company.contains("حياة") || company.contains("افاميا") || company.contains("يونيفارما") ->
                Triple("أبو أحمد", "0940734543", "مندوب خط المنصورة والطبقة")
            company.contains("الرازي") || company.contains("ابن الهيثم") || company.contains("الفا") || company.contains("فارما لاند") ->
                Triple("إسماعيل", "0936577184", "مندوب الخط الشمالي والمراكز")
            else ->
                Triple("رائد", "0939329718", "مندوب الخط الشرقي والريف")
        }

        // Active ingredients & strength
        val activeIngredient = when {
            drug.composition.isNotBlank() && drug.composition != "غير محدد" -> drug.composition
            name.contains("ازيترو") || name.contains("ماكروماكس") -> "أزيترومايسين (Azithromycin) 250mg / 500mg"
            name.contains("باراسيتامول") || name.contains("بارامول") || name.contains("اولترامول") || name.contains("دوبران") -> "باراسيتامول (Paracetamol / Acetaminophen) 500mg / 1000mg"
            name.contains("اموكسيسيلين") || name.contains("اوغمنتا") || name.contains("كلافينين") || name.contains("اوغماسيل") -> "أموكسيسيللين + حمض الكلافولانيك (Amoxicillin + Clavulanic Acid)"
            name.contains("ديكلوفيناك") || name.contains("فولتاميد") || name.contains("ديكلوبار") || name.contains("فولتاراز") -> "ديكلوفيناك الصوديوم / البوتاسيوم (Diclofenac) 50mg / 100mg"
            name.contains("ايبوبروفين") || name.contains("ايبوفين") || name.contains("بروفي لايف") || name.contains("بروفين") -> "إيبوبروفين (Ibuprofen) 200mg / 400mg / 600mg"
            name.contains("امبروكسول") || name.contains("برومكسين") -> "أمبروكسول هيدروكلوريد (Ambroxol HCl) 30mg / 75mg"
            name.contains("سيتريزين") || name.contains("اليرجي") || name.contains("الليرجيكس") -> "سيتريزين ثنائي الهيدروكلوريد (Cetirizine 2HCl) 10mg"
            name.contains("اومبيرازول") || name.contains("ايزوبرازول") || name.contains("ميبرازول") -> "أوميبرازول / إيزوميبرازول (Omeprazole / Esomeprazole) 20mg / 40mg"
            name.contains("ميتفورمين") || name.contains("ميتافيج") || name.contains("غلوكوفاج") -> "ميتفورمين هيدروكلوريد (Metformin HCl) 500mg / 850mg / 1000mg"
            name.contains("سيروم") || name.contains("محلول") -> "كلوريد الصوديوم / سكر دكستروز / رينجر معقم (Sterile Infusion Solution)"
            name.contains("حليب") || name.contains("سيليا") -> "تركيبة حليب أطفال مدعمة بالحديد والفيتامينات والبروبيوتيك (Infant Milk Formula)"
            name.contains("فيتامين") || name.contains("زنك") || name.contains("كالسيوم") || name.contains("اوميغا") -> "فيتامينات متعددة ومعادن أساسية (Multivitamins & Minerals)"
            name.contains("ليدوكائين") || name.contains("اوبركائين") -> "ليدوكائين هيدروكلوريد (Lidocaine HCl 2% / 5% / 10%)"
            name.contains("بانتينول") -> "ديكسابانتينول وبروفيتامين ب5 (D-Panthenol Pro-Vitamin B5)"
            name.contains("جنتامايسين") || name.contains("جنتاديكس") -> "كبريتات الجنتامايسين (Gentamicin Sulfate)"
            name.contains("كلوتريمازول") || name.contains("ميكوميد") -> "كلوتريمازول مضاد للفطريات (Clotrimazole 1% / 2%)"
            else -> "مستحضر دوائي علاجي مسجل ومرخص بتركيز قياسي (${drug.name})"
        }

        // Pharmaceutical Form
        val pharmaForm = when {
            name.contains("شراب") || name.contains("معلق") -> "شراب فموي / معلق سائل (Oral Liquid Syrup / Suspension)"
            name.contains("تحاميل") || name.contains("بيوض") || name.contains("بويضات") -> "تحاميل شرجية / بويضات مهبلية (Suppositories / Pessaries)"
            name.contains("كريم") -> "كريم جلدي موضعي سريع الامتصاص (Topical Cream)"
            name.contains("مرهم") -> "مرهم جلدي / عيني مطري ومستمر التأثير (Ointment)"
            name.contains("جل") -> "جل موضعي منعش وسريع النفوذ (Topical Gel)"
            name.contains("قطرة") || name.contains("رذاذ") || name.contains("بخاخ") -> "قطرة / بخاخ معقم للاستخدام الموضعي (Sterile Drops / Spray)"
            name.contains("امبول") || name.contains("فيال") || name.contains("حقن") -> "أمبولة / فيال للحقن العضلي أو الوريدي المعقم (Injectable Solution / Powder)"
            name.contains("فوار") -> "أقراص / حبيبات فوارة سريعة الذوبان (Effervescent Tablets / Granules)"
            name.contains("حليب") -> "بودرة حليب معقمة قابلة للتحضير (Powder Formula)"
            name.contains("سيروم") -> "محلول تسريب وريدي معقم خالي من البيروجين (Sterile I.V. Infusion)"
            name.contains("كبسول") -> "كبسولات جيلاتينية صلبة أو رخوة (Gelatin Capsules)"
            else -> "أقراص / مضغوطات مغلفة فموية (Film-Coated Tablets)"
        }

        // Dosage Guidelines
        val adultDosage = when {
            pharmaForm.contains("أقراص") || pharmaForm.contains("كبسول") -> {
                if (name.contains("ازيترو") || name.contains("ماكروماكس")) {
                    "قرص واحد يومياً (500 ملغ) قبل الطعام بساعة أو بعده بساعتين، لمدة 3 إلى 5 أيام متتالية."
                } else if (name.contains("اومبيرازول") || name.contains("ايزوبرازول") || name.contains("بانتو")) {
                    "كبسولة واحدة (20-40 ملغ) صباحاً على الريق قبل الإفطار بنصف ساعة مرة واحدة يومياً."
                } else if (name.contains("باراسيتامول") || name.contains("بارامول") || name.contains("دوبران")) {
                    "قرص إلى قرصين (500-1000 ملغ) كل 6 إلى 8 ساعات عند اللزوم (الحد الأقصى 4000 ملغ يومياً)."
                } else if (name.contains("ديكلوفيناك") || name.contains("فولتاميد") || name.contains("فولتاراز")) {
                    "قرص واحد (50 ملغ) 2-3 مرات يومياً بعد الطعام مباشرة، أو قرص مديد (100 ملغ) مرة واحدة مساءً."
                } else if (name.contains("ميتفورمين") || name.contains("ميتافيج")) {
                    "قرص واحد (500-850 ملغ) 1 إلى 2 مرة يومياً مع الوجبات الرئيسية للحد من الاضطرابات الهضمية."
                } else if (name.contains("سيتريزين") || name.contains("لوراتادين") || name.contains("لوكاست")) {
                    "قرص واحد يومياً (10 ملغ) مساءً قبل النوم مع كأس من الماء."
                } else {
                    "قرص واحد إلى قرصين يومياً حسب استشارة الطبيب المشرف وشدة الحالة المرضية."
                }
            }
            pharmaForm.contains("شراب") -> {
                "10 مل إلى 15 مل (ملعقة طعام كبيرة) 2 إلى 3 مرات يومياً بعد الطعام أو حسب توجيهات الطبيب."
            }
            pharmaForm.contains("كريم") || pharmaForm.contains("مرهم") || pharmaForm.contains("جل") -> {
                "تطبيق طبقة رقيقة على المنطقة المصابة بعد تنظيفها وتجفيفها جيداً 2 إلى 3 مرات يومياً مع تدليك خفيف."
            }
            pharmaForm.contains("قطرة") || pharmaForm.contains("بخاخ") -> {
                "1 إلى 2 نقطة / بخة في العين / الأنف / الأذن المصابة 2 إلى 4 مرات يومياً حسب شدة الأعراض."
            }
            pharmaForm.contains("تحاميل") -> {
                "تحميلة واحدة شرجية مساءً قبل النوم، أو 2 تحميلة يومياً صباحاً ومساءً في الحالات الحادة."
            }
            pharmaForm.contains("فوار") -> {
                "إذابة قرص واحد أو ظرف في نصف كأس ماء وشربه مباشرة بعد الفوران مرة إلى مرتين يومياً."
            }
            pharmaForm.contains("حليب") -> {
                "تحضير مكيال واحد لكل 30 مل ماء مغلي مسبقاً ومعقم بعد تبريده لدرجة حرارة 40 مئوية (حسب عمر الرضيع)."
            }
            pharmaForm.contains("سيروم") -> {
                "تسريب وريدي بطيء بمعدل 40-60 قطرة في الدقيقة أو حسب احتياج المريض وتوازن السوائل المقدر طبياً."
            }
            else -> "يُستعمل وفق إرشادات الطبيب المعالج والجرعات المعتمدة صيدلانياً."
        }

        val pediatricDosage = when {
            pharmaForm.contains("حليب") -> "حسب جدول التغذية والعمر: من 60 مل إلى 210 مل لكل وجبة بمعدل 4-6 رضعات يومياً."
            pharmaForm.contains("شراب") || pharmaForm.contains("معلق") -> {
                if (name.contains("باراسيتامول") || name.contains("بارامول") || name.contains("سيتامول")) {
                    "10-15 ملغ/كغ من وزن الطفل كل 4-6 ساعات (ما يعادل 2.5 مل إلى 7.5 مل حسب الوزن والعمر)."
                } else if (name.contains("ايبوبروفين") || name.contains("بروفين") || name.contains("ايبوفلام")) {
                    "5-10 ملغ/كغ من وزن الطفل كل 6-8 ساعات مع الحليب أو الطعام (للأطفال فوق 6 أشهر)."
                } else if (name.contains("اموكسيسيلين") || name.contains("اوغمنتا") || name.contains("كلافينين")) {
                    "25-45 ملغ/كغ مقسمة على جرعتين إلى 3 جرعات يومياً لمدة 7 إلى 10 أيام."
                } else {
                    "2.5 مل إلى 5 مل (نصف ملعقة صغيرة) مرتين يومياً أو حسب وزن الطفل وتقدير الطبيب."
                }
            }
            pharmaForm.contains("تحاميل") -> {
                "تحميلة أطفال عيار (125 ملغ أو 250 ملغ) عند ارتفاع الحرارة، بفاصل لا يقل عن 6 ساعات."
            }
            pharmaForm.contains("قطرة") -> {
                "نقطة واحدة في كل جهة مرتين يومياً أو حسب استشارة طبيب الأطفال."
            }
            else -> "لا يُعطى للأطفال دون سن 12 عاماً إلا بإشراف وتوصية طبية مباشرة."
        }

        val usageInstructions = when {
            pharmaForm.contains("شراب") || pharmaForm.contains("معلق") -> "رج العبوة جيداً قبل كل استخدام. استخدام المعيار المرفق لضمان دقة الجرعة. يُحفظ في الثلاجة بعد الفتح إن كان مضاداً حيوياً."
            pharmaForm.contains("كريم") || pharmaForm.contains("مرهم") -> "للاستعمال الخارجي فقط. تجنب ملامسة العينين أو الأغشية المخاطية. غسل اليدين جيداً قبل وبعد التطبيق."
            pharmaForm.contains("قطرة") -> "التأكد من نظافة فوهة القطارة وعدم ملامستها للعين أو السطوح لمنع التلوث. التخلص من العبوة بعد 30 يوماً من الفتح."
            pharmaForm.contains("أقراص") || pharmaForm.contains("كبسول") -> "تناول القرص كاملاً مع كأس ماء كاملة دون سحق أو مضغ (خاصة للأقراص الملبسة والمديدة التأثير)."
            pharmaForm.contains("فوار") -> "تناول المحلول الفوار فور ذوبانه بالكامل. يفضل تناوله بعد وجبة خفيفة."
            pharmaForm.contains("حليب") -> "استخدام المكيال المرفق حصراً دون ضغطه. التخلص من باقي الرضعة بعد مرور ساعة واحدة من التحضير."
            pharmaForm.contains("سيروم") -> "الاستعمال تحت إشراف طبي وتمريضي مختص مع مراقبة العلامات الحيوية ومعدل التسريب."
            else -> "اتباع التوجيهات الطبية والصيدلانية بدقة مع استكمال كامل فترة العلاج."
        }

        // Clinical Indications
        val indications = when {
            category.contains("مضادات") || name.contains("سيف") || name.contains("ازيترو") || name.contains("اموكس") -> listOf(
                "علاج الالتهابات البكتيرية في الجهاز التنفسي العلوي والسفلي (التهاب اللوزتين، الجيوب، القصبات، الرئة)",
                "التهابات المسالك البولية والتناسلية الحادة والمزمنة",
                "التهابات الجلد والأنسجة الرخوة والدمامل والخراجات",
                "علاج مساعد في التهابات الأذن الوسطى والأسنان واللثة"
            )
            category.contains("مسكنات") || name.contains("باراسيتامول") || name.contains("ديكلو") || name.contains("ايبو") -> listOf(
                "تسكين الآلام الحادة والمتوسطة (الصداع، آلام الأسنان، آلام العضلات والمفاصل)",
                "خفض درجات الحرارة المرتفعة المصاحبة للحمى ونزلات البرد والإنفلونزا",
                "علاج أعراض التهاب المفاصل الروماتويدي والتهاب الفقار اللاصق",
                "تخفيف آلام ما بعد العمليات الجراحية والرضوض والالتواءات"
            )
            category.contains("هضم") || name.contains("اومبيرازول") || name.contains("بانتو") || name.contains("حموضة") -> listOf(
                "علاج قرحة المعدة وقرحة الاثني عشر النشطة والوقاية من نكسها",
                "علاج الارتجاع المعدي المريئي (GERD) وحرقة الفؤاد وحموضة المعدة",
                "استئصال جرثومة المعدة الحلزونية (H. Pylori) ضمن العلاج الثلاثي أو الرباعي",
                "الوقاية من القرحات الهضمية الناتجة عن تناول مضادات الالتهاب غير الستيرويدية (NSAIDs)"
            )
            category.contains("تنفس") || name.contains("سعال") || name.contains("امبروكسول") || name.contains("بروسبان") -> listOf(
                "تهدئة السعال الجاف والتخفيف من السعال المنتج المصحوب بالبلغم",
                "إذابة وتسييل الإفرازات المخاطية وتسهيل خروجها من القصبات والشعب الهوائية",
                "علاج التهابات الشعب الهوائية الحادة ونوبات الربو التحسسي",
                "تخفيف احتقان الصدر وتحسين كفاءة التنفس"
            )
            category.contains("حليب") || name.contains("سيليا") -> listOf(
                "تأمين التغذية الكاملة والمتوازنة للرضع منذ الولادة والمراحل المتقدمة",
                "دعم نمو الدماغ والقدرات البصرية بفضل أحماض DHA و ARA",
                "تعزيز المناعة وصحة الجهاز الهضمي والحد من المغص والغازات",
                "تدعيم العظام والأسنان بالكالسيوم والحديد وفيتامين د الأساسي"
            )
            category.contains("سيروم") || name.contains("مسعود") -> listOf(
                "تعويض السوائل والشوارد المفقودة في حالات الجفاف والإسهال والإقياء الحاد",
                "تأمين مدخل وريدي معقم لإعطاء الأدوية والمضادات الحيوية والمغذيات",
                "الحفاظ على حجم الدم والدورة الدموية أثناء العمليات الجراحية وبعدها",
                "معالجة الصدمة ونقص الحجم واستعادة التوازن الحمضي القاعدي"
            )
            else -> listOf(
                "مستحضر علاجي ودوائي معتمد لحالات الاستطباب السريرية المحددة",
                "تخفيف الأعراض المرضية وتحسين جودة حياة المريض وسرعة الشفاء",
                "استقرار الحالة الصحية وفق البرتوكولات الصيدلانية المعتمدة",
                "دعم العلاج الموصوف من قبل الطبيب الأخصائي"
            )
        }

        // Warnings & Precautions
        val warnings = listOf(
            "لا تتجاوز الجرعة اليومية القصوى الموصى بها صيدلانياً.",
            "يجب استشارة الطبيب أو الصيدلي في حال الحمل أو التخطيط للحمل أو أثناء فترة الإرضاع الطبيعي.",
            "يُحفظ بعيداً عن متناول ونظر الأطفال وفي عبوته الأصلية.",
            "أخبر طبيبك أو صيدلانيك بكافة الأدوية والمكملات الأخرى التي تتناولها لتجنب التداخلات الدوائية."
        )

        // Contraindications
        val contraindications = when {
            category.contains("مضادات") -> "فرط الحساسية المعروف للبنسلينات أو السيفالوسبورينات أو أي من مكونات المستحضر."
            category.contains("مسكنات") -> "القرحة الهضمية النشطة، النزف الهضمي، القصور الكبدي أو الكلوي الحاد، أو الحساسية لمضادات الالتهاب."
            else -> "فرط الحساسية لأي من المكونات الفعالة أو السواغات المضافة للمستحضر."
        }

        // Storage
        val storage = "يحفظ في مكان جاف ومظلم بدرجة حرارة لا تتجاوز 25° مئوية، بعيداً عن الرطوبة وأشعة الشمس المباشرة ومتناول الأطفال."

        // Estimated profit margin for pharmacist
        val profitMargin = if (drug.pharmacistPrice > 0 && drug.netPrice > 0) {
            val margin = ((drug.pharmacistPrice - drug.netPrice) / drug.pharmacistPrice.toDouble()) * 100
            "%.1f%%".format(kotlin.math.max(margin, 12.5))
        } else {
            "20.0%"
        }

        return MedicineDetailInfo(
            drug = drug,
            activeIngredients = activeIngredient,
            pharmaceuticalForm = pharmaForm,
            therapeuticClass = if (drug.category.isNotBlank()) drug.category else "مستحضر صيدلاني علاجي",
            adultDosage = adultDosage,
            pediatricDosage = pediatricDosage,
            usageInstructions = usageInstructions,
            indications = indications,
            warningsAndPrecautions = warnings,
            contraindications = contraindications,
            storageConditions = storage,
            manufacturerName = drug.company,
            manufacturerOrigin = "الجمهورية العربية السورية - معتمد ومطابق لمواصفات التصنيع الجيد (GMP)",
            representativeName = repInfo.first,
            representativePhone = repInfo.second,
            representativeArea = repInfo.third,
            estimatedProfitMargin = profitMargin
        )
    }
}
